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

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.databinding.LayoutPluginGroupBinding;
import com.abiddarris.vnpyemulator.download.base.BaseGroupItem;
import com.abiddarris.vnpyemulator.patches.Patch;

public class PatchItem extends BaseGroupItem {

    private final Patch patch;

    public PatchItem(Patch patch) {
        this.patch = patch;
    }

    @Override
    public void bind(@NonNull LayoutPluginGroupBinding binding, int position) {
        super.bind(binding, position);

        binding.name.setText(patch.getName());
        binding.version.setText(patch.getRenPyVersion());
    }
}
