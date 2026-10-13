package com.sun.java.help.impl;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/DocumentParser.class */
public abstract class DocumentParser {
    static final char EOF = 65535;
    protected Reader source;
    int readOffset;
    ScanBuffer buffer;
    ScanBuffer escapeBuffer;
    ScanBuffer documentSource;
    boolean shouldCacheSource;
    Hashtable entities;
    int defaultCharBufferSize;
    char[] cb;
    int mySize;
    int myCount;

    protected abstract void flush(char[] cArr, int i, int i2);

    protected abstract void comment(String str);

    protected abstract void tag(String str, TagProperties tagProperties, boolean z, boolean z2);

    protected abstract void pi(String str, String str2);

    protected abstract void doctype(String str, String str2, String str3);

    protected abstract String documentAttribute(String str);

    protected abstract void errorString(String str);

    public DocumentParser(InputStream inputStream) {
        this.defaultCharBufferSize = 8192;
        this.cb = new char[this.defaultCharBufferSize];
        this.mySize = 0;
        this.myCount = 0;
        this.source = new MyBufferedReader(new InputStreamReader(inputStream));
        init();
    }

    public DocumentParser(Reader reader) {
        this.defaultCharBufferSize = 8192;
        this.cb = new char[this.defaultCharBufferSize];
        this.mySize = 0;
        this.myCount = 0;
        if (reader instanceof MyBufferedReader) {
            this.source = reader;
        } else {
            this.source = new MyBufferedReader(reader);
        }
        init();
    }

    public void setInput(Reader reader) {
        if (reader instanceof MyBufferedReader) {
            this.source = reader;
        } else {
            this.source = new MyBufferedReader(reader);
        }
    }

    public void setInput(InputStream inputStream, String str) throws UnsupportedEncodingException {
        if (inputStream == null) {
            this.source = null;
        } else {
            this.source = new MyBufferedReader(new InputStreamReader(inputStream, str));
        }
    }

    public void setShouldCacheSource(boolean z) {
        this.shouldCacheSource = z;
    }

    public String getDocumentSource() {
        if (!this.shouldCacheSource) {
            return null;
        }
        return new StringBuffer().append(new String(this.documentSource.buf, 0, 0 == this.documentSource.length() ? 0 : this.documentSource.length() - 1)).append("\n").toString();
    }

    public void parse() throws IOException {
        char escape = 65535;
        this.buffer.clear();
        if (this.source != null) {
            escape = readChar();
        }
        while (escape != 65535) {
            if (escape == '&') {
                escape = parseEscape();
            } else if (escape == '<') {
                this.buffer.flush(this);
                escape = parseTag();
            } else {
                if (this.buffer.buflen >= this.buffer.buf.length) {
                    char[] cArr = new char[this.buffer.buf.length * this.buffer.scale];
                    System.arraycopy(this.buffer.buf, 0, cArr, 0, this.buffer.buf.length);
                    this.buffer.buf = cArr;
                }
                if (escape != '\r') {
                    char[] cArr2 = this.buffer.buf;
                    ScanBuffer scanBuffer = this.buffer;
                    int i = scanBuffer.buflen;
                    scanBuffer.buflen = i + 1;
                    cArr2[i] = escape;
                }
                if (this.myCount >= this.mySize) {
                    try {
                        this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                        if (this.mySize < 0) {
                            break;
                        }
                        if (this.mySize == 0) {
                            System.err.println(" DocumentParser::parse() !!!ERROR !!! source.read(...) == 0");
                            break;
                        }
                        this.myCount = 0;
                    } catch (CharConversionException e) {
                        throw e;
                    } catch (IOException e2) {
                    }
                }
                if (this.shouldCacheSource) {
                    this.documentSource.add(this.cb[this.myCount]);
                }
                char[] cArr3 = this.cb;
                int i2 = this.myCount;
                this.myCount = i2 + 1;
                escape = cArr3[i2];
            }
        }
        this.buffer.flush(this);
    }

