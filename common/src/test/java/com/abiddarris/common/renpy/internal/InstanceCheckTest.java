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
package com.abiddarris.common.renpy.internal;

import static com.abiddarris.common.renpy.internal.PythonObject.True;
import static com.abiddarris.common.renpy.internal.PythonObject.bool;
import static com.abiddarris.common.renpy.internal.PythonObject.isinstance;
import static com.abiddarris.common.renpy.internal.PythonObject.list;
import static com.abiddarris.common.renpy.internal.PythonObject.int0;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InstanceCheckTest {
    
    @Test
    public void sameClassInstance() {
        assertTrue(isinstance.call(True, bool).toBoolean());
    }
    
    @Test
    public void differentClassInstance() {
        assertFalse(isinstance.call(True, list).toBoolean());
    }
    
    @Test
    public void parentClassInstance() {
        assertTrue(isinstance.call(True, int0).toBoolean());
    }
    
}