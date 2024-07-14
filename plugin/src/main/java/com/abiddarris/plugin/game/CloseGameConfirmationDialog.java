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
package com.abiddarris.plugin.game;

import android.os.Bundle;

import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.plugin.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CloseGameConfirmationDialog extends BaseDialogFragment {
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);
        
        builder.setMessage(R.string.quit_message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> getActivity().finish());
    }
    
}
