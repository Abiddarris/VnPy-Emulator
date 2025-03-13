/***********************************************************************************
 * Copyright (C) 2024-2025 Abiddarris
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
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.games;

import androidx.fragment.app.DialogFragment;

import com.abiddarris.common.android.dialogs.ProgressDialog;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.vnpyemulator.R;

public class DeleteGameTask extends TaskDialog {
    
    private final Game game;
    
    public DeleteGameTask(Game game) {
        this.game = game;
    }
    
    @Override
    protected String getTag() {
        return "DeleteGameTask";
    }
    
    @Override
    protected DialogFragment newDialog() {
        ProgressDialog dialog = ProgressDialog.newProgressDialog(
                getString(R.string.delete_game),
                getString(R.string.deleting_game)
        );
        dialog.setCancelable(false);
        
        return dialog;
    }
    
    @Override
    public void execute() throws Exception {
        int index = GameLoader.getGames(getApplicationContext())
            .indexOf(game);
        
        GameLoader.deleteGame(getApplicationContext(), game);
        GameLoader.saveGames(getApplicationContext());
        
        GameListFragment fragment = (GameListFragment)getOwner();
        fragment.getActivity().runOnUiThread(() -> {
            fragment.getAdapter()
                .notifyItemRemoved(index);
        });
    }
    
}
