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
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.FragmentGameListBinding;
import com.abiddarris.vnpyemulator.patches.PatchRunnable;
import com.abiddarris.vnpyemulator.unrpa.FindRpaTask;

import java.io.IOException;

public class GameListFragment extends AdvanceFragment {

    private GameAdapter adapter;
    private TaskViewModel model;
    private View currentItem;
    private FragmentGameListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        model = TaskViewModel.getInstance(this);

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
            new AddNewGameDialog()
                    .showForResult(getParentFragmentManager(), path -> {
                        if(path != null)
                            model.execute(new PatchRunnable(path));
                    });
            return true;
        }

        if(item.getItemId() == R.id.about) {
            startActivity(AboutActivity.newAboutActivity(getContext(), "ABOUT", "ATTRIBUTION"));
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
                            model.execute(new DeleteGameTask(game));
                        }
                    });
            return true;
        }

        if (item.getItemId() == R.id.edit) {
            EditGameDialog.editGame(game)
                    .showForResult(getChildFragmentManager(), result -> {
                        if (result) {
                            try {
                                GameLoader.saveGames(getContext());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            adapter.notifyGameModified(game);
                        }
                    });
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
            model.execute(
                    new FindRpaTask(game.getGamePath())
            );
            return true;
        }

        return false;
    }

    public TaskViewModel getTaskModel() {
        return model;
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

}
