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
package com.abiddarris.plugin;

import com.abiddarris.common.utils.BaseRunnable;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ErrorSenderRunnable implements BaseRunnable {

    private boolean done;
    private int port;
    private Throwable throwable;

    ErrorSenderRunnable(int port, Throwable throwable) {
        this.port = port;
        this.throwable = throwable;
    }
    
    @Override
    public void execute() throws Exception {
        try (Socket socket = new Socket("localhost", port)) {
            ObjectOutputStream stream = new ObjectOutputStream(
                new BufferedOutputStream(
                    socket.getOutputStream()
                )
            );
            stream.writeUTF(" ");
            stream.writeObject(throwable);
            stream.flush();
        }
    }
    
    @Override
    public void onFinally() {
        BaseRunnable.super.onFinally();
        
        done = true;
        
        synchronized(this) {
            notify();
        }
    }
    
    public boolean isDone() {
        return done;
    }
    
}
