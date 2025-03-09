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

import static com.abiddarris.common.stream.InputStreams.readAll;
import static com.abiddarris.vnpyemulator.sources.Source.SOURCE;
import static com.abiddarris.vnpyemulator.sources.Source.VERSION;

import android.content.Context;

import com.abiddarris.vnpyemulator.sources.CachedSource;
import com.abiddarris.vnpyemulator.sources.Connection;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class that provides patches
 */
public class PatchSource {
    
    /**
     * Hold {@code PatchSource} singleton
     */
    private static PatchSource patchSource;

    private static Context context;

    /**
     * Hold fetched patchers
     */
    private static Patch[] patches;

    public static void setContext(Context context) {
        PatchSource.context = context.getApplicationContext();
    }

    /**
     * Returns versions that have a patch
     *
     * @throws IOException if unable to fetch versions
     * @return Versions that have a patch
     */
    public static String[] getVersions() throws IOException {
        if(patches == null) {
            fetch();
        }
        return Stream.of(patches)
            .map(Patch::getRenPyVersion)
            .toArray(String[]::new);
    }
    
    /**
     * Returns {@code Patcher} from given version
     *
     * @param version Patcher's version
     * @throws IOException if unable to fetch the patcher
     * @return {@code Patcher} from given version
     */
    public static Patch getPatch(String version) throws IOException {
        if(patches == null) {
            fetch();
        }
        
        return Stream.of(patches)
            .filter(patch -> patch.getRenPyVersion().equals(version))
            .findFirst()
            .orElse(null);
    }

    /**
     * Open an {@code InputStream} relative from folder containing
     * patches from specified file name
     *
     * @param fileName File path relative from folder containing
     *                 patches from specified file name
     * @throws IOException If unable to open
     * @return {@code Connection}
     */
    public static Connection openInCurrentVersion(String fileName) throws IOException {
        return openInCurrentVersion(fileName, false);
    }

    /**
     * Open an {@code InputStream} relative from folder containing
     * patches from specified file name
     *
     * @param fileName            File path relative from folder containing
     *                            patches from specified file name
     * @param accessibleOnOffline Download the file so later can be accessed without internet connection
     * @return {@code Connection}
     * @throws IOException If unable to open
     */
    public static Connection openInCurrentVersion(String fileName, boolean accessibleOnOffline) throws IOException {
        fileName = "patches/" + VERSION + "/" + fileName;
        if (accessibleOnOffline) {
            return CachedSource.getInstance(context).openConnection(fileName);
        }
        return SOURCE.openConnection(fileName);
    }

    public static Patch[] getPatches() throws IOException {
        if (patches == null) {
            fetch();
        }
        return patches;
    }

    /**
     * Function that fetched patches and store it in 
     * {@code patchers} field
     */
    private static void fetch() throws IOException {
        try (Connection connection = openInCurrentVersion("patches.json", true)) {
            JSONArray patches = new JSONArray(new String(readAll(connection.getInputStream())));
            List<Patch> patchesList = new ArrayList<>();

            for (int i = 0; i < patches.length(); i++) {
                patchesList.add(new Patch(patches.getJSONObject(i)));
            }

            PatchSource.patches = patchesList.toArray(new Patch[0]);
        } catch (JSONException e) {
            throw new IOException("Unable to fetch patches", e);
        }
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
