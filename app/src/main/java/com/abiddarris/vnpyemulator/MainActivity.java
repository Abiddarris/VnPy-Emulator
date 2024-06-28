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

import static com.abiddarris.common.logs.Level.DEBUG;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abiddarris.common.android.about.AboutActivity;
import com.abiddarris.common.android.tasks.TaskViewModel;
import com.abiddarris.common.logs.Logger;
import com.abiddarris.common.logs.Logs;
import com.abiddarris.plugin.PermissionActivity;
import com.abiddarris.vnpyemulator.adapters.GameAdapter;
import com.abiddarris.vnpyemulator.databinding.ActivityMainBinding;
import com.abiddarris.vnpyemulator.dialogs.AboutGameInformationDialog;
import com.abiddarris.vnpyemulator.dialogs.AddNewGameDialog;
import com.abiddarris.vnpyemulator.dialogs.DeleteGameDialog;
import com.abiddarris.vnpyemulator.errors.ErrorViewModel;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.patches.PatchRunnable;


public class MainActivity extends PermissionActivity {
   
    private ActivityMainBinding binding;
    private ErrorViewModel errorViewModel;
    private Logger log = Logs.newLogger(DEBUG, this);
    private TaskViewModel model;
    private GameAdapter adapter;
    private View currentItem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        model = TaskViewModel.getInstance(this);
       
        errorViewModel = new ViewModelProvider(this)
            .get(ErrorViewModel.class);
        errorViewModel.attach(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
   
        adapter = new GameAdapter(this);
        
        binding.games.setLayoutManager(new LinearLayoutManager(this));
        binding.games.setAdapter(adapter);
    }
    
    @Override
    protected void setupErrorHandler() {
        //Do nothing
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
       
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_new_game) {
            new AddNewGameDialog()
                .showForResult(getSupportFragmentManager(), path -> {
                    if(path != null)
                        model.execute(new PatchRunnable(path));
                });
            return true; 
        }
        
        if(item.getItemId() == R.id.about) {
            startActivity(AboutActivity.newAboutActivity(this, "ABOUT", "ATTRIBUTION"));
            return true;
        }
        
        return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        
        getMenuInflater().inflate(R.menu.layout_game_menu, menu);
        currentItem = view;
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Game game = adapter.get((int)currentItem.getTag());
        if(item.getItemId() == R.id.delete) {
            DeleteGameDialog.getInstance(game)
                .show(getSupportFragmentManager(), null);
            return true;
        } 
        
        if(item.getItemId() == R.id.open) {
            open(game);
            return true;
        }
        
        if(item.getItemId() == R.id.about) {
            AboutGameInformationDialog.newInstance(game)
                .show(getSupportFragmentManager(), null);
            return true;
        }
        
        return false;
    }
  
    public int getPort() {
        return errorViewModel.getPort();
    }
    
    public void open(Game game) {
        adapter.open(game);
    }
    
    public TaskViewModel getTaskModel() {
        return model;
    }
    
    public GameAdapter getAdapter() {
        return adapter;
    }
    
    public void refresh() {
        runOnUiThread(() -> {
            adapter.refresh();
            adapter.notifyDataSetChanged();
        });
    }
}
