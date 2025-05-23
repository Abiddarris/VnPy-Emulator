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

import static com.abiddarris.vnpyemulator.download.plugin.PluginFragment.PLUGIN_GROUP_EXPANDED;

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.databinding.LayoutPluginGroupBinding;
import com.abiddarris.vnpyemulator.download.base.BaseGroupItem;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;

public class PluginGroupItem extends BaseGroupItem {

    protected PluginGroup group;

    public PluginGroupItem(PluginGroup group) {
        this.group = group;
    }

    @Override
    public void bind(@NonNull LayoutPluginGroupBinding binding, int position) {
        super.bind(binding, position);

        binding.name.setText(group.getName());
        binding.version.setText(group.getVersion());
    }

    @Override
    protected void toggleExpand() {
        super.toggleExpand();

        PLUGIN_GROUP_EXPANDED.put(group, isExpanded());
    }
}
