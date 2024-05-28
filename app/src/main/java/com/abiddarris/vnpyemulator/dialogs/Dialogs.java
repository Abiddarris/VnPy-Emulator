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
import androidx.fragment.app.FragmentManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.abiddarris.vnpyemulator.dialogs.ProgressDialog.ProgressDialogInformation;

public class Dialogs {
    
    static final String ID = "id";
    
    private static final Random ID_GENERATOR = new Random();
    private static final Map<Integer, ? super DialogInformation> infos = new HashMap<>();
    
    public static void runTask(FragmentManager fragmentManager, String title,
            boolean cancelable, Task task) {
        int id = generateID();
        var idString = String.valueOf(id);
        
        var info = new ProgressDialogInformation();
        info.title = title;
        info.cancelable = cancelable;
        info.task = task;
        
        task.init(fragmentManager, idString);
        
        infos.put(id, info);
       
        var arguments = new Bundle();
        arguments.putInt(ID, id);
        
    	var dialog = new ProgressDialog();
        dialog.setArguments(arguments);
        dialog.show(fragmentManager, idString);
    }
    
    @SuppressWarnings("unchecked")
    static <T extends DialogInformation> T getDialogInfo(int id) {
    	return (T)infos.get(id);
    }
    
    private static int generateID() {
        return ID_GENERATOR.nextInt();
    }
    
    static class DialogInformation {
        protected String title;
        protected String message;
        protected boolean cancelable;
    }

}
