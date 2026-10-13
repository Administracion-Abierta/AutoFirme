package org.mozilla.javascript.json;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/json/JsonParser.class */
public class JsonParser {
    private Context cx;
    private Scriptable scope;
    private int pos;
    private int length;
    private String src;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !JsonParser.class.desiredAssertionStatus();
    }

    public JsonParser(Context cx, Scriptable scope) {
        this.cx = cx;
        this.scope = scope;
    }

    public synchronized Object parseValue(String json) throws ParseException {
        if (json == null) {
            throw new ParseException("Input string may not be null");
        }
        this.pos = 0;
        this.length = json.length();
        this.src = json;
        Object value = readValue();
        consumeWhitespace();
        if (this.pos < this.length) {
            throw new ParseException("Expected end of stream at char " + this.pos);
        }
        return value;
    }

    private Object readValue() throws ParseException {
        consumeWhitespace();
        if (this.pos < this.length) {
            String str = this.src;
            int i = this.pos;
            this.pos = i + 1;
            char c = str.charAt(i);
            switch (c) {
                case '\"':
                    return readString();
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return readNumber(c);
                case '[':
                    return readArray();
                case 'f':
                    return readFalse();
                case 'n':
                    return readNull();
                case 't':
                    return readTrue();
                case '{':
                    return readObject();
                default:
                    throw new ParseException("Unexpected token: " + c);
            }
        }
        throw new ParseException("Empty JSON string");
    }

    private Object readObject() throws ParseException {
        boolean z;
        consumeWhitespace();
        Scriptable object = this.cx.newObject(this.scope);
        if (this.pos < this.length && this.src.charAt(this.pos) == '}') {
            this.pos++;
            return object;
        }
        boolean needsComma = false;
        while (this.pos < this.length) {
            String str = this.src;
            int i = this.pos;
            this.pos = i + 1;
            char c = str.charAt(i);
            switch (c) {
                case '\"':
                    if (needsComma) {
                        throw new ParseException("Missing comma in object literal");
                    }
                    String id = readString();
                    consume(':');
                    Object value = readValue();
                    long index = ScriptRuntime.indexFromString(id);
                    if (index < 0) {
                        object.put(id, object, value);
                    } else {
                        object.put((int) index, object, value);
                    }
                    z = true;
                    break;
                    break;
                case ',':
                    if (!needsComma) {
                        throw new ParseException("Unexpected comma in object literal");
                    }
                    z = false;
                    break;
                    break;
                case '}':
                    if (!needsComma) {
                        throw new ParseException("Unexpected comma in object literal");
                    }
                    return object;
                default:
                    throw new ParseException("Unexpected token in object literal");
            }
            needsComma = z;
            consumeWhitespace();
        }
        throw new ParseException("Unterminated object literal");
    }

    private Object readArray() throws ParseException {
        consumeWhitespace();
        if (this.pos < this.length && this.src.charAt(this.pos) == ']') {
            this.pos++;
            return this.cx.newArray(this.scope, 0);
        }
        List<Object> list = new ArrayList<>();
        boolean needsComma = false;
        while (this.pos < this.length) {
            char c = this.src.charAt(this.pos);
            switch (c) {
                case ',':
                    if (!needsComma) {
                        throw new ParseException("Unexpected comma in array literal");
                    }
                    needsComma = false;
                    this.pos++;
                    break;
                    break;
                case ']':
                    if (!needsComma) {
                        throw new ParseException("Unexpected comma in array literal");
                    }
                    this.pos++;
                    return this.cx.newArray(this.scope, list.toArray());
                default:
                    if (needsComma) {
                        throw new ParseException("Missing comma in array literal");
                    }
                    list.add(readValue());
                    needsComma = true;
                    break;
                    break;
            }
            consumeWhitespace();
        }
        throw new ParseException("Unterminated array literal");
    }

    private String readString() throws ParseException {
        int stringStart = this.pos;
        while (this.pos < this.length) {
            String str = this.src;
            int i = this.pos;
            this.pos = i + 1;
            char c = str.charAt(i);
            if (c <= 31) {
                throw new ParseException("String contains control character");
            }
            if (c == '\\') {
                break;
            }
            if (c == '\"') {
                return this.src.substring(stringStart, this.pos - 1);
            }
        }
        StringBuilder b = new StringBuilder();
        while (this.pos < this.length) {
            if (!$assertionsDisabled && this.src.charAt(this.pos - 1) != '\\') {
                throw new AssertionError();
            }
            b.append((CharSequence) this.src, stringStart, this.pos - 1);
            if (this.pos >= this.length) {
                throw new ParseException("Unterminated string");
            }
            String str2 = this.src;
            int i2 = this.pos;
            this.pos = i2 + 1;
            char c2 = str2.charAt(i2);
            switch (c2) {
                case '\"':
                    b.append('\"');
                    break;
                case '/':
                    b.append('/');
                    break;
                case '\\':
                    b.append('\\');
                    break;
                case 'b':
                    b.append('\b');
                    break;
                case 'f':
                    b.append('\f');
                    break;
                case 'n':
                    b.append('\n');
                    break;
                case 'r':
                    b.append('\r');
                    break;
                case 't':
                    b.append('\t');
                    break;
                case 'u':
                    if (this.length - this.pos < 5) {
                        throw new ParseException("Invalid character code: \\u" + this.src.substring(this.pos));
                    }
                    int code = (fromHex(this.src.charAt(this.pos + 0)) << 12) | (fromHex(this.src.charAt(this.pos + 1)) << 8) | (fromHex(this.src.charAt(this.pos + 2)) << 4) | fromHex(this.src.charAt(this.pos + 3));
                    if (code < 0) {
                        throw new ParseException("Invalid character code: " + this.src.substring(this.pos, this.pos + 4));
                    }
                    this.pos += 4;
                    b.append((char) code);
                    break;
                    break;
                default:
                    throw new ParseException("Unexpected character in string: '\\" + c2 + OperatorName.SHOW_TEXT_LINE);
            }
            stringStart = this.pos;
            while (this.pos < this.length) {
                String str3 = this.src;
                int i3 = this.pos;
                this.pos = i3 + 1;
                char c3 = str3.charAt(i3);
                if (c3 <= 31) {
                    throw new ParseException("String contains control character");
                }
                if (c3 == '\\') {
                    break;
                }
                if (c3 == '\"') {
                    b.append((CharSequence) this.src, stringStart, this.pos - 1);
                    return b.toString();
                }
            }
        }
        throw new ParseException("Unterminated string literal");
    }

    private static int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        }
        if (c < 'a' || c > 'f') {
            return -1;
        }
        return (c - 'a') + 10;
    }

    private Number readNumber(char c) throws ParseException {
        char c2;
        if (!$assertionsDisabled && c != '-' && (c < '0' || c > '9')) {
            throw new AssertionError();
        }
        int numberStart = this.pos - 1;
        if (c == '-') {
            c = nextOrNumberError(numberStart);
            if (c < '0' || c > '9') {
                throw numberError(numberStart, this.pos);
            }
        }
        if (c != '0') {
            readDigits();
        }
        if (this.pos < this.length && this.src.charAt(this.pos) == '.') {
            this.pos++;
            char c3 = nextOrNumberError(numberStart);
            if (c3 < '0' || c3 > '9') {
                throw numberError(numberStart, this.pos);
            }
            readDigits();
        }
        if (this.pos < this.length && ((c2 = this.src.charAt(this.pos)) == 'e' || c2 == 'E')) {
            this.pos++;
            char c4 = nextOrNumberError(numberStart);
            if (c4 == '-' || c4 == '+') {
                c4 = nextOrNumberError(numberStart);
            }
            if (c4 < '0' || c4 > '9') {
                throw numberError(numberStart, this.pos);
            }
            readDigits();
        }
        String num = this.src.substring(numberStart, this.pos);
        double dval = Double.parseDouble(num);
        int ival = (int) dval;
        if (ival == dval) {
            return Integer.valueOf(ival);
        }
        return Double.valueOf(dval);
    }

    private ParseException numberError(int start, int end) {
        return new ParseException("Unsupported number format: " + this.src.substring(start, end));
    }

    private char nextOrNumberError(int numberStart) throws ParseException {
        if (this.pos >= this.length) {
            throw numberError(numberStart, this.length);
        }
        String str = this.src;
        int i = this.pos;
        this.pos = i + 1;
        return str.charAt(i);
    }

    private void readDigits() {
        char c;
        while (this.pos < this.length && (c = this.src.charAt(this.pos)) >= '0' && c <= '9') {
            this.pos++;
        }
    }

    private Boolean readTrue() throws ParseException {
        if (this.length - this.pos < 3 || this.src.charAt(this.pos) != 'r' || this.src.charAt(this.pos + 1) != 'u' || this.src.charAt(this.pos + 2) != 'e') {
            throw new ParseException("Unexpected token: t");
        }
        this.pos += 3;
        return Boolean.TRUE;
    }

    private Boolean readFalse() throws ParseException {
        if (this.length - this.pos < 4 || this.src.charAt(this.pos) != 'a' || this.src.charAt(this.pos + 1) != 'l' || this.src.charAt(this.pos + 2) != 's' || this.src.charAt(this.pos + 3) != 'e') {
            throw new ParseException("Unexpected token: f");
        }
        this.pos += 4;
        return Boolean.FALSE;
    }

    private Object readNull() throws ParseException {
        if (this.length - this.pos < 3 || this.src.charAt(this.pos) != 'u' || this.src.charAt(this.pos + 1) != 'l' || this.src.charAt(this.pos + 2) != 'l') {
            throw new ParseException("Unexpected token: n");
        }
        this.pos += 3;
        return null;
    }

    private void consumeWhitespace() {
        while (this.pos < this.length) {
            char c = this.src.charAt(this.pos);
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    this.pos++;
                    break;
                default:
                    return;
            }
        }
    }

    private void consume(char token) throws ParseException {
        consumeWhitespace();
        if (this.pos >= this.length) {
            throw new ParseException("Expected " + token + " but reached end of stream");
        }
        String str = this.src;
        int i = this.pos;
        this.pos = i + 1;
        char c = str.charAt(i);
        if (c == token) {
        } else {
            throw new ParseException("Expected " + token + " found " + c);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/json/JsonParser$ParseException.class */
    public static class ParseException extends Exception {
        private static final long serialVersionUID = 4804542791749920772L;

        ParseException(String message) {
            super(message);
        }

        ParseException(Exception cause) {
            super(cause);
        }
    }
}
