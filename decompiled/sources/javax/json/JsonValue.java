package javax.json;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonValue.class */
public interface JsonValue {
    public static final JsonValue NULL = new JsonValue() { // from class: javax.json.JsonValue.1
        @Override // javax.json.JsonValue
        public ValueType getValueType() {
            return ValueType.NULL;
        }

        public boolean equals(Object obj) {
            if (obj instanceof JsonValue) {
                return getValueType().equals(((JsonValue) obj).getValueType());
            }
            return false;
        }

        public int hashCode() {
            return ValueType.NULL.hashCode();
        }

        @Override // javax.json.JsonValue
        public String toString() {
            return "null";
        }
    };
    public static final JsonValue TRUE = new JsonValue() { // from class: javax.json.JsonValue.2
        @Override // javax.json.JsonValue
        public ValueType getValueType() {
            return ValueType.TRUE;
        }

        public boolean equals(Object obj) {
            if (obj instanceof JsonValue) {
                return getValueType().equals(((JsonValue) obj).getValueType());
            }
            return false;
        }

        public int hashCode() {
            return ValueType.TRUE.hashCode();
        }

        @Override // javax.json.JsonValue
        public String toString() {
            return "true";
        }
    };
    public static final JsonValue FALSE = new JsonValue() { // from class: javax.json.JsonValue.3
        @Override // javax.json.JsonValue
        public ValueType getValueType() {
            return ValueType.FALSE;
        }

        public boolean equals(Object obj) {
            if (obj instanceof JsonValue) {
                return getValueType().equals(((JsonValue) obj).getValueType());
            }
            return false;
        }

        public int hashCode() {
            return ValueType.FALSE.hashCode();
        }

        @Override // javax.json.JsonValue
        public String toString() {
            return "false";
        }
    };

    /* JADX INFO: loaded from: autofirma.jar:javax/json/JsonValue$ValueType.class */
    public enum ValueType {
        ARRAY,
        OBJECT,
        STRING,
        NUMBER,
        TRUE,
        FALSE,
        NULL
    }

    ValueType getValueType();

    String toString();
}
