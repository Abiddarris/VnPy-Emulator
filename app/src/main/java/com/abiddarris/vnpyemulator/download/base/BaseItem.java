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
package com.abiddarris.vnpyemulator.download.base;

import android.view.View;

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.xwray.groupie.viewbinding.BindableItem;

public abstract class BaseItem extends BindableItem<LayoutPluginBinding> {

    protected final BaseDownloadFragment.BaseDownloadViewModel pluginViewModel;

    public BaseItem(BaseDownloadFragment.BaseDownloadViewModel pluginViewModel) {
        this.pluginViewModel = pluginViewModel;
    }

    @Override
    public int getLayout() {
        return R.layout.layout_plugin;
    }

    @NonNull
    @Override
    protected LayoutPluginBinding initializeViewBinding(@NonNull View view) {
        return LayoutPluginBinding.bind(view);
    }
}
