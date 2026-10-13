package org.mozilla.javascript.commonjs.module.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mozilla.javascript.commonjs.module.ModuleScript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/StrongCachingModuleScriptProvider.class */
public class StrongCachingModuleScriptProvider extends CachingModuleScriptProviderBase {
    private static final long serialVersionUID = 1;
    private final Map<String, CachingModuleScriptProviderBase.CachedModuleScript> modules;

    public StrongCachingModuleScriptProvider(ModuleSourceProvider moduleSourceProvider) {
        super(moduleSourceProvider);
        this.modules = new ConcurrentHashMap(16, 0.75f, getConcurrencyLevel());
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase
    protected CachingModuleScriptProviderBase.CachedModuleScript getLoadedModule(String moduleId) {
        return this.modules.get(moduleId);
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase
    protected void putLoadedModule(String moduleId, ModuleScript moduleScript, Object validator) {
        this.modules.put(moduleId, new CachingModuleScriptProviderBase.CachedModuleScript(moduleScript, validator));
    }
}