    public void parseText() throws IOException {
        tag("PRE", null, false, false);
        this.buffer.clear();
        char c = readChar();
        while (true) {
            char c2 = c;
            if (c2 != 65535) {
                this.buffer.add(c2);
                c = readChar();
            } else {
                this.buffer.flush(this);
                return;
            }
        }
    }

    protected void callFlush(char[] cArr, int i, int i2) {
        flush(cArr, i, i2);
    }

    private void init() {
        this.buffer = new ScanBuffer(8192, 4);
        this.escapeBuffer = new ScanBuffer(8192, 4);
        this.documentSource = new ScanBuffer(8192, 4);
        this.readOffset = 0;
    }

    protected void findCloseAngleForComment(char c) throws IOException {
        this.buffer.add(c);
        while (true) {
            char c2 = readChar();
            if (c2 != '>') {
                this.buffer.add(c2);
            } else {
                this.buffer.add(c2);
                comment(this.buffer.extract(0));
                this.buffer.clear();
                return;
            }
        }
    }

    protected char handleCommentOrDoctype(char c) throws IOException {
        this.buffer.add(c);
        int length = this.buffer.length();
        char cScanIdentifier = scanIdentifier(c);
        if (!this.buffer.extract(length).equals("DOCTYPE")) {
            findCloseAngleForComment(cScanIdentifier);
            return readChar();
        }
        char cSkipWhite = skipWhite(cScanIdentifier);
        int length2 = this.buffer.length();
        char cScanIdentifier2 = scanIdentifier(cSkipWhite);
        String strExtract = this.buffer.extract(length2);
        char cSkipWhite2 = skipWhite(cScanIdentifier2);
        if (cSkipWhite2 == '>') {
            this.buffer.clear();
            return readChar();
        }
        int length3 = this.buffer.length();
        char cScanIdentifier3 = scanIdentifier(cSkipWhite2);
        String strExtract2 = this.buffer.extract(length3);
        if (strExtract2.equals("SYSTEM")) {
            char cSkipWhite3 = skipWhite(cScanIdentifier3);
            int length4 = this.buffer.length();
            char cScanQuotedString = scanQuotedString(cSkipWhite3);
            doctype(strExtract, null, this.buffer.extract(length4));
            if (cScanQuotedString != '>') {
                findCloseAngleForComment(cScanQuotedString);
            }
            this.buffer.clear();
            return readChar();
        }
        if (strExtract2.equals("PUBLIC")) {
            char cSkipWhite4 = skipWhite(cScanIdentifier3);
            int length5 = this.buffer.length();
            char cScanQuotedString2 = scanQuotedString(cSkipWhite4);
            String strExtract3 = this.buffer.extract(length5);
            char cSkipWhite5 = skipWhite(cScanQuotedString2);
            int length6 = this.buffer.length();
            char cScanQuotedString3 = scanQuotedString(cSkipWhite5);
            doctype(strExtract, strExtract3, this.buffer.extract(length6));
            if (cScanQuotedString3 != '>') {
                findCloseAngleForComment(cScanQuotedString3);
            }
            this.buffer.clear();
            return readChar();
        }
        if (cScanIdentifier3 != '>') {
            findCloseAngleForComment(cScanIdentifier3);
        }
        findCloseAngleForComment(cScanIdentifier3);
        doctype(strExtract, null, null);
        this.buffer.clear();
        return readChar();
    }

    protected void setXmlEntities(TagProperties tagProperties) {
    }

