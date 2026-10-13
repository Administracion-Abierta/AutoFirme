package org.glassfish.json;

import javax.json.JsonString;
import javax.json.JsonValue;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonStringImpl.class */
final class JsonStringImpl implements JsonString {
    private final String value;

    JsonStringImpl(String value) {
        this.value = value;
    }

    @Override // javax.json.JsonString
    public String getString() {
        return this.value;
    }

    @Override // javax.json.JsonString
    public CharSequence getChars() {
        return this.value;
    }

    @Override // javax.json.JsonValue
    public JsonValue.ValueType getValueType() {
        return JsonValue.ValueType.STRING;
    }

    @Override // javax.json.JsonString
    public int hashCode() {
        return getString().hashCode();
    }

    @Override // javax.json.JsonString
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonString)) {
            return false;
        }
        JsonString other = (JsonString) obj;
        return getString().equals(other.getString());
    }

    @Override // javax.json.JsonValue
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('\"');
        for (int i = 0; i < this.value.length(); i++) {
            char c = this.value.charAt(i);
            if (c >= ' ' && c <= 65535 && c != '\"' && c != '\\') {
                sb.append(c);
            } else {
                switch (c) {
                    case '\b':
                        sb.append('\\');
                        sb.append('b');
                        break;
                    case '\t':
                        sb.append('\\');
                        sb.append('t');
                        break;
                    case '\n':
                        sb.append('\\');
                        sb.append('n');
                        break;
                    case '\f':
                        sb.append('\\');
                        sb.append('f');
                        break;
                    case '\r':
                        sb.append('\\');
                        sb.append('r');
                        break;
                    case '\"':
                    case '\\':
                        sb.append('\\');
                        sb.append(c);
                        break;
                    default:
                        String hex = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(hex.substring(hex.length() - 4));
                        break;
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
}
