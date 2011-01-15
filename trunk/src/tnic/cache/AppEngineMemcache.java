package tnic.cache;

import tnic.config.Env;

import java.util.Collections;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheException;
import javax.cache.CacheManager;


public class AppEngineMemcache {
    
    private static Cache cache;
    
    static {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        }
        catch (CacheException e) {
            Env.log.severe (e.toString ());
        }
    }

    /**
     * Store an object in memcache.
     *
     * @param String Key used for mapping to the object
     * @param Serializable The object to insert into memcache
     * @return true if successful, false otherwise
     */
    public static Object put (String key, Serializable value) {
        Object valueObj = (Object)value;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            ObjectOutputStream out = new ObjectOutputStream(bos) ;
            
            out.writeObject(valueObj);
            out.close();
            
            byte[] objectBytes = bos.toByteArray();      
            cache.put (key, objectBytes); 
        }
        catch (IOException e) {
            Env.log.severe (e.toString ());
            return valueObj;
        }
        return valueObj;
    }
    
    /**
     * Retreives an object from memcache by using a key.
     *
     * @param String Key that maps to an object
     * @return Object The unserialized object from memcache or NULL if the key
     *  doesn't map to an object
     */
    public static Object get (String key) {
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
            Env.log.severe (e.toString ());
            return null;
        }
        catch (ClassNotFoundException e) {
            Env.log.severe (e.toString ());
            return null;
        }
    }
    /**
     * Deletes an object from memcache, and returns the object
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
            Env.log.severe (e.toString ());
            return null;
        }
        catch (ClassNotFoundException e) {
            Env.log.severe (e.toString ());
            return null;
        }
    }

}
