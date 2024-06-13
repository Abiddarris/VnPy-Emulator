
package com.abiddarris.vnpyemulator.sources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface Connection extends Closeable {
    
    public abstract boolean isExists() throws IOException;
    
    public abstract long getSize() throws IOException;
    
    public abstract InputStream getInputStream() throws IOException;
    
}
