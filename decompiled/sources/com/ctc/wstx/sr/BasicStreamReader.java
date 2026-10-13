package com.ctc.wstx.sr;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.cfg.XmlConsts;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.TextBuffer;
import com.ctc.wstx.util.TextBuilder;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/BasicStreamReader.class */
public abstract class BasicStreamReader extends StreamScanner implements StreamReaderImpl, DTDInfo, LocationInfo {
    static final int DOC_STANDALONE_UNKNOWN = 0;
    static final int DOC_STANDALONE_YES = 1;
    static final int DOC_STANDALONE_NO = 2;
    static final int STATE_PROLOG = 0;
    static final int STATE_TREE = 1;
    static final int STATE_EPILOG = 2;
    static final int STATE_MULTIDOC_HACK = 3;
    static final int STATE_CLOSED = 4;
    static final int TOKEN_NOT_STARTED = 0;
    static final int TOKEN_STARTED = 1;
    static final int TOKEN_PARTIAL_SINGLE = 2;
    static final int TOKEN_FULL_SINGLE = 3;
    static final int TOKEN_FULL_COALESCED = 4;
    protected static final int MASK_GET_TEXT = 6768;
    protected static final int MASK_GET_TEXT_XXX = 4208;
    protected static final int MASK_GET_TEXT_WITH_WRITER = 6776;
    protected static final int MASK_GET_ELEMENT_TEXT = 4688;
    static final int ALL_WS_UNKNOWN = 0;
    static final int ALL_WS_YES = 1;
    static final int ALL_WS_NO = 2;
    private static final int INDENT_CHECK_START = 16;
    private static final int INDENT_CHECK_MAX = 40;
    protected static final String sPrefixXml = DefaultXmlSymbolTable.getXmlSymbol();
    protected static final String sPrefixXmlns = DefaultXmlSymbolTable.getXmlnsSymbol();
    protected final int mConfigFlags;
    protected final boolean mCfgCoalesceText;
    protected final boolean mCfgReportTextAsChars;
    protected final boolean mCfgLazyParsing;
    protected final int mShortestTextSegment;
    protected final ReaderCreator mOwner;
    protected int mDocStandalone;
    protected String mRootPrefix;
    protected String mRootLName;
    protected String mDtdPublicId;
    protected String mDtdSystemId;
    protected final TextBuffer mTextBuffer;
    protected final InputElementStack mElementStack;
    protected final AttributeCollector mAttrCollector;
    protected boolean mStDoctypeFound;
    protected int mTokenState;
    protected final int mStTextThreshold;
    protected int mCurrTextLength;
    protected boolean mStEmptyElem;
    protected int mParseState;
    protected int mCurrToken;
    protected int mSecondaryToken;
    protected int mWsStatus;
    protected boolean mValidateText;
    protected int mCheckIndentation;
    protected XMLStreamException mPendingException;
    protected Map<String, EntityDecl> mGeneralEntities;
    protected int mVldContent;
    protected boolean mReturnNullForDefaultNamespace;

    protected BasicStreamReader(InputBootstrapper bs, BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputElementStack elemStack, boolean forER) throws XMLStreamException {
        super(input, cfg, cfg.getEntityResolver());
        this.mDocStandalone = 0;
        this.mStDoctypeFound = false;
        this.mTokenState = 4;
        this.mStEmptyElem = false;
        this.mCurrToken = 7;
        this.mSecondaryToken = 7;
        this.mValidateText = false;
        this.mPendingException = null;
        this.mGeneralEntities = null;
        this.mVldContent = 4;
        this.mOwner = owner;
        this.mTextBuffer = TextBuffer.createRecyclableBuffer(cfg);
        this.mConfigFlags = cfg.getConfigFlags();
        this.mCfgCoalesceText = (this.mConfigFlags & 2) != 0;
        this.mCfgReportTextAsChars = (this.mConfigFlags & 512) == 0;
        this.mXml11 = cfg.isXml11();
        this.mCheckIndentation = this.mNormalizeLFs ? 16 : 0;
        this.mCfgLazyParsing = (forER || (this.mConfigFlags & 262144) == 0) ? false : true;
        if (this.mCfgCoalesceText) {
            this.mStTextThreshold = 4;
            this.mShortestTextSegment = WinNT.MAXLONG;
        } else {
            this.mStTextThreshold = 2;
            if (forER && !cfg.isCoalescingExplicitlyDisabled()) {
                this.mShortestTextSegment = WinNT.MAXLONG;
            } else {
                this.mShortestTextSegment = cfg.getShortestReportedTextSegment();
            }
        }
        this.mDocXmlVersion = bs.getDeclaredVersion();
        this.mDocInputEncoding = bs.getInputEncoding();
        this.mDocXmlEncoding = bs.getDeclaredEncoding();
        String sa = bs.getStandalone();
        if (sa == null) {
            this.mDocStandalone = 0;
        } else if (XmlConsts.XML_SA_YES.equals(sa)) {
            this.mDocStandalone = 1;
        } else {
            this.mDocStandalone = 2;
        }
        this.mParseState = this.mConfig.inputParsingModeFragment() ? 1 : 0;
        this.mElementStack = elemStack;
        this.mAttrCollector = elemStack.getAttrCollector();
        input.initInputLocation(this, this.mCurrDepth, 0);
        elemStack.connectReporter(this);
        this.mReturnNullForDefaultNamespace = this.mConfig.returnNullForDefaultNamespace();
    }

    protected static InputElementStack createElementStack(ReaderConfig cfg) {
        return new InputElementStack(cfg, cfg.willSupportNamespaces());
    }

    public String getCharacterEncodingScheme() {
        return this.mDocXmlEncoding;
    }

    public String getEncoding() {
        return this.mDocInputEncoding;
    }

    public String getVersion() {
        if (this.mDocXmlVersion == 256) {
            return "1.0";
        }
        if (this.mDocXmlVersion == 272) {
            return XmlConsts.XML_V_11_STR;
        }
        return null;
    }

    public boolean isStandalone() {
        return this.mDocStandalone == 1;
    }

    public boolean standaloneSet() {
        return this.mDocStandalone != 0;
    }

    public Object getProperty(String name) {
        if (WstxInputProperties.P_BASE_URL.equals(name)) {
            try {
                return this.mInput.getSource();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return this.mConfig.safeGetProperty(name);
    }

    public int getAttributeCount() {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getCount();
    }

    public String getAttributeLocalName(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getLocalName(index);
    }

    public QName getAttributeName(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getQName(index);
    }

    public String getAttributeNamespace(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        String uri = this.mAttrCollector.getURI(index);
        return uri == null ? "" : uri;
    }

    public String getAttributePrefix(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        String p = this.mAttrCollector.getPrefix(index);
        return p == null ? "" : p;
    }

    public String getAttributeType(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getValue(index);
    }

    public String getAttributeValue(String nsURI, String localName) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        if (nsURI == null) {
            return this.mAttrCollector.getValueByLocalName(localName);
        }
        return this.mAttrCollector.getValue(nsURI, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public String getElementText() throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throwParseError(ErrorConsts.ERR_STATE_NOT_STELEM, null, null);
        }
        if (this.mStEmptyElem) {
            this.mStEmptyElem = false;
            this.mCurrToken = 2;
            return "";
        }
        while (true) {
            int type = next();
            if (type == 2) {
                return "";
            }
            if (type != 5 && type != 3) {
                if (((1 << type) & MASK_GET_ELEMENT_TEXT) == 0) {
                    throw _constructUnexpectedInTyped(type);
                }
                if (this.mTokenState < 4) {
                    readCoalescedText(this.mCurrToken, false);
                }
                if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
                    this.mInputPtr += 2;
                    this.mCurrToken = 2;
                    String result = this.mTextBuffer.contentsAsString();
                    readEndElem();
                    return result;
                }
                int extra = 1 + (this.mTextBuffer.size() >> 1);
                StringBuilder sb = this.mTextBuffer.contentsAsStringBuilder(extra);
                while (true) {
                    int type2 = next();
                    if (type2 != 2) {
                        if (((1 << type2) & MASK_GET_ELEMENT_TEXT) != 0) {
                            if (this.mTokenState < this.mStTextThreshold) {
                                finishToken(false);
                            }
                            verifyLimit("Text size", this.mConfig.getMaxTextLength(), sb.length());
                            this.mTextBuffer.contentsToStringBuilder(sb);
                        } else if (type2 != 5 && type2 != 3) {
                            throw _constructUnexpectedInTyped(type2);
                        }
                    } else {
                        return sb.toString();
                    }
                }
            }
        }
    }

    public int getEventType() {
        if (this.mCurrToken == 12 && (this.mCfgCoalesceText || this.mCfgReportTextAsChars)) {
            return 4;
        }
        return this.mCurrToken;
    }

    public String getLocalName() {
        if (this.mCurrToken == 1 || this.mCurrToken == 2) {
            return this.mElementStack.getLocalName();
        }
        if (this.mCurrToken == 9) {
            return this.mCurrEntity == null ? this.mCurrName : this.mCurrEntity.getName();
        }
        throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE");
    }

