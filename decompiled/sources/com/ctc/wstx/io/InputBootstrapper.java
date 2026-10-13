package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.XmlConsts;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.exc.WstxUnexpectedCharException;
import java.io.IOException;
import java.io.Reader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/InputBootstrapper.class */
public abstract class InputBootstrapper {
    protected static final String ERR_XMLDECL_KW_VERSION = "; expected keyword 'version'";
    protected static final String ERR_XMLDECL_KW_ENCODING = "; expected keyword 'encoding'";
    protected static final String ERR_XMLDECL_KW_STANDALONE = "; expected keyword 'standalone'";
    protected static final String ERR_XMLDECL_END_MARKER = "; expected \"?>\" end marker";
    protected static final String ERR_XMLDECL_EXP_SPACE = "; expected a white space";
    protected static final String ERR_XMLDECL_EXP_EQ = "; expected '=' after ";
    protected static final String ERR_XMLDECL_EXP_ATTRVAL = "; expected a quote character enclosing value for ";
    public static final char CHAR_NULL = 0;
    public static final char CHAR_SPACE = ' ';
    public static final char CHAR_NEL = 133;
    public static final byte CHAR_CR = 13;
    public static final byte CHAR_LF = 10;
    public static final byte BYTE_NULL = 0;
    public static final byte BYTE_CR = 13;
    public static final byte BYTE_LF = 10;
    protected final String mPublicId;
    protected final SystemId mSystemId;
    protected String mFoundEncoding;
    protected String mStandalone;
    protected int mInputProcessed = 0;
    protected int mInputRow = 1;
    protected int mInputRowStart = 0;
    protected int mDeclaredXmlVersion = 0;
    protected boolean mXml11Handling = false;
    protected final char[] mKeywordBuffer = new char[60];

    public abstract Reader bootstrapInput(ReaderConfig readerConfig, boolean z, int i) throws XMLStreamException, IOException;

    public abstract int getInputTotal();

    public abstract int getInputColumn();

    public abstract String getInputEncoding();

    protected abstract void pushback();

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected abstract int getNext() throws IOException;

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected abstract int getNextAfterWs(boolean z) throws IOException;

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected abstract int checkKeyword(String str) throws IOException;

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected abstract int readQuotedValue(char[] cArr, int i) throws IOException;

    protected abstract Location getLocation();

    protected InputBootstrapper(String pubId, SystemId sysId) {
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }

