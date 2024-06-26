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
package com.abiddarris.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Exceptions {
    
    public static String toString(Throwable throwable) {
        var os = new ByteArrayOutputStream();
        
        throwable.printStackTrace(new PrintStream(os));
        
        String string = new String(os.toByteArray());
        try {
            os.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        
        return string;
    }
    
    public static RuntimeException toUncheckException(Throwable throwable) {
        if(throwable instanceof RuntimeException) {
            return (RuntimeException)throwable;
        }
        
        return new UncheckExceptionWrapper(throwable);
    }
    
}
