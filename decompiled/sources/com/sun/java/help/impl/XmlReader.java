package com.sun.java.help.impl;

import com.aowagie.text.pdf.PdfObject;
import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.io.CharsetNames;
import com.sun.jna.platform.win32.WinUser;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/XmlReader.class */
public final class XmlReader extends Reader {
    private boolean closed;
    private InputStreamReader in;
    private InputStream raw;
    private byte[] buffer;
    private boolean isASCII;
    private boolean isLatin1;
    private int offset;
    private int length;
    private char nextChar;
    private int switchover;
    private String encodingAssigned;
    private static boolean debug = false;

    public static Reader createReader(URLConnection uRLConnection) throws IOException {
        String encodingFromContentType = getEncodingFromContentType(uRLConnection.getContentType());
        if (encodingFromContentType == null) {
            return createReader(uRLConnection.getInputStream());
        }
        return createReader(uRLConnection.getInputStream(), encodingFromContentType);
    }

    private static String getEncodingFromContentType(String str) {
        int iIndexOf;
        debug(new StringBuffer().append("type=").append(str).toString());
        if (str != null && (iIndexOf = str.indexOf(PolicyIdFilter.OID_SEPARATOR)) > -1) {
            String strSubstring = str.substring(iIndexOf);
            if (str.substring(0, iIndexOf).trim().compareTo("text/xml") == 0) {
                return getCharsetFromContentTypeParameters(strSubstring);
            }
            return null;
        }
        return null;
    }

    private static String getCharsetFromContentTypeParameters(String str) {
        String strFindValue = null;
        try {
            int iIndexOf = str.indexOf(59);
            if (iIndexOf > -1 && iIndexOf < str.length() - 1) {
                str = str.substring(iIndexOf + 1);
            }
            if (str.length() > 0) {
                strFindValue = new HeaderParser(str).findValue("charset");
                return strFindValue;
            }
        } catch (IndexOutOfBoundsException e) {
        } catch (NullPointerException e2) {
        } catch (Exception e3) {
            System.err.println(new StringBuffer().append("Indexer.getCharsetFromContentTypeParameters failed on: ").append(str).toString());
            e3.printStackTrace();
        }
        return strFindValue;
    }

    public static Reader createReader(InputStream inputStream) throws IOException {
        return new XmlReader(inputStream);
    }

    public static Reader createReader(InputStream inputStream, String str) throws IOException {
        if (str == null) {
            return new XmlReader(inputStream);
        }
        if (CharsetNames.CS_UTF16.equalsIgnoreCase(str) || "ISO-106460-UCS-2".equalsIgnoreCase(str)) {
            str = "Unicode";
        } else {
            if ("UTF-8".equalsIgnoreCase(str)) {
                return new XmlReader(inputStream, "UTF-8");
            }
            if ("EUC-JP".equalsIgnoreCase(str)) {
                str = "EUCJIS";
            } else {
                if (isAsciiName(str)) {
                    return new XmlReader(inputStream, CharsetNames.CS_US_ASCII);
                }
                if (isLatinName(str)) {
                    return new XmlReader(inputStream, "ISO-8859-1");
                }
            }
        }
        return new InputStreamReader(inputStream, str);
    }

    private XmlReader(InputStream inputStream, String str) throws IOException {
        this.buffer = new byte[8192];
        this.length = 0;
        this.raw = inputStream;
        if (CharsetNames.CS_US_ASCII.equals(str)) {
            setASCII();
        } else if ("ISO-8859-1".equals(str)) {
            setLatin1();
        } else {
            if (!"UTF-8".equals(str)) {
                throw new UnsupportedEncodingException(str);
            }
            setUTF8();
        }
    }

    private static boolean isAsciiName(String str) {
        return CharsetNames.CS_US_ASCII.equalsIgnoreCase(str) || "ASCII".equalsIgnoreCase(str);
    }

    private static boolean isLatinName(String str) {
        return "ISO-8859-1".equalsIgnoreCase(str) || "Latin1".equalsIgnoreCase(str) || "8859_1".equalsIgnoreCase(str);
    }

    private void setASCII() {
        this.encodingAssigned = CharsetNames.CS_US_ASCII;
        this.isASCII = true;
        this.isLatin1 = false;
        this.offset = 0;
    }

    private void setLatin1() {
        this.encodingAssigned = "ISO-8859-1";
        this.isASCII = false;
        this.isLatin1 = true;
        this.offset = 0;
    }

    private void setUTF8() {
        this.encodingAssigned = "UTF-8";
        this.isASCII = false;
        this.isLatin1 = false;
        this.offset = 0;
    }

    public String getEncoding() {
        return this.encodingAssigned;
    }

