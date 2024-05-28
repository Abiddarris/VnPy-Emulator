/***********************************************************************************
 * Copyright 2024 Abiddarris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogProgressBinding;
import com.abiddarris.vnpyemulator.dialogs.Dialogs.DialogInformation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressDialog extends BaseDialogFragment {
    
    private DialogProgressBinding binding;
    private ProgressDialogInformation info;
    
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        var arguments = getArguments();
        var id = arguments.getInt(Dialogs.ID);
        
        info = Dialogs.getDialogInfo(id);
       
        var dialog = super.onCreateDialog(bundle);
        
        if(!info.executed) {
            info.task.onAttachApplicationContext(getContext().getApplicationContext());
            info.executor.submit(info.task);
            info.executed = true;
        } 
        
        return dialog;
    }
    
    @Override
    protected MaterialAlertDialogBuilder createDialog() {
        return super.createDialog()
            .setTitle(info.title);
    }
    
    @Override
    protected View createView() {
        binding = DialogProgressBinding.inflate(getLayoutInflater());
        binding.message.setText(info.task.getMessage());
        
        return binding.getRoot();
    }
    
    public void tear() {
    	info.executor.shutdown();
        dismiss();
    }
    
    public void sendMessage(String message) {
    	getActivity().runOnUiThread(() -> {
            if(binding != null) 
                binding.message.setText(message);
        });
    }
    
    static class ProgressDialogInformation extends DialogInformation {
        protected boolean executed;
        protected Task task;
        protected ExecutorService executor = Executors.newSingleThreadExecutor();
    }
    
}
