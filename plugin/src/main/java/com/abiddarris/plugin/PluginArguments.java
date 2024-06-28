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

import static com.abiddarris.plugin.PluginLoader.ERROR_PORT;
import static com.abiddarris.plugin.PluginLoader.KEYBOARD_FOLDER_PATH;

import android.content.Intent;
import android.os.Bundle;

public class PluginArguments {

    private String gamePath;
    private String gameScript;
    private String renpyPrivatePath;
    private String keyboardFolderPath;
    private int errorPort;

    public PluginArguments() {}

    public PluginArguments(Intent intent) {
        Bundle bundle = intent.getExtras();

        gamePath = bundle.getString(PluginLoader.GAME_PATH);
        renpyPrivatePath = bundle.getString(PluginLoader.RENPY_PRIVATE_PATH);
        gameScript = bundle.getString(PluginLoader.GAME_SCRIPT);
        keyboardFolderPath = bundle.getString(KEYBOARD_FOLDER_PATH);
        errorPort = bundle.getInt(ERROR_PORT);
    }

    public String getGamePath() {
        return this.gamePath;
    }

    public String getRenpyPrivatePath() {
        return this.renpyPrivatePath;
    }

    public String getGameScript() {
        return gameScript;
    }

    public String getKeyboardFolderPath() {
        return keyboardFolderPath;
    }
    
    public int getErrorPort() {
        return this.errorPort;
    }

    public PluginArguments setGamePath(String gamePath) {
        this.gamePath = gamePath;

        return this;
    }

    public PluginArguments setRenPyPrivatePath(String renPyPrivatePath) {
        this.renpyPrivatePath = renPyPrivatePath;

        return this;
    }

    public PluginArguments setGameScript(String gameScript) {
        this.gameScript = gameScript;

        return this;
    }

    public PluginArguments setKeyboardFolderPath(String keyboardFolderPath) {
        this.keyboardFolderPath = keyboardFolderPath;

        return this;
    }

    public PluginArguments setErrorPort(int errorPort) {
        this.errorPort = errorPort;
        
        return this;
    }
}
