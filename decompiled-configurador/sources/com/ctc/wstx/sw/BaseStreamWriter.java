package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.OutputConfigFlags;
import com.ctc.wstx.cfg.XmlConsts;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.exc.WstxValidationException;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.StreamReaderImpl;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.StringUtil;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2WriterImpl;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.ValidatorPair;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/BaseStreamWriter.class */
public abstract class BaseStreamWriter extends Stax2WriterImpl implements ValidationContext, OutputConfigFlags {
    protected static final int STATE_PROLOG = 1;
    protected static final int STATE_TREE = 2;
    protected static final int STATE_EPILOG = 3;
    protected static final char CHAR_SPACE = ' ';
    protected static final int MIN_ARRAYCOPY = 12;
    protected static final int ATTR_MIN_ARRAYCOPY = 12;
    protected static final int DEFAULT_COPYBUFFER_LEN = 512;
    protected final XmlWriter mWriter;
    protected final WriterConfig mConfig;
    protected final boolean mCfgCDataAsText;
    protected final boolean mCfgCopyDefaultAttrs;
    protected final boolean mCfgAutomaticEmptyElems;
    protected boolean mCheckStructure;
    protected boolean mCheckAttrs;
    protected String mEncoding;
    protected boolean mReturnNullForDefaultNamespace;
    protected char[] mCopyBuffer = null;
    protected XMLValidator mValidator = null;
    protected boolean mXml11 = false;
    protected ValidationProblemHandler mVldProbHandler = null;
    protected int mState = 1;
    protected boolean mAnyOutput = false;
    protected boolean mStartElementOpen = false;
    protected boolean mEmptyElement = false;
    protected int mVldContent = 4;
    protected String mDtdRootElem = null;

    public abstract NamespaceContext getNamespaceContext();

    public abstract String getPrefix(String str);

    public abstract void setDefaultNamespace(String str) throws XMLStreamException;

    public abstract void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException;

    public abstract void setPrefix(String str, String str2) throws XMLStreamException;

    public abstract void writeAttribute(String str, String str2) throws XMLStreamException;

    public abstract void writeAttribute(String str, String str2, String str3) throws XMLStreamException;

    public abstract void writeAttribute(String str, String str2, String str3, String str4) throws XMLStreamException;

    public abstract void writeDefaultNamespace(String str) throws XMLStreamException;

    public abstract void writeEmptyElement(String str) throws XMLStreamException;

    public abstract void writeEmptyElement(String str, String str2) throws XMLStreamException;

    public abstract void writeEmptyElement(String str, String str2, String str3) throws XMLStreamException;

    public abstract void writeEndElement() throws XMLStreamException;

    public abstract void writeNamespace(String str, String str2) throws XMLStreamException;

    public abstract void writeStartElement(String str) throws XMLStreamException;

    public abstract void writeStartElement(String str, String str2) throws XMLStreamException;

    public abstract void writeStartElement(String str, String str2, String str3) throws XMLStreamException;

    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public abstract void writeFullEndElement() throws XMLStreamException;

    public abstract QName getCurrentElementName();

    public abstract String getNamespaceURI(String str);

    public abstract void writeStartElement(StartElement startElement) throws XMLStreamException;

    public abstract void writeEndElement(QName qName) throws XMLStreamException;

    protected abstract void closeStartElement(boolean z) throws XMLStreamException;

    public abstract void copyStartElement(InputElementStack inputElementStack, AttributeCollector attributeCollector) throws XMLStreamException, IOException;

    public abstract String validateQNamePrefix(QName qName) throws XMLStreamException;

    protected abstract String getTopElementDesc();

