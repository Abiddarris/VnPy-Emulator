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

import static com.abiddarris.common.logs.Level.DEBUG;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.abiddarris.common.logs.Logger;
import com.abiddarris.common.logs.Logs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ErrorHandlerService extends Service {

    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private ErrorHandlerBinder binder;
    private Logger logger = Logs.newLogger(DEBUG, this);
    private OnErrorOccurs occurs;
    private ServerRunnable runnable;

    @Override
    @Nullable
    public IBinder onBind(Intent arg0) {
        if (binder == null) {
            binder = new ErrorHandlerBinder();
        }
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        logger.log("ErrorHandlerService.onCreate() is called");
        
        executor.submit((runnable = new ServerRunnable(this, executor)));
    }

    public int getPort() {
        return runnable.getPort();
    }
    
    public OnErrorOccurs getOnErrorOccurs() {
        return this.occurs;
    }

    public void setOnErrorOccurs(OnErrorOccurs occurs) {
        this.occurs = occurs;
    }

    public class ErrorHandlerBinder extends Binder {

        public ErrorHandlerService getService() {
            return ErrorHandlerService.this;
        }
    }

}
