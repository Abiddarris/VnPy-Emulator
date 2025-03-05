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

import static com.abiddarris.vnpyemulator.utils.Notifications.DOWNLOAD_CHANNEL_ID;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.abiddarris.common.android.tasks.v2.DeterminateNotificationProgressPublisher;
import com.abiddarris.common.android.tasks.v2.TaskManager;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.patch.DownloadPatchTask;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.abiddarris.vnpyemulator.plugins.Plugin;

public class DownloadService extends Service {

    private final TaskManager taskManager = new TaskManager(this);

    private DownloadServiceBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new DownloadServiceBinder();
        }
        return binder;
    }

    public void downloadPlugin(Plugin plugin) {
        var publisher = new DeterminateNotificationProgressPublisher(createDefaultNotification(), this);

        taskManager.execute(new DownloadPluginTask(this, plugin), publisher);
    }

    public void downloadPatcher(Patcher patcher) {
        taskManager.execute(
                new DownloadPatchTask(patcher),
                new DeterminateNotificationProgressPublisher(createDefaultNotification(), this)
        );
    }

    private NotificationCompat.Builder createDefaultNotification() {
        return new NotificationCompat.Builder(this, DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download)
                .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    public class DownloadServiceBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
