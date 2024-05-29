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
package com.abiddarris.vnpyemulator.adapters;

import android.content.Intent;
import com.abiddarris.vnpyemulator.files.Files;
import static com.abiddarris.vnpyemulator.games.Game.*;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.adapters.GameAdapter.GameViewHolder;
import com.abiddarris.vnpyemulator.databinding.LayoutGameBinding;
import com.abiddarris.vnpyemulator.dialogs.DialogUtils;
import com.abiddarris.vnpyemulator.games.Game;
import com.abiddarris.vnpyemulator.pythons.FetchPythonTask;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import org.renpy.android.PythonSDLActivity;

public class GameAdapter extends Adapter<GameViewHolder> {

    private AppCompatActivity context;
    private List<Game> games;
    private LayoutInflater inflater;
    
    public GameAdapter(AppCompatActivity context) {
    	this.context = context;
        
        inflater = LayoutInflater.from(context);
        refresh();
    }
    
    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup group, int type) {
        return new GameViewHolder(LayoutGameBinding.inflate(inflater, group, false));
    }
    
    @Override
    public void onBindViewHolder(GameViewHolder holder, int index) {
        Game game = games.get(index);
        holder.binding.root
            .setOnClickListener(v -> open(game));
        
        try {
            holder.binding.gameName.setText(
                game.getString(GAME_NAME));
            holder.binding.renpyVersion.setText(
                game.getString(RENPY_VERSION));
        } catch (JSONException e) {
            e.printStackTrace();
            
            holder.binding.gameName.setText(R.string.error);
            holder.binding.renpyVersion.setText(R.string.error);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
    
    public void refresh() {
    	this.games = Game.loadGames(context);
    }
    
    private void open(Game game) {
        String pythonVersion = game.optString(PYTHON_VERSION, null);
        if(pythonVersion != null) {
            var gamePath = game.getGamePath();
            copyGameMainScript(gamePath, game.getGameScript());
            
            var intent = new Intent(context, PythonSDLActivity.class)
                .putExtra(PythonSDLActivity.GAME_PATH, gamePath)
                .putExtra(PythonSDLActivity.PYTHON_PATH, new File(
                    Files.getPythonFolders(context), pythonVersion).getAbsolutePath());
            
            context.startActivity(intent);
            
            return;
        }
        
        DialogUtils.runTask(context.getSupportFragmentManager(),
             context.getString(R.string.fetching_python_title), false, 
             new FetchPythonTask(game));
    }
    
    private void copyGameMainScript(String gamePath, String scriptName) {
    	try {
            var src = new File(gamePath, scriptName);
            var dest = new File(gamePath, "main.py");
            
            var inputStream = new BufferedInputStream(new FileInputStream(src));
            var outputStream = new BufferedOutputStream(new FileOutputStream(dest));
            var buf = new byte[8192];
            int len;
            while((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static class GameViewHolder extends ViewHolder {
        
        private LayoutGameBinding binding;
        
        public GameViewHolder(LayoutGameBinding binding) {
            super(binding.getRoot());
            
            this.binding = binding;
        }
        
    }
}