    private XmlReader(InputStream inputStream) throws IOException {
        this.raw = inputStream;
        this.switchover = -1;
        this.buffer = new byte[8192];
        this.length = 0;
        this.offset = 0;
        this.isLatin1 = true;
        switch (read()) {
            case -1:
                return;
            case 0:
                if (read() == 60 && read() == 0 && read() == 63) {
                    setSwitchover(PdfObject.TEXT_UNICODE);
                    return;
                }
                throw new UnsupportedEncodingException("UCS-4 (?)");
            case 60:
                switch (read()) {
                    case 0:
                        if (read() == 63 && read() == 0) {
                            setSwitchover("UnicodeLittle");
                            return;
                        }
                        throw new UnsupportedEncodingException("UCS-4");
                    case 63:
                        if (read() == 120 && read() == 109 && read() == 108 && read() == 32) {
                            guessEncoding();
                            return;
                        }
                        break;
                }
                break;
            case 254:
                if (read() == 255) {
                    setSwitchover(PdfObject.TEXT_UNICODE);
                    this.offset = 2;
                    return;
                }
                break;
            case 255:
                if (read() == 254) {
                    setSwitchover("UnicodeLittle");
                    this.offset = 2;
                    return;
                }
                break;
        }
        setUTF8();
    }

    private void setSwitchover(String str) throws IOException {
        this.switchover = this.offset;
        this.encodingAssigned = str;
        this.offset = 0;
    }

    private void doSwitchover() throws IOException {
        if (this.offset != this.switchover) {
            throw new InternalError();
        }
        this.in = new InputStreamReader(this.raw, this.encodingAssigned);
        this.buffer = null;
        this.switchover = -1;
    }

