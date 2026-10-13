package org.glassfish.json;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonStructureParser.class */
class JsonStructureParser implements JsonParser {
    private Scope current;
    private JsonParser.Event state;
    private final Deque<Scope> scopeStack = new ArrayDeque();

    JsonStructureParser(JsonArray array) {
        this.current = new ArrayScope(array);
    }

    JsonStructureParser(JsonObject object) {
        this.current = new ObjectScope(object);
    }

    @Override // javax.json.stream.JsonParser
    public String getString() {
        if (this.state != JsonParser.Event.KEY_NAME) {
            if (this.state == JsonParser.Event.VALUE_STRING) {
                return ((JsonString) this.current.getJsonValue()).getString();
            }
            throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.state));
        }
        return ((ObjectScope) this.current).key;
    }

    @Override // javax.json.stream.JsonParser
    public boolean isIntegralNumber() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber) this.current.getJsonValue()).isIntegral();
        }
        throw new IllegalStateException(JsonMessages.PARSER_ISINTEGRALNUMBER_ERR(this.state));
    }

    @Override // javax.json.stream.JsonParser
    public int getInt() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber) this.current.getJsonValue()).intValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETINT_ERR(this.state));
    }

    @Override // javax.json.stream.JsonParser
    public long getLong() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber) this.current.getJsonValue()).longValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETLONG_ERR(this.state));
    }

    @Override // javax.json.stream.JsonParser
    public BigDecimal getBigDecimal() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber) this.current.getJsonValue()).bigDecimalValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETBIGDECIMAL_ERR(this.state));
    }

    @Override // javax.json.stream.JsonParser
    public JsonLocation getLocation() {
        return JsonLocationImpl.UNKNOWN;
    }

    @Override // javax.json.stream.JsonParser
    public boolean hasNext() {
        return ((this.state == JsonParser.Event.END_OBJECT || this.state == JsonParser.Event.END_ARRAY) && this.scopeStack.isEmpty()) ? false : true;
    }

    @Override // javax.json.stream.JsonParser
    public JsonParser.Event next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        transition();
        return this.state;
    }

    private void transition() {
        if (this.state == null) {
            this.state = this.current instanceof ArrayScope ? JsonParser.Event.START_ARRAY : JsonParser.Event.START_OBJECT;
            return;
        }
        if (this.state == JsonParser.Event.END_OBJECT || this.state == JsonParser.Event.END_ARRAY) {
            this.current = this.scopeStack.pop();
        }
        if (this.current instanceof ArrayScope) {
            if (this.current.hasNext()) {
                this.current.next();
                this.state = getState(this.current.getJsonValue());
                if (this.state == JsonParser.Event.START_ARRAY || this.state == JsonParser.Event.START_OBJECT) {
                    this.scopeStack.push(this.current);
                    this.current = Scope.createScope(this.current.getJsonValue());
                    return;
                }
                return;
            }
            this.state = JsonParser.Event.END_ARRAY;
            return;
        }
        if (this.state == JsonParser.Event.KEY_NAME) {
            this.state = getState(this.current.getJsonValue());
            if (this.state == JsonParser.Event.START_ARRAY || this.state == JsonParser.Event.START_OBJECT) {
                this.scopeStack.push(this.current);
                this.current = Scope.createScope(this.current.getJsonValue());
                return;
            }
            return;
        }
        if (this.current.hasNext()) {
            this.current.next();
            this.state = JsonParser.Event.KEY_NAME;
        } else {
            this.state = JsonParser.Event.END_OBJECT;
        }
    }

    @Override // javax.json.stream.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    private static JsonParser.Event getState(JsonValue value) {
        switch (value.getValueType()) {
            case ARRAY:
                return JsonParser.Event.START_ARRAY;
            case OBJECT:
                return JsonParser.Event.START_OBJECT;
            case STRING:
                return JsonParser.Event.VALUE_STRING;
            case NUMBER:
                return JsonParser.Event.VALUE_NUMBER;
            case TRUE:
                return JsonParser.Event.VALUE_TRUE;
            case FALSE:
                return JsonParser.Event.VALUE_FALSE;
            case NULL:
                return JsonParser.Event.VALUE_NULL;
            default:
                throw new JsonException("Unknown value type=" + value.getValueType());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonStructureParser$Scope.class */
    private static abstract class Scope implements Iterator {
        abstract JsonValue getJsonValue();

        private Scope() {
        }

        static Scope createScope(JsonValue value) {
            if (value instanceof JsonArray) {
                return new ArrayScope((JsonArray) value);
            }
            if (value instanceof JsonObject) {
                return new ObjectScope((JsonObject) value);
            }
            throw new JsonException("Cannot be called for value=" + value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonStructureParser$ArrayScope.class */
    private static class ArrayScope extends Scope {
        private final Iterator<JsonValue> it;
        private JsonValue value;

        ArrayScope(JsonArray array) {
            super();
            this.it = array.iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override // java.util.Iterator
        public JsonValue next() {
            this.value = this.it.next();
            return this.value;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // org.glassfish.json.JsonStructureParser.Scope
        JsonValue getJsonValue() {
            return this.value;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonStructureParser$ObjectScope.class */
    private static class ObjectScope extends Scope {
        private final Iterator<Map.Entry<String, JsonValue>> it;
        private JsonValue value;
        private String key;

        ObjectScope(JsonObject object) {
            super();
            this.it = object.entrySet().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override // java.util.Iterator
        public Map.Entry<String, JsonValue> next() {
            Map.Entry<String, JsonValue> next = this.it.next();
            this.key = next.getKey();
            this.value = next.getValue();
            return next;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // org.glassfish.json.JsonStructureParser.Scope
        JsonValue getJsonValue() {
            return this.value;
        }
    }
}
