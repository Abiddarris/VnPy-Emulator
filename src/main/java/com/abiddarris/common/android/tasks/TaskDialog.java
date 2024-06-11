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
package com.abiddarris.common.android.tasks;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelStoreOwner;
import java.util.Objects;

public abstract class TaskDialog extends Task {
    
    private DialogFragment dialog;
    private String tag;
    
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends DialogFragment> T getDialog() {
        T dialog = (T) getFragmentManager()
            .findFragmentByTag(tag);
        if(dialog != null) {
            this.dialog = dialog;
        }
        
        return (T)this.dialog;
    }
    
    public String getString(int id) {
        return getApplicationContext()
            .getString(id);
    }
    
    public String getString(int id, Object... objects) {
        return getApplicationContext()
            .getString(id, objects);
    }
    
    @Override
    protected void onAttach(TaskViewModel model, ViewModelStoreOwner owner) {
        super.onAttach(model, owner);
        
        if(dialog != null) {
            return;
        }
        
        tag = getTag();
        
        Objects.requireNonNull(tag);
        
        dialog = newDialog();
        dialog.show(getFragmentManager(), tag);
    }
    
    @NonNull
    protected FragmentManager getFragmentManager() {
        var owner = getOwner();
        if(owner instanceof FragmentActivity) {
            return ((FragmentActivity)owner).getSupportFragmentManager();
        } 
        
        if(owner instanceof Fragment) {
            return ((Fragment)owner).getParentFragmentManager();
        }
        
        throw new ClassCastException();
    }
    
    @Override
    public void onFinally() {
        super.onFinally();
        
        getDialog().dismiss();
    }
    
    @NonNull
    protected abstract DialogFragment newDialog();
    
    @NonNull
    protected abstract String getTag();
    
}
