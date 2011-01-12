package com.tnic.cache;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class GenericMemcache implements Memcache {
    private Map map;
    public GenericMemcache () {
        map = Collections.synchronizedMap(new HashMap<String, Object>());
    }
    public boolean put (String key, Object value) {
        map.put(key, value);
        return true;
    }
    public Object get (String key) {
        return map.get(key);
    }
    public Object remove (String key) {
        return map.remove((Object)key);
    }
}
