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

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogApplyPatchBinding;
import com.abiddarris.vnpyemulator.patches.PatchRunnable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplyPatchDialog extends BaseDialogFragment {
    
    public static final String FOLDER_TO_PATCH = "folderToPatch";
    
    private static final ExecutorService PATCH_THREAD = Executors.newSingleThreadExecutor();
    private static PatchRunnable runnable;
    
    private DialogApplyPatchBinding binding;
    
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        setCancelable(false);
       
        var dialog = super.onCreateDialog(bundle);
        
        if(runnable == null) {
            PATCH_THREAD.submit(runnable = new PatchRunnable(this));
        } else {
            runnable.setDialog(this);
        }
        
        return dialog;
    }
    
    @Override
    protected MaterialAlertDialogBuilder createDialog() {
        return super.createDialog()
            .setTitle(R.string.apply_patch_dialog_title);
    }
    
    @Override
    protected View createView() {
        binding = DialogApplyPatchBinding.inflate(getLayoutInflater());
        
        return binding.getRoot();
    }
    
    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if(runnable != null) {
            runnable.setDialog(null);
        }
        
        super.onDestroy();
    }
    
    
    public void setMessage(String message) {
        getActivity().runOnUiThread(() -> 
            binding.message.setText(message));
    }
    
    public void tear() {
    	runnable = null;
        dismiss();
    }
}
