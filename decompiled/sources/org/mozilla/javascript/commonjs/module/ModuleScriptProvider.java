package org.mozilla.javascript.commonjs.module;

import java.net.URI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/ModuleScriptProvider.class */
public interface ModuleScriptProvider {
    ModuleScript getModuleScript(Context context, String str, URI uri, URI uri2, Scriptable scriptable) throws Exception;
}
