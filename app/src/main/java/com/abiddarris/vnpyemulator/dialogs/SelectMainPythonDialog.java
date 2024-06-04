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
import android.widget.Toast;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.common.android.dialogs.SingleChoiceDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SelectMainPythonDialog extends SingleChoiceDialog {
    
    private static final String ITEMS = "items";
    
    public static SelectMainPythonDialog newDialog(String[] items) {
        var bundle = new Bundle();
        bundle.putStringArray(ITEMS, items);
        
        var dialog = new SelectMainPythonDialog();
        dialog.setArguments(bundle);
        
        return dialog;
    }
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);

        builder.setTitle(R.string.select_python_script)
            .setMessage(R.string.select_python_script_message)
            .setPositiveButton(R.string.select, (dialog, which) -> {Toast.makeText(getContext(), getItems()[which], Toast.LENGTH_LONG).show();})
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
        
        var items = getArguments().getStringArray(ITEMS);
        setItems(items, 0);
    }
    
}
