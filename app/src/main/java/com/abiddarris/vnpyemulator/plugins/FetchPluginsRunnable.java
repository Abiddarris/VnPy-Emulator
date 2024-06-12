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

import androidx.fragment.app.DialogFragment;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.dialogs.FetchPluginsDialog;
import com.abiddarris.vnpyemulator.games.Game;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FetchPluginsRunnable extends TaskDialog {
    
    private Game game;
    
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
            // TODO: handle non exist plugin
        }
        Plugin plugin = plugins[index];
        setMessage(plugin.getVersion());
        
        Thread.sleep(3000);
    }
    
    private void setMessage(String message) {
        FetchPluginsDialog dialog = getDialog();
        dialog.setMessage(message);
    }
    
}
