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

package com.abiddarris.vnpyemulator.games;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.android.utils.TextListener;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.AddNewGameLayoutBinding;
import com.abiddarris.vnpyemulator.files.Files;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;

public class AddNewGameDialog extends BaseDialogFragment<String> {
    
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
    public void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        binding = AddNewGameLayoutBinding.inflate(getLayoutInflater());
        binding.pathEditText.addOnEditTextAttachedListener(v -> v.getEditText().setText(path));
        binding.pathEditText.getEditText()
            .addTextChangedListener(TextListener.newTextListener(editable -> {
                var file = new File(editable.toString());
                String errorMessage = null;    
               
                if(!file.exists()) {
                    errorMessage = getString(R.string.folder_not_exists);
                } else if(!file.isDirectory()) {
                    errorMessage = getString(R.string.not_a_folder);
                }
                    
                binding.pathEditText.setError(errorMessage == null ? "" : errorMessage);
                binding.pathEditText.setErrorEnabled(errorMessage != null);
                    
                AlertDialog dialog = (AlertDialog)getDialog();
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setEnabled(!binding.pathEditText.isErrorEnabled());
            }));
        
        builder.setTitle(R.string.add_new_game)
            .setView(binding.getRoot())
            .setNegativeButton(android.R.string.cancel, (d,w) -> {})
            .setPositiveButton(android.R.string.ok, (d,w) -> 
                sendResult(binding.pathEditText.getEditText().getText().toString()));
    }
    
    @Override
    @MainThread
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        
        bundle.putString(PATH, binding.pathEditText.getEditText().getText().toString());
    }
  
    @SuppressWarnings("deprecation")
    private String getDefaultLocation() {
    	return Files.getVnPyEmulatorFolder(getContext())
            .getAbsolutePath();
    }
    
}
