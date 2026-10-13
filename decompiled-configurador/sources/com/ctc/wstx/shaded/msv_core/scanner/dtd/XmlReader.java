package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.io.CharsetNames;
import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/XmlReader.class */
final class XmlReader extends Reader {
    private static final int MAXPUSHBACK = 512;
    private Reader in;
    private String assignedEncoding;
    private boolean closed;
    private static final Hashtable charsets = new Hashtable(31);

    public static Reader createReader(InputStream in) throws IOException {
        return new XmlReader(in);
    }

    public static Reader createReader(InputStream in, String encoding) throws IOException {
        if (encoding == null) {
            return new XmlReader(in);
        }
        if ("UTF-8".equalsIgnoreCase(encoding) || "UTF8".equalsIgnoreCase(encoding)) {
            return new Utf8Reader(in);
        }
        if (CharsetNames.CS_US_ASCII.equalsIgnoreCase(encoding) || "ASCII".equalsIgnoreCase(encoding)) {
            return new AsciiReader(in);
        }
        if ("ISO-8859-1".equalsIgnoreCase(encoding)) {
            return new Iso8859_1Reader(in);
        }
        return new InputStreamReader(in, std2java(encoding));
    }

    static {
        charsets.put(CharsetNames.CS_UTF16, "Unicode");
        charsets.put("ISO-10646-UCS-2", "Unicode");
        charsets.put("EBCDIC-CP-US", "cp037");
        charsets.put("EBCDIC-CP-CA", "cp037");
        charsets.put("EBCDIC-CP-NL", "cp037");
        charsets.put("EBCDIC-CP-WT", "cp037");
        charsets.put("EBCDIC-CP-DK", "cp277");
        charsets.put("EBCDIC-CP-NO", "cp277");
        charsets.put("EBCDIC-CP-FI", "cp278");
        charsets.put("EBCDIC-CP-SE", "cp278");
        charsets.put("EBCDIC-CP-IT", "cp280");
        charsets.put("EBCDIC-CP-ES", "cp284");
        charsets.put("EBCDIC-CP-GB", "cp285");
        charsets.put("EBCDIC-CP-FR", "cp297");
        charsets.put("EBCDIC-CP-AR1", "cp420");
        charsets.put("EBCDIC-CP-HE", "cp424");
        charsets.put("EBCDIC-CP-BE", "cp500");
        charsets.put("EBCDIC-CP-CH", "cp500");
        charsets.put("EBCDIC-CP-ROECE", "cp870");
        charsets.put("EBCDIC-CP-YU", "cp870");
        charsets.put("EBCDIC-CP-IS", "cp871");
        charsets.put("EBCDIC-CP-AR2", "cp918");
    }

    private static String std2java(String encoding) {
        String temp = (String) charsets.get(encoding.toUpperCase());
        return temp != null ? temp : encoding;
    }

    public String getEncoding() {
        return this.assignedEncoding;
    }

    private XmlReader(InputStream stream) throws IOException {
        PushbackInputStream pb;
        super(stream);
        if (stream instanceof PushbackInputStream) {
            pb = (PushbackInputStream) stream;
        } else {
            pb = new PushbackInputStream(stream, 512);
        }
        byte[] buf = new byte[4];
        int len = pb.read(buf);
        if (len > 0) {
            pb.unread(buf, 0, len);
        }
        if (len == 4) {
            switch (buf[0] & 255) {
                case 0:
                    if (buf[1] == 60 && buf[2] == 0 && buf[3] == 63) {
                        setEncoding(pb, "UnicodeBig");
                        return;
                    }
                    break;
                case 60:
                    switch (buf[1] & 255) {
                        case 0:
                            if (buf[2] == 63 && buf[3] == 0) {
                                setEncoding(pb, "UnicodeLittle");
                                return;
                            }
                            break;
                        case 63:
                            if (buf[2] == 120 && buf[3] == 109) {
                                useEncodingDecl(pb, "UTF8");
                                return;
                            }
                            break;
                    }
                    break;
                case 76:
                    if (buf[1] == 111 && (255 & buf[2]) == 167 && (255 & buf[3]) == 148) {
                        useEncodingDecl(pb, "CP037");
                        return;
                    }
                    break;
                case 254:
                    if ((buf[1] & 255) == 255) {
                        setEncoding(pb, CharsetNames.CS_UTF16);
                        return;
                    }
                    break;
                case 255:
                    if ((buf[1] & 255) == 254) {
                        setEncoding(pb, CharsetNames.CS_UTF16);
                        return;
                    }
                    break;
            }
        }
        setEncoding(pb, "UTF-8");
    }

