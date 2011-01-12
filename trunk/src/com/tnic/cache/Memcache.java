package com.tnic.cache;

public interface Memcache {
    public boolean put (String key, Object value);
    public Object get (String key);
    public Object remove (String key);
}
