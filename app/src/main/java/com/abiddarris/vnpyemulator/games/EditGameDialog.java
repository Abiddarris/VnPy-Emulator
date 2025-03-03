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

import static com.abiddarris.common.android.utils.TextListener.*;
import static com.abiddarris.common.stream.InputStreams.writeAllTo;
import static com.abiddarris.common.utils.Randoms.newRandomString;
import static com.abiddarris.vnpyemulator.files.Files.getIconFolder;
import static com.abiddarris.vnpyemulator.games.GameLoader.getGames;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument;

import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogEditGameBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EditGameDialog extends BaseDialogFragment<Boolean> {

    private static final String GAME = "game";
    private static final String NEW_GAME = "new_game";
    private static final String ICON_URI = "icon_uri";

    private ActivityResultLauncher<String[]> selectImageLauncher;
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

        selectImageLauncher = registerForActivityResult(new OpenDocument(), this::onImageSelected);

        initInvalidNames();

        Game game = getGame();

        ui = DialogEditGameBinding.inflate(getLayoutInflater());
        ui.name.setText(game.getName());
        ui.name.addTextChangedListener(newTextListener(this::onNameTextChanged));
        ui.card.setOnClickListener(v -> selectImageLauncher.launch(new String[] {"image/*"}));

        Glide.with(this)
                .load(game.getIconPath())
                .fallback(R.drawable.ic_launcher)
                .into(ui.icon);

        builder.setTitle(R.string.edit_game)
                .setView(ui.getRoot())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> onPositiveButtonClicked(game));

        if (!isNewGame()) {
            setCancelable(false);
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
        }
    }

    private void onImageSelected(Uri uri) {
        if (uri == null) {
            return;
        }

        Glide.with(this)
                .load(uri)
                .fallback(R.drawable.ic_launcher)
                .into(ui.icon);

        setIconUri(uri);
    }

    private void setIconUri(Uri uri) {
        saveVariable(ICON_URI, uri);
    }

    private Uri getIconUri() {
        return getVariable(ICON_URI);
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

        if (getIconUri() != null) {
            updated = updateIcon(game);
        }

        sendResult(updated);
    }

    private boolean updateIcon(Game game) {
        File path = copyIcon();
        if (path == null) {
            return false;
        }

        if (game.getIconPath() != null) {
            new File(game.getIconPath()).delete();
        }

        game.setIconPath(path.getAbsolutePath());

        return true;
    }

    private File copyIcon() {
        File icon = new File(getIconFolder(getContext()), newRandomString(8));
        try (BufferedInputStream inputStream = new BufferedInputStream(
                getContext()
                        .getContentResolver()
                        .openInputStream(getIconUri()));
             BufferedOutputStream outputStream = new BufferedOutputStream(
                     new FileOutputStream(icon))) {
            writeAllTo(inputStream, outputStream);
            return icon;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
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
