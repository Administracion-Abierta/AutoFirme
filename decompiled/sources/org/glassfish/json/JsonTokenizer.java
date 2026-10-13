package org.glassfish.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.json.JsonException;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonTokenizer.class */
final class JsonTokenizer implements Closeable {
    private static final int[] HEX;
    private static final int HEX_LENGTH;
    private final BufferPool bufferPool;
    private final Reader reader;
    private char[] buf;
    private int readBegin;
    private int readEnd;
    private int storeBegin;
    private int storeEnd;
    private long lineNo = 1;
    private long lastLineOffset = 0;
    private long bufferOffset = 0;
    private boolean minus;
    private boolean fracOrExp;
    private BigDecimal bd;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !JsonTokenizer.class.desiredAssertionStatus();
        HEX = new int[128];
        Arrays.fill(HEX, -1);
        for (int i = 48; i <= 57; i++) {
            HEX[i] = i - 48;
        }
        for (int i2 = 65; i2 <= 70; i2++) {
            HEX[i2] = (10 + i2) - 65;
        }
        for (int i3 = 97; i3 <= 102; i3++) {
            HEX[i3] = (10 + i3) - 97;
        }
        HEX_LENGTH = HEX.length;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonTokenizer$JsonToken.class */
    enum JsonToken {
        CURLYOPEN(JsonParser.Event.START_OBJECT, false),
        SQUAREOPEN(JsonParser.Event.START_ARRAY, false),
        COLON(null, false),
        COMMA(null, false),
        STRING(JsonParser.Event.VALUE_STRING, true),
        NUMBER(JsonParser.Event.VALUE_NUMBER, true),
        TRUE(JsonParser.Event.VALUE_TRUE, true),
        FALSE(JsonParser.Event.VALUE_FALSE, true),
        NULL(JsonParser.Event.VALUE_NULL, true),
        CURLYCLOSE(JsonParser.Event.END_OBJECT, false),
        SQUARECLOSE(JsonParser.Event.END_ARRAY, false),
        EOF(null, false);

        private final JsonParser.Event event;
        private final boolean value;

        JsonToken(JsonParser.Event event, boolean value) {
            this.event = event;
            this.value = value;
        }

        JsonParser.Event getEvent() {
            return this.event;
        }

        boolean isValue() {
            return this.value;
        }
    }

    JsonTokenizer(Reader reader, BufferPool bufferPool) {
        this.reader = reader;
        this.bufferPool = bufferPool;
        this.buf = bufferPool.take();
    }

    private void readString() {
        char c;
        boolean inPlace = true;
        int i = this.readBegin;
        this.storeEnd = i;
        this.storeBegin = i;
        while (true) {
            if (inPlace) {
                while (this.readBegin < this.readEnd && (c = this.buf[this.readBegin]) >= ' ' && c != '\\') {
                    if (c == '\"') {
                        int i2 = this.readBegin;
                        this.readBegin = i2 + 1;
                        this.storeEnd = i2;
                        return;
                    }
                    this.readBegin++;
                }
                this.storeEnd = this.readBegin;
            }
            int ch = read();
            if (ch >= 32 && ch != 34 && ch != 92) {
                if (!inPlace) {
                    this.buf[this.storeEnd] = (char) ch;
                }
                this.storeEnd++;
            } else {
                switch (ch) {
                    case 34:
                        return;
                    case 92:
                        inPlace = false;
                        unescape();
                        break;
                    default:
                        throw unexpectedChar(ch);
                }
            }
        }
    }

    private void unescape() {
        int ch = read();
        switch (ch) {
            case 34:
            case 47:
            case 92:
                char[] cArr = this.buf;
                int i = this.storeEnd;
                this.storeEnd = i + 1;
                cArr[i] = (char) ch;
                return;
            case 98:
                char[] cArr2 = this.buf;
                int i2 = this.storeEnd;
                this.storeEnd = i2 + 1;
                cArr2[i2] = '\b';
                return;
            case 102:
                char[] cArr3 = this.buf;
                int i3 = this.storeEnd;
                this.storeEnd = i3 + 1;
                cArr3[i3] = '\f';
                return;
            case 110:
                char[] cArr4 = this.buf;
                int i4 = this.storeEnd;
                this.storeEnd = i4 + 1;
                cArr4[i4] = '\n';
                return;
            case 114:
                char[] cArr5 = this.buf;
                int i5 = this.storeEnd;
                this.storeEnd = i5 + 1;
                cArr5[i5] = '\r';
                return;
            case 116:
                char[] cArr6 = this.buf;
                int i6 = this.storeEnd;
                this.storeEnd = i6 + 1;
                cArr6[i6] = '\t';
                return;
            case 117:
                int unicode = 0;
                for (int i7 = 0; i7 < 4; i7++) {
                    int ch3 = read();
                    int digit = (ch3 < 0 || ch3 >= HEX_LENGTH) ? -1 : HEX[ch3];
                    if (digit < 0) {
                        throw unexpectedChar(ch3);
                    }
                    unicode = (unicode << 4) | digit;
                }
                char[] cArr7 = this.buf;
                int i8 = this.storeEnd;
                this.storeEnd = i8 + 1;
                cArr7[i8] = (char) unicode;
                return;
            default:
                throw unexpectedChar(ch);
        }
    }

