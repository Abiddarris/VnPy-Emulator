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

import static android.content.pm.PackageInstaller.STATUS_SUCCESS;

import static com.abiddarris.common.android.handlers.MainThreads.runOnMainThreadIfNot;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.android.dialogs.SimpleDialog;
import com.abiddarris.common.android.pm.Packages;
import com.abiddarris.common.android.tasks.v2.DeterminateProgress;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.abiddarris.vnpyemulator.download.DownloadService;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment.BaseDownloadViewModel;
import com.abiddarris.vnpyemulator.download.base.BaseItem;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.abiddarris.vnpyemulator.plugins.PluginSource;

import java.io.IOException;

public class PluginItem extends BaseItem {

    private final Plugin plugin;
    private final PluginState pluginState;
    private LayoutPluginBinding viewBinding;

    public PluginItem(PluginState pluginState, BaseDownloadViewModel pluginViewModel) {
        super(pluginViewModel);

        this.pluginState = pluginState;
        this.plugin = pluginState.getPlugin();
    }

    @Override
    public void bind(@NonNull LayoutPluginBinding viewBinding, int position) {
        this.viewBinding = viewBinding;

        BaseDownloadFragment fragment = pluginViewModel.getFragment();
        viewBinding.version.setText(String.format("%s (%s)", plugin.getVersion(), plugin.getAbi()));

        if (PluginSource.isInstalled(fragment.getContext(), plugin) || pluginState.isDownloading()) {
            viewBinding.download.setOnClickListener(null);
            viewBinding.download.setVisibility(View.INVISIBLE);
        } else {
            viewBinding.download.setVisibility(View.VISIBLE);
            viewBinding.download.setOnClickListener(v -> download(fragment));
        }
    }

    private void download(BaseDownloadFragment fragment) {
        if (viewBinding.download.getVisibility() == View.INVISIBLE) {
            return;
        }
        pluginState.setDownloading(true);
        notifyChanged();

        DownloadService service = fragment.getDownloadService();

        TaskInfo<DeterminateProgress, Boolean> info = service.download(new DownloadPluginTask(plugin));
        info.addOnTaskExecuted(this::onDownloadResult);
    }

    private void onDownloadResult(Boolean success) {
        if (success) {
            installPlugin();
            return;
        }
        pluginState.setDownloading(false);

        PluginFragment fragment = pluginViewModel.getFragment();
        PluginItem item = fragment.getActivePluginItem(pluginState);
        if (item != null) {
            runOnMainThreadIfNot(item::notifyChanged);
        }
    }

    private void installPlugin() {
        BaseDownloadFragment fragment = pluginViewModel.getFragment();
        Context context = fragment.requireContext().getApplicationContext();

        try {
            Packages.installPackage(context, plugin.getPluginApk(context), (status, message) -> {
                if (status == STATUS_SUCCESS) {
                    notifyChanged();
                    return;
                }

                SimpleDialog.show(
                        pluginViewModel.getFragment().getChildFragmentManager(),
                        context.getString(R.string.installation_error),
                        context.getString(
                                R.string.installation_error_message,
                                plugin.toString(),
                                String.valueOf(status), message
                        )
                );
            });
        } catch (IOException e) {
            ExceptionDialog.showExceptionDialog(fragment.getChildFragmentManager(), e);
        }
    }
}
