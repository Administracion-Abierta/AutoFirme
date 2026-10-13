package org.glassfish.json;

import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonPrettyGeneratorImpl.class */
public class JsonPrettyGeneratorImpl extends JsonGeneratorImpl {
    private int indentLevel;
    private static final String INDENT = "    ";

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator writeNull() {
        return super.writeNull();
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(boolean x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(BigDecimal x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(BigInteger x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(double x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(long x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(int x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, JsonValue x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(JsonValue x0) {
        return super.write(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator writeNull(String x0) {
        return super.writeNull(x0);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, boolean x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, BigDecimal x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, BigInteger x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, double x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, long x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, int x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public /* bridge */ /* synthetic */ JsonGenerator write(String x0, String x1) {
        return super.write(x0, x1);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator, java.io.Flushable
    public /* bridge */ /* synthetic */ void flush() {
        super.flush();
    }

    public JsonPrettyGeneratorImpl(Writer writer, BufferPool bufferPool) {
        super(writer, bufferPool);
    }

    public JsonPrettyGeneratorImpl(OutputStream out, BufferPool bufferPool) {
        super(out, bufferPool);
    }

    public JsonPrettyGeneratorImpl(OutputStream out, Charset encoding, BufferPool bufferPool) {
        super(out, encoding, bufferPool);
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public JsonGenerator writeStartObject() {
        super.writeStartObject();
        this.indentLevel++;
        return this;
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public JsonGenerator writeStartObject(String name) {
        super.writeStartObject(name);
        this.indentLevel++;
        return this;
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public JsonGenerator writeStartArray() {
        super.writeStartArray();
        this.indentLevel++;
        return this;
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public JsonGenerator writeStartArray(String name) {
        super.writeStartArray(name);
        this.indentLevel++;
        return this;
    }

    @Override // org.glassfish.json.JsonGeneratorImpl, javax.json.stream.JsonGenerator
    public JsonGenerator writeEnd() {
        writeNewLine();
        this.indentLevel--;
        writeIndent();
        super.writeEnd();
        return this;
    }

    private void writeIndent() {
        for (int i = 0; i < this.indentLevel; i++) {
            writeString(INDENT);
        }
    }

    @Override // org.glassfish.json.JsonGeneratorImpl
    protected void writeComma() {
        super.writeComma();
        writeChar('\n');
        writeIndent();
    }

    private void writeNewLine() {
        writeChar('\n');
    }
}
