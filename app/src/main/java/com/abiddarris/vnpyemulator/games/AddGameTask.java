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

import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.tasks.v2.IndeterminateTask;
import com.abiddarris.common.utils.ObjectWrapper;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.patches.Patch;
import com.abiddarris.vnpyemulator.patches.PatchSource;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.abiddarris.vnpyemulator.plugins.PluginSource;
import com.abiddarris.vnpyemulator.renpy.RenPyParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddGameTask extends IndeterminateTask<EditGameDialog> {

    private final File gameFolder;

    public AddGameTask(String gameFolder) {
        this.gameFolder = new File(gameFolder);
    }

    @Override
    public void execute() throws Exception {
        setMessage(getString(R.string.patching));

        File[] mainScriptCandidates = getMainScriptCandidates();
        if(mainScriptCandidates == null) {
            return;
        }

        Game game = new Game();
        game.setGamePath(gameFolder.getPath());
        if (mainScriptCandidates.length == 1) {
            File mainScript = mainScriptCandidates[0];

            game.setGameScript(mainScript.getName());
            game.setName(getGameName(mainScript));
        }

        String patchVersion = null;
        try {
            patchVersion = RenPyParser.getVersion(gameFolder);
            game.setRenPyVersion(patchVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Patch[] patches = PatchSource.getPatches();
        String[] patchVersions = Arrays.asList(patches)
                .stream()
                .flatMap(patch -> Arrays.asList(patch.getPatchers()).stream())
                .map(patcher -> patcher.getPatch().getRenPyVersion() + "." + patcher.getVersion())
                .toArray(String[]::new);

        Patch patch = PatchSource.getPatch(patchVersion);
        if (patch != null) {
            Patcher[] patchers = patch.getPatchers();
            patchVersion = patch.getRenPyVersion() + "." + patchers[patchers.length - 1].getVersion();
        } else {
            patchVersion = null;
        }

        PluginGroup[] plugins = PluginSource.getPlugins(getContext());
        Plugin[] pluginVersions = Arrays.asList(plugins)
                .stream()
                .flatMap(pluginGroup -> Arrays.asList(pluginGroup.getPlugins(true)).stream())
                .toArray(Plugin[]::new);

        PluginGroup pluginGroup = PluginSource.getPluginGroup(getContext(), game.getRenPyVersion());
        Plugin pluginVersion = null;
        if (pluginGroup != null) {
            pluginVersion = findPreferredPluginVersion(pluginGroup);
        }
        List<String> versions = Stream.of(plugins)
                .map(PluginGroup::getVersion)
                .collect(Collectors.toList());

        EditGameDialog dialog = EditGameDialog.editGame(
                game, mainScriptCandidates.length == 1 ? null : mainScriptCandidates,
                patchVersions, patchVersion, pluginVersions, pluginVersion
        );
        setResult(dialog);
    }

    private Plugin findPreferredPluginVersion(PluginGroup pluginGroup) {
        Plugin[] plugins = pluginGroup.getPlugins(true);
        List<String> supportedABIs = Arrays.asList(Build.SUPPORTED_ABIS);
        Arrays.sort(plugins, (plugin, plugin2) -> {
            int pluginVersion = Integer.parseInt(plugin.getVersion());
            int plugin2Version = Integer.parseInt(plugin2.getVersion());

            if (pluginVersion > plugin2Version) {
                return -1;
            }

            if (pluginVersion < plugin2Version) {
                return 1;
            }

            int abi = supportedABIs.indexOf(plugin.getAbi());
            int abi2 = supportedABIs.indexOf(plugin2.getAbi());

            return Integer.compare(abi2, abi);
        });

        return plugins.length == 0 ? null : plugins[0];
    }

    private String getGameName(File mainScript) throws IOException {
        String baseName = removeExtension(mainScript.getName());
        ObjectWrapper<String> name = new ObjectWrapper<>(baseName);
        List<Game> games = GameLoader.loadGames(getContext());
        int i = 0;
        while(games.stream()
                .map(Game::getName)
                .anyMatch(gameName -> gameName.equals(name.getObject()))) {

            name.setObject(baseName + String.format(" (%s)", ++i));
        }
        return name.getObject();
    }

    private String removeExtension(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    private File[] getMainScriptCandidates() {
        var files = gameFolder.listFiles();
        if(files == null) {
            showScriptNotFoundError();

            return null;
        }

        files = Stream.of(files)
                .filter(file -> file.getName().endsWith(".py"))
                .toArray(File[]::new);

        if(files.length < 1) {
            showScriptNotFoundError();

            return null;
        }

        return files;
    }

    private void showScriptNotFoundError() {
        SimpleDialog.show(getFragmentManager(),
                getString(R.string.patch_error),
                getString(R.string.py_script_not_found));
    }

    private FragmentManager getFragmentManager() {
        return ((AppCompatActivity)getContext()).getSupportFragmentManager();
    }
}
