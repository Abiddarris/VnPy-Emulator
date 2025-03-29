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
package com.abiddarris.vnpyemulator.patches;

import static com.abiddarris.common.files.Files.delete;
import static com.abiddarris.common.files.Files.getPathName;
import static com.abiddarris.common.files.Files.makeDirectories;
import static com.abiddarris.common.files.Files.openBufferedOutput;
import static com.abiddarris.common.stream.InputStreams.writeAllTo;
import static com.abiddarris.vnpyemulator.files.Files.getPatchFolder;

import android.content.Context;

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.download.ProgressPublisher;
import com.abiddarris.vnpyemulator.sources.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Patcher is a group of patches
 */
public class Patcher {

    private final Patch patch;

    /**
     * Target Ren'Py version
     */
    private final String version;

    /**
     * Store patch objects
     */
    private final PatchFile[] patchFiles;

    /**
     * Create patcher from specified string
     */
    Patcher(Patch patch, JSONObject object) throws JSONException {
        this.patch = patch;
        version = object.getString("version");

        JSONArray contents = object.getJSONArray("contents");

        this.patchFiles = new PatchFile[contents.length()];
        for (int i = 0; i < contents.length(); i++) {
            this.patchFiles[i] = new PatchFile(contents.getJSONObject(i));
        }
    }
    
    /**
     * Returns array of patches 
     *
     * @return Array of patches 
     */
    public PatchFile[] getPatches() {
        return patchFiles;
    }
    
    /**
     * Returns target {@code Patcher} version
     *
     * @return Target {@code Patcher} version
     */
    public String getVersion() {
        return version;
    }

    public Patch getPatch() {
        return patch;
    }

    @NonNull
    @Override
    public String toString() {
        return getPatch().getRenPyVersion() + "." + getVersion();
    }


}
