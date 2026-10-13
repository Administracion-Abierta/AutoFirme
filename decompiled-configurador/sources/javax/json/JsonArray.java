package javax.json;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:javax/json/JsonArray.class */
public interface JsonArray extends JsonStructure, List<JsonValue> {
    JsonObject getJsonObject(int i);

    JsonArray getJsonArray(int i);

    JsonNumber getJsonNumber(int i);

    JsonString getJsonString(int i);

    <T extends JsonValue> List<T> getValuesAs(Class<T> cls);

    String getString(int i);

    String getString(int i, String str);

    int getInt(int i);

    int getInt(int i, int i2);

    boolean getBoolean(int i);

    boolean getBoolean(int i, boolean z);

    boolean isNull(int i);
}
