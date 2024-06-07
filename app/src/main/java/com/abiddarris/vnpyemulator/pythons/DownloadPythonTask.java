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

import android.content.Context;
import com.abiddarris.vnpyemulator.dialogs.Task;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.patches.Source;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

public class DownloadPythonTask extends Task {
    
    private Context context;
    private ExternalPython python;
    private Runnable success;
    
    public DownloadPythonTask(Context context, ExternalPython python, Runnable success) {
        this.context = context;
        this.python = python;
        this.success = success;
    }
    
    @Override
    public void execute() throws Exception {
        var dest = new File(Files.getPythonFolders(context), python.getVersionName());
        dest.mkdirs();
        
        var temp = new File(dest.getPath() + ".tar.gz");
        
        try (var is = new BufferedInputStream(Source.getSource()
                .open("python/" + python.getFilePath()))) {
            var os = new BufferedOutputStream(new FileOutputStream(temp));
            os.write(is.readAllBytes());
            os.flush();
            os.close();
        }
        
        var is = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(temp))));
        TarEntry entry;
        while((entry = is.getNextEntry()) != null) {
            var destination = new File(dest, entry.getName());
            if(entry.isDirectory()) {
                destination.mkdirs();
                continue;
            } 
            var os = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] buf = new byte[8192];
            int len;
            while((len = is.read(buf)) != -1) {
                os.write(buf,0,len);
            }
            os.flush();
            os.close();
        }
        is.close();
        
        temp.delete();
        success.run();
    }
    

}
