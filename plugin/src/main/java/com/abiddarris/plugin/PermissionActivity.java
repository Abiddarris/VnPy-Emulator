package com.abiddarris.plugin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.abiddarris.common.android.utils.Permissions;

public class PermissionActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Permissions.requestManageExternalStoragePermission(
            this, getString(R.string.external_storage_permission_required_message));
    }
}
