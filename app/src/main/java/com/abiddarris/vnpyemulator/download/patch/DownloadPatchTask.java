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
package com.abiddarris.vnpyemulator.download.patch;

import com.abiddarris.common.android.tasks.v2.DeterminateProgress;
import com.abiddarris.common.android.tasks.v2.DeterminateTask;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.ProgressPublisher;
import com.abiddarris.vnpyemulator.patches.Patcher;

public class DownloadPatchTask extends DeterminateTask<DeterminateProgress> implements ProgressPublisher {

    private final Patcher patcher;

    public DownloadPatchTask(Patcher patcher) {
        this.patcher = patcher;
    }

    @Override
    public void execute() throws Exception {
        setTitle("Downloading Patch");
        setMessage(getString(R.string.downloading_patch_message, patcher.getPatch().getName(), patcher.getVersion()));

        patcher.download(getContext(), this);

        setMessage(getString(R.string.downloaded));
    }

    @Override
    public void incrementProgress(int progress) {
        setProgress(getProgress().getProgress() + progress);
    }

    @Override
    public void setMaxProgress(int maxProgress) {
        setMaxProgress((long)maxProgress);
    }
}
