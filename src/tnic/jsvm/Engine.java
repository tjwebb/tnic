package tnic.jsvm;

import tnic.config.Env;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Creates the runtime environment for running tnic javascript programs.
 */
public class Engine {
    private static String WRAPPER_PREFIX =
        "function run ($argv_string) {"
//      +     "importPackage(tnic.util);"
      +     "var $argv = eval($argv_string);"
      +     "return ("
    ;
    private static String WRAPPER_SUFFIX = 
            ").toSource(); "
      + "}"
    ;

    /**
     * Returns the Javascript engine. The purpose of this function is to
     * standardize the creation and usage of the built-in Java ScriptEngine
     * @return ScriptEngine
     */
    private static ScriptEngine getEngine () {
        ScriptEngineManager factory = new ScriptEngineManager();
        return factory.getEngineByName("JavaScript");
    }

    /**
     * Compiles Javascript source. Wraps the source code inside a function
     * called 'run' for compatibility with eval()
     * @param src   The javascript source code as a String
     * @return CompiledScript instance
     */
    public static CompiledScript compile (String src) {
        ScriptEngine engine = Engine.getEngine();
        try {
            return ((Compilable)engine).compile(
                WRAPPER_PREFIX + src + WRAPPER_SUFFIX
            );
        }
        catch (ScriptException ex) {
            Env.log.severe(ex.toString());
            return null;
        }
    }

    /**
     * Evaluates a compiled script. Invokes the 'run' function that wraps the
     * subroutine.
     * @param script    The script to compile
     * @param argv      A collection of arguments
     * @return result of script evaluation
     */
    public static String eval (CompiledScript script, String argv) {
        try {
            script.eval();
            return ((Invocable)script.getEngine())
                .invokeFunction("run", (Object)argv).toString();
        }
        catch (ScriptException se) {
            Env.log.severe(se.toString());
            return null;
        }
        catch (NoSuchMethodException nsme) {
            Env.log.severe(nsme.toString());
            return null;
        }
    }
}
