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

import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.function.Consumer;
import java.util.function.Function;

public class DialogInformation {
    
    private Consumer<MaterialAlertDialogBuilder> customizer;
    private Consumer<BaseDialog> onDialogCreated;
    private Function<LayoutInflater, View> view;
    
    protected String message;
    
    public Consumer<MaterialAlertDialogBuilder> getCustomizer() {
        return this.customizer;
    }
   
    public DialogInformation setCustomizer(Consumer<MaterialAlertDialogBuilder> customizer) {
        this.customizer = customizer;
        
        return this;
    }

    public Function<LayoutInflater, View> getView() {
        return this.view;
    }
    
    public DialogInformation setView(Function<LayoutInflater, View> view) {
        this.view = view;
      
        return this;
    }

    public Consumer<BaseDialog> getOnDialogCreated() {
        return this.onDialogCreated;
    }
    
    public DialogInformation setOnDialogCreated(Consumer<BaseDialog> onDialogCreated) {
        this.onDialogCreated = onDialogCreated;
        
        return this;
    }
    
}