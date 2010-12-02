package com.tnic.test;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.script.*;

import java.io.IOException;

public class JavascriptInterpreterTest extends HttpServlet {
    public void doGet (HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
            
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        Compilable compiler = (Compilable)engine;
        Object result1 = null;
        Object result2 = null;
        try {
            CompiledScript script1 = compiler.compile("var x = (function() { return { value : 'ok1' }; })()");
            CompiledScript script2 = compiler.compile("function y (obj) { return obj.value; }");
            script1.eval();
            script2.eval();
            Invocable inv = (Invocable)engine;
            result1 = engine.get("x");
            result2 = inv.invokeFunction("y", result1);

        }
        catch (Exception e) { }

        resp.setContentType("text/plain");
        resp.getWriter().println(result2.toString());
    }
}
