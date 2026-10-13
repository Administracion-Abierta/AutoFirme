package org.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/json/JSONArray.class */
public class JSONArray implements Iterable<Object> {
    private final ArrayList<Object> myArrayList;

    public JSONArray() {
        this.myArrayList = new ArrayList<>();
    }

    /* JADX WARN: Switch 'out' block B:13:0x002e for B:18:0x005d already processed. Defaulting to fallback option. */
    public JSONArray(JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        char nextChar = x.nextClean();
        if (nextChar == 0) {
            throw x.syntaxError("Expected a ',' or ']'");
        }
        if (nextChar != ']') {
            x.back();
            while (true) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.myArrayList.add(JSONObject.NULL);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }
                switch (x.nextClean()) {
                    case 0:
                        throw x.syntaxError("Expected a ',' or ']'");
                    case ',':
                        char nextChar2 = x.nextClean();
                        if (nextChar2 == 0) {
                            throw x.syntaxError("Expected a ',' or ']'");
                        }
                        if (nextChar2 == ']') {
                            return;
                        }
                        x.back();
                        break;
                        break;
                    case ']':
                        return;
                    default:
                        throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }

    public JSONArray(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public JSONArray(Collection<?> collection) {
        this(collection, 0, new JSONParserConfiguration());
    }

    public JSONArray(Collection<?> collection, JSONParserConfiguration jsonParserConfiguration) {
        this(collection, 0, jsonParserConfiguration);
    }

    JSONArray(Collection<?> collection, int recursionDepth, JSONParserConfiguration jsonParserConfiguration) {
        if (recursionDepth > jsonParserConfiguration.getMaxNestingDepth()) {
            throw new JSONException("JSONArray has reached recursion depth limit of " + jsonParserConfiguration.getMaxNestingDepth());
        }
        if (collection == null) {
            this.myArrayList = new ArrayList<>();
        } else {
            this.myArrayList = new ArrayList<>(collection.size());
            addAll(collection, true, recursionDepth, jsonParserConfiguration);
        }
    }

    public JSONArray(Iterable<?> iter) {
        this();
        if (iter == null) {
            return;
        }
        addAll(iter, true);
    }

    public JSONArray(JSONArray array) {
        if (array == null) {
            this.myArrayList = new ArrayList<>();
        } else {
            this.myArrayList = new ArrayList<>(array.myArrayList);
        }
    }

    public JSONArray(Object array) throws JSONException {
        this();
        if (!array.getClass().isArray()) {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        }
        addAll(array, true, 0);
    }

    public JSONArray(int initialCapacity) throws JSONException {
        if (initialCapacity < 0) {
            throw new JSONException("JSONArray initial capacity cannot be negative.");
        }
        this.myArrayList = new ArrayList<>(initialCapacity);
    }

    @Override // java.lang.Iterable
    public Iterator<Object> iterator() {
        return this.myArrayList.iterator();
    }

    public Object get(int index) throws JSONException {
        Object object = opt(index);
        if (object == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return object;
    }

    public boolean getBoolean(int index) throws JSONException {
        Object object = get(index);
        if (!object.equals(Boolean.FALSE)) {
            if ((object instanceof String) && ((String) object).equalsIgnoreCase("false")) {
                return false;
            }
            if (!object.equals(Boolean.TRUE)) {
                if ((object instanceof String) && ((String) object).equalsIgnoreCase("true")) {
                    return true;
                }
                throw wrongValueFormatException(index, "boolean", object, null);
            }
            return true;
        }
        return false;
    }

    public double getDouble(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "double", object, e);
        }
    }

    public float getFloat(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }
        try {
            return Float.parseFloat(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "float", object, e);
        }
    }

    public Number getNumber(int index) throws JSONException {
        Object object = get(index);
        try {
            if (object instanceof Number) {
                return (Number) object;
            }
            return JSONObject.stringToNumber(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "number", object, e);
        }
    }

    public <E extends Enum<E>> E getEnum(Class<E> cls, int i) throws JSONException {
        E e = (E) optEnum(cls, i);
        if (e == null) {
            throw wrongValueFormatException(i, "enum of type " + JSONObject.quote(cls.getSimpleName()), opt(i), null);
        }
        return e;
    }

    public BigDecimal getBigDecimal(int index) throws JSONException {
        Object object = get(index);
        BigDecimal val = JSONObject.objectToBigDecimal(object, null);
        if (val == null) {
            throw wrongValueFormatException(index, "BigDecimal", object, null);
        }
        return val;
    }

    public BigInteger getBigInteger(int index) throws JSONException {
        Object object = get(index);
        BigInteger val = JSONObject.objectToBigInteger(object, null);
        if (val == null) {
            throw wrongValueFormatException(index, "BigInteger", object, null);
        }
        return val;
    }

