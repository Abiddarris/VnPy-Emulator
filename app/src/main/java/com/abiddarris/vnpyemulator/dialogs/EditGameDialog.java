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

import static com.abiddarris.common.android.utils.TextListener.*;
import static com.abiddarris.vnpyemulator.games.GameLoader.getGames;

import android.os.Bundle;
import android.text.Editable;

import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.android.utils.TextListener;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogEditGameBinding;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.games.GameLoader;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EditGameDialog extends BaseDialogFragment<Boolean> {

    private static final String GAME = "game";
    private static final String NEW_GAME = "new_game";
    private DialogEditGameBinding ui;
    private List<String> disallowedNames;

    public static EditGameDialog editGame(Game game) {
        EditGameDialog dialog = new EditGameDialog();
        dialog.saveVariable(GAME, game);

        return dialog;
    }

    public static EditGameDialog editNewGame(Game game) {
        EditGameDialog dialog = editGame(game);
        dialog.saveVariable(NEW_GAME, true);

        return dialog;
    }

    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);

        initInvalidNames();

        Game game = getGame();

        ui = DialogEditGameBinding.inflate(getLayoutInflater());
        ui.name.setText(game.getName());
        ui.name.addTextChangedListener(newTextListener(this::onNameTextChanged));

        builder.setTitle(R.string.edit_game)
                .setView(ui.getRoot())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> onPositiveButtonClicked(game));

        if (!isNewGame()) {
            setCancelable(false);
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
        }
    }

    private void initInvalidNames() {
        try {
             disallowedNames = getGames(getContext()).stream()
                    .map(Game::getName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onPositiveButtonClicked(Game game) {
        boolean updated = false;

        String name = ui.name.getText().toString();
        if (!game.getName().equals(name)) {
            updated = true;
            game.setName(name);
        }

        sendResult(updated);
    }

    private void onNameTextChanged(Editable editable) {
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

        boolean error = ui.nameLayout.isErrorEnabled();
        if(error == invalid) return;

        ui.nameLayout.setErrorEnabled(invalid);
        ui.nameLayout.setError(message);

        enablePositiveButton(!invalid);
    }

    @Override
    protected Boolean getDefaultResult() {
        return false;
    }

    private Game getGame() {
        return getVariable(GAME);
    }

    private boolean isNewGame() {
        return getVariable(NEW_GAME, false);
    }
}
