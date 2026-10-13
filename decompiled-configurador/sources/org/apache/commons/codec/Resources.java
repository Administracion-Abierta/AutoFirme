package org.apache.commons.codec;

import java.io.InputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/Resources.class */
public class Resources {
    public static InputStream getInputStream(String name) {
        InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalArgumentException("Unable to resolve required resource: " + name);
        }
        return inputStream;
    }

    @Deprecated
    public Resources() {
    }
}
