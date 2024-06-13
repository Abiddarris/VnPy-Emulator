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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

final class LocalConnection implements Connection {
    
    private File file;
    private FileInputStream stream;
    
    LocalConnection(File file) {
        this.file = file;
    }
    
    @Override
    public long getSize() throws IOException {
        return file.length();
    }
    
    @Override
    public boolean isExists() throws IOException {
        return file.exists();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if(stream == null) {
            stream = new FileInputStream(file);
        }
        return stream;
    }
    
    @Override
    public void close() throws IOException {
        if(stream != null) {
            stream.close();
        }
    }
    
}
