package com.abiddarris.common.renpy.internal;

import static com.abiddarris.common.renpy.internal.PythonSyntax.getAttr;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PythonObject {
    
    public static final PythonObject object;
    public static final PythonObject type;
    
    static {
        type = new PythonObject();
        type.addField("__name__", "type");
        type.addMethod("__new__", (args, kwargs) -> {
            PythonObject object = new PythonObject();
            object.addField("__name__", args.get(1));
            object.addField("__bases__", args.get(2));
            
            return object;
        });
        type.addMethod("__call__", (args, kwargs) -> {
            List args2 = new ArrayList<>();
            args2.add(type);
            args2.addAll(args);    
                
            return type.invokeStaticMethod("__new__", args2, kwargs);
        });
        
        object = type.invokeStaticMethod("__new__",
            List.of(type, emptyList(), emptyList(), emptyMap()), emptyMap());
        object.addMethod("__setattr__", (args, kwargs) -> {
            object.setAttribute((String)args.get(1), args.get(2));
           
            return null;
        });
        
    }
    
    protected Map<String, Object> attributes = new HashMap<>();
    
    public void addMethod(String name, PythonMethod func) {
        setAttribute(name, func);
    }
    
    private void addField(String name, Object obj) {
        setAttribute(name, obj);
    }
    
    public void setAttribute(String name, Object obj) {
        attributes.put(name, obj);
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
        
    public PythonObject invokeStaticMethod(String name, List args, Map kwargs) {
        PythonMethod method = (PythonMethod)getAttr(this, name, null);
        return method.call(args, kwargs);
    }
    
    public PythonObject call(List args, Map kwargs) {
        return invokeStaticMethod("__call__", args, kwargs);
    }
}