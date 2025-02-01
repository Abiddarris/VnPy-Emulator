package com.abiddarris.vnpyemulator.download;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.abiddarris.common.android.dialogs.ProgressDialog;
import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.download.PluginFragment.PluginViewModel;
import com.abiddarris.vnpyemulator.plugins.PluginSource;

public class FetchPluginTask extends TaskDialog {

    @NonNull
    @Override
    protected DialogFragment newDialog() {
        return ProgressDialog.newProgressDialog(getString(R.string.fetch_plugin_title), getString(R.string.fetching));
    }

    @NonNull
    @Override
    protected String getTag() {
        return "FetchPluginDialog";
    }

    @Override
    public void execute() throws Exception {
        ((PluginViewModel)getModel())
                .onPluginFetched(PluginSource.getPlugins(getApplicationContext()));
    }
}
