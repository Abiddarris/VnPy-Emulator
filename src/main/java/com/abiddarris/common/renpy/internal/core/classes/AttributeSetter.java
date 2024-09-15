/***********************************************************************************
 * Copyright 2024 Abiddarris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************************/
package com.abiddarris.common.renpy.internal.core.classes;

import static com.abiddarris.common.renpy.internal.Python.newInt;

import com.abiddarris.common.renpy.internal.PythonObject;

public class AttributeSetter {

    private static boolean setAttribute;

    public static void activate() {
        setAttribute = true;
    }

    static void setAttributes(PythonObject self, PythonObject args) {
        if (!setAttribute) {
            return;
        }

        PythonObject attributes = args.getItem(newInt(2));
        attributes.iterator().forEachRemaining(k -> {
            String key = k.toString();
            if (key.equals("__name__") || key.equals("__bases__") || key.equals("__class__")) {
                return;
            }
            self.getAttributes().put(key, attributes.getItem(k));
        });
    }


}
