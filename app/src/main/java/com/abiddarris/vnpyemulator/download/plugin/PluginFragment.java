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

public class PluginFragment extends BaseDownloadFragment {

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

        baseDownloadViewModel.execute(new FetchPluginTask());
    }

    private void requestInstallFromUnknownSourceCallback(Boolean success) {
        checkInstallFromUnknownSourcePermission();
    }

    public void onPluginFetched(PluginGroup[] pluginGroups) {
        requireActivity().runOnUiThread(() -> {
            for (PluginGroup group : pluginGroups) {
                ExpandableGroup pluginGroup = new ExpandableGroup(new PluginGroupItem(group));
                for (Plugin plugin : group.getPlugins()){
                    pluginGroup.add(new PluginItem(plugin, baseDownloadViewModel));
                }

                adapter.add(pluginGroup);
            }
        });

        checkInstallFromUnknownSourcePermission();
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
