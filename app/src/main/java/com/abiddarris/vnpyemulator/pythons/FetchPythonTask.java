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

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.dialogs.Task;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.patches.Source;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchPythonTask extends Task {
    
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
        
        List<ExternalPython> pythons = new ArrayList<>();
        try (var reader = new BufferedReader(new FileReader(
                Files.getPythonVersionCache(getApplicationContext())))) {
         	reader.lines()
                .forEach(line -> {
                    var components = line.split("//");
                    pythons.add(new ExternalPython(components[0], components[1]));
                });
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
       
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
