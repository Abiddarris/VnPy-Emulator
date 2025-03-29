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

import static com.abiddarris.common.files.Files.delete;
import static com.abiddarris.common.files.Files.getPathName;
import static com.abiddarris.common.files.Files.makeDirectories;
import static com.abiddarris.common.files.Files.openBufferedOutput;
import static com.abiddarris.common.stream.InputStreams.readAll;
import static com.abiddarris.common.stream.InputStreams.writeAllTo;
import static com.abiddarris.vnpyemulator.files.Files.getPatchFolder;
import static com.abiddarris.vnpyemulator.sources.Source.SOURCE;
import static com.abiddarris.vnpyemulator.sources.Source.VERSION;

import android.content.Context;

import com.abiddarris.common.stream.Canceler;
import com.abiddarris.common.stream.NullOutputStream;
import com.abiddarris.common.utils.Hash;
import com.abiddarris.vnpyemulator.download.ProgressPublisher;
import com.abiddarris.vnpyemulator.sources.CachedSource;
import com.abiddarris.vnpyemulator.sources.Connection;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class that provides patches
 */
public class PatchSource {

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

    public static Patcher getPatcher(String fullVersion) throws IOException {
        int lastDot = fullVersion.lastIndexOf(".");
        String patchVersion = fullVersion.substring(0, lastDot);
        String patcherVersion = fullVersion.substring(lastDot + 1);

        Patch patch = getPatch(patchVersion);
        if (patch == null) {
            return null;
        }

        for (Patcher patcher : patch.getPatchers()) {
            if (patcher.getVersion().equals(patcherVersion)) {
                return patcher;
            }
        }

        return null;
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

    public static void download(Patcher patcher, ProgressPublisher progressPublisher) throws IOException {
        download(patcher, progressPublisher, new Canceler());
    }

    public static boolean download(Patcher patcher, ProgressPublisher progressPublisher, Canceler canceler) throws IOException {
        File dest = getPatcherFolder(patcher);
        makeDirectories(dest);

        PatchFile[] patchFiles = patcher.getPatches();
        progressPublisher.setMaxProgress(patchFiles.length);
        for (PatchFile patchFile : patchFiles) {
            if (canceler.isCancel()) {
                delete(dest);
                return false;
            }

            try (Connection connection = patchFile.open();
                 OutputStream output = openBufferedOutput(new File(dest, getPathName(patchFile.getSource())))) {
                InputStream input = new BufferedInputStream(connection.getInputStream());
                if (!writeAllTo(input, output, canceler)) {
                    delete(dest);
                    return false;
                }
            } catch (IOException e) {
                delete(dest);
                throw e;
            }
            progressPublisher.incrementProgress(1);
        }
        return true;
    }

    public static void uninstall(Patcher patcher) throws IOException {
        File dest = getPatcherFolder(patcher);
        delete(dest);
    }

    public static boolean apply(Patcher patcher, String gamePath, IncompatiblePatchCallback callback) throws IOException {
        for(PatchFile patchFile : patcher.getPatches()) {
            File target = new File(gamePath, patchFile.getTarget());
            if(!target.exists()) {
                throw new PatchException("Unable to patch non exist file: " + target.getPath());
            }

            File src = new File(getPatcherFolder(patcher), getPathName(patchFile.getSource()));
            if (!src.exists()) {
                throw new PatchException(String.format("Broken patch file (Missing %s)", patchFile.getSource()));
            }

            InputStream inputStream = new BufferedInputStream(new FileInputStream(src));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            String sourceHash = Hash.createHashingFrom(inputStream, outputStream);
            byte[] patchContent = outputStream.toByteArray();

            outputStream.close();
            inputStream.close();
            inputStream = new BufferedInputStream(new FileInputStream(target));

            String targetHash = Hash.createHashingFrom(inputStream, new NullOutputStream());

            inputStream.close();
            if(targetHash.equals(sourceHash)) {
                continue;
            }

            if(!targetHash.equals(patchFile.getOriginalFileHash())) {
                switch (callback.onConflict(target)) {
                    case ABORT:
                        return false;
                    case SKIP:
                        continue;
                }
            }

            var os = new BufferedOutputStream(new FileOutputStream(target));
            os.write(patchContent);
            os.flush();
            os.close();
        }
        return true;
    }

    public static boolean isInstalled(Patcher patcher) {
        return getPatcherFolder(patcher).exists();
    }

    private static File getPatcherFolder(Patcher patcher) {
        File patch = new File(getPatchFolder(context), patcher.getPatch().getName());
        return new File(patch, patcher.getVersion());
    }
}
