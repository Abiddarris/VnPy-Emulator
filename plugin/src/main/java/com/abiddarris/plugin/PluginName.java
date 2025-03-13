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
 ***********************************************************************************/
package com.abiddarris.plugin;

public class PluginName {

    private final String version;
    private final String pluginInternalVersion;

    public PluginName(String rawVersion) {
        int dot = rawVersion.lastIndexOf(".");

        this.version = rawVersion.substring(0, dot);
        this.pluginInternalVersion = rawVersion.substring(dot + 1);
    }

    public PluginName(String version, String pluginInternalVersion) {
        this.version = version;
        this.pluginInternalVersion = pluginInternalVersion;
    }

    public String getVersion() {
        return version;
    }

    public String getPluginInternalVersion() {
        return pluginInternalVersion;
    }
}
