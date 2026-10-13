package org.mozilla.javascript.commonjs.module;

import com.aowagie.text.ElementTags;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/Require.class */
public class Require extends BaseFunction {
    private static final long serialVersionUID = 1;
    private final ModuleScriptProvider moduleScriptProvider;
    private final Scriptable nativeScope;
    private final Scriptable paths;
    private final boolean sandboxed;
    private final Script preExec;
    private final Script postExec;
    private Scriptable mainExports;
    private static final ThreadLocal<Map<String, Scriptable>> loadingModuleInterfaces = new ThreadLocal<>();
    private String mainModuleId = null;
    private final Map<String, Scriptable> exportedModuleInterfaces = new ConcurrentHashMap();
    private final Object loadLock = new Object();

    public Require(Context cx, Scriptable nativeScope, ModuleScriptProvider moduleScriptProvider, Script preExec, Script postExec, boolean sandboxed) {
        this.moduleScriptProvider = moduleScriptProvider;
        this.nativeScope = nativeScope;
        this.sandboxed = sandboxed;
        this.preExec = preExec;
        this.postExec = postExec;
        setPrototype(ScriptableObject.getFunctionPrototype(nativeScope));
        if (!sandboxed) {
            this.paths = cx.newArray(nativeScope, 0);
            defineReadOnlyProperty(this, "paths", this.paths);
        } else {
            this.paths = null;
        }
    }

    public Scriptable requireMain(Context cx, String mainModuleId) {
        if (this.mainModuleId != null) {
            if (!this.mainModuleId.equals(mainModuleId)) {
                throw new IllegalStateException("Main module already set to " + this.mainModuleId);
            }
            return this.mainExports;
        }
        try {
            ModuleScript moduleScript = this.moduleScriptProvider.getModuleScript(cx, mainModuleId, null, null, this.paths);
            if (moduleScript != null) {
                this.mainExports = getExportedModuleInterface(cx, mainModuleId, null, null, true);
            } else if (!this.sandboxed) {
                URI mainUri = null;
                try {
                    mainUri = new URI(mainModuleId);
                } catch (URISyntaxException e) {
                }
                if (mainUri == null || !mainUri.isAbsolute()) {
                    File file = new File(mainModuleId);
                    if (!file.isFile()) {
                        throw ScriptRuntime.throwError(cx, this.nativeScope, "Module \"" + mainModuleId + "\" not found.");
                    }
                    mainUri = file.toURI();
                }
                this.mainExports = getExportedModuleInterface(cx, mainUri.toString(), mainUri, null, true);
            }
            this.mainModuleId = mainModuleId;
            return this.mainExports;
        } catch (RuntimeException x) {
            throw x;
        } catch (Exception x2) {
            throw new RuntimeException(x2);
        }
    }

