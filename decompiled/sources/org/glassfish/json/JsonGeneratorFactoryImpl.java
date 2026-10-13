package org.glassfish.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonGeneratorFactoryImpl.class */
class JsonGeneratorFactoryImpl implements JsonGeneratorFactory {
    private final boolean prettyPrinting;
    private final Map<String, ?> config;
    private final BufferPool bufferPool;

    JsonGeneratorFactoryImpl(Map<String, ?> config, boolean prettyPrinting, BufferPool bufferPool) {
        this.config = config;
        this.prettyPrinting = prettyPrinting;
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.stream.JsonGeneratorFactory
    public JsonGenerator createGenerator(Writer writer) {
        return this.prettyPrinting ? new JsonPrettyGeneratorImpl(writer, this.bufferPool) : new JsonGeneratorImpl(writer, this.bufferPool);
    }

    @Override // javax.json.stream.JsonGeneratorFactory
    public JsonGenerator createGenerator(OutputStream out) {
        return this.prettyPrinting ? new JsonPrettyGeneratorImpl(out, this.bufferPool) : new JsonGeneratorImpl(out, this.bufferPool);
    }

    @Override // javax.json.stream.JsonGeneratorFactory
    public JsonGenerator createGenerator(OutputStream out, Charset charset) {
        return this.prettyPrinting ? new JsonPrettyGeneratorImpl(out, charset, this.bufferPool) : new JsonGeneratorImpl(out, charset, this.bufferPool);
    }

    @Override // javax.json.stream.JsonGeneratorFactory
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}
