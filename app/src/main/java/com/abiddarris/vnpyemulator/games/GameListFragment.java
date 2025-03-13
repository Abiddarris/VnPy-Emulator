/***********************************************************************************
 * Copyright (C) 2024 Abiddarris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.games;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abiddarris.common.android.about.AboutActivity;
import com.abiddarris.common.android.fragments.AdvanceFragment;
import com.abiddarris.common.android.tasks.TaskViewModel;
import com.abiddarris.common.android.tasks.v2.IndeterminateProgress;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.common.android.tasks.v2.TaskManager;
import com.abiddarris.common.android.tasks.v2.dialog.DialogProgressPublisherManager;
import com.abiddarris.common.android.tasks.v2.dialog.IndeterminateDialogProgressPublisher;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.FragmentGameListBinding;
import com.abiddarris.vnpyemulator.download.DownloadFragment;
import com.abiddarris.vnpyemulator.unrpa.FindRpaTask;

import java.io.IOException;

public class GameListFragment extends AdvanceFragment {

    private GameAdapter adapter;
    private GameListViewModel gameListViewModel;
    private View currentItem;
    private FragmentGameListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        requireActivity().setTitle(R.string.icon_name);

        gameListViewModel = TaskViewModel.getInstance(this, GameListViewModel.class);
        gameListViewModel.attach(this);

        adapter = new GameAdapter(this);

        binding = FragmentGameListBinding.inflate(inflater, container, false);
        binding.games.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.games.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_game_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_new_game) {
            new AddNewGameDialog().showForResult(getParentFragmentManager(), path -> {
                if (path == null) {
                    return;
                }
                var publisher = new IndeterminateDialogProgressPublisher("AddGameDialog");
                gameListViewModel.dialogManager.registerPublisher(publisher);

                TaskInfo<IndeterminateProgress, EditGameDialog> info =
                        gameListViewModel.taskManager.execute(new AddGameTask(path), publisher);
                info.addOnTaskExecuted(dialog -> dialog.show(getChildFragmentManager(), null));
            });
            return true;
        }

        if(item.getItemId() == R.id.about) {
            startActivity(AboutActivity.newAboutActivity(getContext(), "ABOUT", "ATTRIBUTION"));
            return true;
        }

        if (item.getItemId() == R.id.download) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, DownloadFragment.class, null)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return false;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View view, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        getActivity()
                .getMenuInflater()
                .inflate(R.menu.layout_game_menu, menu);
        currentItem = view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Game game = adapter.get((int)currentItem.getTag());
        if(item.getItemId() == R.id.delete) {
            DeleteGameDialog.getInstance(game)
                    .showForResult(getChildFragmentManager(), (delete) -> {
                        if (delete) {
                            gameListViewModel.execute(new DeleteGameTask(game));
                        }
                    });
            return true;
        }

        if (item.getItemId() == R.id.edit) {
            // TODO: 09/03/25 fix this
//            EditGameDialog.editGame(game)
//                    .showForResult(getChildFragmentManager(), result -> {
//                        if (result) {
//                            try {
//                                GameLoader.saveGames(getContext());
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                            adapter.notifyGameModified(game);
//                        }
//                    });
            return true;
        }

        if(item.getItemId() == R.id.open) {
            open(game);
            return true;
        }

        if(item.getItemId() == R.id.about) {
            AboutGameInformationDialog.newInstance(game)
                    .show(getChildFragmentManager(), null);
            return true;
        }

        if(item.getItemId() == R.id.unpack_archive) {
            gameListViewModel.execute(
                    new FindRpaTask(game.getGamePath())
            );
            return true;
        }

        return false;
    }

    public GameListViewModel getTaskModel() {
        return gameListViewModel;
    }

    public void open(Game game) {
        adapter.open(game);
    }

    public GameAdapter getAdapter() {
        return adapter;
    }

    public void refresh() {
        getActivity().runOnUiThread(() -> {
            adapter.refresh();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroy() {
        gameListViewModel.detach();

        super.onDestroy();
    }

    public static class GameListViewModel extends TaskViewModel {

        private TaskManager taskManager;
        private DialogProgressPublisherManager dialogManager;

        private void attach(GameListFragment fragment) {
            if (taskManager == null) {
                taskManager = new TaskManager(fragment.getContext());
                dialogManager = new DialogProgressPublisherManager(fragment);
            }

            taskManager.setContext(fragment.getContext());
            dialogManager.attach(fragment);
        }

        public DialogProgressPublisherManager getDialogManager() {
            return dialogManager;
        }

        public TaskManager getTaskManager() {
            return taskManager;
        }

        private void detach() {
            dialogManager.invalidate();
        }

        @Override
        protected void onCleared() {
            taskManager.shutdown(true);

            super.onCleared();
        }

        public void refresh() {
            GameListFragment fragment = (GameListFragment) getOwner();
            fragment.refresh();
        }
    }

}
