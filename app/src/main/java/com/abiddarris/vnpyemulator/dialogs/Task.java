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

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import com.abiddarris.vnpyemulator.utils.BaseRunnable;

public abstract class Task implements BaseRunnable {
    
    private FragmentManager manager;
    private String id;
    private String message;
    
    public final void onFinally() {
        var dialog = getDialog();
        if(dialog != null) {
            dialog.tear();
        }
         
        cleanUp();
    }
    
    protected void cleanUp() {
    }
    
    protected void setMessage(String message) {
        this.message = message;
        
        var dialog = getDialog();
        if(dialog != null)
            dialog.sendMessage(message);
    }
    
    public String getMessage() {
    	return message;
    }
    
    void init(FragmentManager manager, String id) {
        this.manager = manager;
        this.id = id;
    }
    
    public ProgressDialog getDialog() {
        return (ProgressDialog) manager.findFragmentByTag(id);
    }
    
    public void onAttachApplicationContext(Context context) {
    }
}
