package org.glassfish.json.api;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/api/BufferPool.class */
public interface BufferPool {
    char[] take();

    void recycle(char[] cArr);
}
