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

import com.abiddarris.common.utils.BaseRunnable;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.plugins.Plugin;

public class DownloadPluginRunnable implements BaseRunnable, ProgressPublisher {

    private final Context context;
    private final Plugin plugin;
    private final NotificationCompat.Builder builder;
    private final NotificationManagerCompat notificationManager;
    private final int id;
    private int maxProgress;
    private int progress;

    public DownloadPluginRunnable(Context context, Plugin plugin) {
        this.context = context;
        this.plugin = plugin;

        notificationManager = NotificationManagerCompat.from(context);
        id = randomInt(Integer.MAX_VALUE);

        builder = new NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID);
        builder.setContentTitle(context.getString(R.string.plugin_downloader))
                .setContentText(context.getString(R.string.downloading_plugin, plugin.getFile()))
                .setSmallIcon(R.drawable.ic_download)
                .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    @Override
    public void execute() throws Exception {
        plugin.downloadPlugin(context,this);
        plugin.downloadPrivateFiles(context, this);

        complete();
    }

    @Override
    public void incrementProgress(int progress) {
        this.progress += progress;

        builder.setProgress(maxProgress, this.progress, false);

        updateNotification();
    }

    @Override
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;

        builder.setProgress(this.maxProgress, progress, false);

        updateNotification();
    }

    private void complete() {
        builder.setContentText(context.getString(R.string.downloaded))
                .setProgress(0,0,false);
        updateNotification();
    }

    private void updateNotification() {
        notificationManager.notify(id, builder.build());
    }
}
