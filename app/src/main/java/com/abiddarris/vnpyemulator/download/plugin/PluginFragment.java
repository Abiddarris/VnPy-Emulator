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

import static com.abiddarris.common.android.pm.Packages.isAllowedToInstallPackage;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.pm.Packages;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.xwray.groupie.ExpandableGroup;

import java.util.HashMap;
import java.util.Map;

public class PluginFragment extends BaseDownloadFragment {

    public static final String PLUGIN_GROUPS = "pluginGroups";
    public static final String PLUGIN_STATE = "pluginState";
    public static final String PLUGIN_ITEMS = "pluginItems";
    private ActivityResultLauncher<Void> requestInstallFromUnknownSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestInstallFromUnknownSource = registerForActivityResult(
                    new Packages.RequestInstallPackagePermission(),
                    this::requestInstallFromUnknownSourceCallback
            );
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPluginToAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPluginItems().clear();
    }

    private void requestInstallFromUnknownSourceCallback(Boolean success) {
        checkInstallFromUnknownSourcePermission();
    }

    public void onPluginFetched(PluginGroup[] pluginGroups) {
        setPluginGroups(pluginGroups);
        requireActivity().runOnUiThread(this::setPluginToAdapter);

        checkInstallFromUnknownSourcePermission();
    }

    private void setPluginToAdapter() {
        PluginGroup[] pluginGroups = getPluginGroups();
        if (pluginGroups == null) {
            baseDownloadViewModel.execute(new FetchPluginTask());
            return;
        }

        Map<Plugin, PluginState> pluginStates = getPluginStates();
        Map<PluginState, PluginItem> pluginItems = getPluginItems();
        for (PluginGroup group : pluginGroups) {
            ExpandableGroup pluginGroup = new ExpandableGroup(new PluginGroupItem(group));
            for (Plugin plugin : group.getPlugins()) {
                PluginState state = pluginStates.get(plugin);
                if (state == null) {
                    state = new PluginState(plugin);
                    pluginStates.put(plugin, state);
                }

                PluginItem item = new PluginItem(state, baseDownloadViewModel);
                pluginGroup.add(item);
                pluginItems.put(state, item);
            }

            adapter.add(pluginGroup);
        }
    }

    public PluginItem getActivePluginItem(PluginState pluginState) {
        return getPluginItems().get(pluginState);
    }

    private Map<PluginState, PluginItem> getPluginItems() {
        Map<PluginState, PluginItem> pluginItems = getVariable(PLUGIN_ITEMS);
        if (pluginItems == null) {
            pluginItems = new HashMap<>();
            saveVariable(PLUGIN_ITEMS, pluginItems);
        }

        return pluginItems;
    }

    private Map<Plugin, PluginState> getPluginStates() {
        Map<Plugin, PluginState> pluginStates = getVariable(PLUGIN_STATE);
        if (pluginStates == null) {
            pluginStates = new HashMap<>();
            saveVariable(PLUGIN_STATE, pluginStates);
        }

        return pluginStates;
    }

    private void setPluginGroups(PluginGroup[] groups) {
        saveVariable(PLUGIN_GROUPS, groups);
    }

    private PluginGroup[] getPluginGroups() {
        return getVariable(PLUGIN_GROUPS);
    }

    private void checkInstallFromUnknownSourcePermission() {
        if (isAllowedToInstallPackage(requireContext())) {
            return;
        }

        SimpleDialog dialog = SimpleDialog.newSimpleDialog(
                getString(R.string.permission_required),
                getString(R.string.request_install_permission_message)
        );
        dialog.setCancelable(false);
        dialog.showForResult(getChildFragmentManager(),
                ignored -> requestInstallFromUnknownSource.launch(null));
    }
}
