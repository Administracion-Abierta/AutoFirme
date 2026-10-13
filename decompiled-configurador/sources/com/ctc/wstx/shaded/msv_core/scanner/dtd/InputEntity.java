package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/InputEntity.class */
public class InputEntity {
    private int start;
    private int finish;
    private char[] buf;
    private int lineNumber = 1;
    private boolean returnedFirstHalf = false;
    private boolean maybeInCRLF = false;
    private String name;
    private InputEntity next;
    private InputSource input;
    private Reader reader;
    private boolean isClosed;
    private DTDEventListener errHandler;
    private Locale locale;
    private StringBuffer rememberedText;
    private int startRemember;
    private boolean isPE;
    private static final int BUFSIZ = 8193;
    private static final char[] newline = {'\n'};

    public static InputEntity getInputEntity(DTDEventListener h, Locale l) {
        InputEntity retval = new InputEntity();
        retval.errHandler = h;
        retval.locale = l;
        return retval;
    }

    private InputEntity() {
    }

    public boolean isInternal() {
        return this.reader == null;
    }

    public boolean isDocument() {
        return this.next == null;
    }

    public boolean isParameterEntity() {
        return this.isPE;
    }

    public String getName() {
        return this.name;
    }

    public void init(InputSource in, String name, InputEntity stack, boolean isPE) throws SAXException, IOException {
        this.input = in;
        this.isPE = isPE;
        this.reader = in.getCharacterStream();
        if (this.reader == null) {
            InputStream bytes = in.getByteStream();
            if (bytes == null) {
                this.reader = XmlReader.createReader(new URL(in.getSystemId()).openStream());
            } else if (in.getEncoding() != null) {
                this.reader = XmlReader.createReader(in.getByteStream(), in.getEncoding());
            } else {
                this.reader = XmlReader.createReader(in.getByteStream());
            }
        }
        this.next = stack;
        this.buf = new char[BUFSIZ];
        this.name = name;
        checkRecursion(stack);
    }

    public void init(char[] b, String name, InputEntity stack, boolean isPE) throws SAXException {
        this.next = stack;
        this.buf = b;
        this.finish = b.length;
        this.name = name;
        this.isPE = isPE;
        checkRecursion(stack);
    }

    private void checkRecursion(InputEntity stack) throws SAXException {
        if (stack == null) {
            return;
        }
        InputEntity inputEntity = stack.next;
        while (true) {
            InputEntity stack2 = inputEntity;
            if (stack2 != null) {
                if (stack2.name != null && stack2.name.equals(this.name)) {
                    fatal("P-069", new Object[]{this.name});
                }
                inputEntity = stack2.next;
            } else {
                return;
            }
        }
    }

    public InputEntity pop() throws IOException {
        close();
        return this.next;
    }

    public boolean isEOF() throws SAXException, IOException {
        if (this.start >= this.finish) {
            fillbuf();
            return this.start >= this.finish;
        }
        return false;
    }

    public String getEncoding() {
        if (this.reader == null) {
            return null;
        }
        if (this.reader instanceof XmlReader) {
            return ((XmlReader) this.reader).getEncoding();
        }
        if (this.reader instanceof InputStreamReader) {
            return ((InputStreamReader) this.reader).getEncoding();
        }
        return null;
    }

    public char getNameChar() throws SAXException, IOException {
        if (this.finish <= this.start) {
            fillbuf();
        }
        if (this.finish > this.start) {
            char[] cArr = this.buf;
            int i = this.start;
            this.start = i + 1;
            char c = cArr[i];
            if (XmlChars.isNameChar(c)) {
                return c;
            }
            this.start--;
            return (char) 0;
        }
        return (char) 0;
    }

    public char getc() throws SAXException, IOException {
        if (this.finish <= this.start) {
            fillbuf();
        }
        if (this.finish > this.start) {
            char[] cArr = this.buf;
            int i = this.start;
            this.start = i + 1;
            char c = cArr[i];
            if (this.returnedFirstHalf) {
                if (c >= 56320 && c <= 57343) {
                    this.returnedFirstHalf = false;
                    return c;
                }
                fatal("P-070", new Object[]{Integer.toHexString(c)});
            }
            if ((c >= ' ' && c <= 55295) || c == '\t' || (c >= 57344 && c <= 65533)) {
                return c;
            }
            if (c == '\r' && !isInternal()) {
                this.maybeInCRLF = true;
                if (getc() != '\n') {
                    ungetc();
                }
                this.maybeInCRLF = false;
                this.lineNumber++;
                return '\n';
            }
            if (c == '\n' || c == '\r') {
                if (!isInternal() && !this.maybeInCRLF) {
                    this.lineNumber++;
                }
                return c;
            }
            if (c >= 55296 && c < 56320) {
                this.returnedFirstHalf = true;
                return c;
            }
            fatal("P-071", new Object[]{Integer.toHexString(c)});
        }
        throw new EndOfInputException();
    }

