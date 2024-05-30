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
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BaseDialog<T extends DialogInformation> extends BaseDialogFragment {
    
    private int id;
    private T info;
    
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        var arguments = getArguments();
        id = arguments.getInt(DialogUtils.ID);
        
        info = DialogUtils.getDialogInfo(id);
       
        var dialog = super.onCreateDialog(bundle);
        var onDialogCreated = getDialogInformation().getOnDialogCreated();
        if(onDialogCreated != null) {
            onDialogCreated.accept(this);
        }
        
        return dialog;
    }
    
    @Override
    protected View createView() {
        var supplier = getDialogInformation().getView();
        return supplier == null ? null : supplier.apply(getLayoutInflater());
    }
    
    public T getDialogInformation() {
        return info;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected MaterialAlertDialogBuilder createDialog() {
        var dialogBuilder = super.createDialog();
        var consumer = getDialogInformation().getCustomizer();
        if(consumer != null) {
            consumer.accept(dialogBuilder);
        }
            
        return dialogBuilder;
    }
    
    @Override
    public void onCancel(DialogInterface _interface) {
        super.onCancel(_interface);

        tear();
    }
    
    
    public void tear() {
        dismiss();
        
        DialogUtils.tear(id);
    }
    
}
