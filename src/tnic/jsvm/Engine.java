package tnic.jsvm;

import tnic.config.Env;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

/**
 * Creates the runtime environment for running tnic javascript programs.
 */
public class Engine {
    private static String WRAPPER_PREFIX =
        "function run ($argv_string) {"
      +     "importPackage(Packages.tnic.util);"
      +     "var $argv = eval($argv_string);"
      +     "return ("
    ;
    private static String WRAPPER_SUFFIX = 
            ").toSource(); "
      + "}"
    ;

    /**
     * Compiles Javascript source. Wraps the source code inside a function
     * called 'run' for compatibility with eval()
     * @param src   The javascript source code as a String
     * @return CompiledScript instance
     */
    public static CompiledScript compile (String src) {
        Env.log.info("compile/1: src: "+ src);
        Context cx = Context.enter();
        cx.initStandardObjects();
        try {
            return new CompiledScript(cx.compileFunction(
                /* scope */  new ImporterTopLevel(cx),
                /* source */ WRAPPER_PREFIX + src + WRAPPER_SUFFIX,
                "RUN", 0, null
            ));
        }
        finally {
            Context.exit();
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
        if (script == null) return null;
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        try {
            return script.getScriptFunction().call(
                cx, scope, scope, new Object [] { (Object)argv }
            ).toString();
        }
        finally {
            Context.exit();
        }
    }

    public static String eval (Class module) {
        return null;
    }


    /**
     * Attempt to locate a compiled javascript class
     * @param name the name of the class
     * @return the Class descriptor if found, null otherwise
     */
    public static Class locate (String name) {
        try {
            return Class.forName(name);
        }
        catch (ClassNotFoundException ce) {
            return null;
        }
    }
}
