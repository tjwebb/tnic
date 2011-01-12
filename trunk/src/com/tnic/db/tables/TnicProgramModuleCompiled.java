package com.tnic.db.tables;

import com.tnic.jsvm.TnicJavascriptEngine;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import javax.script.CompiledScript;

@PersistenceCapable
public class TnicProgramModuleCompiled {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    CompiledScript script;

    public TnicProgramModuleCompiled (String src) {
        this.script = TnicJavascriptEngine.compile(src);
    }
}
