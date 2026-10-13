package org.glassfish.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonParserFactoryImpl.class */
class JsonParserFactoryImpl implements JsonParserFactory {
    private final Map<String, ?> config = Collections.emptyMap();
    private final BufferPool bufferPool;

    JsonParserFactoryImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.stream.JsonParserFactory
    public JsonParser createParser(Reader reader) {
        return new JsonParserImpl(reader, this.bufferPool);
    }

    @Override // javax.json.stream.JsonParserFactory
    public JsonParser createParser(InputStream in) {
        return new JsonParserImpl(in, this.bufferPool);
    }

    @Override // javax.json.stream.JsonParserFactory
    public JsonParser createParser(InputStream in, Charset charset) {
        return new JsonParserImpl(in, charset, this.bufferPool);
    }

    @Override // javax.json.stream.JsonParserFactory
    public JsonParser createParser(JsonArray array) {
        return new JsonStructureParser(array);
    }

    @Override // javax.json.stream.JsonParserFactory
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }

    @Override // javax.json.stream.JsonParserFactory
    public JsonParser createParser(JsonObject object) {
        return new JsonStructureParser(object);
    }
}