    protected char parseTag() throws IOException {
        boolean z;
        boolean z2;
        String strExtract;
        String strExtract2;
        char c;
        this.buffer.clear();
        this.buffer.add('<');
        char c2 = readChar();
        if (c2 == '!') {
            this.buffer.add('!');
            char c3 = readChar();
            if (c3 != '-') {
                return handleCommentOrDoctype(c3);
            }
            this.buffer.add(c3);
            char c4 = readChar();
            if (c4 != '-') {
                findCloseAngleForComment(c4);
                return readChar();
            }
            this.buffer.add(c4);
            int length = this.buffer.length();
            int i = 0;
            while (true) {
                c2 = readChar();
                if (c2 == 65535) {
                    commentEOFError(length);
                    break;
                }
                if (c2 != '-') {
                    this.buffer.add(c2);
                } else {
                    while (c2 == '-') {
                        this.buffer.add(c2);
                        i++;
                        c2 = readChar();
                    }
                    if (c2 == 65535) {
                        commentEOFError(length);
                        break;
                    }
                    this.buffer.add(c2);
                    if (i >= 2 && c2 == '>') {
                        comment(this.buffer.extract(0));
                        this.buffer.clear();
                        return readChar();
                    }
                    i = 0;
                }
            }
        }
        if (c2 == '?') {
            StringBuffer stringBuffer = new StringBuffer();
            this.buffer.add('?');
            while (true) {
                char c5 = readChar();
                if (c5 == '\"' || c5 == ' ' || c5 == '\t' || c5 == '\n' || c5 == '>') {
                    break;
                }
                this.buffer.add(c5);
                stringBuffer.append(c5);
            }
            if (!stringBuffer.toString().equals("xml")) {
                this.buffer.clear();
                while (true) {
                    c = readChar();
                    if (c == '?' || c == 65535) {
                        break;
                    }
                    this.buffer.add(c);
                }
                if (c == 65535) {
                    eofError();
                    return readChar();
                }
                char c6 = readChar();
                if (c6 != '>' && c6 != 65535) {
                    this.buffer.add('?');
                    this.buffer.add(c6);
                }
                if (c6 == 65535) {
                    eofError();
                    return readChar();
                }
                pi(stringBuffer.toString(), this.buffer.extract(0));
                return readChar();
            }
            char cSkipWhite = readChar();
            TagProperties tagProperties = null;
            while (true) {
                char cSkipWhite2 = skipWhite(cSkipWhite);
                if (cSkipWhite2 == 65535) {
                    eofError();
                    return cSkipWhite2;
                }
                if (cSkipWhite2 != '?') {
                    int length2 = this.buffer.length();
                    char cScanIdentifier = scanIdentifier(cSkipWhite2);
                    if (length2 == this.buffer.length()) {
                        error("Expecting an attribute");
                        skipToCloseAngle(cScanIdentifier);
                        return readChar();
                    }
                    String strExtract3 = this.buffer.extract(length2);
                    cSkipWhite = skipWhite(cScanIdentifier);
                    if (tagProperties == null) {
                        tagProperties = new TagProperties();
                    }
                    if (cSkipWhite == '=') {
                        this.buffer.add(cSkipWhite);
                        cSkipWhite = skipWhite(readChar());
                        if (cSkipWhite == '?' || cSkipWhite == '<') {
                            strExtract2 = "";
                        } else if (cSkipWhite == '\"') {
                            this.buffer.add(cSkipWhite);
                            int length3 = this.buffer.length();
                            while (true) {
                                char escape = readChar();
                                if (escape == 65535) {
                                    eofError();
                                    return escape;
                                }
                                if (escape != '\"') {
                                    if (escape == '&') {
                                        escape = parseEscape();
                                    }
                                    this.buffer.add(escape);
                                } else {
                                    strExtract2 = this.buffer.extract(length3);
                                    this.buffer.add(escape);
                                    cSkipWhite = readChar();
                                    break;
                                }
                            }
                        } else {
                            int length4 = this.buffer.length();
                            this.buffer.add(cSkipWhite);
                            while (true) {
                                cSkipWhite = readChar();
                                if (cSkipWhite == 65535) {
                                    eofError();
                                    return cSkipWhite;
                                }
                                if (cSkipWhite == '\"' || cSkipWhite == ' ' || cSkipWhite == '\t' || cSkipWhite == '\n' || cSkipWhite == '?') {
                                    break;
                                }
                                if (cSkipWhite == '&') {
                                    cSkipWhite = parseEscape();
                                }
                                this.buffer.add(cSkipWhite);
                            }
                            strExtract2 = this.buffer.extract(length4);
                        }
                    } else {
                        strExtract2 = "true";
                    }
                    tagProperties.put(strExtract3, strExtract2);
                } else {
                    char c7 = readChar();
                    if (c7 == 65535) {
                        eofError();
                        return readChar();
                    }
                    char cSkipWhite3 = skipWhite(c7);
                    this.buffer.add(cSkipWhite3);
                    if (cSkipWhite3 == '>') {
                        setXmlEntities(tagProperties);
                        this.buffer.clear();
                        return readChar();
                    }
                    error("Expecting ?>");
                    skipToCloseAngle(cSkipWhite3);
                    return readChar();
                }
            }
        } else {
            char cSkipWhite4 = skipWhite(c2);
            if (cSkipWhite4 == 65535) {
                eofError();
                return cSkipWhite4;
            }
            if (cSkipWhite4 == '/') {
                this.buffer.add(cSkipWhite4);
                cSkipWhite4 = skipWhite(readChar());
                z = true;
                z2 = false;
            } else {
                z = false;
                z2 = false;
            }
            int length5 = this.buffer.length();
            char cScanIdentifier2 = scanIdentifier(cSkipWhite4);
            String strExtract4 = this.buffer.extract(length5);
            TagProperties tagProperties2 = null;
            while (true) {
                char cSkipWhite5 = skipWhite(cScanIdentifier2);
                if (cSkipWhite5 == 65535) {
                    eofError();
                    return cSkipWhite5;
                }
                if (cSkipWhite5 != '>') {
                    if (cSkipWhite5 == '/') {
                        this.buffer.add(cSkipWhite5);
                        char c8 = readChar();
                        if (c8 == '>') {
                            z = true;
                            z2 = true;
                        } else {
                            error("Expecting />");
                            skipToCloseAngle(c8);
                            return readChar();
                        }
                    } else {
                        if (cSkipWhite5 == '<') {
                            tag(strExtract4, tagProperties2, z, false);
                            this.buffer.clear();
                            return '<';
                        }
                        int length6 = this.buffer.length();
                        char cScanIdentifier3 = scanIdentifier(cSkipWhite5);
                        if (length6 == this.buffer.length()) {
                            error("Expecting an attribute (2)");
                            skipToCloseAngle(cScanIdentifier3);
                            return readChar();
                        }
                        String strExtract5 = this.buffer.extract(length6);
                        cScanIdentifier2 = skipWhite(cScanIdentifier3);
                        if (tagProperties2 == null) {
                            tagProperties2 = new TagProperties();
                        }
                        if (cScanIdentifier2 == '=') {
                            this.buffer.add(cScanIdentifier2);
                            cScanIdentifier2 = skipWhite(readChar());
                            if (cScanIdentifier2 == '>' || cScanIdentifier2 == '<') {
                                strExtract = "";
                            } else if (cScanIdentifier2 == '\"') {
                                this.buffer.add(cScanIdentifier2);
                                int length7 = this.buffer.length();
                                while (true) {
                                    char escape2 = readChar();
                                    if (escape2 == 65535) {
                                        eofError();
                                        return escape2;
                                    }
                                    if (escape2 != '\"') {
                                        if (escape2 == '&') {
                                            escape2 = parseEscape();
                                        }
                                        this.buffer.add(escape2);
                                    } else {
                                        strExtract = this.buffer.extract(length7);
                                        this.buffer.add(escape2);
                                        cScanIdentifier2 = readChar();
                                        break;
                                    }
                                }
                            } else {
                                int length8 = this.buffer.length();
                                this.buffer.add(cScanIdentifier2);
                                while (true) {
                                    cScanIdentifier2 = readChar();
                                    if (cScanIdentifier2 == 65535) {
                                        eofError();
                                        return cScanIdentifier2;
                                    }
                                    if (cScanIdentifier2 == '\"' || cScanIdentifier2 == ' ' || cScanIdentifier2 == '\t' || cScanIdentifier2 == '\n' || cScanIdentifier2 == '>') {
                                        break;
                                    }
                                    if (cScanIdentifier2 == '&') {
                                        cScanIdentifier2 = parseEscape();
                                    }
                                    this.buffer.add(cScanIdentifier2);
                                }
                                strExtract = this.buffer.extract(length8);
                            }
                        } else {
                            strExtract = "true";
                        }
                        tagProperties2.put(strExtract5, strExtract);
                    }
                }
                tag(strExtract4, tagProperties2, z, z2);
                this.buffer.clear();
                return readChar();
            }
        }
    }

