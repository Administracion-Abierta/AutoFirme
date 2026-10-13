package org.glassfish.json;

import java.util.Collections;
import java.util.Map;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonBuilderFactoryImpl.class */
class JsonBuilderFactoryImpl implements JsonBuilderFactory {
    private final Map<String, ?> config = Collections.emptyMap();
    private final BufferPool bufferPool;

    JsonBuilderFactoryImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.JsonBuilderFactory
    public JsonObjectBuilder createObjectBuilder() {
        return new JsonObjectBuilderImpl(this.bufferPool);
    }

    @Override // javax.json.JsonBuilderFactory
    public JsonArrayBuilder createArrayBuilder() {
        return new JsonArrayBuilderImpl(this.bufferPool);
    }

    @Override // javax.json.JsonBuilderFactory
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}
