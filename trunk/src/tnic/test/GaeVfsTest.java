package tnic.test;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.vfs.*;
import com.newatlanta.commons.vfs.provider.gae.GaeVFS;

public class GaeVfsTest extends HttpServlet
{
    private static FileSystemManager fsManager;
    private static boolean isGoogleAppEngine;

    public void init() throws ServletException {
        // determine if we're running within GAE and get the appropriate FileSystemManager
        isGoogleAppEngine = getServletContext().getServerInfo().contains( "Google" );
        try {
            if ( isGoogleAppEngine ) {
                GaeVFS.setRootPath( getServletContext().getRealPath( "/" ) );
                fsManager = GaeVFS.getManager();
            } else {
                fsManager = VFS.getManager();
            }
        } catch ( FileSystemException fse ) {
            throw new ServletException( fse );
        }
    }

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        try {
            String realPath = getServletContext().getRealPath( req.getRequestURI() );
            res.getWriter().print(realPath);
            // an example of using Commons VFS (the FileObject class is similar to java.io.File)
            FileObject fileObject = fsManager.resolveFile( realPath );
            /*
            if ( !fileObject.exists() ) {
                res.sendError( HttpServletResponse.SC_NOT_FOUND ); // return 404 to client
                return;
            }
            */
            // ...continue processing the GET request...
        } finally {
            if ( isGoogleAppEngine ) {
                GaeVFS.clearFilesCache(); // this is important!
            }
        }
    }

    public void destroy() {
        if ( isGoogleAppEngine ) {
            GaeVFS.close(); // this is not so important, but nice to do
        }
    }
}