    public int getInt(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "int", object, e);
        }
    }

    public JSONArray getJSONArray(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        throw wrongValueFormatException(index, "JSONArray", object, null);
    }

    public JSONObject getJSONObject(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        throw wrongValueFormatException(index, "JSONObject", object, null);
    }

    public long getLong(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        try {
            return Long.parseLong(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "long", object, e);
        }
    }

    public String getString(int index) throws JSONException {
        Object object = get(index);
        if (object instanceof String) {
            return (String) object;
        }
        throw wrongValueFormatException(index, "String", object, null);
    }

    public boolean isNull(int index) {
        return JSONObject.NULL.equals(opt(index));
    }

    public String join(String separator) throws JSONException {
        int len = length();
        if (len == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(JSONObject.valueToString(this.myArrayList.get(0)));
        for (int i = 1; i < len; i++) {
            sb.append(separator).append(JSONObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }

    public int length() {
        return this.myArrayList.size();
    }

    public void clear() {
        this.myArrayList.clear();
    }

    public Object opt(int index) {
        if (index < 0 || index >= length()) {
            return null;
        }
        return this.myArrayList.get(index);
    }

    public boolean optBoolean(int index) {
        return optBoolean(index, false);
    }

    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Boolean optBooleanObject(int index) {
        return optBooleanObject(index, false);
    }

    public Boolean optBooleanObject(int index, Boolean defaultValue) {
        try {
            return Boolean.valueOf(getBoolean(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }

    public double optDouble(int index, double defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        double doubleValue = val.doubleValue();
        return doubleValue;
    }

    public Double optDoubleObject(int index) {
        return optDoubleObject(index, Double.valueOf(Double.NaN));
    }

    public Double optDoubleObject(int index, Double defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        Double doubleValue = Double.valueOf(val.doubleValue());
        return doubleValue;
    }

    public float optFloat(int index) {
        return optFloat(index, Float.NaN);
    }

    public float optFloat(int index, float defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        float floatValue = val.floatValue();
        return floatValue;
    }

    public Float optFloatObject(int index) {
        return optFloatObject(index, Float.valueOf(Float.NaN));
    }

    public Float optFloatObject(int index, Float defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        Float floatValue = Float.valueOf(val.floatValue());
        return floatValue;
    }

    public int optInt(int index) {
        return optInt(index, 0);
    }

    public int optInt(int index, int defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }

    public Integer optIntegerObject(int index) {
        return optIntegerObject(index, 0);
    }

    public Integer optIntegerObject(int index, Integer defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return Integer.valueOf(val.intValue());
    }

    public <E extends Enum<E>> E optEnum(Class<E> cls, int i) {
        return (E) optEnum(cls, i, null);
    }

    public <E extends Enum<E>> E optEnum(Class<E> cls, int i, E e) {
        try {
            Object objOpt = opt(i);
            if (JSONObject.NULL.equals(objOpt)) {
                return e;
            }
            if (cls.isAssignableFrom(objOpt.getClass())) {
                return (E) objOpt;
            }
            return (E) Enum.valueOf(cls, objOpt.toString());
        } catch (IllegalArgumentException e2) {
            return e;
        } catch (NullPointerException e3) {
            return e;
        }
    }

    public BigInteger optBigInteger(int index, BigInteger defaultValue) {
        Object val = opt(index);
        return JSONObject.objectToBigInteger(val, defaultValue);
    }

    public BigDecimal optBigDecimal(int index, BigDecimal defaultValue) {
        Object val = opt(index);
        return JSONObject.objectToBigDecimal(val, defaultValue);
    }

    public JSONArray optJSONArray(int index) {
        return optJSONArray(index, null);
    }

    public JSONArray optJSONArray(int index, JSONArray defaultValue) {
        Object object = opt(index);
        return object instanceof JSONArray ? (JSONArray) object : defaultValue;
    }

    public JSONObject optJSONObject(int index) {
        return optJSONObject(index, null);
    }

    public JSONObject optJSONObject(int index, JSONObject defaultValue) {
        Object object = opt(index);
        return object instanceof JSONObject ? (JSONObject) object : defaultValue;
    }

    public long optLong(int index) {
        return optLong(index, 0L);
    }

    public long optLong(int index, long defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return val.longValue();
    }

    public Long optLongObject(int index) {
        return optLongObject(index, 0L);
    }

    public Long optLongObject(int index, Long defaultValue) {
        Number val = optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return Long.valueOf(val.longValue());
    }

    public Number optNumber(int index) {
        return optNumber(index, null);
    }

    public Number optNumber(int index, Number defaultValue) {
        Object val = opt(index);
        if (JSONObject.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return (Number) val;
        }
        if (val instanceof String) {
            try {
                return JSONObject.stringToNumber((String) val);
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String optString(int index) {
        return optString(index, "");
    }

    public String optString(int index, String defaultValue) {
        Object object = opt(index);
        return JSONObject.NULL.equals(object) ? defaultValue : object.toString();
    }

    public JSONArray put(boolean value) {
        return put(value ? Boolean.TRUE : Boolean.FALSE);
    }

    public JSONArray put(Collection<?> value) {
        return put(new JSONArray(value));
    }

    public JSONArray put(double value) throws JSONException {
        return put(Double.valueOf(value));
    }

    public JSONArray put(float value) throws JSONException {
        return put(Float.valueOf(value));
    }

    public JSONArray put(int value) {
        return put(Integer.valueOf(value));
    }

    public JSONArray put(long value) {
        return put(Long.valueOf(value));
    }

    public JSONArray put(Map<?, ?> value) {
        return put(new JSONObject(value));
    }

    public JSONArray put(Object value) {
        JSONObject.testValidity(value);
        this.myArrayList.add(value);
        return this;
    }

    public JSONArray put(int index, boolean value) throws JSONException {
        return put(index, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public JSONArray put(int index, Collection<?> value) throws JSONException {
        return put(index, new JSONArray(value));
    }

    public JSONArray put(int index, double value) throws JSONException {
        return put(index, Double.valueOf(value));
    }

    public JSONArray put(int index, float value) throws JSONException {
        return put(index, Float.valueOf(value));
    }

    public JSONArray put(int index, int value) throws JSONException {
        return put(index, Integer.valueOf(value));
    }

    public JSONArray put(int index, long value) throws JSONException {
        return put(index, Long.valueOf(value));
    }

    public JSONArray put(int index, Map<?, ?> value) throws JSONException {
        put(index, new JSONObject(value, new JSONParserConfiguration()));
        return this;
    }

    public JSONArray put(int index, Map<?, ?> value, JSONParserConfiguration jsonParserConfiguration) throws JSONException {
        put(index, new JSONObject(value, jsonParserConfiguration));
        return this;
    }

    public JSONArray put(int index, Object value) throws JSONException {
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < length()) {
            JSONObject.testValidity(value);
            this.myArrayList.set(index, value);
            return this;
        }
        if (index == length()) {
            return put(value);
        }
        this.myArrayList.ensureCapacity(index + 1);
        while (index != length()) {
            this.myArrayList.add(JSONObject.NULL);
        }
        return put(value);
    }

    public JSONArray putAll(Collection<?> collection) {
        addAll((Iterable<?>) collection, false);
        return this;
    }

    public JSONArray putAll(Iterable<?> iter) {
        addAll(iter, false);
        return this;
    }

    public JSONArray putAll(JSONArray array) {
        this.myArrayList.addAll(array.myArrayList);
        return this;
    }

    public JSONArray putAll(Object array) throws JSONException {
        addAll(array, false);
        return this;
    }

    public Object query(String jsonPointer) {
        return query(new JSONPointer(jsonPointer));
    }

    public Object query(JSONPointer jsonPointer) {
        return jsonPointer.queryFrom(this);
    }

    public Object optQuery(String jsonPointer) {
        return optQuery(new JSONPointer(jsonPointer));
    }

    public Object optQuery(JSONPointer jsonPointer) {
        try {
            return jsonPointer.queryFrom(this);
        } catch (JSONPointerException e) {
            return null;
        }
    }

    public Object remove(int index) {
        if (index < 0 || index >= length()) {
            return null;
        }
        return this.myArrayList.remove(index);
    }

    public boolean similar(Object other) {
        int len;
        if (!(other instanceof JSONArray) || (len = length()) != ((JSONArray) other).length()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            Object valueThis = this.myArrayList.get(i);
            Object valueOther = ((JSONArray) other).myArrayList.get(i);
            if (valueThis != valueOther) {
                if (valueThis == null) {
                    return false;
                }
                if (valueThis instanceof JSONObject) {
                    if (!((JSONObject) valueThis).similar(valueOther)) {
                        return false;
                    }
                } else if (valueThis instanceof JSONArray) {
                    if (!((JSONArray) valueThis).similar(valueOther)) {
                        return false;
                    }
                } else if ((valueThis instanceof Number) && (valueOther instanceof Number)) {
                    if (!JSONObject.isNumberSimilar((Number) valueThis, (Number) valueOther)) {
                        return false;
                    }
                } else if ((valueThis instanceof JSONString) && (valueOther instanceof JSONString)) {
                    if (!((JSONString) valueThis).toJSONString().equals(((JSONString) valueOther).toJSONString())) {
                        return false;
                    }
                } else if (!valueThis.equals(valueOther)) {
                    return false;
                }
            }
        }
        return true;
    }

    public JSONObject toJSONObject(JSONArray names) throws JSONException {
        if (names == null || names.isEmpty() || isEmpty()) {
            return null;
        }
        JSONObject jo = new JSONObject(names.length());
        for (int i = 0; i < names.length(); i++) {
            jo.put(names.getString(i), opt(i));
        }
        return jo;
    }

    public String toString() {
        try {
            return toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String toString(int indentFactor) throws JSONException {
        StringWriter sw = new StringWriter();
        return write(sw, indentFactor, 0).toString();
    }

    public Writer write(Writer writer) throws JSONException {
        return write(writer, 0, 0);
    }

    public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean needsComma = false;
            int length = length();
            writer.write(91);
            if (length == 1) {
                try {
                    JSONObject.writeValue(writer, this.myArrayList.get(0), indentFactor, indent);
                } catch (Exception e) {
                    throw new JSONException("Unable to write JSONArray value at index: 0", e);
                }
            } else if (length != 0) {
                int newIndent = indent + indentFactor;
                for (int i = 0; i < length; i++) {
                    if (needsComma) {
                        writer.write(44);
                    }
                    if (indentFactor > 0) {
                        writer.write(10);
                    }
                    JSONObject.indent(writer, newIndent);
                    try {
                        JSONObject.writeValue(writer, this.myArrayList.get(i), indentFactor, newIndent);
                        needsComma = true;
                    } catch (Exception e2) {
                        throw new JSONException("Unable to write JSONArray value at index: " + i, e2);
                    }
                }
                if (indentFactor > 0) {
                    writer.write(10);
                }
                JSONObject.indent(writer, indent);
            }
            writer.write(93);
            return writer;
        } catch (IOException e3) {
            throw new JSONException(e3);
        }
    }

    public List<Object> toList() {
        List<Object> results = new ArrayList<>(this.myArrayList.size());
        for (Object element : this.myArrayList) {
            if (element == null || JSONObject.NULL.equals(element)) {
                results.add(null);
            } else if (element instanceof JSONArray) {
                results.add(((JSONArray) element).toList());
            } else if (element instanceof JSONObject) {
                results.add(((JSONObject) element).toMap());
            } else {
                results.add(element);
            }
        }
        return results;
    }

    public boolean isEmpty() {
        return this.myArrayList.isEmpty();
    }

    private void addAll(Collection<?> collection, boolean wrap, int recursionDepth, JSONParserConfiguration jsonParserConfiguration) {
        this.myArrayList.ensureCapacity(this.myArrayList.size() + collection.size());
        if (wrap) {
            for (Object o : collection) {
                put(JSONObject.wrap(o, recursionDepth + 1, jsonParserConfiguration));
            }
            return;
        }
        for (Object o2 : collection) {
            put(o2);
        }
    }

    private void addAll(Iterable<?> iter, boolean wrap) {
        if (wrap) {
            for (Object o : iter) {
                put(JSONObject.wrap(o));
            }
            return;
        }
        for (Object o2 : iter) {
            put(o2);
        }
    }

    private void addAll(Object array, boolean wrap) throws JSONException {
        addAll(array, wrap, 0);
    }

    private void addAll(Object array, boolean wrap, int recursionDepth) {
        addAll(array, wrap, recursionDepth, new JSONParserConfiguration());
    }

    private void addAll(Object array, boolean wrap, int recursionDepth, JSONParserConfiguration jsonParserConfiguration) throws JSONException {
        if (!array.getClass().isArray()) {
            if (array instanceof JSONArray) {
                this.myArrayList.addAll(((JSONArray) array).myArrayList);
                return;
            } else if (array instanceof Collection) {
                addAll((Collection) array, wrap, recursionDepth);
                return;
            } else {
                if (array instanceof Iterable) {
                    addAll((Iterable<?>) array, wrap);
                    return;
                }
                throw new JSONException("JSONArray initial value should be a string or collection or array.");
            }
        }
        int length = Array.getLength(array);
        this.myArrayList.ensureCapacity(this.myArrayList.size() + length);
        if (wrap) {
            for (int i = 0; i < length; i++) {
                put(JSONObject.wrap(Array.get(array, i), recursionDepth + 1, jsonParserConfiguration));
            }
            return;
        }
        for (int i2 = 0; i2 < length; i2++) {
            put(Array.get(array, i2));
        }
    }

    private static JSONException wrongValueFormatException(int idx, String valueType, Object value, Throwable cause) {
        if (value == null) {
            return new JSONException("JSONArray[" + idx + "] is not a " + valueType + " (null).", cause);
        }
        if ((value instanceof Map) || (value instanceof Iterable) || (value instanceof JSONObject)) {
            return new JSONException("JSONArray[" + idx + "] is not a " + valueType + " (" + value.getClass() + ").", cause);
        }
        return new JSONException("JSONArray[" + idx + "] is not a " + valueType + " (" + value.getClass() + " : " + value + ").", cause);
    }
}
