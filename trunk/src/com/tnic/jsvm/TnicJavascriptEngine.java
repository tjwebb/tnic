package com.tnic.jsvm;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TnicJavascriptEngine {
    /**
     * Returns the Javascript engine. The purpose of this function is to
     * standardize the creation and usage of the built-in Java ScriptEngine
     * @return ScriptEngine
     */
    private static ScriptEngine getEngine () {
        ScriptEngineManager factory = new ScriptEngineManager();
        return factory.getEngineByName("Javascript");
    }

    /**
     * Compiles Javascript source.
     * @param src   The javascript source code as a String
     * @return CompiledScript instance
     */
    public static CompiledScript compile (String src) {
        ScriptEngine engine = TnicJavascriptEngine.getEngine();
        try {
            return ((Compilable)engine).compile(src);
        }
        catch (ScriptException ex) {
            System.err.println(ex.toString());
        }
        return null;
    }

    public static Object runScript (CompiledScript script) {
        ScriptEngine engine = TnicJavascriptEngine.getEngine();
        // TODO:
        return null;
    }
}
