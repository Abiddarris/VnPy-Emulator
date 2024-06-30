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

import android.content.pm.ApplicationInfo;
import static android.widget.FrameLayout.LayoutParams.MATCH_PARENT;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;

import android.app.Notification;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboard;
import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboardOptions;
import com.abiddarris.plugin.PluginArguments;
import com.abiddarris.plugin.R;

import org.libsdl.app.SDLActivity;
import org.renpy.android.PythonSDLActivity;

public class RenPyGame implements DefaultLifecycleObserver {
    
    public static final String GAME_OVERLAY_ID = "game_overlay";
  
    private static final int NOTIFICATION_ID = 7890;
    
    private static RenPyGame game;
    
    private PluginArguments arguments;
    private PythonSDLActivity activity;
    
    private Notification notification;
    private NotificationManagerCompat notificationManager;
    
    private RenPyGame(PythonSDLActivity activity) {
        this.activity = activity;
    }
    
    public PluginArguments getArguments() {
        if(arguments == null) {
            arguments = new PluginArguments(activity.getIntent());
        }
        
        return arguments;
    }
    
    public void onCreate(Bundle savedInstanceState) {
        notificationManager = NotificationManagerCompat.from(activity);
        notificationManager.createNotificationChannel(
            new NotificationChannelCompat.Builder(GAME_OVERLAY_ID, IMPORTANCE_DEFAULT)
                .setName(activity.getString(R.string.game_overlay_channel_title))
                .setDescription(activity.getString(R.string.game_overlay_channel_desc))
                .build()
        );
        
        activity.getLifecycle()
            .addObserver(this);
        
        ApplicationInfo info = activity.getApplicationInfo();
        
        notification = new NotificationCompat.Builder(activity, GAME_OVERLAY_ID)
            .setOngoing(true)
            .setShowWhen(false)
            .setSmallIcon(info.icon)
            .setContentTitle(activity.getString(info.labelRes))
            .setContentText(activity.getString(R.string.notification_text))
            .build();
    }    
    
    @Override
    public void onResume(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    @Override
    public void onPause(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        
        notificationManager.cancel(NOTIFICATION_ID);
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