    public boolean peekc(char c) throws SAXException, IOException {
        if (this.finish <= this.start) {
            fillbuf();
        }
        if (this.finish > this.start && this.buf[this.start] == c) {
            this.start++;
            return true;
        }
        return false;
    }

    public void ungetc() {
        if (this.start == 0) {
            throw new InternalError("ungetc");
        }
        this.start--;
        if (this.buf[this.start] == '\n' || this.buf[this.start] == '\r') {
            if (!isInternal()) {
                this.lineNumber--;
            }
        } else if (this.returnedFirstHalf) {
            this.returnedFirstHalf = false;
        }
    }

    public boolean maybeWhitespace() throws SAXException, IOException {
        boolean isSpace = false;
        boolean sawCR = false;
        while (true) {
            if (this.finish <= this.start) {
                fillbuf();
            }
            if (this.finish <= this.start) {
                return isSpace;
            }
            char[] cArr = this.buf;
            int i = this.start;
            this.start = i + 1;
            char c = cArr[i];
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                isSpace = true;
                if (c == '\n' || c == '\r') {
                    if (!isInternal()) {
                        if (c != '\n' || !sawCR) {
                            this.lineNumber++;
                            sawCR = false;
                        }
                        if (c == '\r') {
                            sawCR = true;
                        }
                    }
                }
            } else {
                this.start--;
                return isSpace;
            }
        }
    }

    public boolean parsedContent(DTDEventListener docHandler) throws SAXException, IOException {
        int i = this.start;
        int last = i;
        int first = i;
        boolean sawContent = false;
        while (true) {
            if (last >= this.finish) {
                if (last > first) {
                    docHandler.characters(this.buf, first, last - first);
                    sawContent = true;
                    this.start = last;
                }
                if (isEOF()) {
                    return sawContent;
                }
                first = this.start;
                last = first - 1;
            } else {
                char c = this.buf[last];
                if ((c <= ']' || c > 55295) && ((c >= '&' || c < ' ') && ((c <= '<' || c >= ']') && ((c <= '&' || c >= '<') && c != '\t' && (c < 57344 || c > 65533))))) {
                    if (c != '<' && c != '&') {
                        if (c == '\n') {
                            if (!isInternal()) {
                                this.lineNumber++;
                            }
                        } else if (c == '\r') {
                            if (!isInternal()) {
                                docHandler.characters(this.buf, first, last - first);
                                docHandler.characters(newline, 0, 1);
                                sawContent = true;
                                this.lineNumber++;
                                if (this.finish > last + 1 && this.buf[last + 1] == '\n') {
                                    last++;
                                }
                                int i2 = last + 1;
                                this.start = i2;
                                first = i2;
                            }
                        } else if (c == ']') {
                            switch (this.finish - last) {
                                case 1:
                                    break;
                                case 2:
                                    if (this.buf[last + 1] != ']') {
                                        continue;
                                    }
                                    break;
                                default:
                                    if (this.buf[last + 1] != ']') {
                                        continue;
                                    } else if (this.buf[last + 2] == '>') {
                                        fatal("P-072", null);
                                    }
                                    break;
                            }
                            if (this.reader != null && !this.isClosed) {
                                if (last == first) {
                                    throw new InternalError("fillbuf");
                                }
                                int last2 = last - 1;
                                if (last2 > first) {
                                    docHandler.characters(this.buf, first, last2 - first);
                                    sawContent = true;
                                    this.start = last2;
                                }
                                fillbuf();
                                int i3 = this.start;
                                last = i3;
                                first = i3;
                            }
                        } else if (c >= 55296 && c <= 57343) {
                            if (last + 1 >= this.finish) {
                                if (last > first) {
                                    docHandler.characters(this.buf, first, last - first);
                                    sawContent = true;
                                    this.start = last + 1;
                                }
                                if (isEOF()) {
                                    fatal("P-081", new Object[]{Integer.toHexString(c)});
                                }
                                first = this.start;
                                last = first;
                            } else if (checkSurrogatePair(last)) {
                                last++;
                            } else {
                                last--;
                            }
                        } else {
                            fatal("P-071", new Object[]{Integer.toHexString(c)});
                        }
                    }
                }
            }
            last++;
        }
        if (last == first) {
            return sawContent;
        }
        docHandler.characters(this.buf, first, last - first);
        this.start = last;
        return true;
    }

    public boolean unparsedContent(DTDEventListener docHandler, boolean ignorableWhitespace, String whitespaceInvalidMessage) throws SAXException, IOException {
        if (!peek("![CDATA[", null)) {
            return false;
        }
        docHandler.startCDATA();
        while (true) {
            boolean done = false;
            boolean white = ignorableWhitespace;
            int last = this.start;
            while (last < this.finish) {
                char c = this.buf[last];
                if (!XmlChars.isChar(c)) {
                    white = false;
                    if (c >= 55296 && c <= 57343) {
                        if (checkSurrogatePair(last)) {
                            last++;
                            last++;
                        } else {
                            last--;
                            break;
                        }
                    } else {
                        fatal("P-071", new Object[]{Integer.toHexString(this.buf[last])});
                    }
                }
                if (c == '\n') {
                    if (!isInternal()) {
                        this.lineNumber++;
                    }
                } else if (c == '\r') {
                    if (!isInternal()) {
                        if (white) {
                            if (whitespaceInvalidMessage != null) {
                                this.errHandler.error(new SAXParseException(DTDParser.messages.getMessage(this.locale, whitespaceInvalidMessage), null));
                            }
                            docHandler.ignorableWhitespace(this.buf, this.start, last - this.start);
                            docHandler.ignorableWhitespace(newline, 0, 1);
                        } else {
                            docHandler.characters(this.buf, this.start, last - this.start);
                            docHandler.characters(newline, 0, 1);
                        }
                        this.lineNumber++;
                        if (this.finish > last + 1 && this.buf[last + 1] == '\n') {
                            last++;
                        }
                        this.start = last + 1;
                    }
                } else if (c != ']') {
                    if (c != ' ' && c != '\t') {
                        white = false;
                    }
                } else if (last + 2 < this.finish) {
                    if (this.buf[last + 1] == ']' && this.buf[last + 2] == '>') {
                        done = true;
                        break;
                    }
                    white = false;
                } else {
                    break;
                }
                last++;
            }
            if (white) {
                if (whitespaceInvalidMessage != null) {
                    this.errHandler.error(new SAXParseException(DTDParser.messages.getMessage(this.locale, whitespaceInvalidMessage), null));
                }
                docHandler.ignorableWhitespace(this.buf, this.start, last - this.start);
            } else {
                docHandler.characters(this.buf, this.start, last - this.start);
            }
            if (done) {
                this.start = last + 3;
                docHandler.endCDATA();
                return true;
            }
            this.start = last;
            if (isEOF()) {
                fatal("P-073", null);
            }
        }
    }

    private boolean checkSurrogatePair(int offset) throws SAXException {
        if (offset + 1 >= this.finish) {
            return false;
        }
        char c1 = this.buf[offset];
        char c2 = this.buf[offset + 1];
        if (c1 >= 55296 && c1 < 56320 && c2 >= 56320 && c2 <= 57343) {
            return true;
        }
        fatal("P-074", new Object[]{Integer.toHexString(c1 & 65535), Integer.toHexString(c2 & 65535)});
        return false;
    }

    public boolean ignorableWhitespace(DTDEventListener handler) throws SAXException, IOException {
        boolean isSpace = false;
        int first = this.start;
        while (true) {
            if (this.finish <= this.start) {
                if (isSpace) {
                    handler.ignorableWhitespace(this.buf, first, this.start - first);
                }
                fillbuf();
                first = this.start;
            }
            if (this.finish <= this.start) {
                return isSpace;
            }
            char[] cArr = this.buf;
            int i = this.start;
            this.start = i + 1;
            char c = cArr[i];
            switch (c) {
                case '\t':
                case ' ':
                    break;
                case '\n':
                    if (!isInternal()) {
                        this.lineNumber++;
                    }
                    break;
                case '\r':
                    isSpace = true;
                    if (!isInternal()) {
                        this.lineNumber++;
                    }
                    handler.ignorableWhitespace(this.buf, first, (this.start - 1) - first);
                    handler.ignorableWhitespace(newline, 0, 1);
                    if (this.start < this.finish && this.buf[this.start] == '\n') {
                        this.start++;
                    }
                    first = this.start;
                    continue;
                default:
                    ungetc();
                    if (isSpace) {
                        handler.ignorableWhitespace(this.buf, first, this.start - first);
                    }
                    return isSpace;
            }
            isSpace = true;
        }
    }

    public boolean peek(String next, char[] chars) throws SAXException, IOException {
        int len;
        int i;
        if (chars != null) {
            len = chars.length;
        } else {
            len = next.length();
        }
        if (this.finish <= this.start || this.finish - this.start < len) {
            fillbuf();
        }
        if (this.finish <= this.start) {
            return false;
        }
        if (chars != null) {
            i = 0;
            while (i < len && this.start + i < this.finish) {
                if (this.buf[this.start + i] == chars[i]) {
                    i++;
                } else {
                    return false;
                }
            }
        } else {
            i = 0;
            while (i < len && this.start + i < this.finish) {
                if (this.buf[this.start + i] == next.charAt(i)) {
                    i++;
                } else {
                    return false;
                }
            }
        }
        if (i < len) {
            if (this.reader == null || this.isClosed) {
                return false;
            }
            if (len > this.buf.length) {
                fatal("P-077", new Object[]{new Integer(this.buf.length)});
            }
            fillbuf();
            return peek(next, chars);
        }
        this.start += len;
        return true;
    }

    public void startRemembering() {
        if (this.startRemember != 0) {
            throw new InternalError();
        }
        this.startRemember = this.start;
    }

    public String rememberText() {
        String retval;
        if (this.rememberedText != null) {
            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
            retval = this.rememberedText.toString();
        } else {
            retval = new String(this.buf, this.startRemember, this.start - this.startRemember);
        }
        this.startRemember = 0;
        this.rememberedText = null;
        return retval;
    }

    private InputEntity getTopEntity() {
        InputEntity current;
        InputEntity inputEntity = this;
        while (true) {
            current = inputEntity;
            if (current == null || current.input != null) {
                break;
            }
            inputEntity = current.next;
        }
        return current == null ? this : current;
    }

    public String getPublicId() {
        InputEntity where = getTopEntity();
        if (where == this) {
            return this.input.getPublicId();
        }
        return where.getPublicId();
    }

    public String getSystemId() {
        InputEntity where = getTopEntity();
        if (where == this) {
            return this.input.getSystemId();
        }
        return where.getSystemId();
    }

    public int getLineNumber() {
        InputEntity where = getTopEntity();
        if (where == this) {
            return this.lineNumber;
        }
        return where.getLineNumber();
    }

    public int getColumnNumber() {
        return -1;
    }

    private void fillbuf() throws SAXException, IOException {
        if (this.reader == null || this.isClosed) {
            return;
        }
        if (this.startRemember != 0) {
            if (this.rememberedText == null) {
                this.rememberedText = new StringBuffer(this.buf.length);
            }
            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
        }
        boolean extra = this.finish > 0 && this.start > 0;
        if (extra) {
            this.start--;
        }
        int len = this.finish - this.start;
        System.arraycopy(this.buf, this.start, this.buf, 0, len);
        this.start = 0;
        this.finish = len;
        try {
            len = this.reader.read(this.buf, this.finish, this.buf.length - len);
        } catch (CharConversionException e) {
            fatal("P-076", new Object[]{e.getMessage()});
        } catch (UnsupportedEncodingException e2) {
            fatal("P-075", new Object[]{e2.getMessage()});
        }
        if (len >= 0) {
            this.finish += len;
        } else {
            close();
        }
        if (extra) {
            this.start++;
        }
        if (this.startRemember != 0) {
            this.startRemember = 1;
        }
    }

    public void close() {
        try {
            if (this.reader != null && !this.isClosed) {
                this.reader.close();
            }
            this.isClosed = true;
        } catch (IOException e) {
        }
    }

    private void fatal(String messageId, Object[] params) throws SAXException {
        SAXParseException x = new SAXParseException(DTDParser.messages.getMessage(this.locale, messageId, params), null);
        close();
        this.errHandler.fatalError(x);
        throw x;
    }
}
