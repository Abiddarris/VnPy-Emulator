package com.abiddarris.vnpyemulator.download.base;

import android.view.View;

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.LayoutPluginGroupBinding;
import com.abiddarris.vnpyemulator.plugins.PluginGroup;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.ExpandableItem;
import com.xwray.groupie.viewbinding.BindableItem;

public abstract class BaseGroupItem extends BindableItem<LayoutPluginGroupBinding> implements ExpandableItem {

    protected ExpandableGroup onToggleListener;

    @NonNull
    @Override
    protected LayoutPluginGroupBinding initializeViewBinding(@NonNull View view) {
        return LayoutPluginGroupBinding.bind(view);
    }

    @Override
    public void bind(@NonNull LayoutPluginGroupBinding binding, int position) {
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
