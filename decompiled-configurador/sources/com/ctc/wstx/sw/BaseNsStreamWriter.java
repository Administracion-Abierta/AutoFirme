package com.ctc.wstx.sw;

import com.ctc.wstx.api.EmptyElementHandler;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.util.DefaultXmlSymbolTable;
import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/BaseNsStreamWriter.class */
public abstract class BaseNsStreamWriter extends TypedStreamWriter {
    protected static final String sPrefixXml = DefaultXmlSymbolTable.getXmlSymbol();
    protected static final String sPrefixXmlns = DefaultXmlSymbolTable.getXmlnsSymbol();
    protected static final String ERR_NSDECL_WRONG_STATE = "Trying to write a namespace declaration when there is no open start element.";
    protected final boolean mAutomaticNS;
    protected final EmptyElementHandler mEmptyElementHandler;
    protected SimpleOutputElement mCurrElem;
    protected NamespaceContext mRootNsContext;
    protected SimpleOutputElement mOutputElemPool;
    static final int MAX_POOL_SIZE = 8;
    protected int mPoolSize;

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public abstract void setDefaultNamespace(String str) throws XMLStreamException;

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public abstract void writeAttribute(String str, String str2, String str3) throws XMLStreamException;

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public abstract void writeAttribute(String str, String str2, String str3, String str4) throws XMLStreamException;

    public abstract void doSetPrefix(String str, String str2) throws XMLStreamException;

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public abstract void writeDefaultNamespace(String str) throws XMLStreamException;

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public abstract void writeNamespace(String str, String str2) throws XMLStreamException;

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public abstract void writeStartElement(StartElement startElement) throws XMLStreamException;

    protected abstract void writeStartOrEmpty(String str, String str2) throws XMLStreamException;

    protected abstract void writeStartOrEmpty(String str, String str2, String str3) throws XMLStreamException;

