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
import com.abiddarris.common.utils.Preconditions;
import com.abiddarris.vnpyemulator.files.Files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public final class GameLoader {
    
    private static List<Game> games;
    
    /**
     * Returns {@code List} of {@code Game}s
     *
     * <p>This method will load games if it never loaded before,
     * Otherwise it will return previous load result
     *
     * @param context Context
     * @throws NullPointerException if {@code context} is null
     * @return {@code List} of {@code Game}s
     */
    public static List<Game> getGames(Context context) throws IOException {
        Preconditions.checkNonNull(context, "Context cannot be null");
        if(games == null) {
            loadGames(context);
        }
        return Collections.unmodifiableList(games);
    }
    
    /**
     * Load games from file
     *
     * @param context Context
     * @throws NullPointerException if {@code context} is null
     * @throws IOException When I/O operation failed
     * @return {@code List} of {@code Game}s
     */
    public static List<Game> loadGames(Context context) throws IOException {
        Preconditions.checkNonNull(context, "Context cannot be null");
        if(games == null) {
            games = new ArrayList<>();
        } else {
            games.clear();
        }
        
        File gameFile = Files.getGamesFile(context);
        if(!gameFile.exists()) {
            return games;
        }
        
        char[] cbuf = new char[(int)gameFile.length()];
        try (BufferedReader reader = new BufferedReader(new FileReader(gameFile))){
            reader.read(cbuf);
            
            var array = new JSONArray(new String(cbuf));
            for(int i = 0; i < array.length(); ++i) {
            	games.add(new Game(
                        array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new IOException("Error while loading games", e);
        }
        return Collections.unmodifiableList(games);
    }
    
    /**
     * Save games. 
     * Do nothing if {@link #loadGames(Context)} is never called
     *
     * @param context Context
     * @throws NullPointerException if {@code context} is null
     * @throws IOException When I/O operation failed
     */
    public static void saveGames(Context context) throws IOException {
        Preconditions.checkNonNull(context, "Context cannot be null");
        if(games == null) {
            return;
        }
        
        var array = new JSONArray();
            
        games.forEach(array::put);
        try (var writer = new BufferedWriter(new FileWriter(Files.getGamesFile(context)))) {
            writer.write(array.toString());
            writer.flush();
    	} 
    }
    
    /**
     * Add new game.
     * Call {@link #loadGames(Context)} if games is not loaded
     *
     * @param context Context
     * @throws NullPointerException if {@code context} is null
     * @throws IOException When I/O operation failed
     */
    public static void addGame(Context context, Game game) throws IOException {
        Preconditions.checkNonNull(game, "Game cannot be null");
        getGames(context);
        
        games.add(game);
    }
}
