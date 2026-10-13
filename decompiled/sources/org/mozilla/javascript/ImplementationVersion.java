package org.mozilla.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ImplementationVersion.class */
public class ImplementationVersion {
    private String versionString;
    private static final ImplementationVersion version = new ImplementationVersion();

    public static String get() {
        return version.versionString;
    }

    private ImplementationVersion() {
        try {
            Enumeration<URL> urls = ImplementationVersion.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (urls.hasMoreElements()) {
                URL metaUrl = urls.nextElement();
                try {
                    InputStream is = metaUrl.openStream();
                    Throwable th = null;
                    try {
                        try {
                            Manifest mf = new Manifest(is);
                            Attributes attrs = mf.getMainAttributes();
                            if ("Mozilla Rhino".equals(attrs.getValue("Implementation-Title"))) {
                                this.versionString = "Rhino " + attrs.getValue("Implementation-Version") + " " + attrs.getValue("Built-Date").replaceAll("-", " ");
                                if (is != null) {
                                    if (0 == 0) {
                                        is.close();
                                        return;
                                    }
                                    try {
                                        is.close();
                                        return;
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                        return;
                                    }
                                }
                                return;
                            }
                            if (is != null) {
                                if (0 != 0) {
                                    try {
                                        is.close();
                                    } catch (Throwable th3) {
                                        th.addSuppressed(th3);
                                    }
                                } else {
                                    is.close();
                                }
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            throw th4;
                        }
                    } catch (Throwable th5) {
                        if (is != null) {
                            if (th != null) {
                                try {
                                    is.close();
                                } catch (Throwable th6) {
                                    th.addSuppressed(th6);
                                }
                            } else {
                                is.close();
                            }
                        }
                        throw th5;
                    }
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
        }
    }
}
