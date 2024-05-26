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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

/**
 * Patcher is a group of patches
 */
public class Patcher {
    
    /**
     * Target Ren'Py version
     */
    private String version;
    
    /**
     * Store folder that contains patches
     */
    private String patchFolderName;
    
    /**
     * Store patch objects
     */
    private Patch[] patches;
    
    /**
     * Store associated {@code PatchSource}
     */
    private PatchSource source;
    
    /**
     * Create patcher from specified string
     */
    Patcher(PatchSource source, String patchString) {
        this.source = source;
        
        String[] components = patchString.split("//");
        
        version = components[0];
        patchFolderName = components[1];
        
        try(var reader = new BufferedReader(
                new InputStreamReader(open("patches")))){
            patches = reader.lines()
                .map(line -> {
                    String[] patchComponents = line.split("//");
                    if(patchComponents.length != 3) {
                        throw new ParseException("Unexpected components when parsing (expected 3 but " + 
                        patchComponents.length +")");
                    }
                    return new Patch(patchComponents[0], patchComponents[1], patchComponents[2]);
                }).toArray(Patch[]::new);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Open an {@code InputStream} relative from patch folder from specified file name
     *
     * @param fileName File path relative from patch folder
     * @throws IOException If unable to open
     * @return {@code InputStream}
     */
    public InputStream open(String fileName) throws IOException {
        return source.open(patchFolderName + File.separator + fileName);
    }
    
    /**
     * Returns array of patches 
     *
     * @return Array of patches 
     */
    public Patch[] getPatches() {
        return patches;
    }
    
    /**
     * Returns target Ren'Py version
     *
     * @return Target Ren'Py version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Returns folder that contains patches
     *
     * @return Folder that contains patches
     */
    public String getPatchFolderName() {
    	return patchFolderName;
    }
}
