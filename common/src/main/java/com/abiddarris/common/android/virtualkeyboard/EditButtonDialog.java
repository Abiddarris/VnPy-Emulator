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
package com.abiddarris.common.android.virtualkeyboard;

import android.os.Bundle;
import android.widget.Button;
import com.abiddarris.common.R;
import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.databinding.DialogEditButtonBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class EditButtonDialog extends BaseDialogFragment<Void> {
    
    private static final String FOCUS = "focus";
    
    private DialogEditButtonBinding binding;
    private Keycode code;
    
    public static EditButtonDialog newInstance(Key key) {
        var dialog = new EditButtonDialog();
        dialog.saveVariable(FOCUS, key);
        
        return dialog;
    }
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);
       
        Key key = getVariable(FOCUS);
        Button button = key.getButton();
        
        binding = DialogEditButtonBinding.inflate(getLayoutInflater());
        binding.name.getEditText()
            .setText(button.getText());
       
        var adapter = new KeySpinner(getContext());
        
        MaterialAutoCompleteTextView keySpinner = (MaterialAutoCompleteTextView) binding.key.getEditText();
        keySpinner.setAdapter(adapter);
        keySpinner.setOnItemClickListener((adapterView, view, index, id) -> code = adapter.getItem(index));
        
        builder.setTitle(R.string.edit)
            .setView(binding.getRoot())
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                button.setText(binding.name.getEditText().getText().toString());
                
                key.setKeycode(code);
            });
    }
    
}