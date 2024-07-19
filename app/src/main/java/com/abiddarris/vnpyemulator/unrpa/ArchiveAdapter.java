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
package com.abiddarris.vnpyemulator.unrpa;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.abiddarris.vnpyemulator.databinding.LayoutArchiveBinding;
import com.abiddarris.vnpyemulator.unrpa.ArchiveAdapter.ArchiveViewHolder;

public class ArchiveAdapter extends Adapter<ArchiveViewHolder> {

    private Archive[] archives;
    private LayoutInflater inflater;

    public ArchiveAdapter(LayoutInflater inflater, Archive[] archives) {
        this.archives = archives;
        this.inflater = inflater;
    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup group, int type) {
        return new ArchiveViewHolder(LayoutArchiveBinding.inflate(inflater, group, false));
    }

    @Override
    public void onBindViewHolder(ArchiveViewHolder holder, int index) {
        var ui = holder.ui;
        Archive archive = archives[index];
        
        ui.name.setText(archive.getName());
        ui.checked.setChecked(archive.isChecked());
        ui.checked.setOnCheckedChangeListener((checkBox, checked) -> archive.setChecked(checked));
    }

    @Override
    public int getItemCount() {
        return archives.length;
    }

    public Archive[] getArchives() {
        return archives;
    }

    public static class ArchiveViewHolder extends ViewHolder {

        private LayoutArchiveBinding ui;

        public ArchiveViewHolder(LayoutArchiveBinding ui) {
            super(ui.getRoot());
            
            this.ui = ui;
        }
    }
}
