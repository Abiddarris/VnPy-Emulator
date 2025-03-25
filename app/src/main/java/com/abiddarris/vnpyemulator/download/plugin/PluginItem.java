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
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.abiddarris.common.android.handlers.MainThreads.runOnMainThreadIfNot;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.android.dialogs.SimpleConfirmationDialog;
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

        PluginFragment fragment = baseDownloadViewModel.getFragment();
        viewBinding.version.setText(String.format("%s (%s)", plugin.getVersion(), plugin.getAbi()));

        int downloadButtonVisibility = VISIBLE;
        int deleteButtonVisibility = PluginSource.isDownloaded(fragment.getContext(), plugin) ? VISIBLE : GONE;
        View.OnClickListener downloadClickListener = v -> download(fragment);
        Integer iconResource = R.drawable.ic_download;

        if (PluginSource.isInstalled(fragment.getContext(), plugin) || pluginState.isDownloading() || pluginState.isInstalling()) {
            downloadClickListener = null;
            downloadButtonVisibility = deleteButtonVisibility == VISIBLE ? GONE : INVISIBLE;
            iconResource = null;
        } else if (PluginSource.isDownloaded(fragment.getContext(), plugin)) {
            downloadClickListener = v -> {
                installPlugin();
                notifyThisItem(fragment);
            };
            iconResource = R.drawable.ic_install;
        }

        viewBinding.download.setVisibility(downloadButtonVisibility);
        viewBinding.download.setOnClickListener(downloadClickListener);
        viewBinding.delete.setVisibility(deleteButtonVisibility);
        viewBinding.delete.setOnClickListener(v -> showDeleteConfirmationDialog(fragment));

        if (iconResource != null) {
            viewBinding.download.setIconResource(iconResource);
        }
    }

    private void showDeleteConfirmationDialog(PluginFragment fragment) {
        SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newConfirmationDialog(
                fragment.getString(R.string.confirmation),
                fragment.getString(R.string.delete_plugin_confirmation, plugin),
                fragment.getString(android.R.string.cancel),
                fragment.getString(android.R.string.ok)
        );
        dialog.showForResult(fragment.getChildFragmentManager(), delete -> {
            if (!delete) {
                return;
            }

            PluginFragment fragment1 = baseDownloadViewModel.getFragment();
            try {
                PluginSource.delete(fragment1.getContext(), plugin);
                notifyThisItem(fragment1);
            } catch (IOException e) {
                ExceptionDialog.showExceptionDialog(fragment1.getChildFragmentManager(), e);
            }
        });
    }

    private void download(BaseDownloadFragment fragment) {
        if (viewBinding.download.getVisibility() == INVISIBLE) {
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
        notifyThisItem(fragment);
    }

    private void notifyThisItem(PluginFragment fragment) {
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
        notifyThisItem(fragment);
    }
}
