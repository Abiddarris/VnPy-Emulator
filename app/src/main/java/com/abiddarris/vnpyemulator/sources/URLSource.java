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
import android.net.Uri.Builder;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

/**
 * Provide an {@code InputStream} from github repo
 *
 * @Author Abiddarris
 */
public abstract class URLSource extends Source {

    private final Uri base;

    URLSource(Uri base) {
        this.base = base;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection newConnection(String fileName) throws IOException {
        String[] parts = fileName.split("/");
        
        Builder builder = base.buildUpon();
        Stream.of(parts)
            .forEach(builder::appendPath);
        Uri uri = builder.build();
        
        return new HttpConnection((HttpURLConnection)
            new URL(uri.toString()).openConnection());
    }
    
}
