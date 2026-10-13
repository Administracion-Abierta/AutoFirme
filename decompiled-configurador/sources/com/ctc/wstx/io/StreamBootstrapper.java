package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.cfg.XmlConsts;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxIOException;
import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/StreamBootstrapper.class */
public final class StreamBootstrapper extends InputBootstrapper {
    static final int MIN_BUF_SIZE = 128;
    final InputStream mIn;
    private byte[] mByteBuffer;
    private final boolean mRecycleBuffer;
    private int mInputPtr;
    private int mInputEnd;
    boolean mBigEndian;
    boolean mHadBOM;
    boolean mByteSizeFound;
    int mBytesPerChar;
    boolean mEBCDIC;
    String mInputEncoding;
    int[] mSingleByteTranslation;

    private StreamBootstrapper(String pubId, SystemId sysId, InputStream in) {
        super(pubId, sysId);
        this.mBigEndian = true;
        this.mHadBOM = false;
        this.mByteSizeFound = false;
        this.mEBCDIC = false;
        this.mInputEncoding = null;
        this.mSingleByteTranslation = null;
        this.mIn = in;
        this.mInputEnd = 0;
        this.mInputPtr = 0;
        this.mRecycleBuffer = true;
    }

    private StreamBootstrapper(String pubId, SystemId sysId, byte[] data, int start, int end) {
        super(pubId, sysId);
        this.mBigEndian = true;
        this.mHadBOM = false;
        this.mByteSizeFound = false;
        this.mEBCDIC = false;
        this.mInputEncoding = null;
        this.mSingleByteTranslation = null;
        this.mIn = null;
        this.mRecycleBuffer = false;
        this.mByteBuffer = data;
        this.mInputPtr = start;
        this.mInputEnd = end;
    }

    public static StreamBootstrapper getInstance(String pubId, SystemId sysId, InputStream in) {
        return new StreamBootstrapper(pubId, sysId, in);
    }