    public void install(Scriptable scope) {
        ScriptableObject.putProperty(scope, "require", this);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args == null || args.length < 1) {
            throw ScriptRuntime.throwError(cx, scope, "require() needs one argument");
        }
        String id = (String) Context.jsToJava(args[0], String.class);
        URI uri = null;
        URI base = null;
        if (id.startsWith("./") || id.startsWith("../")) {
            if (!(thisObj instanceof ModuleScope)) {
                throw ScriptRuntime.throwError(cx, scope, "Can't resolve relative module ID \"" + id + "\" when require() is used outside of a module");
            }
            ModuleScope moduleScope = (ModuleScope) thisObj;
            base = moduleScope.getBase();
            URI current = moduleScope.getUri();
            uri = current.resolve(id);
            if (base == null) {
                id = uri.toString();
            } else {
                id = base.relativize(current).resolve(id).toString();
                if (id.charAt(0) == '.') {
                    if (this.sandboxed) {
                        throw ScriptRuntime.throwError(cx, scope, "Module \"" + id + "\" is not contained in sandbox.");
                    }
                    id = uri.toString();
                }
            }
        }
        return getExportedModuleInterface(cx, id, uri, base, false);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw ScriptRuntime.throwError(cx, scope, "require() can not be invoked as a constructor");
    }

    private Scriptable getExportedModuleInterface(Context cx, String id, URI uri, URI base, boolean isMain) {
        Scriptable exports;
        Scriptable exports2 = this.exportedModuleInterfaces.get(id);
        if (exports2 != null) {
            if (isMain) {
                throw new IllegalStateException("Attempt to set main module after it was loaded");
            }
            return exports2;
        }
        Map<String, Scriptable> threadLoadingModules = loadingModuleInterfaces.get();
        if (threadLoadingModules != null && (exports = threadLoadingModules.get(id)) != null) {
            return exports;
        }
        synchronized (this.loadLock) {
            Scriptable exports3 = this.exportedModuleInterfaces.get(id);
            if (exports3 != null) {
                return exports3;
            }
            ModuleScript moduleScript = getModule(cx, id, uri, base);
            if (this.sandboxed && !moduleScript.isSandboxed()) {
                throw ScriptRuntime.throwError(cx, this.nativeScope, "Module \"" + id + "\" is not contained in sandbox.");
            }
            Scriptable exports4 = cx.newObject(this.nativeScope);
            boolean outermostLocked = threadLoadingModules == null;
            if (outermostLocked) {
                threadLoadingModules = new HashMap();
                loadingModuleInterfaces.set(threadLoadingModules);
            }
            threadLoadingModules.put(id, exports4);
            try {
                try {
                    Scriptable newExports = executeModuleScript(cx, id, exports4, moduleScript, isMain);
                    if (exports4 != newExports) {
                        threadLoadingModules.put(id, newExports);
                        exports4 = newExports;
                    }
                    if (outermostLocked) {
                        this.exportedModuleInterfaces.putAll(threadLoadingModules);
                        loadingModuleInterfaces.set(null);
                    }
                    return exports4;
                } catch (RuntimeException e) {
                    threadLoadingModules.remove(id);
                    throw e;
                }
            } catch (Throwable th) {
                if (outermostLocked) {
                    this.exportedModuleInterfaces.putAll(threadLoadingModules);
                    loadingModuleInterfaces.set(null);
                }
                throw th;
            }
        }
    }

    private Scriptable executeModuleScript(Context cx, String id, Scriptable exports, ModuleScript moduleScript, boolean isMain) {
        ScriptableObject scriptableObject = (ScriptableObject) cx.newObject(this.nativeScope);
        URI uri = moduleScript.getUri();
        URI base = moduleScript.getBase();
        defineReadOnlyProperty(scriptableObject, ElementTags.ID, id);
        if (!this.sandboxed) {
            defineReadOnlyProperty(scriptableObject, "uri", uri.toString());
        }
        Scriptable moduleScope = new ModuleScope(this.nativeScope, uri, base);
        moduleScope.put("exports", moduleScope, exports);
        moduleScope.put("module", moduleScope, scriptableObject);
        scriptableObject.put("exports", scriptableObject, exports);
        install(moduleScope);
        if (isMain) {
            defineReadOnlyProperty(this, "main", scriptableObject);
        }
        executeOptionalScript(this.preExec, cx, moduleScope);
        moduleScript.getScript().exec(cx, moduleScope);
        executeOptionalScript(this.postExec, cx, moduleScope);
        return ScriptRuntime.toObject(cx, this.nativeScope, ScriptableObject.getProperty(scriptableObject, "exports"));
    }

    private static void executeOptionalScript(Script script, Context cx, Scriptable executionScope) {
        if (script != null) {
            script.exec(cx, executionScope);
        }
    }

    private static void defineReadOnlyProperty(ScriptableObject obj, String name, Object value) {
        ScriptableObject.putProperty(obj, name, value);
        obj.setAttributes(name, 5);
    }

    private ModuleScript getModule(Context cx, String id, URI uri, URI base) {
        try {
            ModuleScript moduleScript = this.moduleScriptProvider.getModuleScript(cx, id, uri, base, this.paths);
            if (moduleScript == null) {
                throw ScriptRuntime.throwError(cx, this.nativeScope, "Module \"" + id + "\" not found.");
            }
            return moduleScript;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e2) {
            throw Context.throwAsScriptRuntimeEx(e2);
        }
    }

    @Override // org.mozilla.javascript.BaseFunction
    public String getFunctionName() {
        return "require";
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getArity() {
        return 1;
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getLength() {
        return 1;
    }
}
