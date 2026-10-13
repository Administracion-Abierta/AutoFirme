package javax.json;

import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonObjectBuilder.class */
public interface JsonObjectBuilder {
    JsonObjectBuilder add(String str, JsonValue jsonValue);

    JsonObjectBuilder add(String str, String str2);

    JsonObjectBuilder add(String str, BigInteger bigInteger);

    JsonObjectBuilder add(String str, BigDecimal bigDecimal);

    JsonObjectBuilder add(String str, int i);

    JsonObjectBuilder add(String str, long j);

    JsonObjectBuilder add(String str, double d);

    JsonObjectBuilder add(String str, boolean z);

    JsonObjectBuilder addNull(String str);

    JsonObjectBuilder add(String str, JsonObjectBuilder jsonObjectBuilder);

    JsonObjectBuilder add(String str, JsonArrayBuilder jsonArrayBuilder);

    JsonObject build();
}
