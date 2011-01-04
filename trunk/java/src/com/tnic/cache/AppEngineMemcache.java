package com.tnic.cache;

import java.util.logging.Logger;
import java.util.Collections;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheException;
import javax.cache.CacheManager;


public class AppEngineMemcache implements Memcache {
    
    private static final Logger log = Logger.getLogger(AppEngineMemcache.class.getName());
    private Cache cache;
    
    /**
     * Sole Constructor
     */
    public AppEngineMemcache () {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        }
        catch (CacheException e) {
            log.severe (e.toString ());
        }
    }

    /**
     * Put an object into the memcache; The object MUST be serializable.
     *
     * @param String Key used for mapping to the object
     * @param Object The object to insert into memcache
     * @return true if successful, false otherwise
     */
    public boolean put (String key, Object value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            ObjectOutputStream out = new ObjectOutputStream(bos) ;
            
            out.writeObject(value);
            out.close();
            
            byte[] objectBytes = bos.toByteArray();      
            cache.put (key, objectBytes); 
        }
        catch (IOException e) {
            log.severe (e.toString ());
            return false;
        }
        return true;

    }
    
    /**
     * Retreives an object from the mem cache by using a key.
     *
     * @param String Key that maps to an object
     * @return Object The unserialized object from memcache or NULL if the key
     *  doesn't map to an object
     */
    public Object get (String key) {
        if (!cache.containsKey(key)) return null;
        
        byte[] objectBytes = (byte[]) cache.get (key);
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (objectBytes); 
            ObjectInputStream oin = new ObjectInputStream (bis);
        
            Object object = oin.readObject ();
            oin.close ();
            return object;
        }
        catch (IOException e) {
            log.severe (e.toString ());
            return null;
        }
        catch (ClassNotFoundException e) {
            log.severe (e.toString ());
            return null;
        }
    }
    /**
     * Deletes an object from the mem cache, and returns the object
     */
    public Object remove (String key) {
        if (!cache.containsKey(key)) return null;
        byte[] objectBytes = (byte[]) cache.remove (key);
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (objectBytes); 
            ObjectInputStream oin = new ObjectInputStream (bis);
        
            Object object = oin.readObject ();
            oin.close ();
            return object;
        }
        catch (IOException e) {
            log.severe (e.toString ());
            return null;
        }
        catch (ClassNotFoundException e) {
            log.severe (e.toString ());
            return null;
        }
    }

}
