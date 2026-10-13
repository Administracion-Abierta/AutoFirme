package javax.json;

import java.io.Closeable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:javax/json/JsonWriter.class */
public interface JsonWriter extends Closeable {
    void writeArray(JsonArray jsonArray);

    void writeObject(JsonObject jsonObject);

    void write(JsonStructure jsonStructure);

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();
}
