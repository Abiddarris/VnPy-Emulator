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
package com.abiddarris.vnpyemulator;

import static com.abiddarris.common.logs.Logs.setLogFactory;
import static com.abiddarris.vnpyemulator.files.Files.getLogFile;

import android.app.Application;

import com.abiddarris.common.android.logs.factory.AndroidLogFactory;
import com.abiddarris.common.logs.factory.FileLogFactory;
import com.abiddarris.common.logs.factory.MultipleLogFactory;
import com.google.android.material.color.DynamicColors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class VnPyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            setLogFactory(new MultipleLogFactory(
                new AndroidLogFactory(),
                new FileLogFactory(getLogFile(this))
            ));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            getExternalFilesDir(null).mkdirs();
            try (var writer = new PrintWriter(new BufferedWriter(
                new FileWriter(new File(getExternalFilesDir(null), "error.txt"), true)))) {
                 
                e.printStackTrace(writer);
                writer.flush();    
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(1);
        });
        
        try {
            var writer = new FileOutputStream(new File(getExternalFilesDir(null), "log.txt"));
            
            System.setOut(new PrintStream(writer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
    
}
