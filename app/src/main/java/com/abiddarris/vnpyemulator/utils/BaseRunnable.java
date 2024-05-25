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
package com.abiddarris.vnpyemulator.utils;

public interface BaseRunnable extends Runnable {
    
    public abstract void execute() throws Exception;
    
    @Override
    public default void run() {
        try {
            execute();
        } catch (Exception e) {
            onExceptionThrown(e);
        } finally {
            onFinally();
        }
    }
    
    public default void onFinally() {}
    
    public default void onExceptionThrown(Exception e) {
        e.printStackTrace();
            
        throw new RuntimeException("Error while running : " + toString(), e);
    }
    
}
