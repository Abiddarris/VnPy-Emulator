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

import android.view.View;

import androidx.annotation.NonNull;

import com.abiddarris.common.android.tasks.v2.DeterminateProgress;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment.BaseDownloadViewModel;
import com.abiddarris.vnpyemulator.download.base.BaseItem;
import com.abiddarris.vnpyemulator.patches.Patcher;

public class PatcherItem extends BaseItem {

    private final Patcher patcher;
    private final PatcherState state;

    public PatcherItem(PatcherState state, BaseDownloadViewModel baseDownloadViewModel) {
        super(baseDownloadViewModel);

        this.patcher = state.getPatcher();
        this.state = state;
    }

    @Override
    public void bind(@NonNull LayoutPluginBinding viewBinding, int position) {
        BaseDownloadFragment fragment = baseDownloadViewModel.getFragment();

        viewBinding.version.setText(patcher.getVersion());

        if (patcher.isInstalled(fragment.getContext()) || state.isDownloading()) {
            viewBinding.download.setVisibility(View.INVISIBLE);
            viewBinding.download.setOnClickListener(null);
        } else {
            viewBinding.download.setVisibility(View.VISIBLE);
            viewBinding.download.setOnClickListener(v -> {
                TaskInfo<DeterminateProgress, Boolean> taskInfo =
                        fragment.getDownloadService().download(new DownloadPatchTask(patcher));
                taskInfo.addOnTaskExecuted(this::onTaskExecuted);

                this.notifyChanged();
            });
        }
    }

    private void onTaskExecuted(Boolean success) {
        state.setDownloading(false);

        PatchFragment fragment = baseDownloadViewModel.getFragment();
        PatcherItem item = fragment.getActivePatcherItem(state);
        if (item != null) {
            runOnMainThreadIfNot(item::notifyChanged);
        }
    }
}
