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
package com.abiddarris.vnpyemulator.patches;

import com.abiddarris.vnpyemulator.sources.Connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Class to store individual patch information for individual file
 */
public class PatchFile {
    
    private final String originalFileHash;
    private final String source;
    private final String target;

    public PatchFile(JSONObject object) throws JSONException {
        originalFileHash = object.getString("original_hash");
        source = object.getString("src");
        target = object.getString("dest");
    }

    public String getOriginalFileHash() {
        return this.originalFileHash;
    }
    
    public String getSource() {
        return this.source;
    }
        
    public String getTarget() {
        return this.target;
    }

    public Connection open() throws IOException {
        return PatchSource.openInCurrentVersion(getSource());
    }

    public void patch(File folderToPatch, boolean force) {


    }
}
