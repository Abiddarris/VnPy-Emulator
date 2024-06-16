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
 *
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.plugins;

import static android.content.pm.PackageInstaller.STATUS_SUCCESS;

import android.os.Build;
import androidx.fragment.app.DialogFragment;
import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.pm.InstallationResult;
import com.abiddarris.common.android.pm.Packages;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.plugin.PluginLoader;
import com.abiddarris.vnpyemulator.MainActivity;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.dialogs.FetchPluginsDialog;
import com.abiddarris.vnpyemulator.dialogs.SelectPluginVersionDialog;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.renpy.RenPyPrivate;
import com.abiddarris.vnpyemulator.sources.Connection;
import com.abiddarris.vnpyemulator.sources.Source;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

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
        Plugin[] plugins = Plugin.getPlugins(getApplicationContext());
        List<String> versions = Stream.of(plugins) 
            .map(Plugin::getVersion)
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
        Plugin plugin = plugins[index];
        
        if(!PluginLoader.hasPlugin(getApplicationContext(), plugin.getVersion())) {
            if(!downloadPlugin(plugin)) {
                return;
            }
        }
        
        if(!RenPyPrivate.hasPrivateFiles(getApplicationContext(), plugin.getPrivateRenPyVersion())) {
            downloadPrivateFiles(plugin);
        }
        
        if(!installPlugin()) {
            return;
        }
        
        game.setPlugin(plugin.getVersion());
        game.setRenPyPrivateVersion(plugin.getPrivateRenPyVersion());
        
        Game.updateGame(getApplicationContext(), game);
        
        MainActivity activity = getActivity();
        activity.open(game);
    }
    
    private void downloadPrivateFiles(Plugin plugin) throws IOException {
        setMessage(getString(R.string.downloading_renpy_private_files, plugin.getVersion()));
        File cache = new File(Files.getCacheFolder(getApplicationContext()), plugin.getPrivateRenPyVersion());
        try (BufferedInputStream inputStream = new BufferedInputStream(Source.getSource()
                .open("plugins/" + plugin.getPrivateRenPyDownloadPath()));
             BufferedOutputStream outputStream = new BufferedOutputStream(
                 new FileOutputStream(cache))) {
            byte[] buf = new byte[8192];
            int len;
            while((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        }
        unpackRenPyPrivateFiles(cache, RenPyPrivate.getPrivateFiles(getApplicationContext(), plugin.getPrivateRenPyVersion()));
    }
    
    private void unpackRenPyPrivateFiles(File cache, File dest) throws IOException {
        setMessage(getString(R.string.unpacking_renpy_private_files));
        dest.mkdirs();
        
        var is = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(cache))));
        TarEntry entry;
        while((entry = is.getNextEntry()) != null) {
            var destination = new File(dest, entry.getName());
            if(entry.isDirectory()) {
                destination.mkdirs();
                continue;
            } 
            var os = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] buf = new byte[8192];
            int len;
            while((len = is.read(buf)) != -1) {
                os.write(buf,0,len);
            }
            os.flush();
            os.close();
        }
        is.close();
        
        cache.delete();
    }
    
    private boolean downloadPlugin(Plugin plugin) throws IOException {
        setMessage(getString(R.string.downloading_plugin, plugin.getVersion()));
        
        Source source = Source.getSource();
        for(String abi : Build.SUPPORTED_ABIS) {
            String path = plugin.getPluginDownloadPath(abi);
        	try (Connection connection = source.openConnection(path)){
                if(connection.isExists()) {
                    downloadPlugin(connection, plugin.getVersion() + ".apk");
                    return true;
                }
            }
        }
        
        SimpleDialog.show(getFragmentManager(),
             getString(R.string.plugin_not_supported),
             getString(R.string.plugin_not_supported_message, Arrays.toString(Build.SUPPORTED_ABIS)));
        
        return false;
    }
    
    private void downloadPlugin(Connection connection, String name) throws IOException {
        pluginApk = new File(Files.getCacheFolder(getApplicationContext()), name);
        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(pluginApk))) {
            byte[] buf = new byte[8192];
            int len;
            while((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        }
    }  
    
    private boolean installPlugin() throws IOException {
        if(pluginApk == null) return true;
        
        setMessage(getString(R.string.installing_plugin));
        
        InstallationResult result = Packages.installPackage(getDialog().getActivity(), pluginApk);
        if(result.getStatus() != STATUS_SUCCESS) {
            return false;
        }
        
        return true;
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