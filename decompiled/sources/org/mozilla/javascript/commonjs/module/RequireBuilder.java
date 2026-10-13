package org.mozilla.javascript.commonjs.module;

import java.io.Serializable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/RequireBuilder.class */
public class RequireBuilder implements Serializable {
    private static final long serialVersionUID = 1;
    private boolean sandboxed = true;
    private ModuleScriptProvider moduleScriptProvider;
    private Script preExec;
    private Script postExec;

    public RequireBuilder setModuleScriptProvider(ModuleScriptProvider moduleScriptProvider) {
        this.moduleScriptProvider = moduleScriptProvider;
        return this;
    }

    public RequireBuilder setPostExec(Script postExec) {
        this.postExec = postExec;
        return this;
    }

    public RequireBuilder setPreExec(Script preExec) {
        this.preExec = preExec;
        return this;
    }

    public RequireBuilder setSandboxed(boolean sandboxed) {
        this.sandboxed = sandboxed;
        return this;
    }

    public Require createRequire(Context cx, Scriptable globalScope) {
        return new Require(cx, globalScope, this.moduleScriptProvider, this.preExec, this.postExec, this.sandboxed);
    }
}
