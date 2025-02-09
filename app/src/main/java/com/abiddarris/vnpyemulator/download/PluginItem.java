package com.abiddarris.vnpyemulator.download;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.abiddarris.vnpyemulator.plugins.Plugin;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.viewbinding.BindableItem;

public class PluginItem extends BindableItem<LayoutPluginBinding> {

    private final Plugin plugin;

    public PluginItem(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getLayout() {
        return R.layout.layout_plugin;
    }

    @NonNull
    @Override
    protected LayoutPluginBinding initializeViewBinding(@NonNull View view) {
        return LayoutPluginBinding.bind(view);
    }

    @Override
    public void bind(@NonNull LayoutPluginBinding viewBinding, int position) {
        viewBinding.version.setText(String.format("%s (%s)", plugin.getVersion(), plugin.getAbi()));
    }
}
