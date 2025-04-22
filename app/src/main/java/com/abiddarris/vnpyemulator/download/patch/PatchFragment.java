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
package com.abiddarris.vnpyemulator.download.patch;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.common.android.tasks.v2.Task;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.patches.Patch;
import com.abiddarris.vnpyemulator.patches.PatchSource;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.abiddarris.vnpyemulator.plugins.PluginSource;
import com.xwray.groupie.ExpandableGroup;

import java.util.HashMap;
import java.util.Map;

public class PatchFragment extends BaseDownloadFragment {

    private static final String FETCHED = "fetched";
    public static final String PATCHES = "patches";

    static final Map<Patch, Boolean> PATCH_EXPANDED = new HashMap<>();
    private static final Map<Patcher, PatcherState> PATCHER_STATE = new HashMap<>();
    private static final Map<PatcherState, PatcherItem> PATCHER_ITEMS = new HashMap<>();
    private boolean refresh;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ui.refreshLayout.setOnRefreshListener(() -> {
            setPatches(null);
            setFetched(false);
            setPatchesToAdapter();
            refresh = true;
        });
        setPatchesToAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PATCHER_ITEMS.clear();
    }

    public void onPatchFetched(Patch[] patches) {
        setPatches(patches);
        requireActivity().runOnUiThread(this::setPatchesToAdapter);
    }

    private void setPatchesToAdapter() {
        if (!getVariable(FETCHED, false)) {
            setFetched(true);

            ui.refreshLayout.setRefreshing(true);
            TaskInfo<Void, Patch[]> info =
                    baseDownloadViewModel.getTaskManager().execute(new Task<>() {
                        @Override
                        public void execute() throws Exception {
                            setResult(PatchSource.getPatches(refresh));
                        }
                    });

            info.addOnTaskExecuted(this::onPatchFetched);

            return;
        }
        if (getPatches() == null) {
            return;
        }

        ui.refreshLayout.setRefreshing(false);
        adapter.clear();
        for (Patch patch : getPatches()) {
            ExpandableGroup pluginGroup = new ExpandableGroup(new PatchItem(patch));
            for (Patcher patcher : patch.getPatchers()) {
                PatcherState state = PATCHER_STATE.get(patcher);
                if (state == null) {
                    state = new PatcherState(patcher);
                    PATCHER_STATE.put(patcher, state);
                }

                PatcherItem item = new PatcherItem(state, baseDownloadViewModel);
                pluginGroup.add(item);
                PATCHER_ITEMS.put(state, item);
            }

            pluginGroup.setExpanded(
                    Boolean.TRUE.equals(PATCH_EXPANDED.getOrDefault(patch, false)));

            adapter.add(pluginGroup);
        }
    }

    private void setFetched(boolean fetched) {
        saveVariable(FETCHED, fetched);
    }

    public PatcherItem getActivePatcherItem(PatcherState state) {
        return PATCHER_ITEMS.get(state);
    }

    private Patch[] getPatches() {
        return getVariable(PATCHES);
    }

    private void setPatches(Patch[] patches) {
        saveVariable(PATCHES, patches);
    }

}
