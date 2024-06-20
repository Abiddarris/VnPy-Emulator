package com.abiddarris.renpy.plugin735606;

import android.content.Intent;
import android.os.Bundle;
import com.abiddarris.plugin.PermissionActivity;
import com.google.android.material.color.DynamicColors;
import org.renpy.android.PythonSDLActivity;

public class MainActivity extends PermissionActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DynamicColors.applyToActivityIfAvailable(this);
        
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void permissionGranted() {
        super.permissionGranted();
        
        Intent intent = new Intent(this, PythonSDLActivity.class);
        intent.putExtras(getIntent());
        
        startActivity(intent);
        finish();
    }
    
}
