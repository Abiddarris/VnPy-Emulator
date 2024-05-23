/***********************************************************************************
 * <one line to give the program's name and a brief idea of what it does.>
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
package com.abiddarris.renpyemulator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.abiddarris.renpyemulator.databinding.ActivityMainBinding;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.renpy.android.PythonSDLActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
        setContentView(binding.getRoot());
        
        //Quick fix
        try {
           new File(getExternalMediaDirs()[0], "python")
                .createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        
        binding.openGame.setOnClickListener(v -> startActivity(new Intent(this, PythonSDLActivity.class)));
        binding.extract.setOnClickListener(v -> {
            try {
                extract();
            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
                    .show();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    public void extract() throws IOException {
    	List<File> files = new ArrayList<>();
        File internal = getFilesDir();
        getFilesTree(files, internal);
        
        String internalPath = internal.getAbsolutePath();
        File dest = getExternalMediaDirs()[0];
        for(var file : files) {
            String relativePath = file.getPath()
                .replace(internalPath, "");
            
            if(relativePath.isBlank()) {
                continue;
            }
            
            File destFile = new File(dest, relativePath);
            if(file.isDirectory()) {
                destFile.mkdirs();
                continue;
            }
            
            BufferedInputStream is = new BufferedInputStream(
                new FileInputStream(file));
            BufferedOutputStream os = new BufferedOutputStream(
                new FileOutputStream(destFile));
            byte[] buf = new byte[1024 * 8];
            int len;
            while((len = is.read(buf)) != -1) {
                os.write(buf,0,len);
            }
            os.flush();
            os.close();
            is.close();
        }
    }
    
    private void getFilesTree(List<File> files, File file) {
    	files.add(file);
        
        if(!file.isDirectory()) {
            return;
        }
        
        for(var child : file.listFiles()) {
        	getFilesTree(files, child);
        }
    }
}
