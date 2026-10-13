package org.json;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/json/JSONWriter.class */
public class JSONWriter {
    private static final int maxdepth = 200;
    private boolean comma = false;
    protected char mode = 'i';
    private final JSONObject[] stack = new JSONObject[200];
    private int top = 0;
    protected Appendable writer;

    public JSONWriter(Appendable w) {
        this.writer = w;
    }

    private JSONWriter append(String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null pointer");
        }
        if (this.mode == 'o' || this.mode == 'a') {
            try {
                if (this.comma && this.mode == 'a') {
                    this.writer.append(',');
                }
                this.writer.append(string);
                if (this.mode == 'o') {
                    this.mode = 'k';
                }
                this.comma = true;
                return this;
            } catch (IOException e) {
                throw new JSONException(e);
            }
        }
        throw new JSONException("Value out of sequence.");
    }

    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            push(null);
            append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }

    private JSONWriter end(char m, char c) throws JSONException {
        if (this.mode != m) {
            throw new JSONException(m == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
        }
        pop(m);
        try {
            this.writer.append(c);
            this.comma = true;
            return this;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public JSONWriter endArray() throws JSONException {
        return end('a', ']');
    }

    public JSONWriter endObject() throws JSONException {
        return end('k', '}');
    }

    public JSONWriter key(String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null key.");
        }
        if (this.mode == 'k') {
            try {
                JSONObject topObject = this.stack[this.top - 1];
                if (topObject.has(string)) {
                    throw new JSONException("Duplicate key \"" + string + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                }
                topObject.put(string, true);
                if (this.comma) {
                    this.writer.append(',');
                }
                this.writer.append(JSONObject.quote(string));
                this.writer.append(':');
                this.comma = false;
                this.mode = 'o';
                return this;
            } catch (IOException e) {
                throw new JSONException(e);
            }
        }
        throw new JSONException("Misplaced key.");
    }

    public JSONWriter object() throws JSONException {
        if (this.mode == 'i') {
            this.mode = 'o';
        }
        if (this.mode == 'o' || this.mode == 'a') {
            append("{");
            push(new JSONObject());
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }

    private void pop(char c) throws JSONException {
        char c2;
        if (this.top <= 0) {
            throw new JSONException("Nesting error.");
        }
        char m = this.stack[this.top - 1] == null ? 'a' : 'k';
        if (m != c) {
            throw new JSONException("Nesting error.");
        }
        this.top--;
        if (this.top == 0) {
            c2 = 'd';
        } else {
            c2 = this.stack[this.top - 1] == null ? 'a' : 'k';
        }
        this.mode = c2;
    }

    private void push(JSONObject jo) throws JSONException {
        if (this.top >= 200) {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = jo;
        this.mode = jo == null ? 'a' : 'k';
        this.top++;
    }

    public static String valueToString(Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            try {
                String object = ((JSONString) value).toJSONString();
                if (object != null) {
                    return object;
                }
                throw new JSONException("Bad value from toJSONString: " + object);
            } catch (Exception e) {
                throw new JSONException(e);
            }
        }
        if (value instanceof Number) {
            String numberAsString = JSONObject.numberToString((Number) value);
            if (JSONObject.NUMBER_PATTERN.matcher(numberAsString).matches()) {
                return numberAsString;
            }
            return JSONObject.quote(numberAsString);
        }
        if ((value instanceof Boolean) || (value instanceof JSONObject) || (value instanceof JSONArray)) {
            return value.toString();
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            return new JSONObject(map).toString();
        }
        if (value instanceof Collection) {
            Collection<?> coll = (Collection) value;
            return new JSONArray(coll).toString();
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString();
        }
        if (value instanceof Enum) {
            return JSONObject.quote(((Enum) value).name());
        }
        return JSONObject.quote(value.toString());
    }

    public JSONWriter value(boolean b) throws JSONException {
        return append(b ? "true" : "false");
    }

    public JSONWriter value(double d) throws JSONException {
        return value(Double.valueOf(d));
    }

    public JSONWriter value(long l) throws JSONException {
        return append(Long.toString(l));
    }

    public JSONWriter value(Object object) throws JSONException {
        return append(valueToString(object));
    }
}
