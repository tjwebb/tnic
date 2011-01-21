package tnic.fs;

import tnic.cache.AppEngineMemcache;
import tnic.jsvm.Engine;
import tnic.config.Env;

import org.apache.commons.vfs.*;
import org.apache.commons.io.IOUtils;
import com.newatlanta.commons.vfs.provider.gae.GaeVFS;

import javax.script.CompiledScript;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.*;

public class TnicFileSystem {
    public static FileSystemManager Manager;
    static {
        try {
            GaeVFS.setRootPath(
                tnic.config.Env.SERVLET_CONTEXT.getRealPath( "/" )
            );
            Manager = GaeVFS.getManager();
        }
        catch (Exception e) {
            Env.log.severe(e.toString());
        }
    }

    /**
     * Retrieve an ASCII file.
     * @param path  The path of the file to retrieve
     * @return Contents of the file as a String
     */
    public static String getAsciiFile (String path) 
            throws IOException {
        String file = (String)AppEngineMemcache.get(path);
        if (file == null) {
            file = IOUtils.toString(
                Manager.resolveFile(path).getContent().getInputStream()
            );
            AppEngineMemcache.put(path, file);
        }
        return file.toString();
    }

    /**
     * Get a compiled script from the filesystem.
     */
    public static CompiledScript getCompiledScript (String path)
            throws IOException {
        CompiledScript script = null;
        try {
            script = (CompiledScript)AppEngineMemcache.get(path);
        }
        catch (ClassCastException cce) {
            script = null;
        }
        if (script == null) {
            String file = getAsciiFile(path);
            script = Engine.compile(file);

            AppEngineMemcache.put(path, (Serializable)script);
        }
        return script;
    }

    /**
     * Store an ASCII file.
     * @param path      The path (file name) of the file
     * @param contents  The contents of the file
     */
    public static void storeAsciiFile (String path, String contents)
            throws IOException {
        FileObject file = Manager.resolveFile(path);
        file.createFile();
        IOUtils.copy(
            new ByteArrayInputStream(contents.getBytes()),
            file.getContent().getOutputStream()
        );
        file.close();
        AppEngineMemcache.put(path, contents);
    }

    public static void cleanup () {
        GaeVFS.clearFilesCache();
    }
}
