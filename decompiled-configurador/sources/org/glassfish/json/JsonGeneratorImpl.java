package org.glassfish.json;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonGeneratorImpl.class */
class JsonGeneratorImpl implements JsonGenerator {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final char[] INT_MIN_VALUE_CHARS = "-2147483648".toCharArray();
    private static final int[] INT_CHARS_SIZE_TABLE = {9, 99, Comparator.UNDECIDABLE, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    private static final char[] DIGIT_TENS = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
    private static final char[] DIGIT_ONES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final BufferPool bufferPool;
    private final Writer writer;
    private Context currentContext;
    private final Deque<Context> stack;
    private final char[] buf;
    private int len;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonGeneratorImpl$Scope.class */
    private enum Scope {
        IN_NONE,
        IN_OBJECT,
        IN_ARRAY
    }

    JsonGeneratorImpl(Writer writer, BufferPool bufferPool) {
        this.currentContext = new Context(Scope.IN_NONE);
        this.stack = new ArrayDeque();
        this.len = 0;
        this.writer = writer;
        this.bufferPool = bufferPool;
        this.buf = bufferPool.take();
    }

    JsonGeneratorImpl(OutputStream out, BufferPool bufferPool) {
        this(out, UTF_8, bufferPool);
    }

    JsonGeneratorImpl(OutputStream out, Charset encoding, BufferPool bufferPool) {
        this(new OutputStreamWriter(out, encoding), bufferPool);
    }

    @Override // javax.json.stream.JsonGenerator, java.io.Flushable
    public void flush() {
        flushBuffer();
        try {
            this.writer.flush();
        } catch (IOException ioe) {
            throw new JsonException(JsonMessages.GENERATOR_FLUSH_IO_ERR(), ioe);
        }
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeStartObject() {
        if (this.currentContext.scope == Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        if (this.currentContext.scope == Scope.IN_NONE && !this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_MULTIPLE_TEXT());
        }
        writeComma();
        writeChar('{');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_OBJECT);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeStartObject(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeChar('{');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_OBJECT);
        return this;
    }

    private JsonGenerator writeName(String name) {
        writeComma();
        writeEscapedString(name);
        writeChar(':');
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, String fieldValue) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeEscapedString(fieldValue);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, int value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeInt(value);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, long value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeString(String.valueOf(value));
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, double value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new NumberFormatException(JsonMessages.GENERATOR_DOUBLE_INFINITE_NAN());
        }
        writeName(name);
        writeString(String.valueOf(value));
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, BigInteger value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeString(String.valueOf(value));
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, BigDecimal value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeString(String.valueOf(value));
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, boolean value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeString(value ? "true" : "false");
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeNull(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeString("null");
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(JsonValue value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        switch (value.getValueType()) {
            case ARRAY:
                JsonArray array = (JsonArray) value;
                writeStartArray();
                for (JsonValue child : array) {
                    write(child);
                }
                writeEnd();
                break;
            case OBJECT:
                JsonObject object = (JsonObject) value;
                writeStartObject();
                for (Map.Entry<String, JsonValue> member : object.entrySet()) {
                    write(member.getKey(), member.getValue());
                }
                writeEnd();
                break;
            case STRING:
                JsonString str = (JsonString) value;
                write(str.getString());
                break;
            case NUMBER:
                JsonNumber number = (JsonNumber) value;
                writeValue(number.toString());
                break;
            case TRUE:
                write(true);
                break;
            case FALSE:
                write(false);
                break;
            case NULL:
                writeNull();
                break;
        }
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeStartArray() {
        if (this.currentContext.scope == Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        if (this.currentContext.scope == Scope.IN_NONE && !this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_MULTIPLE_TEXT());
        }
        writeComma();
        writeChar('[');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_ARRAY);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeStartArray(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeName(name);
        writeChar('[');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_ARRAY);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String name, JsonValue value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        switch (value.getValueType()) {
            case ARRAY:
                JsonArray array = (JsonArray) value;
                writeStartArray(name);
                for (JsonValue child : array) {
                    write(child);
                }
                writeEnd();
                break;
            case OBJECT:
                JsonObject object = (JsonObject) value;
                writeStartObject(name);
                for (Map.Entry<String, JsonValue> member : object.entrySet()) {
                    write(member.getKey(), member.getValue());
                }
                writeEnd();
                break;
            case STRING:
                JsonString str = (JsonString) value;
                write(name, str.getString());
                break;
            case NUMBER:
                JsonNumber number = (JsonNumber) value;
                writeValue(name, number.toString());
                break;
            case TRUE:
                write(name, true);
                break;
            case FALSE:
                write(name, false);
                break;
            case NULL:
                writeNull(name);
                break;
        }
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(String value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeComma();
        writeEscapedString(value);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(int value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeComma();
        writeInt(value);
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(long value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeValue(String.valueOf(value));
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(double value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new NumberFormatException(JsonMessages.GENERATOR_DOUBLE_INFINITE_NAN());
        }
        writeValue(String.valueOf(value));
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(BigInteger value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeValue(value.toString());
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(BigDecimal value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeValue(value.toString());
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator write(boolean value) {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeComma();
        writeString(value ? "true" : "false");
        return this;
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeNull() {
        if (this.currentContext.scope != Scope.IN_ARRAY) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
        }
        writeComma();
        writeString("null");
        return this;
    }

    private void writeValue(String value) {
        writeComma();
        writeString(value);
    }

    private void writeValue(String name, String value) {
        writeComma();
        writeEscapedString(name);
        writeChar(':');
        writeString(value);
    }

    @Override // javax.json.stream.JsonGenerator
    public JsonGenerator writeEnd() {
        if (this.currentContext.scope == Scope.IN_NONE) {
            throw new JsonGenerationException("writeEnd() cannot be called in no context");
        }
        writeChar(this.currentContext.scope == Scope.IN_ARRAY ? ']' : '}');
        this.currentContext = this.stack.pop();
        return this;
    }

    protected void writeComma() {
        if (!this.currentContext.first) {
            writeChar(',');
        }
        this.currentContext.first = false;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonGeneratorImpl$Context.class */
    private static class Context {
        boolean first = true;
        final Scope scope;

        Context(Scope scope) {
            this.scope = scope;
        }
    }

    @Override // javax.json.stream.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.currentContext.scope != Scope.IN_NONE || this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_INCOMPLETE_JSON());
        }
        flushBuffer();
        try {
            this.writer.close();
            this.bufferPool.recycle(this.buf);
        } catch (IOException ioe) {
            throw new JsonException(JsonMessages.GENERATOR_CLOSE_IO_ERR(), ioe);
        }
    }

    void writeEscapedString(String string) {
        char c;
        writeChar('\"');
        int len = string.length();
        int i = 0;
        while (i < len) {
            int begin = i;
            int end = i;
            char cCharAt = string.charAt(i);
            while (true) {
                c = cCharAt;
                if (c >= ' ' && c <= 65535 && c != '\"' && c != '\\') {
                    i++;
                    end = i;
                    if (i < len) {
                        cCharAt = string.charAt(i);
                    }
                }
            }
            if (begin < end) {
                writeString(string, begin, end);
                if (i == len) {
                    writeChar('\"');
                }
            }
            switch (c) {
                case '\b':
                    writeChar('\\');
                    writeChar('b');
                    break;
                case '\t':
                    writeChar('\\');
                    writeChar('t');
                    break;
                case '\n':
                    writeChar('\\');
                    writeChar('n');
                    break;
                case '\f':
                    writeChar('\\');
                    writeChar('f');
                    break;
                case '\r':
                    writeChar('\\');
                    writeChar('r');
                    break;
                case '\"':
                case '\\':
                    writeChar('\\');
                    writeChar(c);
                    break;
                default:
                    String hex = "000" + Integer.toHexString(c);
                    writeString("\\u" + hex.substring(hex.length() - 4));
                    break;
            }
            i++;
        }
        writeChar('\"');
    }

    void writeString(String str, int begin, int end) {
        while (begin < end) {
            int no = Math.min(this.buf.length - this.len, end - begin);
            str.getChars(begin, begin + no, this.buf, this.len);
            begin += no;
            this.len += no;
            if (this.len >= this.buf.length) {
                flushBuffer();
            }
        }
    }

    void writeString(String str) {
        writeString(str, 0, str.length());
    }

    void writeChar(char c) {
        if (this.len >= this.buf.length) {
            flushBuffer();
        }
        char[] cArr = this.buf;
        int i = this.len;
        this.len = i + 1;
        cArr[i] = c;
    }

    void writeInt(int num) {
        int size;
        if (num == Integer.MIN_VALUE) {
            size = INT_MIN_VALUE_CHARS.length;
        } else {
            size = num < 0 ? stringSize(-num) + 1 : stringSize(num);
        }
        if (this.len + size >= this.buf.length) {
            flushBuffer();
        }
        if (num == Integer.MIN_VALUE) {
            System.arraycopy(INT_MIN_VALUE_CHARS, 0, this.buf, this.len, size);
        } else {
            fillIntChars(num, this.buf, this.len + size);
        }
        this.len += size;
    }

    void flushBuffer() {
        try {
            if (this.len > 0) {
                this.writer.write(this.buf, 0, this.len);
                this.len = 0;
            }
        } catch (IOException ioe) {
            throw new JsonException(JsonMessages.GENERATOR_WRITE_IO_ERR(), ioe);
        }
    }

    private static int stringSize(int x) {
        int i = 0;
        while (x > INT_CHARS_SIZE_TABLE[i]) {
            i++;
        }
        return i + 1;
    }

    private static void fillIntChars(int i, char[] buf, int index) {
        int charPos = index;
        char sign = 0;
        if (i < 0) {
            sign = '-';
            i = -i;
        }
        while (i >= 65536) {
            int q = i / 100;
            int r = i - (((q << 6) + (q << 5)) + (q << 2));
            i = q;
            int charPos2 = charPos - 1;
            buf[charPos2] = DIGIT_ONES[r];
            charPos = charPos2 - 1;
            buf[charPos] = DIGIT_TENS[r];
        }
        do {
            int q2 = (i * 52429) >>> 19;
            charPos--;
            buf[charPos] = DIGITS[i - ((q2 << 3) + (q2 << 1))];
            i = q2;
        } while (i != 0);
        if (sign != 0) {
            buf[charPos - 1] = sign;
        }
    }
}
