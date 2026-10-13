package javax.json;

import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonNumber.class */
public interface JsonNumber extends JsonValue {
    boolean isIntegral();

    int intValue();

    int intValueExact();

    long longValue();

    long longValueExact();

    BigInteger bigIntegerValue();

    BigInteger bigIntegerValueExact();

    double doubleValue();

    BigDecimal bigDecimalValue();

    @Override // javax.json.JsonValue
    String toString();

    boolean equals(Object obj);

    int hashCode();
}
