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
package com.abiddarris.vnpyemulator.download;

import static com.abiddarris.common.utils.Randoms.randomInt;
import static com.abiddarris.vnpyemulator.utils.Notifications.DOWNLOAD_CHANNEL_ID;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.abiddarris.common.android.handlers.MainThreads;
import com.abiddarris.common.android.tasks.v2.DeterminateProgress;
import com.abiddarris.common.android.tasks.v2.DeterminateTask;
import com.abiddarris.common.android.tasks.v2.Task;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.plugins.Plugin;

public class DownloadPluginTask extends DeterminateTask<Void> implements ProgressPublisher {

    private final Context context;
    private final Plugin plugin;
    private int progress;

    public DownloadPluginTask(Context context, Plugin plugin) {
        this.context = context;
        this.plugin = plugin;
    }

    @Override
    public void execute() throws Exception {
        setTitle(context.getString(R.string.plugin_downloader));
        setMessage(context.getString(R.string.downloading_plugin, plugin.getFile()));

        plugin.downloadPlugin(context,this);
        plugin.downloadPrivateFiles(context, this);

        setMessage(context.getString(R.string.downloaded));
    }

    @Override
    public void incrementProgress(int progress) {
        this.progress += progress;
        setProgress(this.progress);
    }

    @Override
    public void setMaxProgress(int maxProgress) {
        setMaxProgress((long)maxProgress);
    }

}
