package tnic.editor;

import tnic.fs.TnicFileSystem;
import tnic.config.Env;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.vfs.*;
import com.newatlanta.commons.vfs.provider.gae.GaeVFS;

public class Servlet extends HttpServlet {

    private static FileSystemManager fsManager;

    public void init () throws ServletException {
        tnic.config.Env.SERVLET_CONTEXT = getServletContext();

    }

    /**
     * Handles the loading of a file into the browser editor
     */
    public void doGet (HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {

        res.setContentType("text/plain");

        try {
            String fileName = req.getParameter("f");
            res.getWriter().println(TnicFileSystem.getAsciiFile(fileName));
        }
        catch (NullPointerException ex) {
            res.getWriter().print("File Not Found");
            Env.log.severe (ex.toString());
        }
        finally {
            GaeVFS.clearFilesCache();
        }
    }

    /**
     * Handles the saving of a file
     */
    public void doPost (HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/plain");

        try {
            String fileName     = req.getParameter("f");
            String fileContents = req.getParameter("c");

            TnicFileSystem.storeAsciiFile(fileName, fileContents.trim());
        }
        catch (Exception ex) {
            res.getWriter().println("Fail");
            Env.log.severe (ex.toString());
        }
        finally {
            GaeVFS.clearFilesCache();
        }
    }
}

