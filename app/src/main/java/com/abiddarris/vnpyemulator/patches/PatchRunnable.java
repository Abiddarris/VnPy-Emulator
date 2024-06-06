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
package com.abiddarris.vnpyemulator.patches;

import android.widget.Toast;
import com.abiddarris.vnpyemulator.dialogs.SelectMainPythonDialog;
import static com.abiddarris.vnpyemulator.games.Game.*;

import android.content.Context;
import android.util.Log;
import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.utils.BaseRunnable;
import com.abiddarris.common.utils.Hash;
import com.abiddarris.common.utils.ObjectWrapper;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.dialogs.ApplyPatchDialog;
import com.abiddarris.vnpyemulator.games.Game;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PatchRunnable implements BaseRunnable {
    
    public static final String TAG = PatchRunnable.class.getSimpleName();
    
    private ApplyPatchDialog dialog;
    private Context applicationContext;
    private File folderToPatch;
    private String message;
    
    public PatchRunnable(ApplyPatchDialog dialog) {
        this.dialog = dialog;
        
        folderToPatch = new File(dialog.getArguments()
            .getString(ApplyPatchDialog.FOLDER_TO_PATCH));
        applicationContext = dialog.getActivity()
            .getApplicationContext();
    }
    
    @Override
    public void execute() throws Exception {
        setMessage(applicationContext.getString(
                R.string.patching));
        
        File script = getScriptFile();
        if(script == null) {
            return;
        }
        
        String version = RenPyParser.getVersion(folderToPatch);
        PatchSource source = PatchSource.getPatcher();
        
        if(!Arrays.asList(source.getVersions())
            .contains(version)) {
               // TODO: Implemenent Error handling if version is not available
            return;
        }

        var patcher = source.getPatcher(version);
        for(var patch : patcher.getPatches()) {
            var target = new File(folderToPatch, patch.getFileToPatch());
            if(!target.exists()) {
                throw new PatchException("Unable to patch non exist file: " + target.getPath());
            }
            
            var inputStream = new BufferedInputStream(patcher.open(patch.getPatchFileName()));
            var outputStream = new ByteArrayOutputStream();
            
            var patchHash = Hash.createHashingFrom(inputStream, outputStream);
            var patchContent = outputStream.toByteArray();
           
            outputStream.close();
            inputStream.close();
            inputStream = new BufferedInputStream(new FileInputStream(target));
            
            var originalFileHash = Hash.createHashingFrom(inputStream, OutputStream.nullOutputStream());
            
            inputStream.close();
            if(originalFileHash.equals(patchHash)) {
                Log.i(TAG, target.getPath() + "Already patched");
                continue;
            }
            
            if(originalFileHash.equals(patch.getOriginalFileHash())) {
                // TODO: prompt a user patching may cause a problem
            }
            
            var os = new BufferedOutputStream(new FileOutputStream(target));
            os.write(patchContent);
            os.flush();
            os.close();
        }
        
        String baseName = removeExtension(script.getName());
        ObjectWrapper<String> name = new ObjectWrapper<>(baseName);
        List<Game> games = Game.loadGames(applicationContext);
        int i = 0;
        while(games.stream()
                .map(Game::getName)
                .anyMatch(gameName -> gameName.equals(name.getObject()))) {
            
            name.setObject(baseName + String.format(" (%s)", ++i));
        }
        
        var game = new Game();
        game.put(GAME_FOLDER_PATH, folderToPatch.getPath());
        game.put(GAME_SCRIPT, script.getName());
        game.put(GAME_NAME, name.getObject());
        game.put(RENPY_VERSION, version);
        
        Game.storeGame(applicationContext, game);
    }
    
    private String removeExtension(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }
    
    private File getScriptFile() {
        var files = folderToPatch.listFiles();
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
        
        File script = null;
        if(files.length > 1) {
            int index = SelectMainPythonDialog.newDialog(
                Stream.of(files)
                    .map(File::getName)
                    .toArray(String[]::new))
            .showForResultAndBlock(dialog.getParentFragmentManager());
            
            script = index >= 0 ? files[index] : null;
        } else {
            script = files[0];
        }
        return script;
    }

    private void showScriptNotFoundError() {
        SimpleDialog.show(
                dialog.getParentFragmentManager(), 
                applicationContext.getString(R.string.patch_error),
                applicationContext.getString(R.string.py_script_not_found));
    }
    
    private void setMessage(String message) {
        this.message = message;
        if(dialog != null) {
            dialog.setMessage(message);
        }
    }
    
    @Override
    public void onFinally() {
        if(dialog != null) {
            dialog.tear();
        }
    }    
    
    public void setDialog(ApplyPatchDialog dialog) {
    	this.dialog = dialog;
        
        setMessage(message);
    }
    
}
