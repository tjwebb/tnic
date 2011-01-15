package tnic.test;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.script.*;

import java.io.IOException;

public class JavascriptInterpreterTest extends HttpServlet {
    public void doGet (HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
            
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        Compilable compiler = (Compilable)engine;
        Object result1 = null;
        Object result2 = null;
        Object result3 = null;
        try {
            CompiledScript script1 = compiler.compile("var x = (function() { return { value : 'ok1' }; })()");
            CompiledScript script2 = compiler.compile("function y (obj) { return obj.value; }");
            CompiledScript script3 = compiler.compile("(function () { return 4208; })()");
            script1.eval();
            script2.eval();
            Invocable inv = (Invocable)engine;
            result1 = engine.get("x");
            result2 = inv.invokeFunction("y", result1);
            result3 = script3.eval();
        }
        catch (Exception e) {
            resp.getWriter().println(e.toString());
        }
        resp.getWriter().println(result1.toString());
        resp.getWriter().println(result2.toString());
        resp.getWriter().println(result3.toString());
    }
}
