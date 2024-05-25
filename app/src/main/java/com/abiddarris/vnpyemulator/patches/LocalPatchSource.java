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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * {@link PatchSource} implementation that searches patches in local storage
 */
public class LocalPatchSource implements PatchSource {
    
    /**
     * Hardcoded path
     */
    private static final File PATCH_FOLDER = new File("/storage/emulated/0/Android/media/com.abiddarris.vnpyemulator/patches");
    
    /**
     * Hold fetched patchers
     */
    private Patcher[] patchers;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getVersions() throws IOException {
        if(patchers == null) {
            fetch();
        }
        return Stream.of(patchers)
            .map(Patcher::getVersion)
            .toArray(String[]::new);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Patcher getPatcher(String version) {
        return null;
    }
    
    /**
     * Function that fetched patches and store it in 
     * {@code patchers} field
     */
    private void fetch() throws IOException {
        BufferedReader reader = new BufferedReader(
            new FileReader(new File(PATCH_FOLDER, "version")));
        
        patchers = reader.lines()
            .map(Patcher::new)
            .toArray(Patcher[]::new);
    }
}
