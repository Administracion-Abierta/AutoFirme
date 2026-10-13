package com.ctc.wstx.sw;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.WstxInputData;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.MessageFormat;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.io.EscapingWriterFactory;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/XmlWriter.class */
public abstract class XmlWriter {
    protected static final int SURR1_FIRST = 55296;
    protected static final int SURR1_LAST = 56319;
    protected static final int SURR2_FIRST = 56320;
    protected static final int SURR2_LAST = 57343;
    protected static final char DEFAULT_QUOTE_CHAR = '\"';
    protected final WriterConfig mConfig;
    protected final String mEncoding;
    protected final boolean mNsAware;
    protected final boolean mCheckStructure;
    protected final boolean mCheckContent;
    protected final boolean mCheckNames;
    protected final boolean mFixContent;
    final boolean mEscapeCR;
    final boolean mAddSpaceAfterEmptyElem;
    final boolean mUseDoubleQuotesInXmlDecl;
    protected final boolean mAutoCloseOutput;
    protected Writer mTextWriter;
    protected Writer mAttrValueWriter;
    protected boolean mXml11 = false;
    protected XmlWriterWrapper mRawWrapper = null;
    protected XmlWriterWrapper mTextWrapper = null;
    protected int mLocPastChars = 0;
    protected int mLocRowNr = 1;
    protected int mLocRowStartOffset = 0;

    protected abstract OutputStream getOutputStream();

    protected abstract Writer getWriter();

    public abstract void close(boolean z) throws IOException;

    public abstract void flush() throws IOException;

    public abstract void writeRaw(String str, int i, int i2) throws IOException;

    public abstract void writeRaw(char[] cArr, int i, int i2) throws IOException;

    public abstract void writeRawAscii(char[] cArr, int i, int i2) throws IOException;

    public abstract void writeCDataStart() throws IOException;

    public abstract void writeCDataEnd() throws IOException;

    public abstract void writeCommentStart() throws IOException;

    public abstract void writeCommentEnd() throws IOException;

    public abstract void writePIStart(String str, boolean z) throws IOException;

    public abstract void writePIEnd() throws IOException;

    public abstract int writeCData(String str) throws XMLStreamException, IOException;

    public abstract int writeCData(char[] cArr, int i, int i2) throws XMLStreamException, IOException;

    public abstract void writeCharacters(String str) throws IOException;

    public abstract void writeCharacters(char[] cArr, int i, int i2) throws IOException;

    public abstract int writeComment(String str) throws XMLStreamException, IOException;

    public abstract void writeDTD(String str) throws XMLStreamException, IOException;

    public abstract void writeDTD(String str, String str2, String str3, String str4) throws XMLStreamException, IOException;

    public abstract void writeEntityReference(String str) throws XMLStreamException, IOException;

    public abstract int writePI(String str, String str2) throws XMLStreamException, IOException;

    public abstract void writeXmlDeclaration(String str, String str2, String str3) throws IOException;

    public abstract void writeStartTagStart(String str) throws XMLStreamException, IOException;

    public abstract void writeStartTagStart(String str, String str2) throws XMLStreamException, IOException;

    public abstract void writeStartTagEnd() throws IOException;

    public abstract void writeStartTagEmptyEnd() throws IOException;

    public abstract void writeEndTag(String str) throws IOException;

    public abstract void writeEndTag(String str, String str2) throws IOException;

    public abstract void writeAttribute(String str, String str2) throws XMLStreamException, IOException;

    public abstract void writeAttribute(String str, char[] cArr, int i, int i2) throws XMLStreamException, IOException;

    public abstract void writeAttribute(String str, String str2, String str3) throws XMLStreamException, IOException;

    public abstract void writeAttribute(String str, String str2, char[] cArr, int i, int i2) throws XMLStreamException, IOException;

    public abstract void writeTypedElement(AsciiValueEncoder asciiValueEncoder) throws IOException;

    public abstract void writeTypedElement(AsciiValueEncoder asciiValueEncoder, XMLValidator xMLValidator, char[] cArr) throws XMLStreamException, IOException;

    public abstract void writeTypedAttribute(String str, AsciiValueEncoder asciiValueEncoder) throws XMLStreamException, IOException;

    public abstract void writeTypedAttribute(String str, String str2, AsciiValueEncoder asciiValueEncoder) throws XMLStreamException, IOException;

