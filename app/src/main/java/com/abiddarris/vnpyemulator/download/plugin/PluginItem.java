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

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment.BaseDownloadViewModel;
import com.abiddarris.vnpyemulator.download.base.BasePluginItem;
import com.abiddarris.vnpyemulator.plugins.Plugin;

public class PluginItem extends BasePluginItem {

    private final Plugin plugin;

    public PluginItem(Plugin plugin, BaseDownloadViewModel pluginViewModel) {
        super(pluginViewModel);
        this.plugin = plugin;
    }

    @Override
    public void bind(@NonNull LayoutPluginBinding viewBinding, int position) {
        viewBinding.version.setText(String.format("%s (%s)", plugin.getVersion(), plugin.getAbi()));
        viewBinding.download.setOnClickListener(v -> pluginViewModel.getFragment().getDownloadService().downloadPlugin(plugin));

    }
}
