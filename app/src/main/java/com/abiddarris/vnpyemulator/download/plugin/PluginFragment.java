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

import static com.abiddarris.common.android.pm.Packages.isAllowedToInstallPackage;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.pm.Packages;
import com.abiddarris.common.android.tasks.v2.Task;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.abiddarris.vnpyemulator.plugins.PluginSource;
import com.xwray.groupie.ExpandableGroup;

import java.util.HashMap;
import java.util.Map;

public class PluginFragment extends BaseDownloadFragment {

    private static final String PLUGIN_GROUPS = "pluginGroups";
    private static final String FETCHED = "fetched";

    private static final Map<Plugin, PluginState> PLUGIN_STATES = new HashMap<>();
    private static final Map<PluginState, PluginItem> PLUGIN_ITEMS = new HashMap<>();

    private ActivityResultLauncher<Void> requestInstallFromUnknownSource;
    private boolean refresh;

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

        ui.refreshLayout.setOnRefreshListener(() -> {
            setPluginGroups(null);
            setFetched(false);
            setPluginToAdapter();
            refresh = true;
        });
        setPluginToAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PLUGIN_ITEMS.clear();
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
            if (getVariable(FETCHED, false)) {
                return;
            }
            setFetched(true);

            ui.refreshLayout.setRefreshing(true);
            TaskInfo<Void, PluginGroup[]> info =
                    baseDownloadViewModel.getTaskManager().execute(new Task<>() {
                        @Override
                        public void execute() throws Exception {
                            setResult(PluginSource.getPluginGroups(getContext(), refresh));

                            PluginSource.getPlugins(getContext(), false);
                        }
                    });

            info.addOnTaskExecuted(this::onPluginFetched);

            return;
        }
        ui.refreshLayout.setRefreshing(false);

        adapter.clear();
        for (PluginGroup group : pluginGroups) {
            ExpandableGroup pluginGroup = new ExpandableGroup(new PluginGroupItem(group));
            for (Plugin plugin : group.getPlugins()) {
                PluginState state = PLUGIN_STATES.get(plugin);
                if (state == null) {
                    state = new PluginState(plugin);
                    PLUGIN_STATES.put(plugin, state);
                }

                PluginItem item = new PluginItem(state, baseDownloadViewModel);
                pluginGroup.add(item);
                PLUGIN_ITEMS.put(state, item);
            }

            adapter.add(pluginGroup);
        }
    }

    private void setFetched(boolean s) {
        saveVariable(FETCHED, s);
    }

    public PluginItem getActivePluginItem(PluginState pluginState) {
        return PLUGIN_ITEMS.get(pluginState);
    }

    public PluginState getPluginState(Plugin plugin) {
        return PLUGIN_STATES.get(plugin);
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
