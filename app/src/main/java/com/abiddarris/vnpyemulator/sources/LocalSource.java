/***********************************************************************************
 * Copyright (C) 2024 - 2025 Abiddarris
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

import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Source implementation that provides file from local host
 */
public class LocalSource extends URLSource {
    
    private static final int PORT = 5788;
    private static final String HOST = "192.168.17.50";
    private static final Uri SOURCE = Uri.parse("http://" + HOST + ":" + PORT);

    LocalSource() {
        super(SOURCE);
    }
    
}
