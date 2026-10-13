package org.apache.pdfbox.pdfparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/BaseParser.class */
public abstract class BaseParser {
    private static final long OBJECT_NUMBER_THRESHOLD = 10000000000L;
    private static final long GENERATION_NUMBER_THRESHOLD = 65535;
    private final CharsetDecoder utf8Decoder = Charsets.UTF_8.newDecoder();
    protected static final int E = 101;
    protected static final int N = 110;
    protected static final int D = 100;
    protected static final int S = 115;
    protected static final int T = 116;
    protected static final int R = 114;
    protected static final int A = 97;
    protected static final int M = 109;
    protected static final int O = 111;
    protected static final int B = 98;
    protected static final int J = 106;
    public static final String DEF = "def";
    protected static final String ENDOBJ_STRING = "endobj";
    protected static final String ENDSTREAM_STRING = "endstream";
    protected static final String STREAM_STRING = "stream";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String NULL = "null";
    protected static final byte ASCII_LF = 10;
    protected static final byte ASCII_CR = 13;
    private static final byte ASCII_ZERO = 48;
    private static final byte ASCII_NINE = 57;
    private static final byte ASCII_SPACE = 32;
    final SequentialSource seqSource;
    protected COSDocument document;
    static final int MAX_LENGTH_LONG = Long.toString(Long.MAX_VALUE).length();
    private static final Log LOG = LogFactory.getLog(BaseParser.class);

    BaseParser(SequentialSource pdfSource) {
        this.seqSource = pdfSource;
    }

