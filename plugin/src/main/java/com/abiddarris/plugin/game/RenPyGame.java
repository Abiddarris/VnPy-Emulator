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
package com.abiddarris.plugin.game;

import static android.widget.FrameLayout.LayoutParams.MATCH_PARENT;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboard;
import android.view.KeyEvent;
import com.abiddarris.plugin.R;
import com.abiddarris.plugin.databinding.LayoutOptionsBinding;
import org.libsdl.app.SDLActivity;
import org.renpy.android.PythonSDLActivity;

public class RenPyGame {
    
    private static RenPyGame game;
    
    private PythonSDLActivity activity;
    
    private RenPyGame(PythonSDLActivity activity) {
        this.activity = activity;
    }
    
    public void setContentView(View view) {
        var binding = LayoutOptionsBinding.inflate(activity.getLayoutInflater());
        
        var keyboard = new VirtualKeyboard(activity);
        keyboard.addView(binding.getRoot());
        keyboard.setKeyListener((event, keycode) -> {
            switch(event) {
                case DOWN :
                    SDLActivity.onNativeKeyDown(keycode);
                    break;
                case UP :
                    SDLActivity.onNativeKeyUp(keycode);
            }
        });
        
        binding.edit.setOnClickListener(v -> {
            keyboard.setEdit(!keyboard.isEdit());
             
            int visibility = keyboard.isEdit() ? View.VISIBLE : View.GONE;
            binding.add.setVisibility(visibility);    
        });
        
        binding.add.setOnClickListener(v -> keyboard.addButton());
        
        activity.mFrameLayout
            .addView(keyboard, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }
    
    public static RenPyGame getInstance(PythonSDLActivity activity) {
        if(game == null) {
        	game = new RenPyGame(activity);
        }
        return game;
    }
    
}
