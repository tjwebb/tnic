package tnic.jsvm;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import tnic.config.Env;
import tnic.jsvm.Engine;
import tnic.fs.TnicFileSystem;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.newatlanta.commons.vfs.provider.gae.GaeVFS;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

/**
 * Execute a tnic application via HTTP.
 */
public class Servlet extends HttpServlet {

    private PrintWriter client;
    private HttpServletResponse res;
    private HttpServletRequest req;

    public void init () throws ServletException {
        Env.SERVLET_CONTEXT = getServletContext();
        Env.JSVM_THREAD_POOL = "jsvm-thread-pool"; 
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        this.res    = res;
        this.req    = req;
        this.client = res.getWriter();

        Env.TESTING = "true".equals(req.getParameter("test")) ? true : false;

        try {
            runTnicApp();
        }
        catch (NullPointerException ex) {
            Env.log.severe(ex.toString());
            res.sendError(500, Engine.error(4, "null ptr"));
            return;
        }
        finally {
            TnicFileSystem.cleanup();
        }
    }

    /* executes a tnic app */
    private void runTnicApp () 
            throws IOException {
        String app  = req.getParameter("app");
        String arg  = req.getParameter("arg");

        if (app == null) return;

        boolean thread = false;

        /* check whether to spaw a new thread (task) for this app */
        if ("true" .equals(req.getParameter("thread"))) {
            thread = true;
            Queue q = QueueFactory.getQueue(Env.JSVM_THREAD_POOL);
            q.add(withUrl("/run?app="+ app).method(Method.GET));
            return;
        }

        JSONArray modList = null;   // list of modules to be run
        String modResult  = null;   // running result of module invocation

        try {
            String descriptorFile = 
                TnicFileSystem.getAsciiFile(app.replaceAll("\\.", "/"));

            /* check if app does not exist */
            if (null == descriptorFile) {
                res.sendError(500, Engine.error(1, "app does not exist"));
                return;
            }
            /* Get the module list from the tnic app descriptor */
            modList = new JSONArray(Engine.eval(
                descriptorFile,
                req.getParameter("argv")
            ));
        }
        catch (JSONException je) {
            /* object returned by app descriptor is invalid. this has never
             * been observed in practice because the object must be valid in
             * order for the app descriptor to be compiled. */
            Env.log.severe(je.toString());
            res.sendError(500, Engine.error(2, "invalid app descriptor"));
            return;
        }

        /* iterate over the module list and execute each module */
        for (int i = 0; i < modList.length(); i++) {
            String modName = null;
            try {
                modName  = modList.getString(i);
            }
            catch (JSONException je) {
                Env.log.severe(je.toString());
                res.sendError(500, Engine.error(2, "invalid app descriptor"));
                return;
            }
            modResult = runTnicModule(
                /* name */ modName,
                /* argv */ (i == 0 && arg != null) ? arg : modResult
            );
        }
        client.print(modResult);
    }

    /* execute a tnic module */
    private String runTnicModule (String moduleName, String argv) throws IOException {
        Class moduleClass = Engine.locate(moduleName);

        /* invoke using js engine */
        if (null == moduleClass || Env.TESTING) {
            String moduleFile = TnicFileSystem.getAsciiFile(moduleName.replaceAll("\\.", "/"));

            /* module not found, return error */
            if (null == moduleFile) {
                res.sendError(
                    500, 
                    Engine.error(3, "module "+ moduleName +" does not exist")
                );
                return "";
            }
            return Engine.eval(Engine.compile(moduleFile), argv);
        }
        /* class exists, invoke using reflection */
        else {
            return Engine.eval(moduleClass, argv);
        }
    }
}
