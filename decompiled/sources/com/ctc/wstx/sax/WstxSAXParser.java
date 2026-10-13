package com.ctc.wstx.sax;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.BasicStreamReader;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.URLUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/WstxSAXParser.class */
public class WstxSAXParser extends SAXParser implements Parser, XMLReader, Attributes2, Locator2, DTDEventListener {
    static final boolean FEAT_DEFAULT_NS_PREFIXES = false;
    protected final WstxInputFactory mStaxFactory;
    protected final ReaderConfig mConfig;
    protected boolean mFeatNsPrefixes;
    protected BasicStreamReader mScanner;
    protected AttributeCollector mAttrCollector;
    protected InputElementStack mElemStack;
    protected String mEncoding;
    protected String mXmlVersion;
    protected boolean mStandalone;
    protected ContentHandler mContentHandler;
    protected DTDHandler mDTDHandler;
    protected EntityResolver mEntityResolver;
    protected ErrorHandler mErrorHandler;
    protected LexicalHandler mLexicalHandler;
    protected DeclHandler mDeclHandler;
    protected int mAttrCount;
    protected int mNsCount;

    public WstxSAXParser(WstxInputFactory sf, boolean nsPrefixes) {
        this.mNsCount = 0;
        this.mStaxFactory = sf;
        this.mFeatNsPrefixes = nsPrefixes;
        this.mConfig = sf.createPrivateConfig();
        this.mConfig.doSupportDTDs(true);
        ResolverProxy r = new ResolverProxy();
        this.mConfig.setDtdResolver(r);
        this.mConfig.setEntityResolver(r);
        this.mConfig.setDTDEventListener(this);
    }

    public WstxSAXParser() {
        this(new WstxInputFactory(), false);
    }

    @Override // javax.xml.parsers.SAXParser
    public final Parser getParser() {
        return this;
    }

    @Override // javax.xml.parsers.SAXParser
    public final XMLReader getXMLReader() {
        return this;
    }

    public final ReaderConfig getStaxConfig() {
        return this.mConfig;
    }

    @Override // javax.xml.parsers.SAXParser
    public boolean isNamespaceAware() {
        return this.mConfig.willSupportNamespaces();
    }

    @Override // javax.xml.parsers.SAXParser
    public boolean isValidating() {
        return this.mConfig.willValidateWithDTD();
    }

