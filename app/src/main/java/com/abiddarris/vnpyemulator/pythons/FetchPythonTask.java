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
package com.abiddarris.vnpyemulator.pythons;

import android.widget.Toast;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.dialogs.DialogUtils;
import com.abiddarris.vnpyemulator.dialogs.Task;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.patches.Source;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;

public class FetchPythonTask extends Task {
    
    private Game game;
    
    public FetchPythonTask(Game game) {
        this.game = game;
    }
    
    @Override
    public void execute() throws Exception {
        setMessage(getApplicationContext()
            .getString(R.string.fetching));
        try {
        	var continue0 = fetchFromSource();
            if(!continue0) {
                // TODO: SHOW ERROR
                return;
            }
        } catch(IOException e) {
        	e.printStackTrace();
        }
        
        List<ExternalPython> externalPythons = new ArrayList<>();
        try (var reader = new BufferedReader(new FileReader(
                Files.getPythonVersionCache(getApplicationContext())))) {
         	reader.lines()
                .forEach(line -> {
                    var components = line.split("//");
                    externalPythons.add(new ExternalPython(components[0], components[1]));
                });
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        List<String> downloadedVersion = getDownloadedVersion();
        List<String> choices = new ArrayList<>(downloadedVersion);
        
        externalPythons.stream()
            .filter(python -> !downloadedVersion.contains(python.getVersionName()))
            .map(ExternalPython::getVersionName)
            .forEach(choices::add);
      
        var context = getApplicationContext();
        var manager = getDialog().getParentFragmentManager();
        DialogUtils.choseItem(manager,
            getApplicationContext().getString(R.string.select_python_version), 
            getApplicationContext().getString(R.string.select_python_message, game.optString(Game.GAME_NAME)), 
            true, choices.toArray(new String[0]), 0, index -> {
                String version = choices.get(index);
                if(downloadedVersion.contains(version)) {
                    setGameVersion(version);
                    return;
                }
                    
                externalPythons.stream()
                    .filter(python -> version.equals(python.getVersionName()))
                    .findFirst()
                    .ifPresent(python -> DialogUtils.runTask(manager, context.getString(R.string.downloading_python),
                        false, new DownloadPythonTask(context, python, () -> setGameVersion(version))));
            });
    }
    
    private void setGameVersion(String version) {
        try {
            game.put(Game.PYTHON_VERSION, version);
            
            Game.updateGame(getApplicationContext(), game);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    
    private List<String> getDownloadedVersion() {
        var pythonFolder = Files.getPythonFolders(getApplicationContext())
            .list();
        return pythonFolder == null ? new LinkedList<String>() : List.of(pythonFolder);
    }
    
    private boolean fetchFromSource() throws IOException {
    	Source source = Source.getSource();
        byte[] data;
        try (var stream = new BufferedInputStream(source.open("python/version"))) {
            data = stream.readAllBytes();
        } 
        
        try (var stream = new BufferedOutputStream(
                new FileOutputStream(Files.getPythonVersionCache(getApplicationContext())))) {
        	
            stream.write(data);
            stream.flush();
        } catch(IOException e) {
            //this should not happend!
        	e.printStackTrace();
            return false;
        }
        return true;
    }
    

}
