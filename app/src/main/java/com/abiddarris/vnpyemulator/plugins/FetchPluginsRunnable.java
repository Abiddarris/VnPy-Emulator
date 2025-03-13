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
 *
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.plugins;

import static android.content.pm.PackageInstaller.STATUS_SUCCESS;

import static com.abiddarris.common.stream.InputStreams.writeAllTo;

import android.os.Build;

import androidx.fragment.app.DialogFragment;

import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.pm.InstallationResult;
import com.abiddarris.common.android.pm.Packages;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.plugin.PluginLoader;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.games.GameListFragment;
import com.abiddarris.vnpyemulator.renpy.RenPyPrivate;
import com.abiddarris.vnpyemulator.sources.Connection;
import com.abiddarris.vnpyemulator.sources.Source;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class FetchPluginsRunnable extends TaskDialog {
    
    private Game game;
    private File pluginApk;
    
    public FetchPluginsRunnable(Game game) {
        this.game = game;
    }
    
    @Override
    protected String getTag() {
        return "fetchPluginDialog";
    }
    
    @Override
    protected DialogFragment newDialog() {
        return new FetchPluginsDialog();
    }
    
    @Override
    public void execute() throws Exception {
        setMessage(getString(R.string.fetching));
        PluginGroup[] plugins = PluginSource.getPlugins(getApplicationContext());
        List<String> versions = Stream.of(plugins) 
            .map(PluginGroup::getVersion)
            .collect(Collectors.toList());
        
        int index = versions.indexOf(game.getRenPyVersion());
        if(index == -1) {
            var dialog = new SelectPluginVersionDialog();
            dialog.saveVariable(SelectPluginVersionDialog.MESSAGE,
                getString(R.string.plugin_not_available, game.getRenPyVersion()));
            dialog.setItems(versions.toArray(String[]::new), -1);
            
            index = dialog.showForResultAndBlock(getFragmentManager());
            
            if(index == -1) {
                return;
            }
        }
        PluginGroup pluginGroup = plugins[index];
        Plugin plugin = getPreferedPlugin(pluginGroup);
        if (plugin == null) {
            return;
        }
        
//        if(!PluginLoader.hasPlugin(getApplicationContext(), pluginGroup.getVersion())) {
//            downloadPlugin(pluginGroup, plugin);
//        }
        
        if(!RenPyPrivate.hasPrivateFiles(getApplicationContext(), pluginGroup.getVersion())) {
            downloadPrivateFiles(pluginGroup, plugin);
        }
        
        if(!installPlugin()) {
            return;
        }
        
        game.setPlugin(pluginGroup.getVersion());
        game.setRenPyPrivateVersion(pluginGroup.getVersion());
        
        Game.updateGame(getApplicationContext(), game);
        
        ((GameListFragment)getOwner()).open(game);
    }

    private Plugin getPreferedPlugin(PluginGroup pluginGroup) {
        Plugin[] plugins = pluginGroup.getPlugins();
        for(String abi : Build.SUPPORTED_ABIS) {
            Plugin[] supportedPlugins = List.of(plugins)
                    .stream()
                    .filter(plugin -> plugin.getAbi().equals(abi))
                    .toArray(Plugin[]::new);
            if (supportedPlugins.length == 0) {
                continue;
            }
            Arrays.sort(supportedPlugins, Comparator.comparing(plugin -> Integer.valueOf(plugin.getVersion())));

            return supportedPlugins[supportedPlugins.length - 1];
        }
        SimpleDialog.show(getFragmentManager(),
                getString(R.string.plugin_not_supported),
                getString(R.string.plugin_not_supported_message, Arrays.toString(Build.SUPPORTED_ABIS)));

        return null;
    }

    private void downloadPlugin(PluginGroup pluginGroup, Plugin plugin) throws IOException {
        setMessage(getString(R.string.downloading_plugin, pluginGroup.getVersion()));
//        try (Connection connection = plugin.downloadPlugin()) {
//            pluginApk = new File(Files.getCacheFolder(getApplicationContext()), pluginGroup.getName() + ".apk");
//
//            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
//            try (BufferedOutputStream outputStream = new BufferedOutputStream(
//                    new FileOutputStream(pluginApk))) {
//                writeAllTo(inputStream, outputStream);
//            }
//        }
    }

    private void downloadPrivateFiles(PluginGroup group, Plugin plugin) throws IOException {
        setMessage(getString(R.string.downloading_renpy_private_files, group.getVersion()));

        File cache = new File(Files.getCacheFolder(getApplicationContext()), group.getVersion());
//        try (Connection connection = plugin.downloadPrivateFiles();
//             BufferedOutputStream outputStream = new BufferedOutputStream(
//                     new FileOutputStream(cache))) {
//            writeAllTo(connection.getInputStream(), outputStream);
//        }
//
//        unpackRenPyPrivateFiles(cache, RenPyPrivate.getPrivateFiles(getApplicationContext(), group.getVersion()));
    }

    private void unpackRenPyPrivateFiles(File cache, File dest) throws IOException {
        setMessage(getString(R.string.unpacking_renpy_private_files));

    }

    private boolean installPlugin() throws IOException {
        if(pluginApk == null) return true;
        
        setMessage(getString(R.string.installing_plugin));
        
        InstallationResult result = Packages.installPackage(getDialog().getActivity(), pluginApk);

        return result.getStatus() == STATUS_SUCCESS;
    }
      
    private void setMessage(String message) {
        FetchPluginsDialog dialog = getDialog();
        dialog.setMessage(message);
    }
    
    @Override
    public void onFinally() {
    	super.onFinally();
        
        if(pluginApk != null) 
            pluginApk.delete();
    }
    
}