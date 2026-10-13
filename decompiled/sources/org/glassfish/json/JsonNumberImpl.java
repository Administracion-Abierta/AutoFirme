package org.glassfish.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonNumberImpl.class */
abstract class JsonNumberImpl implements JsonNumber {
    JsonNumberImpl() {
    }

    static JsonNumber getJsonNumber(int num) {
        return new JsonIntNumber(num);
    }

    static JsonNumber getJsonNumber(long num) {
        return new JsonLongNumber(num);
    }

    static JsonNumber getJsonNumber(BigInteger value) {
        return new JsonBigDecimalNumber(new BigDecimal(value));
    }

    static JsonNumber getJsonNumber(double value) {
        return new JsonBigDecimalNumber(BigDecimal.valueOf(value));
    }

    static JsonNumber getJsonNumber(BigDecimal value) {
        return new JsonBigDecimalNumber(value);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonNumberImpl$JsonIntNumber.class */
    private static final class JsonIntNumber extends JsonNumberImpl {
        private final int num;
        private BigDecimal bigDecimal;

        JsonIntNumber(int num) {
            this.num = num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public boolean isIntegral() {
            return true;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public int intValue() {
            return this.num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public int intValueExact() {
            return this.num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public long longValue() {
            return this.num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public long longValueExact() {
            return this.num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public double doubleValue() {
            return this.num;
        }

        @Override // javax.json.JsonNumber
        public BigDecimal bigDecimalValue() {
            BigDecimal bd = this.bigDecimal;
            if (bd == null) {
                BigDecimal bigDecimal = new BigDecimal(this.num);
                bd = bigDecimal;
                this.bigDecimal = bigDecimal;
            }
            return bd;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber, javax.json.JsonValue
        public String toString() {
            return Integer.toString(this.num);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonNumberImpl$JsonLongNumber.class */
    private static final class JsonLongNumber extends JsonNumberImpl {
        private final long num;
        private BigDecimal bigDecimal;

        JsonLongNumber(long num) {
            this.num = num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public boolean isIntegral() {
            return true;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public long longValue() {
            return this.num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public long longValueExact() {
            return this.num;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber
        public double doubleValue() {
            return this.num;
        }

        @Override // javax.json.JsonNumber
        public BigDecimal bigDecimalValue() {
            BigDecimal bd = this.bigDecimal;
            if (bd == null) {
                BigDecimal bigDecimal = new BigDecimal(this.num);
                bd = bigDecimal;
                this.bigDecimal = bigDecimal;
            }
            return bd;
        }

        @Override // org.glassfish.json.JsonNumberImpl, javax.json.JsonNumber, javax.json.JsonValue
        public String toString() {
            return Long.toString(this.num);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonNumberImpl$JsonBigDecimalNumber.class */
    private static final class JsonBigDecimalNumber extends JsonNumberImpl {
        private final BigDecimal bigDecimal;

        JsonBigDecimalNumber(BigDecimal value) {
            this.bigDecimal = value;
        }

        @Override // javax.json.JsonNumber
        public BigDecimal bigDecimalValue() {
            return this.bigDecimal;
        }
    }

    @Override // javax.json.JsonNumber
    public boolean isIntegral() {
        return bigDecimalValue().scale() == 0;
    }

    @Override // javax.json.JsonNumber
    public int intValue() {
        return bigDecimalValue().intValue();
    }

    @Override // javax.json.JsonNumber
    public int intValueExact() {
        return bigDecimalValue().intValueExact();
    }

    @Override // javax.json.JsonNumber
    public long longValue() {
        return bigDecimalValue().longValue();
    }

    @Override // javax.json.JsonNumber
    public long longValueExact() {
        return bigDecimalValue().longValueExact();
    }

    @Override // javax.json.JsonNumber
    public double doubleValue() {
        return bigDecimalValue().doubleValue();
    }

    @Override // javax.json.JsonNumber
    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override // javax.json.JsonNumber
    public BigInteger bigIntegerValueExact() {
        return bigDecimalValue().toBigIntegerExact();
    }

    @Override // javax.json.JsonValue
    public JsonValue.ValueType getValueType() {
        return JsonValue.ValueType.NUMBER;
    }

    @Override // javax.json.JsonNumber
    public int hashCode() {
        return bigDecimalValue().hashCode();
    }

    @Override // javax.json.JsonNumber
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonNumber)) {
            return false;
        }
        JsonNumber other = (JsonNumber) obj;
        return bigDecimalValue().equals(other.bigDecimalValue());
    }

    @Override // javax.json.JsonNumber, javax.json.JsonValue
    public String toString() {
        return bigDecimalValue().toString();
    }
}
