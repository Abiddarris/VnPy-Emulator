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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.abiddarris.common.android.tasks.v2.IndeterminateTask;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.ProgressPublisher;
import com.abiddarris.vnpyemulator.patches.IncompatiblePatchStrategy;
import com.abiddarris.vnpyemulator.patches.PatchSource;
import com.abiddarris.vnpyemulator.patches.Patcher;

public class PatchGameTask extends IndeterminateTask<Game> {

    private final Game game;

    public PatchGameTask(Game game) {
        this.game = game;
    }

    @Override
    public void execute() throws Exception {
        setTitle(R.string.apply_patch_dialog_title);

        Patcher patcher = PatchSource.getPatcher(game.getPatchVersion());
        if (!PatchSource.isInstalled(patcher)) {
            setMessage(getString(R.string.downloading_patch_message, patcher.getPatch().getName(), patcher.getVersion()));
            PatchSource.download(patcher, new ProgressPublisher() {
                @Override
                public void incrementProgress(int progress) {
                }

                @Override
                public void setMaxProgress(int maxProgress) {
                }
            });
        }

        setMessage(R.string.patching);

        if (PatchSource.apply(patcher, game.getGamePath(), file ->
                IncompatiblePatchDialog.newInstance(file.getName())
                        .showForResultAndBlock(getFragmentManager())
        )) {
            GameLoader.addGame(getContext(), game);
            GameLoader.saveGames(getContext());
        }

        setResult(game);
    }

    private FragmentManager getFragmentManager() {
        return ((AppCompatActivity)getContext()).getSupportFragmentManager();
    }
}
