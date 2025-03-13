/***********************************************************************************
 * Copyright (C) 2024 - 2025 Abiddarris
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

import static com.abiddarris.vnpyemulator.games.Game.GAME_FOLDER_PATH;
import static com.abiddarris.vnpyemulator.games.Game.GAME_SCRIPT;

import androidx.fragment.app.DialogFragment;

import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.common.utils.ObjectWrapper;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.games.GameListFragment;
import com.abiddarris.vnpyemulator.games.GameLoader;
import com.abiddarris.vnpyemulator.renpy.RenPyParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PatchRunnable extends TaskDialog {
    
    public static final String TAG = PatchRunnable.class.getSimpleName();
  
    private static final String DIALOG_TAG = "applyPatchDialog";
    private File folderToPatch;
    
    public PatchRunnable(String folderToPatch) {
        this.folderToPatch = new File(folderToPatch);
    }
    
    @Override
    protected String getTag() {
        return DIALOG_TAG;
    }
    
    @Override
    protected DialogFragment newDialog() {
        return new ApplyPatchDialog();
    }
    
    @Override
    public void execute() throws Exception {
        setMessage(getString(
                R.string.patching));
        
        File script = getScriptFile();
        if(script == null) {
            return;
        }
        PatchSource source = PatchSource.getPatcher();
        
        String version;
        try {
            version = RenPyParser.getVersion(folderToPatch);
        } catch (IOException e) {
            e.printStackTrace();
            
            version = null;
        }
        String renPyVersion = version;
        
        String[] versions = source.getVersions();
        if(!Arrays.asList(versions).contains(version)) {
            var dialog = new SelectPatchVersionDialog();
            dialog.saveVariable(SelectPatchVersionDialog.MESSAGE,
                 version == null ? getString(R.string.unknown_version_message) 
                 : getString(R.string.renpy_version_not_available, version));
            dialog.setItems(versions, -1);
           
            int selection = dialog.showForResultAndBlock(getFragmentManager());
            
            if(selection < 0)
                return;
            
            version = versions[selection];
        }

        Patch patch = source.getPatch(version);
        Patcher[] patchers = patch.getPatchers();

        for(PatchFile patchFile : patchers[patchers.length - 1].getPatches()) {
            try {
                patchFile.patch(folderToPatch, false);
            } catch (IncompatiblePatchException e) {
                var dialog = new IncompatiblePatchDialog();
                dialog.saveVariable(IncompatiblePatchDialog.FILE_NAME, patchFile.getTarget());

                boolean result = dialog.showForResultAndBlock(getFragmentManager());
                if(!result) {
                    continue;
                }

                patchFile.patch(folderToPatch, true);
            }

        }
        
        String baseName = removeExtension(script.getName());
        ObjectWrapper<String> name = new ObjectWrapper<>(baseName);
        List<Game> games = Game.loadGames(getApplicationContext());
        int i = 0;
        while(games.stream()
                .map(Game::getName)
                .anyMatch(gameName -> gameName.equals(name.getObject()))) {
            
            name.setObject(baseName + String.format(" (%s)", ++i));
        }

        Game game = new Game();
        game.put(GAME_FOLDER_PATH, folderToPatch.getPath());
        game.put(GAME_SCRIPT, script.getName());
        game.setName(name.getObject());
        game.setPatchVersion(version);
        game.setRenPyVersion(renPyVersion);

        //EditGameDialog.editGame(game)
          //      .showForResultAndBlock(getFragmentManager());;

        GameLoader.addGame(getApplicationContext(), game);
        GameLoader.saveGames(getApplicationContext());
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
            .showForResultAndBlock(getFragmentManager());
            
            script = index >= 0 ? files[index] : null;
        } else {
            script = files[0];
        }
        return script;
    }
    
    @Override
    public void onFinally() {
        super.onFinally();
        
        GameListFragment fragment = (GameListFragment) getOwner();
        fragment.refresh();
    }

    private void showScriptNotFoundError() {
        SimpleDialog.show(
                getFragmentManager(), 
                getString(R.string.patch_error),
                getString(R.string.py_script_not_found));
    }
    
    private void setMessage(String message) {
        ApplyPatchDialog dialog = getDialog();
        
        dialog.setMessage(message);
    }
    
}
