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

import static com.abiddarris.common.stream.InputStreams.readAll;
import static com.abiddarris.vnpyemulator.sources.Source.SOURCE;
import static com.abiddarris.vnpyemulator.sources.Source.VERSION;

import android.content.Context;

import com.abiddarris.vnpyemulator.sources.CachedSource;
import com.abiddarris.vnpyemulator.sources.Connection;
import com.abiddarris.vnpyemulator.sources.Source;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;

public class PluginSource {

    private static PluginGroup[] pluginGroups;

    public static Connection openInCurrentVersion(String fileName) throws IOException {
        return openInCurrentVersion(SOURCE, fileName);
    }

    private static Connection openInCurrentVersion(Source source, String fileName) throws IOException {
        fileName = "plugins/" + VERSION + "/" + fileName;
        return source.openConnection(fileName);
    }

    public static PluginGroup[] getPluginGroups(Context context) throws IOException {
        if (pluginGroups != null) {
            return pluginGroups;
        }

        try (Connection connection = openInCurrentVersion(CachedSource.getInstance(context), "plugins.json")) {
            JSONArray pluginsJSON = new JSONArray(new String(readAll(connection.getInputStream())));

            pluginGroups = new PluginGroup[pluginsJSON.length()];
            for (int i = 0; i < pluginGroups.length; i++) {
                pluginGroups[i] = new PluginGroup(pluginsJSON.getJSONObject(i));
            }

            return pluginGroups;
        } catch (JSONException e) {
            throw new IOException("Cannot fetch plugins.json", e);
        }
    }

    public static PluginGroup getPluginGroup(Context context, String renPyVersion) throws IOException {
        return Arrays.asList(getPluginGroups(context))
                .stream()
                .filter(plugin -> plugin.getVersion().equals(renPyVersion))
                .findFirst()
                .orElse(null);
    }

    public static Plugin[] getPlugins(Context context, boolean onlySupportedAbi) throws IOException {
        return Arrays.asList(getPluginGroups(context))
                .stream()
                .flatMap(pluginGroup -> Arrays.asList(pluginGroup.getPlugins(onlySupportedAbi)).stream())
                .toArray(Plugin[]::new);
    }
}
