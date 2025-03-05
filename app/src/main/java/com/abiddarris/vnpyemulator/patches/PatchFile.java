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

import android.util.Log;

import com.abiddarris.common.stream.NullOutputStream;
import com.abiddarris.common.utils.Hash;
import com.abiddarris.vnpyemulator.sources.Connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class to store individual patch information for individual file
 */
public class PatchFile {
    
    private final String originalFileHash;
    private final String patchFileName;
    private final String fileToPatch;
    
    PatchFile(String originalFileHash, String patchFileName, String fileToPatch) {
        this.originalFileHash = originalFileHash;
        this.patchFileName = patchFileName;
        this.fileToPatch = fileToPatch;
    }

    public PatchFile(JSONObject object) throws JSONException {
        originalFileHash = object.getString("original_hash");
        patchFileName = object.getString("src");
        fileToPatch = object.getString("dest");
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

    public Connection open() throws IOException {
        return PatchSource.openInCurrentVersion(getPatchFileName());
    }

    public void patch(File folderToPatch, boolean force) {
        File target = new File(folderToPatch, getFileToPatch());
        if(!target.exists()) {
            throw new PatchException("Unable to patch non exist file: " + target.getPath());
        }

        try (Connection connection = PatchSource.getPatcher()
                .openInCurrentVersion(getPatchFileName())) {
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            String patchHash = Hash.createHashingFrom(inputStream, outputStream);
            byte[] patchContent = outputStream.toByteArray();

            outputStream.close();
            inputStream.close();
            inputStream = new BufferedInputStream(new FileInputStream(target));

            String originalFileHash = Hash.createHashingFrom(inputStream, new NullOutputStream());

            inputStream.close();
            if(originalFileHash.equals(patchHash)) {
                return;
            }

            if(!originalFileHash.equals(getOriginalFileHash()) && !force) {
                throw new IncompatiblePatchException();
            }

            var os = new BufferedOutputStream(new FileOutputStream(target));
            os.write(patchContent);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new PatchException(String.format("Cannot patch %s", getPatchFileName()), e);
        }

    }
}
