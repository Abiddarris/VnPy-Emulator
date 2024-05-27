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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * Class that provides patches
 */
public class PatchSource {
    
    /**
     * Hold {@code PatchSource} singleton
     */
    private static PatchSource patchSource;
    
    /**
     * Hold fetched patchers
     */
    private Patcher[] patchers;
    
    /**
     * Returns versions that have a patch
     *
     * @throws IOException if unable to fetch versions
     * @return Versions that have a patch
     */
    public String[] getVersions() throws IOException {
        if(patchers == null) {
            fetch();
        }
        return Stream.of(patchers)
            .map(Patcher::getVersion)
            .toArray(String[]::new);
    }
    
    /**
     * Returns {@code Patcher} from given version
     *
     * @param version Patcher's version
     * @throws IOException if unable to fetch the patcher
     * @return {@code Patcher} from given version
     */
    public Patcher getPatcher(String version) throws IOException {
        if(patchers == null) {
            fetch();
        }
        
        return Stream.of(patchers)
            .filter(patcher -> patcher.getVersion().equals(version))
            .findFirst()
            .get();
    }
    
    /**
     * Open an {@code InputStream} relative from folder containing 
     * patches from specified file name
     *
     * @param fileName File path relative from folder containing 
     *                 patches from specified file name
     * @throws IOException If unable to open
     * @return {@code InputStream}
     */
    public InputStream open(String fileName) throws IOException {
        return Source.getSource()
            .open("patches/" + fileName);
    }
    
    /**
     * Function that fetched patches and store it in 
     * {@code patchers} field
     */
    private void fetch() throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(open("version")));
        
        patchers = reader.lines()
            .map(line -> new Patcher(this, line))
            .toArray(Patcher[]::new);
    }
    
    /**
     * Returns {@code PatchSource} that provides {@code Patcher}
     *
     * @return {@code PatchSource} that provides {@code Patcher}
     */
    public static PatchSource getPatcher() {
    	if(patchSource == null) {
            return new PatchSource();
        }
        return patchSource;
    }
}
