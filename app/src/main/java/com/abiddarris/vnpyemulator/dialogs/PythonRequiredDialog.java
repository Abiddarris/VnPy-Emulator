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
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.games.Game;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONException;

public class PythonRequiredDialog extends BaseDialogFragment {
    
    public static final String GAME = "game";
    public static final String PYTHON_VERSIONS = "python_versions";
    
    @Override
    protected MaterialAlertDialogBuilder createDialog() {
        Bundle bundle = getArguments();
        var pythonVersions = bundle.getStringArray(PYTHON_VERSIONS);
        
        Game game = null;
        try {
            game = new Game(bundle.getString(GAME));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return super.createDialog()
            .setTitle(R.string.python_required)
            .setMessage(getString(
                R.string.python_required_message, game.optString(Game.GAME_NAME)))
            .setSingleChoiceItems(pythonVersions, -1, (dialog, index) -> download(pythonVersions[index]));
    }
    
    public void download(String a) {
    	
    }
    
}
