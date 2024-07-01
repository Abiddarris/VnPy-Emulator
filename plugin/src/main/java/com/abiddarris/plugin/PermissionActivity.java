package com.abiddarris.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abiddarris.common.android.utils.Permissions;
import com.google.android.material.color.DynamicColors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermissionActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        
        setupErrorHandler();
        
        if(Permissions.isManageExternalStorageGranted(this)) {
            permissionGranted();
            return;
        }
        Permissions.requestManageExternalStoragePermission(this, 
            getString(R.string.external_storage_permission_required_message), (granted) -> {
                if(granted) {
                    permissionGranted();
                }
            });
    }
    
    protected void setupErrorHandler() {
        Intent intent = getIntent();
        int port = new PluginArguments(intent)
                    .getErrorPort();
        
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            sendError(port, throwable);
        });
    }
    
    private void sendError(int port, Throwable throwable) {
        ErrorSenderRunnable runnable = new ErrorSenderRunnable(port, throwable);
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(runnable);
        
        synchronized(runnable) {
            if(!runnable.isDone()) {
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        executor.shutdown();
        System.exit(1);
    }
    
    protected void permissionGranted() {
    }
}
