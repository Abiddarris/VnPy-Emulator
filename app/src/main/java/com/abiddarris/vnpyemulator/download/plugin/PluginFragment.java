/***********************************************************************************
 * Copyright (C) 2024 Abiddarris
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
package com.abiddarris.vnpyemulator.download.plugin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.xwray.groupie.ExpandableGroup;

public class PluginFragment extends BaseDownloadFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseDownloadViewModel.execute(new FetchPluginTask());
    }

    public void onPluginFetched(PluginGroup[] pluginGroups) {
        requireActivity().runOnUiThread(() -> {
            for (PluginGroup group : pluginGroups) {
                ExpandableGroup pluginGroup = new ExpandableGroup(new PluginGroupItem(group));
                for (Plugin plugin : group.getPlugins()){
                    pluginGroup.add(new PluginItem(plugin, baseDownloadViewModel));
                }

                adapter.add(pluginGroup);
            }
        });
    }
}