    protected BaseStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        this.mWriter = xw;
        this.mEncoding = enc;
        this.mConfig = cfg;
        int flags = cfg.getConfigFlags();
        this.mCheckStructure = (flags & 256) != 0;
        this.mCheckAttrs = (flags & 2048) != 0;
        this.mCfgAutomaticEmptyElems = (flags & 4) != 0;
        this.mCfgCDataAsText = (flags & 8) != 0;
        this.mCfgCopyDefaultAttrs = (flags & 16) != 0;
        this.mReturnNullForDefaultNamespace = this.mConfig.returnNullForDefaultNamespace();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void close() throws XMLStreamException {
        _finishDocument(false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void flush() throws XMLStreamException {
        try {
            this.mWriter.flush();
        } catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    public Object getProperty(String name) {
        if (name.equals(WstxOutputProperties.P_OUTPUT_UNDERLYING_STREAM)) {
            return this.mWriter.getOutputStream();
        }
        if (name.equals(WstxOutputProperties.P_OUTPUT_UNDERLYING_WRITER)) {
            return this.mWriter.getWriter();
        }
        return this.mConfig.getProperty(name);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCData(String data) throws XMLStreamException {
        if (this.mCfgCDataAsText) {
            writeCharacters(data);
            return;
        }
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        verifyWriteCData();
        if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(data, false);
        }
        try {
            int ix = this.mWriter.writeCData(data);
            if (ix >= 0) {
                reportNwfContent(ErrorConsts.WERR_CDATA_CONTENT, DataUtil.Integer(ix));
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && inPrologOrEpilog() && !StringUtil.isAllWhitespace(text, start, len)) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0 || !StringUtil.isAllWhitespace(text, start, len)) {
                reportInvalidContent(4);
            }
        } else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(text, start, start + len, false);
        }
        if (len > 0) {
            try {
                if (inPrologOrEpilog()) {
                    this.mWriter.writeRaw(text, start, len);
                } else {
                    this.mWriter.writeCharacters(text, start, len);
                }
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCharacters(String text) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && inPrologOrEpilog() && !StringUtil.isAllWhitespace(text)) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0 || !StringUtil.isAllWhitespace(text)) {
                reportInvalidContent(4);
            }
        } else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(text, false);
        }
        if (inPrologOrEpilog()) {
            try {
                this.mWriter.writeRaw(text);
                return;
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
        int len = text.length();
        if (len >= 12) {
            char[] buf = getCopyBuffer();
            int offset = 0;
            while (len > 0) {
                int thisLen = len > buf.length ? buf.length : len;
                text.getChars(offset, offset + thisLen, buf, 0);
                try {
                    this.mWriter.writeCharacters(buf, 0, thisLen);
                    offset += thisLen;
                    len -= thisLen;
                } catch (IOException ioe2) {
                    throw new WstxIOException(ioe2);
                }
            }
            return;
        }
        try {
            this.mWriter.writeCharacters(text);
        } catch (IOException ioe3) {
            throw new WstxIOException(ioe3);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeComment(String data) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mVldContent == 0) {
            reportInvalidContent(5);
        }
        try {
            int ix = this.mWriter.writeComment(data);
            if (ix >= 0) {
                reportNwfContent(ErrorConsts.WERR_COMMENT_CONTENT, DataUtil.Integer(ix));
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeDTD(String dtd) throws XMLStreamException {
        verifyWriteDTD();
        this.mDtdRootElem = "";
        try {
            this.mWriter.writeDTD(dtd);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEndDocument() throws XMLStreamException {
        _finishDocument(false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEntityRef(String name) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && inPrologOrEpilog()) {
            reportNwfStructure("Trying to output an entity reference outside main element tree (in prolog or epilog)");
        }
        if (this.mVldContent == 0) {
            reportInvalidContent(9);
        }
        try {
            this.mWriter.writeEntityReference(name);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        writeProcessingInstruction(target, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mVldContent == 0) {
            reportInvalidContent(3);
        }
        try {
            int ix = this.mWriter.writePI(target, data);
            if (ix >= 0) {
                throw new XMLStreamException("Illegal input: processing instruction content has embedded '?>' in it (index " + ix + ")");
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument() throws XMLStreamException {
        if (this.mEncoding == null) {
            this.mEncoding = "UTF-8";
        }
        writeStartDocument(this.mEncoding, "1.0");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument(String version) throws XMLStreamException {
        writeStartDocument(this.mEncoding, version);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        doWriteStartDocument(version, encoding, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doWriteStartDocument(String version, String encoding, String standAlone) throws XMLStreamException {
        if (this.mCheckStructure && this.mAnyOutput) {
            reportNwfStructure("Can not output XML declaration, after other output has already been done.");
        }
        this.mAnyOutput = true;
        if (this.mConfig.willValidateContent() && version != null && version.length() > 0 && !version.equals("1.0") && !version.equals(XmlConsts.XML_V_11_STR)) {
            reportNwfContent("Illegal version argument ('" + version + "'); should only use '1.0' or '" + XmlConsts.XML_V_11_STR + "'");
        }
        if (version == null || version.length() == 0) {
            version = "1.0";
        }
        this.mXml11 = XmlConsts.XML_V_11_STR.equals(version);
        if (this.mXml11) {
            this.mWriter.enableXml11();
        }
        if (encoding != null && encoding.length() > 0 && (this.mEncoding == null || this.mEncoding.length() == 0)) {
            this.mEncoding = encoding;
        }
        try {
            this.mWriter.writeXmlDeclaration(version, encoding, standAlone);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void copyEventFromReader(XMLStreamReader2 sr, boolean preserveEventData) throws XMLStreamException {
        try {
            switch (sr.getEventType()) {
                case 1:
                    if (sr instanceof StreamReaderImpl) {
                        StreamReaderImpl impl = (StreamReaderImpl) sr;
                        copyStartElement(impl.getInputElementStack(), impl.getAttributeCollector());
                        return;
                    } else {
                        super.copyStartElement(sr);
                        return;
                    }
                case 2:
                    writeEndElement();
                    return;
                case 3:
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        closeStartElement(this.mEmptyElement);
                    }
                    this.mWriter.writePIStart(sr.getPITarget(), true);
                    sr.getText(wrapAsRawWriter(), preserveEventData);
                    this.mWriter.writePIEnd();
                    return;
                case 4:
                    break;
                case 5:
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        closeStartElement(this.mEmptyElement);
                    }
                    this.mWriter.writeCommentStart();
                    sr.getText(wrapAsRawWriter(), preserveEventData);
                    this.mWriter.writeCommentEnd();
                    return;
                case 6:
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        closeStartElement(this.mEmptyElement);
                    }
                    if (this.mValidator != null) {
                        writeSpace(sr.getText());
                        return;
                    } else {
                        sr.getText(wrapAsRawWriter(), preserveEventData);
                        return;
                    }
                case 7:
                    String version = sr.getVersion();
                    if (version != null && version.length() != 0) {
                        if (sr.standaloneSet()) {
                            writeStartDocument(sr.getVersion(), sr.getCharacterEncodingScheme(), sr.isStandalone());
                            return;
                        } else {
                            writeStartDocument(sr.getCharacterEncodingScheme(), sr.getVersion());
                            return;
                        }
                    }
                    return;
                case 8:
                    writeEndDocument();
                    return;
                case 9:
                    writeEntityRef(sr.getLocalName());
                    return;
                case 10:
                case 13:
                case 14:
                case 15:
                default:
                    throw new XMLStreamException("Unrecognized event type (" + sr.getEventType() + "); not sure how to copy");
                case 11:
                    DTDInfo info = sr.getDTDInfo();
                    if (info == null) {
                        throwOutputError("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                    }
                    writeDTD(info);
                    return;
                case 12:
                    if (this.mValidator != null) {
                        writeCData(sr.getText());
                        return;
                    }
                    if (!this.mCfgCDataAsText) {
                        this.mAnyOutput = true;
                        if (this.mStartElementOpen) {
                            closeStartElement(this.mEmptyElement);
                        }
                        if (this.mCheckStructure && inPrologOrEpilog()) {
                            reportNwfStructure(ErrorConsts.WERR_PROLOG_CDATA);
                        }
                        this.mWriter.writeCDataStart();
                        sr.getText(wrapAsRawWriter(), preserveEventData);
                        this.mWriter.writeCDataEnd();
                        return;
                    }
                    break;
            }
            if (this.mValidator != null) {
                writeCharacters(sr.getText());
                return;
            }
            this.mAnyOutput = true;
            if (this.mStartElementOpen) {
                closeStartElement(this.mEmptyElement);
            }
            sr.getText(wrapAsTextWriter(), preserveEventData);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void closeCompletely() throws XMLStreamException {
        _finishDocument(true);
    }

    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public boolean setProperty(String name, Object value) {
        return this.mConfig.setProperty(name, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        XMLValidator vld = schema.createValidator(this);
        if (this.mValidator == null) {
            this.mCheckStructure = true;
            this.mCheckAttrs = true;
            this.mValidator = vld;
        } else {
            this.mValidator = new ValidatorPair(this.mValidator, vld);
        }
        return vld;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        XMLValidator found = null;
        if (ValidatorPair.removeValidator(this.mValidator, schema, results)) {
            found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            if (this.mValidator == null) {
                resetValidationFlags();
            }
        }
        return found;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        XMLValidator found = null;
        if (ValidatorPair.removeValidator(this.mValidator, validator, results)) {
            found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            if (this.mValidator == null) {
                resetValidationFlags();
            }
        }
        return found;
    }

    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        ValidationProblemHandler oldH = this.mVldProbHandler;
        this.mVldProbHandler = h;
        return oldH;
    }

    private void resetValidationFlags() {
        int flags = this.mConfig.getConfigFlags();
        this.mCheckStructure = (flags & 256) != 0;
        this.mCheckAttrs = (flags & 2048) != 0;
    }

    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public XMLStreamLocation2 getLocation() {
        return new WstxInputLocation((WstxInputLocation) null, (String) null, (String) null, this.mWriter.getAbsOffset(), this.mWriter.getRow(), this.mWriter.getColumn());
    }

    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public String getEncoding() {
        return this.mEncoding;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeCData(char[] cbuf, int start, int len) throws XMLStreamException {
        if (this.mCfgCDataAsText) {
            writeCharacters(cbuf, start, len);
            return;
        }
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        verifyWriteCData();
        if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(cbuf, start, start + len, false);
        }
        try {
            int ix = this.mWriter.writeCData(cbuf, start, len);
            if (ix >= 0) {
                throwOutputError(ErrorConsts.WERR_CDATA_CONTENT, DataUtil.Integer(ix));
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeDTD(DTDInfo info) throws XMLStreamException {
        writeDTD(info.getDTDRootName(), info.getDTDSystemId(), info.getDTDPublicId(), info.getDTDInternalSubset());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        verifyWriteDTD();
        this.mDtdRootElem = rootName;
        try {
            this.mWriter.writeDTD(rootName, systemId, publicId, internalSubset);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        doWriteStartDocument(version, encoding, standAlone ? XmlConsts.XML_SA_YES : XmlConsts.XML_SA_NO);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, 0, text.length());
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text, int start, int offset) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, start, offset);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(char[] text, int start, int offset) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, start, offset);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(String text) throws XMLStreamException {
        writeRaw(text);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
        writeRaw(text, offset, length);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getXmlVersion() {
        return this.mXml11 ? XmlConsts.XML_V_11_STR : "1.0";
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getBaseUri() {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public Location getValidationLocation() {
        return getLocation();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.ValidationContext
    public void reportProblem(XMLValidationProblem prob) throws XMLStreamException {
        if (this.mVldProbHandler != null) {
            this.mVldProbHandler.reportProblem(prob);
            return;
        }
        if (prob.getSeverity() > 2) {
            throw WstxValidationException.create(prob);
        }
        XMLReporter rep = this.mConfig.getProblemReporter();
        if (rep != null) {
            doReportProblem(rep, prob);
        } else if (prob.getSeverity() >= 2) {
            throw WstxValidationException.create(prob);
        }
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public int addDefaultAttribute(String localName, String uri, String prefix, String value) {
        return -1;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public boolean isNotationDeclared(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public boolean isUnparsedEntityDeclared(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public int getAttributeCount() {
        return 0;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeLocalName(int index) {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeNamespace(int index) {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributePrefix(int index) {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeValue(int index) {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeValue(String nsURI, String localName) {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeType(int index) {
        return "";
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public int findAttributeIndex(String nsURI, String localName) {
        return -1;
    }

    public final Writer wrapAsRawWriter() {
        return this.mWriter.wrapAsRawWriter();
    }

    public final Writer wrapAsTextWriter() {
        return this.mWriter.wrapAsTextWriter();
    }

    protected boolean isValidating() {
        return this.mValidator != null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCharacters(Characters ch) throws XMLStreamException {
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && inPrologOrEpilog() && !ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                reportInvalidContent(4);
            } else if (!ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
                reportInvalidContent(4);
            }
        } else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(ch.getData(), false);
        }
        try {
            this.mWriter.writeCharacters(ch.getData());
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    protected final boolean inPrologOrEpilog() {
        return this.mState != 2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final void _finishDocument(boolean forceRealClose) throws XMLStreamException {
        if (this.mState != 3) {
            if (this.mCheckStructure && this.mState == 1) {
                reportNwfStructure("Trying to write END_DOCUMENT when document has no root (ie. trying to output empty document).");
            }
            if (this.mStartElementOpen) {
                closeStartElement(this.mEmptyElement);
            }
            if (this.mState != 3 && this.mConfig.automaticEndElementsEnabled()) {
                do {
                    writeEndElement();
                } while (this.mState != 3);
            }
        }
        char[] buf = this.mCopyBuffer;
        if (buf != null) {
            this.mCopyBuffer = null;
            this.mConfig.freeMediumCBuffer(buf);
        }
        try {
            this.mWriter.close(forceRealClose);
        } catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void verifyWriteCData() throws XMLStreamException {
        if (this.mCheckStructure && inPrologOrEpilog()) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_CDATA);
        }
        if (this.mVldContent <= 1) {
            reportInvalidContent(12);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void verifyWriteDTD() throws XMLStreamException {
        if (this.mCheckStructure) {
            if (this.mState != 1) {
                throw new XMLStreamException("Can not write DOCTYPE declaration (DTD) when not in prolog any more (state " + this.mState + "; start element(s) written)");
            }
            if (this.mDtdRootElem != null) {
                throw new XMLStreamException("Trying to write multiple DOCTYPE declarations");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void verifyRootElement(String localName, String prefix) throws XMLStreamException {
        int lnLen;
        int oldLen;
        if (isValidating() && this.mDtdRootElem != null && this.mDtdRootElem.length() > 0) {
            String wrongElem = null;
            if (!localName.equals(this.mDtdRootElem) && ((oldLen = this.mDtdRootElem.length()) <= (lnLen = localName.length()) || !this.mDtdRootElem.endsWith(localName) || this.mDtdRootElem.charAt((oldLen - lnLen) - 1) != ':')) {
                if (prefix == null) {
                    wrongElem = localName;
                } else {
                    wrongElem = prefix.length() == 0 ? "[unknown]:" + localName : prefix + ":" + localName;
                }
            }
            if (wrongElem != null) {
                reportValidationProblem(ErrorConsts.ERR_VLD_WRONG_ROOT, wrongElem, this.mDtdRootElem);
            }
        }
        this.mState = 2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void throwOutputError(String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void throwOutputError(String format, Object arg) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg);
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportIllegalMethod(String msg) throws XMLStreamException {
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportNwfStructure(String msg) throws XMLStreamException {
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportNwfStructure(String msg, Object arg) throws XMLStreamException {
        throwOutputError(msg, arg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportNwfContent(String msg) throws XMLStreamException {
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportNwfContent(String msg, Object arg) throws XMLStreamException {
        throwOutputError(msg, arg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportNwfAttr(String msg) throws XMLStreamException {
        throwOutputError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void reportNwfAttr(String msg, Object arg) throws XMLStreamException {
        throwOutputError(msg, arg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void throwFromIOE(IOException ioe) throws XMLStreamException {
        throw new WstxIOException(ioe);
    }

    protected static void reportIllegalArg(String msg) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void reportInvalidContent(int evtType) throws XMLStreamException {
        switch (this.mVldContent) {
            case 0:
                reportValidationProblem(ErrorConsts.ERR_VLD_EMPTY, getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            case 1:
                reportValidationProblem(ErrorConsts.ERR_VLD_NON_MIXED, getTopElementDesc());
                break;
            case 2:
            default:
                reportValidationProblem("Internal error: trying to report invalid content for " + evtType);
                break;
            case 3:
            case 4:
                reportValidationProblem(ErrorConsts.ERR_VLD_ANY, getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(String msg, Location loc, int severity) throws XMLStreamException {
        reportProblem(new XMLValidationProblem(loc, msg, severity));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(String msg, int severity) throws XMLStreamException {
        reportProblem(new XMLValidationProblem(getValidationLocation(), msg, severity));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(String msg) throws XMLStreamException {
        reportProblem(new XMLValidationProblem(getValidationLocation(), msg, 2));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(Location loc, String msg) throws XMLStreamException {
        reportProblem(new XMLValidationProblem(loc, msg));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(String format, Object arg) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg);
        reportProblem(new XMLValidationProblem(getValidationLocation(), msg));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(String format, Object arg, Object arg2) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg, arg2);
        reportProblem(new XMLValidationProblem(getValidationLocation(), msg));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doReportProblem(XMLReporter rep, String probType, String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = getLocation();
        }
        doReportProblem(rep, new XMLValidationProblem(loc, msg, 2, probType));
    }

    protected void doReportProblem(XMLReporter rep, XMLValidationProblem prob) throws XMLStreamException {
        if (rep != null) {
            Location loc = prob.getLocation();
            if (loc == null) {
                loc = getLocation();
                prob.setLocation(loc);
            }
            if (prob.getType() == null) {
                prob.setType(ErrorConsts.WT_VALIDATION);
            }
            rep.report(prob.getMessage(), prob.getType(), prob, loc);
        }
    }

    protected final char[] getCopyBuffer() {
        char[] buf = this.mCopyBuffer;
        if (buf == null) {
            char[] cArrAllocMediumCBuffer = this.mConfig.allocMediumCBuffer(512);
            buf = cArrAllocMediumCBuffer;
            this.mCopyBuffer = cArrAllocMediumCBuffer;
        }
        return buf;
    }

    protected final char[] getCopyBuffer(int minLen) {
        char[] buf = this.mCopyBuffer;
        if (buf == null || minLen > buf.length) {
            char[] cArrAllocMediumCBuffer = this.mConfig.allocMediumCBuffer(Math.max(512, minLen));
            buf = cArrAllocMediumCBuffer;
            this.mCopyBuffer = cArrAllocMediumCBuffer;
        }
        return buf;
    }

    public String toString() {
        return "[StreamWriter: " + getClass() + ", underlying outputter: " + (this.mWriter == null ? "NULL" : this.mWriter.toString() + "]");
    }
}
