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

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ConnectionRunnable implements BaseRunnable {

    private ErrorHandlerService service;
    private Socket socket;

    ConnectionRunnable(ErrorHandlerService service, Socket socket) {
        this.service = service;
        this.socket = socket;
    }
    
    @Override
    public void execute() throws Exception {
        try (BufferedInputStream stream = new BufferedInputStream(socket.getInputStream())) {
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            String applicationName = objectStream.readUTF();
            Throwable throwable = (Throwable)objectStream.readObject();
            
            service.getOnErrorOccurs()
                .onErrorOccurs(applicationName, throwable);
        }
    }

}
