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
package com.abiddarris.vnpyemulator.download.plugin;

import com.abiddarris.vnpyemulator.plugins.Plugin;

public class PluginState {

    private final Plugin plugin;
    private boolean isDownloading;
    private boolean installing;
    private boolean hideDownloadButton;

    public PluginState(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setInstalling(boolean installing) {
        this.installing = installing;
    }

    public boolean isInstalling() {
        return installing;
    }

    public boolean isHideDownloadButton() {
        return hideDownloadButton;
    }

    public void setHideDownloadButton(boolean hide) {
        this.hideDownloadButton = hide;
    }
}