    public QName getName() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        return this.mElementStack.getCurrentElementName();
    }

    public NamespaceContext getNamespaceContext() {
        return this.mElementStack;
    }

    public int getNamespaceCount() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        return this.mElementStack.getCurrentNsCount();
    }

    public String getNamespacePrefix(int index) {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String p = this.mElementStack.getLocalNsPrefix(index);
        if (p == null) {
            if (this.mReturnNullForDefaultNamespace) {
                return null;
            }
            return "";
        }
        return p;
    }

    public String getNamespaceURI() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String uri = this.mElementStack.getNsURI();
        return uri == null ? "" : uri;
    }

    public String getNamespaceURI(int index) {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String uri = this.mElementStack.getLocalNsURI(index);
        return uri == null ? "" : uri;
    }

    public String getNamespaceURI(String prefix) {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        return this.mElementStack.getNamespaceURI(prefix);
    }

    public String getPIData() {
        if (this.mCurrToken != 3) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_PI);
        }
        if (this.mTokenState <= 1) {
            safeFinishToken();
        }
        return this.mTextBuffer.contentsAsString();
    }

    public String getPITarget() {
        if (this.mCurrToken != 3) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_PI);
        }
        return this.mCurrName;
    }

    public String getPrefix() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String p = this.mElementStack.getPrefix();
        return p == null ? "" : p;
    }

    public String getText() {
        int currToken = this.mCurrToken;
        if (((1 << currToken) & MASK_GET_TEXT) == 0) {
            throwNotTextual(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
        if (currToken == 9) {
            if (this.mCurrEntity == null) {
                return null;
            }
            return this.mCurrEntity.getReplacementText();
        }
        if (currToken == 11) {
            return getDTDInternalSubset();
        }
        return this.mTextBuffer.contentsAsString();
    }

    public char[] getTextCharacters() {
        int currToken = this.mCurrToken;
        if (((1 << currToken) & 4208) == 0) {
            throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
        if (currToken == 9) {
            return this.mCurrEntity.getReplacementChars();
        }
        if (currToken == 11) {
            return getDTDInternalSubsetArray();
        }
        return this.mTextBuffer.getTextBuffer();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int len) {
        int currToken = this.mCurrToken;
        if (((1 << currToken) & 4208) == 0) {
            throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
        return this.mTextBuffer.contentsToArray(sourceStart, target, targetStart, len);
    }

    public int getTextLength() {
        int currToken = this.mCurrToken;
        if (((1 << currToken) & 4208) == 0) {
            throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
        return this.mTextBuffer.size();
    }

    public int getTextStart() {
        int currToken = this.mCurrToken;
        if (((1 << currToken) & 4208) == 0) {
            throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
        return this.mTextBuffer.getTextStart();
    }

    public boolean hasName() {
        return this.mCurrToken == 1 || this.mCurrToken == 2;
    }

    public boolean hasNext() {
        return this.mCurrToken != 8 || this.mParseState == 3;
    }

    public boolean hasText() {
        return ((1 << this.mCurrToken) & MASK_GET_TEXT) != 0;
    }

    public boolean isAttributeSpecified(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.isSpecified(index);
    }

    public boolean isCharacters() {
        return 4 == getEventType();
    }

    public boolean isEndElement() {
        return this.mCurrToken == 2;
    }

    public boolean isStartElement() {
        return this.mCurrToken == 1;
    }

    public boolean isWhiteSpace() {
        int currToken = this.mCurrToken;
        if (currToken != 4 && currToken != 12) {
            return currToken == 6;
        }
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
        if (this.mWsStatus == 0) {
            this.mWsStatus = this.mTextBuffer.isAllWhitespace() ? 1 : 2;
        }
        return this.mWsStatus == 1;
    }

    public void require(int type, String nsUri, String localName) throws XMLStreamException {
        int curr = this.mCurrToken;
        if (curr != type) {
            if (curr == 12) {
                if (this.mCfgCoalesceText || this.mCfgReportTextAsChars) {
                    curr = 4;
                }
            } else if (curr == 6) {
            }
        }
        if (type != curr) {
            throwParseError("Expected type " + tokenTypeDesc(type) + ", current type " + tokenTypeDesc(curr));
        }
        if (localName != null) {
            if (curr != 1 && curr != 2 && curr != 9) {
                throwParseError("Expected non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + tokenTypeDesc(this.mCurrToken) + ")");
            }
            String n = getLocalName();
            if (n != localName && !n.equals(localName)) {
                throwParseError("Expected local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (nsUri != null) {
            if (curr != 1 && curr != 2) {
                throwParseError("Expected non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + tokenTypeDesc(curr) + ")");
            }
            String uri = this.mElementStack.getNsURI();
            if (nsUri.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    throwParseError("Expected empty namespace, instead have '" + uri + "'.");
                    return;
                }
                return;
            }
            if (nsUri != uri && !nsUri.equals(uri)) {
                throwParseError("Expected namespace '" + nsUri + "'; have '" + uri + "'.");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final int next() throws XMLStreamException {
        if (this.mPendingException != null) {
            XMLStreamException strEx = this.mPendingException;
            this.mPendingException = null;
            throw strEx;
        }
        if (this.mParseState == 1) {
            int type = nextFromTree();
            this.mCurrToken = type;
            if (this.mTokenState < this.mStTextThreshold && (!this.mCfgLazyParsing || (this.mValidateText && (type == 4 || type == 12)))) {
                finishToken(false);
            }
            if (type == 12) {
                if (this.mValidateText) {
                    this.mElementStack.validateText(this.mTextBuffer, false);
                }
                if (this.mCfgCoalesceText || this.mCfgReportTextAsChars) {
                    return 4;
                }
                this.mCurrTextLength += this.mTextBuffer.size();
                verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mCurrTextLength);
            } else if (type == 4) {
                if (this.mValidateText) {
                    if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
                        this.mElementStack.validateText(this.mTextBuffer, true);
                    } else {
                        this.mElementStack.validateText(this.mTextBuffer, false);
                    }
                }
                this.mCurrTextLength += this.mTextBuffer.size();
                verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mCurrTextLength);
            } else if (type == 1 || type == 2) {
                this.mCurrTextLength = 0;
            }
            return type;
        }
        if (this.mParseState == 0) {
            nextFromProlog(true);
        } else if (this.mParseState == 2) {
            if (nextFromProlog(false)) {
                this.mSecondaryToken = 0;
            }
        } else if (this.mParseState == 3) {
            this.mCurrToken = nextFromMultiDocState();
        } else {
            if (this.mSecondaryToken == 8) {
                this.mSecondaryToken = 0;
                return 8;
            }
            throw new NoSuchElementException();
        }
        return this.mCurrToken;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int nextTag() throws XMLStreamException {
        while (true) {
            int next = next();
            switch (next) {
                case 1:
                case 2:
                    return next;
                case 3:
                case 5:
                case 6:
                    continue;
                case 4:
                case 12:
                    if (this.mTokenState < this.mStTextThreshold) {
                        finishToken(false);
                    }
                    if (this.mWsStatus == 0) {
                        this.mWsStatus = this.mTextBuffer.isAllWhitespace() ? 1 : 2;
                    }
                    if (this.mWsStatus != 1) {
                        throwParseError("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                        break;
                    }
                    break;
            }
            throwParseError("Received event " + ErrorConsts.tokenTypeDesc(next) + ", instead of START_ELEMENT or END_ELEMENT.");
        }
    }

    public void close() throws XMLStreamException {
        if (this.mParseState != 4) {
            this.mParseState = 4;
            if (this.mCurrToken != 8) {
                this.mSecondaryToken = 8;
                this.mCurrToken = 8;
                if (this.mSymbols.isDirty()) {
                    this.mOwner.updateSymbolTable(this.mSymbols);
                }
            }
            closeAllInput(false);
            this.mTextBuffer.recycle(true);
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    @Deprecated
    public Object getFeature(String name) {
        throw new IllegalArgumentException(MessageFormat.format(ErrorConsts.ERR_UNKNOWN_FEATURE, name));
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    @Deprecated
    public void setFeature(String name, Object value) {
        throw new IllegalArgumentException(MessageFormat.format(ErrorConsts.ERR_UNKNOWN_FEATURE, name));
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean setProperty(String name, Object value) {
        boolean ok = this.mConfig.setProperty(name, value);
        if (ok && WstxInputProperties.P_BASE_URL.equals(name)) {
            this.mInput.overrideSource(this.mConfig.getBaseURL());
        }
        return ok;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public void skipElement() throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        int nesting = 1;
        while (true) {
            int type = next();
            if (type == 1) {
                nesting++;
            } else if (type == 2) {
                nesting--;
                if (nesting == 0) {
                    return;
                }
            } else {
                continue;
            }
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this.mCurrToken != 11) {
            return null;
        }
        if (this.mTokenState < 3) {
            finishToken(false);
        }
        return this;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public final LocationInfo getLocationInfo() {
        return this;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getText(Writer w, boolean preserveContents) throws XMLStreamException, IOException {
        int currToken = this.mCurrToken;
        if (((1 << currToken) & MASK_GET_TEXT_WITH_WRITER) == 0) {
            throwNotTextual(currToken);
        }
        if (!preserveContents) {
            if (currToken == 4) {
                int count = this.mTextBuffer.rawContentsTo(w);
                this.mTextBuffer.resetWithEmpty();
                if (this.mTokenState < 3) {
                    count += readAndWriteText(w);
                }
                if (this.mCfgCoalesceText && this.mTokenState < 4 && this.mCfgCoalesceText) {
                    count += readAndWriteCoalesced(w, false);
                }
                return count;
            }
            if (currToken == 12) {
                int count2 = this.mTextBuffer.rawContentsTo(w);
                this.mTextBuffer.resetWithEmpty();
                if (this.mTokenState < 3) {
                    count2 += readAndWriteCData(w);
                }
                if (this.mCfgCoalesceText && this.mTokenState < 4 && this.mCfgCoalesceText) {
                    count2 += readAndWriteCoalesced(w, true);
                }
                return count2;
            }
        }
        if (this.mTokenState < this.mStTextThreshold) {
            finishToken(false);
        }
        if (currToken == 9) {
            return this.mCurrEntity.getReplacementText(w);
        }
        if (currToken == 11) {
            char[] ch = getDTDInternalSubsetArray();
            if (ch != null) {
                w.write(ch);
                return ch.length;
            }
            return 0;
        }
        return this.mTextBuffer.rawContentsTo(w);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getDepth() {
        return this.mElementStack.getDepth();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isEmptyElement() throws XMLStreamException {
        if (this.mCurrToken == 1) {
            return this.mStEmptyElem;
        }
        return false;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public NamespaceContext getNonTransientNamespaceContext() {
        return this.mElementStack.createNonTransientNsContext(null);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public String getPrefixedName() {
        switch (this.mCurrToken) {
            case 1:
            case 2:
                String prefix = this.mElementStack.getPrefix();
                String ln = this.mElementStack.getLocalName();
                if (prefix == null) {
                    return ln;
                }
                StringBuilder sb = new StringBuilder(ln.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(ln);
                return sb.toString();
            case 3:
                return getPITarget();
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
            case 9:
                return getLocalName();
            case 11:
                return getDTDRootName();
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public void closeCompletely() throws XMLStreamException {
        closeAllInput(true);
    }

    @Override // org.codehaus.stax2.DTDInfo
    public Object getProcessedDTD() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDRootName() {
        if (this.mRootPrefix == null) {
            return this.mRootLName;
        }
        return this.mRootPrefix + ":" + this.mRootLName;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDPublicId() {
        return this.mDtdPublicId;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDSystemId() {
        return this.mDtdSystemId;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDInternalSubset() {
        if (this.mCurrToken != 11) {
            return null;
        }
        return this.mTextBuffer.contentsAsString();
    }

    private char[] getDTDInternalSubsetArray() {
        return this.mTextBuffer.contentsAsArray();
    }

    @Override // org.codehaus.stax2.DTDInfo
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getStartingByteOffset() {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getStartingCharOffset() {
        return this.mTokenInputTotal;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.LocationInfo
    public long getEndingCharOffset() throws XMLStreamException {
        if (this.mTokenState < this.mStTextThreshold) {
            finishToken(false);
        }
        return this.mCurrInputProcessed + ((long) this.mInputPtr);
    }

    @Override // com.ctc.wstx.sr.StreamScanner, com.ctc.wstx.sr.InputProblemReporter
    public final Location getLocation() {
        return getStartLocation();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.LocationInfo
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        if (this.mTokenState < this.mStTextThreshold) {
            finishToken(false);
        }
        return getCurrentLocation();
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    @Override // com.ctc.wstx.sr.StreamReaderImpl
    public EntityDecl getCurrentEntityDecl() {
        return this.mCurrEntity;
    }

    @Override // com.ctc.wstx.sr.StreamReaderImpl
    public Object withStartElement(ElemCallback cb, Location loc) {
        if (this.mCurrToken != 1) {
            return null;
        }
        return cb.withStartElement(loc, getName(), this.mElementStack.createNonTransientNsContext(loc), this.mAttrCollector.buildAttrOb(), this.mStEmptyElem);
    }

    @Override // com.ctc.wstx.sr.StreamReaderImpl
    public boolean isNamespaceAware() {
        return this.mCfgNsEnabled;
    }

    @Override // com.ctc.wstx.sr.StreamReaderImpl
    public InputElementStack getInputElementStack() {
        return this.mElementStack;
    }

    @Override // com.ctc.wstx.sr.StreamReaderImpl
    public AttributeCollector getAttributeCollector() {
        return this.mAttrCollector;
    }

    public void fireSaxStartElement(ContentHandler h, Attributes attrs) throws SAXException {
        if (h != null) {
            int nsCount = this.mElementStack.getCurrentNsCount();
            for (int i = 0; i < nsCount; i++) {
                String prefix = this.mElementStack.getLocalNsPrefix(i);
                h.startPrefixMapping(prefix == null ? "" : prefix, this.mElementStack.getLocalNsURI(i));
            }
            String uri = this.mElementStack.getNsURI();
            h.startElement(uri == null ? "" : uri, this.mElementStack.getLocalName(), getPrefixedName(), attrs);
        }
    }

    public void fireSaxEndElement(ContentHandler h) throws SAXException {
        if (h != null) {
            String uri = this.mElementStack.getNsURI();
            h.endElement(uri == null ? "" : uri, this.mElementStack.getLocalName(), getPrefixedName());
            int nsCount = this.mElementStack.getCurrentNsCount();
            for (int i = 0; i < nsCount; i++) {
                String prefix = this.mElementStack.getLocalNsPrefix(i);
                h.endPrefixMapping(prefix == null ? "" : prefix);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void fireSaxCharacterEvents(ContentHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mPendingException != null) {
                XMLStreamException sex = this.mPendingException;
                this.mPendingException = null;
                throw sex;
            }
            if (this.mTokenState < this.mStTextThreshold) {
                finishToken(false);
            }
            this.mTextBuffer.fireSaxCharacterEvents(h);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void fireSaxSpaceEvents(ContentHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mTokenState < this.mStTextThreshold) {
                finishToken(false);
            }
            this.mTextBuffer.fireSaxSpaceEvents(h);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void fireSaxCommentEvent(LexicalHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mTokenState < this.mStTextThreshold) {
                finishToken(false);
            }
            this.mTextBuffer.fireSaxCommentEvent(h);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void fireSaxPIEvent(ContentHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mTokenState < this.mStTextThreshold) {
                finishToken(false);
            }
            h.processingInstruction(this.mCurrName, this.mTextBuffer.contentsAsString());
        }
    }

    protected final boolean hasConfigFlags(int flags) {
        return (this.mConfigFlags & flags) == flags;
    }

    protected String checkKeyword(char c, String expected) throws XMLStreamException {
        char c2;
        int ptr = 0;
        int len = expected.length();
        while (expected.charAt(ptr) == c) {
            ptr++;
            if (ptr >= len) {
                break;
            }
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                c = cArr[i];
            } else {
                int ci = getNext();
                if (ci < 0) {
                    break;
                }
                c = (char) ci;
            }
        }
        if (ptr == len) {
            int i2 = peekNext();
            if (i2 < 0) {
                return null;
            }
            if (!isNameChar((char) i2) && i2 != 58) {
                return null;
            }
        }
        StringBuilder sb = new StringBuilder(expected.length() + 16);
        sb.append(expected.substring(0, ptr));
        if (ptr < len) {
            sb.append(c);
        }
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr2 = this.mInputBuffer;
                int i3 = this.mInputPtr;
                this.mInputPtr = i3 + 1;
                c2 = cArr2[i3];
            } else {
                int ci2 = getNext();
                if (ci2 < 0) {
                    break;
                }
                c2 = (char) ci2;
            }
            if (!isNameChar(c2)) {
                this.mInputPtr--;
                break;
            }
            sb.append(c2);
        }
        return sb.toString();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void checkCData() throws XMLStreamException {
        String wrong = checkKeyword(getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CDATA), "CDATA");
        if (wrong != null) {
            throwParseError("Unrecognized XML directive '" + wrong + "'; expected 'CDATA'.");
        }
        char c = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CDATA);
        if (c != '[') {
            throwUnexpectedChar(c, "expected '[' after '<![CDATA'");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:38:0x00dc  */
    /* JADX WARN: Code duplicated, block: B:43:0x00f2  */
    /* JADX WARN: Code duplicated, block: B:44:0x00fa  */
    /* JADX WARN: Code duplicated, block: B:46:0x0108  */
    /* JADX WARN: Code duplicated, block: B:61:0x00eb A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x0029 A[SYNTHETIC] */
    private final void parseAttrValue(char openingQuote, TextBuilder tb) throws XMLStreamException {
        char nextChar;
        int ch;
        char[] outBuf = tb.getCharBuffer();
        int outPtr = tb.getCharSize();
        int maxAttrSize = this.mConfig.getMaxAttributeSize();
        int outLimit = _outputLimit(outBuf, outPtr, maxAttrSize);
        WstxInputSource currScope = this.mInput;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(ParsingErrorMsgs.SUFFIX_IN_ATTR_VALUE);
            }
            char c = nextChar;
            if (c <= '\'') {
                if (c < ' ') {
                    if (c == '\n') {
                        markLF();
                    } else if (c == '\r') {
                        if (this.mNormalizeLFs && getNextChar(ParsingErrorMsgs.SUFFIX_IN_ATTR_VALUE) != '\n') {
                            this.mInputPtr--;
                        }
                        markLF();
                    } else if (c != '\t') {
                        throwInvalidSpace(c);
                    }
                    c = ' ';
                } else if (c == openingQuote) {
                    if (this.mInput == currScope) {
                        tb.setBufferSize(outPtr);
                        return;
                    }
                } else if (c == '&') {
                    if (inputInBuffer() >= 3) {
                        int iResolveSimpleEntity = resolveSimpleEntity(true);
                        ch = iResolveSimpleEntity;
                        if (iResolveSimpleEntity == 0) {
                            ch = fullyResolveEntity(false);
                            if (ch != 0) {
                            }
                        }
                        if (ch <= 65535) {
                            c = (char) ch;
                        } else {
                            int ch2 = ch - 65536;
                            if (outPtr >= outLimit) {
                                outBuf = _checkAttributeLimit(tb, outBuf, outPtr, outPtr - outPtr, maxAttrSize);
                                outLimit = _outputLimit(outBuf, outPtr, maxAttrSize);
                            }
                            int i2 = outPtr;
                            outPtr++;
                            outBuf[i2] = (char) ((ch2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                            c = (char) ((ch2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                        }
                    } else {
                        ch = fullyResolveEntity(false);
                        if (ch != 0) {
                            if (ch <= 65535) {
                                c = (char) ch;
                            } else {
                                int ch3 = ch - 65536;
                                if (outPtr >= outLimit) {
                                    outBuf = _checkAttributeLimit(tb, outBuf, outPtr, outPtr - outPtr, maxAttrSize);
                                    outLimit = _outputLimit(outBuf, outPtr, maxAttrSize);
                                }
                                int i3 = outPtr;
                                outPtr++;
                                outBuf[i3] = (char) ((ch3 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                                c = (char) ((ch3 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                            }
                        }
                    }
                }
            } else if (c == '<') {
                throwUnexpectedChar(c, ParsingErrorMsgs.SUFFIX_IN_ATTR_VALUE);
            }
            if (outPtr >= outLimit) {
                outBuf = _checkAttributeLimit(tb, outBuf, outPtr, outPtr - outPtr, maxAttrSize);
                outLimit = _outputLimit(outBuf, outPtr, maxAttrSize);
            }
            int i4 = outPtr;
            outPtr++;
            outBuf[i4] = c;
        }
    }

    private final int _outputLimit(char[] outBuf, int offset, int maxAttrLen) {
        return Math.min(outBuf.length, Math.max(maxAttrLen, offset + maxAttrLen));
    }

    private final char[] _checkAttributeLimit(TextBuilder tb, char[] outBuf, int outPtr, int currAttrSize, int maxAttrSize) throws XMLStreamException {
        verifyLimit("Maximum attribute size", maxAttrSize, currAttrSize + 1);
        if (outPtr < outBuf.length) {
            ExceptionUtil.throwInternal(String.format("Expected either currAttrSize (%d) > maxAttrSize (%d) OR outPtr (%d) >= outBuf.length (%d)", Integer.valueOf(currAttrSize), Integer.valueOf(maxAttrSize), Integer.valueOf(outPtr), Integer.valueOf(outBuf.length)));
        }
        return tb.bufferFull(1);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private boolean nextFromProlog(boolean isProlog) throws XMLStreamException {
        int i;
        if (this.mTokenState < this.mStTextThreshold) {
            this.mTokenState = 4;
            i = skipToken();
        } else {
            this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
            this.mTokenInputRow = this.mCurrInputRow;
            this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
            i = getNext();
        }
        if (i <= 32 && i >= 0) {
            if (hasConfigFlags(256)) {
                this.mCurrToken = 6;
                if (readSpacePrimary((char) i, true)) {
                    this.mTokenState = 4;
                    return false;
                }
                if (this.mCfgLazyParsing) {
                    this.mTokenState = 1;
                    return false;
                }
                readSpaceSecondary(true);
                this.mTokenState = 4;
                return false;
            }
            this.mInputPtr--;
            i = getNextAfterWS();
            if (i >= 0) {
                this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
            }
        }
        if (i < 0) {
            handleEOF(isProlog);
            this.mParseState = 4;
            return true;
        }
        if (i != 60) {
            throwUnexpectedChar(i, (isProlog ? ParsingErrorMsgs.SUFFIX_IN_PROLOG : ParsingErrorMsgs.SUFFIX_IN_EPILOG) + "; expected '<'");
        }
        char c = getNextChar(isProlog ? ParsingErrorMsgs.SUFFIX_IN_PROLOG : ParsingErrorMsgs.SUFFIX_IN_EPILOG);
        if (c == '?') {
            this.mCurrToken = readPIPrimary();
        } else if (c == '!') {
            nextFromPrologBang(isProlog);
        } else if (c == '/') {
            if (isProlog) {
                throwParseError("Unexpected character combination '</' in prolog.");
            }
            throwParseError("Unexpected character combination '</' in epilog (extra close tag?).");
        } else if (c == ':' || isNameStartChar(c)) {
            if (!isProlog) {
                this.mCurrToken = handleExtraRoot(c);
                return false;
            }
            handleRootElem(c);
            this.mCurrToken = 1;
        } else {
            throwUnexpectedChar(c, (isProlog ? ParsingErrorMsgs.SUFFIX_IN_PROLOG : ParsingErrorMsgs.SUFFIX_IN_EPILOG) + ", after '<'.");
        }
        if (!this.mCfgLazyParsing && this.mTokenState < this.mStTextThreshold) {
            finishToken(false);
            return false;
        }
        return false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void handleRootElem(char c) throws XMLStreamException {
        this.mParseState = 1;
        initValidation();
        handleStartElem(c);
        if (this.mRootLName != null && hasConfigFlags(32) && !this.mElementStack.matches(this.mRootPrefix, this.mRootLName)) {
            String actual = this.mRootPrefix == null ? this.mRootLName : this.mRootPrefix + ":" + this.mRootLName;
            reportValidationProblem(ErrorConsts.ERR_VLD_WRONG_ROOT, actual, this.mRootLName);
        }
    }

    protected void initValidation() throws XMLStreamException {
    }

    protected int handleEOF(boolean isProlog) throws XMLStreamException {
        this.mSecondaryToken = 8;
        this.mCurrToken = 8;
        this.mTextBuffer.recycle(true);
        if (isProlog) {
            throwUnexpectedEOF(ParsingErrorMsgs.SUFFIX_IN_PROLOG);
        }
        return this.mCurrToken;
    }

    private int handleExtraRoot(char c) throws XMLStreamException {
        if (!this.mConfig.inputParsingModeDocuments()) {
            throwParseError("Illegal to have multiple roots (start tag in epilog?).");
        }
        this.mInputPtr--;
        return handleMultiDocStart(1);
    }

    protected int handleMultiDocStart(int nextEvent) {
        this.mParseState = 3;
        this.mTokenState = 4;
        this.mSecondaryToken = nextEvent;
        return 8;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private int nextFromMultiDocState() throws XMLStreamException {
        if (this.mCurrToken == 8) {
            if (this.mSecondaryToken == 7) {
                handleMultiDocXmlDecl();
                return 7;
            }
            this.mDocXmlEncoding = null;
            this.mDocXmlVersion = 0;
            this.mDocStandalone = 0;
            return 7;
        }
        if (this.mCurrToken == 7) {
            this.mParseState = 0;
            if (this.mSecondaryToken == 7) {
                nextFromProlog(true);
                return this.mCurrToken;
            }
            if (this.mSecondaryToken == 1) {
                handleRootElem(getNextChar(ParsingErrorMsgs.SUFFIX_IN_ELEMENT));
                return 1;
            }
            if (this.mSecondaryToken == 11) {
                this.mStDoctypeFound = true;
                startDTD();
                return 11;
            }
        }
        throw new IllegalStateException("Internal error: unexpected state; current event " + tokenTypeDesc(this.mCurrToken) + ", sec. state: " + tokenTypeDesc(this.mSecondaryToken));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void handleMultiDocXmlDecl() throws XMLStreamException {
        this.mDocStandalone = 0;
        this.mDocXmlEncoding = null;
        char c = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
        String wrong = checkKeyword(c, XmlConsts.XML_DECL_KW_VERSION);
        if (wrong != null) {
            throwParseError(ErrorConsts.ERR_UNEXP_KEYWORD, wrong, XmlConsts.XML_DECL_KW_VERSION);
        }
        char c2 = skipEquals(XmlConsts.XML_DECL_KW_VERSION, ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
        TextBuffer tb = this.mTextBuffer;
        tb.resetInitialized();
        parseQuoted(XmlConsts.XML_DECL_KW_VERSION, c2, tb);
        if (tb.equalsString("1.0")) {
            this.mDocXmlVersion = 256;
            this.mXml11 = false;
        } else if (tb.equalsString(XmlConsts.XML_V_11_STR)) {
            this.mDocXmlVersion = 272;
            this.mXml11 = true;
        } else {
            this.mDocXmlVersion = 0;
            this.mXml11 = false;
            throwParseError("Unexpected xml version '" + tb.toString() + "'; expected '1.0' or '" + XmlConsts.XML_V_11_STR + OperatorName.SHOW_TEXT_LINE);
        }
        char c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
        if (c3 != '?') {
            if (c3 == 'e') {
                String wrong2 = checkKeyword(c3, "encoding");
                if (wrong2 != null) {
                    throwParseError(ErrorConsts.ERR_UNEXP_KEYWORD, wrong2, "encoding");
                }
                char c4 = skipEquals("encoding", ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
                tb.resetWithEmpty();
                parseQuoted("encoding", c4, tb);
                this.mDocXmlEncoding = tb.toString();
                c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
            } else if (c3 != 's') {
                throwUnexpectedChar(c3, " in xml declaration; expected either 'encoding' or 'standalone' pseudo-attribute");
            }
            if (c3 == 's') {
                String wrong3 = checkKeyword(c3, XmlConsts.XML_DECL_KW_STANDALONE);
                if (wrong3 != null) {
                    throwParseError(ErrorConsts.ERR_UNEXP_KEYWORD, wrong3, XmlConsts.XML_DECL_KW_STANDALONE);
                }
                char c5 = skipEquals(XmlConsts.XML_DECL_KW_STANDALONE, ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
                tb.resetWithEmpty();
                parseQuoted(XmlConsts.XML_DECL_KW_STANDALONE, c5, tb);
                if (tb.equalsString(XmlConsts.XML_SA_YES)) {
                    this.mDocStandalone = 1;
                } else if (tb.equalsString(XmlConsts.XML_SA_NO)) {
                    this.mDocStandalone = 2;
                } else {
                    throwParseError("Unexpected xml 'standalone' pseudo-attribute value '" + tb.toString() + "'; expected '" + XmlConsts.XML_SA_YES + "' or '" + XmlConsts.XML_SA_NO + OperatorName.SHOW_TEXT_LINE);
                }
                c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
            }
        }
        if (c3 != '?') {
            throwUnexpectedChar(c3, " in xml declaration; expected '?>' as the end marker");
        }
        char c6 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
        if (c6 != '>') {
            throwUnexpectedChar(c6, " in xml declaration; expected '>' to close the declaration");
        }
    }

    protected final char skipEquals(String name, String eofMsg) throws XMLStreamException {
        char c = getNextInCurrAfterWS(eofMsg);
        if (c != '=') {
            throwUnexpectedChar(c, " in xml declaration; expected '=' to follow pseudo-attribute '" + name + OperatorName.SHOW_TEXT_LINE);
        }
        return getNextInCurrAfterWS(eofMsg);
    }

    protected final void parseQuoted(String name, char quoteChar, TextBuffer tbuf) throws XMLStreamException {
        char nextChar;
        if (quoteChar != '\"' && quoteChar != '\'') {
            throwUnexpectedChar(quoteChar, " in xml declaration; waited ' or \" to start a value for pseudo-attribute '" + name + OperatorName.SHOW_TEXT_LINE);
        }
        char[] outBuf = tbuf.getCurrentSegment();
        int outPtr = 0;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
            }
            char c = nextChar;
            if (c != quoteChar) {
                if (c < ' ' || c == '<') {
                    if (c == 0) {
                        throwNullChar();
                    } else {
                        throwUnexpectedChar(c, ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
                    }
                }
                if (outPtr >= outBuf.length) {
                    outBuf = tbuf.finishCurrentSegment();
                    outPtr = 0;
                }
                int i2 = outPtr;
                outPtr++;
                outBuf[i2] = c;
            } else {
                tbuf.setCurrentLength(outPtr);
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void nextFromPrologBang(boolean isProlog) throws XMLStreamException {
        int i = getNext();
        if (i < 0) {
            throwUnexpectedEOF(ParsingErrorMsgs.SUFFIX_IN_PROLOG);
        }
        if (i == 68) {
            String keyw = checkKeyword('D', "DOCTYPE");
            if (keyw != null) {
                throwParseError("Unrecognized XML directive '<!" + keyw + "' (misspelled DOCTYPE?).");
            }
            if (!isProlog) {
                if (this.mConfig.inputParsingModeDocuments()) {
                    if (!this.mStDoctypeFound) {
                        this.mCurrToken = handleMultiDocStart(11);
                        return;
                    }
                } else {
                    throwParseError(ErrorConsts.ERR_DTD_IN_EPILOG);
                }
            }
            if (this.mStDoctypeFound) {
                throwParseError(ErrorConsts.ERR_DTD_DUP);
            }
            this.mStDoctypeFound = true;
            this.mCurrToken = 11;
            startDTD();
            return;
        }
        if (i == 45) {
            char c = getNextChar(isProlog ? ParsingErrorMsgs.SUFFIX_IN_PROLOG : ParsingErrorMsgs.SUFFIX_IN_EPILOG);
            if (c != '-') {
                throwUnexpectedChar(i, " (malformed comment?)");
            }
            this.mTokenState = 1;
            this.mCurrToken = 5;
            return;
        }
        if (i == 91) {
            i = peekNext();
            if (i == 67) {
                throwUnexpectedChar(i, ErrorConsts.ERR_CDATA_IN_EPILOG);
            }
        }
        throwUnexpectedChar(i, " after '<!' (malformed comment?)");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void startDTD() throws XMLStreamException {
        this.mTextBuffer.resetInitialized();
        char c = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_DTD);
        if (this.mCfgNsEnabled) {
            String str = parseLocalName(c);
            char c2 = getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD);
            if (c2 == ':') {
                this.mRootPrefix = str;
                this.mRootLName = parseLocalName(getNextChar(ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME));
            } else if (c2 <= ' ' || c2 == '[' || c2 == '>') {
                this.mInputPtr--;
                this.mRootPrefix = null;
                this.mRootLName = str;
            } else {
                throwUnexpectedChar(c2, " in DOCTYPE declaration; expected '[' or white space.");
            }
        } else {
            this.mRootLName = parseFullName(c);
            this.mRootPrefix = null;
        }
        char c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_DTD);
        if (c3 != '[' && c3 != '>') {
            String keyw = null;
            if (c3 == 'P') {
                keyw = checkKeyword(getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD), "UBLIC");
                if (keyw != null) {
                    keyw = "P" + keyw;
                } else {
                    if (!skipWS(getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD))) {
                        throwUnexpectedChar(c3, " in DOCTYPE declaration; expected a space between PUBLIC keyword and public id");
                    }
                    char c4 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_DTD);
                    if (c4 != '\"' && c4 != '\'') {
                        throwUnexpectedChar(c4, " in DOCTYPE declaration; expected a public identifier.");
                    }
                    this.mDtdPublicId = parsePublicId(c4, ParsingErrorMsgs.SUFFIX_IN_DTD);
                    if (this.mDtdPublicId.length() == 0) {
                    }
                    if (!skipWS(getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD))) {
                        throwUnexpectedChar(c4, " in DOCTYPE declaration; expected a space between public and system identifiers");
                    }
                    char c5 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_DTD);
                    if (c5 != '\"' && c5 != '\'') {
                        throwParseError(" in DOCTYPE declaration; expected a system identifier.");
                    }
                    this.mDtdSystemId = parseSystemId(c5, this.mNormalizeLFs, ParsingErrorMsgs.SUFFIX_IN_DTD);
                    if (this.mDtdSystemId.length() == 0) {
                    }
                }
            } else if (c3 == 'S') {
                this.mDtdPublicId = null;
                keyw = checkKeyword(getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD), "YSTEM");
                if (keyw != null) {
                    keyw = "S" + keyw;
                } else {
                    char c6 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_DTD);
                    if (c6 != '\"' && c6 != '\'') {
                        throwUnexpectedChar(c6, " in DOCTYPE declaration; expected a system identifier.");
                    }
                    this.mDtdSystemId = parseSystemId(c6, this.mNormalizeLFs, ParsingErrorMsgs.SUFFIX_IN_DTD);
                    if (this.mDtdSystemId.length() == 0) {
                        this.mDtdSystemId = null;
                    }
                }
            } else if (!isNameStartChar(c3)) {
                throwUnexpectedChar(c3, " in DOCTYPE declaration; expected keywords 'PUBLIC' or 'SYSTEM'.");
            } else {
                this.mInputPtr--;
                keyw = checkKeyword(c3, "SYSTEM");
            }
            if (keyw != null) {
                throwParseError("Unexpected keyword '" + keyw + "'; expected 'PUBLIC' or 'SYSTEM'");
            }
            c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_DTD);
        }
        if (c3 != '[' && c3 != '>') {
            throwUnexpectedChar(c3, " in DOCTYPE declaration; expected closing '>'.");
        }
        this.mInputPtr--;
        this.mTokenState = 1;
    }

    protected void finishDTD(boolean copyContents) throws XMLStreamException {
        char c = getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD);
        if (c == '[') {
            if (copyContents) {
                ((BranchingReaderSource) this.mInput).startBranch(this.mTextBuffer, this.mInputPtr, this.mNormalizeLFs);
            }
            try {
                MinimalDTDReader.skipInternalSubset(this, this.mInput, this.mConfig);
                if (copyContents) {
                    ((BranchingReaderSource) this.mInput).endBranch(this.mInputPtr - 1);
                }
                c = getNextCharAfterWS(ParsingErrorMsgs.SUFFIX_IN_DTD_INTERNAL);
            } catch (Throwable th) {
                if (copyContents) {
                    ((BranchingReaderSource) this.mInput).endBranch(this.mInputPtr - 1);
                }
                throw th;
            }
        }
        if (c != '>') {
            throwUnexpectedChar(c, "; expected '>' to finish DOCTYPE declaration.");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final int nextFromTree() throws XMLStreamException {
        char c;
        int i;
        if (this.mTokenState < this.mStTextThreshold) {
            if (this.mVldContent == 3 && (this.mCurrToken == 4 || this.mCurrToken == 12)) {
                throwParseError("Internal error: skipping validatable text");
            }
            i = skipToken();
        } else {
            if (this.mCurrToken == 1) {
                if (this.mStEmptyElem) {
                    this.mStEmptyElem = false;
                    int vld = this.mElementStack.validateEndElement();
                    this.mVldContent = vld;
                    this.mValidateText = vld == 3;
                    return 2;
                }
            } else if (this.mCurrToken == 2) {
                if (!this.mElementStack.pop() && !this.mConfig.inputParsingModeFragment()) {
                    return closeContentTree();
                }
            } else if (this.mCurrToken == 12 && this.mTokenState <= 2) {
                this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    c = cArr[i2];
                } else {
                    c = getNextChar(ParsingErrorMsgs.SUFFIX_IN_CDATA);
                }
                if (readCDataPrimary(c)) {
                    if (this.mTextBuffer.size() > 0) {
                        return 12;
                    }
                } else {
                    if (this.mTextBuffer.size() == 0) {
                        if (readCDataSecondary(this.mCfgLazyParsing ? 1 : this.mShortestTextSegment)) {
                            if (this.mTextBuffer.size() > 0) {
                                this.mTokenState = 3;
                                return 12;
                            }
                        }
                    }
                    this.mTokenState = 2;
                    return 12;
                }
            }
            this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
            this.mTokenInputRow = this.mCurrInputRow;
            this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
            i = getNext();
        }
        if (i < 0) {
            if (!this.mElementStack.isEmpty()) {
                throwUnexpectedEOF();
            }
            return handleEOF(false);
        }
        while (i == 38) {
            this.mWsStatus = 0;
            if (this.mVldContent == 0) {
                reportInvalidContent(9);
            }
            int ch = this.mCfgReplaceEntities ? fullyResolveEntity(true) : resolveCharOnlyEntity(true);
            if (ch != 0) {
                if (this.mVldContent <= 1 && ch > 32) {
                    reportInvalidContent(4);
                }
                TextBuffer tb = this.mTextBuffer;
                tb.resetInitialized();
                if (ch <= 65535) {
                    tb.append((char) ch);
                } else {
                    int ch2 = ch - 65536;
                    tb.append((char) ((ch2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST));
                    tb.append((char) ((ch2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST));
                }
                this.mTokenState = 1;
                return 4;
            }
            if (!this.mCfgReplaceEntities || this.mCfgTreatCharRefsAsEntities) {
                if (!this.mCfgTreatCharRefsAsEntities) {
                    EntityDecl ed = resolveNonCharEntity();
                    this.mCurrEntity = ed;
                }
                this.mTokenState = 4;
                return 9;
            }
            i = getNextChar(ParsingErrorMsgs.SUFFIX_IN_DOC);
        }
        if (i == 60) {
            char c2 = getNextChar(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            if (c2 == '?') {
                if (this.mVldContent == 0) {
                    reportInvalidContent(3);
                }
                return readPIPrimary();
            }
            if (c2 == '!') {
                int type = nextFromTreeCommentOrCData();
                if (this.mVldContent == 0) {
                    reportInvalidContent(type);
                }
                return type;
            }
            if (c2 == '/') {
                readEndElem();
                return 2;
            }
            if (c2 == ':' || isNameStartChar(c2)) {
                handleStartElem(c2);
                return 1;
            }
            if (c2 == '[') {
                throwUnexpectedChar(c2, " in content after '<' (malformed <![CDATA[]] directive?)");
            }
            throwUnexpectedChar(c2, " in content after '<' (malformed start element?).");
        }
        if (this.mVldContent <= 2) {
            if (this.mVldContent == 0 && this.mElementStack.reallyValidating()) {
                reportInvalidContent(4);
            }
            if (i <= 32) {
                this.mTokenState = readSpacePrimary((char) i, false) ? 4 : 1;
                return 6;
            }
            if (this.mElementStack.reallyValidating()) {
                reportInvalidContent(4);
            }
        }
        if (readTextPrimary((char) i)) {
            this.mTokenState = 3;
            return 4;
        }
        if (!this.mCfgCoalesceText && this.mTextBuffer.size() >= this.mShortestTextSegment) {
            this.mTokenState = 2;
            return 4;
        }
        this.mTokenState = 1;
        return 4;
    }

    private int closeContentTree() throws XMLStreamException {
        this.mParseState = 2;
        if (nextFromProlog(false)) {
            this.mSecondaryToken = 0;
        }
        if (this.mSymbols.isDirty()) {
            this.mOwner.updateSymbolTable(this.mSymbols);
        }
        this.mTextBuffer.recycle(false);
        return this.mCurrToken;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final void handleStartElem(char c) throws XMLStreamException {
        char nextCharFromCurrent;
        boolean empty;
        char nextCharFromCurrent2;
        char c2;
        char nextCharFromCurrent3;
        this.mTokenState = 4;
        if (this.mCfgNsEnabled) {
            String str = parseLocalName(c);
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextCharFromCurrent2 = cArr[i];
            } else {
                nextCharFromCurrent2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
            }
            char c3 = nextCharFromCurrent2;
            if (c3 == ':') {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr2 = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    c2 = cArr2[i2];
                } else {
                    c2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
                }
                this.mElementStack.push(str, parseLocalName(c2));
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr3 = this.mInputBuffer;
                    int i3 = this.mInputPtr;
                    this.mInputPtr = i3 + 1;
                    nextCharFromCurrent3 = cArr3[i3];
                } else {
                    nextCharFromCurrent3 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
                }
                c3 = nextCharFromCurrent3;
            } else {
                this.mElementStack.push(null, str);
            }
            empty = c3 == '>' ? false : handleNsAttrs(c3);
        } else {
            this.mElementStack.push(null, parseFullName(c));
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr4 = this.mInputBuffer;
                int i4 = this.mInputPtr;
                this.mInputPtr = i4 + 1;
                nextCharFromCurrent = cArr4[i4];
            } else {
                nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            char c4 = nextCharFromCurrent;
            empty = c4 == '>' ? false : handleNonNsAttrs(c4);
        }
        if (!empty) {
            this.mCurrDepth++;
        }
        this.mStEmptyElem = empty;
        int vld = this.mElementStack.resolveAndValidateElement();
        this.mVldContent = vld;
        this.mValidateText = vld == 3;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final boolean handleNsAttrs(char c) throws XMLStreamException {
        char nextCharFromCurrent;
        String prefix;
        String localName;
        char nextCharFromCurrent2;
        char nextCharFromCurrent3;
        TextBuilder tb;
        char nextCharFromCurrent4;
        char nextCharFromCurrent5;
        AttributeCollector ac = this.mAttrCollector;
        while (true) {
            if (c <= ' ') {
                c = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_ELEMENT, c);
            } else if (c != '/' && c != '>') {
                throwUnexpectedChar(c, " expected space, or '>' or \"/>\"");
            }
            if (c == '/') {
                char c2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
                if (c2 != '>') {
                    throwUnexpectedChar(c2, " expected '>'");
                    return true;
                }
                return true;
            }
            if (c == '>') {
                return false;
            }
            if (c == '<') {
                throwParseError("Unexpected '<' character in element (missing closing '>'?)");
            }
            String str = parseLocalName(c);
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextCharFromCurrent = cArr[i];
            } else {
                nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
            }
            char c3 = nextCharFromCurrent;
            if (c3 == ':') {
                prefix = str;
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr2 = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    nextCharFromCurrent5 = cArr2[i2];
                } else {
                    nextCharFromCurrent5 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
                }
                char c4 = nextCharFromCurrent5;
                localName = parseLocalName(c4);
            } else {
                this.mInputPtr--;
                prefix = null;
                localName = str;
            }
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr3 = this.mInputBuffer;
                int i3 = this.mInputPtr;
                this.mInputPtr = i3 + 1;
                nextCharFromCurrent2 = cArr3[i3];
            } else {
                nextCharFromCurrent2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            char c5 = nextCharFromCurrent2;
            if (c5 <= ' ') {
                c5 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_ELEMENT, c5);
            }
            if (c5 != '=') {
                throwUnexpectedChar(c5, " expected '='");
            }
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr4 = this.mInputBuffer;
                int i4 = this.mInputPtr;
                this.mInputPtr = i4 + 1;
                nextCharFromCurrent3 = cArr4[i4];
            } else {
                nextCharFromCurrent3 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            char c6 = nextCharFromCurrent3;
            if (c6 <= ' ') {
                c6 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_ELEMENT, c6);
            }
            if (c6 != '\"' && c6 != '\'') {
                throwUnexpectedChar(c6, " in start tag Expected a quote");
            }
            int startLen = -1;
            if (prefix == sPrefixXmlns) {
                tb = ac.getNsBuilder(localName);
                if (null == tb) {
                    throwParseError("Duplicate declaration for namespace prefix '" + localName + "'.");
                    return false;
                }
                startLen = tb.getCharSize();
            } else if (localName == sPrefixXmlns && prefix == null) {
                tb = ac.getDefaultNsBuilder();
                if (null == tb) {
                    throwParseError("Duplicate default namespace declaration.");
                }
            } else {
                tb = ac.getAttrBuilder(prefix, localName);
            }
            parseAttrValue(c6, tb);
            if (!this.mXml11 && startLen >= 0 && tb.getCharSize() == startLen) {
                throwParseError(ErrorConsts.ERR_NS_EMPTY);
            }
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr5 = this.mInputBuffer;
                int i5 = this.mInputPtr;
                this.mInputPtr = i5 + 1;
                nextCharFromCurrent4 = cArr5[i5];
            } else {
                nextCharFromCurrent4 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            c = nextCharFromCurrent4;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final boolean handleNonNsAttrs(char c) throws XMLStreamException {
        char nextCharFromCurrent;
        char nextCharFromCurrent2;
        char nextCharFromCurrent3;
        AttributeCollector ac = this.mAttrCollector;
        while (true) {
            if (c <= ' ') {
                c = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_ELEMENT, c);
            } else if (c != '/' && c != '>') {
                throwUnexpectedChar(c, " expected space, or '>' or \"/>\"");
            }
            if (c == '/') {
                char c2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
                if (c2 != '>') {
                    throwUnexpectedChar(c2, " expected '>'");
                    return true;
                }
                return true;
            }
            if (c == '>') {
                return false;
            }
            if (c == '<') {
                throwParseError("Unexpected '<' character in element (missing closing '>'?)");
            }
            String name = parseFullName(c);
            TextBuilder tb = ac.getAttrBuilder(null, name);
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextCharFromCurrent = cArr[i];
            } else {
                nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            char c3 = nextCharFromCurrent;
            if (c3 <= ' ') {
                c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_ELEMENT, c3);
            }
            if (c3 != '=') {
                throwUnexpectedChar(c3, " expected '='");
            }
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr2 = this.mInputBuffer;
                int i2 = this.mInputPtr;
                this.mInputPtr = i2 + 1;
                nextCharFromCurrent2 = cArr2[i2];
            } else {
                nextCharFromCurrent2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            char c4 = nextCharFromCurrent2;
            if (c4 <= ' ') {
                c4 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_ELEMENT, c4);
            }
            if (c4 != '\"' && c4 != '\'') {
                throwUnexpectedChar(c4, " in start tag Expected a quote");
            }
            parseAttrValue(c4, tb);
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr3 = this.mInputBuffer;
                int i3 = this.mInputPtr;
                this.mInputPtr = i3 + 1;
                nextCharFromCurrent3 = cArr3[i3];
            } else {
                nextCharFromCurrent3 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ELEMENT);
            }
            c = nextCharFromCurrent3;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void readEndElem() throws XMLStreamException {
        char nextCharFromCurrent;
        char nextCharFromCurrent2;
        char nextCharFromCurrent3;
        char c;
        char nextCharFromCurrent4;
        char nextCharFromCurrent5;
        this.mTokenState = 4;
        if (this.mElementStack.isEmpty()) {
            reportExtraEndElem();
            return;
        }
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            nextCharFromCurrent = cArr[i];
        } else {
            nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT);
        }
        char c2 = nextCharFromCurrent;
        if (!isNameStartChar(c2) && c2 != ':') {
            if (c2 <= ' ') {
                throwUnexpectedChar(c2, "; missing element name?");
            }
            throwUnexpectedChar(c2, "; expected an element name.");
        }
        String expPrefix = this.mElementStack.getPrefix();
        String expLocalName = this.mElementStack.getLocalName();
        if (expPrefix != null && expPrefix.length() > 0) {
            int len = expPrefix.length();
            int i2 = 0;
            while (true) {
                if (c2 != expPrefix.charAt(i2)) {
                    reportWrongEndPrefix(expPrefix, expLocalName, i2);
                    return;
                }
                i2++;
                if (i2 < len) {
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr2 = this.mInputBuffer;
                        int i3 = this.mInputPtr;
                        this.mInputPtr = i3 + 1;
                        nextCharFromCurrent5 = cArr2[i3];
                    } else {
                        nextCharFromCurrent5 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT);
                    }
                    c2 = nextCharFromCurrent5;
                } else {
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr3 = this.mInputBuffer;
                        int i4 = this.mInputPtr;
                        this.mInputPtr = i4 + 1;
                        c = cArr3[i4];
                    } else {
                        c = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT);
                    }
                    if (c != ':') {
                        reportWrongEndPrefix(expPrefix, expLocalName, i2);
                        return;
                    }
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr4 = this.mInputBuffer;
                        int i5 = this.mInputPtr;
                        this.mInputPtr = i5 + 1;
                        nextCharFromCurrent4 = cArr4[i5];
                    } else {
                        nextCharFromCurrent4 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT);
                    }
                    c2 = nextCharFromCurrent4;
                    break;
                }
            }
        }
        int len2 = expLocalName.length();
        int i6 = 0;
        while (c2 == expLocalName.charAt(i6)) {
            i6++;
            if (i6 < len2) {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr5 = this.mInputBuffer;
                    int i7 = this.mInputPtr;
                    this.mInputPtr = i7 + 1;
                    nextCharFromCurrent3 = cArr5[i7];
                } else {
                    nextCharFromCurrent3 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT);
                }
                c2 = nextCharFromCurrent3;
            } else {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr6 = this.mInputBuffer;
                    int i8 = this.mInputPtr;
                    this.mInputPtr = i8 + 1;
                    nextCharFromCurrent2 = cArr6[i8];
                } else {
                    nextCharFromCurrent2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT);
                }
                char c3 = nextCharFromCurrent2;
                if (c3 <= ' ') {
                    c3 = getNextInCurrAfterWS(ParsingErrorMsgs.SUFFIX_IN_CLOSE_ELEMENT, c3);
                } else if (c3 != '>' && (c3 == ':' || isNameChar(c3))) {
                    reportWrongEndElem(expPrefix, expLocalName, len2);
                }
                if (c3 != '>') {
                    throwUnexpectedChar(c3, " in end tag Expected '>'.");
                }
                int vld = this.mElementStack.validateEndElement();
                this.mVldContent = vld;
                this.mValidateText = vld == 3;
                if (this.mCurrDepth == this.mInputTopDepth) {
                    handleGreedyEntityProblem(this.mInput);
                }
                this.mCurrDepth--;
                return;
            }
        }
        reportWrongEndElem(expPrefix, expLocalName, i6);
    }

    private void reportExtraEndElem() throws XMLStreamException {
        String name = parseFNameForError();
        throwParseError("Unbalanced close tag </" + name + ">; no open start tag.");
    }

    private void reportWrongEndPrefix(String prefix, String localName, int done) throws XMLStreamException {
        this.mInputPtr--;
        String fullName = prefix + ":" + localName;
        String rest = parseFNameForError();
        String actName = fullName.substring(0, done) + rest;
        throwParseError("Unexpected close tag </" + actName + ">; expected </" + fullName + ">.");
    }

    private void reportWrongEndElem(String prefix, String localName, int done) throws XMLStreamException {
        String fullName;
        this.mInputPtr--;
        if (prefix != null && prefix.length() > 0) {
            fullName = prefix + ":" + localName;
            done += 1 + prefix.length();
        } else {
            fullName = localName;
        }
        String rest = parseFNameForError();
        String actName = fullName.substring(0, done) + rest;
        throwParseError("Unexpected close tag </" + actName + ">; expected </" + fullName + ">.");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private int nextFromTreeCommentOrCData() throws XMLStreamException {
        char c;
        char c2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_DOC);
        if (c2 == '[') {
            checkCData();
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                c = cArr[i];
            } else {
                c = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CDATA);
            }
            readCDataPrimary(c);
            return 12;
        }
        if (c2 == '-' && getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_DOC) == '-') {
            this.mTokenState = 1;
            return 5;
        }
        throwParseError("Unrecognized XML directive; expected CDATA or comment ('<![CDATA[' or '<!--').");
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:66:0x01db  */
    /* JADX WARN: Code duplicated, block: B:68:0x0202  */
    /* JADX WARN: Code duplicated, block: B:69:0x0206  */
    /* JADX WARN: Code duplicated, block: B:71:0x020b  */
    private int skipToken() throws XMLStreamException {
        int result;
        char nextCharFromCurrent;
        char nextCharFromCurrent2;
        switch (this.mCurrToken) {
            case 1:
            case 2:
            case 10:
            case 13:
            default:
                throw new IllegalStateException("Internal error: unexpected token " + tokenTypeDesc(this.mCurrToken));
            case 3:
                while (true) {
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr = this.mInputBuffer;
                        int i = this.mInputPtr;
                        this.mInputPtr = i + 1;
                        nextCharFromCurrent = cArr[i];
                    } else {
                        nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_PROC_INSTR);
                    }
                    char c = nextCharFromCurrent;
                    if (c == '?') {
                        do {
                            if (this.mInputPtr < this.mInputEnd) {
                                char[] cArr2 = this.mInputBuffer;
                                int i2 = this.mInputPtr;
                                this.mInputPtr = i2 + 1;
                                nextCharFromCurrent2 = cArr2[i2];
                            } else {
                                nextCharFromCurrent2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_PROC_INSTR);
                            }
                            c = nextCharFromCurrent2;
                        } while (c == '?');
                        if (c == '>') {
                            result = 0;
                            if (result < 1) {
                                this.mTokenInputRow = this.mCurrInputRow;
                                this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                                this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                                return result < 0 ? result : getNext();
                            }
                            this.mTokenInputRow = this.mCurrInputRow;
                            this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                            this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
                            return result;
                        }
                    }
                    if (c < ' ') {
                        if (c == '\n' || c == '\r') {
                            skipCRLF(c);
                        } else if (c != '\t') {
                            throwInvalidSpace(c);
                        }
                    }
                }
                break;
            case 4:
                result = skipTokenText(getNext());
                if (this.mCfgCoalesceText) {
                    result = skipCoalescedText(result);
                }
                if (result < 1) {
                    this.mTokenInputRow = this.mCurrInputRow;
                    this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                    this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                    if (result < 0) {
                    }
                }
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
                return result;
            case 5:
                skipCommentOrCData(ParsingErrorMsgs.SUFFIX_IN_COMMENT, '-', true);
                result = 0;
                if (result < 1) {
                    this.mTokenInputRow = this.mCurrInputRow;
                    this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                    this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                    if (result < 0) {
                    }
                }
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
                return result;
            case 6:
                while (true) {
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr3 = this.mInputBuffer;
                        int i3 = this.mInputPtr;
                        this.mInputPtr = i3 + 1;
                        char c2 = cArr3[i3];
                        if (c2 > ' ') {
                            result = c2;
                        } else if (c2 == '\n' || c2 == '\r') {
                            skipCRLF(c2);
                        } else if (c2 != ' ' && c2 != '\t') {
                            throwInvalidSpace(c2);
                        }
                    } else if (!loadMore()) {
                        result = -1;
                    }
                }
                if (result < 1) {
                    this.mTokenInputRow = this.mCurrInputRow;
                    this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                    this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                    if (result < 0) {
                    }
                }
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
                return result;
            case 7:
            case 8:
            case 9:
            case 14:
            case 15:
                throw new IllegalStateException("skipToken() called when current token is " + tokenTypeDesc(this.mCurrToken));
            case 11:
                finishDTD(false);
                result = 0;
                if (result < 1) {
                    this.mTokenInputRow = this.mCurrInputRow;
                    this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                    this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                    if (result < 0) {
                    }
                }
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
                return result;
            case 12:
                if (this.mTokenState <= 2) {
                    skipCommentOrCData(ParsingErrorMsgs.SUFFIX_IN_CDATA, ']', false);
                }
                result = getNext();
                if (this.mCfgCoalesceText) {
                    result = skipCoalescedText(result);
                }
                if (result < 1) {
                    this.mTokenInputRow = this.mCurrInputRow;
                    this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                    this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                    if (result < 0) {
                    }
                }
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputTotal = (this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1;
                this.mTokenInputCol = (this.mInputPtr - this.mCurrInputRowStart) - 1;
                return result;
        }
    }

    private void skipCommentOrCData(String errorMsg, char endChar, boolean preventDoubles) throws XMLStreamException {
        char c;
        char nextCharFromCurrent;
        int count = 0;
        while (true) {
            if (this.mInputPtr >= this.mInputEnd) {
                verifyLimit("Text size", this.mConfig.getMaxTextLength(), count);
                c = getNextCharFromCurrent(errorMsg);
            } else {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                c = cArr[i];
            }
            if (c < ' ') {
                if (c == '\n' || c == '\r') {
                    skipCRLF(c);
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
            } else if (c == endChar) {
                char c2 = getNextChar(errorMsg);
                if (c2 == endChar) {
                    c2 = getNextChar(errorMsg);
                    if (c2 != '>') {
                        if (preventDoubles) {
                            throwParseError("String '--' not allowed in comment (missing '>'?)");
                        }
                        while (c2 == endChar) {
                            if (this.mInputPtr < this.mInputEnd) {
                                char[] cArr2 = this.mInputBuffer;
                                int i2 = this.mInputPtr;
                                this.mInputPtr = i2 + 1;
                                nextCharFromCurrent = cArr2[i2];
                            } else {
                                nextCharFromCurrent = getNextCharFromCurrent(errorMsg);
                            }
                            c2 = nextCharFromCurrent;
                        }
                        if (c2 == '>') {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (c2 < ' ') {
                    if (c2 == '\n' || c2 == '\r') {
                        skipCRLF(c2);
                    } else if (c2 != '\t') {
                        throwInvalidSpace(c2);
                    }
                }
            }
            count++;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private int skipCoalescedText(int i) throws XMLStreamException {
        while (true) {
            if (i == 60) {
                if (!ensureInput(3)) {
                    return i;
                }
                if (this.mInputBuffer[this.mInputPtr] != '!' || this.mInputBuffer[this.mInputPtr + 1] != '[') {
                    return i;
                }
                this.mInputPtr += 2;
                checkCData();
                skipCommentOrCData(ParsingErrorMsgs.SUFFIX_IN_CDATA, ']', false);
                i = getNext();
            } else {
                if (i < 0) {
                    return i;
                }
                i = skipTokenText(i);
                if (i == 38 || i < 0) {
                    return i;
                }
            }
        }
    }

    private int skipTokenText(int i) throws XMLStreamException {
        int count = 0;
        while (i != 60) {
            if (i == 38) {
                if (this.mCfgReplaceEntities) {
                    if (this.mInputEnd - this.mInputPtr < 3 || resolveSimpleEntity(true) == 0) {
                        fullyResolveEntity(true);
                    }
                } else if (resolveCharOnlyEntity(true) == 0) {
                    return i;
                }
            } else if (i < 32) {
                if (i == 13 || i == 10) {
                    skipCRLF((char) i);
                } else {
                    if (i < 0) {
                        return i;
                    }
                    if (i != 9) {
                        throwInvalidSpace(i);
                    }
                }
            }
            count++;
            verifyLimit("Text size", this.mConfig.getMaxTextLength(), count);
            while (true) {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    char c = cArr[i2];
                    if (c < '?') {
                        i = c;
                        break;
                    }
                } else {
                    i = getNext();
                    break;
                }
            }
        }
        return i;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void ensureFinishToken() throws XMLStreamException {
        if (this.mTokenState < this.mStTextThreshold) {
            finishToken(false);
        }
    }

    protected void safeEnsureFinishToken() {
        if (this.mTokenState < this.mStTextThreshold) {
            safeFinishToken();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void safeFinishToken() {
        try {
            boolean deferErrors = this.mCurrToken == 4;
            finishToken(deferErrors);
        } catch (XMLStreamException strex) {
            throwLazyError(strex);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void finishToken(boolean deferErrors) throws XMLStreamException {
        switch (this.mCurrToken) {
            case 1:
            case 2:
            case 7:
            case 8:
            case 9:
            case 14:
            case 15:
                throw new IllegalStateException("finishToken() called when current token is " + tokenTypeDesc(this.mCurrToken));
            case 3:
                readPI();
                this.mTokenState = 4;
                return;
            case 4:
                if (this.mCfgCoalesceText) {
                    if (this.mTokenState == 3 && this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr + 1] != '!') {
                        this.mTokenState = 4;
                        return;
                    } else {
                        readCoalescedText(this.mCurrToken, deferErrors);
                        return;
                    }
                }
                if (readTextSecondary(this.mShortestTextSegment, deferErrors)) {
                    this.mTokenState = 3;
                    return;
                } else {
                    this.mTokenState = 2;
                    return;
                }
            case 5:
                readComment();
                this.mTokenState = 4;
                return;
            case 6:
                boolean prolog = this.mParseState != 1;
                readSpaceSecondary(prolog);
                this.mTokenState = 4;
                return;
            case 10:
            case 13:
            default:
                throw new IllegalStateException("Internal error: unexpected token " + tokenTypeDesc(this.mCurrToken));
            case 11:
                try {
                    finishDTD(true);
                    return;
                } finally {
                    this.mTokenState = 4;
                }
            case 12:
                if (this.mCfgCoalesceText) {
                    readCoalescedText(this.mCurrToken, deferErrors);
                    return;
                } else if (readCDataSecondary(WinNT.MAXLONG)) {
                    this.mTokenState = 3;
                    return;
                } else {
                    this.mTokenState = 2;
                    return;
                }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void readComment() throws XMLStreamException {
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int ptr = this.mInputPtr;
        while (ptr < inputLen) {
            int i = ptr;
            ptr++;
            char c = inputBuf[i];
            if (c <= '-') {
                if (c < ' ') {
                    if (c == '\n') {
                        markLF(ptr);
                    } else if (c == '\r') {
                        if (!this.mNormalizeLFs && ptr < inputLen) {
                            if (inputBuf[ptr] == '\n') {
                                ptr++;
                            }
                            markLF(ptr);
                        } else {
                            ptr--;
                            break;
                        }
                    } else if (c != '\t') {
                        throwInvalidSpace(c);
                    }
                } else if (c != '-') {
                    continue;
                } else if (ptr + 1 >= inputLen) {
                    ptr--;
                    break;
                } else if (inputBuf[ptr] == '-') {
                    if (inputBuf[ptr + 1] != '>') {
                        throwParseError("String '--' not allowed in comment (missing '>'?)");
                    }
                    this.mTextBuffer.resetWithShared(inputBuf, ptr, (ptr - ptr) - 1);
                    this.mInputPtr = ptr + 2;
                    return;
                }
            }
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithCopy(inputBuf, ptr, ptr - ptr);
        readComment2(this.mTextBuffer);
    }

    private void readComment2(TextBuffer tb) throws XMLStreamException {
        char nextCharFromCurrent;
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        int outLen = outBuf.length;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextCharFromCurrent = cArr[i];
            } else {
                nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_COMMENT);
            }
            char c = nextCharFromCurrent;
            if (c < ' ') {
                if (c == '\n') {
                    markLF();
                } else if (c == '\r') {
                    if (skipCRLF(c)) {
                        if (!this.mNormalizeLFs) {
                            if (outPtr >= outLen) {
                                outBuf = this.mTextBuffer.finishCurrentSegment();
                                outLen = outBuf.length;
                                outPtr = 0;
                            }
                            int i2 = outPtr;
                            outPtr++;
                            outBuf[i2] = c;
                        }
                        c = '\n';
                    } else if (this.mNormalizeLFs) {
                        c = '\n';
                    }
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
            } else if (c == '-') {
                if (getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_COMMENT) == '-') {
                    break;
                }
                c = '-';
                this.mInputPtr--;
            }
            if (outPtr >= outLen) {
                outBuf = this.mTextBuffer.finishCurrentSegment();
                outLen = outBuf.length;
                outPtr = 0;
                verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
            }
            int i3 = outPtr;
            outPtr++;
            outBuf[i3] = c;
        }
        if (getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_COMMENT) != '>') {
            throwParseError(ErrorConsts.ERR_HYPHENS_IN_COMMENT);
        }
        this.mTextBuffer.setCurrentLength(outPtr);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final int readPIPrimary() throws XMLStreamException {
        char nextCharFromCurrent;
        String target = parseFullName();
        this.mCurrName = target;
        if (target.length() == 0) {
            throwParseError(ErrorConsts.ERR_WF_PI_MISSING_TARGET);
        }
        if (target.equalsIgnoreCase("xml")) {
            if (!this.mConfig.inputParsingModeDocuments()) {
                throwParseError(ErrorConsts.ERR_WF_PI_XML_TARGET, target, null);
            }
            char c = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_XML_DECL);
            if (!isSpaceChar(c)) {
                throwUnexpectedChar(c, "expected a space in xml declaration after 'xml'");
            }
            return handleMultiDocStart(7);
        }
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            nextCharFromCurrent = cArr[i];
        } else {
            nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_PROC_INSTR);
        }
        char c2 = nextCharFromCurrent;
        if (isSpaceChar(c2)) {
            this.mTokenState = 1;
            skipWS(c2);
            return 3;
        }
        this.mTokenState = 4;
        this.mTextBuffer.resetWithEmpty();
        if (c2 != '?' || getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_PROC_INSTR) != '>') {
            throwUnexpectedChar(c2, ErrorConsts.ERR_WF_PI_XML_MISSING_SPACE);
            return 3;
        }
        return 3;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void readPI() throws XMLStreamException {
        char c;
        int ptr = this.mInputPtr;
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        loop0: while (ptr < inputLen) {
            int i = ptr;
            ptr++;
            char c2 = inputBuf[i];
            if (c2 < ' ') {
                if (c2 == '\n') {
                    markLF(ptr);
                } else if (c2 == '\r') {
                    if (ptr < inputLen && !this.mNormalizeLFs) {
                        if (inputBuf[ptr] == '\n') {
                            ptr++;
                        }
                        markLF(ptr);
                    } else {
                        ptr--;
                        break;
                    }
                } else if (c2 != '\t') {
                    throwInvalidSpace(c2);
                }
            } else if (c2 == '?') {
                do {
                    if (ptr >= inputLen) {
                        ptr--;
                        break loop0;
                    }
                    int i2 = ptr;
                    ptr++;
                    c = inputBuf[i2];
                    if (c == '>') {
                        this.mInputPtr = ptr;
                        this.mTextBuffer.resetWithShared(inputBuf, ptr, (ptr - ptr) - 2);
                        return;
                    }
                } while (c == '?');
                ptr--;
            } else {
                continue;
            }
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithCopy(inputBuf, ptr, ptr - ptr);
        readPI2(this.mTextBuffer);
    }

    private void readPI2(TextBuffer tb) throws XMLStreamException {
        char nextCharFromCurrent;
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int inputPtr = this.mInputPtr;
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        while (true) {
            if (inputPtr >= inputLen) {
                loadMoreFromCurrent(ParsingErrorMsgs.SUFFIX_IN_PROC_INSTR);
                inputBuf = this.mInputBuffer;
                inputPtr = this.mInputPtr;
                inputLen = this.mInputEnd;
            }
            int i = inputPtr;
            inputPtr++;
            char c = inputBuf[i];
            if (c < ' ') {
                if (c == '\n') {
                    markLF(inputPtr);
                } else if (c == '\r') {
                    this.mInputPtr = inputPtr;
                    if (skipCRLF(c)) {
                        if (!this.mNormalizeLFs) {
                            if (outPtr >= outBuf.length) {
                                outBuf = this.mTextBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int i2 = outPtr;
                            outPtr++;
                            outBuf[i2] = c;
                        }
                        c = '\n';
                    } else if (this.mNormalizeLFs) {
                        c = '\n';
                    }
                    inputPtr = this.mInputPtr;
                    inputBuf = this.mInputBuffer;
                    inputLen = this.mInputEnd;
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
            } else if (c == '?') {
                this.mInputPtr = inputPtr;
                while (true) {
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr = this.mInputBuffer;
                        int i3 = this.mInputPtr;
                        this.mInputPtr = i3 + 1;
                        nextCharFromCurrent = cArr[i3];
                    } else {
                        nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_PROC_INSTR);
                    }
                    char c2 = nextCharFromCurrent;
                    if (c2 != '>') {
                        if (c2 == '?') {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int i4 = outPtr;
                            outPtr++;
                            outBuf[i4] = c2;
                        } else {
                            int i5 = this.mInputPtr - 1;
                            this.mInputPtr = i5;
                            inputPtr = i5;
                            inputBuf = this.mInputBuffer;
                            inputLen = this.mInputEnd;
                            c = '?';
                            break;
                        }
                    } else {
                        tb.setCurrentLength(outPtr);
                        return;
                    }
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            int i6 = outPtr;
            outPtr++;
            outBuf[i6] = c;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:24:0x0082  */
    /* JADX WARN: Code duplicated, block: B:28:0x00a2  */
    /* JADX WARN: Code duplicated, block: B:31:0x00b4  */
    /* JADX WARN: Code duplicated, block: B:33:0x00c4  */
    /* JADX WARN: Code duplicated, block: B:46:0x010c A[ADDED_TO_REGION, EDGE_INSN: B:46:0x010c->B:44:0x010c BREAK  A[LOOP:0: B:15:0x005b->B:54:0x005b], REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:50:0x00f1 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:51:0x010c A[EDGE_INSN: B:51:0x010c->B:44:0x010c BREAK  A[LOOP:0: B:15:0x005b->B:54:0x005b], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:53:0x0094 A[SYNTHETIC] */
    protected void readCoalescedText(int currType, boolean deferErrors) throws XMLStreamException {
        boolean z;
        char c;
        if (currType == 4 || currType == 6) {
            readTextSecondary(WinNT.MAXLONG, deferErrors);
            z = false;
        } else if (currType == 12) {
            if (this.mTokenState <= 2) {
                readCDataSecondary(WinNT.MAXLONG);
            }
            z = true;
        } else {
            throw new IllegalStateException("Internal error: unexpected token " + tokenTypeDesc(this.mCurrToken) + "; expected CHARACTERS, CDATA or SPACE.");
        }
        while (true) {
            boolean wasCData = z;
            if (deferErrors && this.mPendingException != null) {
                break;
            }
            if (this.mInputPtr >= this.mInputEnd) {
                this.mTextBuffer.ensureNotShared();
                if (!loadMore()) {
                    break;
                }
                c = this.mInputBuffer[this.mInputPtr];
                if (c == '<') {
                    if (this.mInputEnd - this.mInputPtr < 9) {
                        this.mTextBuffer.ensureNotShared();
                        if (ensureInput(3)) {
                            break;
                        }
                        if (this.mInputBuffer[this.mInputPtr + 1] != '!' || this.mInputBuffer[this.mInputPtr + 2] != '[') {
                            break;
                        }
                        this.mInputPtr += 3;
                        checkCData();
                        readCDataSecondary(WinNT.MAXLONG);
                        z = true;
                    } else {
                        if (this.mInputBuffer[this.mInputPtr + 1] != '!') {
                            break;
                        }
                        break;
                        break;
                    }
                } else {
                    if (c != '&' && !wasCData) {
                        break;
                    }
                    readTextSecondary(WinNT.MAXLONG, deferErrors);
                    z = false;
                }
            } else {
                c = this.mInputBuffer[this.mInputPtr];
                if (c == '<') {
                    if (this.mInputEnd - this.mInputPtr < 9) {
                        this.mTextBuffer.ensureNotShared();
                        if (ensureInput(3)) {
                            if (this.mInputBuffer[this.mInputPtr + 1] != '!') {
                                break;
                                break;
                            } else {
                                break;
                                break;
                            }
                        }
                        break;
                        break;
                    }
                    if (this.mInputBuffer[this.mInputPtr + 1] != '!') {
                        break;
                        break;
                    } else {
                        break;
                        break;
                    }
                }
                if (c != '&') {
                }
                readTextSecondary(WinNT.MAXLONG, deferErrors);
                z = false;
            }
        }
        this.mTokenState = 4;
    }

    /* JADX WARN: Code duplicated, block: B:51:0x00f7 A[LOOP:0: B:7:0x0024->B:51:0x00f7, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:64:0x0102 A[EDGE_INSN: B:64:0x0102->B:52:0x0102 BREAK  A[LOOP:0: B:7:0x0024->B:51:0x00f7], SYNTHETIC] */
    private final boolean readCDataPrimary(char c) throws XMLStreamException {
        this.mWsStatus = c <= ' ' ? 0 : 2;
        int ptr = this.mInputPtr;
        int inputLen = this.mInputEnd;
        char[] inputBuf = this.mInputBuffer;
        int start = ptr - 1;
        while (true) {
            if (c < ' ') {
                if (c == '\n') {
                    markLF(ptr);
                } else if (c == '\r') {
                    if (ptr >= inputLen) {
                        ptr--;
                        break;
                    }
                    if (this.mNormalizeLFs) {
                        if (inputBuf[ptr] == '\n') {
                            ptr--;
                            break;
                        }
                        inputBuf[ptr - 1] = '\n';
                    } else if (inputBuf[ptr] == '\n') {
                        ptr++;
                    }
                    markLF(ptr);
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
                if (ptr >= inputLen) {
                    break;
                }
                int i = ptr;
                ptr++;
                c = inputBuf[i];
            } else if (c == ']') {
                if (ptr + 1 >= inputLen) {
                    ptr--;
                    break;
                }
                if (inputBuf[ptr] == ']') {
                    int ptr2 = ptr + 1;
                    while (true) {
                        if (ptr2 >= inputLen) {
                            ptr = ptr2 - 2;
                            break;
                        }
                        int i2 = ptr2;
                        ptr2++;
                        char c2 = inputBuf[i2];
                        if (c2 == '>') {
                            this.mInputPtr = ptr2;
                            this.mTextBuffer.resetWithShared(inputBuf, start, ptr2 - (start + 3));
                            this.mTokenState = 3;
                            return true;
                        }
                        if (c2 != ']') {
                            ptr = ptr2 - 1;
                            break;
                        }
                    }
                }
                if (ptr >= inputLen) {
                    break;
                    break;
                }
                int i3 = ptr;
                ptr++;
                c = inputBuf[i3];
            } else {
                if (ptr >= inputLen) {
                    break;
                    break;
                }
                int i4 = ptr;
                ptr++;
                c = inputBuf[i4];
            }
        }
        this.mInputPtr = ptr;
        int len = ptr - start;
        this.mTextBuffer.resetWithShared(inputBuf, start, len);
        if (this.mCfgCoalesceText || this.mTextBuffer.size() < this.mShortestTextSegment) {
            this.mTokenState = 1;
            return false;
        }
        this.mTokenState = 2;
        return false;
    }

    protected boolean readCDataSecondary(int shortestSegment) throws XMLStreamException {
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int inputPtr = this.mInputPtr;
        char[] outBuf = this.mTextBuffer.getCurrentSegment();
        int outPtr = this.mTextBuffer.getCurrentSegmentSize();
        while (true) {
            if (inputPtr >= inputLen) {
                loadMore(ParsingErrorMsgs.SUFFIX_IN_CDATA);
                inputBuf = this.mInputBuffer;
                inputPtr = this.mInputPtr;
                inputLen = this.mInputEnd;
            }
            int i = inputPtr;
            inputPtr++;
            char c = inputBuf[i];
            if (c < ' ') {
                if (c == '\n') {
                    markLF(inputPtr);
                } else if (c == '\r') {
                    this.mInputPtr = inputPtr;
                    if (skipCRLF(c)) {
                        if (!this.mNormalizeLFs) {
                            int i2 = outPtr;
                            outPtr++;
                            outBuf[i2] = c;
                            if (outPtr >= outBuf.length) {
                                outBuf = this.mTextBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                        }
                        c = '\n';
                    } else if (this.mNormalizeLFs) {
                        c = '\n';
                    }
                    inputPtr = this.mInputPtr;
                    inputBuf = this.mInputBuffer;
                    inputLen = this.mInputEnd;
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
            } else if (c == ']') {
                this.mInputPtr = inputPtr;
                if (checkCDataEnd(outBuf, outPtr)) {
                    return true;
                }
                inputPtr = this.mInputPtr;
                inputBuf = this.mInputBuffer;
                inputLen = this.mInputEnd;
                outBuf = this.mTextBuffer.getCurrentSegment();
                outPtr = this.mTextBuffer.getCurrentSegmentSize();
            }
            int i3 = outPtr;
            outPtr++;
            outBuf[i3] = c;
            if (outPtr >= outBuf.length) {
                TextBuffer tb = this.mTextBuffer;
                if (!this.mCfgCoalesceText) {
                    tb.setCurrentLength(outBuf.length);
                    if (tb.size() >= shortestSegment) {
                        this.mInputPtr = inputPtr;
                        return false;
                    }
                }
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
                verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
            } else {
                continue;
            }
        }
    }

    private boolean checkCDataEnd(char[] outBuf, int outPtr) throws XMLStreamException {
        char nextCharFromCurrent;
        char c;
        int bracketCount = 0;
        do {
            bracketCount++;
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextCharFromCurrent = cArr[i];
            } else {
                nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CDATA);
            }
            c = nextCharFromCurrent;
        } while (c == ']');
        boolean match = bracketCount >= 2 && c == '>';
        if (match) {
            bracketCount -= 2;
        }
        while (bracketCount > 0) {
            bracketCount--;
            int i2 = outPtr;
            outPtr++;
            outBuf[i2] = ']';
            if (outPtr >= outBuf.length) {
                outBuf = this.mTextBuffer.finishCurrentSegment();
                outPtr = 0;
            }
        }
        this.mTextBuffer.setCurrentLength(outPtr);
        if (match) {
            return true;
        }
        this.mInputPtr--;
        return false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:21:0x0063  */
    /* JADX WARN: Code duplicated, block: B:23:0x006e A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:79:0x01a0 A[LOOP:0: B:29:0x0093->B:79:0x01a0, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:84:0x01ab A[EDGE_INSN: B:84:0x01ab->B:80:0x01ab BREAK  A[LOOP:0: B:29:0x0093->B:79:0x01a0], SYNTHETIC] */
    private final boolean readTextPrimary(char c) throws XMLStreamException {
        int ptr = this.mInputPtr;
        int start = ptr - 1;
        if (c <= ' ') {
            int len = this.mInputEnd;
            if (ptr < len && this.mNormalizeLFs) {
                if (c == '\r') {
                    c = '\n';
                    if (this.mInputBuffer[ptr] != '\n') {
                        this.mInputBuffer[start] = '\n';
                    } else {
                        start++;
                        ptr++;
                        if (ptr < len) {
                        }
                    }
                    markLF(ptr);
                    if (this.mCheckIndentation > 0) {
                        ptr = readIndentation(c, ptr);
                        if (ptr < 0) {
                            return true;
                        }
                    }
                    int i = ptr;
                    ptr++;
                    c = this.mInputBuffer[i];
                } else if (c == '\n') {
                    markLF(ptr);
                    if (this.mCheckIndentation > 0) {
                        ptr = readIndentation(c, ptr);
                        if (ptr < 0) {
                            return true;
                        }
                    }
                    int i2 = ptr;
                    ptr++;
                    c = this.mInputBuffer[i2];
                }
            }
            this.mWsStatus = 0;
        } else {
            this.mWsStatus = 2;
        }
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        while (true) {
            if (c < '?') {
                if (c == '<') {
                    int ptr2 = ptr - 1;
                    this.mInputPtr = ptr2;
                    this.mTextBuffer.resetWithShared(inputBuf, start, ptr2 - start);
                    return true;
                }
                if (c < ' ') {
                    if (c == '\n') {
                        markLF(ptr);
                    } else if (c == '\r') {
                        if (ptr >= inputLen) {
                            ptr--;
                            break;
                        }
                        if (this.mNormalizeLFs) {
                            if (inputBuf[ptr] == '\n') {
                                ptr--;
                                break;
                            }
                            inputBuf[ptr - 1] = '\n';
                        } else if (inputBuf[ptr] == '\n') {
                            ptr++;
                        }
                        markLF(ptr);
                    } else if (c != '\t') {
                        this.mInputPtr = ptr;
                        this.mTextBuffer.resetWithShared(inputBuf, start, (ptr - start) - 1);
                        boolean deferErrors = ptr - start > 1;
                        this.mPendingException = throwInvalidSpace(c, deferErrors);
                        return true;
                    }
                    if (ptr >= inputLen) {
                        break;
                        break;
                    }
                    int i3 = ptr;
                    ptr++;
                    c = inputBuf[i3];
                } else {
                    if (c == '&') {
                        ptr--;
                        break;
                    }
                    if (c == '>' && ptr - start >= 3 && inputBuf[ptr - 3] == ']' && inputBuf[ptr - 2] == ']') {
                        this.mInputPtr = ptr;
                        this.mTextBuffer.resetWithShared(inputBuf, start, (ptr - start) - 1);
                        this.mPendingException = throwWfcException(ErrorConsts.ERR_BRACKET_IN_TEXT, true);
                        return true;
                    }
                    if (ptr >= inputLen) {
                        break;
                        break;
                    }
                    int i4 = ptr;
                    ptr++;
                    c = inputBuf[i4];
                }
            } else {
                if (ptr >= inputLen) {
                    break;
                }
                int i5 = ptr;
                ptr++;
                c = inputBuf[i5];
            }
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start);
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:101:0x0023 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:11:0x005d  */
    /* JADX WARN: Code duplicated, block: B:15:0x006b  */
    /* JADX WARN: Code duplicated, block: B:16:0x0074  */
    /* JADX WARN: Code duplicated, block: B:18:0x007b  */
    /* JADX WARN: Code duplicated, block: B:20:0x008a  */
    /* JADX WARN: Code duplicated, block: B:22:0x0091  */
    /* JADX WARN: Code duplicated, block: B:24:0x00a1  */
    /* JADX WARN: Code duplicated, block: B:26:0x00b3  */
    /* JADX WARN: Code duplicated, block: B:28:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:30:0x00cd  */
    /* JADX WARN: Code duplicated, block: B:33:0x00f1  */
    /* JADX WARN: Code duplicated, block: B:36:0x0103  */
    /* JADX WARN: Code duplicated, block: B:38:0x010a  */
    /* JADX WARN: Code duplicated, block: B:40:0x0117  */
    /* JADX WARN: Code duplicated, block: B:42:0x0120  */
    /* JADX WARN: Code duplicated, block: B:45:0x012e  */
    /* JADX WARN: Code duplicated, block: B:53:0x016f  */
    /* JADX WARN: Code duplicated, block: B:54:0x0177  */
    /* JADX WARN: Code duplicated, block: B:56:0x0185  */
    /* JADX WARN: Code duplicated, block: B:59:0x01a7  */
    /* JADX WARN: Code duplicated, block: B:63:0x01b7  */
    /* JADX WARN: Code duplicated, block: B:81:0x0237  */
    /* JADX WARN: Code duplicated, block: B:85:0x00f8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:86:0x01d5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:88:0x014f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:89:0x0218 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:90:0x0168 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:91:0x0064 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:92:0x0168 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:93:0x00d4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:94:0x013a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:95:0x015b A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:96:0x01b5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:98:0x0228 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:0x0235 A[SYNTHETIC] */
    protected final boolean readTextSecondary(int shortestSegment, boolean deferErrors) throws XMLStreamException {
        char c;
        char[] cArr_expandOutputForText;
        int ch;
        char[] cArr_expandOutputForText2;
        int iResolveSimpleEntity;
        char[] outBuf = this.mTextBuffer.getCurrentSegment();
        int outPtr = this.mTextBuffer.getCurrentSegmentSize();
        int inputPtr = this.mInputPtr;
        char[] inputBuffer = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        while (true) {
            if (inputPtr >= inputLen) {
                this.mInputPtr = inputPtr;
                if (!loadMore()) {
                    break;
                }
                inputPtr = this.mInputPtr;
                inputBuffer = this.mInputBuffer;
                inputLen = this.mInputEnd;
                int i = inputPtr;
                inputPtr++;
                c = inputBuffer[i];
                if (c >= '?') {
                    if (c < ' ') {
                        if (c == '\n') {
                            markLF(inputPtr);
                        } else if (c == '\r') {
                            this.mInputPtr = inputPtr;
                            if (skipCRLF(c)) {
                                if (!this.mNormalizeLFs) {
                                    int i2 = outPtr;
                                    outPtr++;
                                    outBuf[i2] = c;
                                    if (outPtr >= outBuf.length) {
                                        outBuf = this.mTextBuffer.finishCurrentSegment();
                                        outPtr = 0;
                                    }
                                }
                                c = '\n';
                            } else if (this.mNormalizeLFs) {
                                c = '\n';
                            }
                            inputLen = this.mInputEnd;
                            inputPtr = this.mInputPtr;
                        } else if (c != '\t') {
                            this.mTextBuffer.setCurrentLength(outPtr);
                            this.mInputPtr = inputPtr;
                            this.mPendingException = throwInvalidSpace(c, deferErrors);
                            break;
                        }
                    } else {
                        if (c == '<') {
                            this.mInputPtr = inputPtr - 1;
                            break;
                        }
                        if (c != '&') {
                            this.mInputPtr = inputPtr;
                            if (this.mCfgReplaceEntities) {
                                if (inputLen - inputPtr >= 3) {
                                    iResolveSimpleEntity = resolveSimpleEntity(true);
                                    ch = iResolveSimpleEntity;
                                    if (iResolveSimpleEntity != 0) {
                                        ch = fullyResolveEntity(true);
                                        if (ch == 0) {
                                            inputBuffer = this.mInputBuffer;
                                            inputLen = this.mInputEnd;
                                            inputPtr = this.mInputPtr;
                                        }
                                    }
                                } else {
                                    ch = fullyResolveEntity(true);
                                    if (ch == 0) {
                                        inputBuffer = this.mInputBuffer;
                                        inputLen = this.mInputEnd;
                                        inputPtr = this.mInputPtr;
                                    }
                                }
                            } else {
                                ch = resolveCharOnlyEntity(true);
                                if (ch == 0) {
                                    this.mInputPtr--;
                                    break;
                                }
                            }
                            if (ch <= 65535) {
                                c = (char) ch;
                            } else {
                                int ch2 = ch - 65536;
                                if (outPtr >= outBuf.length) {
                                    outBuf = this.mTextBuffer.finishCurrentSegment();
                                    outPtr = 0;
                                }
                                int i3 = outPtr;
                                outPtr++;
                                outBuf[i3] = (char) ((ch2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                                if (outPtr >= outBuf.length) {
                                    cArr_expandOutputForText2 = _expandOutputForText(inputPtr, outBuf, WinNT.MAXLONG);
                                    outBuf = cArr_expandOutputForText2;
                                    if (cArr_expandOutputForText2 == null) {
                                        return false;
                                    }
                                    outPtr = 0;
                                }
                                c = (char) ((ch2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                            }
                            inputPtr = this.mInputPtr;
                            inputLen = this.mInputEnd;
                        } else if (c == '>' && inputPtr > 2 && inputBuffer[inputPtr - 3] == ']' && inputBuffer[inputPtr - 2] == ']') {
                            this.mInputPtr = inputPtr;
                            this.mTextBuffer.setCurrentLength(outPtr);
                            this.mPendingException = throwWfcException(ErrorConsts.ERR_BRACKET_IN_TEXT, deferErrors);
                            break;
                        }
                    }
                }
                int i4 = outPtr;
                outPtr++;
                outBuf[i4] = c;
                if (outPtr >= outBuf.length) {
                    cArr_expandOutputForText = _expandOutputForText(inputPtr, outBuf, shortestSegment);
                    outBuf = cArr_expandOutputForText;
                    if (cArr_expandOutputForText == null) {
                        return false;
                    }
                    verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
                    outPtr = 0;
                } else {
                    continue;
                }
            } else {
                int i5 = inputPtr;
                inputPtr++;
                c = inputBuffer[i5];
                if (c >= '?') {
                    if (c < ' ') {
                        if (c == '\n') {
                            markLF(inputPtr);
                        } else if (c == '\r') {
                            this.mInputPtr = inputPtr;
                            if (skipCRLF(c)) {
                                if (!this.mNormalizeLFs) {
                                    int i6 = outPtr;
                                    outPtr++;
                                    outBuf[i6] = c;
                                    if (outPtr >= outBuf.length) {
                                        outBuf = this.mTextBuffer.finishCurrentSegment();
                                        outPtr = 0;
                                    }
                                }
                                c = '\n';
                            } else if (this.mNormalizeLFs) {
                                c = '\n';
                            }
                            inputLen = this.mInputEnd;
                            inputPtr = this.mInputPtr;
                        } else if (c != '\t') {
                            this.mTextBuffer.setCurrentLength(outPtr);
                            this.mInputPtr = inputPtr;
                            this.mPendingException = throwInvalidSpace(c, deferErrors);
                            break;
                        }
                    } else {
                        if (c == '<') {
                            this.mInputPtr = inputPtr - 1;
                            break;
                        }
                        if (c != '&') {
                            if (c == '>') {
                                this.mInputPtr = inputPtr;
                                this.mTextBuffer.setCurrentLength(outPtr);
                                this.mPendingException = throwWfcException(ErrorConsts.ERR_BRACKET_IN_TEXT, deferErrors);
                                break;
                            }
                        } else {
                            this.mInputPtr = inputPtr;
                            if (this.mCfgReplaceEntities) {
                                if (inputLen - inputPtr >= 3) {
                                    iResolveSimpleEntity = resolveSimpleEntity(true);
                                    ch = iResolveSimpleEntity;
                                    if (iResolveSimpleEntity != 0) {
                                        ch = fullyResolveEntity(true);
                                        if (ch == 0) {
                                            inputBuffer = this.mInputBuffer;
                                            inputLen = this.mInputEnd;
                                            inputPtr = this.mInputPtr;
                                        }
                                    }
                                } else {
                                    ch = fullyResolveEntity(true);
                                    if (ch == 0) {
                                        inputBuffer = this.mInputBuffer;
                                        inputLen = this.mInputEnd;
                                        inputPtr = this.mInputPtr;
                                    }
                                }
                            } else {
                                ch = resolveCharOnlyEntity(true);
                                if (ch == 0) {
                                    this.mInputPtr--;
                                    break;
                                }
                            }
                            if (ch <= 65535) {
                                c = (char) ch;
                            } else {
                                int ch3 = ch - 65536;
                                if (outPtr >= outBuf.length) {
                                    outBuf = this.mTextBuffer.finishCurrentSegment();
                                    outPtr = 0;
                                }
                                int i7 = outPtr;
                                outPtr++;
                                outBuf[i7] = (char) ((ch3 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                                if (outPtr >= outBuf.length) {
                                    cArr_expandOutputForText2 = _expandOutputForText(inputPtr, outBuf, WinNT.MAXLONG);
                                    outBuf = cArr_expandOutputForText2;
                                    if (cArr_expandOutputForText2 == null) {
                                        return false;
                                    }
                                    outPtr = 0;
                                }
                                c = (char) ((ch3 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                            }
                            inputPtr = this.mInputPtr;
                            inputLen = this.mInputEnd;
                        }
                    }
                }
                int i8 = outPtr;
                outPtr++;
                outBuf[i8] = c;
                if (outPtr >= outBuf.length) {
                    cArr_expandOutputForText = _expandOutputForText(inputPtr, outBuf, shortestSegment);
                    outBuf = cArr_expandOutputForText;
                    if (cArr_expandOutputForText == null) {
                        return false;
                    }
                    verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
                    outPtr = 0;
                } else {
                    continue;
                }
            }
        }
        this.mTextBuffer.setCurrentLength(outPtr);
        return true;
    }

    private final char[] _expandOutputForText(int inputPtr, char[] outBuf, int shortestSegment) {
        TextBuffer tb = this.mTextBuffer;
        tb.setCurrentLength(outBuf.length);
        if (tb.size() >= shortestSegment) {
            this.mInputPtr = inputPtr;
            return null;
        }
        return tb.finishCurrentSegment();
    }

    /* JADX WARN: Code duplicated, block: B:27:0x0082 A[PHI: r6
      0x0082: PHI (r6v6 'ptr' int) = (r6v4 'ptr' int), (r6v1 'ptr' int) binds: [B:19:0x0064, B:25:0x0079] A[DONT_GENERATE, DONT_INLINE]] */
    private final int readIndentation(char c, int ptr) throws XMLStreamException {
        int ptr2;
        int inputLen = this.mInputEnd;
        char[] inputBuf = this.mInputBuffer;
        int start = ptr - 1;
        int ptr3 = ptr + 1;
        char c2 = inputBuf[ptr];
        if (c2 == ' ' || c2 == '\t') {
            int lastIndCharPos = (c2 == ' ' ? 32 : 8) + ptr3;
            if (lastIndCharPos > inputLen) {
                lastIndCharPos = inputLen;
            }
            while (true) {
                if (ptr3 >= lastIndCharPos) {
                    ptr2 = ptr3 - 1;
                } else {
                    int i = ptr3;
                    ptr3++;
                    char d = inputBuf[i];
                    if (d != c2) {
                        if (d != '<') {
                            ptr2 = ptr3 - 1;
                        } else {
                            if (ptr3 >= inputLen && inputBuf[ptr3] != '!') {
                                int ptr4 = ptr3 - 1;
                                this.mInputPtr = ptr4;
                                this.mTextBuffer.resetWithIndentation((ptr4 - start) - 1, c2);
                                if (this.mCheckIndentation < 40) {
                                    this.mCheckIndentation += 16;
                                }
                                this.mWsStatus = 1;
                                return -1;
                            }
                            ptr2 = ptr3 - 1;
                        }
                    }
                }
            }
        } else if (c2 != '<') {
            ptr2 = ptr3 - 1;
        } else {
            if (ptr3 >= inputLen) {
            }
            ptr2 = ptr3 - 1;
        }
        this.mCheckIndentation--;
        if (c == '\r') {
            inputBuf[start] = '\n';
        }
        return ptr2;
    }

    private final boolean readSpacePrimary(char c, boolean prologWS) throws XMLStreamException {
        int ptr = this.mInputPtr;
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int start = ptr - 1;
        while (c <= ' ') {
            if (c == '\n') {
                markLF(ptr);
            } else {
                if (c == '\r') {
                    if (ptr >= this.mInputEnd) {
                        ptr--;
                    } else {
                        if (this.mNormalizeLFs) {
                            if (inputBuf[ptr] == '\n') {
                                ptr--;
                            } else {
                                inputBuf[ptr - 1] = '\n';
                            }
                        } else if (inputBuf[ptr] == '\n') {
                            ptr++;
                        }
                        markLF(ptr);
                    }
                    this.mInputPtr = ptr;
                    this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start);
                    return false;
                }
                if (c != ' ' && c != '\t') {
                    throwInvalidSpace(c);
                }
            }
            if (ptr < inputLen) {
                int i = ptr;
                ptr++;
                c = inputBuf[i];
            } else {
                this.mInputPtr = ptr;
                this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start);
                return false;
            }
        }
        int ptr2 = ptr - 1;
        this.mInputPtr = ptr2;
        this.mTextBuffer.resetWithShared(this.mInputBuffer, start, ptr2 - start);
        return true;
    }

    private void readSpaceSecondary(boolean prologWS) throws XMLStreamException {
        char[] outBuf = this.mTextBuffer.getCurrentSegment();
        int outPtr = this.mTextBuffer.getCurrentSegmentSize();
        while (true) {
            if (this.mInputPtr >= this.mInputEnd && !loadMore()) {
                break;
            }
            char c = this.mInputBuffer[this.mInputPtr];
            if (c > ' ') {
                break;
            }
            this.mInputPtr++;
            if (c == '\n') {
                markLF();
            } else if (c == '\r') {
                if (skipCRLF(c)) {
                    if (!this.mNormalizeLFs) {
                        int i = outPtr;
                        outPtr++;
                        outBuf[i] = c;
                        if (outPtr >= outBuf.length) {
                            outBuf = this.mTextBuffer.finishCurrentSegment();
                            outPtr = 0;
                        }
                    }
                    c = '\n';
                } else if (this.mNormalizeLFs) {
                    c = '\n';
                }
            } else if (c != ' ' && c != '\t') {
                throwInvalidSpace(c);
            }
            int i2 = outPtr;
            outPtr++;
            outBuf[i2] = c;
            if (outPtr >= outBuf.length) {
                outBuf = this.mTextBuffer.finishCurrentSegment();
                outPtr = 0;
            }
        }
        this.mTextBuffer.setCurrentLength(outPtr);
    }

    /* JADX WARN: Code duplicated, block: B:69:0x01bb  */
    private int readAndWriteText(Writer w) throws XMLStreamException, IOException {
        char c;
        char d;
        int ch;
        char c2;
        this.mTokenState = 3;
        int start = this.mInputPtr;
        int count = 0;
        while (true) {
            if (this.mInputPtr >= this.mInputEnd) {
                int len = this.mInputPtr - start;
                if (len > 0) {
                    w.write(this.mInputBuffer, start, len);
                    count += len;
                }
                c = getNextChar(ParsingErrorMsgs.SUFFIX_IN_TEXT);
                start = this.mInputPtr - 1;
            } else {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                c = cArr[i];
            }
            if (c < '?') {
                if (c < ' ') {
                    if (c == '\n') {
                        markLF();
                    } else if (c == '\r') {
                        boolean atBoundary = this.mInputPtr >= this.mInputEnd;
                        if (atBoundary) {
                            int len2 = (this.mInputPtr - start) - 1;
                            if (len2 > 0) {
                                w.write(this.mInputBuffer, start, len2);
                                count += len2;
                            }
                            d = getNextChar(ParsingErrorMsgs.SUFFIX_IN_TEXT);
                            start = this.mInputPtr;
                        } else {
                            char[] cArr2 = this.mInputBuffer;
                            int i2 = this.mInputPtr;
                            this.mInputPtr = i2 + 1;
                            d = cArr2[i2];
                        }
                        if (d == '\n') {
                            if (this.mNormalizeLFs) {
                                int len3 = (this.mInputPtr - start) - 2;
                                if (len3 > 0) {
                                    w.write(this.mInputBuffer, start, len3);
                                    count += len3;
                                }
                                start = this.mInputPtr - 1;
                            } else if (atBoundary) {
                                w.write(c);
                            }
                        } else {
                            this.mInputPtr--;
                            if (atBoundary) {
                                w.write(this.mNormalizeLFs ? '\n' : c);
                                count++;
                            } else if (this.mNormalizeLFs) {
                                this.mInputBuffer[this.mInputPtr - 1] = '\n';
                            }
                        }
                        markLF();
                    } else if (c != '\t') {
                        throwInvalidSpace(c);
                    }
                } else {
                    if (c == '<') {
                        break;
                    }
                    if (c == '&') {
                        int len4 = (this.mInputPtr - 1) - start;
                        if (len4 > 0) {
                            w.write(this.mInputBuffer, start, len4);
                            count += len4;
                        }
                        if (this.mCfgReplaceEntities) {
                            if (this.mInputEnd - this.mInputPtr >= 3) {
                                int iResolveSimpleEntity = resolveSimpleEntity(true);
                                ch = iResolveSimpleEntity;
                                if (iResolveSimpleEntity == 0) {
                                    ch = fullyResolveEntity(true);
                                }
                            } else {
                                ch = fullyResolveEntity(true);
                            }
                        } else {
                            ch = resolveCharOnlyEntity(true);
                            if (ch == 0) {
                                start = this.mInputPtr;
                                break;
                            }
                        }
                        if (ch != 0) {
                            if (ch <= 65535) {
                                c2 = (char) ch;
                            } else {
                                int ch2 = ch - 65536;
                                w.write((char) ((ch2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST));
                                c2 = (char) ((ch2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                            }
                            w.write(c2);
                            count++;
                        }
                        start = this.mInputPtr;
                    } else if (c == '>' && this.mInputPtr >= 2 && this.mInputBuffer[this.mInputPtr - 2] == ']' && this.mInputBuffer[this.mInputPtr - 1] == ']') {
                        int len5 = this.mInputPtr - start;
                        if (len5 > 0) {
                            w.write(this.mInputBuffer, start, len5);
                        }
                        throwParseError(ErrorConsts.ERR_BRACKET_IN_TEXT);
                    }
                }
            }
        }
        this.mInputPtr--;
        int len6 = this.mInputPtr - start;
        if (len6 > 0) {
            w.write(this.mInputBuffer, start, len6);
            count += len6;
        }
        return count;
    }

    private int readAndWriteCData(Writer w) throws XMLStreamException, IOException {
        char nextChar;
        char d;
        char nextCharFromCurrent;
        boolean match;
        this.mTokenState = 3;
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            nextChar = cArr[i];
        } else {
            nextChar = getNextChar(ParsingErrorMsgs.SUFFIX_IN_CDATA);
        }
        char c = nextChar;
        int count = 0;
        do {
            int start = this.mInputPtr - 1;
            while (true) {
                if (c >= ' ') {
                    if (c == ']') {
                        break;
                    }
                } else if (c == '\n') {
                    markLF();
                } else if (c == '\r') {
                    if (this.mInputPtr >= this.mInputEnd) {
                        int len = this.mInputPtr - start;
                        if (len > 0) {
                            w.write(this.mInputBuffer, start, len);
                            count += len;
                        }
                        d = getNextChar(ParsingErrorMsgs.SUFFIX_IN_CDATA);
                        start = this.mInputPtr;
                    } else {
                        char[] cArr2 = this.mInputBuffer;
                        int i2 = this.mInputPtr;
                        this.mInputPtr = i2 + 1;
                        d = cArr2[i2];
                    }
                    if (d == '\n') {
                        if (this.mNormalizeLFs) {
                            int len2 = (this.mInputPtr - 2) - start;
                            if (len2 > 0) {
                                w.write(this.mInputBuffer, start, len2);
                                count += len2;
                            }
                            start = this.mInputPtr - 1;
                        }
                    } else {
                        this.mInputPtr--;
                        if (this.mNormalizeLFs) {
                            this.mInputBuffer[this.mInputPtr - 1] = '\n';
                        }
                    }
                    markLF();
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
                if (this.mInputPtr >= this.mInputEnd) {
                    int len3 = this.mInputPtr - start;
                    if (len3 > 0) {
                        w.write(this.mInputBuffer, start, len3);
                        count += len3;
                    }
                    start = 0;
                    c = getNextChar(ParsingErrorMsgs.SUFFIX_IN_CDATA);
                } else {
                    char[] cArr3 = this.mInputBuffer;
                    int i3 = this.mInputPtr;
                    this.mInputPtr = i3 + 1;
                    c = cArr3[i3];
                }
            }
            int len4 = (this.mInputPtr - start) - 1;
            if (len4 > 0) {
                w.write(this.mInputBuffer, start, len4);
                count += len4;
            }
            int bracketCount = 0;
            do {
                bracketCount++;
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr4 = this.mInputBuffer;
                    int i4 = this.mInputPtr;
                    this.mInputPtr = i4 + 1;
                    nextCharFromCurrent = cArr4[i4];
                } else {
                    nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_CDATA);
                }
                c = nextCharFromCurrent;
            } while (c == ']');
            match = bracketCount >= 2 && c == '>';
            if (match) {
                bracketCount -= 2;
            }
            while (bracketCount > 0) {
                bracketCount--;
                w.write(93);
                count++;
            }
        } while (!match);
        return count;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private int readAndWriteCoalesced(Writer w, boolean wasCData) throws XMLStreamException, IOException {
        this.mTokenState = 4;
        int count = 0;
        while (true) {
            if (this.mInputPtr >= this.mInputEnd && !loadMore()) {
                break;
            }
            char c = this.mInputBuffer[this.mInputPtr];
            if (c == '<') {
                if ((this.mInputEnd - this.mInputPtr < 3 && !ensureInput(3)) || this.mInputBuffer[this.mInputPtr + 1] != '!' || this.mInputBuffer[this.mInputPtr + 2] != '[') {
                    break;
                }
                this.mInputPtr += 3;
                checkCData();
                count += readAndWriteCData(w);
                wasCData = true;
            } else {
                if (c == '&' && !wasCData) {
                    break;
                }
                count += readAndWriteText(w);
                wasCData = false;
            }
        }
        return count;
    }

    protected final boolean skipWS(char c) throws XMLStreamException {
        if (c > ' ') {
            return false;
        }
        while (true) {
            if (c == '\n' || c == '\r') {
                skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd && !loadMoreFromCurrent()) {
                return true;
            }
            c = this.mInputBuffer[this.mInputPtr];
            if (c > ' ') {
                return true;
            }
            this.mInputPtr++;
        }
    }

    @Override // com.ctc.wstx.sr.StreamScanner
    protected EntityDecl findEntity(String id, Object arg) throws XMLStreamException {
        EntityDecl ed = this.mConfig.findCustomInternalEntity(id);
        if (ed == null && this.mGeneralEntities != null) {
            ed = this.mGeneralEntities.get(id);
        }
        if (this.mDocStandalone == 1 && ed != null && ed.wasDeclaredExternally()) {
            throwParseError(ErrorConsts.ERR_WF_ENTITY_EXT_DECLARED, ed.getName(), null);
        }
        return ed;
    }

    @Override // com.ctc.wstx.sr.StreamScanner
    protected void handleUndeclaredEntity(String id) throws XMLStreamException {
        throwParseError(this.mDocStandalone == 1 ? ErrorConsts.ERR_WF_GE_UNDECLARED_SA : ErrorConsts.ERR_WF_GE_UNDECLARED, id, null);
    }

    @Override // com.ctc.wstx.sr.StreamScanner
    protected void handleIncompleteEntityProblem(WstxInputSource closing) throws XMLStreamException {
        String top = this.mElementStack.isEmpty() ? "[ROOT]" : this.mElementStack.getTopElementDesc();
        throwParseError("Unexpected end of entity expansion for entity &{0}; was expecting a close tag for element <{1}>", closing.getEntityId(), top);
    }

    protected void handleGreedyEntityProblem(WstxInputSource input) throws XMLStreamException {
        String top = this.mElementStack.isEmpty() ? "[ROOT]" : this.mElementStack.getTopElementDesc();
        throwParseError("Improper GE/element nesting: entity &" + input.getEntityId() + " contains closing tag for <" + top + ">");
    }

    private void throwNotTextual(int type) {
        throw new IllegalStateException("Not a textual event (" + tokenTypeDesc(type) + ")");
    }

    private void throwNotTextXxx(int type) {
        throw new IllegalStateException("getTextXxx() methods can not be called on " + tokenTypeDesc(type));
    }

    protected void throwNotTextualOrElem(int type) {
        throw new IllegalStateException(MessageFormat.format(ErrorConsts.ERR_STATE_NOT_ELEM_OR_TEXT, tokenTypeDesc(type)));
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwUnexpectedEOF() {
        throwUnexpectedEOF("; was expecting a close tag for element <" + this.mElementStack.getTopElementDesc() + ">");
    }

    protected XMLStreamException _constructUnexpectedInTyped(int nextToken) {
        if (nextToken == 1) {
            return _constructTypeException("Element content can not contain child START_ELEMENT when using Typed Access methods", null);
        }
        return _constructTypeException("Expected a text token, got " + tokenTypeDesc(nextToken), null);
    }

    protected TypedXMLStreamException _constructTypeException(String msg, String lexicalValue) {
        return new TypedXMLStreamException(lexicalValue, msg, getStartLocation());
    }

    protected void reportInvalidContent(int evtType) throws XMLStreamException {
        throwParseError("Internal error: sub-class should override method");
    }
}
