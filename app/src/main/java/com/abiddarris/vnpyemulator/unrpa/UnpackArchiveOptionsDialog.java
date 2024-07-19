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

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.android.tasks.TaskViewModel;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogUnpackArchiveBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.stream.Stream;

public class UnpackArchiveOptionsDialog extends BaseDialogFragment<Void> {
    
    public static final String BASE_PATH = "basePath";
    public static final String ARCHIVES = "archives";
    
    private ArchiveAdapter adapter;
    private DialogUnpackArchiveBinding binding;
    
    public static UnpackArchiveOptionsDialog newInstance(String basePath, File[] archives) {
        var dialog = new UnpackArchiveOptionsDialog();
        dialog.saveVariable(BASE_PATH, basePath);
        dialog.saveVariable(ARCHIVES, 
            Stream.of(archives)
                .map(file -> new Archive(basePath, file))
                .toArray(Archive[]::new)
        );
        
        return dialog;
    }
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);
        
        adapter = new ArchiveAdapter(getLayoutInflater(), getArchives());
        
        binding = DialogUnpackArchiveBinding.inflate(getLayoutInflater());
        binding.archives.setLayoutManager(
            new LinearLayoutManager(getContext())
        );
        binding.archives.setAdapter(adapter);
        
        builder.setTitle(R.string.unpack_archive)
            .setView(binding.getRoot())
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.unpack, (dialog, which) -> {
                TaskViewModel.getInstance(getActivity())
                    .execute(new UnrpaTask(
                        Stream.of(adapter.getArchives())
                            .filter(Archive::isChecked)
                            .map(Archive::getFile)
                            .toArray(File[]::new),
                        getVariable(BASE_PATH),
                        binding.deleteAfterUnpacking.isChecked()
                    ));
            });
    }
    
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        
        saveVariable(ARCHIVES, adapter.getArchives());
    }
    
    private Archive[] getArchives() {
        return getVariable(ARCHIVES);
    }
}
