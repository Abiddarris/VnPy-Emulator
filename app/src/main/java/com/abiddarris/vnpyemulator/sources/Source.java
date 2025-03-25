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
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.sources;

import com.abiddarris.vnpyemulator.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides file for patching and downloading
 * python for running renpy games
 */
public abstract class Source {

    public static final String VERSION = "0.3.0";

    /**
     * For testing purpose, {@link #getSource()} will provide
     * stream on local network.
     */
    private static final boolean LOCAL = BuildConfig.DEBUG;
    
    /**
     * Store singleton of source
     */
    public static final Source SOURCE = LOCAL ? new LocalSource() : new GithubSource();
    
    /**
     * Open an {@code InputStream} relative from folder containing 
     * patches folder and python
     *
     * @param fileName File path relative from folder containing 
     *        patches folder and python
     * @throws IOException If unable to open
     * @return {@code InputStream}
     */
    @Deprecated
    public InputStream open(String fileName) throws IOException {
        return openConnection(fileName)
            .getInputStream();
    }

    public final Connection openConnection(String fileName) throws IOException {
        if(fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        if(fileName.endsWith("/")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }

        String[] parts = fileName.split("/");
        List<String> newParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.equals("..")) {
                newParts.add(part);
                continue;
            }

            if (newParts.isEmpty()) {
                continue;
            }

            newParts.remove(newParts.size() - 1);
        }

        return newConnection(newParts.stream()
                .reduce((part, part2) -> part + "/" + part2)
                .orElse(""));
    }

    protected abstract Connection newConnection(String fileName) throws IOException;

    public static Source getSource() {
        return SOURCE;
    }
}
