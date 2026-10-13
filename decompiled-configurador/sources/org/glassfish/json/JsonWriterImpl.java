package org.glassfish.json;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonWriterImpl.class */
class JsonWriterImpl implements JsonWriter {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final JsonGeneratorImpl generator;
    private boolean writeDone;
    private final NoFlushOutputStream os;

    JsonWriterImpl(Writer writer, BufferPool bufferPool) {
        this(writer, false, bufferPool);
    }

    JsonWriterImpl(Writer writer, boolean prettyPrinting, BufferPool bufferPool) {
        this.generator = prettyPrinting ? new JsonPrettyGeneratorImpl(writer, bufferPool) : new JsonGeneratorImpl(writer, bufferPool);
        this.os = null;
    }

    JsonWriterImpl(OutputStream out, BufferPool bufferPool) {
        this(out, UTF_8, false, bufferPool);
    }

    JsonWriterImpl(OutputStream out, boolean prettyPrinting, BufferPool bufferPool) {
        this(out, UTF_8, prettyPrinting, bufferPool);
    }

    JsonWriterImpl(OutputStream out, Charset charset, boolean prettyPrinting, BufferPool bufferPool) {
        this.os = new NoFlushOutputStream(out);
        this.generator = prettyPrinting ? new JsonPrettyGeneratorImpl(this.os, charset, bufferPool) : new JsonGeneratorImpl(this.os, charset, bufferPool);
    }

    @Override // javax.json.JsonWriter
    public void writeArray(JsonArray array) {
        if (this.writeDone) {
            throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
        }
        this.writeDone = true;
        this.generator.writeStartArray();
        for (JsonValue value : array) {
            this.generator.write(value);
        }
        this.generator.writeEnd();
        this.generator.flushBuffer();
        if (this.os != null) {
            this.generator.flush();
        }
    }

    @Override // javax.json.JsonWriter
    public void writeObject(JsonObject object) {
        if (this.writeDone) {
            throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
        }
        this.writeDone = true;
        this.generator.writeStartObject();
        for (Map.Entry<String, JsonValue> e : object.entrySet()) {
            this.generator.write(e.getKey(), e.getValue());
        }
        this.generator.writeEnd();
        this.generator.flushBuffer();
        if (this.os != null) {
            this.generator.flush();
        }
    }

    @Override // javax.json.JsonWriter
    public void write(JsonStructure value) {
        if (value instanceof JsonArray) {
            writeArray((JsonArray) value);
        } else {
            writeObject((JsonObject) value);
        }
    }

    @Override // javax.json.JsonWriter, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.writeDone = true;
        this.generator.close();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonWriterImpl$NoFlushOutputStream.class */
    private static final class NoFlushOutputStream extends FilterOutputStream {
        public NoFlushOutputStream(OutputStream out) {
            super(out);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() {
        }
    }
}
