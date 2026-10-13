package org.mozilla.javascript.commonjs.module.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/SoftCachingModuleScriptProvider.class */
public class SoftCachingModuleScriptProvider extends CachingModuleScriptProviderBase {
    private static final long serialVersionUID = 1;
    private transient ReferenceQueue<Script> scriptRefQueue;
    private transient ConcurrentMap<String, ScriptReference> scripts;

    public SoftCachingModuleScriptProvider(ModuleSourceProvider moduleSourceProvider) {
        super(moduleSourceProvider);
        this.scriptRefQueue = new ReferenceQueue<>();
        this.scripts = new ConcurrentHashMap(16, 0.75f, getConcurrencyLevel());
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase, org.mozilla.javascript.commonjs.module.ModuleScriptProvider
    public ModuleScript getModuleScript(Context cx, String moduleId, URI uri, URI base, Scriptable paths) throws Exception {
        while (true) {
            ScriptReference ref = (ScriptReference) this.scriptRefQueue.poll();
            if (ref != null) {
                this.scripts.remove(ref.getModuleId(), ref);
            } else {
                return super.getModuleScript(cx, moduleId, uri, base, paths);
            }
        }
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase
    protected CachingModuleScriptProviderBase.CachedModuleScript getLoadedModule(String moduleId) {
        ScriptReference scriptRef = this.scripts.get(moduleId);
        if (scriptRef != null) {
            return scriptRef.getCachedModuleScript();
        }
        return null;
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase
    protected void putLoadedModule(String moduleId, ModuleScript moduleScript, Object validator) {
        this.scripts.put(moduleId, new ScriptReference(moduleScript.getScript(), moduleId, moduleScript.getUri(), moduleScript.getBase(), validator, this.scriptRefQueue));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/SoftCachingModuleScriptProvider$ScriptReference.class */
    private static class ScriptReference extends SoftReference<Script> {
        private final String moduleId;
        private final URI uri;
        private final URI base;
        private final Object validator;

        ScriptReference(Script script, String moduleId, URI uri, URI base, Object validator, ReferenceQueue<Script> refQueue) {
            super(script, refQueue);
            this.moduleId = moduleId;
            this.uri = uri;
            this.base = base;
            this.validator = validator;
        }

        CachingModuleScriptProviderBase.CachedModuleScript getCachedModuleScript() {
            Script script = get();
            if (script == null) {
                return null;
            }
            return new CachingModuleScriptProviderBase.CachedModuleScript(new ModuleScript(script, this.uri, this.base), this.validator);
        }

        String getModuleId() {
            return this.moduleId;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.scriptRefQueue = new ReferenceQueue<>();
        this.scripts = new ConcurrentHashMap();
        Map<String, CachingModuleScriptProviderBase.CachedModuleScript> serScripts = (Map) in.readObject();
        for (Map.Entry<String, CachingModuleScriptProviderBase.CachedModuleScript> entry : serScripts.entrySet()) {
            CachingModuleScriptProviderBase.CachedModuleScript cachedModuleScript = entry.getValue();
            putLoadedModule(entry.getKey(), cachedModuleScript.getModule(), cachedModuleScript.getValidator());
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Map<String, CachingModuleScriptProviderBase.CachedModuleScript> serScripts = new HashMap<>();
        for (Map.Entry<String, ScriptReference> entry : this.scripts.entrySet()) {
            CachingModuleScriptProviderBase.CachedModuleScript cachedModuleScript = entry.getValue().getCachedModuleScript();
            if (cachedModuleScript != null) {
                serScripts.put(entry.getKey(), cachedModuleScript);
            }
        }
        out.writeObject(serScripts);
    }
}
