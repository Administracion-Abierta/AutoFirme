package org.glassfish.json;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonObjectBuilderImpl.class */
class JsonObjectBuilderImpl implements JsonObjectBuilder {
    private Map<String, JsonValue> valueMap;
    private final BufferPool bufferPool;

    JsonObjectBuilderImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, JsonValue value) {
        validateName(name);
        validateValue(value);
        putValueMap(name, value);
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, String value) {
        validateName(name);
        validateValue(value);
        putValueMap(name, new JsonStringImpl(value));
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, BigInteger value) {
        validateName(name);
        validateValue(value);
        putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, BigDecimal value) {
        validateName(name);
        validateValue(value);
        putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, int value) {
        validateName(name);
        putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, long value) {
        validateName(name);
        putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, double value) {
        validateName(name);
        putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, boolean value) {
        validateName(name);
        putValueMap(name, value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder addNull(String name) {
        validateName(name);
        putValueMap(name, JsonValue.NULL);
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        validateName(name);
        if (builder == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_OBJECT_BUILDER_NULL());
        }
        putValueMap(name, builder.build());
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        validateName(name);
        if (builder == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_ARRAY_BUILDER_NULL());
        }
        putValueMap(name, builder.build());
        return this;
    }

    @Override // javax.json.JsonObjectBuilder
    public JsonObject build() {
        Map<String, JsonValue> snapshot = this.valueMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.valueMap);
        this.valueMap = null;
        return new JsonObjectImpl(snapshot, this.bufferPool);
    }

    private void putValueMap(String name, JsonValue value) {
        if (this.valueMap == null) {
            this.valueMap = new LinkedHashMap();
        }
        this.valueMap.put(name, value);
    }

    private void validateName(String name) {
        if (name == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_NAME_NULL());
        }
    }

    private void validateValue(Object value) {
        if (value == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_VALUE_NULL());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonObjectBuilderImpl$JsonObjectImpl.class */
    private static final class JsonObjectImpl extends AbstractMap<String, JsonValue> implements JsonObject {
        private final Map<String, JsonValue> valueMap;
        private final BufferPool bufferPool;

        JsonObjectImpl(Map<String, JsonValue> valueMap, BufferPool bufferPool) {
            this.valueMap = valueMap;
            this.bufferPool = bufferPool;
        }

        @Override // javax.json.JsonObject
        public JsonArray getJsonArray(String name) {
            return (JsonArray) get(name);
        }

        @Override // javax.json.JsonObject
        public JsonObject getJsonObject(String name) {
            return (JsonObject) get(name);
        }

        @Override // javax.json.JsonObject
        public JsonNumber getJsonNumber(String name) {
            return (JsonNumber) get(name);
        }

        @Override // javax.json.JsonObject
        public JsonString getJsonString(String name) {
            return (JsonString) get(name);
        }

        @Override // javax.json.JsonObject
        public String getString(String name) {
            return getJsonString(name).getString();
        }

        @Override // javax.json.JsonObject
        public String getString(String name, String defaultValue) {
            try {
                return getString(name);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        @Override // javax.json.JsonObject
        public int getInt(String name) {
            return getJsonNumber(name).intValue();
        }

        @Override // javax.json.JsonObject
        public int getInt(String name, int defaultValue) {
            try {
                return getInt(name);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        @Override // javax.json.JsonObject
        public boolean getBoolean(String name) {
            JsonValue value = get(name);
            if (value == null) {
                throw new NullPointerException();
            }
            if (value == JsonValue.TRUE) {
                return true;
            }
            if (value == JsonValue.FALSE) {
                return false;
            }
            throw new ClassCastException();
        }

        @Override // javax.json.JsonObject
        public boolean getBoolean(String name, boolean defaultValue) {
            try {
                return getBoolean(name);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        @Override // javax.json.JsonObject
        public boolean isNull(String name) {
            return get(name).equals(JsonValue.NULL);
        }

        @Override // javax.json.JsonValue
        public JsonValue.ValueType getValueType() {
            return JsonValue.ValueType.OBJECT;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<String, JsonValue>> entrySet() {
            return this.valueMap.entrySet();
        }

        @Override // java.util.AbstractMap, javax.json.JsonValue
        public String toString() {
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriterImpl(sw, this.bufferPool);
            jw.write(this);
            jw.close();
            return sw.toString();
        }
    }
}
