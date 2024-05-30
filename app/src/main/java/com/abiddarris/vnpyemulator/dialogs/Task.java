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
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.FragmentManager;
import com.abiddarris.common.utils.BaseRunnable;
import com.abiddarris.vnpyemulator.databinding.DialogProgressBinding;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Task implements BaseRunnable {
    
    private volatile Context context;
    
    private boolean executed;
    private BaseDialog dialog;
    private DialogProgressBinding binding;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private FragmentManager manager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String message;
    
    public final void onFinally() {
        var dialog = getDialog();
        if(dialog != null) {
            dialog.tear();
        }
        
        executor.shutdown();
        
        cleanUp();
    }
    
    protected void cleanUp() {
    }
    
    protected void setMessage(String message) {
        this.message = message;
        
        var dialog = getDialog();
        if(binding != null) {
            handler.post(() -> binding.message.setText(message));
        }
    }
    
    public BaseDialog getDialog() {
        return dialog;
    }
    
    /**
     * Returns application context. 
     *
     * @return application context.
     */
    public Context getApplicationContext() {
        return context;
    }
    
    public String getMessage() {
    	return message;
    }
    
    void attachDialog(BaseDialog dialog) {
        this.dialog = dialog;
        this.context = dialog.getContext()
            .getApplicationContext();
        
        if(!executed) {
            executor.submit(this);
            executed = true;
        } 
    }
    
    void attachUI(DialogProgressBinding binding) {
        this.binding = binding;
    }
}
