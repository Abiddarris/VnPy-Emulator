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

import android.content.Context;

import com.abiddarris.common.utils.Exceptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that hold {@code Game} information and managed it
 */
public class Game extends JSONObject {
    
    @Deprecated
    public static final String GAME_FOLDER_PATH = "folder_path";
   
    @Deprecated
    public static final String GAME_NAME = "name";
    
    @Deprecated
    public static final String GAME_SCRIPT = "script";
   
    private static final String RENPY_VERSION = "real_renpy_version";
    private static final String PATCH_VERSION = "renpy_version";
    private static final String PLUGIN_VERSION = "plugin_version";
    private static final String RENPY_PRIVATE_VERSION = "renpy_private_version";
    
    Game(JSONObject object) throws JSONException {
        this(object.toString());
    }
    
    public Game(String json) throws JSONException {
        super(json);
    }
        
    public Game() {}
    
    public String getName() {
    	return optString(GAME_NAME, null);
    }
    
    public String getGamePath() {
    	return optString(GAME_FOLDER_PATH, null);
    }
    
    public String getGameScript() {
    	return optString(GAME_SCRIPT, null);
    }
    
    public String getPlugin() {
        return optString(PLUGIN_VERSION, null);
    }
    
    public String getRenPyVersion() {
        return optString(RENPY_VERSION, null);
    }
    
    public String getPatchVersion() {
        return optString(PATCH_VERSION, null);
    }
    
    public String getRenPyPrivateVersion() {
        return optString(RENPY_PRIVATE_VERSION, null);
    }
    
    public void setRenPyPrivateVersion(String version) {
        try {
            putOpt(RENPY_PRIVATE_VERSION, version);
        } catch (JSONException e) {
            throw Exceptions.toUncheckException(e);
        }
    }
    
    public void setPlugin(String version) {
        try {
            putOpt(PLUGIN_VERSION, version);
        } catch (JSONException e) {
            throw Exceptions.toUncheckException(e);
        }
    }

    public void setName(String name) {
        set(GAME_NAME, name);
    }

    public void setRenPyVersion(String version) {
        set(RENPY_VERSION, version);
    }

    public void setPatchVersion(String patchVersion) {
        set(PATCH_VERSION, patchVersion);
    }

    private void set(String key, String value) {
        try {
            putOpt(key, value);
        } catch (JSONException e) {
            throw Exceptions.toUncheckException(e);
        }
    }

    @Deprecated
    public static void updateGame(Context context, Game game) throws IOException {
        GameLoader.getGames(context);
        GameLoader.saveGames(context);
    }

    @Deprecated
    public static void storeGame(Context context, Game game) throws IOException {
        GameLoader.addGame(context, game);
        GameLoader.saveGames(context);
    }

    @Deprecated
    public static List<Game> loadGames(Context context) {
        try {
            return GameLoader.loadGames(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public static void saveGames(Context context, List<Game> games) throws IOException {
    	GameLoader.saveGames(context);
    }

}
