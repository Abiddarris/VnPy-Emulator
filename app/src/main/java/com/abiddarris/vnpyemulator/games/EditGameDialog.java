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

import static com.abiddarris.common.android.utils.TextListener.newTextListener;
import static com.abiddarris.common.files.Files.getPathName;
import static com.abiddarris.common.stream.InputStreams.writeAllTo;
import static com.abiddarris.common.utils.Randoms.newRandomString;
import static com.abiddarris.vnpyemulator.files.Files.getIconFolder;
import static com.abiddarris.vnpyemulator.games.GameLoader.getGames;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument;
import androidx.annotation.NonNull;

import com.abiddarris.common.android.dialogs.BaseDialogFragment;
import com.abiddarris.common.android.dialogs.ExceptionDialog;
import com.abiddarris.common.android.tasks.v2.IndeterminateProgress;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.common.android.tasks.v2.dialog.IndeterminateDialogProgressPublisher;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.DialogEditGameBinding;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditGameDialog extends BaseDialogFragment<Boolean> {

    private static final String GAME = "game";
    private static final String ICON_URI = "icon_uri";
    private static final String MAIN_SCRIPT_CANDIDATES = "mainScriptCandidates";
    private static final String PATCH_VERSIONS = "patchVersions";
    private static final String PATCH_VERSION = "patchVersion";
    private static final String PLUGIN_VERSIONS = "pluginVersions";
    private static final String PLUGIN_VERSION = "pluginVersion";

    private ActivityResultLauncher<String[]> selectImageLauncher;
    private DialogEditGameBinding ui;
    private List<String> disallowedNames;

    public static EditGameDialog editGame(Game game, Plugin[] pluginVersions, Plugin pluginVersion) {
        return editGame(game, null, null,
                null, pluginVersions, pluginVersion);
    }

    public static EditGameDialog editGame(Game game, File[] mainScriptCandidates,
                                          String[] patchVersions, String patchVersion,
                                          Plugin[] pluginVersions, Plugin pluginVersion) {
        EditGameDialog dialog = new EditGameDialog();
        dialog.saveVariable(GAME, game);
        dialog.saveVariable(MAIN_SCRIPT_CANDIDATES, mainScriptCandidates);
        dialog.saveVariable(PATCH_VERSIONS, patchVersions);
        dialog.saveVariable(PATCH_VERSION, patchVersion);
        dialog.saveVariable(PLUGIN_VERSIONS, pluginVersions);
        dialog.saveVariable(PLUGIN_VERSION, pluginVersion);

        return dialog;
    }

    @Override
    protected Boolean getDefaultResult() {
        return false;
    }

    @Override
    protected void onCreateDialog(MaterialAlertDialogBuilder builder, Bundle savedInstanceState) {
        super.onCreateDialog(builder, savedInstanceState);

        selectImageLauncher = registerForActivityResult(new OpenDocument(), this::onImageSelected);

        initInvalidNames();

        Game game = getGame();

        ui = DialogEditGameBinding.inflate(getLayoutInflater());
        ui.name.setText(game.getName());
        ui.name.addTextChangedListener(newTextListener(editable -> validate()));
        ui.card.setOnClickListener(v -> selectImageLauncher.launch(new String[] {"image/*"}));

        OnItemClickListener itemClickListener = (ignored, ignored1, ignored2, ignored3) -> validate();
        if (getMainScriptsCandidate() != null) {
            ui.mainGameScripts.setVisibility(View.VISIBLE);

            MaterialAutoCompleteTextView textView = (MaterialAutoCompleteTextView) ui.mainGameScripts.getEditText();
            textView.setOnItemClickListener(itemClickListener);
            textView.setSimpleItems(Arrays.asList(getMainScriptsCandidate())
                    .stream()
                    .map(File::getName)
                    .toArray(String[]::new));
        }

        if (getPatchVersions() != null) {
            setCancelable(false);
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});

            ui.patches.setVisibility(View.VISIBLE);

            MaterialAutoCompleteTextView textView = (MaterialAutoCompleteTextView) ui.patches.getEditText();
            textView.setOnItemClickListener(itemClickListener);
            textView.setAdapter(new PatchSpinnerAdapter(requireContext(), textView, getPatchVersions()));
            textView.setText(getPatchVersion(), false);
        }

        MaterialAutoCompleteTextView textView = (MaterialAutoCompleteTextView) ui.plugins.getEditText();
        textView.setOnItemClickListener(itemClickListener);
        textView.setAdapter(new PluginSpinnerAdapter(requireContext(), textView, getPluginVersions()));

        Plugin pluginVersion = getPluginVersion();
        if (pluginVersion != null) {
            textView.setText(pluginVersion.toStringWithoutAbi(), false);
        }

        Glide.with(this)
                .load(game.getIconPath())
                .fallback(R.drawable.ic_launcher)
                .into(ui.icon);

        builder.setTitle(R.string.edit_game)
                .setView(ui.getRoot())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> onPositiveButtonClicked(game));

        validate();
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
        String name = getStringFromTextLayout(ui.nameLayout);

        if (!name.equals(game.getName())) {
            updated = true;
            game.setName(name);
        }

        if (getIconUri() != null) {
            updated |= updateIcon(game);
        }

        String pluginStr = getStringFromTextLayout(ui.plugins);
        if (!pluginStr.equals(game.getPlugin())) {
            Plugin plugin = Arrays.asList(getPluginVersions())
                    .stream()
                    .filter(p -> p.toStringWithoutAbi().equals(pluginStr))
                    .findFirst()
                    .get();
            game.setPlugin(plugin.toStringWithoutAbi());
            game.setRenPyPrivateVersion(plugin.getPrivateFilesName());

            try {
                GameLoader.saveGames(requireContext());
            } catch (IOException e) {
                ExceptionDialog.showExceptionDialog(getParentFragmentManager(), e);
            }
        }

        if (getPatchVersions() == null) {
            sendResult(updated);
            return;
        }

        game.setPatchVersion(getStringFromTextLayout(ui.patches));

        if (getMainScriptsCandidate() != null) {
            game.setGameScript(getStringFromTextLayout(ui.mainGameScripts));
        }

        GameListFragment.GameListViewModel taskModel = ((GameListFragment) getParentFragment())
                .getTaskModel();
        IndeterminateDialogProgressPublisher publisher = new IndeterminateDialogProgressPublisher("PatchGameDialog");
        taskModel.getDialogManager().registerPublisher(publisher);

        TaskInfo<IndeterminateProgress, Game> info = taskModel.getTaskManager()
                .execute(new PatchGameTask(game), publisher);
        info.addOnTaskExecuted(_game -> taskModel.notifyNewGame(_game));
        sendResult(true);
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

    private @NonNull String getStringFromTextLayout(TextInputLayout layout) {
        return layout.getEditText().getText().toString();
    }

    private File copyIcon() {
        File icon = new File(getIconFolder(getContext()), newRandomString(8));
        try (BufferedInputStream inputStream = new BufferedInputStream(
                requireContext()
                        .getContentResolver()
                        .openInputStream(getIconUri()));
             BufferedOutputStream outputStream = new BufferedOutputStream(
                     new FileOutputStream(icon))) {
            writeAllTo(inputStream, outputStream);
            return icon;
        } catch (IOException e) {
            ExceptionDialog.showExceptionDialog(getParentFragmentManager(), e);

            return null;
        }
    }

    private void validate() {
        boolean valid = isNameValid() && isSpinnerValid(ui.plugins);
        if (getMainScriptsCandidate() != null) {
            valid &= isSpinnerValid(ui.mainGameScripts);
        }

        if (getPatchVersions() != null) {
            valid &= isSpinnerValid(ui.patches);
        }

        enablePositiveButton(valid);
    }

    private boolean isSpinnerValid(TextInputLayout layout) {
        MaterialAutoCompleteTextView editText = (MaterialAutoCompleteTextView) layout.getEditText();
        return editText.getText().length() != 0;
    }

    private boolean isNameValid() {
        String string = ui.name.getText().toString();
        boolean invalid = false;
        String message = null;

        if(string.isBlank()) {
            invalid = true;
            message = getString(R.string.name_cannot_be_blank);
        } else if(disallowedNames.contains(string) && !string.equals(getGame().getName())) {
            invalid = true;
            message = getString(R.string.name_already_used);
        }

        boolean error = ui.nameLayout.isErrorEnabled();
        if(error == invalid) return !invalid;

        ui.nameLayout.setErrorEnabled(invalid);
        ui.nameLayout.setError(message);

        return !invalid;
    }

    private Game getGame() {
        return getVariable(GAME);
    }

    private Plugin getPluginVersion() {
        return getVariable(PLUGIN_VERSION, null);
    }

    private Plugin[] getPluginVersions() {
        return getVariable(PLUGIN_VERSIONS, null);
    }

    private String getPatchVersion() {
        return getVariable(PATCH_VERSION, null);
    }

    private String[] getPatchVersions() {
        return getVariable(PATCH_VERSIONS, null);
    }

    private File[] getMainScriptsCandidate() {
        return getVariable(MAIN_SCRIPT_CANDIDATES, null);
    }
}
