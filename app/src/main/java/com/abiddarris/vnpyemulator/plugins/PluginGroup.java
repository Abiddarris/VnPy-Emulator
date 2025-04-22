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

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PluginGroup {

    private final String name;
    private final String version;
    private final Plugin[] plugins;

    PluginGroup(JSONObject object) throws JSONException {
        name = object.getString("name");
        version = object.getString("version");

        JSONArray pluginsJSON = object.getJSONArray("downloads");

        plugins = new Plugin[pluginsJSON.length()];
        for (int i = 0; i < pluginsJSON.length(); i++) {
            plugins[i] = new Plugin(this, pluginsJSON.getJSONObject(i));
        }
    }

    public Plugin[] getPlugins() {
        return getPlugins(false);
    }

    public Plugin[] getPlugins(boolean onlySupportedABIs) {
        if (!onlySupportedABIs) {
            return plugins;
        }

        List<String> supportedABIs = Arrays.asList(Build.SUPPORTED_ABIS);
        return Arrays.asList(plugins)
                .stream()
                .filter(plugin -> supportedABIs.contains(plugin.getAbi()))
                .toArray(Plugin[]::new);
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginGroup that = (PluginGroup) o;
        return Objects.equals(name, that.name) && Objects.equals(version, that.version) && Objects.deepEquals(plugins, that.plugins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, Arrays.hashCode(plugins));
    }
}