    protected char parseEscape() throws IOException {
        char c;
        int i;
        int length = this.buffer.length();
        this.buffer.add('&');
        char c2 = readChar();
        if (c2 == 65535) {
            generateError(length);
            return c2;
        }
        if (c2 == '#') {
            int iDigit = 0;
            while (true) {
                i = iDigit;
                c = readChar();
                if (c == 65535) {
                    generateError(length);
                    return c;
                }
                if (c == ';') {
                    c = 0;
                    break;
                }
                if (!Character.isDigit(c)) {
                    if (i > 0) {
                        break;
                    }
                    error("Expecting a digit");
                    generateError(length);
                    return c;
                }
                this.buffer.add(c);
                iDigit = (i * 10) + Character.digit(c, 10);
            }
            this.buffer.reset(length);
            this.buffer.add((char) i);
        } else if (Character.isLowerCase(c2) || Character.isUpperCase(c2)) {
            if (this.entities == null) {
                initEntities();
            }
            this.escapeBuffer.clear();
            this.escapeBuffer.add(c2);
            while (true) {
                this.buffer.add(c2);
                c2 = readChar();
                if (c2 == 65535) {
                    generateError(length);
                    return c2;
                }
                if (Character.isLowerCase(c2) || Character.isUpperCase(c2)) {
                    this.escapeBuffer.add(c2);
                    if (this.entities.get(this.escapeBuffer.extract(0)) != null) {
                        c = readChar();
                        if (c != ';') {
                            break;
                        }
                        c = 0;
                        break;
                    }
                } else {
                    if (c2 == ';') {
                        c = 0;
                        break;
                    }
                    error("Expecting a letter");
                    generateError(length);
                    return c2;
                }
            }
            String strExtract = this.escapeBuffer.extract(0);
            this.buffer.reset(length);
            Character ch = (Character) this.entities.get(strExtract);
            if (ch != null) {
                return ch.charValue();
            }
            String strDocumentAttribute = documentAttribute(strExtract);
            if (strDocumentAttribute != null) {
                for (int i2 = 0; i2 < strDocumentAttribute.length(); i2++) {
                    this.buffer.add(strDocumentAttribute.charAt(i2));
                }
            }
        } else {
            error("Expecting a letter o");
            generateError(length);
            return c2;
        }
        if (c != 0) {
            return c;
        }
        return readChar();
    }

