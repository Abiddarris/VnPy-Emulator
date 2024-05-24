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
import com.abiddarris.renpyemulator.dialogs.ApplyPatchDialog;
import com.abiddarris.renpyemulator.utils.BaseRunnable;
import java.io.IOException;

public class PatchRunnable implements BaseRunnable {
    
    private ApplyPatchDialog dialog;
    private Context applicationContext;
    private String message;
    
    public PatchRunnable(ApplyPatchDialog dialog) {
        this.dialog = dialog;
        
        applicationContext = dialog.getActivity()
            .getApplicationContext();
    }
    
    @Override
    public void execute() throws IOException {
        setMessage("Finding path");
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