    @Override // javax.xml.parsers.SAXParser, org.xml.sax.XMLReader
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        SAXProperty prop = SAXProperty.findByUri(name);
        if (prop == SAXProperty.DECLARATION_HANDLER) {
            return this.mDeclHandler;
        }
        if (prop == SAXProperty.DOCUMENT_XML_VERSION) {
            return this.mXmlVersion;
        }
        if (prop == SAXProperty.DOM_NODE) {
            return null;
        }
        if (prop == SAXProperty.LEXICAL_HANDLER) {
            return this.mLexicalHandler;
        }
        if (prop == SAXProperty.XML_STRING) {
            return null;
        }
        throw new SAXNotRecognizedException("Property '" + name + "' not recognized");
    }

    @Override // javax.xml.parsers.SAXParser, org.xml.sax.XMLReader
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        SAXProperty prop = SAXProperty.findByUri(name);
        if (prop == SAXProperty.DECLARATION_HANDLER) {
            this.mDeclHandler = (DeclHandler) value;
            return;
        }
        if (prop != SAXProperty.DOCUMENT_XML_VERSION && prop != SAXProperty.DOM_NODE) {
            if (prop == SAXProperty.LEXICAL_HANDLER) {
                this.mLexicalHandler = (LexicalHandler) value;
                return;
            } else if (prop != SAXProperty.XML_STRING) {
                throw new SAXNotRecognizedException("Property '" + name + "' not recognized");
            }
        }
        throw new SAXNotSupportedException("Property '" + name + "' is read-only, can not be modified");
    }

    @Override // javax.xml.parsers.SAXParser
    public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException {
        if (hb != null) {
            if (this.mContentHandler == null) {
                setDocumentHandler(hb);
            }
            if (this.mEntityResolver == null) {
                setEntityResolver(hb);
            }
            if (this.mErrorHandler == null) {
                setErrorHandler(hb);
            }
            if (this.mDTDHandler == null) {
                setDTDHandler(hb);
            }
        }
        parse(is);
    }

    @Override // javax.xml.parsers.SAXParser
    public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
        if (dh != null) {
            if (this.mContentHandler == null) {
                setContentHandler(dh);
            }
            if (this.mEntityResolver == null) {
                setEntityResolver(dh);
            }
            if (this.mErrorHandler == null) {
                setErrorHandler(dh);
            }
            if (this.mDTDHandler == null) {
                setDTDHandler(dh);
            }
        }
        parse(is);
    }

    @Override // org.xml.sax.XMLReader
    public ContentHandler getContentHandler() {
        return this.mContentHandler;
    }

    @Override // org.xml.sax.XMLReader
    public DTDHandler getDTDHandler() {
        return this.mDTDHandler;
    }

    @Override // org.xml.sax.XMLReader
    public EntityResolver getEntityResolver() {
        return this.mEntityResolver;
    }

    @Override // org.xml.sax.XMLReader
    public ErrorHandler getErrorHandler() {
        return this.mErrorHandler;
    }

    @Override // org.xml.sax.XMLReader
    public boolean getFeature(String name) throws SAXNotRecognizedException {
        SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            return this.mConfig.willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            return this.mConfig.willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.IS_STANDALONE) {
            return this.mStandalone;
        }
        if (stdFeat == SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
            return false;
        }
        if (stdFeat == SAXFeature.NAMESPACES) {
            return this.mConfig.willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
            return !this.mConfig.willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.RESOLVE_DTD_URIS) {
            return false;
        }
        if (stdFeat == SAXFeature.STRING_INTERNING) {
            return true;
        }
        if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
            return false;
        }
        if (stdFeat == SAXFeature.USE_ATTRIBUTES2 || stdFeat == SAXFeature.USE_LOCATOR2 || stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
            return true;
        }
        if (stdFeat == SAXFeature.VALIDATION) {
            return this.mConfig.willValidateWithDTD();
        }
        if (stdFeat == SAXFeature.XMLNS_URIS || stdFeat == SAXFeature.XML_1_1) {
            return true;
        }
        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
    }

    @Override // org.xml.sax.XMLReader
    public void setContentHandler(ContentHandler handler) {
        this.mContentHandler = handler;
    }

    @Override // org.xml.sax.Parser, org.xml.sax.XMLReader
    public void setDTDHandler(DTDHandler handler) {
        this.mDTDHandler = handler;
    }

    @Override // org.xml.sax.Parser, org.xml.sax.XMLReader
    public void setEntityResolver(EntityResolver resolver) {
        this.mEntityResolver = resolver;
    }

    @Override // org.xml.sax.Parser, org.xml.sax.XMLReader
    public void setErrorHandler(ErrorHandler handler) {
        this.mErrorHandler = handler;
    }

    @Override // org.xml.sax.XMLReader
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        boolean invalidValue = false;
        boolean readOnly = false;
        SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            this.mConfig.doSupportExternalEntities(value);
        } else if (stdFeat != SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            if (stdFeat == SAXFeature.IS_STANDALONE) {
                readOnly = true;
            } else if (stdFeat != SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
                if (stdFeat == SAXFeature.NAMESPACES) {
                    this.mConfig.doSupportNamespaces(value);
                } else if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
                    this.mFeatNsPrefixes = value;
                } else if (stdFeat != SAXFeature.RESOLVE_DTD_URIS) {
                    if (stdFeat == SAXFeature.STRING_INTERNING) {
                        invalidValue = !value;
                    } else if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
                        invalidValue = value;
                    } else if (stdFeat == SAXFeature.USE_ATTRIBUTES2 || stdFeat == SAXFeature.USE_LOCATOR2 || stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
                        readOnly = true;
                    } else if (stdFeat == SAXFeature.VALIDATION) {
                        this.mConfig.doValidateWithDTD(value);
                    } else if (stdFeat == SAXFeature.XMLNS_URIS) {
                        invalidValue = !value;
                    } else if (stdFeat == SAXFeature.XML_1_1) {
                        readOnly = true;
                    } else {
                        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
                    }
                }
            }
        }
        if (readOnly) {
            throw new SAXNotSupportedException("Feature '" + name + "' is read-only, can not be modified");
        }
        if (invalidValue) {
            throw new SAXNotSupportedException("Trying to set invalid value for feature '" + name + "', '" + value + OperatorName.SHOW_TEXT_LINE);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.xml.sax.Parser, org.xml.sax.XMLReader
    public void parse(InputSource input) throws SAXException {
        this.mScanner = null;
        String sysIdStr = input.getSystemId();
        ReaderConfig cfg = this.mConfig;
        URL srcUrl = null;
        InputStream is = null;
        Reader r = input.getCharacterStream();
        if (r == null) {
            is = input.getByteStream();
            if (is == null) {
                if (sysIdStr == null) {
                    throw new SAXException("Invalid InputSource passed: neither character or byte stream passed, nor system id specified");
                }
                try {
                    srcUrl = URLUtil.urlFromSystemId(sysIdStr);
                    is = URLUtil.inputStreamFromURL(srcUrl);
                } catch (IOException ioe) {
                    SAXException saxe = new SAXException(ioe);
                    ExceptionUtil.setInitCause(saxe, ioe);
                    throw saxe;
                }
            }
        }
        if (this.mContentHandler != null) {
            this.mContentHandler.setDocumentLocator(this);
            this.mContentHandler.startDocument();
        }
        cfg.resetState();
        try {
            String inputEnc = input.getEncoding();
            String publicId = input.getPublicId();
            if (r == null && inputEnc != null && inputEnc.length() > 0) {
                r = DefaultInputResolver.constructOptimizedReader(cfg, is, false, inputEnc);
            }
            SystemId systemId = SystemId.construct(sysIdStr, srcUrl);
            if (r != null) {
                InputBootstrapper bs = ReaderBootstrapper.getInstance(publicId, systemId, r, inputEnc);
                this.mScanner = (BasicStreamReader) this.mStaxFactory.createSR(cfg, systemId, bs, false, false);
            } else {
                InputBootstrapper bs2 = StreamBootstrapper.getInstance(publicId, systemId, is);
                this.mScanner = (BasicStreamReader) this.mStaxFactory.createSR(cfg, systemId, bs2, false, false);
            }
            String enc2 = this.mScanner.getEncoding();
            if (enc2 == null) {
                enc2 = this.mScanner.getCharacterEncodingScheme();
            }
            this.mEncoding = enc2;
            this.mXmlVersion = this.mScanner.getVersion();
            this.mStandalone = this.mScanner.standaloneSet();
            this.mAttrCollector = this.mScanner.getAttributeCollector();
            this.mElemStack = this.mScanner.getInputElementStack();
            fireEvents();
        } catch (XMLStreamException strex) {
            throwSaxException((Exception) strex);
        } catch (IOException io) {
            throwSaxException(io);
        } finally {
            if (this.mContentHandler != null) {
                this.mContentHandler.endDocument();
            }
            if (this.mScanner != null) {
                BasicStreamReader sr = this.mScanner;
                this.mScanner = null;
                try {
                    sr.close();
                } catch (XMLStreamException e) {
                }
            }
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e2) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        }
    }

    @Override // org.xml.sax.Parser, org.xml.sax.XMLReader
    public void parse(String systemId) throws SAXException {
        InputSource src = new InputSource(systemId);
        parse(src);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final void fireEvents() throws XMLStreamException, SAXException, IOException {
        this.mConfig.doParseLazily(false);
        while (true) {
            int type = this.mScanner.next();
            if (type == 1) {
                break;
            } else {
                fireAuxEvent(type, false);
            }
        }
        fireStartTag();
        int depth = 1;
        while (true) {
            int type2 = this.mScanner.next();
            if (type2 == 1) {
                fireStartTag();
                depth++;
            } else if (type2 == 2) {
                this.mScanner.fireSaxEndElement(this.mContentHandler);
                depth--;
                if (depth < 1) {
                    break;
                }
            } else if (type2 == 4) {
                this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
            } else {
                fireAuxEvent(type2, true);
            }
        }
        while (true) {
            int type3 = this.mScanner.next();
            if (type3 != 8) {
                if (type3 != 6) {
                    fireAuxEvent(type3, false);
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final void fireAuxEvent(int type, boolean inTree) throws XMLStreamException, SAXException, IOException {
        switch (type) {
            case 3:
                this.mScanner.fireSaxPIEvent(this.mContentHandler);
                return;
            case 4:
            case 7:
            case 8:
            case 10:
            default:
                if (type == 8) {
                    throwSaxException("Unexpected end-of-input in " + (inTree ? "tree" : "prolog"));
                }
                throw new RuntimeException("Internal error: unexpected type, " + type);
            case 5:
                this.mScanner.fireSaxCommentEvent(this.mLexicalHandler);
                return;
            case 6:
                if (inTree) {
                    this.mScanner.fireSaxSpaceEvents(this.mContentHandler);
                    return;
                }
                return;
            case 9:
                if (this.mContentHandler != null) {
                    this.mContentHandler.skippedEntity(this.mScanner.getLocalName());
                    return;
                }
                return;
            case 11:
                if (this.mLexicalHandler != null) {
                    String rootName = this.mScanner.getDTDRootName();
                    String sysId = this.mScanner.getDTDSystemId();
                    String pubId = this.mScanner.getDTDPublicId();
                    this.mLexicalHandler.startDTD(rootName, pubId, sysId);
                    try {
                        this.mScanner.getDTDInfo();
                        this.mLexicalHandler.endDTD();
                        return;
                    } catch (WrappedSaxException wse) {
                        throw wse.getSaxException();
                    }
                }
                return;
            case 12:
                if (this.mLexicalHandler != null) {
                    this.mLexicalHandler.startCDATA();
                    this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                    this.mLexicalHandler.endCDATA();
                    return;
                }
                this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                return;
        }
    }

    private final void fireStartTag() throws SAXException {
        this.mAttrCount = this.mAttrCollector.getCount();
        if (this.mFeatNsPrefixes) {
            this.mNsCount = this.mElemStack.getCurrentNsCount();
        }
        this.mScanner.fireSaxStartElement(this.mContentHandler, this);
    }

    @Override // org.xml.sax.Parser
    public void setDocumentHandler(DocumentHandler handler) {
        setContentHandler(new DocHandlerWrapper(handler));
    }

    @Override // org.xml.sax.Parser
    public void setLocale(Locale locale) {
    }

    @Override // org.xml.sax.Attributes
    public int getIndex(String qName) {
        if (this.mElemStack == null) {
            return -1;
        }
        int ix = this.mElemStack.findAttributeIndex(null, qName);
        return ix;
    }

    @Override // org.xml.sax.Attributes
    public int getIndex(String uri, String localName) {
        if (this.mElemStack == null) {
            return -1;
        }
        int ix = this.mElemStack.findAttributeIndex(uri, localName);
        return ix;
    }

    @Override // org.xml.sax.Attributes
    public int getLength() {
        return this.mAttrCount + this.mNsCount;
    }

    @Override // org.xml.sax.Attributes
    public String getLocalName(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            return this.mAttrCollector.getLocalName(index);
        }
        int index2 = index - this.mAttrCount;
        if (index2 < this.mNsCount) {
            String prefix = this.mElemStack.getLocalNsPrefix(index2);
            return (prefix == null || prefix.length() == 0) ? CanonicalizerBase.XMLNS : prefix;
        }
        return null;
    }

    @Override // org.xml.sax.Attributes
    public String getQName(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String prefix = this.mAttrCollector.getPrefix(index);
            String ln = this.mAttrCollector.getLocalName(index);
            return (prefix == null || prefix.length() == 0) ? ln : prefix + ":" + ln;
        }
        int index2 = index - this.mAttrCount;
        if (index2 < this.mNsCount) {
            String prefix2 = this.mElemStack.getLocalNsPrefix(index2);
            if (prefix2 == null || prefix2.length() == 0) {
                return CanonicalizerBase.XMLNS;
            }
            return "xmlns:" + prefix2;
        }
        return null;
    }

    @Override // org.xml.sax.Attributes
    public String getType(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String type = this.mElemStack.getAttributeType(index);
            if (type == "ENUMERATED") {
                type = DTDParser.TYPE_NMTOKEN;
            }
            return type;
        }
        if (index - this.mAttrCount < this.mNsCount) {
            return "CDATA";
        }
        return null;
    }

    @Override // org.xml.sax.Attributes
    public String getType(String qName) {
        return getType(getIndex(qName));
    }

    @Override // org.xml.sax.Attributes
    public String getType(String uri, String localName) {
        return getType(getIndex(uri, localName));
    }

    @Override // org.xml.sax.Attributes
    public String getURI(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String uri = this.mAttrCollector.getURI(index);
            return uri == null ? "" : uri;
        }
        if (index - this.mAttrCount < this.mNsCount) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return null;
    }

    @Override // org.xml.sax.Attributes
    public String getValue(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            return this.mAttrCollector.getValue(index);
        }
        int index2 = index - this.mAttrCount;
        if (index2 < this.mNsCount) {
            String uri = this.mElemStack.getLocalNsURI(index2);
            return uri == null ? "" : uri;
        }
        return null;
    }

    @Override // org.xml.sax.Attributes
    public String getValue(String qName) {
        return getValue(getIndex(qName));
    }

    @Override // org.xml.sax.Attributes
    public String getValue(String uri, String localName) {
        return getValue(getIndex(uri, localName));
    }

    @Override // org.xml.sax.ext.Attributes2
    public boolean isDeclared(int index) {
        if (index < this.mAttrCount) {
            if (index >= 0) {
                return true;
            }
        } else {
            index -= this.mAttrCount;
            if (index < this.mNsCount) {
                return true;
            }
        }
        throwNoSuchAttribute(index);
        return false;
    }

    @Override // org.xml.sax.ext.Attributes2
    public boolean isDeclared(String qName) {
        return false;
    }

    @Override // org.xml.sax.ext.Attributes2
    public boolean isDeclared(String uri, String localName) {
        return false;
    }

    @Override // org.xml.sax.ext.Attributes2
    public boolean isSpecified(int index) {
        if (index < this.mAttrCount) {
            if (index >= 0) {
                return this.mAttrCollector.isSpecified(index);
            }
        } else {
            index -= this.mAttrCount;
            if (index < this.mNsCount) {
                return true;
            }
        }
        throwNoSuchAttribute(index);
        return false;
    }

    @Override // org.xml.sax.ext.Attributes2
    public boolean isSpecified(String qName) {
        int ix = getIndex(qName);
        if (ix < 0) {
            throw new IllegalArgumentException("No attribute with qName '" + qName + OperatorName.SHOW_TEXT_LINE);
        }
        return isSpecified(ix);
    }

    @Override // org.xml.sax.ext.Attributes2
    public boolean isSpecified(String uri, String localName) {
        int ix = getIndex(uri, localName);
        if (ix < 0) {
            throw new IllegalArgumentException("No attribute with uri " + uri + ", local name '" + localName + OperatorName.SHOW_TEXT_LINE);
        }
        return isSpecified(ix);
    }

    @Override // org.xml.sax.Locator
    public int getColumnNumber() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getColumnNumber();
        }
        return -1;
    }

    @Override // org.xml.sax.Locator
    public int getLineNumber() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getLineNumber();
        }
        return -1;
    }

    @Override // org.xml.sax.Locator
    public String getPublicId() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getPublicId();
        }
        return null;
    }

    @Override // org.xml.sax.Locator
    public String getSystemId() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getSystemId();
        }
        return null;
    }

    @Override // org.xml.sax.ext.Locator2
    public String getEncoding() {
        return this.mEncoding;
    }

    @Override // org.xml.sax.ext.Locator2
    public String getXMLVersion() {
        return this.mXmlVersion;
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public boolean dtdReportComments() {
        return this.mLexicalHandler != null;
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdComment(char[] data, int offset, int len) {
        if (this.mLexicalHandler != null) {
            try {
                this.mLexicalHandler.comment(data, offset, len);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdProcessingInstruction(String target, String data) {
        if (this.mContentHandler != null) {
            try {
                this.mContentHandler.processingInstruction(target, data);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdSkippedEntity(String name) {
        if (this.mContentHandler != null) {
            try {
                this.mContentHandler.skippedEntity(name);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdNotationDecl(String name, String publicId, String systemId, URL baseURL) throws XMLStreamException {
        if (this.mDTDHandler != null) {
            if (systemId != null && systemId.indexOf(58) < 0) {
                try {
                    systemId = URLUtil.urlFromSystemId(systemId, baseURL).toExternalForm();
                } catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            try {
                this.mDTDHandler.notationDecl(name, publicId, systemId);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdUnparsedEntityDecl(String name, String publicId, String systemId, String notationName, URL baseURL) throws XMLStreamException {
        if (this.mDTDHandler != null) {
            if (systemId.indexOf(58) < 0) {
                try {
                    systemId = URLUtil.urlFromSystemId(systemId, baseURL).toExternalForm();
                } catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            try {
                this.mDTDHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void attributeDecl(String eName, String aName, String type, String mode, String value) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.attributeDecl(eName, aName, type, mode, value);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdElementDecl(String name, String model) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.elementDecl(name, model);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdExternalEntityDecl(String name, String publicId, String systemId) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.externalEntityDecl(name, publicId, systemId);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override // com.ctc.wstx.dtd.DTDEventListener
    public void dtdInternalEntityDecl(String name, String value) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.internalEntityDecl(name, value);
            } catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    private void throwSaxException(Exception src) throws SAXException {
        SAXParseException se = new SAXParseException(src.getMessage(), this, src);
        ExceptionUtil.setInitCause(se, src);
        if (this.mErrorHandler != null) {
            this.mErrorHandler.fatalError(se);
        }
        throw se;
    }

    private void throwSaxException(String msg) throws SAXException {
        SAXParseException se = new SAXParseException(msg, this);
        if (this.mErrorHandler != null) {
            this.mErrorHandler.fatalError(se);
        }
        throw se;
    }

    private void throwNoSuchAttribute(int index) {
        throw new IllegalArgumentException("No attribute with index " + index + " (have " + (this.mAttrCount + this.mNsCount) + " attributes)");
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/WstxSAXParser$ResolverProxy.class */
    final class ResolverProxy implements XMLResolver {
        public ResolverProxy() {
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
            if (WstxSAXParser.this.mEntityResolver != null) {
                try {
                    URL url = new URL(baseURI);
                    String ref = new URL(url, systemID).toExternalForm();
                    InputSource isrc = WstxSAXParser.this.mEntityResolver.resolveEntity(publicID, ref);
                    if (isrc != null) {
                        InputStream in = isrc.getByteStream();
                        if (in != null) {
                            return in;
                        }
                        Reader r = isrc.getCharacterStream();
                        if (r != null) {
                            return r;
                        }
                        return null;
                    }
                    return null;
                } catch (IOException ex) {
                    throw new WstxIOException(ex);
                } catch (Exception ex2) {
                    throw new XMLStreamException(ex2.getMessage(), ex2);
                }
            }
            return null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/WstxSAXParser$DocHandlerWrapper.class */
    static final class DocHandlerWrapper implements ContentHandler {
        final DocumentHandler mDocHandler;
        final AttributesWrapper mAttrWrapper = new AttributesWrapper();

        DocHandlerWrapper(DocumentHandler h) {
            this.mDocHandler = h;
        }

        @Override // org.xml.sax.ContentHandler
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.mDocHandler.characters(ch, start, length);
        }

        @Override // org.xml.sax.ContentHandler
        public void endDocument() throws SAXException {
            this.mDocHandler.endDocument();
        }

        @Override // org.xml.sax.ContentHandler
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName == null) {
                qName = localName;
            }
            this.mDocHandler.endElement(qName);
        }

        @Override // org.xml.sax.ContentHandler
        public void endPrefixMapping(String prefix) {
        }

        @Override // org.xml.sax.ContentHandler
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            this.mDocHandler.ignorableWhitespace(ch, start, length);
        }

        @Override // org.xml.sax.ContentHandler
        public void processingInstruction(String target, String data) throws SAXException {
            this.mDocHandler.processingInstruction(target, data);
        }

        @Override // org.xml.sax.ContentHandler
        public void setDocumentLocator(Locator locator) {
            this.mDocHandler.setDocumentLocator(locator);
        }

        @Override // org.xml.sax.ContentHandler
        public void skippedEntity(String name) {
        }

        @Override // org.xml.sax.ContentHandler
        public void startDocument() throws SAXException {
            this.mDocHandler.startDocument();
        }

        @Override // org.xml.sax.ContentHandler
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            if (qName == null) {
                qName = localName;
            }
            this.mAttrWrapper.setAttributes(attrs);
            this.mDocHandler.startElement(qName, this.mAttrWrapper);
        }

        @Override // org.xml.sax.ContentHandler
        public void startPrefixMapping(String prefix, String uri) {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/WstxSAXParser$AttributesWrapper.class */
    static final class AttributesWrapper implements AttributeList {
        Attributes mAttrs;

        public void setAttributes(Attributes a) {
            this.mAttrs = a;
        }

        @Override // org.xml.sax.AttributeList
        public int getLength() {
            return this.mAttrs.getLength();
        }

        @Override // org.xml.sax.AttributeList
        public String getName(int i) {
            String n = this.mAttrs.getQName(i);
            return n == null ? this.mAttrs.getLocalName(i) : n;
        }

        @Override // org.xml.sax.AttributeList
        public String getType(int i) {
            return this.mAttrs.getType(i);
        }

        @Override // org.xml.sax.AttributeList
        public String getType(String name) {
            return this.mAttrs.getType(name);
        }

        @Override // org.xml.sax.AttributeList
        public String getValue(int i) {
            return this.mAttrs.getValue(i);
        }

        @Override // org.xml.sax.AttributeList
        public String getValue(String name) {
            return this.mAttrs.getValue(name);
        }
    }
}
