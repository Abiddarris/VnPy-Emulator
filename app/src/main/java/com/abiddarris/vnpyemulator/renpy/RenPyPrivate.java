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
package com.abiddarris.vnpyemulator.renpy;

import android.content.Context;
import com.abiddarris.vnpyemulator.files.Files;
import java.io.File;

public class RenPyPrivate {
    
    public static boolean hasPrivateFiles(Context context, String renPyVersion) {
        return getPrivateFiles(context, renPyVersion).isDirectory();
    }
    
    public static File getPrivateFiles(Context context, String renPyVersion) {
        return new File(Files.getRenPyPrivateFolder(context), renPyVersion);
    }
}
