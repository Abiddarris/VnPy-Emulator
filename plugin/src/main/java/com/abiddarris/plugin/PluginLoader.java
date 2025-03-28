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
package com.abiddarris.plugin;

import static androidx.core.content.pm.PackageInfoCompat.getLongVersionCode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.abiddarris.common.android.pm.Packages;

public class PluginLoader {
    
    public static final String GAME_PATH = "game_path";
    public static final String RENPY_PRIVATE_PATH = "renpy_private_path";
    public static final String GAME_SCRIPT = "game_script";
    public static final String KEYBOARD_FOLDER_PATH = "keyboard_folder_path";
    public static final String ERROR_PORT = "error_port";

    public static long getPluginInternalVersion(Context context, String version) {
        PackageManager manager = context.getPackageManager();
        try {
            return getLongVersionCode(manager.getPackageInfo(getPackage(version), 0));
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static boolean hasPlugin(Context context, PluginName name) {
        return Packages.isInstalled(context, getPackage(name.getVersion()));
    }
    
    public static String getPackage(String version) {
    	return String.format("com.abiddarris.renpy.plugin%s", version.replace(".", ""));
    }
    
    public static Intent getIntentForPlugin(String plugin, PluginArguments argument) {
        String packageName = getPackage(plugin);
        
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(packageName, packageName + ".MainActivity"));
        intent.putExtra(RENPY_PRIVATE_PATH, argument.getRenpyPrivatePath());
        intent.putExtra(GAME_PATH, argument.getGamePath());
        intent.putExtra(GAME_SCRIPT, argument.getGameScript());
        intent.putExtra(KEYBOARD_FOLDER_PATH, argument.getKeyboardFolderPath());
        intent.putExtra(ERROR_PORT, argument.getErrorPort());
        
        return intent;
    }

    public static boolean hasPluginWithExactInternalVersion(Context context, PluginName name) {
        if (!hasPlugin(context, name)) {
            return false;
        }

        return getPluginInternalVersion(context, name.getVersion())
                == Integer.parseInt(name.getPluginInternalVersion());
    }
}
