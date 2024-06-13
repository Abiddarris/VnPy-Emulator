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

import android.content.Context;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.sources.Source;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Plugin {
    
    private String[] components;
    
    private Plugin(String[] components) {
        this.components = components;
    }
    
    public String getVersion() {
        return components[0];
    }
    
    public String getPrivateRenPyVersion() {
        return components[1];
    }
    
    public String getPrivateRenPyDownloadPath() {
        return components[2];
    }
    
    public static Plugin[] getPlugins(Context context) throws IOException {
        Source source = Source.getSource();
        File cache = Files.getCacheFolder(context);
        File versionsCache = new File(cache, "plugin_version_cache");
        File versions = new File(cache, "plugin_version");
        
        try(BufferedInputStream inputStream = new BufferedInputStream(
                source.open("plugins/versions"));
            BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(versionsCache))) {
            byte[] buf = new byte[1024 * 8];
            int len;
            while((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            
            versions.delete();
            versionsCache.renameTo(versions);
        } catch (IOException e) {
            if(!versions.exists()) {
                throw e;
            }
        }
        
        try (BufferedReader reader = new BufferedReader(
                new FileReader(versions))) {
            return reader.lines()
                .map(string -> string.split("//"))
                .map(Plugin::new)
                .toArray(Plugin[]::new);
        }
    }
    
    
}
