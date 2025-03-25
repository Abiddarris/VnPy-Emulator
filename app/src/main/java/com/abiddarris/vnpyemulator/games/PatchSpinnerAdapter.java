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
package com.abiddarris.vnpyemulator.games;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginForSpinnerBinding;
import com.abiddarris.vnpyemulator.patches.PatchSource;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.io.IOException;

public class PatchSpinnerAdapter extends ArrayAdapter<String> {

    private MaterialAutoCompleteTextView owner;

    public PatchSpinnerAdapter(@NonNull Context context, MaterialAutoCompleteTextView owner, @NonNull String[] patchers) {
        super(context, R.layout.layout_plugin_for_spinner, R.id.text, patchers);

        this.owner = owner;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        LayoutPluginForSpinnerBinding ui = LayoutPluginForSpinnerBinding.bind(view);
        String item = getItem(position);
        boolean installed = false;

        try {
            Patcher patcher = PatchSource.getPatcher(item);
            installed = PatchSource.isInstalled(patcher);
        } catch (IOException ignored) {
        }

        ui.imageView.setVisibility(installed ? View.GONE : View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ui.check.getLayoutParams();
        if (installed) {
            layoutParams.removeRule(RelativeLayout.LEFT_OF);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        } else {
            layoutParams.addRule(RelativeLayout.LEFT_OF, ui.imageView.getId());
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
        }

        boolean checked = item.equals(owner.getText().toString());
        ui.check.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);

        return view;
    }
}
