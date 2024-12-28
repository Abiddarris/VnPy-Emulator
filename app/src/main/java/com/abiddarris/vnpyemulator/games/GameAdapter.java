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

import static com.abiddarris.vnpyemulator.files.Files.getKeyboardFolder;
import static com.abiddarris.vnpyemulator.games.Game.*;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.abiddarris.plugin.PluginArguments;
import com.abiddarris.plugin.PluginLoader;
import com.abiddarris.vnpyemulator.MainActivity;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.games.GameAdapter.GameViewHolder;
import com.abiddarris.vnpyemulator.databinding.LayoutGameBinding;
import com.abiddarris.vnpyemulator.plugins.FetchPluginsRunnable;
import com.abiddarris.vnpyemulator.renpy.RenPyPrivate;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.List;

public class GameAdapter extends Adapter<GameViewHolder> {

    private Fragment fragment;
    private List<Game> games;
    private LayoutInflater inflater;
    
    public GameAdapter(Fragment fragment) {
    	this.fragment = fragment;
        
        inflater = fragment.getLayoutInflater();
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
            .setOnClickListener(v -> open(game));
        holder.binding.getRoot()
            .setTag(index);
        
        try {
            String renpyVersion = game.getRenPyVersion();
            
            holder.binding.gameName.setText(
                game.getString(GAME_NAME));
            holder.binding.renpyVersion.setText(
                renpyVersion != null ? renpyVersion : fragment.getString(R.string.unknown));

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
    
    public void open(Game game) {
        String plugin = game.getPlugin();
        String renpyPrivateVersion = game.getRenPyPrivateVersion();
        if(plugin == null || !PluginLoader.hasPlugin(fragment.getContext(), plugin) || !RenPyPrivate.hasPrivateFiles(fragment.getContext(), renpyPrivateVersion)) {
            ((GameListFragment)fragment).getTaskModel()
                .execute(new FetchPluginsRunnable(game));
            return;
        }
        String renpyPrivateVersionPath = RenPyPrivate.getPrivateFiles(fragment.getContext(), renpyPrivateVersion)
                .getAbsolutePath();
        
        MainActivity activity = (MainActivity) fragment.getActivity();
        
        var intent = PluginLoader.getIntentForPlugin(plugin, new PluginArguments()
            .setRenPyPrivatePath(renpyPrivateVersionPath)
            .setGamePath(game.getGamePath())
            .setGameScript(game.getGameScript())
            .setErrorPort(activity.getPort())
            .setKeyboardFolderPath(getKeyboardFolder(fragment.getContext()).getAbsolutePath()));
        
        fragment.startActivity(intent);
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
