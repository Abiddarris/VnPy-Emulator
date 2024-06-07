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
package com.abiddarris.vnpyemulator.patches;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Stream;

/**
 * Provide an {@code InputStream} from github repo
 *
 * @Author Abiddarris
 */
public class GithubSource implements Source {
    
    /**
     * Hardcoded URL
     */
    private static final Uri PARENT = Uri.parse("https://raw.githubusercontent.com/Abiddarris/VnPy-Emulator/0.1.0");
    
    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream open(String fileName) throws IOException {
        return new URL(PARENT.buildUpon()
            .appendPath(fileName)
            .build()
            .toString())
            .openStream();
    }
    
}
