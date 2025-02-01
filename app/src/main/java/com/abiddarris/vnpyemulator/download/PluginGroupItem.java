package com.abiddarris.vnpyemulator.download;

import android.view.View;

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginGroupBinding;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.ExpandableItem;
import com.xwray.groupie.viewbinding.BindableItem;

public class PluginGroupItem extends BindableItem<LayoutPluginGroupBinding> implements ExpandableItem {

    private PluginGroup group;
    private ExpandableGroup onToggleListener;

    public PluginGroupItem(PluginGroup group) {
        this.group = group;
    }

    @NonNull
    @Override
    protected LayoutPluginGroupBinding initializeViewBinding(@NonNull View view) {
        return LayoutPluginGroupBinding.bind(view);
    }

    @Override
    public void bind(@NonNull LayoutPluginGroupBinding binding, int position) {
        binding.name.setText(group.getName());
        binding.version.setText(group.getVersion());
        binding.getRoot().setOnClickListener(v -> onToggleListener.onToggleExpanded());
    }

    @Override
    public int getLayout() {
        return R.layout.layout_plugin_group;
    }

    @Override
    public void setExpandableGroup(@NonNull ExpandableGroup onToggleListener) {
        this.onToggleListener = onToggleListener;
    }
}
