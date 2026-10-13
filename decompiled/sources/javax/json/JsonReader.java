package javax.json;

import java.io.Closeable;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonReader.class */
public interface JsonReader extends Closeable {
    JsonStructure read();

    JsonObject readObject();

    JsonArray readArray();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();
}