    protected void initEntities() {
        this.entities = new Hashtable();
        this.entities.put("quot", new Character('\"'));
        this.entities.put("amp", new Character('&'));
        this.entities.put("gt", new Character('>'));
        this.entities.put("lt", new Character('<'));
        this.entities.put("nbsp", new Character((char) 160));
        this.entities.put("copy", new Character((char) 169));
        this.entities.put("Agrave", new Character((char) 192));
        this.entities.put("Aacute", new Character((char) 193));
        this.entities.put("Acirc", new Character((char) 194));
        this.entities.put("Atilde", new Character((char) 195));
        this.entities.put("Auml", new Character((char) 196));
        this.entities.put("Aring", new Character((char) 197));
        this.entities.put("AElig", new Character((char) 198));
        this.entities.put("Ccedil", new Character((char) 199));
        this.entities.put("Egrave", new Character((char) 200));
        this.entities.put("Eacute", new Character((char) 201));
        this.entities.put("Ecirc", new Character((char) 202));
        this.entities.put("Euml", new Character((char) 203));
        this.entities.put("Igrave", new Character((char) 204));
        this.entities.put("Iacute", new Character((char) 205));
        this.entities.put("Icirc", new Character((char) 206));
        this.entities.put("Iuml", new Character((char) 207));
        this.entities.put("Ntilde", new Character((char) 209));
        this.entities.put("Ograve", new Character((char) 210));
        this.entities.put("Oacute", new Character((char) 211));
        this.entities.put("Ocirc", new Character((char) 212));
        this.entities.put("Otilde", new Character((char) 213));
        this.entities.put("Ouml", new Character((char) 214));
        this.entities.put("Oslash", new Character((char) 216));
        this.entities.put("Ugrave", new Character((char) 217));
        this.entities.put("Uacute", new Character((char) 218));
        this.entities.put("Ucirc", new Character((char) 219));
        this.entities.put("Uuml", new Character((char) 220));
        this.entities.put("Yacute", new Character((char) 221));
        this.entities.put("THORN", new Character((char) 222));
        this.entities.put("szlig", new Character((char) 223));
        this.entities.put("agrave", new Character((char) 224));
        this.entities.put("aacute", new Character((char) 225));
        this.entities.put("acirc", new Character((char) 226));
        this.entities.put("atilde", new Character((char) 227));
        this.entities.put("auml", new Character((char) 228));
        this.entities.put("aring", new Character((char) 229));
        this.entities.put("aelig", new Character((char) 230));
        this.entities.put("ccedil", new Character((char) 231));
        this.entities.put("egrave", new Character((char) 232));
        this.entities.put("eacute", new Character((char) 233));
        this.entities.put("ecirc", new Character((char) 234));
        this.entities.put("euml", new Character((char) 235));
        this.entities.put("igrave", new Character((char) 236));
        this.entities.put("iacute", new Character((char) 237));
        this.entities.put("icirc", new Character((char) 238));
        this.entities.put("iuml", new Character((char) 239));
        this.entities.put("eth", new Character((char) 240));
        this.entities.put("ntilde", new Character((char) 241));
        this.entities.put("ograve", new Character((char) 242));
        this.entities.put("oacute", new Character((char) 243));
        this.entities.put("ocirc", new Character((char) 244));
        this.entities.put("otilde", new Character((char) 245));
        this.entities.put("ouml", new Character((char) 246));
        this.entities.put("oslash", new Character((char) 248));
        this.entities.put("ugrave", new Character((char) 249));
        this.entities.put("uacute", new Character((char) 250));
        this.entities.put("ucirc", new Character((char) 251));
        this.entities.put("uuml", new Character((char) 252));
        this.entities.put("yacute", new Character((char) 253));
        this.entities.put("thorn", new Character((char) 254));
        this.entities.put("yuml", new Character((char) 255));
    }