    /* JADX WARN: Code duplicated, block: B:99:0x019e  */
    private void guessEncoding() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = null;
        String string = null;
        boolean z = false;
        char c = 0;
        boolean z2 = false;
        for (int i = 0; i < 100; i++) {
            int i2 = read();
            if (i2 == -1) {
                setASCII();
                return;
            }
            if (!Character.isWhitespace((char) i2)) {
                if (i2 == 63) {
                    z2 = true;
                } else if (z2) {
                    if (i2 == 62) {
                        break;
                    } else {
                        z2 = false;
                    }
                }
                if (string == null || !z) {
                    if (stringBuffer2 == null) {
                        if (!Character.isWhitespace((char) i2)) {
                            stringBuffer2 = stringBuffer;
                            stringBuffer.setLength(0);
                            stringBuffer.append((char) i2);
                            z = false;
                        }
                    } else if (Character.isWhitespace((char) i2)) {
                        string = stringBuffer2.toString();
                    } else if (i2 == 61) {
                        if (string == null) {
                            string = stringBuffer2.toString();
                        }
                        z = true;
                        stringBuffer2 = null;
                        c = 0;
                    } else {
                        stringBuffer2.append((char) i2);
                    }
                } else if (Character.isWhitespace((char) i2)) {
                    continue;
                } else if (i2 == 34 || i2 == 39) {
                    if (c == 0) {
                        c = (char) i2;
                        stringBuffer.setLength(0);
                    } else if (i2 == c) {
                        if ("encoding".equals(string)) {
                            String string2 = stringBuffer.toString();
                            for (int i3 = 0; i3 < string2.length(); i3++) {
                                char cCharAt = string2.charAt(i3);
                                if ((cCharAt < 'A' || cCharAt > 'Z') && ((cCharAt < 'a' || cCharAt > 'z') && (i3 <= 0 || !(cCharAt == '-' || ((cCharAt >= '0' && cCharAt <= '9') || cCharAt == '.' || cCharAt == '_'))))) {
                                    break;
                                }
                            }
                            if (isAsciiName(string2)) {
                                setASCII();
                                return;
                            }
                            if (isLatinName(string2)) {
                                setLatin1();
                                return;
                            }
                            if ("UTF-8".equalsIgnoreCase(string2) || "UTF8".equalsIgnoreCase(string2)) {
                                break;
                                break;
                            }
                            if ("EUC-JP".equalsIgnoreCase(string2)) {
                                string2 = "EUCJIS";
                            }
                            setSwitchover(string2);
                            return;
                        }
                        string = null;
                    } else {
                        stringBuffer.append((char) i2);
                    }
                } else {
                    stringBuffer.append((char) i2);
                }
            }
        }
        setUTF8();
    }

    private char utf8char() throws IOException {
        char c;
        int i;
        if (this.nextChar != 0) {
            char c2 = this.nextChar;
            this.nextChar = (char) 0;
            return c2;
        }
        char c3 = (char) this.buffer[this.offset];
        if ((c3 & 128) == 0) {
            this.offset++;
            return c3;
        }
        if (this.isASCII) {
            throw new CharConversionException(new StringBuffer().append("Not US-ASCII:  0x").append(Integer.toHexString(c3 & 255)).toString());
        }
        int i2 = this.offset;
        try {
            if ((this.buffer[i2] & 224) == 192) {
                int i3 = i2 + 1;
                int i4 = (this.buffer[i2] & 31) << 6;
                i2 = i3 + 1;
                c = (char) (i4 + (this.buffer[i3] & 63));
                i = 0;
            } else if ((this.buffer[i2] & 240) == 224) {
                int i5 = i2 + 1;
                int i6 = (this.buffer[i2] & 15) << 12;
                int i7 = i5 + 1;
                int i8 = i6 + ((this.buffer[i5] & 63) << 6);
                i2 = i7 + 1;
                c = (char) (i8 + (this.buffer[i7] & 63));
                i = 0;
            } else if ((this.buffer[i2] & 248) == 240) {
                int i9 = i2 + 1;
                int i10 = (this.buffer[i2] & 7) << 18;
                int i11 = i9 + 1;
                int i12 = i10 + ((this.buffer[i9] & 63) << 12);
                int i13 = i11 + 1;
                int i14 = i12 + ((this.buffer[i11] & 63) << 6);
                i2 = i13 + 1;
                int i15 = (i14 + (this.buffer[i13] & 63)) - 65536;
                c = (char) (InvalidCharHandler.FailingHandler.SURR1_FIRST + (i15 >> 10));
                i = InvalidCharHandler.FailingHandler.SURR2_FIRST + (i15 & WinUser.CF_GDIOBJLAST);
            } else {
                throw new CharConversionException(new StringBuffer().append("Illegal XML character 0x").append(Integer.toHexString(this.buffer[this.offset] & 255)).toString());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            c = 0;
            i = 0;
        }
        if (i2 > this.length) {
            System.arraycopy(this.buffer, this.offset, this.buffer, 0, this.length - this.offset);
            this.length -= this.offset;
            this.offset = 0;
            int i16 = this.raw.read(this.buffer, this.length, this.buffer.length - this.length);
            if (i16 < 0) {
                throw new CharConversionException("Partial UTF-8 char");
            }
            this.length += i16;
            return utf8char();
        }
        this.offset++;
        while (this.offset < i2) {
            if ((this.buffer[this.offset] & 192) == 128) {
                this.offset++;
            } else {
                throw new CharConversionException("Malformed UTF-8 char");
            }
        }
        this.nextChar = (char) i;
        return c;
    }

    @Override // java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        int i3;
        if (this.closed) {
            return -1;
        }
        if (this.switchover > 0 && this.offset == this.switchover) {
            doSwitchover();
        }
        if (this.in != null) {
            return this.in.read(cArr, i, i2);
        }
        if (this.offset >= this.length) {
            this.offset = 0;
            this.length = this.raw.read(this.buffer, 0, this.buffer.length);
        }
        if (this.length <= 0) {
            return -1;
        }
        if (this.encodingAssigned == null || this.isLatin1) {
            i3 = 0;
            while (i3 < i2 && this.offset < this.length) {
                int i4 = i;
                i++;
                byte[] bArr = this.buffer;
                int i5 = this.offset;
                this.offset = i5 + 1;
                cArr[i4] = (char) (bArr[i5] & 255);
                i3++;
            }
        } else {
            i3 = 0;
            while (i3 < i2 && this.offset < this.length) {
                int i6 = i;
                i++;
                cArr[i6] = utf8char();
                i3++;
            }
        }
        return i3;
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        if (this.switchover > 0 && this.offset == this.switchover) {
            doSwitchover();
        }
        if (this.in != null) {
            return this.in.read();
        }
        if (this.offset >= this.length) {
            if (this.encodingAssigned == null) {
                if (this.length == this.buffer.length) {
                    throw new InternalError("too much peekahead");
                }
                int i = this.raw.read(this.buffer, this.offset, 1);
                if (i <= 0) {
                    return -1;
                }
                this.length += i;
            } else {
                this.offset = 0;
                this.length = this.raw.read(this.buffer, 0, this.buffer.length);
                if (this.length <= 0) {
                    return -1;
                }
            }
        }
        if (this.isLatin1 || this.encodingAssigned == null) {
            byte[] bArr = this.buffer;
            int i2 = this.offset;
            this.offset = i2 + 1;
            return bArr[i2] & 255;
        }
        return utf8char();
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return this.in != null && this.in.markSupported();
    }

    @Override // java.io.Reader
    public void mark(int i) throws IOException {
        if (this.in != null) {
            this.in.mark(i);
        }
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();
        }
    }

    @Override // java.io.Reader
    public long skip(long j) throws IOException {
        if (j < 0) {
            return 0L;
        }
        if (this.in != null) {
            return this.in.skip(j);
        }
        long j2 = this.length - this.offset;
        if (j2 >= j) {
            this.offset += (int) j;
            return j;
        }
        this.offset = (int) (((long) this.offset) + j2);
        return j2 + this.raw.skip(j - j2);
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        if (this.in != null) {
            return this.in.ready();
        }
        return this.length > this.offset || this.raw.available() != 0;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        if (this.in != null) {
            this.in.close();
        } else {
            this.raw.close();
        }
        this.closed = true;
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("XmlReader: ").append(str).toString());
        }
    }
}
