package com.abiddarris.renpy.plugin6991242187;

import android.os.Bundle;
import com.abiddarris.plugin.PermissionActivity;
import com.google.android.material.color.DynamicColors;

public class MainActivity extends PermissionActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DynamicColors.applyToActivityIfAvailable(this);
        
        super.onCreate(savedInstanceState);
    }
}
