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
import com.google.android.material.button.MaterialButton;

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

        PluginFragment fragment = baseDownloadViewModel.getFragment();
        viewBinding.version.setText(String.format("%s (%s)", plugin.getVersion(), plugin.getAbi()));

        if (PluginSource.isInstalled(fragment.getContext(), plugin) || pluginState.isDownloading() || pluginState.isInstalling()) {
            viewBinding.download.setOnClickListener(null);
            viewBinding.download.setVisibility(View.INVISIBLE);
        } else if (PluginSource.isDownloaded(fragment.getContext(), plugin)) {
            viewBinding.download.setVisibility(View.VISIBLE);
            viewBinding.download.setOnClickListener(v -> {
                installPlugin();
                notifyItem(fragment.getActivePluginItem(pluginState));
            });
            ((MaterialButton)viewBinding.download).setIconResource(R.drawable.ic_install);
        } else {
            viewBinding.download.setVisibility(View.VISIBLE);
            viewBinding.download.setOnClickListener(v -> download(fragment));

            ((MaterialButton)viewBinding.download).setIconResource(R.drawable.ic_download);
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
        pluginState.setDownloading(false);
        if (success) {
            installPlugin();
        }

        PluginFragment fragment = baseDownloadViewModel.getFragment();
        notifyItem(fragment.getActivePluginItem(pluginState));
    }

    private void notifyItem(PluginItem item) {
        if (item != null) {
            runOnMainThreadIfNot(item::notifyChanged);
        }
    }

    private void installPlugin() {
        BaseDownloadFragment fragment = baseDownloadViewModel.getFragment();
        Context context = fragment.requireContext().getApplicationContext();
        pluginState.setInstalling(true);

        try {
            Packages.installPackage(
                    context, plugin.getPluginApk(context),
                    (status, message) -> onInstallResult(context, status, message)
            );
        } catch (IOException e) {
            pluginState.setInstalling(false);
            ExceptionDialog.showExceptionDialog(fragment.getChildFragmentManager(), e);
        }
    }

    private void onInstallResult(Context context, int status, String message) {
        PluginFragment fragment = baseDownloadViewModel.getFragment();
        if (status != STATUS_SUCCESS) {
            SimpleDialog.show(
                    baseDownloadViewModel.getFragment().getChildFragmentManager(),
                    context.getString(R.string.installation_error),
                    context.getString(
                            R.string.installation_error_message,
                            plugin.toString(),
                            String.valueOf(status), message
                    )
            );
        } else {
            for (Plugin neighbouringPlugin : plugin.getPluginGroup().getPlugins()) {
                if (neighbouringPlugin == plugin) {
                    PluginSource.setInstalled(plugin, true);
                    continue;
                }

                if (neighbouringPlugin.getVersion().equals(plugin.getVersion())) {
                    PluginSource.setInstalled(plugin, false);
                    notifyItem(fragment.getActivePluginItem(fragment.getPluginState(neighbouringPlugin)));
                }
            }
        }

        pluginState.setInstalling(false);
        notifyItem(fragment.getActivePluginItem(pluginState));
    }
}
