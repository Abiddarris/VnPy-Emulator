/***********************************************************************************
 * Copyright (C) 2024-2025 Abiddarris
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
package com.abiddarris.vnpyemulator.plugins;

import static com.abiddarris.vnpyemulator.files.Files.getPlugin;
import static com.abiddarris.vnpyemulator.renpy.RenPyPrivate.hasPrivateFiles;

import android.content.Context;

import com.abiddarris.vnpyemulator.download.ProgressPublisher;
import com.abiddarris.vnpyemulator.files.Files;
import com.abiddarris.vnpyemulator.renpy.RenPyPrivate;
import com.abiddarris.vnpyemulator.sources.Connection;

import org.json.JSONException;
import org.json.JSONObject;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Plugin {

    private final String privateFiles;
    private final String abi;
    private final String file;
    private final String version;

    public Plugin(JSONObject object) throws JSONException {
        abi = object.getString("abi");
        version = object.getString("version");
        file = object.getString("file");
        privateFiles = object.getString("private-files");
    }

    public String getAbi() {
        return abi;
    }

    public String getFile() {
        return file;
    }

    public String getPrivateFiles() {
        return privateFiles;
    }

    public String getVersion() {
        return version;
    }

    public boolean isPrivateFilesDownloaded(Context context) {
        return hasPrivateFiles(context, getPrivateFiles());
    }

    public void downloadPlugin(Context context, ProgressPublisher progressPublisher) throws IOException {
        try(Connection connection = PluginSource.openInCurrentVersion(getFile());
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(getPlugin(context, getFile())))) {
            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
            long size = connection.getSize();
            progressPublisher.setMaxProgress(size >= Integer.MAX_VALUE ? Integer.MIN_VALUE : (int)size);

            download(progressPublisher, input, output);
        }
    }

    public void downloadPrivateFiles(Context context, ProgressPublisher progressPublisher) throws IOException {
        if (isPrivateFilesDownloaded(context)) {
            return;
        }
        File cache = new File(Files.getCacheFolder(context), getPrivateFiles());
        cache.deleteOnExit();

        try(Connection connection = PluginSource.openInCurrentVersion(getPrivateFiles());
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(cache))) {
            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
            long size = connection.getSize();
            progressPublisher.setMaxProgress(size >= Integer.MAX_VALUE ? Integer.MIN_VALUE : (int)size);

            download(progressPublisher, input, output);
            unpackPrivateFiles(cache, RenPyPrivate.getPrivateFiles(context, getPrivateFiles()));
        } finally {
             cache.delete();
        }
    }

    private void unpackPrivateFiles(File cache, File dest) throws IOException {
        dest.mkdirs();

        var is = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(cache))));
        TarEntry entry;
        while((entry = is.getNextEntry()) != null) {
            var destination = new File(dest, entry.getName());
            if(entry.isDirectory()) {
                destination.mkdirs();
                continue;
            }
            var os = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] buf = new byte[8192];
            int len;
            while((len = is.read(buf)) != -1) {
                os.write(buf,0,len);
            }
            os.flush();
            os.close();
        }
        is.close();
    }

    private static void download(ProgressPublisher progressPublisher, BufferedInputStream input, BufferedOutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 16];
        int len;
        while ((len = input.read(buffer)) != -1) {
            progressPublisher.incrementProgress(len);
            output.write(buffer, 0, len);
        }
        output.flush();
    }
}
