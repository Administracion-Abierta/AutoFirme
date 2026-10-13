package javax.json;

import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonObject.class */
public interface JsonObject extends JsonStructure, Map<String, JsonValue> {
    JsonArray getJsonArray(String str);

    JsonObject getJsonObject(String str);

    JsonNumber getJsonNumber(String str);

    JsonString getJsonString(String str);

    String getString(String str);

    String getString(String str, String str2);

    int getInt(String str);

    int getInt(String str, int i);

    boolean getBoolean(String str);

    boolean getBoolean(String str, boolean z);

    boolean isNull(String str);
}
