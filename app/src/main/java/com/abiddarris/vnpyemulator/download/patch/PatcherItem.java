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

import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.android.dialogs.SimpleConfirmationDialog;
import com.abiddarris.common.android.tasks.v2.DeterminateProgress;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment.BaseDownloadViewModel;
import com.abiddarris.vnpyemulator.download.base.BaseItem;
import com.abiddarris.vnpyemulator.patches.PatchSource;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

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

        if (PatchSource.isInstalled(patcher)) {
            viewBinding.download.setVisibility(View.VISIBLE);
            viewBinding.download.setIconResource(R.drawable.ic_delete);
            viewBinding.download.setOnClickListener(v -> {
                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newConfirmationDialog(
                        fragment.getString(R.string.confirmation),
                        fragment.getString(R.string.delete_patcher_confirmation, patcher),
                        fragment.getString(android.R.string.cancel),
                        fragment.getString(android.R.string.ok)
                );
                dialog.showForResult(fragment.getChildFragmentManager(), delete -> {
                    try {
                        PatchSource.uninstall(patcher);
                    } catch (IOException e) {
                        ExceptionDialog.showExceptionDialog(baseDownloadViewModel.getFragment()
                                .getChildFragmentManager(), e);
                    }
                    notifyThisChanged();
                });
            });
        } else if (state.isDownloading()) {
            viewBinding.download.setVisibility(View.INVISIBLE);
            viewBinding.download.setOnClickListener(null);
        } else {
            viewBinding.download.setIconResource(R.drawable.ic_download);
            viewBinding.download.setVisibility(View.VISIBLE);
            viewBinding.download.setOnClickListener(v -> {
                TaskInfo<DeterminateProgress, Boolean> taskInfo =
                        fragment.getDownloadService().download(new DownloadPatchTask(patcher));
                taskInfo.addOnTaskExecuted(this::onTaskExecuted);

                state.setDownloading(true);
                this.notifyChanged();
            });
        }
    }

    private void onTaskExecuted(Boolean success) {
        state.setDownloading(false);
        notifyThisChanged();
    }

    private void notifyThisChanged() {
        PatchFragment fragment = baseDownloadViewModel.getFragment();
        PatcherItem item = fragment.getActivePatcherItem(state);
        if (item != null) {
            runOnMainThreadIfNot(item::notifyChanged);
        }
    }
}
