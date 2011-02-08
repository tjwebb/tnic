package tnic.jsvm;

import tnic.config.Env;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

/**
 * Creates the runtime environment for running tnic javascript programs.
 */
public class Engine {
    private static String ARGV        = "$argv";
    private static String ARGV_STRING = ARGV + "_string";
    private static String WRAPPER_PREFIX_COMPILE_1 =
        "function run ("+ ARGV +"_string) {"
    ;
    private static String WRAPPER_PREFIX_COMPILE_2 = "return ";
    private static String IMPORT_AND_DEFINE = 
        "importPackage(Packages.tnic.util);"
      + "importPackage(Packages.tnic.config);"
      + "var "+ ARGV +" = eval("+ ARGV_STRING +");"
    ;
    private static String WRAPPER_SUFFIX = ".toSource(); ";
    private static String WRAPPER_SUFFIX_COMPILE = "}" ;

    /**
     * Compiles Javascript source. Wraps the source code inside a function
     * called 'run' for compatibility with eval()
     * @param src   The javascript source code as a String
     * @return CompiledScript instance
     */
    public static CompiledScript compile (String src) {
        Context cx = Context.enter();
        cx.initStandardObjects();
        try {
            return new CompiledScript(cx.compileFunction(
                /* scope */  new ImporterTopLevel(cx),
                /* source */ prepare(src, false),
                "RUN", 0, null
            ));
        }
        finally {
            Context.exit();
        }
    }

    /**
     * Prepares the javascript source string for compilation.
     */
    public static String prepare (String core, boolean eval) {
        String src = "";
        if (!eval) {
            src += WRAPPER_PREFIX_COMPILE_1;
            src += IMPORT_AND_DEFINE;
            src += WRAPPER_PREFIX_COMPILE_2;
            src += core;
            src += WRAPPER_SUFFIX;
            src += WRAPPER_SUFFIX_COMPILE;
        }
        else {
            src += IMPORT_AND_DEFINE;
            src += core;
            src += WRAPPER_SUFFIX;
        }
        return src;
    }

    /**
     * Evaluates Javascript source directly.
     * @param src   The javascript source code as a String
     * @returns the result of the script execution
     */
    public static String eval (String src, String argv) {
        if (src == null) return null;
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        scope.setParentScope(new ImporterTopLevel(cx));
        scope.put(ARGV_STRING, scope, (Object)argv);
        
        try {
            return cx.evaluateString(scope, prepare(src, true), "RUN", 0, null)
                .toString();
        }
        catch (EvaluatorException ex) {
            return null;
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

    /**
     * Evaluates a compiled Javascript class.
     */
    public static String eval (Class module, String argv) {
        try {
            return module.getMethod("run", Object.class).invoke(
                /* instance */  module.newInstance(),
                /* args */      argv
            ).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    /**
     * Construct a standard error message as JSON
     * @param err The error code
     * @param msg The error message
     * @return JSON string of error
     */
    public static String error (int err, String msg) {
        return "({ error : "+ err +", msg: \""+ msg +"\" })";
    }
}
