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
package com.abiddarris.vnpyemulator.games;

import android.os.Bundle;

import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.android.dialogs.FragmentDialog;
import com.abiddarris.common.android.fragments.TextFragment;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogAboutGameBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AboutGameInformationDialog extends BaseDialogFragment<Void> {
    
    private static final String GAME = "game";
    
    public static AboutGameInformationDialog newInstance(Game game) {
        var dialog = new AboutGameInformationDialog();
        dialog.saveVariable(GAME, game);
        
        return dialog;
    }
    
    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);
        
        builder.setTitle(R.string.about);
        
        Game game = getVariable(GAME);
        if (game == null) {
            return;
        }

        DialogAboutGameBinding ui = DialogAboutGameBinding.inflate(getLayoutInflater());
        ui.aboutText.setText(getString(R.string.about_message,
                game.getName(),
                game.getGamePath(),
                game.getGameScript(),
                game.getPlugin(),
                game.getRenPyVersion(),
                game.getPatchVersion()
        ));

        Glide.with(this)
                .load(game.getIconPath())
                .fallback(R.drawable.ic_launcher)
                .into(ui.gameIcon);

        builder.setView(ui.getRoot());
    }
    
    
}
