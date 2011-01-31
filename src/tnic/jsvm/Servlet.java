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

public class Servlet extends HttpServlet {

    public void init () throws ServletException {
        tnic.config.Env.SERVLET_CONTEXT = getServletContext();
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {

        String app = req.getParameter("app");
        String arg = req.getParameter("arg");
        if (app == null) return;

        boolean spawn = false;

        if ("true" .equals(req.getParameter("spawn"))) {
            spawn = true;
            // TODO: change from default queue to app queue
            Queue q = QueueFactory.getDefaultQueue();
            q.add(withUrl("/run?app="+ app).method(Method.GET));
        }

        try {
            JSONArray modList = new JSONArray(
                Engine.eval(
                    TnicFileSystem.getAsciiFile(app.replaceAll("\\.", "/")),
                    req.getParameter("argv")
                )
            );
            String modResult = null;
            for (int i = 0; i < modList.length(); i++) {
                String modName  = modList.getString(i);
                Class  modClass = Engine.locate(modName);
                if (modClass == null) modResult = Engine.eval(
                    Engine.compile(TnicFileSystem.getAsciiFile(
                        modName.replaceAll("\\.", "/")
                    )),
                    (i == 0 && arg != null) ? arg : modResult
                );
                else Engine.eval(modClass);
            }
            res.getWriter().print(modResult);
        }
        catch (JSONException je) {
            Env.log.severe(je.toString());
            res.sendError(500);
        }
        catch (NullPointerException npe) {
            Env.log.severe(npe.toString());
            res.getWriter().print(npe.toString());
            res.sendError(500);
        }
        finally {
            if (!spawn) TnicFileSystem.cleanup();
        }
    }
}
