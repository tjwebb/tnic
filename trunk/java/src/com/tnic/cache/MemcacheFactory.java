package com.tnic.cache;

import com.tnic.config.InvalidConfigurationException;
import com.tnic.config.Env;

/**
 * Provide a Memcache that is appropriate for the current system configuration
 */
public class MemcacheFactory {
    public static Memcache getMemcache () throws InvalidConfigurationException {
        if (Env.SERVER_INFO == null) {
            throw new InvalidConfigurationException ("SERVER_INFO not set");
        }
        String p = Env.SERVER_INFO;
        if ("Google".equals(p)) {
            return new AppEngineMemcache();
        }
        else {
            return new GenericMemcache();
        }
    }
}
