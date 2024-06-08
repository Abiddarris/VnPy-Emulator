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

import android.os.Bundle;
import com.abiddarris.common.android.dialogs.EditTextDialog;
import com.abiddarris.common.android.utils.TextListener;
import com.abiddarris.vnpyemulator.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class SetGameNameDialog extends EditTextDialog {
   
    private static final String DISALLOWED_NAMES = "disallowed_names";
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);
        
        setCancelable(false);
        
        var ui = getUI();
        List<String> disallowedNames = getVariable(DISALLOWED_NAMES);
        
        ui.textInputLayout.setHint(R.string.set_game_name_title);
        ui.textInputEditText.addTextChangedListener(TextListener.newTextListener((editable) -> {
            String string = editable.toString();
            boolean invalid = false;
            String message = null;
                    
            if(string.isBlank()) {
                invalid = true;
                message = getString(R.string.name_cannot_be_blank);
            } else if(disallowedNames.contains(string)) {
                invalid = true;
                message = getString(R.string.name_already_used);
            }
                    
            boolean error = ui.textInputLayout.isErrorEnabled();
            if(error == invalid) return;        
           
            ui.textInputLayout.setErrorEnabled(invalid);
            ui.textInputLayout.setError(message);
                    
            enablePositiveButton(!invalid);
        }));
        
        builder.setPositiveButton(android.R.string.ok, null)
            .setTitle(R.string.set_game_name_title);
    }
    
    public void setDisallowedNames(List<String> names) {
        saveVariable(DISALLOWED_NAMES, names);
    }
}
