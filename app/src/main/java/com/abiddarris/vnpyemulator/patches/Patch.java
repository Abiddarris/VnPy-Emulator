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

/**
 * Class to store individual patch information for individual file
 */
public class Patch {
    
    private String originalFileHash;
    private String patchFileName;
    private String fileToPatch;
    
    Patch(String originalFileHash, String patchFileName, String fileToPatch) {
        this.originalFileHash = originalFileHash;
        this.patchFileName = patchFileName;
        this.fileToPatch = fileToPatch;
    }
    
    public String getOriginalFileHash() {
        return this.originalFileHash;
    }
    
    public String getPatchFileName() {
        return this.patchFileName;
    }
        
    public String getFileToPatch() {
        return this.fileToPatch;
    }
}
