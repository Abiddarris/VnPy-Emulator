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
import androidx.annotation.Nullable;
import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.vnpyemulator.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class IncompatiblePatchDialog extends BaseDialogFragment<Boolean> {
    
    public static final String FILE_NAME = "fileName";
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);
        
        String fileName = getVariable(FILE_NAME);
        
        builder.setTitle(R.string.incompatible_patch)
            .setMessage(getString(R.string.incompatible_patch_message, fileName))
            .setPositiveButton(R.string.apply_anyway, (dialog, which) -> sendResult(true))
            .setNegativeButton(R.string.abort, null);
    }

    @Nullable
    @Override
    protected Boolean getDefaultResult() {
        return false;
    }
}
