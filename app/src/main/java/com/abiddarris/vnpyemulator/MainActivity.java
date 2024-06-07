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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.abiddarris.common.android.about.AboutActivity;
import com.abiddarris.common.android.utils.Permissions;
import com.abiddarris.vnpyemulator.adapters.GameAdapter;
import com.abiddarris.vnpyemulator.databinding.ActivityMainBinding;
import com.abiddarris.vnpyemulator.dialogs.AddNewGameDialog;
import com.abiddarris.vnpyemulator.patches.PatchRunnable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
   
    private ActivityMainBinding binding;
    private ActivityViewModel model;
    private GameAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        model = new ViewModelProvider(this)
            .get(ActivityViewModel.class);
        model.attachActivity(this);
        
        Permissions.requestManageExternalStoragePermission(
            this, getString(R.string.external_storage_permission_required_message));
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
   
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
                    .showForResult(getSupportFragmentManager(), message -> 
                        model.execute(new PatchRunnable(message)));
                return true; 
            case R.id.about :
                startActivity(AboutActivity.newAboutActivity(this, "ABOUT", "ATTRIBUTION"));
                return true;
        }
        
        return false;
    }
    
    public void detach() {
        model.currentPatchRunnable = null;
        
        runOnUiThread(() -> {
            adapter.refresh();
            adapter.notifyDataSetChanged();
        });
    }
 
    public static class ActivityViewModel extends ViewModel {
        
        private MainActivity activity;
        private ExecutorService executor = Executors.newSingleThreadExecutor();
        private PatchRunnable currentPatchRunnable;
        
        private void attachActivity(MainActivity activity) {
            this.activity = activity;
            
            attachToRunnable();
        }
        
        private void attachToRunnable() {
            if(currentPatchRunnable != null) {
                currentPatchRunnable.setActivity(activity);
            }
        }
        
        private void execute(PatchRunnable runnable) {
            this.currentPatchRunnable = runnable;
            
            attachToRunnable();
            
            executor.submit(runnable);
        }
        
        @Override
        protected void onCleared() {
            super.onCleared();
            
            executor.shutdown();
        }
        
    }
}