    public static StreamBootstrapper getInstance(String pubId, SystemId sysId, byte[] data, int start, int end) {
        return new StreamBootstrapper(pubId, sysId, data, start, end);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.io.InputBootstrapper
    public Reader bootstrapInput(ReaderConfig cfg, boolean mainDoc, int xmlVersion) throws XMLStreamException, IOException {
        BaseReader r;
        String normEnc = null;
        int bufSize = cfg.getInputBufferLength();
        if (bufSize < 128) {
            bufSize = 128;
        }
        if (this.mByteBuffer == null) {
            this.mByteBuffer = cfg.allocFullBBuffer(bufSize);
        }
        resolveStreamEncoding();
        if (hasXmlDecl()) {
            readXmlDecl(mainDoc, xmlVersion);
            if (this.mFoundEncoding != null) {
                normEnc = verifyXmlEncoding(this.mFoundEncoding);
            }
        } else {
            this.mXml11Handling = 272 == xmlVersion;
        }
        if (normEnc == null) {
            if (this.mEBCDIC) {
                if (this.mFoundEncoding == null || this.mFoundEncoding.length() == 0) {
                    reportXmlProblem("Missing encoding declaration: underlying encoding looks like an EBCDIC variant, but no xml encoding declaration found");
                }
                normEnc = this.mFoundEncoding;
            } else if (this.mBytesPerChar == 2) {
                normEnc = this.mBigEndian ? CharsetNames.CS_UTF16BE : CharsetNames.CS_UTF16LE;
            } else if (this.mBytesPerChar == 4) {
                normEnc = this.mBigEndian ? CharsetNames.CS_UTF32BE : CharsetNames.CS_UTF32LE;
            } else {
                normEnc = "UTF-8";
            }
        }
        this.mInputEncoding = normEnc;
        if (normEnc == "UTF-8") {
            r = new UTF8Reader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer);
        } else if (normEnc == "ISO-8859-1") {
            r = new ISOLatinReader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer);
        } else if (normEnc == CharsetNames.CS_US_ASCII) {
            r = new AsciiReader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer);
        } else if (normEnc.startsWith(CharsetNames.CS_UTF32)) {
            if (normEnc == CharsetNames.CS_UTF32) {
                this.mInputEncoding = this.mBigEndian ? CharsetNames.CS_UTF32BE : CharsetNames.CS_UTF32LE;
            }
            r = new UTF32Reader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer, this.mBigEndian);
        } else {
            InputStream in = this.mIn;
            if (this.mInputPtr < this.mInputEnd) {
                if (in == null) {
                    in = new ByteArrayInputStream(this.mByteBuffer, this.mInputPtr, this.mInputEnd - this.mInputPtr);
                } else {
                    in = new MergedStream(cfg, in, this.mByteBuffer, this.mInputPtr, this.mInputEnd);
                }
            } else if (in == null) {
                in = new ByteArrayInputStream(this.mByteBuffer, 0, 0);
            }
            if (normEnc == CharsetNames.CS_UTF16) {
                String str = this.mBigEndian ? CharsetNames.CS_UTF16BE : CharsetNames.CS_UTF16LE;
                normEnc = str;
                this.mInputEncoding = str;
            }
            try {
                return new InputStreamReader(in, normEnc);
            } catch (UnsupportedEncodingException usex) {
                throw new WstxIOException("Unsupported encoding: " + usex.getMessage());
            }
        }
        if (this.mXml11Handling) {
            r.setXmlCompliancy(XmlConsts.XML_V_11);
        }
        return r;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    public String getInputEncoding() {
        return this.mInputEncoding;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    public int getInputTotal() {
        int total = this.mInputProcessed + this.mInputPtr;
        if (this.mBytesPerChar > 1) {
            total /= this.mBytesPerChar;
        }
        return total;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    public int getInputColumn() {
        int col = this.mInputPtr - this.mInputRowStart;
        if (this.mBytesPerChar > 1) {
            col /= this.mBytesPerChar;
        }
        return col;
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void resolveStreamEncoding() throws IOException {
        this.mBytesPerChar = 0;
        this.mBigEndian = true;
        if (ensureLoaded(4)) {
            int quartet = (this.mByteBuffer[0] << 24) | ((this.mByteBuffer[1] & 255) << 16) | ((this.mByteBuffer[2] & 255) << 8) | (this.mByteBuffer[3] & 255);
            switch (quartet) {
                case -16842752:
                    reportWeirdUCS4("3412");
                    break;
                case -131072:
                    this.mBytesPerChar = 4;
                    this.mInputPtr = 4;
                    this.mBigEndian = false;
                    break;
                case 65279:
                    this.mBigEndian = true;
                    this.mBytesPerChar = 4;
                    this.mInputPtr = 4;
                    break;
                case 65534:
                    reportWeirdUCS4("2143");
                    break;
                default:
                    int msw = quartet >>> 16;
                    if (msw == 65279) {
                        this.mBytesPerChar = 2;
                        this.mInputPtr = 2;
                        this.mBigEndian = true;
                        break;
                    } else if (msw == 65534) {
                        this.mBytesPerChar = 2;
                        this.mInputPtr = 2;
                        this.mBigEndian = false;
                        break;
                    } else if ((quartet >>> 8) == 15711167) {
                        this.mInputPtr = 3;
                        this.mBytesPerChar = 1;
                        this.mBigEndian = true;
                        break;
                    } else {
                        switch (quartet) {
                            case 60:
                                this.mBigEndian = true;
                                this.mBytesPerChar = 4;
                                break;
                            case 15360:
                                reportWeirdUCS4("2143");
                                break;
                            case 3932160:
                                reportWeirdUCS4("3412");
                                break;
                            case 3932223:
                                this.mBytesPerChar = 2;
                                this.mBigEndian = true;
                                break;
                            case 1006632960:
                                this.mBytesPerChar = 4;
                                this.mBigEndian = false;
                                break;
                            case 1006649088:
                                this.mBytesPerChar = 2;
                                this.mBigEndian = false;
                                break;
                            case 1010792557:
                                this.mBytesPerChar = 1;
                                this.mBigEndian = true;
                                break;
                            case 1282385812:
                                this.mBytesPerChar = -1;
                                this.mEBCDIC = true;
                                this.mSingleByteTranslation = EBCDICCodec.getCp037Mapping();
                                break;
                        }
                    }
                    break;
            }
            this.mHadBOM = this.mInputPtr > 0;
            this.mInputProcessed = -this.mInputPtr;
            this.mInputRowStart = this.mInputPtr;
        }
        this.mByteSizeFound = this.mBytesPerChar != 0;
        if (!this.mByteSizeFound) {
            this.mBytesPerChar = 1;
            this.mBigEndian = true;
        }
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected String verifyXmlEncoding(String enc) {
        String enc2 = CharsetNames.normalize(enc);
        if (enc2 == "UTF-8" || enc2 == "ISO-8859-1" || enc2 == CharsetNames.CS_US_ASCII) {
            verifyEncoding(enc2, 1);
        } else if (enc2 == CharsetNames.CS_UTF16) {
            verifyEncoding(enc2, 2);
        } else if (enc2 == CharsetNames.CS_UTF16LE) {
            verifyEncoding(enc2, 2, false);
        } else if (enc2 == CharsetNames.CS_UTF16BE) {
            verifyEncoding(enc2, 2, true);
        } else if (enc2 == CharsetNames.CS_UTF32) {
            verifyEncoding(enc2, 4);
        } else if (enc2 == CharsetNames.CS_UTF32LE) {
            verifyEncoding(enc2, 4, false);
        } else if (enc2 == CharsetNames.CS_UTF32BE) {
            verifyEncoding(enc2, 4, true);
        }
        return enc2;
    }

    protected boolean ensureLoaded(int minimum) throws IOException {
        int i = this.mInputEnd - this.mInputPtr;
        while (true) {
            int gotten = i;
            if (gotten < minimum) {
                int count = this.mIn == null ? -1 : this.mIn.read(this.mByteBuffer, this.mInputEnd, this.mByteBuffer.length - this.mInputEnd);
                if (count < 1) {
                    return false;
                }
                this.mInputEnd += count;
                i = gotten + count;
            } else {
                return true;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void loadMore() throws XMLStreamException, IOException {
        this.mInputProcessed += this.mInputEnd;
        this.mInputRowStart -= this.mInputEnd;
        this.mInputPtr = 0;
        this.mInputEnd = this.mIn == null ? -1 : this.mIn.read(this.mByteBuffer, 0, this.mByteBuffer.length);
        if (this.mInputEnd < 1) {
            throw new WstxEOFException(ParsingErrorMsgs.SUFFIX_IN_XML_DECL, getLocation());
        }
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    protected void pushback() {
        if (this.mBytesPerChar < 0) {
            this.mInputPtr += this.mBytesPerChar;
        } else {
            this.mInputPtr -= this.mBytesPerChar;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int getNext() throws XMLStreamException, IOException {
        byte bNextByte;
        if (this.mBytesPerChar != 1) {
            if (this.mBytesPerChar == -1) {
                return nextTranslated();
            }
            return nextMultiByte();
        }
        if (this.mInputPtr < this.mInputEnd) {
            byte[] bArr = this.mByteBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            bNextByte = bArr[i];
        } else {
            bNextByte = nextByte();
        }
        byte b = bNextByte;
        return b & 255;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int getNextAfterWs(boolean reqWs) throws XMLStreamException, IOException {
        int count;
        byte bNextByte;
        if (this.mBytesPerChar == 1) {
            count = skipSbWs();
        } else if (this.mBytesPerChar == -1) {
            count = skipTranslatedWs();
        } else {
            count = skipMbWs();
        }
        if (reqWs && count == 0) {
            reportUnexpectedChar(getNext(), "; expected a white space");
        }
        if (this.mBytesPerChar != 1) {
            if (this.mBytesPerChar == -1) {
                return nextTranslated();
            }
            return nextMultiByte();
        }
        if (this.mInputPtr < this.mInputEnd) {
            byte[] bArr = this.mByteBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            bNextByte = bArr[i];
        } else {
            bNextByte = nextByte();
        }
        byte b = bNextByte;
        return b & 255;
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int checkKeyword(String exp) throws IOException {
        if (this.mBytesPerChar != 1) {
            if (this.mBytesPerChar == -1) {
                return checkTranslatedKeyword(exp);
            }
            return checkMbKeyword(exp);
        }
        return checkSbKeyword(exp);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int readQuotedValue(char[] outputBuffer, int quoteChar) throws XMLStreamException, IOException {
        int c;
        byte bNextByte;
        int outputPtr = 0;
        int outputBufferLen = outputBuffer.length;
        boolean simple = this.mBytesPerChar == 1;
        boolean mb = !simple && this.mBytesPerChar > 1;
        while (outputPtr < outputBufferLen) {
            if (simple) {
                if (this.mInputPtr < this.mInputEnd) {
                    byte[] bArr = this.mByteBuffer;
                    int i = this.mInputPtr;
                    this.mInputPtr = i + 1;
                    bNextByte = bArr[i];
                } else {
                    bNextByte = nextByte();
                }
                byte b = bNextByte;
                if (b == 0) {
                    reportNull();
                }
                if (b == 13 || b == 10) {
                    skipSbLF(b);
                    b = 10;
                }
                c = b & 255;
            } else if (mb) {
                c = nextMultiByte();
                if (c == 13 || c == 10) {
                    skipMbLF(c);
                    c = 10;
                }
            } else {
                c = nextTranslated();
                if (c == 13 || c == 10) {
                    skipTranslatedLF(c);
                    c = 10;
                }
            }
            if (c == quoteChar) {
                return outputPtr;
            }
            int i2 = outputPtr;
            outputPtr++;
            outputBuffer[i2] = (char) c;
        }
        return -1;
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected boolean hasXmlDecl() throws IOException {
        if (this.mBytesPerChar == 1) {
            if (ensureLoaded(6) && this.mByteBuffer[this.mInputPtr] == 60 && this.mByteBuffer[this.mInputPtr + 1] == 63 && this.mByteBuffer[this.mInputPtr + 2] == 120 && this.mByteBuffer[this.mInputPtr + 3] == 109 && this.mByteBuffer[this.mInputPtr + 4] == 108 && (this.mByteBuffer[this.mInputPtr + 5] & 255) <= 32) {
                this.mInputPtr += 6;
                return true;
            }
            return false;
        }
        if (this.mBytesPerChar == -1) {
            if (ensureLoaded(6)) {
                int start = this.mInputPtr;
                if (nextTranslated() == 60 && nextTranslated() == 63 && nextTranslated() == 120 && nextTranslated() == 109 && nextTranslated() == 108 && nextTranslated() <= 32) {
                    return true;
                }
                this.mInputPtr = start;
                return false;
            }
            return false;
        }
        if (ensureLoaded(6 * this.mBytesPerChar)) {
            int start2 = this.mInputPtr;
            if (nextMultiByte() == 60 && nextMultiByte() == 63 && nextMultiByte() == 120 && nextMultiByte() == 109 && nextMultiByte() == 108 && nextMultiByte() <= 32) {
                return true;
            }
            this.mInputPtr = start2;
            return false;
        }
        return false;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    protected Location getLocation() {
        int total = this.mInputProcessed + this.mInputPtr;
        int col = this.mInputPtr - this.mInputRowStart;
        if (this.mBytesPerChar > 1) {
            total /= this.mBytesPerChar;
            col /= this.mBytesPerChar;
        }
        return new WstxInputLocation((WstxInputLocation) null, this.mPublicId, this.mSystemId, total - 1, this.mInputRow, col);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected byte nextByte() throws XMLStreamException, IOException {
        if (this.mInputPtr >= this.mInputEnd) {
            loadMore();
        }
        byte[] bArr = this.mByteBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return bArr[i];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int skipSbWs() throws XMLStreamException, IOException {
        byte bNextByte;
        int count = 0;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                byte[] bArr = this.mByteBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                bNextByte = bArr[i];
            } else {
                bNextByte = nextByte();
            }
            byte b = bNextByte;
            if ((b & 255) > 32) {
                this.mInputPtr--;
                return count;
            }
            if (b == 13 || b == 10) {
                skipSbLF(b);
            } else if (b == 0) {
                reportNull();
            }
            count++;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void skipSbLF(byte lfByte) throws XMLStreamException, IOException {
        byte bNextByte;
        if (lfByte == 13) {
            if (this.mInputPtr < this.mInputEnd) {
                byte[] bArr = this.mByteBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                bNextByte = bArr[i];
            } else {
                bNextByte = nextByte();
            }
            byte b = bNextByte;
            if (b != 10) {
                this.mInputPtr--;
            }
        }
        this.mInputRow++;
        this.mInputRowStart = this.mInputPtr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int checkSbKeyword(String expected) throws XMLStreamException, IOException {
        byte bNextByte;
        int len = expected.length();
        for (int ptr = 1; ptr < len; ptr++) {
            if (this.mInputPtr < this.mInputEnd) {
                byte[] bArr = this.mByteBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                bNextByte = bArr[i];
            } else {
                bNextByte = nextByte();
            }
            byte b = bNextByte;
            if (b == 0) {
                reportNull();
            }
            if ((b & 255) != expected.charAt(ptr)) {
                return b & 255;
            }
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int nextMultiByte() throws XMLStreamException, IOException {
        byte bNextByte;
        byte bNextByte2;
        byte bNextByte3;
        byte bNextByte4;
        int c;
        if (this.mInputPtr < this.mInputEnd) {
            byte[] bArr = this.mByteBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            bNextByte = bArr[i];
        } else {
            bNextByte = nextByte();
        }
        byte b = bNextByte;
        if (this.mInputPtr < this.mInputEnd) {
            byte[] bArr2 = this.mByteBuffer;
            int i2 = this.mInputPtr;
            this.mInputPtr = i2 + 1;
            bNextByte2 = bArr2[i2];
        } else {
            bNextByte2 = nextByte();
        }
        byte b2 = bNextByte2;
        if (this.mBytesPerChar == 2) {
            if (this.mBigEndian) {
                c = ((b & 255) << 8) | (b2 & 255);
            } else {
                c = (b & 255) | ((b2 & 255) << 8);
            }
        } else {
            if (this.mInputPtr < this.mInputEnd) {
                byte[] bArr3 = this.mByteBuffer;
                int i3 = this.mInputPtr;
                this.mInputPtr = i3 + 1;
                bNextByte3 = bArr3[i3];
            } else {
                bNextByte3 = nextByte();
            }
            byte b3 = bNextByte3;
            if (this.mInputPtr < this.mInputEnd) {
                byte[] bArr4 = this.mByteBuffer;
                int i4 = this.mInputPtr;
                this.mInputPtr = i4 + 1;
                bNextByte4 = bArr4[i4];
            } else {
                bNextByte4 = nextByte();
            }
            byte b4 = bNextByte4;
            if (this.mBigEndian) {
                c = (b << 24) | ((b2 & 255) << 16) | ((b3 & 255) << 8) | (b4 & 255);
            } else {
                c = (b4 << 24) | ((b3 & 255) << 16) | ((b2 & 255) << 8) | (b & 255);
            }
        }
        if (c == 0) {
            reportNull();
        }
        return c;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int nextTranslated() throws XMLStreamException, IOException {
        byte bNextByte;
        if (this.mInputPtr < this.mInputEnd) {
            byte[] bArr = this.mByteBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            bNextByte = bArr[i];
        } else {
            bNextByte = nextByte();
        }
        byte b = bNextByte;
        int ch = this.mSingleByteTranslation[b & 255];
        if (ch < 0) {
            ch = -ch;
        }
        return ch;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int skipMbWs() throws XMLStreamException, IOException {
        int count = 0;
        while (true) {
            int c = nextMultiByte();
            if (c > 32) {
                this.mInputPtr -= this.mBytesPerChar;
                return count;
            }
            if (c == 13 || c == 10) {
                skipMbLF(c);
            } else if (c == 0) {
                reportNull();
            }
            count++;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int skipTranslatedWs() throws XMLStreamException, IOException {
        int count = 0;
        while (true) {
            int c = nextTranslated();
            if (c > 32 && c != 133) {
                this.mInputPtr--;
                return count;
            }
            if (c == 13 || c == 10) {
                skipTranslatedLF(c);
            } else if (c == 0) {
                reportNull();
            }
            count++;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void skipMbLF(int lf) throws XMLStreamException, IOException {
        if (lf == 13) {
            int c = nextMultiByte();
            if (c != 10) {
                this.mInputPtr -= this.mBytesPerChar;
            }
        }
        this.mInputRow++;
        this.mInputRowStart = this.mInputPtr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void skipTranslatedLF(int lf) throws XMLStreamException, IOException {
        if (lf == 13) {
            int c = nextTranslated();
            if (c != 10) {
                this.mInputPtr--;
            }
        }
        this.mInputRow++;
        this.mInputRowStart = this.mInputPtr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int checkMbKeyword(String expected) throws XMLStreamException, IOException {
        int len = expected.length();
        for (int ptr = 1; ptr < len; ptr++) {
            int c = nextMultiByte();
            if (c == 0) {
                reportNull();
            }
            if (c != expected.charAt(ptr)) {
                return c;
            }
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected int checkTranslatedKeyword(String expected) throws XMLStreamException, IOException {
        int len = expected.length();
        for (int ptr = 1; ptr < len; ptr++) {
            int c = nextTranslated();
            if (c == 0) {
                reportNull();
            }
            if (c != expected.charAt(ptr)) {
                return c;
            }
        }
        return 0;
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private void verifyEncoding(String id, int bpc) {
        if (this.mByteSizeFound && bpc != this.mBytesPerChar) {
            if (this.mEBCDIC) {
                reportXmlProblem("Declared encoding '" + id + "' incompatible with auto-detected physical encoding (EBCDIC variant), can not decode input since actual code page not known");
            }
            reportXmlProblem("Declared encoding '" + id + "' uses " + bpc + " bytes per character; but physical encoding appeared to use " + this.mBytesPerChar + "; cannot decode");
        }
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private void verifyEncoding(String id, int bpc, boolean bigEndian) {
        if (this.mByteSizeFound) {
            verifyEncoding(id, bpc);
            if (bigEndian != this.mBigEndian) {
                String bigStr = bigEndian ? "big" : "little";
                reportXmlProblem("Declared encoding '" + id + "' has different endianness (" + bigStr + " endian) than what physical ordering appeared to be; cannot decode");
            }
        }
    }

    private void reportWeirdUCS4(String type) throws IOException {
        throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
    }
}
