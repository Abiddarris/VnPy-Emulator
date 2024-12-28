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
    
    public static File getKeyboardFolder(Context context) {
        File keyboardFolder = new File(getVnPyEmulatorFolder(context), "keyboards");
        createDirectory(keyboardFolder);
        
        return keyboardFolder;
    }
    
    public static File getRenPyPrivateFolder(Context context) {
        File privateFolder = new File(getVnPyEmulatorFolder(context), "private");
        createDirectory(privateFolder);
        
        return privateFolder;
    }
    
    public static File getCacheFolder(Context context) {
    	File cacheFolder = new File(getVnPyEmulatorFolder(context), ".cache");
        if(!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        
        return cacheFolder;
    }

    public static File getIconFolder(Context context) {
        File iconFolder = new File(getVnPyEmulatorFolder(context), "icons");
        if(!iconFolder.exists()) {
            iconFolder.mkdirs();
        }

        return iconFolder;
    }
    
    /**
     * Returns {@code File} where information about games are stored.
     *
     * @param context Context
     * @return {@code File} where information about games are stored.
     */
    public static File getGamesFile(Context context) {
        return new File(context.getFilesDir(), "game");
    }
    
    public static File getLogFile(Context context) {
        File log = new File(getVnPyEmulatorFolder(context), "log.txt");
        
        return log;
    }
    
    private static void createDirectory(File file) {
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
