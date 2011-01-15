package tnic.fs;

import tnic.cache.AppEngineMemcache;

import org.apache.commons.vfs.*;
import org.apache.commons.io.IOUtils;
import com.newatlanta.commons.vfs.provider.gae.GaeVFS;

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
        catch (Exception e) { }
    }

    /**
     * Retrieve an ASCII file.
     * @param path  The path of the file to retrieve
     * @return Contents of the file as a String
     */
    public static String getAsciiFile(String path) throws IOException {
        String file = AppEngineMemcache.get(path).toString();
        if (file != null) return file;

        file = IOUtils.toString(
            Manager.resolveFile(path).getContent().getInputStream()
        );
        AppEngineMemcache.put(path, (Serializable)file);
        return file;
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
}
