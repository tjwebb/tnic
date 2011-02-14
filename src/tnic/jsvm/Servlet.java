package tnic.jsvm;

import tnic.config.Env;
import tnic.fs.TnicFileSystem;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

/**
 * Execute a tnic application via HTTP.
 */
public class Servlet extends HttpServlet {
    public void init () throws ServletException {
        Env.JSVM_THREAD_POOL = "jsvm-thread-pool"; 
        Env.SERVLET_CONTEXT = getServletContext();
    }
    public void doGet (HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {

        String app  = req.getParameter("app");
        String arg  = req.getParameter("arg");

        Env.TESTING = "true".equals(req.getParameter("test")) ? true : false;

        if (null == app) return;

        /* check whether to spaw a new thread (task) for this app */
        if ("true" .equals(req.getParameter("thread"))) {
            Queue q = QueueFactory.getQueue(Env.JSVM_THREAD_POOL);
            q.add(withUrl("/run?app="+ app).method(Method.GET));
            return;
        }

        try {
            /* run app and return the result */
            res.getWriter().print((new App(app)).run(arg));
        }
        catch (TnicExecutableException ex) {
            /* a known error occurred while attempting to load/execute app */
            res.sendError(500, ex.toString());
        }
        catch (NullPointerException ex) {
            /* an unknown error occurred */
            res.sendError(500, "({ error: 4, msg: \"An error occurred.\" })");
        }
        finally {
            TnicFileSystem.cleanup();
        }
    }
}
