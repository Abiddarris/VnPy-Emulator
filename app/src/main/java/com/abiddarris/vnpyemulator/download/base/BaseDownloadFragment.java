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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abiddarris.common.android.fragments.AdvanceFragment;
import com.abiddarris.common.android.tasks.TaskViewModel;
import com.abiddarris.vnpyemulator.databinding.FragmentPluginBinding;
import com.abiddarris.vnpyemulator.download.DownloadFragment;
import com.abiddarris.vnpyemulator.download.DownloadService;
import com.abiddarris.vnpyemulator.download.plugin.PluginFragment;
import com.xwray.groupie.GroupieAdapter;

public class BaseDownloadFragment extends AdvanceFragment {

    protected BaseDownloadViewModel baseDownloadViewModel;
    protected GroupieAdapter adapter;

    private FragmentPluginBinding ui;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseDownloadViewModel = TaskViewModel.getInstance(this, BaseDownloadViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ui = FragmentPluginBinding.inflate(inflater);

        return ui.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new GroupieAdapter();

        ui.plugins.setLayoutManager(new LinearLayoutManager(getContext()));
        ui.plugins.setAdapter(adapter);
    }

    public DownloadService getDownloadService() {
        return ((DownloadFragment)getParentFragment()).getDownloadService();
    }

    public static class BaseDownloadViewModel extends TaskViewModel {

        public BaseDownloadFragment getFragment() {
            return (BaseDownloadFragment) getOwner();
        }
    }
}
