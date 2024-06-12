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
package com.abiddarris.vnpyemulator.files;

import android.content.Context;
import android.os.Build;
import java.io.File;

public class Files {
    
    @SuppressWarnings("deprecation")
    public static File getVnPyEmulatorFolder(Context context) {
        File[] files = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && "samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
            files = context.getExternalMediaDirs();
            
            for(var file : files) {
                if(file != null) {
                    return file;
                }
            } 
        }
        File file = context.getExternalFilesDir(null);
        return file == null ? null : file;
    }
    
    public static File getPythonFolders(Context context) {
        var folder = new File(context.getExternalFilesDir(null), "python");
        if(!folder.exists()) {
            folder.mkdirs();
        }
        
    	return folder;
    }
    
    public static File getExternalCache(Context context) {
    	var folder = context.getExternalCacheDir();
        if(!folder.exists()) {
        	folder.mkdirs();
        }
        return folder;
    }
    
    public static File getPythonVersionCache(Context context) {
    	return new File(getExternalCache(context), "pyversioncache");
    }
}