    public abstract void writeTypedAttribute(String str, String str2, String str3, AsciiValueEncoder asciiValueEncoder, XMLValidator xMLValidator, char[] cArr) throws XMLStreamException, IOException;

    protected abstract int getOutputPtr();

    protected XmlWriter(WriterConfig cfg, String encoding, boolean autoclose) throws IOException {
        this.mConfig = cfg;
        this.mEncoding = encoding;
        this.mAutoCloseOutput = autoclose;
        int flags = cfg.getConfigFlags();
        this.mNsAware = (flags & 1) != 0;
        this.mCheckStructure = (flags & 256) != 0;
        this.mCheckContent = (flags & 512) != 0;
        this.mCheckNames = (flags & 1024) != 0;
        this.mFixContent = (flags & 4096) != 0;
        this.mEscapeCR = (flags & 32) != 0;
        this.mAddSpaceAfterEmptyElem = (flags & 64) != 0;
        this.mUseDoubleQuotesInXmlDecl = (flags & 16384) != 0;
        EscapingWriterFactory f = this.mConfig.getTextEscaperFactory();
        if (f == null) {
            this.mTextWriter = null;
        } else {
            String enc = (this.mEncoding == null || this.mEncoding.length() == 0) ? "UTF-8" : this.mEncoding;
            this.mTextWriter = f.createEscapingWriterFor(wrapAsRawWriter(), enc);
        }
        EscapingWriterFactory f2 = this.mConfig.getAttrValueEscaperFactory();
        if (f2 == null) {
            this.mAttrValueWriter = null;
        } else {
            String enc2 = (this.mEncoding == null || this.mEncoding.length() == 0) ? "UTF-8" : this.mEncoding;
            this.mAttrValueWriter = f2.createEscapingWriterFor(wrapAsRawWriter(), enc2);
        }
    }

    public void enableXml11() {
        this.mXml11 = true;
    }

    public void writeRaw(String str) throws IOException {
        writeRaw(str, 0, str.length());
    }

    public int getRow() {
        return this.mLocRowNr;
    }

    public int getColumn() {
        return (getOutputPtr() - this.mLocRowStartOffset) + 1;
    }

    public int getAbsOffset() {
        return this.mLocPastChars + getOutputPtr();
    }

    public final Writer wrapAsRawWriter() {
        if (this.mRawWrapper == null) {
            this.mRawWrapper = XmlWriterWrapper.wrapWriteRaw(this);
        }
        return this.mRawWrapper;
    }

    public final Writer wrapAsTextWriter() {
        if (this.mTextWrapper == null) {
            this.mTextWrapper = XmlWriterWrapper.wrapWriteCharacters(this);
        }
        return this.mTextWrapper;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final void verifyNameValidity(String name, boolean checkNs) throws XMLStreamException {
        String msg;
        if (name == null || name.length() == 0) {
            reportNwfName(ErrorConsts.WERR_NAME_EMPTY);
        }
        int illegalIx = WstxInputData.findIllegalNameChar(name, checkNs, this.mXml11);
        if (illegalIx >= 0) {
            if (illegalIx == 0) {
                msg = MessageFormat.format(ErrorConsts.WERR_NAME_ILLEGAL_FIRST_CHAR, WstxInputData.getCharDesc(name.charAt(0)), name);
            } else {
                msg = MessageFormat.format(ErrorConsts.WERR_NAME_ILLEGAL_CHAR, WstxInputData.getCharDesc(name.charAt(illegalIx)), name, Integer.valueOf(illegalIx));
            }
            reportNwfName(msg);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void reportNwfName(String msg) throws XMLStreamException {
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void reportNwfContent(String msg) throws XMLStreamException {
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void throwOutputError(String msg) throws XMLStreamException {
        try {
            flush();
            throw new XMLStreamException(msg);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void throwOutputError(String format, Object arg) throws XMLStreamException {
        throwOutputError(MessageFormat.format(format, arg));
    }

    protected char handleInvalidChar(int c) throws IOException {
        flush();
        InvalidCharHandler h = this.mConfig.getInvalidCharHandler();
        if (h == null) {
            h = InvalidCharHandler.FailingHandler.getInstance();
        }
        return h.convertInvalidChar(c);
    }
}