    private int readNumberChar() {
        if (this.readBegin < this.readEnd) {
            char[] cArr = this.buf;
            int i = this.readBegin;
            this.readBegin = i + 1;
            return cArr[i];
        }
        this.storeEnd = this.readBegin;
        return read();
    }

    private void readNumber(int ch) {
        int ch2;
        int i = this.readBegin - 1;
        this.storeEnd = i;
        this.storeBegin = i;
        if (ch == 45) {
            this.minus = true;
            ch = readNumberChar();
            if (ch < 48 || ch > 57) {
                throw unexpectedChar(ch);
            }
        }
        if (ch != 48) {
            do {
                ch2 = readNumberChar();
                if (ch2 < 48) {
                    break;
                }
            } while (ch2 <= 57);
        } else {
            ch2 = readNumberChar();
        }
        if (ch2 == 46) {
            this.fracOrExp = true;
            int count = 0;
            do {
                ch2 = readNumberChar();
                count++;
                if (ch2 < 48) {
                    break;
                }
            } while (ch2 <= 57);
            if (count == 1) {
                throw unexpectedChar(ch2);
            }
        }
        if (ch2 == 101 || ch2 == 69) {
            this.fracOrExp = true;
            int ch3 = readNumberChar();
            if (ch3 == 43 || ch3 == 45) {
                ch3 = readNumberChar();
            }
            int count2 = 0;
            while (ch3 >= 48 && ch3 <= 57) {
                ch3 = readNumberChar();
                count2++;
            }
            if (count2 == 0) {
                throw unexpectedChar(ch3);
            }
        }
        this.readBegin--;
        this.storeEnd = this.readBegin;
    }

    private void readTrue() {
        int ch1 = read();
        if (ch1 != 114) {
            throw expectedChar(ch1, 'r');
        }
        int ch2 = read();
        if (ch2 != 117) {
            throw expectedChar(ch2, 'u');
        }
        int ch3 = read();
        if (ch3 != 101) {
            throw expectedChar(ch3, 'e');
        }
    }

    private void readFalse() {
        int ch1 = read();
        if (ch1 != 97) {
            throw expectedChar(ch1, 'a');
        }
        int ch2 = read();
        if (ch2 != 108) {
            throw expectedChar(ch2, 'l');
        }
        int ch3 = read();
        if (ch3 != 115) {
            throw expectedChar(ch3, 's');
        }
        int ch4 = read();
        if (ch4 != 101) {
            throw expectedChar(ch4, 'e');
        }
    }

    private void readNull() {
        int ch1 = read();
        if (ch1 != 117) {
            throw expectedChar(ch1, 'u');
        }
        int ch2 = read();
        if (ch2 != 108) {
            throw expectedChar(ch2, 'l');
        }
        int ch3 = read();
        if (ch3 != 108) {
            throw expectedChar(ch3, 'l');
        }
    }

    JsonToken nextToken() {
        reset();
        int ch = read();
        while (true) {
            if (ch != 32 && ch != 9 && ch != 10 && ch != 13) {
                break;
            }
            if (ch == 13) {
                this.lineNo++;
                ch = read();
                if (ch == 10) {
                    this.lastLineOffset = this.bufferOffset + ((long) this.readBegin);
                } else {
                    this.lastLineOffset = (this.bufferOffset + ((long) this.readBegin)) - 1;
                }
            } else if (ch == 10) {
                this.lineNo++;
                this.lastLineOffset = this.bufferOffset + ((long) this.readBegin);
            }
            ch = read();
        }
        switch (ch) {
            case -1:
                return JsonToken.EOF;
            case 34:
                readString();
                return JsonToken.STRING;
            case 44:
                return JsonToken.COMMA;
            case 45:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                readNumber(ch);
                return JsonToken.NUMBER;
            case 58:
                return JsonToken.COLON;
            case 91:
                return JsonToken.SQUAREOPEN;
            case 93:
                return JsonToken.SQUARECLOSE;
            case 102:
                readFalse();
                return JsonToken.FALSE;
            case 110:
                readNull();
                return JsonToken.NULL;
            case 116:
                readTrue();
                return JsonToken.TRUE;
            case 123:
                return JsonToken.CURLYOPEN;
            case 125:
                return JsonToken.CURLYCLOSE;
            default:
                throw unexpectedChar(ch);
        }
    }

