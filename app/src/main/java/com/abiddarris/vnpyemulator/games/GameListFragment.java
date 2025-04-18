/***********************************************************************************
 * Copyright (C) 2024-2025 Abiddarris
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

import static com.abiddarris.vnpyemulator.files.Files.getKeyboardFolder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abiddarris.common.android.about.AboutActivity;
import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.fragments.AdvanceFragment;
import com.abiddarris.common.android.tasks.TaskViewModel;
import com.abiddarris.common.android.tasks.v2.IndeterminateProgress;
import com.abiddarris.common.android.tasks.v2.IndeterminateTask;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.common.android.tasks.v2.TaskManager;
import com.abiddarris.common.android.tasks.v2.dialog.DialogProgressPublisherManager;
import com.abiddarris.common.android.tasks.v2.dialog.IndeterminateDialogProgressPublisher;
import com.abiddarris.common.utils.ObjectWrapper;
import com.abiddarris.plugin.PluginArguments;
import com.abiddarris.plugin.PluginLoader;
import com.abiddarris.plugin.PluginName;
import com.abiddarris.vnpyemulator.MainActivity;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.FragmentGameListBinding;
import com.abiddarris.vnpyemulator.download.DownloadFragment;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.abiddarris.vnpyemulator.plugins.PluginSource;
import com.abiddarris.vnpyemulator.renpy.RenPyPrivate;
import com.abiddarris.vnpyemulator.unrpa.FindRpaTask;

import java.io.IOException;
import java.util.Arrays;

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
            var publisher = new IndeterminateDialogProgressPublisher("GetPluginTask");
            gameListViewModel.dialogManager.registerPublisher(publisher);

            TaskInfo<IndeterminateProgress, Plugin[]> info =
                    gameListViewModel.taskManager.execute(new GetPluginTask(), publisher);
            info.addOnTaskExecuted(plugins -> {
                Plugin plugin = Arrays.asList(plugins)
                        .stream()
                        .filter(p -> p.toStringWithoutAbi().equals(game.getPlugin()))
                        .findFirst()
                        .orElse(null);
                EditGameDialog.editGame(game, plugins, plugin)
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
            });
            return true;
        }

        if(item.getItemId() == R.id.open) {
            open(game);
            return true;
        }

        if (item.getItemId() == R.id.create_shortcut) {
            createShortcut(game);

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

    private void createShortcut(Game game) {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setAction(game.getName());

        String iconPath = game.getIconPath();
        IconCompat icon;
        Bitmap bitmap = null;
        if (iconPath == null) {
            icon = IconCompat.createWithResource(requireContext(), R.drawable.ic_launcher);
        } else {
            bitmap = BitmapFactory.decodeFile(iconPath);
            if (bitmap == null) {
                icon = IconCompat.createWithResource(requireContext(), R.drawable.ic_launcher);
            } else {
                icon = IconCompat.createWithBitmap(bitmap);
            }
        }

        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(requireContext(), game.getName())
                .setShortLabel(game.getName())
                .setIcon(icon)
                .setIntent(intent)
                .build();

        ShortcutManagerCompat.requestPinShortcut(requireContext(), shortcut, null);

        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public GameListViewModel getTaskModel() {
        return gameListViewModel;
    }

    public void open(Game game) {
        IndeterminateDialogProgressPublisher progressPublisher = new IndeterminateDialogProgressPublisher("fetch");
        gameListViewModel.dialogManager.registerPublisher(progressPublisher);

        ObjectWrapper<Boolean> shouldOpen = new ObjectWrapper<>(false);
        TaskInfo<IndeterminateProgress, Plugin> taskInfo = gameListViewModel.taskManager.execute(new IndeterminateTask<>() {
            @Override
            public void execute() throws Exception {
                setTitle(R.string.fetch_plugin_title);
                setMessage(R.string.fetching);

                Plugin[] plugins = PluginSource.getPlugins(getContext(), false);
                setResult(Arrays.asList(plugins)
                        .stream()
                        .filter(plugin -> plugin.toStringWithoutAbi().equals(game.getPlugin()))
                        .findFirst()
                        .orElse(null));
                shouldOpen.setObject(true);
            }

            @Override
            public void onThrowableCatched(Throwable throwable) {
                super.onThrowableCatched(throwable);

                ExceptionDialog.showExceptionDialog(getChildFragmentManager(), throwable);
            }
        }, progressPublisher);
        taskInfo.addOnTaskExecuted(plugin -> open(game, shouldOpen.getObject(), plugin));
    }

    private void open(Game game, boolean shouldOpen, Plugin plugin0) {
        if (!shouldOpen) {
            return;
        }

        if (plugin0 == null) {
            SimpleDialog.newSimpleDialog(
                    getString(R.string.unsupported_plugin),
                    getString(R.string.plugin_not_supported_message, game.getPlugin())
            ).show(getChildFragmentManager(), null);
            return;
        }

        String plugin = game.getPlugin();
        String renpyPrivateVersion = game.getRenPyPrivateVersion();

        PluginName name = new PluginName(plugin);
        if(!PluginLoader.hasPlugin(getContext(), name)) {
            SimpleDialog.show(
                    getChildFragmentManager(),
                    getString(R.string.plugin_not_installed),
                    getString(R.string.please_install_plugin)
            );
            return;
        }

        long pluginInternalVersion = PluginLoader.getPluginInternalVersion(
                requireContext(), name.getVersion());
        if (pluginInternalVersion != Integer.parseInt(name.getPluginInternalVersion())) {
            SimpleDialog.show(
                    getChildFragmentManager(),
                    getString(R.string.mismatch_plugin_version),
                    getString(
                            R.string.mismatch_plugin_message,
                            plugin, name.getVersion() + "." + pluginInternalVersion
                    )
            );
            return;
        }

        String renpyPrivateVersionPath = null;
        if (plugin0.getPrivateFiles() != null) {
            if (!RenPyPrivate.hasPrivateFiles(getContext(), renpyPrivateVersion)) {
                SimpleDialog.show(
                        getChildFragmentManager(),
                        getString(R.string.plugin_corrupted),
                        getString(R.string.plugin_corrupted_message)
                );
                return;
            }
            renpyPrivateVersionPath = RenPyPrivate.getPrivateFiles(getContext(), renpyPrivateVersion)
                    .getAbsolutePath();
        }

        MainActivity activity = (MainActivity)getActivity();
        var intent = PluginLoader.getIntentForPlugin(name.getVersion(), new PluginArguments()
                .setRenPyPrivatePath(renpyPrivateVersionPath)
                .setGamePath(game.getGamePath())
                .setGameScript(game.getGameScript())
                .setErrorPort(activity.getPort())
                .setKeyboardFolderPath(getKeyboardFolder(getContext()).getAbsolutePath()));

        startActivity(intent);
    }

    public GameAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onDestroy() {
        if (gameListViewModel != null) {
            gameListViewModel.detach();
        }

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

        public void notifyNewGame(Game game) {
            GameListFragment fragment = (GameListFragment) getOwner();
            fragment.adapter.notifyNewGame(game);
        }
    }

}
