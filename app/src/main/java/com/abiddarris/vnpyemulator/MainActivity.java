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

import androidx.lifecycle.ViewModelProvider;

import com.abiddarris.common.logs.Logger;
import com.abiddarris.common.logs.Logs;
import com.abiddarris.plugin.PermissionActivity;
import com.abiddarris.vnpyemulator.databinding.ActivityMainBinding;
import com.abiddarris.vnpyemulator.errors.ErrorViewModel;
import com.abiddarris.vnpyemulator.games.GameListFragment;

public class MainActivity extends PermissionActivity {
   
    private ActivityMainBinding binding;
    private ErrorViewModel errorViewModel;
    private Logger log = Logs.newLogger(DEBUG, this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        errorViewModel = new ViewModelProvider(this)
            .get(ErrorViewModel.class);
        errorViewModel.attach(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, GameListFragment.class, null)
                .commit();
    }
    
    @Override
    protected void setupErrorHandler() {
        //Do nothing
    }

    public int getPort() {
        return errorViewModel.getPort();
    }

}
