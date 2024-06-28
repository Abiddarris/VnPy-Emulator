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

import com.abiddarris.common.utils.BaseRunnable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ServerRunnable implements BaseRunnable {

    private ErrorHandlerService service;
    private ExecutorService executor;
    private ServerSocket serverSocket;

    ServerRunnable(ErrorHandlerService service, ExecutorService executor) {
        this.service = service;
        this.executor = executor;
    }

    @Override
    public void execute() throws Exception {
        serverSocket = new ServerSocket(0);
        while (true) {
            Socket client = serverSocket.accept();
            
            executor.submit(new ConnectionRunnable(service, client));
        }
    }
    
    @Override
    public void onFinally() {
        BaseRunnable.super.onFinally();
        
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
