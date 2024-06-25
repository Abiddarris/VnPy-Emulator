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
import android.widget.FrameLayout;

import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboard;
import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboardOptions;
import com.abiddarris.plugin.PluginArguments;

import org.libsdl.app.SDLActivity;
import org.renpy.android.PythonSDLActivity;

public class RenPyGame {
    
    private static RenPyGame game;
    
    private PluginArguments arguments;
    private PythonSDLActivity activity;
    
    private RenPyGame(PythonSDLActivity activity) {
        this.activity = activity;
    }
    
    public PluginArguments getArguments() {
        if(arguments == null) {
            arguments = new PluginArguments(activity.getIntent());
        }
        
        return arguments;
    }
    
    public void setContentView(View view) {
        var keyboard = new VirtualKeyboard(activity);
        var options = new VirtualKeyboardOptions(activity, keyboard);
        options.setKeyboardFolderPath(
            getArguments()
                .getKeyboardFolderPath()
        );
        
        keyboard.setKeyListener((event, keycode) -> {
            switch(event) {
                case DOWN :
                    SDLActivity.onNativeKeyDown(keycode);
                    break;
                case UP :
                    SDLActivity.onNativeKeyUp(keycode);
            }
        });
        
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
