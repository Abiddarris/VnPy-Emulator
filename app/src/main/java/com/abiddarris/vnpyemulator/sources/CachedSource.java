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
package com.abiddarris.vnpyemulator.sources;

import static com.abiddarris.common.files.Files.openBufferedOutput;
import static com.abiddarris.common.utils.Randoms.newRandomString;

import android.content.Context;

import com.abiddarris.common.stream.DelegateInputStream;
import com.abiddarris.common.utils.Serializes;
import com.abiddarris.vnpyemulator.files.Files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class CachedSource extends Source {

    private static CachedSource instance;

    public static Source getInstance(Context context) {
        if (instance == null) {
            instance = new CachedSource(context);
        }

        return instance;
    }

    private final Context context;
    public final Map<String, File> caches;

    private CachedSource(Context context) {
        this.context = context.getApplicationContext();
        if (!getCacheMapFile().exists()) {
            this.caches = new HashMap<>();
            return;
        }

        Map<String, File> caches;

        try (InputStream stream = new BufferedInputStream(new FileInputStream(getCacheMapFile()))){
            caches = Serializes.deserialize(stream);
        } catch (IOException | ClassNotFoundException e) {
            caches = new HashMap<>();
        }

        this.caches = caches;
    }

    @Override
    protected Connection newConnection(String fileName) throws IOException {
        return new CachedConnection(SOURCE.openConnection(fileName), fileName);
    }

    private void storeNewCache(String fileName, File dest) throws IOException {
        File oldCache = caches.put(fileName, dest);
        if (oldCache != null) {
            oldCache.delete();
        }

        try (OutputStream outputStream = openBufferedOutput(getCacheMapFile())){
            Serializes.serialize(caches, outputStream);
        }
    }

    private File getCacheMapFile() {
        return new File(Files.getCacheFolder(context), "cached_source");
    }

    private class CachedConnection implements Connection {

        private final String fileName;

        private boolean useCached;
        private Connection connection;
        private InputStream stream;

        public CachedConnection(Connection connection, String fileName) {
            this.connection = connection;
            this.fileName = fileName;
        }

        @Override
        public boolean isExists() throws IOException {
            try {
                return connection.isExists();
            } catch (IOException e) {
                switchToCachedConnection(e);
                return isExists();
            }
        }

        @Override
        public long getSize() throws IOException {
            try {
                return connection.getSize();
            } catch (IOException e) {
                switchToCachedConnection(e);
                return getSize();
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (stream != null) {
                return stream;
            }
            try {
                stream = connection.getInputStream();
                if (!useCached) {
                    stream = new CachingInputStream(stream, fileName, getSize());
                }
                return stream;
            } catch (IOException e) {
                switchToCachedConnection(e);
                return getInputStream();
            }
        }

        @Override
        public void close() throws IOException {
            if (stream != null) {
                stream.close();
            }
            connection.close();
        }

        private void switchToCachedConnection(IOException e) throws IOException {
            File file = caches.get(fileName);
            if (file == null) {
                throw e;
            }

            this.connection.close();
            this.connection = new LocalConnection(file);
            this.useCached = true;
        }

    }

    private class CachingInputStream extends DelegateInputStream {

        private final File dest;
        private final BufferedOutputStream output;
        private final String fileName;
        private final long streamSize;

        private long read;

        public CachingInputStream(InputStream stream, String fileName, long streamSize) throws IOException {
            super(stream);

            this.fileName = fileName;
            this.streamSize = streamSize;
            this.dest = new File(Files.getCacheFolder(context), newRandomString(7));
            this.output = new BufferedOutputStream(new FileOutputStream(dest));
        }

        @Override
        public int read() throws IOException {
            int b = super.read();
            if (b == -1) {
                return b;
            }

            output.write(b);
            read++;

            return b;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int actual = super.read(b, off, len);
            if (actual == -1) {
                return actual;
            }

            output.write(b, off, actual);
            read += actual;

            return actual;
        }

        @Override
        public long skip(long n) throws IOException {
            throw new IOException("not supported");
        }

        @Override
        public void close() throws IOException {
            super.close();

            output.flush();
            output.close();

            if (read == streamSize) {
                storeNewCache(fileName, dest);
            } else {
                dest.delete();
            }
        }

        @Override
        public void mark(int readlimit) {
        }

        @Override
        public void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }

        @Override
        public boolean markSupported() {
            return false;
        }

    }
}
