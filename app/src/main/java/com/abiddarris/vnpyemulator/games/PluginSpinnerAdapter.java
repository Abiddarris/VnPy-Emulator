package com.abiddarris.vnpyemulator.games;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abiddarris.plugin.PluginLoader;
import com.abiddarris.plugin.PluginName;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginForSpinnerBinding;
import com.abiddarris.vnpyemulator.plugins.Plugin;

import java.util.Arrays;

public class PluginSpinnerAdapter extends ArrayAdapter<String> {

    public PluginSpinnerAdapter(@NonNull Context context, @NonNull Plugin[] objects) {
        super(context, R.layout.layout_plugin_for_spinner, R.id.text,
                Arrays.asList(objects)
                .stream()
                .map(Plugin::toStringWithoutAbi)
                .distinct()
                .toArray(String[]::new));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        LayoutPluginForSpinnerBinding ui = LayoutPluginForSpinnerBinding.bind(view);
        PluginName name = new PluginName(getItem(position));

        ui.imageView.setVisibility(PluginLoader.hasPluginWithExactInternalVersion(getContext(), name) ? View.GONE : View.VISIBLE);

        return view;
    }
}
