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
package com.abiddarris.vnpyemulator.sources;

import android.os.Build;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

final class HttpConnection implements Connection {
    
    private final HttpURLConnection connection;
   
    HttpConnection(HttpURLConnection connection) {
        this.connection = connection;
        this.connection.setConnectTimeout(10000);
        this.connection.setReadTimeout(10000);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return connection.getInputStream();
    }
    
    @Override
    public long getSize() throws IOException {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? connection.getContentLengthLong() : connection.getContentLength();
    }
    
    @Override
    public boolean isExists() throws IOException {
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }
    
    @Override
    public void close() throws IOException {
        connection.disconnect();
    }
    
}
