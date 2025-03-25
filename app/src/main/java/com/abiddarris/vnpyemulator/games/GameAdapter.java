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

import static com.abiddarris.vnpyemulator.games.Game.GAME_NAME;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutGameBinding;
import com.abiddarris.vnpyemulator.games.GameAdapter.GameViewHolder;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.File;
import java.util.List;

public class GameAdapter extends Adapter<GameViewHolder> {

    private final GameListFragment fragment;
    private final LayoutInflater inflater;
    private List<Game> games;

    public GameAdapter(GameListFragment fragment) {
    	this.fragment = fragment;
        this.inflater = fragment.getLayoutInflater();

        refresh();
    }
    
    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup group, int type) {
        var holder = new GameViewHolder(LayoutGameBinding.inflate(inflater, group, false));
        
        fragment.registerForContextMenu(holder.binding.getRoot());
        
        return holder;
    }
    
    @Override
    public void onBindViewHolder(GameViewHolder holder, int index) {
        Game game = games.get(index);
        holder.binding.root
            .setOnClickListener(v -> fragment.open(game));
        holder.binding.getRoot()
            .setTag(index);
        
        try {
            String renpyVersion = game.getRenPyVersion();
            
            holder.binding.gameName.setText(
                game.getString(GAME_NAME));
            holder.binding.renpyVersion.setText(
                renpyVersion != null ? renpyVersion : fragment.getString(R.string.unknown));

            holder.binding.root.setCardBackgroundColor(getColor(game));

            Glide.with(holder.binding.getRoot())
                    .load(game.getIconPath())
                    .fallback(R.drawable.ic_launcher)
                    .into(holder.binding.icon);
        } catch (JSONException e) {
            e.printStackTrace();

            holder.binding.gameName.setText(R.string.error);
            holder.binding.renpyVersion.setText(R.string.error);
        }
    }

    private int getColor(Game game) {
        int res = new File(game.getGamePath()).exists() ? R.attr.colorSurfaceVariant : R.attr.colorOnError;
        TypedValue value = new TypedValue();

        fragment.getActivity()
                .getTheme()
                .resolveAttribute(res, value, true);
        return value.data;
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
    
    public Game get(int index) {
        return games.get(index);
    }
    
    public void refresh() {
    	this.games = Game.loadGames(fragment.getContext());
    }

    public void notifyGameModified(Game game) {
        notifyItemChanged(games.indexOf(game));
    }

    public static class GameViewHolder extends ViewHolder {
        
        private LayoutGameBinding binding;
        
        public GameViewHolder(LayoutGameBinding binding) {
            super(binding.getRoot());
            
            this.binding = binding;
        }
        
    }

}
