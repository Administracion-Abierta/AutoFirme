package org.mozilla.javascript.commonjs.module.provider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/ModuleSourceProvider.class */
public interface ModuleSourceProvider {
    public static final ModuleSource NOT_MODIFIED = new ModuleSource(null, null, null, null, null);

    ModuleSource loadSource(String str, Scriptable scriptable, Object obj) throws URISyntaxException, IOException;

    ModuleSource loadSource(URI uri, URI uri2, Object obj) throws URISyntaxException, IOException;
}
