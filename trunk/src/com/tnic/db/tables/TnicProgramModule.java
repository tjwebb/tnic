package com.tnic.db.tables;

import com.tnic.jsvm.TnicJavascriptEngine;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class TnicProgramModule {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension (vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String moduleName;

    @Persistent
    String src;

    @Persistent
    TnicProgramModuleCompiled moduleCompiled;

    public TnicProgramModule (String moduleName, String src) {
        this.moduleName = moduleName;
        this.src = src;
    }
    public void setCompiledModule (TnicProgramModuleCompiled compiled) {
        this.moduleCompiled = compiled;
    }
}