    protected void initFrom(InputBootstrapper src) {
        this.mInputProcessed = src.mInputProcessed;
        this.mInputRow = src.mInputRow;
        this.mInputRowStart = src.mInputRowStart;
        this.mDeclaredXmlVersion = src.mDeclaredXmlVersion;
        this.mFoundEncoding = src.mFoundEncoding;
        this.mStandalone = src.mStandalone;
        this.mXml11Handling = src.mXml11Handling;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public SystemId getSystemId() {
        return this.mSystemId;
    }

    public int getDeclaredVersion() {
        return this.mDeclaredXmlVersion;
    }

    public boolean declaredXml11() {
        return this.mDeclaredXmlVersion == 272;
    }

    public String getStandalone() {
        return this.mStandalone;
    }

    public String getDeclaredEncoding() {
        return this.mFoundEncoding;
    }

    public int getInputRow() {
        return this.mInputRow;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void readXmlDecl(boolean isMainDoc, int xmlVersion) throws XMLStreamException, IOException {
        int c = getNextAfterWs(false);
        if (c != 118) {
            if (isMainDoc) {
                reportUnexpectedChar(c, ERR_XMLDECL_KW_VERSION);
            }
        } else {
            this.mDeclaredXmlVersion = readXmlVersion();
            c = getWsOrChar(63);
        }
        boolean thisIs11 = this.mDeclaredXmlVersion == 272;
        if (xmlVersion != 0) {
            this.mXml11Handling = 272 == xmlVersion;
            if (thisIs11 && !this.mXml11Handling) {
                reportXmlProblem(ErrorConsts.ERR_XML_10_VS_11);
            }
        } else {
            this.mXml11Handling = thisIs11;
        }
        if (c != 101) {
            if (!isMainDoc) {
                reportUnexpectedChar(c, ERR_XMLDECL_KW_ENCODING);
            }
        } else {
            this.mFoundEncoding = readXmlEncoding();
            c = getWsOrChar(63);
        }
        if (isMainDoc && c == 115) {
            this.mStandalone = readXmlStandalone();
            c = getWsOrChar(63);
        }
        if (c != 63) {
            reportUnexpectedChar(c, ERR_XMLDECL_END_MARKER);
        }
        int c2 = getNext();
        if (c2 != 62) {
            reportUnexpectedChar(c2, ERR_XMLDECL_END_MARKER);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private final int readXmlVersion() throws XMLStreamException, IOException {
        String got;
        int c = checkKeyword(XmlConsts.XML_DECL_KW_VERSION);
        if (c != 0) {
            reportUnexpectedChar(c, XmlConsts.XML_DECL_KW_VERSION);
        }
        int len = readQuotedValue(this.mKeywordBuffer, handleEq(XmlConsts.XML_DECL_KW_VERSION));
        if (len == 3 && this.mKeywordBuffer[0] == '1' && this.mKeywordBuffer[1] == '.') {
            char c2 = this.mKeywordBuffer[2];
            if (c2 == '0') {
                return 256;
            }
            if (c2 == '1') {
                return 272;
            }
        }
        if (len < 0) {
            got = OperatorName.SHOW_TEXT_LINE + new String(this.mKeywordBuffer) + "[..]'";
        } else if (len == 0) {
            got = "<empty>";
        } else {
            got = OperatorName.SHOW_TEXT_LINE + new String(this.mKeywordBuffer, 0, len) + OperatorName.SHOW_TEXT_LINE;
        }
        reportPseudoAttrProblem(XmlConsts.XML_DECL_KW_VERSION, got, "1.0", XmlConsts.XML_V_11_STR);
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private final String readXmlEncoding() throws XMLStreamException, IOException {
        int c = checkKeyword("encoding");
        if (c != 0) {
            reportUnexpectedChar(c, "encoding");
        }
        int len = readQuotedValue(this.mKeywordBuffer, handleEq("encoding"));
        if (len == 0) {
            reportPseudoAttrProblem("encoding", null, null, null);
        }
        if (len < 0) {
            return new String(this.mKeywordBuffer);
        }
        return new String(this.mKeywordBuffer, 0, len);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private final String readXmlStandalone() throws XMLStreamException, IOException {
        String got;
        int c = checkKeyword(XmlConsts.XML_DECL_KW_STANDALONE);
        if (c != 0) {
            reportUnexpectedChar(c, XmlConsts.XML_DECL_KW_STANDALONE);
        }
        int len = readQuotedValue(this.mKeywordBuffer, handleEq(XmlConsts.XML_DECL_KW_STANDALONE));
        if (len == 2) {
            if (this.mKeywordBuffer[0] == 'n' && this.mKeywordBuffer[1] == 'o') {
                return XmlConsts.XML_SA_NO;
            }
        } else if (len == 3 && this.mKeywordBuffer[0] == 'y' && this.mKeywordBuffer[1] == 'e' && this.mKeywordBuffer[2] == 's') {
            return XmlConsts.XML_SA_YES;
        }
        if (len < 0) {
            got = OperatorName.SHOW_TEXT_LINE + new String(this.mKeywordBuffer) + "[..]'";
        } else if (len == 0) {
            got = "<empty>";
        } else {
            got = OperatorName.SHOW_TEXT_LINE + new String(this.mKeywordBuffer, 0, len) + OperatorName.SHOW_TEXT_LINE;
        }
        reportPseudoAttrProblem(XmlConsts.XML_DECL_KW_STANDALONE, got, XmlConsts.XML_SA_YES, XmlConsts.XML_SA_NO);
        return got;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private final int handleEq(String attr) throws XMLStreamException, IOException {
        int c = getNextAfterWs(false);
        if (c != 61) {
            reportUnexpectedChar(c, "; expected '=' after '" + attr + OperatorName.SHOW_TEXT_LINE);
        }
        int c2 = getNextAfterWs(false);
        if (c2 != 34 && c2 != 39) {
            reportUnexpectedChar(c2, "; expected a quote character enclosing value for '" + attr + OperatorName.SHOW_TEXT_LINE);
        }
        return c2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private final int getWsOrChar(int ok) throws XMLStreamException, IOException {
        int c = getNext();
        if (c == ok) {
            return c;
        }
        if (c > 32) {
            reportUnexpectedChar(c, "; expected either '" + ((char) ok) + "' or white space");
        }
        if (c == 10 || c == 13) {
            pushback();
        }
        return getNextAfterWs(false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void reportNull() throws XMLStreamException {
        throw new WstxException("Illegal null byte in input stream", getLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void reportXmlProblem(String msg) throws XMLStreamException {
        throw new WstxParsingException(msg, getLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void reportUnexpectedChar(int i, String msg) throws XMLStreamException {
        String excMsg;
        char c = (char) i;
        if (Character.isISOControl(c)) {
            excMsg = "Unexpected character (CTRL-CHAR, code " + i + ")" + msg;
        } else {
            excMsg = "Unexpected character '" + c + "' (code " + i + ")" + msg;
        }
        Location loc = getLocation();
        throw new WstxUnexpectedCharException(excMsg, loc, c);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    private final void reportPseudoAttrProblem(String attrName, String got, String expVal1, String expVal2) throws XMLStreamException {
        String expStr = expVal1 == null ? "" : "; expected \"" + expVal1 + "\" or \"" + expVal2 + OperatorName.SHOW_TEXT_LINE_AND_SPACE;
        if (got == null || got.length() == 0) {
            throw new WstxParsingException("Missing XML pseudo-attribute '" + attrName + "' value" + expStr, getLocation());
        }
        throw new WstxParsingException("Invalid XML pseudo-attribute '" + attrName + "' value " + got + expStr, getLocation());
    }
}
