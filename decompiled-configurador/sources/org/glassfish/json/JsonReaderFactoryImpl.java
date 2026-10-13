package org.glassfish.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonReaderFactoryImpl.class */
class JsonReaderFactoryImpl implements JsonReaderFactory {
    private final Map<String, ?> config = Collections.emptyMap();
    private final BufferPool bufferPool;

    JsonReaderFactoryImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.JsonReaderFactory
    public JsonReader createReader(Reader reader) {
        return new JsonReaderImpl(reader, this.bufferPool);
    }

    @Override // javax.json.JsonReaderFactory
    public JsonReader createReader(InputStream in) {
        return new JsonReaderImpl(in, this.bufferPool);
    }

    @Override // javax.json.JsonReaderFactory
    public JsonReader createReader(InputStream in, Charset charset) {
        return new JsonReaderImpl(in, charset, this.bufferPool);
    }

    @Override // javax.json.JsonReaderFactory
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}
