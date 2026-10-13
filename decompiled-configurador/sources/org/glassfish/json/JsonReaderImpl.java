package org.glassfish.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonReaderImpl.class */
class JsonReaderImpl implements JsonReader {
    private final JsonParserImpl parser;
    private boolean readDone;
    private final BufferPool bufferPool;

    JsonReaderImpl(Reader reader, BufferPool bufferPool) {
        this.parser = new JsonParserImpl(reader, bufferPool);
        this.bufferPool = bufferPool;
    }

    JsonReaderImpl(InputStream in, BufferPool bufferPool) {
        this.parser = new JsonParserImpl(in, bufferPool);
        this.bufferPool = bufferPool;
    }

    JsonReaderImpl(InputStream in, Charset charset, BufferPool bufferPool) {
        this.parser = new JsonParserImpl(in, charset, bufferPool);
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.JsonReader
    public JsonStructure read() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            JsonParser.Event e = this.parser.next();
            if (e == JsonParser.Event.START_ARRAY) {
                return readArray(new JsonArrayBuilderImpl(this.bufferPool));
            }
            if (e == JsonParser.Event.START_OBJECT) {
                return readObject(new JsonObjectBuilderImpl(this.bufferPool));
            }
        }
        throw new JsonException("Internal Error");
    }

    @Override // javax.json.JsonReader
    public JsonObject readObject() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            JsonParser.Event e = this.parser.next();
            if (e == JsonParser.Event.START_OBJECT) {
                return readObject(new JsonObjectBuilderImpl(this.bufferPool));
            }
            if (e == JsonParser.Event.START_ARRAY) {
                throw new JsonException(JsonMessages.READER_EXPECTED_OBJECT_GOT_ARRAY());
            }
        }
        throw new JsonException("Internal Error");
    }

    @Override // javax.json.JsonReader
    public JsonArray readArray() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            JsonParser.Event e = this.parser.next();
            if (e == JsonParser.Event.START_ARRAY) {
                return readArray(new JsonArrayBuilderImpl(this.bufferPool));
            }
            if (e == JsonParser.Event.START_OBJECT) {
                throw new JsonException(JsonMessages.READER_EXPECTED_ARRAY_GOT_OBJECT());
            }
        }
        throw new JsonException("Internal Error");
    }

    @Override // javax.json.JsonReader, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.readDone = true;
        this.parser.close();
    }

    private JsonArray readArray(JsonArrayBuilder builder) {
        while (this.parser.hasNext()) {
            JsonParser.Event e = this.parser.next();
            switch (e) {
                case START_ARRAY:
                    JsonArray array = readArray(new JsonArrayBuilderImpl(this.bufferPool));
                    builder.add(array);
                    break;
                case START_OBJECT:
                    JsonObject object = readObject(new JsonObjectBuilderImpl(this.bufferPool));
                    builder.add(object);
                    break;
                case VALUE_STRING:
                    builder.add(this.parser.getString());
                    break;
                case VALUE_NUMBER:
                    if (this.parser.isDefinitelyInt()) {
                        builder.add(this.parser.getInt());
                    } else {
                        builder.add(this.parser.getBigDecimal());
                    }
                    break;
                case VALUE_TRUE:
                    builder.add(JsonValue.TRUE);
                    break;
                case VALUE_FALSE:
                    builder.add(JsonValue.FALSE);
                    break;
                case VALUE_NULL:
                    builder.addNull();
                    break;
                case END_ARRAY:
                    return builder.build();
                default:
                    throw new JsonException("Internal Error");
            }
        }
        throw new JsonException("Internal Error");
    }

    private JsonObject readObject(JsonObjectBuilder builder) {
        String key = null;
        while (this.parser.hasNext()) {
            JsonParser.Event e = this.parser.next();
            switch (e) {
                case START_ARRAY:
                    JsonArray array = readArray(new JsonArrayBuilderImpl(this.bufferPool));
                    builder.add(key, array);
                    break;
                case START_OBJECT:
                    JsonObject object = readObject(new JsonObjectBuilderImpl(this.bufferPool));
                    builder.add(key, object);
                    break;
                case VALUE_STRING:
                    builder.add(key, this.parser.getString());
                    break;
                case VALUE_NUMBER:
                    if (this.parser.isDefinitelyInt()) {
                        builder.add(key, this.parser.getInt());
                    } else {
                        builder.add(key, this.parser.getBigDecimal());
                    }
                    break;
                case VALUE_TRUE:
                    builder.add(key, JsonValue.TRUE);
                    break;
                case VALUE_FALSE:
                    builder.add(key, JsonValue.FALSE);
                    break;
                case VALUE_NULL:
                    builder.addNull(key);
                    break;
                case END_ARRAY:
                default:
                    throw new JsonException("Internal Error");
                case KEY_NAME:
                    key = this.parser.getString();
                    break;
                case END_OBJECT:
                    return builder.build();
            }
        }
        throw new JsonException("Internal Error");
    }
}
