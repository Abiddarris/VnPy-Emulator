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

import androidx.fragment.app.DialogFragment;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.vnpyemulator.MainActivity;

public class DeleteGameTask extends TaskDialog {
    
    private Game game;
    
    public DeleteGameTask(Game game) {
        this.game = game;
    }
    
    @Override
    protected String getTag() {
        return "deleteGameTask";
    }
    
    @Override
    protected DialogFragment newDialog() {
        return new DeletingGameDialog();
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
