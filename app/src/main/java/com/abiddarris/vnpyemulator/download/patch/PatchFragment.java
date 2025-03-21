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

import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.download.plugin.PluginState;
import com.abiddarris.vnpyemulator.patches.Patch;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.xwray.groupie.ExpandableGroup;

import java.util.HashMap;
import java.util.Map;

public class PatchFragment extends BaseDownloadFragment {

    private static final String FETCHED = "fetched";
    public static final String PATCHES = "patches";
    public static final String PATCH_STATES = "patchStates";

    private Map<PatcherState, PatcherItem> patchItems = new HashMap<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPatchesToAdapter();
    }

    public void onPatchFetched(Patch[] patches) {
        setPatches(patches);
        requireActivity().runOnUiThread(this::setPatchesToAdapter);
    }

    private void setPatchesToAdapter() {
        if (!getVariable(FETCHED, false)) {
            saveVariable(FETCHED, true);
            baseDownloadViewModel.execute(new FetchPatchTask());
            return;
        }

        if (getPatches() == null) {
            return;
        }

        Map<Patcher, PatcherState> patchStates = getPatcherStates();
        for (Patch patch : getPatches()) {
            ExpandableGroup pluginGroup = new ExpandableGroup(new PatchItem(patch));
            for (Patcher patcher : patch.getPatchers()) {
                PatcherState state = patchStates.get(patcher);
                if (state == null) {
                    state = new PatcherState(patcher);
                    patchStates.put(patcher, state);
                }

                PatcherItem item = new PatcherItem(state, baseDownloadViewModel);
                pluginGroup.add(item);
                patchItems.put(state, item);
            }

            adapter.add(pluginGroup);
        }
    }

    public PatcherItem getActivePatcherItem(PatcherState state) {
        return patchItems.get(state);
    }

    private Map<Patcher, PatcherState> getPatcherStates() {
        Map<Patcher, PatcherState> patchStates = getVariable(PATCH_STATES);
        if (patchStates == null) {
            patchStates = new HashMap<>();
            saveVariable(PATCH_STATES, patchStates);
        }

        return patchStates;
    }

    private Patch[] getPatches() {
        return getVariable(PATCHES);
    }

    private void setPatches(Patch[] patches) {
        saveVariable(PATCHES, patches);
    }

}
