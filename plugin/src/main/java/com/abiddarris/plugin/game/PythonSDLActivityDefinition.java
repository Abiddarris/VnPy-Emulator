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
package com.abiddarris.plugin.game;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import com.abiddarris.common.utils.Exceptions;
import com.abiddarris.common.utils.UncheckExceptionWrapper;

public interface PythonSDLActivityDefinition {
    
    public default Activity toActivity() {
        return (Activity)this;
    }
    
    public default FrameLayout getFrameLayout() {
        try {
            return (FrameLayout)Class.forName("org.renpy.android.PythonSDLActivity")
                .getField("mFrameLayout")
                .get(this);
        } catch (Exception e) {
            throw Exceptions.toUncheckException(e);
        }
    }
    
}
