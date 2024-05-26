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

import android.content.Context;
import android.util.Log;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.dialogs.ApplyPatchDialog;
import com.abiddarris.vnpyemulator.utils.BaseRunnable;
import com.abiddarris.vnpyemulator.utils.Hash;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                R.string.patching) + " " + folderToPatch);
        
        File script = getScriptFile();
        String version = RenPyParser.getVersion(folderToPatch);
        PatchSource source = PatchSource.getPatcher();
        
        setMessage(Arrays.toString(source.getVersions()));
        
        Thread.sleep(2000);
        
        setMessage(version);
        
        if(!Arrays.asList(source.getVersions())
            .contains(version)) {
               // TODO: Implemenent Error handling if version is not available
            return;
        }
        
        Thread.sleep(2000);
        
        setMessage(script == null ? "null" : script.getPath());
        
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
    }
    
    private File getScriptFile() {
        var files = folderToPatch.listFiles();
        if(files == null) {
            // TODO: Add error handling
            
        }
        
        List<File> scripts = new ArrayList<>();
        for(var file : files) {
        	if(file.getName().endsWith(".py")) {
                scripts.add(file);
            }
        }
        
        if(scripts.size() < 1) {
            // TODO: Add error handling
        }
        
        File script = null;
        if(scripts.size() > 1) {
            // TODO: open dialog for users to choose
        } else {
            script = scripts.get(0);
        }
        return script;
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