    protected char scanIdentifier(char c) throws IOException {
        while (true) {
            if (c != '_' && c != ':' && ((c < '0' || c > '9') && !Character.isLetter(c))) {
                break;
            }
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] cArr = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, cArr, 0, this.buffer.buf.length);
                this.buffer.buf = cArr;
            }
            char[] cArr2 = this.buffer.buf;
            ScanBuffer scanBuffer = this.buffer;
            int i = scanBuffer.buflen;
            scanBuffer.buflen = i + 1;
            cArr2[i] = c;
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) {
                        break;
                    }
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::scanIdentifier() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                } catch (CharConversionException e) {
                    throw e;
                } catch (IOException e2) {
                }
            }
            if (this.shouldCacheSource) {
                this.documentSource.add(this.cb[this.myCount]);
            }
            char[] cArr3 = this.cb;
            int i2 = this.myCount;
            this.myCount = i2 + 1;
            c = cArr3[i2];
        }
        return c;
    }

    protected char scanQuotedString(char c) throws IOException {
        char cSkipWhite = skipWhite(c);
        if (cSkipWhite == '\"') {
            while (true) {
                char c2 = readChar();
                if (c2 == '\"' || c2 == '>') {
                    break;
                }
                this.buffer.add(c2);
            }
            return readChar();
        }
        if (cSkipWhite == '\'') {
            while (true) {
                char c3 = readChar();
                if (c3 == '\'' || c3 == '>') {
                    break;
                }
                this.buffer.add(c3);
            }
            return readChar();
        }
        return cSkipWhite;
    }

    protected char skipWhite(char c) throws IOException {
        while (true) {
            if (c != ' ' && c != '\r' && c != '\t' && c != '\n') {
                break;
            }
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] cArr = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, cArr, 0, this.buffer.buf.length);
                this.buffer.buf = cArr;
            }
            char[] cArr2 = this.buffer.buf;
            ScanBuffer scanBuffer = this.buffer;
            int i = scanBuffer.buflen;
            scanBuffer.buflen = i + 1;
            cArr2[i] = c;
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) {
                        break;
                    }
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::parse() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                } catch (CharConversionException e) {
                    throw e;
                } catch (IOException e2) {
                }
            }
            if (this.shouldCacheSource) {
                this.documentSource.add(this.cb[this.myCount]);
            }
            char[] cArr3 = this.cb;
            int i2 = this.myCount;
            this.myCount = i2 + 1;
            c = cArr3[i2];
        }
        return c;
    }

    protected char readChar() throws IOException {
        if (this.myCount >= this.mySize) {
            try {
                this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                if (this.mySize < 0) {
                    return (char) 65535;
                }
                if (this.mySize == 0) {
                    System.err.println(" DocumentParser::readChar() !!!ERROR !!! source.read(...) == 0");
                    return (char) 65535;
                }
                this.myCount = 0;
            } catch (CharConversionException e) {
                throw e;
            } catch (IOException e2) {
                return (char) 65535;
            }
        }
        if (this.shouldCacheSource) {
            this.documentSource.add(this.cb[this.myCount]);
        }
        char[] cArr = this.cb;
        int i = this.myCount;
        this.myCount = i + 1;
        return cArr[i];
    }

    protected void skipToCloseAngle(char c) throws IOException {
        while (true) {
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] cArr = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, cArr, 0, this.buffer.buf.length);
                this.buffer.buf = cArr;
            }
            char[] cArr2 = this.buffer.buf;
            ScanBuffer scanBuffer = this.buffer;
            int i = scanBuffer.buflen;
            scanBuffer.buflen = i + 1;
            cArr2[i] = c;
            if (c == '>') {
                break;
            }
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) {
                        break;
                    }
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::skipToCloseAngle() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                } catch (CharConversionException e) {
                    throw e;
                } catch (IOException e2) {
                }
            }
            char[] cArr3 = this.cb;
            int i2 = this.myCount;
            this.myCount = i2 + 1;
            c = cArr3[i2];
        }
        generateError(0);
    }

    protected void generateError(int i) {
        String strExtract = this.buffer.extract(i);
        this.buffer.reset(i);
        this.buffer.flush(this);
        errorString(strExtract);
    }

    protected void commentEOFError(int i) {
        eofError();
    }

    protected void eofError() {
        error("Unexpected end of file");
        generateError(0);
    }

    void error(String str) {
        System.err.println(new StringBuffer().append("DocumentParser Error: ").append(str).toString());
    }
}
