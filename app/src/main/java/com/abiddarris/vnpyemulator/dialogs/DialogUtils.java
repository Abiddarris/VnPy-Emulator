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

import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.fragment.app.FragmentManager;
import com.abiddarris.vnpyemulator.databinding.DialogProgressBinding;
import com.abiddarris.vnpyemulator.databinding.DialogSelectItemBinding;
import com.abiddarris.vnpyemulator.utils.ObjectWrapper;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class DialogUtils {
    
    static final String ID = "id";
    
    private static final Random ID_GENERATOR = new Random();
    private static final Map<Integer, ? super DialogInformation> infos = new HashMap<>();
    
    public static void show(FragmentManager fragmentManager, DialogInformation info) {
        int id = generateID();
       
        var arguments = new Bundle();
        arguments.putInt(ID, id);
        
        infos.put(id, info);
        
    	var dialog = new BaseDialog();
        dialog.setArguments(arguments);
        dialog.show(fragmentManager, null);
    }
    
    public static void choseItem(FragmentManager fragmentManager, String title, String message,
            boolean cancelable, String[] items, int selection, Consumer<Integer> selectCallback) {
        
        var index = new ObjectWrapper<>(selection);
        show(fragmentManager, new DialogInformation()
            .setCustomizer(builder -> 
                builder.setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {})
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        if(selectCallback != null) {
                            selectCallback.accept(index.getObject());
                        }
                    }))
            .setOnDialogCreated(dialog -> dialog.setCancelable(cancelable))
            .setView(inflater -> {
                var binding = DialogSelectItemBinding.inflate(inflater);
                var editText = binding.inputLayout.getEditText();
                if(editText instanceof MaterialAutoCompleteTextView) {
                    var spinner = (MaterialAutoCompleteTextView)editText;
                    var adapter = new ArrayAdapter<>(inflater.getContext(),
                             android.R.layout.simple_dropdown_item_1line, items);
                        
                    spinner.setAdapter(adapter);
                    spinner.setText(items[selection]);
                    spinner.setOnItemClickListener((p, v, pos, id) -> index.setObject(pos));
                }
                    
                return binding.getRoot();
            }));
    }
    
    public static void runTask(FragmentManager fragmentManager, String title,
            boolean cancelable, Task task) {
        
        var info = new ProgressDialogInformation();
        info.setCustomizer(builder -> builder.setTitle(title))
            .setView(inflater -> {
                var binding = DialogProgressBinding.inflate(inflater);
                binding.message.setText(info.task.getMessage());
        
                task.attachUI(binding);  
                    
                return binding.getRoot();
            })
            .setOnDialogCreated(dialog -> {
                dialog.setCancelable(cancelable);
                
                task.attachDialog(dialog);
            });
        
        info.task = task;
        show(fragmentManager, info);
    }
    
    @SuppressWarnings("unchecked")
    static <T extends DialogInformation> T getDialogInfo(int id) {
    	return (T)infos.get(id);
    }
    
    static void tear(int id) {
        infos.remove(id);
    }
    
    private static int generateID() {
        return ID_GENERATOR.nextInt();
    }
    
    
}
