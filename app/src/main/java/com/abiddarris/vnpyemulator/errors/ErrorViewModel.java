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
package com.abiddarris.vnpyemulator.errors;

import android.annotation.SuppressLint;
import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.logs.Level;
import com.abiddarris.common.logs.Logger;
import com.abiddarris.common.logs.Logs;
import com.abiddarris.vnpyemulator.MainActivity;
import com.abiddarris.vnpyemulator.errors.ErrorHandlerService.ErrorHandlerBinder;

public class ErrorViewModel extends ViewModel 
        implements ServiceConnection, OnErrorOccurs, LifecycleEventObserver {
   
    private boolean show;
    private boolean serviceRegistered;
    private Logger debug = Logs.newLogger(Level.DEBUG, this);
    private ExceptionDialog errorDialog;
    
    @SuppressLint("StaticFieldLeak")
    private ErrorHandlerService service;
    
    @SuppressLint("StaticFieldLeak")
    private MainActivity activity;
    
    public void attach(MainActivity activity) {
        boolean firstTime = this.activity == null;
        
        this.activity = activity;
        activity.getLifecycle()
            .addObserver(this);
        
        if(!firstTime)
            return;
        
        serviceRegistered = activity.bindService(
            new Intent(activity, ErrorHandlerService.class),
            this, BIND_AUTO_CREATE
        );
    }
    
    public int getPort() {
        return service.getPort();
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
        
        showDialog();
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        
        if(serviceRegistered)
            activity.unbindService(this);
    }
    
    @Override
    public void onStateChanged(LifecycleOwner owner, Event event) {
        debug.log("State changes for " + owner + " to " + event.name());
        switch(event) {
            case ON_CREATE :
            case ON_START :
            case ON_RESUME :
                show = true;
                showDialog();
                break;
            case ON_PAUSE :
            case ON_STOP :
            case ON_DESTROY :
                show = false;
        }
    }
    
    private void showDialog() {
        if(!show || errorDialog == null) {
            return;
        }
        
        errorDialog.show(activity.getSupportFragmentManager(), null);
        errorDialog = null;
    }
}