    public BaseNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg, boolean repairing) {
        super(xw, enc, cfg);
        this.mCurrElem = SimpleOutputElement.createRoot();
        this.mRootNsContext = null;
        this.mOutputElemPool = null;
        this.mPoolSize = 0;
        this.mAutomaticNS = repairing;
        this.mEmptyElementHandler = cfg.getEmptyElementHandler();
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public NamespaceContext getNamespaceContext() {
        return this.mCurrElem;
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public String getPrefix(String uri) {
        return this.mCurrElem.getPrefix(uri);
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void setNamespaceContext(NamespaceContext ctxt) throws XMLStreamException {
        if (this.mState != 1) {
            throwOutputError("Called setNamespaceContext() after having already output root element.");
        }
        this.mRootNsContext = ctxt;
        this.mCurrElem.setRootNsContext(ctxt);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new NullPointerException("Can not pass null 'prefix' value");
        }
        if (prefix.length() == 0) {
            setDefaultNamespace(uri);
            return;
        }
        if (uri == null) {
            throw new NullPointerException("Can not pass null 'uri' value");
        }
        if (prefix.equals(sPrefixXml)) {
            if (!uri.equals("http://www.w3.org/XML/1998/namespace")) {
                throwOutputError(ErrorConsts.ERR_NS_REDECL_XML, uri);
            }
        } else if (prefix.equals(sPrefixXmlns)) {
            if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
                throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS, uri);
            }
        } else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throwOutputError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix);
        } else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI, prefix);
        }
        if (!this.mXml11 && uri.length() == 0) {
            throwOutputError(ErrorConsts.ERR_NS_EMPTY);
        }
        doSetPrefix(prefix, uri);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        doWriteAttr(localName, null, null, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEmptyElement(String localName) throws XMLStreamException {
        checkStartElement(localName, null);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mEmptyElement = true;
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, localName);
            this.mPoolSize--;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(localName);
        }
        doWriteStartTag(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEmptyElement(String nsURI, String localName) throws XMLStreamException {
        writeStartOrEmpty(localName, nsURI);
        this.mEmptyElement = true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEmptyElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        writeStartOrEmpty(prefix, localName, nsURI);
        this.mEmptyElement = true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEndElement() throws XMLStreamException {
        doWriteEndTag(null, this.mCfgAutomaticEmptyElems);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(String localName) throws XMLStreamException {
        checkStartElement(localName, null);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mEmptyElement = false;
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, localName);
            this.mPoolSize--;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(localName);
        }
        doWriteStartTag(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(String nsURI, String localName) throws XMLStreamException {
        writeStartOrEmpty(localName, nsURI);
        this.mEmptyElement = false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        writeStartOrEmpty(prefix, localName, nsURI);
        this.mEmptyElement = false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.TypedStreamWriter
    protected void writeTypedAttribute(String prefix, String nsURI, String localName, AsciiValueEncoder enc) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            this.mCurrElem.checkAttrWrite(nsURI, localName);
        }
        try {
            if (this.mValidator == null) {
                if (prefix == null || prefix.length() == 0) {
                    this.mWriter.writeTypedAttribute(localName, enc);
                } else {
                    this.mWriter.writeTypedAttribute(prefix, localName, enc);
                }
            } else {
                this.mWriter.writeTypedAttribute(prefix, localName, nsURI, enc, this.mValidator, getCopyBuffer());
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter, org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeFullEndElement() throws XMLStreamException {
        doWriteEndTag(null, false);
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter, org.codehaus.stax2.validation.ValidationContext
    public QName getCurrentElementName() {
        return this.mCurrElem.getName();
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter, org.codehaus.stax2.validation.ValidationContext
    public String getNamespaceURI(String prefix) {
        return this.mCurrElem.getNamespaceURI(prefix);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEndElement(QName name) throws XMLStreamException {
        doWriteEndTag(this.mCheckStructure ? name : null, this.mCfgAutomaticEmptyElems);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    protected void closeStartElement(boolean emptyElem) throws XMLStreamException {
        this.mStartElementOpen = false;
        try {
            if (emptyElem) {
                this.mWriter.writeStartTagEmptyEnd();
            } else {
                this.mWriter.writeStartTagEnd();
            }
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementAndAttributes();
            }
            if (emptyElem) {
                SimpleOutputElement curr = this.mCurrElem;
                this.mCurrElem = curr.getParent();
                if (this.mCurrElem.isRoot()) {
                    this.mState = 3;
                }
                if (this.mValidator != null) {
                    this.mVldContent = this.mValidator.validateElementEnd(curr.getLocalName(), curr.getNamespaceURI(), curr.getPrefix());
                }
                if (this.mPoolSize < 8) {
                    curr.addToPool(this.mOutputElemPool);
                    this.mOutputElemPool = curr;
                    this.mPoolSize++;
                }
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    protected String getTopElementDesc() {
        return this.mCurrElem.getNameDesc();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void checkStartElement(String localName, String prefix) throws XMLStreamException {
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
            return;
        }
        if (this.mState == 1) {
            verifyRootElement(localName, prefix);
        } else if (this.mState == 3) {
            if (this.mCheckStructure) {
                String name = (prefix == null || prefix.length() == 0) ? localName : prefix + ":" + localName;
                reportNwfStructure(ErrorConsts.WERR_PROLOG_SECOND_ROOT, name);
            }
            this.mState = 2;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void doWriteAttr(String localName, String nsURI, String prefix, String value) throws XMLStreamException {
        if (this.mCheckAttrs) {
            this.mCurrElem.checkAttrWrite(nsURI, localName);
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, nsURI, prefix, value);
        }
        try {
            int vlen = value.length();
            if (vlen >= 12) {
                char[] buf = this.mCopyBuffer;
                if (buf == null) {
                    char[] cArrAllocMediumCBuffer = this.mConfig.allocMediumCBuffer(512);
                    buf = cArrAllocMediumCBuffer;
                    this.mCopyBuffer = cArrAllocMediumCBuffer;
                }
                if (vlen <= buf.length) {
                    value.getChars(0, vlen, buf, 0);
                    if (prefix != null && prefix.length() > 0) {
                        this.mWriter.writeAttribute(prefix, localName, buf, 0, vlen);
                        return;
                    } else {
                        this.mWriter.writeAttribute(localName, buf, 0, vlen);
                        return;
                    }
                }
            }
            if (prefix != null && prefix.length() > 0) {
                this.mWriter.writeAttribute(prefix, localName, value);
            } else {
                this.mWriter.writeAttribute(localName, value);
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:13:0x0045 A[Catch: IOException -> 0x0056, TryCatch #0 {IOException -> 0x0056, blocks: (B:10:0x002c, B:12:0x0033, B:13:0x0045), top: B:19:0x002c }] */
    protected final void doWriteAttr(String localName, String nsURI, String prefix, char[] buf, int start, int len) throws XMLStreamException {
        if (this.mCheckAttrs) {
            this.mCurrElem.checkAttrWrite(nsURI, localName);
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, nsURI, prefix, buf, start, len);
        }
        if (prefix == null) {
            this.mWriter.writeAttribute(localName, buf, start, len);
        } else {
            try {
                if (prefix.length() > 0) {
                    this.mWriter.writeAttribute(prefix, localName, buf, start, len);
                } else {
                    this.mWriter.writeAttribute(localName, buf, start, len);
                }
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doWriteNamespace(String prefix, String nsURI) throws XMLStreamException {
        try {
            int vlen = nsURI.length();
            if (vlen >= 12) {
                char[] buf = this.mCopyBuffer;
                if (buf == null) {
                    char[] cArrAllocMediumCBuffer = this.mConfig.allocMediumCBuffer(512);
                    buf = cArrAllocMediumCBuffer;
                    this.mCopyBuffer = cArrAllocMediumCBuffer;
                }
                if (vlen <= buf.length) {
                    nsURI.getChars(0, vlen, buf, 0);
                    this.mWriter.writeAttribute(CanonicalizerBase.XMLNS, prefix, buf, 0, vlen);
                    return;
                }
            }
            this.mWriter.writeAttribute(CanonicalizerBase.XMLNS, prefix, nsURI);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doWriteDefaultNs(String nsURI) throws XMLStreamException {
        int length;
        if (nsURI == null) {
            length = 0;
        } else {
            try {
                length = nsURI.length();
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
        int vlen = length;
        if (vlen >= 12) {
            char[] buf = this.mCopyBuffer;
            if (buf == null) {
                char[] cArrAllocMediumCBuffer = this.mConfig.allocMediumCBuffer(512);
                buf = cArrAllocMediumCBuffer;
                this.mCopyBuffer = cArrAllocMediumCBuffer;
            }
            if (vlen <= buf.length) {
                nsURI.getChars(0, vlen, buf, 0);
                this.mWriter.writeAttribute(CanonicalizerBase.XMLNS, buf, 0, vlen);
                return;
            }
        }
        this.mWriter.writeAttribute(CanonicalizerBase.XMLNS, nsURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void doWriteStartTag(String localName) throws XMLStreamException {
        this.mAnyOutput = true;
        this.mStartElementOpen = true;
        try {
            this.mWriter.writeStartTagStart(localName);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:8:0x0019  */
    protected final void doWriteStartTag(String prefix, String localName) throws XMLStreamException {
        boolean z;
        this.mAnyOutput = true;
        this.mStartElementOpen = true;
        if (prefix != null) {
            try {
                if (prefix.length() > 0) {
                    z = true;
                } else {
                    z = false;
                }
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        } else {
            z = false;
        }
        boolean hasPrefix = z;
        if (hasPrefix) {
            this.mWriter.writeStartTagStart(prefix, localName);
        } else {
            this.mWriter.writeStartTagStart(localName);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doWriteEndTag(QName expName, boolean allowEmpty) throws XMLStreamException {
        if (this.mStartElementOpen && this.mEmptyElement) {
            this.mEmptyElement = false;
            closeStartElement(true);
        }
        if (this.mState != 2) {
            reportNwfStructure("No open start element, when trying to write end element");
        }
        SimpleOutputElement thisElem = this.mCurrElem;
        String prefix = thisElem.getPrefix();
        String localName = thisElem.getLocalName();
        String nsURI = thisElem.getNamespaceURI();
        this.mCurrElem = thisElem.getParent();
        if (this.mPoolSize < 8) {
            thisElem.addToPool(this.mOutputElemPool);
            this.mOutputElemPool = thisElem;
            this.mPoolSize++;
        }
        if (this.mCheckStructure && expName != null && !localName.equals(expName.getLocalPart())) {
            reportNwfStructure("Mismatching close element local name, '" + localName + "'; expected '" + expName.getLocalPart() + "'.");
        }
        if (this.mStartElementOpen) {
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementAndAttributes();
            }
            this.mStartElementOpen = false;
            try {
                if (this.mEmptyElementHandler != null) {
                    allowEmpty = this.mEmptyElementHandler.allowEmptyElement(prefix, localName, nsURI, allowEmpty);
                }
                if (allowEmpty) {
                    this.mWriter.writeStartTagEmptyEnd();
                    if (this.mCurrElem.isRoot()) {
                        this.mState = 3;
                    }
                    if (this.mValidator != null) {
                        this.mVldContent = this.mValidator.validateElementEnd(localName, nsURI, prefix);
                        return;
                    }
                    return;
                }
                this.mWriter.writeStartTagEnd();
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
        try {
            this.mWriter.writeEndTag(prefix, localName);
            if (this.mCurrElem.isRoot()) {
                this.mState = 3;
            }
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementEnd(localName, nsURI, prefix);
            }
        } catch (IOException ioe2) {
            throw new WstxIOException(ioe2);
        }
    }
}
