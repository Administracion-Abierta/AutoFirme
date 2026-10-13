package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.util.StringUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/ReaderBootstrapper.class */
public final class ReaderBootstrapper extends InputBootstrapper {
    static final char CHAR_BOM_MARKER = 65279;
    final Reader mIn;
    final String mInputEncoding;
    private char[] mCharBuffer;
    private int mInputPtr;
    private int mInputEnd;

    private ReaderBootstrapper(String pubId, SystemId sysId, Reader r, String appEncoding) {
        super(pubId, sysId);
        this.mIn = r;
        if (appEncoding == null && (r instanceof InputStreamReader)) {
            appEncoding = ((InputStreamReader) r).getEncoding();
        }
        this.mInputEncoding = appEncoding;
    }

    public static ReaderBootstrapper getInstance(String pubId, SystemId sysId, Reader r, String appEncoding) {
        return new ReaderBootstrapper(pubId, sysId, r, appEncoding);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.io.InputBootstrapper
    public Reader bootstrapInput(ReaderConfig cfg, boolean mainDoc, int xmlVersion) throws XMLStreamException, IOException {
        this.mCharBuffer = cfg == null ? new char[128] : cfg.allocSmallCBuffer(128);
        initialLoad(7);
        if (this.mInputEnd >= 7) {
            char c = this.mCharBuffer[this.mInputPtr];
            if (c == CHAR_BOM_MARKER) {
                char[] cArr = this.mCharBuffer;
                int i = this.mInputPtr + 1;
                this.mInputPtr = i;
                c = cArr[i];
            }
            if (c == '<') {
                if (this.mCharBuffer[this.mInputPtr + 1] == '?' && this.mCharBuffer[this.mInputPtr + 2] == 'x' && this.mCharBuffer[this.mInputPtr + 3] == 'm' && this.mCharBuffer[this.mInputPtr + 4] == 'l' && this.mCharBuffer[this.mInputPtr + 5] <= ' ') {
                    this.mInputPtr += 6;
                    readXmlDecl(mainDoc, xmlVersion);
                    if (this.mFoundEncoding != null && this.mInputEncoding != null) {
                        verifyXmlEncoding(cfg);
                    }
                }
            } else if (c == 239) {
                throw new WstxIOException("Unexpected first character (char code 0xEF), not valid in xml document: could be mangled UTF-8 BOM marker. Make sure that the Reader uses correct encoding or pass an InputStream instead");
            }
        }
        if (this.mInputPtr < this.mInputEnd) {
            return new MergedReader(cfg, this.mIn, this.mCharBuffer, this.mInputPtr, this.mInputEnd);
        }
        return this.mIn;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    public String getInputEncoding() {
        return this.mInputEncoding;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    public int getInputTotal() {
        return this.mInputProcessed + this.mInputPtr;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    public int getInputColumn() {
        return this.mInputPtr - this.mInputRowStart;
    }

    protected void verifyXmlEncoding(ReaderConfig cfg) throws XMLStreamException {
        XMLReporter rep;
        String inputEnc = this.mInputEncoding;
        if (!StringUtil.equalEncodings(inputEnc, this.mFoundEncoding) && (rep = cfg.getXMLReporter()) != null) {
            Location loc = getLocation();
            String msg = MessageFormat.format(ErrorConsts.W_MIXED_ENCODINGS, this.mFoundEncoding, inputEnc);
            String type = ErrorConsts.WT_XML_DECL;
            XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 1, type);
            rep.report(msg, type, prob, loc);
        }
    }

    protected boolean initialLoad(int minimum) throws IOException {
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        while (this.mInputEnd < minimum) {
            int count = this.mIn.read(this.mCharBuffer, this.mInputEnd, this.mCharBuffer.length - this.mInputEnd);
            if (count < 1) {
                return false;
            }
            this.mInputEnd += count;
        }
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void loadMore() throws XMLStreamException, IOException {
        this.mInputProcessed += this.mInputEnd;
        this.mInputRowStart -= this.mInputEnd;
        this.mInputPtr = 0;
        this.mInputEnd = this.mIn.read(this.mCharBuffer, 0, this.mCharBuffer.length);
        if (this.mInputEnd < 1) {
            throw new WstxEOFException(ParsingErrorMsgs.SUFFIX_IN_XML_DECL, getLocation());
        }
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    protected void pushback() {
        this.mInputPtr--;
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int getNext() throws IOException {
        if (this.mInputPtr >= this.mInputEnd) {
            return nextChar();
        }
        char[] cArr = this.mCharBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int getNextAfterWs(boolean reqWs) throws XMLStreamException, IOException {
        char cNextChar;
        char c;
        int count = 0;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mCharBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cNextChar = cArr[i];
            } else {
                cNextChar = nextChar();
            }
            c = cNextChar;
            if (c > ' ') {
                break;
            }
            if (c == '\r' || c == '\n') {
                skipCRLF(c);
            } else if (c == 0) {
                reportNull();
            }
            count++;
        }
        if (reqWs && count == 0) {
            reportUnexpectedChar(c, "; expected a white space");
        }
        return c;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int checkKeyword(String exp) throws XMLStreamException, IOException {
        char cNextChar;
        int len = exp.length();
        for (int ptr = 1; ptr < len; ptr++) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mCharBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cNextChar = cArr[i];
            } else {
                cNextChar = nextChar();
            }
            char c = cNextChar;
            if (c != exp.charAt(ptr)) {
                return c;
            }
            if (c == 0) {
                reportNull();
            }
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    @Override // com.ctc.wstx.io.InputBootstrapper
    protected int readQuotedValue(char[] kw, int quoteChar) throws XMLStreamException, IOException {
        char cNextChar;
        int i = 0;
        int len = kw.length;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mCharBuffer;
                int i2 = this.mInputPtr;
                this.mInputPtr = i2 + 1;
                cNextChar = cArr[i2];
            } else {
                cNextChar = nextChar();
            }
            char c = cNextChar;
            if (c == '\r' || c == '\n') {
                skipCRLF(c);
            } else if (c == 0) {
                reportNull();
            }
            if (c == quoteChar) {
                break;
            }
            if (i < len) {
                int i3 = i;
                i++;
                kw[i3] = c;
            }
        }
        if (i < len) {
            return i;
        }
        return -1;
    }

    @Override // com.ctc.wstx.io.InputBootstrapper
    protected Location getLocation() {
        return new WstxInputLocation((WstxInputLocation) null, this.mPublicId, this.mSystemId, (this.mInputProcessed + this.mInputPtr) - 1, this.mInputRow, this.mInputPtr - this.mInputRowStart);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected char nextChar() throws XMLStreamException, IOException {
        if (this.mInputPtr >= this.mInputEnd) {
            loadMore();
        }
        char[] cArr = this.mCharBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void skipCRLF(char lf) throws XMLStreamException, IOException {
        char cNextChar;
        if (lf == '\r') {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mCharBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cNextChar = cArr[i];
            } else {
                cNextChar = nextChar();
            }
            char c = cNextChar;
            if (c != '\n') {
                this.mInputPtr--;
            }
        }
        this.mInputRow++;
        this.mInputRowStart = this.mInputPtr;
    }
}