    /* JADX WARN: Code duplicated, block: B:98:0x01f2  */
    private void useEncodingDecl(PushbackInputStream pb, String encoding) throws IOException {
        int c;
        byte[] buffer = new byte[512];
        int len = pb.read(buffer, 0, buffer.length);
        pb.unread(buffer, 0, len);
        Reader r = new InputStreamReader(new ByteArrayInputStream(buffer, 4, len), encoding);
        if (r.read() != 108) {
            setEncoding(pb, "UTF-8");
            return;
        }
        StringBuffer buf = new StringBuffer();
        StringBuffer keyBuf = null;
        String key = null;
        boolean sawEq = false;
        char quoteChar = 0;
        boolean sawQuestion = false;
        for (int i = 0; i < 507 && (c = r.read()) != -1; i++) {
            if (c != 32 && c != 9 && c != 10 && c != 13) {
                if (i == 0) {
                    break;
                }
                if (c == 63) {
                    sawQuestion = true;
                } else if (sawQuestion) {
                    if (c == 62) {
                        break;
                    } else {
                        sawQuestion = false;
                    }
                }
                if (key == null || !sawEq) {
                    if (keyBuf == null) {
                        if (!Character.isWhitespace((char) c)) {
                            keyBuf = buf;
                            buf.setLength(0);
                            buf.append((char) c);
                            sawEq = false;
                        }
                    } else if (Character.isWhitespace((char) c)) {
                        key = keyBuf.toString();
                    } else if (c == 61) {
                        if (key == null) {
                            key = keyBuf.toString();
                        }
                        sawEq = true;
                        keyBuf = null;
                        quoteChar = 0;
                    } else {
                        keyBuf.append((char) c);
                    }
                } else if (Character.isWhitespace((char) c)) {
                    continue;
                } else if (c == 34 || c == 39) {
                    if (quoteChar == 0) {
                        quoteChar = (char) c;
                        buf.setLength(0);
                    } else if (c == quoteChar) {
                        if ("encoding".equals(key)) {
                            this.assignedEncoding = buf.toString();
                            for (int i2 = 0; i2 < this.assignedEncoding.length(); i2++) {
                                int c2 = this.assignedEncoding.charAt(i2);
                                if ((c2 < 65 || c2 > 90) && ((c2 < 97 || c2 > 122) && (i2 == 0 || i2 <= 0 || !(c2 == 45 || ((c2 >= 48 && c2 <= 57) || c2 == 46 || c2 == 95))))) {
                                    break;
                                }
                            }
                            setEncoding(pb, this.assignedEncoding);
                            return;
                        }
                        key = null;
                    } else {
                        buf.append((char) c);
                    }
                } else {
                    buf.append((char) c);
                }
            }
        }
        setEncoding(pb, "UTF-8");
    }

    private void setEncoding(InputStream stream, String encoding) throws IOException {
        this.assignedEncoding = encoding;
        this.in = createReader(stream, encoding);
    }

    @Override // java.io.Reader
    public int read(char[] buf, int off, int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        int val = this.in.read(buf, off, len);
        if (val == -1) {
            close();
        }
        return val;
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        int val = this.in.read();
        if (val == -1) {
            close();
        }
        return val;
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        if (this.in == null) {
            return false;
        }
        return this.in.markSupported();
    }