    private static boolean isHexDigit(char ch) {
        return isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    private COSBase parseCOSDictionaryValue() throws IOException {
        long numOffset = this.seqSource.getPosition();
        COSBase value = parseDirObject();
        skipSpaces();
        if (!(value instanceof COSNumber) || !isDigit()) {
            return value;
        }
        long genOffset = this.seqSource.getPosition();
        COSBase generationNumber = parseDirObject();
        skipSpaces();
        readExpectedChar('R');
        if (!(value instanceof COSInteger)) {
            LOG.error("expected number, actual=" + value + " at offset " + numOffset);
            return COSNull.NULL;
        }
        if (!(generationNumber instanceof COSInteger)) {
            LOG.error("expected number, actual=" + value + " at offset " + genOffset);
            return COSNull.NULL;
        }
        COSObjectKey key = new COSObjectKey(((COSInteger) value).longValue(), ((COSInteger) generationNumber).intValue());
        return getObjectFromPool(key);
    }

    private COSBase getObjectFromPool(COSObjectKey key) throws IOException {
        if (this.document == null) {
            throw new IOException("object reference " + key + " at offset " + this.seqSource.getPosition() + " in content stream");
        }
        return this.document.getObjectFromPool(key);
    }

    protected COSDictionary parseCOSDictionary() throws IOException {
        readExpectedChar('<');
        readExpectedChar('<');
        skipSpaces();
        COSDictionary obj = new COSDictionary();
        boolean done = false;
        while (!done) {
            skipSpaces();
            char c = (char) this.seqSource.peek();
            if (c == '>') {
                done = true;
            } else if (c == '/') {
                if (!parseCOSDictionaryNameValuePair(obj)) {
                    return obj;
                }
            } else {
                LOG.warn("Invalid dictionary, found: '" + c + "' but expected: '/' at offset " + this.seqSource.getPosition());
                if (readUntilEndOfCOSDictionary()) {
                    return obj;
                }
            }
        }
        readExpectedChar('>');
        readExpectedChar('>');
        return obj;
    }

    private boolean readUntilEndOfCOSDictionary() throws IOException {
        int c;
        int i = this.seqSource.read();
        while (true) {
            c = i;
            if (c == -1 || c == 47 || c == 62) {
                break;
            }
            if (c == 101 && this.seqSource.read() == 110 && this.seqSource.read() == 100) {
                int c2 = this.seqSource.read();
                boolean isStream = c2 == 115 && this.seqSource.read() == 116 && this.seqSource.read() == 114 && this.seqSource.read() == 101 && this.seqSource.read() == 97 && this.seqSource.read() == 109;
                boolean isObj = !isStream && c2 == 111 && this.seqSource.read() == 98 && this.seqSource.read() == 106;
                if (isStream || isObj) {
                    return true;
                }
            }
            i = this.seqSource.read();
        }
        if (c == -1) {
            return true;
        }
        this.seqSource.unread(c);
        return false;
    }

    private boolean parseCOSDictionaryNameValuePair(COSDictionary obj) throws IOException {
        COSName key = parseCOSName();
        COSBase value = parseCOSDictionaryValue();
        skipSpaces();
        if (value == null) {
            LOG.warn("Bad dictionary declaration at offset " + this.seqSource.getPosition());
            return false;
        }
        if ((value instanceof COSInteger) && !((COSInteger) value).isValid()) {
            LOG.warn("Skipped out of range number value at offset " + this.seqSource.getPosition());
            return true;
        }
        value.setDirect(true);
        obj.setItem(key, value);
        return true;
    }

    protected void skipWhiteSpaces() throws IOException {
        int whitespace;
        int i = this.seqSource.read();
        while (true) {
            whitespace = i;
            if (32 != whitespace) {
                break;
            } else {
                i = this.seqSource.read();
            }
        }
        if (13 == whitespace) {
            int whitespace2 = this.seqSource.read();
            if (10 != whitespace2) {
                this.seqSource.unread(whitespace2);
                return;
            }
            return;
        }
        if (10 != whitespace) {
            this.seqSource.unread(whitespace);
        }
    }

    private int checkForEndOfString(int bracesParameter) throws IOException {
        int braces = bracesParameter;
        byte[] nextThreeBytes = new byte[3];
        int amountRead = this.seqSource.read(nextThreeBytes);
        if (amountRead == 3 && nextThreeBytes[0] == 13 && ((nextThreeBytes[1] == 10 && nextThreeBytes[2] == 47) || nextThreeBytes[2] == 62 || nextThreeBytes[1] == 47 || nextThreeBytes[1] == 62)) {
            braces = 0;
        }
        if (amountRead > 0) {
            this.seqSource.unread(nextThreeBytes, 0, amountRead);
        }
        return braces;
    }

    protected COSString parseCOSString() throws IOException {
        int c;
        int c2;
        char nextChar = (char) this.seqSource.read();
        if (nextChar == '<') {
            return parseCOSHexString();
        }
        if (nextChar != '(') {
            throw new IOException("parseCOSString string should start with '(' or '<' and not '" + nextChar + "' at offset " + this.seqSource.getPosition());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int braces = 1;
        int i = this.seqSource.read();
        while (true) {
            c = i;
            if (braces > 0 && c != -1) {
                char ch = (char) c;
                int nextc = -2;
                if (ch == ')') {
                    braces = checkForEndOfString(braces - 1);
                    if (braces != 0) {
                        out.write(ch);
                    }
                } else if (ch == '(') {
                    braces++;
                    out.write(ch);
                } else if (ch == '\\') {
                    char next = (char) this.seqSource.read();
                    switch (next) {
                        case '\n':
                        case '\r':
                            int i2 = this.seqSource.read();
                            while (true) {
                                c2 = i2;
                                if (isEOL(c2) && c2 != -1) {
                                    i2 = this.seqSource.read();
                                }
                            }
                            nextc = c2;
                            break;
                        case '(':
                        case '\\':
                            out.write(next);
                            break;
                        case ')':
                            braces = checkForEndOfString(braces);
                            if (braces != 0) {
                                out.write(next);
                            } else {
                                out.write(92);
                            }
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                            StringBuilder octal = new StringBuilder();
                            octal.append(next);
                            int c3 = this.seqSource.read();
                            char digit = (char) c3;
                            if (digit >= '0' && digit <= '7') {
                                octal.append(digit);
                                int c4 = this.seqSource.read();
                                char digit2 = (char) c4;
                                if (digit2 >= '0' && digit2 <= '7') {
                                    octal.append(digit2);
                                } else {
                                    nextc = c4;
                                }
                            } else {
                                nextc = c3;
                            }
                            try {
                                int character = Integer.parseInt(octal.toString(), 8);
                                out.write(character);
                            } catch (NumberFormatException e) {
                                throw new IOException("Error: Expected octal character, actual='" + ((Object) octal) + OperatorName.SHOW_TEXT_LINE, e);
                            }
                            break;
                        case 'b':
                            out.write(8);
                            break;
                        case 'f':
                            out.write(12);
                            break;
                        case 'n':
                            out.write(10);
                            break;
                        case 'r':
                            out.write(13);
                            break;
                        case 't':
                            out.write(9);
                            break;
                        default:
                            out.write(next);
                            break;
                    }
                } else {
                    out.write(ch);
                }
                if (nextc != -2) {
                    i = nextc;
                } else {
                    i = this.seqSource.read();
                }
            }
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        return new COSString(out.toByteArray());
    }

    private COSString parseCOSHexString() throws IOException {
        int c;
        StringBuilder sBuf = new StringBuilder();
        while (true) {
            int c2 = this.seqSource.read();
            if (isHexDigit((char) c2)) {
                sBuf.append((char) c2);
            } else {
                if (c2 == 62) {
                    break;
                }
                if (c2 < 0) {
                    throw new IOException("Missing closing bracket for hex string. Reached EOS.");
                }
                if (c2 != 32 && c2 != 10 && c2 != 9 && c2 != 13 && c2 != 8 && c2 != 12) {
                    if (sBuf.length() % 2 != 0) {
                        sBuf.deleteCharAt(sBuf.length() - 1);
                    }
                    do {
                        c = this.seqSource.read();
                        if (c == 62) {
                            break;
                        }
                    } while (c >= 0);
                    if (c >= 0) {
                        break;
                    }
                    throw new IOException("Missing closing bracket for hex string. Reached EOS.");
                }
            }
        }
        return COSString.parseHex(sBuf.toString());
    }

    protected COSArray parseCOSArray() throws IOException {
        long startPosition = this.seqSource.getPosition();
        readExpectedChar('[');
        COSArray po = new COSArray();
        skipSpaces();
        while (true) {
            int i = this.seqSource.peek();
            if (i <= 0 || ((char) i) == ']') {
                break;
            }
            COSBase pbo = parseDirObject();
            if (pbo instanceof COSObject) {
                if (po.size() > 0 && (po.get(po.size() - 1) instanceof COSInteger)) {
                    COSInteger genNumber = (COSInteger) po.remove(po.size() - 1);
                    if (po.size() > 0 && (po.get(po.size() - 1) instanceof COSInteger)) {
                        COSInteger number = (COSInteger) po.remove(po.size() - 1);
                        COSObjectKey key = new COSObjectKey(number.longValue(), genNumber.intValue());
                        pbo = getObjectFromPool(key);
                    } else {
                        pbo = null;
                    }
                } else {
                    pbo = null;
                }
            }
            if (pbo != null) {
                po.add(pbo);
            } else {
                LOG.warn("Corrupt array element at offset " + this.seqSource.getPosition() + ", start offset: " + startPosition);
                String isThisTheEnd = readString();
                if (isThisTheEnd.isEmpty() && this.seqSource.peek() == 91) {
                    return po;
                }
                this.seqSource.unread(isThisTheEnd.getBytes(Charsets.ISO_8859_1));
                if (ENDOBJ_STRING.equals(isThisTheEnd) || ENDSTREAM_STRING.equals(isThisTheEnd)) {
                    return po;
                }
            }
            skipSpaces();
        }
        this.seqSource.read();
        skipSpaces();
        return po;
    }

    protected boolean isEndOfName(int ch) {
        return ch == 32 || ch == 13 || ch == 10 || ch == 9 || ch == 62 || ch == 60 || ch == 91 || ch == 47 || ch == 93 || ch == 41 || ch == 40 || ch == 0 || ch == 12 || ch == 37;
    }

    protected COSName parseCOSName() throws IOException {
        String string;
        readExpectedChar('/');
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int c = this.seqSource.read();
        while (c != -1) {
            int ch = c;
            if (ch == 35) {
                int ch1 = this.seqSource.read();
                int ch2 = this.seqSource.read();
                if (isHexDigit((char) ch1) && isHexDigit((char) ch2)) {
                    String hex = Character.toString((char) ch1) + ((char) ch2);
                    try {
                        buffer.write(Integer.parseInt(hex, 16));
                        c = this.seqSource.read();
                    } catch (NumberFormatException e) {
                        throw new IOException("Error: expected hex digit, actual='" + hex + OperatorName.SHOW_TEXT_LINE, e);
                    }
                } else {
                    if (ch2 == -1 || ch1 == -1) {
                        LOG.error("Premature EOF in BaseParser#parseCOSName");
                        c = -1;
                        break;
                    }
                    this.seqSource.unread(ch2);
                    c = ch1;
                    buffer.write(ch);
                }
            } else {
                if (isEndOfName(ch)) {
                    break;
                }
                buffer.write(ch);
                c = this.seqSource.read();
            }
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        byte[] bytes = buffer.toByteArray();
        if (isValidUTF8(bytes)) {
            string = new String(buffer.toByteArray(), Charsets.UTF_8);
        } else {
            string = new String(buffer.toByteArray(), Charsets.WINDOWS_1252);
        }
        return COSName.getPDFName(string);
    }

    private boolean isValidUTF8(byte[] input) {
        try {
            this.utf8Decoder.decode(ByteBuffer.wrap(input));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    protected COSBoolean parseBoolean() throws IOException {
        COSBoolean retval;
        char c = (char) this.seqSource.peek();
        if (c == 't') {
            String trueString = new String(this.seqSource.readFully(4), Charsets.ISO_8859_1);
            if (!trueString.equals(TRUE)) {
                throw new IOException("Error parsing boolean: expected='true' actual='" + trueString + "' at offset " + this.seqSource.getPosition());
            }
            retval = COSBoolean.TRUE;
        } else if (c == 'f') {
            String falseString = new String(this.seqSource.readFully(5), Charsets.ISO_8859_1);
            if (!falseString.equals(FALSE)) {
                throw new IOException("Error parsing boolean: expected='true' actual='" + falseString + "' at offset " + this.seqSource.getPosition());
            }
            retval = COSBoolean.FALSE;
        } else {
            throw new IOException("Error parsing boolean expected='t or f' actual='" + c + "' at offset " + this.seqSource.getPosition());
        }
        return retval;
    }

    protected COSBase parseDirObject() throws IOException {
        skipSpaces();
        char c = (char) this.seqSource.peek();
        switch (c) {
            case '(':
                return parseCOSString();
            case '/':
                return parseCOSName();
            case '<':
                int leftBracket = this.seqSource.read();
                char c2 = (char) this.seqSource.peek();
                this.seqSource.unread(leftBracket);
                return c2 == '<' ? parseCOSDictionary() : parseCOSString();
            case 'R':
                this.seqSource.read();
                return new COSObject(null);
            case '[':
                return parseCOSArray();
            case 'f':
                String falseString = new String(this.seqSource.readFully(5), Charsets.ISO_8859_1);
                if (falseString.equals(FALSE)) {
                    return COSBoolean.FALSE;
                }
                throw new IOException("expected false actual='" + falseString + "' " + this.seqSource + "' at offset " + this.seqSource.getPosition());
            case 'n':
                readExpectedString(NULL);
                return COSNull.NULL;
            case 't':
                String trueString = new String(this.seqSource.readFully(4), Charsets.ISO_8859_1);
                if (trueString.equals(TRUE)) {
                    return COSBoolean.TRUE;
                }
                throw new IOException("expected true actual='" + trueString + "' " + this.seqSource + "' at offset " + this.seqSource.getPosition());
            case 65535:
                return null;
            default:
                if (Character.isDigit(c) || c == '-' || c == '+' || c == '.') {
                    return parseCOSNumber();
                }
                long startOffset = this.seqSource.getPosition();
                String badString = readString();
                if (badString.isEmpty()) {
                    int peek = this.seqSource.peek();
                    throw new IOException("Unknown dir object c='" + c + "' cInt=" + ((int) c) + " peek='" + ((char) peek) + "' peekInt=" + peek + " at offset " + this.seqSource.getPosition() + " (start offset: " + startOffset + ")");
                }
                if (ENDOBJ_STRING.equals(badString) || ENDSTREAM_STRING.equals(badString)) {
                    this.seqSource.unread(badString.getBytes(Charsets.ISO_8859_1));
                    return null;
                }
                LOG.warn("Skipped unexpected dir object = '" + badString + "' at offset " + this.seqSource.getPosition() + " (start offset: " + startOffset + ")");
                return null;
        }
    }

    private COSNumber parseCOSNumber() throws IOException {
        int ic;
        StringBuilder buf = new StringBuilder();
        int i = this.seqSource.read();
        while (true) {
            ic = i;
            char c = (char) ic;
            if (!Character.isDigit(c) && c != '-' && c != '+' && c != '.' && c != 'E' && c != 'e') {
                break;
            }
            buf.append(c);
            i = this.seqSource.read();
        }
        if (ic != -1) {
            this.seqSource.unread(ic);
        }
        return COSNumber.get(buf.toString());
    }

    protected String readString() throws IOException {
        int c;
        skipSpaces();
        StringBuilder buffer = new StringBuilder();
        int i = this.seqSource.read();
        while (true) {
            c = i;
            if (isEndOfName((char) c) || c == -1) {
                break;
            }
            buffer.append((char) c);
            i = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        return buffer.toString();
    }

    protected void readExpectedString(String expectedString) throws IOException {
        readExpectedString(expectedString.toCharArray(), false);
    }

    protected final void readExpectedString(char[] expectedString, boolean skipSpaces) throws IOException {
        skipSpaces();
        for (char c : expectedString) {
            if (this.seqSource.read() != c) {
                throw new IOException("Expected string '" + new String(expectedString) + "' but missed at character '" + c + "' at offset " + this.seqSource.getPosition());
            }
        }
        skipSpaces();
    }

    protected void readExpectedChar(char ec) throws IOException {
        char c = (char) this.seqSource.read();
        if (c != ec) {
            throw new IOException("expected='" + ec + "' actual='" + c + "' at offset " + this.seqSource.getPosition());
        }
    }

    protected String readString(int length) throws IOException {
        skipSpaces();
        int c = this.seqSource.read();
        StringBuilder buffer = new StringBuilder(length);
        while (!isWhitespace(c) && !isClosing(c) && c != -1 && buffer.length() < length && c != 91 && c != 60 && c != 40 && c != 47) {
            buffer.append((char) c);
            c = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        return buffer.toString();
    }

    protected boolean isClosing() throws IOException {
        return isClosing(this.seqSource.peek());
    }

    protected boolean isClosing(int c) {
        return c == 93;
    }

    protected String readLine() throws IOException {
        int c;
        if (this.seqSource.isEOF()) {
            throw new IOException("Error: End-of-File, expected line");
        }
        StringBuilder buffer = new StringBuilder(11);
        while (true) {
            c = this.seqSource.read();
            if (c == -1 || isEOL(c)) {
                break;
            }
            buffer.append((char) c);
        }
        if (isCR(c) && isLF(this.seqSource.peek())) {
            this.seqSource.read();
        }
        return buffer.toString();
    }

    protected boolean isEOL() throws IOException {
        return isEOL(this.seqSource.peek());
    }

    protected boolean isEOL(int c) {
        return isLF(c) || isCR(c);
    }

    private boolean isLF(int c) {
        return 10 == c;
    }

    private boolean isCR(int c) {
        return 13 == c;
    }

    protected boolean isWhitespace() throws IOException {
        return isWhitespace(this.seqSource.peek());
    }

    protected boolean isWhitespace(int c) {
        return c == 0 || c == 9 || c == 12 || c == 10 || c == 13 || c == 32;
    }

    protected boolean isSpace() throws IOException {
        return isSpace(this.seqSource.peek());
    }

    protected boolean isSpace(int c) {
        return 32 == c;
    }

    protected boolean isDigit() throws IOException {
        return isDigit(this.seqSource.peek());
    }

    protected static boolean isDigit(int c) {
        return c >= 48 && c <= 57;
    }

    protected void skipSpaces() throws IOException {
        int c = this.seqSource.read();
        while (true) {
            if (!isWhitespace(c) && c != 37) {
                break;
            }
            if (c == 37) {
                int i = this.seqSource.read();
                while (true) {
                    c = i;
                    if (isEOL(c) || c == -1) {
                        break;
                    } else {
                        i = this.seqSource.read();
                    }
                }
            } else {
                c = this.seqSource.read();
            }
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
    }

    protected long readObjectNumber() throws IOException {
        long retval = readLong();
        if (retval < 0 || retval >= OBJECT_NUMBER_THRESHOLD) {
            throw new IOException("Object Number '" + retval + "' has more than 10 digits or is negative");
        }
        return retval;
    }

    protected int readGenerationNumber() throws IOException {
        int retval = readInt();
        if (retval < 0 || retval > GENERATION_NUMBER_THRESHOLD) {
            throw new IOException("Generation Number '" + retval + "' has more than 5 digits");
        }
        return retval;
    }

    protected int readInt() throws IOException {
        skipSpaces();
        StringBuilder intBuffer = readStringNumber();
        try {
            int retval = Integer.parseInt(intBuffer.toString());
            return retval;
        } catch (NumberFormatException e) {
            this.seqSource.unread(intBuffer.toString().getBytes(Charsets.ISO_8859_1));
            throw new IOException("Error: Expected an integer type at offset " + this.seqSource.getPosition() + ", instead got '" + ((Object) intBuffer) + OperatorName.SHOW_TEXT_LINE, e);
        }
    }

    protected long readLong() throws IOException {
        skipSpaces();
        StringBuilder longBuffer = readStringNumber();
        try {
            long retval = Long.parseLong(longBuffer.toString());
            return retval;
        } catch (NumberFormatException e) {
            this.seqSource.unread(longBuffer.toString().getBytes(Charsets.ISO_8859_1));
            throw new IOException("Error: Expected a long type at offset " + this.seqSource.getPosition() + ", instead got '" + ((Object) longBuffer) + OperatorName.SHOW_TEXT_LINE, e);
        }
    }

    protected final StringBuilder readStringNumber() throws IOException {
        StringBuilder buffer = new StringBuilder();
        do {
            int lastByte = this.seqSource.read();
            if (lastByte >= 48 && lastByte <= 57) {
                buffer.append((char) lastByte);
            } else {
                if (lastByte != -1) {
                    this.seqSource.unread(lastByte);
                }
                return buffer;
            }
        } while (buffer.length() <= MAX_LENGTH_LONG);
        throw new IOException("Number '" + ((Object) buffer) + "' is getting too long, stop reading at offset " + this.seqSource.getPosition());
    }
}
