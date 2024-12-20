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

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.FrameLayout.LayoutParams.MATCH_PARENT;
import static android.widget.Toast.LENGTH_LONG;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboard;
import com.abiddarris.common.android.virtualkeyboard.VirtualKeyboardOptions;
import com.abiddarris.plugin.PluginArguments;
import com.abiddarris.plugin.R;
import com.abiddarris.plugin.databinding.LayoutVirtualKeyboardBinding;

import org.libsdl.app.SDLActivity;
import org.renpy.android.PythonSDLActivity;

public class RenPyGame extends BroadcastReceiver implements DefaultLifecycleObserver {
    
    public static final String GAME_OVERLAY_ID = "game_overlay";
  
    private static final int NOTIFICATION_ID = 7890;
    private static final String ACTION_SHOW_GAME_OVERLAY = "showGameOverlay";
    
    private static RenPyGame game;
    
    private PluginArguments arguments;
    private PythonSDLActivity activity;
    
    private Notification notification;
    private NotificationManagerCompat notificationManager;
    private LayoutVirtualKeyboardBinding ui;

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
        Toast.makeText(activity, R.string.show_vkeyboard_help, LENGTH_LONG)
            .show();
        
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
        PendingIntent intent = PendingIntent.getBroadcast(
            activity, 0, 
            new Intent(ACTION_SHOW_GAME_OVERLAY),
            FLAG_IMMUTABLE);
        
        notification = new NotificationCompat.Builder(activity, GAME_OVERLAY_ID)
            .setOngoing(true)
            .setShowWhen(false)
            .setSmallIcon(info.icon)
            .setContentIntent(intent)
            .setContentTitle(activity.getString(info.labelRes))
            .setContentText(activity.getString(R.string.notification_text))
            .build();
    }    
    
    @Override
    public void onResume(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        
        activity.registerReceiver(this, new IntentFilter(ACTION_SHOW_GAME_OVERLAY));
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    @Override
    public void onPause(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        
        notificationManager.cancel(NOTIFICATION_ID);
        activity.unregisterReceiver(this);
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        ui.keyboard.setVisibility(VISIBLE);
    }
    
    public void setContentView(View view) {
        ui = LayoutVirtualKeyboardBinding.inflate(activity.getLayoutInflater());
        ui.hideButton.setOnClickListener(v -> ui.keyboard.setVisibility(GONE));
        ui.closeButton.setOnClickListener(v ->
             new CloseGameConfirmationDialog()
                .show(activity.getSupportFragmentManager(), null));
        ui.keyboardOptions.setKeyboardFolderPath(
            getArguments().getKeyboardFolderPath()
        );
        ui.keyboard.setKeyListener((event, keycode) -> {
            switch(event) {
                case DOWN :
                    SDLActivity.onNativeKeyDown(keycode);
                    break;
                case UP :
                    SDLActivity.onNativeKeyUp(keycode);
            }
        });
        
        activity.mFrameLayout
            .addView(ui.keyboard, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }
    
    public static RenPyGame getInstance(PythonSDLActivity activity) {
        if(game == null) {
        	game = new RenPyGame(activity);
        }
        return game;
    }
    
}
