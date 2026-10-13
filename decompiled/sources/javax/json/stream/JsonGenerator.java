package javax.json.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;

/* JADX INFO: loaded from: autofirma.jar:javax/json/stream/JsonGenerator.class */
public interface JsonGenerator extends Flushable, Closeable {
    public static final String PRETTY_PRINTING = "javax.json.stream.JsonGenerator.prettyPrinting";

    JsonGenerator writeStartObject();

    JsonGenerator writeStartObject(String str);

    JsonGenerator writeStartArray();

    JsonGenerator writeStartArray(String str);

    JsonGenerator write(String str, JsonValue jsonValue);

    JsonGenerator write(String str, String str2);

    JsonGenerator write(String str, BigInteger bigInteger);

    JsonGenerator write(String str, BigDecimal bigDecimal);

    JsonGenerator write(String str, int i);

    JsonGenerator write(String str, long j);

    JsonGenerator write(String str, double d);

    JsonGenerator write(String str, boolean z);

    JsonGenerator writeNull(String str);

    JsonGenerator writeEnd();

    JsonGenerator write(JsonValue jsonValue);

    JsonGenerator write(String str);

    JsonGenerator write(BigDecimal bigDecimal);

    JsonGenerator write(BigInteger bigInteger);

    JsonGenerator write(int i);

    JsonGenerator write(long j);

    JsonGenerator write(double d);

    JsonGenerator write(boolean z);

    JsonGenerator writeNull();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();

    @Override // java.io.Flushable
    void flush();
}
