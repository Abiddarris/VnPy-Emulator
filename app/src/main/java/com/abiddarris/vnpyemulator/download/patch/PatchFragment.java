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

import static com.abiddarris.common.android.handlers.MainThreads.runOnMainThreadIfNot;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.patches.Patch;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.xwray.groupie.ExpandableGroup;

public class PatchFragment extends BaseDownloadFragment {

    private static final String FETCHED = "fetched";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getVariable(FETCHED, false)) {
            return;
        }
        saveVariable(FETCHED, true);
        baseDownloadViewModel.execute(new FetchPatchTask());
    }

    public void onPatchFetched(Patch[] patches) {
        runOnMainThreadIfNot(() -> {
            for (Patch patch : patches) {
                ExpandableGroup pluginGroup = new ExpandableGroup(new PatchItem(patch));
                for (Patcher patcher : patch.getPatchers()){
                    pluginGroup.add(new PatcherItem(patcher, baseDownloadViewModel));
                }

                adapter.add(pluginGroup);
            }
        });
    }
}