    JsonLocation getLastCharLocation() {
        return new JsonLocationImpl(this.lineNo, (this.bufferOffset + ((long) this.readBegin)) - this.lastLineOffset, (this.bufferOffset + ((long) this.readBegin)) - 1);
    }

    JsonLocation getLocation() {
        return new JsonLocationImpl(this.lineNo, ((this.bufferOffset + ((long) this.readBegin)) - this.lastLineOffset) + 1, this.bufferOffset + ((long) this.readBegin));
    }

    private int read() {
        try {
            if (this.readBegin == this.readEnd) {
                int len = fillBuf();
                if (len == -1) {
                    return -1;
                }
                if (!$assertionsDisabled && len == 0) {
                    throw new AssertionError();
                }
                this.readBegin = this.storeEnd;
                this.readEnd = this.readBegin + len;
            }
            char[] cArr = this.buf;
            int i = this.readBegin;
            this.readBegin = i + 1;
            return cArr[i];
        } catch (IOException ioe) {
            throw new JsonException(JsonMessages.TOKENIZER_IO_ERR(), ioe);
        }
    }

    private int fillBuf() throws IOException {
        if (this.storeEnd != 0) {
            int storeLen = this.storeEnd - this.storeBegin;
            if (storeLen > 0) {
                if (storeLen == this.buf.length) {
                    char[] doubleBuf = Arrays.copyOf(this.buf, 2 * this.buf.length);
                    this.bufferPool.recycle(this.buf);
                    this.buf = doubleBuf;
                } else {
                    System.arraycopy(this.buf, this.storeBegin, this.buf, 0, storeLen);
                    this.storeEnd = storeLen;
                    this.storeBegin = 0;
                    this.bufferOffset += (long) (this.readBegin - this.storeEnd);
                }
            } else {
                this.storeEnd = 0;
                this.storeBegin = 0;
                this.bufferOffset += (long) this.readBegin;
            }
        } else {
            this.bufferOffset += (long) this.readBegin;
        }
        return this.reader.read(this.buf, this.storeEnd, this.buf.length - this.storeEnd);
    }

    private void reset() {
        if (this.storeEnd != 0) {
            this.storeBegin = 0;
            this.storeEnd = 0;
            this.bd = null;
            this.minus = false;
            this.fracOrExp = false;
        }
    }

    String getValue() {
        return new String(this.buf, this.storeBegin, this.storeEnd - this.storeBegin);
    }

    BigDecimal getBigDecimal() {
        if (this.bd == null) {
            this.bd = new BigDecimal(this.buf, this.storeBegin, this.storeEnd - this.storeBegin);
        }
        return this.bd;
    }

    int getInt() {
        int storeLen = this.storeEnd - this.storeBegin;
        if (!this.fracOrExp && (storeLen <= 9 || (this.minus && storeLen == 10))) {
            int num = 0;
            for (int i = this.minus ? 1 : 0; i < storeLen; i++) {
                num = (num * 10) + (this.buf[this.storeBegin + i] - '0');
            }
            return this.minus ? -num : num;
        }
        return getBigDecimal().intValue();
    }

    boolean isDefinitelyInt() {
        int storeLen = this.storeEnd - this.storeBegin;
        return !this.fracOrExp && (storeLen <= 9 || (this.minus && storeLen == 10));
    }

    boolean isIntegral() {
        return !this.fracOrExp || getBigDecimal().scale() == 0;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.reader.close();
        this.bufferPool.recycle(this.buf);
    }

    private JsonParsingException unexpectedChar(int ch) {
        JsonLocation location = getLastCharLocation();
        return new JsonParsingException(JsonMessages.TOKENIZER_UNEXPECTED_CHAR(ch, location), location);
    }

    private JsonParsingException expectedChar(int unexpected, char expected) {
        JsonLocation location = getLastCharLocation();
        return new JsonParsingException(JsonMessages.TOKENIZER_EXPECTED_CHAR(unexpected, location, expected), location);
    }
}
