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

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abiddarris.common.android.about.AboutActivity;
import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.android.tasks.TaskViewModel;
import com.abiddarris.common.android.utils.Permissions;
import com.abiddarris.common.logs.Logger;
import com.abiddarris.common.logs.Logs;
import com.abiddarris.plugin.PermissionActivity;
import com.abiddarris.vnpyemulator.adapters.GameAdapter;
import com.abiddarris.vnpyemulator.databinding.ActivityMainBinding;
import com.abiddarris.vnpyemulator.dialogs.AboutGameInformationDialog;
import com.abiddarris.vnpyemulator.dialogs.AddNewGameDialog;
import com.abiddarris.vnpyemulator.dialogs.DeleteGameDialog;
import com.abiddarris.vnpyemulator.errors.ErrorHandlerService;
import com.abiddarris.vnpyemulator.errors.ErrorHandlerService.ErrorHandlerBinder;
import com.abiddarris.vnpyemulator.errors.OnErrorOccurs;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.patches.PatchRunnable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends PermissionActivity implements ServiceConnection, OnErrorOccurs {
   
    private ActivityMainBinding binding;
    private ExceptionDialog errorDialog;
    private ErrorHandlerService service;
    private Logger log = Logs.newLogger(DEBUG, this);
    private TaskViewModel model;
    private GameAdapter adapter;
    private View currentItem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(!bindService(
            new Intent(this, ErrorHandlerService.class),
            this, BIND_AUTO_CREATE)) {
           Toast.makeText(this, "Cannot start ErrorHandlerService", Toast
                .LENGTH_LONG)
                .show();
        }
        
        model = TaskViewModel.getInstance(this);
        
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
    protected void onStart() {
        super.onStart();
        
        if(errorDialog != null) {
            errorDialog.show(getSupportFragmentManager(), null);
            errorDialog = null;
        }
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unbindService(this);
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((ErrorHandlerBinder)binder)
            .getService();
        service.setOnErrorOccurs(this);
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
    
    @Override
    public void onErrorOccurs(String applicationName, Throwable throwable) {
        errorDialog = new ExceptionDialog();
        errorDialog.setThrowable(throwable);
    }
    
    public int getPort() {
        return service.getPort();
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
