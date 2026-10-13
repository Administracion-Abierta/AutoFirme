package org.glassfish.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonProviderImpl.class */
public class JsonProviderImpl extends JsonProvider {
    private final BufferPool bufferPool = new BufferPoolImpl();

    @Override // javax.json.spi.JsonProvider
    public JsonGenerator createGenerator(Writer writer) {
        return new JsonGeneratorImpl(writer, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonGenerator createGenerator(OutputStream out) {
        return new JsonGeneratorImpl(out, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonParser createParser(Reader reader) {
        return new JsonParserImpl(reader, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonParser createParser(InputStream in) {
        return new JsonParserImpl(in, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        BufferPool pool = null;
        if (config != null && config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool) config.get(BufferPool.class.getName());
        }
        if (pool == null) {
            pool = this.bufferPool;
        }
        return new JsonParserFactoryImpl(pool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        boolean prettyPrinting;
        BufferPool pool;
        Map<String, Object> providerConfig;
        if (config == null) {
            providerConfig = Collections.emptyMap();
            prettyPrinting = false;
            pool = this.bufferPool;
        } else {
            Map<String, Object> providerConfig2 = new HashMap<>();
            boolean zIsPrettyPrintingEnabled = isPrettyPrintingEnabled(config);
            prettyPrinting = zIsPrettyPrintingEnabled;
            if (zIsPrettyPrintingEnabled) {
                providerConfig2.put(JsonGenerator.PRETTY_PRINTING, true);
            }
            pool = (BufferPool) config.get(BufferPool.class.getName());
            if (pool != null) {
                providerConfig2.put(BufferPool.class.getName(), pool);
            } else {
                pool = this.bufferPool;
            }
            providerConfig = Collections.unmodifiableMap(providerConfig2);
        }
        return new JsonGeneratorFactoryImpl(providerConfig, prettyPrinting, pool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonReader createReader(Reader reader) {
        return new JsonReaderImpl(reader, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonReader createReader(InputStream in) {
        return new JsonReaderImpl(in, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonWriter createWriter(Writer writer) {
        return new JsonWriterImpl(writer, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonWriter createWriter(OutputStream out) {
        return new JsonWriterImpl(out, this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        boolean prettyPrinting;
        BufferPool pool;
        Map<String, Object> providerConfig;
        if (config == null) {
            providerConfig = Collections.emptyMap();
            prettyPrinting = false;
            pool = this.bufferPool;
        } else {
            Map<String, Object> providerConfig2 = new HashMap<>();
            boolean zIsPrettyPrintingEnabled = isPrettyPrintingEnabled(config);
            prettyPrinting = zIsPrettyPrintingEnabled;
            if (zIsPrettyPrintingEnabled) {
                providerConfig2.put(JsonGenerator.PRETTY_PRINTING, true);
            }
            pool = (BufferPool) config.get(BufferPool.class.getName());
            if (pool != null) {
                providerConfig2.put(BufferPool.class.getName(), pool);
            } else {
                pool = this.bufferPool;
            }
            providerConfig = Collections.unmodifiableMap(providerConfig2);
        }
        return new JsonWriterFactoryImpl(providerConfig, prettyPrinting, pool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        BufferPool pool = null;
        if (config != null && config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool) config.get(BufferPool.class.getName());
        }
        if (pool == null) {
            pool = this.bufferPool;
        }
        return new JsonReaderFactoryImpl(pool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonObjectBuilder createObjectBuilder() {
        return new JsonObjectBuilderImpl(this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonArrayBuilder createArrayBuilder() {
        return new JsonArrayBuilderImpl(this.bufferPool);
    }

    @Override // javax.json.spi.JsonProvider
    public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        BufferPool pool = null;
        if (config != null && config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool) config.get(BufferPool.class.getName());
        }
        if (pool == null) {
            pool = this.bufferPool;
        }
        return new JsonBuilderFactoryImpl(pool);
    }

    static boolean isPrettyPrintingEnabled(Map<String, ?> config) {
        return config.containsKey(JsonGenerator.PRETTY_PRINTING);
    }
}
