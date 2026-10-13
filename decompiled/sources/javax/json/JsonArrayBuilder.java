package javax.json;

import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonArrayBuilder.class */
public interface JsonArrayBuilder {
    JsonArrayBuilder add(JsonValue jsonValue);

    JsonArrayBuilder add(String str);

    JsonArrayBuilder add(BigDecimal bigDecimal);

    JsonArrayBuilder add(BigInteger bigInteger);

    JsonArrayBuilder add(int i);

    JsonArrayBuilder add(long j);

    JsonArrayBuilder add(double d);

    JsonArrayBuilder add(boolean z);

    JsonArrayBuilder addNull();

    JsonArrayBuilder add(JsonObjectBuilder jsonObjectBuilder);

    JsonArrayBuilder add(JsonArrayBuilder jsonArrayBuilder);

    JsonArray build();
}
