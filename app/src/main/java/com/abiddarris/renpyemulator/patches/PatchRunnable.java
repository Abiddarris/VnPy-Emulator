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
package com.abiddarris.renpyemulator.patches;

import android.content.Context;
import com.abiddarris.renpyemulator.R;
import com.abiddarris.renpyemulator.dialogs.ApplyPatchDialog;
import com.abiddarris.renpyemulator.utils.BaseRunnable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatchRunnable implements BaseRunnable {
    
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
    public void execute() throws IOException {
        setMessage(applicationContext.getString(
                R.string.patching) + " " + folderToPatch);
        
        String version = RenPyParser.getVersion(folderToPatch);
        
        setMessage(version == null ? "null" : version);
    }
    
    private void setMessage(String message) {
        this.message = message;
        if(dialog != null) {
            dialog.setMessage(message);
        }
    }
    
    public void setDialog(ApplyPatchDialog dialog) {
    	this.dialog = dialog;
        
        setMessage(message);
    }
    
}
