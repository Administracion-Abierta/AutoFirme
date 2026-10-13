package org.glassfish.json;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonArrayBuilderImpl.class */
class JsonArrayBuilderImpl implements JsonArrayBuilder {
    private ArrayList<JsonValue> valueList;
    private final BufferPool bufferPool;

    JsonArrayBuilderImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(JsonValue value) {
        validateValue(value);
        addValueList(value);
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(String value) {
        validateValue(value);
        addValueList(new JsonStringImpl(value));
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(BigDecimal value) {
        validateValue(value);
        addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(BigInteger value) {
        validateValue(value);
        addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(int value) {
        addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(long value) {
        addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(double value) {
        addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(boolean value) {
        addValueList(value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder addNull() {
        addValueList(JsonValue.NULL);
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(JsonObjectBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
        }
        addValueList(builder.build());
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArrayBuilder add(JsonArrayBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_ARRAY_BUILDER_NULL());
        }
        addValueList(builder.build());
        return this;
    }

    @Override // javax.json.JsonArrayBuilder
    public JsonArray build() {
        List<JsonValue> snapshot;
        if (this.valueList == null) {
            snapshot = Collections.emptyList();
        } else {
            snapshot = Collections.unmodifiableList(this.valueList);
        }
        this.valueList = null;
        return new JsonArrayImpl(snapshot, this.bufferPool);
    }

    private void addValueList(JsonValue value) {
        if (this.valueList == null) {
            this.valueList = new ArrayList<>();
        }
        this.valueList.add(value);
    }

    private void validateValue(Object value) {
        if (value == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_VALUE_NULL());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonArrayBuilderImpl$JsonArrayImpl.class */
    private static final class JsonArrayImpl extends AbstractList<JsonValue> implements JsonArray {
        private final List<JsonValue> valueList;
        private final BufferPool bufferPool;

        JsonArrayImpl(List<JsonValue> valueList, BufferPool bufferPool) {
            this.valueList = valueList;
            this.bufferPool = bufferPool;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.valueList.size();
        }

        @Override // javax.json.JsonArray
        public JsonObject getJsonObject(int index) {
            return (JsonObject) this.valueList.get(index);
        }

        @Override // javax.json.JsonArray
        public JsonArray getJsonArray(int index) {
            return (JsonArray) this.valueList.get(index);
        }

        @Override // javax.json.JsonArray
        public JsonNumber getJsonNumber(int index) {
            return (JsonNumber) this.valueList.get(index);
        }

        @Override // javax.json.JsonArray
        public JsonString getJsonString(int index) {
            return (JsonString) this.valueList.get(index);
        }

        @Override // javax.json.JsonArray
        public <T extends JsonValue> List<T> getValuesAs(Class<T> cls) {
            return (List<T>) this.valueList;
        }

        @Override // javax.json.JsonArray
        public String getString(int index) {
            return getJsonString(index).getString();
        }

        @Override // javax.json.JsonArray
        public String getString(int index, String defaultValue) {
            try {
                return getString(index);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        @Override // javax.json.JsonArray
        public int getInt(int index) {
            return getJsonNumber(index).intValue();
        }

        @Override // javax.json.JsonArray
        public int getInt(int index, int defaultValue) {
            try {
                return getInt(index);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        @Override // javax.json.JsonArray
        public boolean getBoolean(int index) {
            JsonValue jsonValue = get(index);
            if (jsonValue == JsonValue.TRUE) {
                return true;
            }
            if (jsonValue == JsonValue.FALSE) {
                return false;
            }
            throw new ClassCastException();
        }

        @Override // javax.json.JsonArray
        public boolean getBoolean(int index, boolean defaultValue) {
            try {
                return getBoolean(index);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        @Override // javax.json.JsonArray
        public boolean isNull(int index) {
            return this.valueList.get(index).equals(JsonValue.NULL);
        }

        @Override // javax.json.JsonValue
        public JsonValue.ValueType getValueType() {
            return JsonValue.ValueType.ARRAY;
        }

        @Override // java.util.AbstractList, java.util.List
        public JsonValue get(int index) {
            return this.valueList.get(index);
        }

        @Override // java.util.AbstractCollection, javax.json.JsonValue
        public String toString() {
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriterImpl(sw, this.bufferPool);
            jw.write(this);
            jw.close();
            return sw.toString();
        }
    }
}
