package org.mozilla.javascript.commonjs.module.provider;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.sun.jna.platform.win32.WinNT;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/ModuleSourceProviderBase.class */
public abstract class ModuleSourceProviderBase implements ModuleSourceProvider, Serializable {
    private static final long serialVersionUID = 1;

    protected abstract ModuleSource loadFromUri(URI uri, URI uri2, Object obj) throws URISyntaxException, IOException;

    @Override // org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider
    public ModuleSource loadSource(String moduleId, Scriptable paths, Object validator) throws URISyntaxException, IOException {
        ModuleSource moduleSource;
        if (!entityNeedsRevalidation(validator)) {
            return NOT_MODIFIED;
        }
        ModuleSource moduleSource2 = loadFromPrivilegedLocations(moduleId, validator);
        if (moduleSource2 != null) {
            return moduleSource2;
        }
        if (paths != null && (moduleSource = loadFromPathArray(moduleId, paths, validator)) != null) {
            return moduleSource;
        }
        return loadFromFallbackLocations(moduleId, validator);
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider
    public ModuleSource loadSource(URI uri, URI base, Object validator) throws URISyntaxException, IOException {
        return loadFromUri(uri, base, validator);
    }

    private ModuleSource loadFromPathArray(String moduleId, Scriptable paths, Object validator) throws IOException {
        long llength = ScriptRuntime.toUint32(ScriptableObject.getProperty(paths, XSDatatype.FACET_LENGTH));
        int ilength = llength > 2147483647L ? WinNT.MAXLONG : (int) llength;
        for (int i = 0; i < ilength; i++) {
            String path = ensureTrailingSlash((String) ScriptableObject.getTypedProperty(paths, i, String.class));
            try {
                URI uri = new URI(path);
                if (!uri.isAbsolute()) {
                    uri = new File(path).toURI().resolve("");
                }
                ModuleSource moduleSource = loadFromUri(uri.resolve(moduleId), uri, validator);
                if (moduleSource != null) {
                    return moduleSource;
                }
            } catch (URISyntaxException e) {
                throw new MalformedURLException(e.getMessage());
            }
        }
        return null;
    }

    private static String ensureTrailingSlash(String path) {
        return path.endsWith("/") ? path : path.concat("/");
    }

    protected boolean entityNeedsRevalidation(Object validator) {
        return true;
    }

    protected ModuleSource loadFromPrivilegedLocations(String moduleId, Object validator) throws URISyntaxException, IOException {
        return null;
    }

    protected ModuleSource loadFromFallbackLocations(String moduleId, Object validator) throws URISyntaxException, IOException {
        return null;
    }
}