    @Override // java.io.Reader
    public void mark(int value) throws IOException {
        if (this.in != null) {
            this.in.mark(value);
        }
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();
        }
    }

    @Override // java.io.Reader
    public long skip(long value) throws IOException {
        if (this.in == null) {
            return 0L;
        }
        return this.in.skip(value);
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        if (this.in == null) {
            return false;
        }
        return this.in.ready();
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.in.close();
        this.in = null;
        this.closed = true;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/XmlReader$BaseReader.class */
    static abstract class BaseReader extends Reader {
        protected InputStream instream;
        protected byte[] buffer;
        protected int start;
        protected int finish;

        BaseReader(InputStream stream) {
            super(stream);
            this.instream = stream;
            this.buffer = new byte[8192];
        }

        @Override // java.io.Reader
        public boolean ready() throws IOException {
            return this.instream == null || this.finish - this.start > 0 || this.instream.available() != 0;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.instream != null) {
                this.instream.close();
                this.finish = 0;
                this.start = 0;
                this.buffer = null;
                this.instream = null;
            }
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/XmlReader$Utf8Reader.class */
    static final class Utf8Reader extends BaseReader {
        private char nextChar;

        Utf8Reader(InputStream stream) {
            super(stream);
        }

        @Override // java.io.Reader
        public int read(char[] buf, int offset, int len) throws IOException {
            int i = 0;
            int c = 0;
            if (len <= 0) {
                return 0;
            }
            if (this.nextChar != 0) {
                i = 0 + 1;
                buf[offset + 0] = this.nextChar;
                this.nextChar = (char) 0;
            }
            while (i < len) {
                if (this.finish <= this.start) {
                    if (this.instream == null) {
                        c = -1;
                        break;
                    }
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        close();
                        c = -1;
                        break;
                    }
                }
                c = this.buffer[this.start] & 255;
                if ((c & 128) == 0) {
                    this.start++;
                    int i2 = i;
                    i++;
                    buf[offset + i2] = (char) c;
                } else {
                    int off = this.start;
                    try {
                        if ((this.buffer[off] & 224) == 192) {
                            int off2 = off + 1;
                            int c2 = (this.buffer[off] & 31) << 6;
                            off = off2 + 1;
                            c = c2 + (this.buffer[off2] & 63);
                        } else if ((this.buffer[off] & 240) == 224) {
                            int off3 = off + 1;
                            int c3 = (this.buffer[off] & 15) << 12;
                            int off4 = off3 + 1;
                            int c4 = c3 + ((this.buffer[off3] & 63) << 6);
                            off = off4 + 1;
                            c = c4 + (this.buffer[off4] & 63);
                        } else if ((this.buffer[off] & 248) == 240) {
                            int off5 = off + 1;
                            int c5 = (this.buffer[off] & 7) << 18;
                            int off6 = off5 + 1;
                            int c6 = c5 + ((this.buffer[off5] & 63) << 12);
                            int off7 = off6 + 1;
                            int c7 = c6 + ((this.buffer[off6] & 63) << 6);
                            off = off7 + 1;
                            int c8 = c7 + (this.buffer[off7] & 63);
                            if (c8 > 1114111) {
                                throw new CharConversionException("UTF-8 encoding of character 0x00" + Integer.toHexString(c8) + " can't be converted to Unicode.");
                            }
                            int c9 = c8 - 65536;
                            this.nextChar = (char) (InvalidCharHandler.FailingHandler.SURR2_FIRST + (c9 & 1023));
                            c = InvalidCharHandler.FailingHandler.SURR1_FIRST + (c9 >> 10);
                        } else {
                            throw new CharConversionException("Unconvertible UTF-8 character beginning with 0x" + Integer.toHexString(this.buffer[this.start] & 255));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        c = 0;
                    }
                    if (off > this.finish) {
                        System.arraycopy(this.buffer, this.start, this.buffer, 0, this.finish - this.start);
                        this.finish -= this.start;
                        this.start = 0;
                        int off8 = this.instream.read(this.buffer, this.finish, this.buffer.length - this.finish);
                        if (off8 < 0) {
                            close();
                            throw new CharConversionException("Partial UTF-8 char");
                        }
                        this.finish += off8;
                    } else {
                        this.start++;
                        while (this.start < off) {
                            if ((this.buffer[this.start] & 192) == 128) {
                                this.start++;
                            } else {
                                close();
                                throw new CharConversionException("Malformed UTF-8 char -- is an XML encoding declaration missing?");
                            }
                        }
                        int i3 = i;
                        i++;
                        buf[offset + i3] = (char) c;
                        if (this.nextChar != 0 && i < len) {
                            i++;
                            buf[offset + i] = this.nextChar;
                            this.nextChar = (char) 0;
                        }
                    }
                }
            }
            if (i > 0) {
                return i;
            }
            return c == -1 ? -1 : 0;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/XmlReader$AsciiReader.class */
    static final class AsciiReader extends BaseReader {
        AsciiReader(InputStream in) {
            super(in);
        }

        @Override // java.io.Reader
        public int read(char[] buf, int offset, int len) throws IOException {
            if (this.instream == null) {
                return -1;
            }
            int i = 0;
            while (i < len) {
                if (this.start >= this.finish) {
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        if (this.finish > 0) {
                            break;
                        }
                        close();
                        break;
                    }
                }
                byte[] bArr = this.buffer;
                int i2 = this.start;
                this.start = i2 + 1;
                byte b = bArr[i2];
                if ((b & 128) != 0) {
                    throw new CharConversionException("Illegal ASCII character, 0x" + Integer.toHexString(b & 255));
                }
                buf[offset + i] = (char) b;
                i++;
            }
            if (i == 0 && this.finish <= 0) {
                return -1;
            }
            return i;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/XmlReader$Iso8859_1Reader.class */
    static final class Iso8859_1Reader extends BaseReader {
        Iso8859_1Reader(InputStream in) {
            super(in);
        }

        @Override // java.io.Reader
        public int read(char[] buf, int offset, int len) throws IOException {
            if (this.instream == null) {
                return -1;
            }
            int i = 0;
            while (i < len) {
                if (this.start >= this.finish) {
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        if (this.finish > 0) {
                            break;
                        }
                        close();
                        break;
                    }
                }
                byte[] bArr = this.buffer;
                int i2 = this.start;
                this.start = i2 + 1;
                buf[offset + i] = (char) (255 & bArr[i2]);
                i++;
            }
            if (i == 0 && this.finish <= 0) {
                return -1;
            }
            return i;
        }
    }
}
