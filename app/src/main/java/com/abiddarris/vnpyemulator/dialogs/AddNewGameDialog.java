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

package com.abiddarris.vnpyemulator.dialogs;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.MainThread;
import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.android.utils.TextListener;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.AddNewGameLayoutBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;

public class AddNewGameDialog extends BaseDialogFragment {
    
    private static final String PATH = "PATH";
    
    private AddNewGameLayoutBinding binding;
    private String path;
    
    @Override
    @MainThread
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        path = getDefaultLocation();
        if(bundle != null) {
            path = bundle.getString(PATH, path);
        }
    }
    
    @Override
    protected MaterialAlertDialogBuilder createDialog() {
        return super.createDialog()
            .setTitle(R.string.add_new_game)
            .setNegativeButton(android.R.string.cancel, (d,w) -> {})
            .setPositiveButton(android.R.string.ok, (d,w) -> {
                var bundle = new Bundle();
                var dialog = new ApplyPatchDialog();
                
                bundle.putString(ApplyPatchDialog.FOLDER_TO_PATCH, 
                    binding.pathEditText.getEditText().getText().toString());
                
                dialog.setArguments(bundle);
                dialog.show(getParentFragmentManager(), null);
            });
    }
    
    @Override
    public View createView() {
        binding = AddNewGameLayoutBinding.inflate(getLayoutInflater());
        binding.pathEditText.addOnEditTextAttachedListener(v -> v.getEditText().setText(path));
        binding.pathEditText.getEditText()
            .addTextChangedListener(TextListener.newTextListener(editable -> {
                var file = new File(editable.toString());
                String errorMessage = null;    
                if(!file.exists()) {
                    errorMessage = getString(R.string.folder_not_exists);
                }
                    
                binding.pathEditText.setError(errorMessage == null ? "" : errorMessage);
                binding.pathEditText.setErrorEnabled(errorMessage != null);
            }));
        
        return binding.getRoot();
    }
    
    @Override
    @MainThread
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        
        bundle.putString(PATH, binding.pathEditText.getEditText().getText().toString());
    }
    
    @Override
    protected boolean hasButton() {
        return true;
    }
  
    @SuppressWarnings("deprecation")
    private String getDefaultLocation() {
    	Context context = getContext();
        File[] files = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && "samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
            files = context.getExternalMediaDirs();
            
            for(var file : files) {
                if(file != null) {
                    return file.getAbsolutePath();
                }
            }
        } 
        
        File file = context.getExternalFilesDir(null);
        return file == null ? "" : file.getAbsolutePath();
    }
    
}
