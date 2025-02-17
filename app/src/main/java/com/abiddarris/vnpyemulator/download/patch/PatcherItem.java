package com.abiddarris.vnpyemulator.download.patch;

import androidx.annotation.NonNull;

import com.abiddarris.vnpyemulator.databinding.LayoutPluginBinding;
import com.abiddarris.vnpyemulator.download.base.BaseDownloadFragment.BaseDownloadViewModel;
import com.abiddarris.vnpyemulator.download.base.BaseItem;
import com.abiddarris.vnpyemulator.patches.Patcher;

public class PatcherItem extends BaseItem {

    private final Patcher patcher;

    public PatcherItem(Patcher patcher, BaseDownloadViewModel baseDownloadViewModel) {
        super(baseDownloadViewModel);

        this.patcher = patcher;
    }

    @Override
    public void bind(@NonNull LayoutPluginBinding viewBinding, int position) {
        viewBinding.version.setText(patcher.getVersion());
    }
}
