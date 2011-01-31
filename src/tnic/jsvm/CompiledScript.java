package tnic.jsvm;

import tnic.config.Env;

import java.io.Serializable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

/**
 * Represents compiled Javascript code. This object is immutable.
 */
public class CompiledScript {
    private Function script;

    /**
     * Build a CompiledScript from a Function object.
     */
    public CompiledScript (Function f) {
        this.script = f;
    }

    /**
     * Get the org.mozilla.javascript.Function that this script contains.
     * @return Function
     */
    public Function getScriptFunction () {
        return this.script;
    }
    public Serializable getSerializableScript () {
        //final Function realScript = this.script;
        //return new Serializable() {
            //public Function script = realScript;
        //};
        //return this.scrip
        return null;
    }
}
