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

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that provides file for patching and downloading
 * python for running renpy games
 */
public interface Source {
    
    /**
     * For testing purpose, {@link #getSource()} will provide
     * stream on internal storage.
     */
    static final boolean LOCAL = false;
    
    /**
     * Store singleton of source
     */
    static Source source = LOCAL ? new LocalSource() : new GithubSource();
    
    /**
     * Open an {@code InputStream} relative from folder containing 
     * patches folder and python
     *
     * @param fileName File path relative from folder containing 
     *        patches folder and python
     * @throws IOException If unable to open
     * @return {@code InputStream}
     */
    InputStream open(String fileName) throws IOException;
    
    public static Source getSource() {
        return source;
    }
}
