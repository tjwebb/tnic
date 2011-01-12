package com.tnic.editor;

import com.tnic.fs.TnicFileSystem;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.vfs.*;
import com.newatlanta.commons.vfs.provider.gae.GaeVFS;

public class Servlet extends HttpServlet {

    private static FileSystemManager fsManager;

    public void init () throws ServletException {
        com.tnic.config.Env.SERVLET_CONTEXT = getServletContext();
    }
    public void doGet (HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {

        try {
        //    String fileName = req.getParameter("file");
            TnicFileSystem.storeAsciiFile("gae://file1.js", "(function () { return 'i am file1'; })();");
            res.getWriter().println(TnicFileSystem.getAsciiFile("gae://file1.js"));
        }
        finally {
            GaeVFS.clearFilesCache();
        }
    }
}

