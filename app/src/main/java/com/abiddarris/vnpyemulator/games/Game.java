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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that hold {@code Game} information and managed it
 */
public class Game extends JSONObject {
    
    public static final String GAME_FOLDER_PATH = "folder_path";
    public static final String GAME_NAME = "name";
    public static final String GAME_SCRIPT = "script";
    
    Game(JSONObject object) throws JSONException {
        super(object.toString());
    }
        
    public Game() {}
    
    public static void storeGame(Context context, Game game) throws IOException {
        var gameFile = getGameFile(context);
        var games = loadGames(context);
        
        saveGames(context, games);
    }
    
    public static List<Game> loadGames(Context context) {
        var list = new LinkedList<Game>();
        File gameFile = getGameFile(context);
        try (BufferedReader reader = new BufferedReader(new FileReader(gameFile))){
            char[] cbuf = new char[(int)gameFile.length()];
            
            reader.read(cbuf);
            
            var array = new JSONArray(new String(cbuf));
            for(int i = 0; i < array.length(); ++i) {
            	list.add(new Game(
                        array.getJSONObject(0)));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static void saveGames(Context context, List<Game> games) throws IOException {
    	var array = new JSONArray();
            
        games.forEach(array::put);
        try (var writer = new BufferedWriter(new FileWriter(getGameFile(context)))) {
            writer.write(array.toString());
            writer.flush();
    	} 
    }
    
    /**
     * Returns {@code File} where information about games are stored.
     *
     * @param context Context
     * @return {@code File} where information about games are stored.
     */
    private static File getGameFile(Context context) {
        return new File(context.getFilesDir(), "game");
    }
}
