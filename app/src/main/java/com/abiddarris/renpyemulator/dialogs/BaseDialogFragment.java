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
package com.abiddarris.renpyemulator.dialogs;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.DialogFragment;
import com.abiddarris.renpyemulator.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BaseDialogFragment extends DialogFragment {
    
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        MaterialAlertDialogBuilder builder = createDialog();
        View view = createView();
        if(view != null) {
            Resources resources = getResources();
            int padding = (int)resources.getDimension(R.dimen.abc_dialog_padding_material);
            int paddingTop = (int)resources.getDimension(R.dimen.abc_dialog_padding_top_material);
            view.setPadding(padding, padding, paddingTop, 0);
            
            builder.setView(view);
        }
        
        return builder.create();
    }
    
    protected MaterialAlertDialogBuilder createDialog() {
    	return new MaterialAlertDialogBuilder(getContext());
    }
    
    protected View createView() {
        return null;
    }
    
}
