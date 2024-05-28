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
import java.util.List;
import org.json.JSONException;

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
            return;
        }
        
        DialogUtils.runTask(context.getSupportFragmentManager(),
             context.getString(R.string.fetching_python_title), false, new FetchPythonTask());
    }
    
    public static class GameViewHolder extends ViewHolder {
        
        private LayoutGameBinding binding;
        
        public GameViewHolder(LayoutGameBinding binding) {
            super(binding.getRoot());
            
            this.binding = binding;
        }
        
    }
}
