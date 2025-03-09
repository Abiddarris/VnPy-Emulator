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

import static com.abiddarris.common.android.pm.Packages.isAllowedToInstallPackage;
import static com.abiddarris.vnpyemulator.utils.Notifications.DOWNLOAD_CHANNEL_ID;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.abiddarris.common.android.pm.Packages;
import com.abiddarris.common.android.tasks.v2.DeterminateProgress;
import com.abiddarris.common.android.tasks.v2.TaskInfo;
import com.abiddarris.common.android.tasks.v2.TaskManager;
import com.abiddarris.common.android.tasks.v2.notifications.DeterminateNotificationProgressPublisher;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.patch.DownloadPatchTask;
import com.abiddarris.vnpyemulator.patches.Patcher;
import com.abiddarris.vnpyemulator.plugins.Plugin;

import java.io.IOException;

public class DownloadService extends Service {

    private final TaskManager taskManager = new TaskManager(this);

    private boolean paused;
    private DownloadServiceBinder binder;
    private DownloadFragment downloadFragment;
    private Plugin plugin;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new DownloadServiceBinder();
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        downloadFragment = null;

        return super.onUnbind(intent);
    }

    public void downloadPlugin(Plugin plugin) {
        var publisher = new DeterminateNotificationProgressPublisher(createDefaultNotification(), this);

        TaskInfo<DeterminateProgress, Void> taskInfo = taskManager.execute(new DownloadPluginTask(this, plugin), publisher);
        taskInfo.addOnTaskExecuted(ignored -> {
            if (!isAllowedToInstallPackage(this) && downloadFragment == null) {
                return;
            }

            this.plugin = plugin;
            if (!isAllowedToInstallPackage(this)) {
                downloadFragment.requestPackagePermission();
                return;
            }

            continueInstall();
        });
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

    public void attachFragment(DownloadFragment downloadFragment) {
        this.downloadFragment = downloadFragment;
    }

    public void continueInstall() {
        try {
            Packages.installPackage(this, plugin.getPluginApk(this));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class DownloadServiceBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
