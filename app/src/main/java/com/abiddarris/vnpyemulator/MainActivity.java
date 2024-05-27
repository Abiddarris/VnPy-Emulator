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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.abiddarris.vnpyemulator.adapters.GameAdapter;
import com.abiddarris.vnpyemulator.databinding.ActivityMainBinding;
import com.abiddarris.vnpyemulator.dialogs.AddNewGameDialog;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
   
    private ActivityMainBinding binding;
    private GameAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
       
        //Quick fix
        try {
           new File(getExternalMediaDirs()[0], "python")
                .createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        adapter = new GameAdapter(this);
        
        binding.games.setLayoutManager(new LinearLayoutManager(this));
        binding.games.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
       
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_new_game:
                new AddNewGameDialog()
                    .show(getSupportFragmentManager(), null);
                return true; 
        }
        
        return false;
    }
 
}
