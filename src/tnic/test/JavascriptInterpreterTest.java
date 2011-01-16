package tnic.test;

import tnic.jsvm.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.script.*;

import java.io.IOException;

public class JavascriptInterpreterTest extends HttpServlet {
    public void doGet (HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        CompiledScript script = TnicJavascriptEngine.compile(
            "(function () { return $argv.x; })();"
        );
        Object result = TnicJavascriptEngine.eval(script, "({ x: 5 })");
        resp.setContentType("text/plain");
            
        resp.getWriter().println(result.toString());
    }
}
