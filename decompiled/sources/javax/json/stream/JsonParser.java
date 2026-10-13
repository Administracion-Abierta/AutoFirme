package javax.json.stream;

import java.io.Closeable;
import java.math.BigDecimal;

/* JADX INFO: loaded from: autofirma.jar:javax/json/stream/JsonParser.class */
public interface JsonParser extends Closeable {

    /* JADX INFO: loaded from: autofirma.jar:javax/json/stream/JsonParser$Event.class */
    public enum Event {
        START_ARRAY,
        START_OBJECT,
        KEY_NAME,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_TRUE,
        VALUE_FALSE,
        VALUE_NULL,
        END_OBJECT,
        END_ARRAY
    }

    boolean hasNext();

    Event next();

    String getString();

    boolean isIntegralNumber();

    int getInt();

    long getLong();

    BigDecimal getBigDecimal();

    JsonLocation getLocation();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();
}
