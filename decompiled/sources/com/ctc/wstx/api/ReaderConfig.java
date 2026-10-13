package com.ctc.wstx.api;

import com.aowagie.text.xml.XMLConstants;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.io.BufferRecycler;
import com.ctc.wstx.util.ArgUtil;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.SymbolTable;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamProperties;
import org.codehaus.stax2.validation.DTDValidationSchema;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/ReaderConfig.class */
public final class ReaderConfig extends CommonConfig implements InputConfigFlags {
    public static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 1000;
    public static final int DEFAULT_MAX_ATTRIBUTE_LENGTH = 524288;
    public static final int DEFAULT_MAX_ELEMENT_DEPTH = 1000;
    public static final int DEFAULT_MAX_ENTITY_DEPTH = 500;
    public static final int DEFAULT_MAX_ENTITY_COUNT = 100000;
    public static final int DEFAULT_MAX_DTD_DEPTH = 500;
    static final int PROP_COALESCE_TEXT = 1;
    static final int PROP_NAMESPACE_AWARE = 2;
    static final int PROP_REPLACE_ENTITY_REFS = 3;
    static final int PROP_SUPPORT_EXTERNAL_ENTITIES = 4;
    static final int PROP_VALIDATE_AGAINST_DTD = 5;
    static final int PROP_SUPPORT_DTD = 6;
    public static final int PROP_EVENT_ALLOCATOR = 7;
    static final int PROP_WARNING_REPORTER = 8;
    static final int PROP_XML_RESOLVER = 9;
    static final int PROP_INTERN_NS_URIS = 20;
    static final int PROP_INTERN_NAMES = 21;
    static final int PROP_REPORT_CDATA = 22;
    static final int PROP_REPORT_PROLOG_WS = 23;
    static final int PROP_PRESERVE_LOCATION = 24;
    static final int PROP_AUTO_CLOSE_INPUT = 25;
    static final int PROP_SUPPORT_XMLID = 26;
    static final int PROP_DTD_OVERRIDE = 27;
    static final int PROP_JAXP_SECURE_PROCESSING = 30;
    static final int PROP_NORMALIZE_LFS = 40;
    static final int PROP_CACHE_DTDS = 42;
    static final int PROP_CACHE_DTDS_BY_PUBLIC_ID = 43;
    static final int PROP_LAZY_PARSING = 44;
    static final int PROP_SUPPORT_DTDPP = 45;
    static final int PROP_TREAT_CHAR_REFS_AS_ENTS = 46;
    static final int PROP_ALLOW_XML11_ESCAPED_CHARS_IN_XML10 = 47;
    static final int PROP_INPUT_BUFFER_LENGTH = 50;
    static final int PROP_MIN_TEXT_SEGMENT = 52;
    static final int PROP_CUSTOM_INTERNAL_ENTITIES = 53;
    static final int PROP_DTD_RESOLVER = 54;
    static final int PROP_ENTITY_RESOLVER = 55;
    static final int PROP_UNDECLARED_ENTITY_RESOLVER = 56;
    static final int PROP_BASE_URL = 57;
    static final int PROP_INPUT_PARSING_MODE = 58;
    static final int PROP_MAX_ATTRIBUTES_PER_ELEMENT = 60;
    static final int PROP_MAX_CHILDREN_PER_ELEMENT = 61;
    static final int PROP_MAX_ELEMENT_COUNT = 62;
    static final int PROP_MAX_ELEMENT_DEPTH = 63;
    static final int PROP_MAX_CHARACTERS = 64;
    static final int PROP_MAX_ATTRIBUTE_SIZE = 65;
    static final int PROP_MAX_TEXT_LENGTH = 66;
    static final int PROP_MAX_ENTITY_COUNT = 67;
    static final int PROP_MAX_ENTITY_DEPTH = 68;
    static final int PROP_MAX_DTD_DEPTH = 69;
    static final int MIN_INPUT_BUFFER_LENGTH = 8;
    static final int DTD_CACHE_SIZE_J2SE = 12;
    static final int DTD_CACHE_SIZE_J2ME = 5;
    static final int DEFAULT_SHORTEST_TEXT_SEGMENT = 64;
    static final int DEFAULT_FLAGS_FULL = 2973213;
    static final int DEFAULT_FLAGS_J2ME = 2973213;
    static final HashMap<String, Integer> sProperties = new HashMap<>(64);
    protected final boolean mIsJ2MESubset;
    protected final SymbolTable mSymbols;
    protected int mConfigFlags;
    protected int mConfigFlagMods;
    static final int PROP_INTERN_NAMES_EXPLICIT = 26;
    static final int PROP_INTERN_NS_URIS_EXPLICIT = 27;
    protected int mInputBufferLen;
    protected int mMinTextSegmentLen;
    protected int mMaxAttributesPerElement;
    protected int mMaxAttributeSize;
    protected int mMaxChildrenPerElement;
    protected int mMaxElementDepth;
    protected long mMaxElementCount;
    protected long mMaxCharacters;
    protected int mMaxTextLength;
    protected int mMaxEntityDepth;
    protected long mMaxEntityCount;
    protected int mMaxDtdDepth;
    protected URL mBaseURL;
    protected WstxInputProperties.ParsingMode mParsingMode;
    protected boolean mXml11;
    XMLReporter mReporter;
    XMLResolver mDtdResolver;
    XMLResolver mEntityResolver;
    Object[] mSpecialProperties;
    private static final int SPEC_PROC_COUNT = 4;
    private static final int SP_IX_CUSTOM_ENTITIES = 0;
    private static final int SP_IX_UNDECL_ENT_RESOLVER = 1;
    private static final int SP_IX_DTD_EVENT_LISTENER = 2;
    private static final int SP_IX_DTD_OVERRIDE = 3;
    static final ThreadLocal<SoftReference<BufferRecycler>> mRecyclerRef;
    BufferRecycler mCurrRecycler;

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean returnNullForDefaultNamespace() {
        return super.returnNullForDefaultNamespace();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean doesSupportXmlId() {
        return super.doesSupportXmlId();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean doesSupportXml11() {
        return super.doesSupportXml11();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean setProperty(String str, Object obj) {
        return super.setProperty(str, obj);
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean isPropertySupported(String str) {
        return super.isPropertySupported(str);
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ Object getProperty(String str) {
        return super.getProperty(str);
    }

    static {
        sProperties.put("javax.xml.stream.isCoalescing", 1);
        sProperties.put(XMLStreamProperties.XSP_NAMESPACE_AWARE, 2);
        sProperties.put("javax.xml.stream.isReplacingEntityReferences", 3);
        sProperties.put("javax.xml.stream.isSupportingExternalEntities", 4);
        sProperties.put("javax.xml.stream.isValidating", 5);
        sProperties.put("javax.xml.stream.supportDTD", 6);
        sProperties.put("javax.xml.stream.allocator", 7);
        sProperties.put(XMLStreamProperties.XSP_PROBLEM_REPORTER, 8);
        sProperties.put("javax.xml.stream.resolver", 9);
        sProperties.put(XMLInputFactory2.P_INTERN_NAMES, 21);
        sProperties.put(XMLInputFactory2.P_INTERN_NS_URIS, 20);
        sProperties.put(XMLInputFactory2.P_REPORT_CDATA, 22);
        sProperties.put(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, 23);
        sProperties.put(XMLInputFactory2.P_PRESERVE_LOCATION, 24);
        sProperties.put(XMLInputFactory2.P_AUTO_CLOSE_INPUT, 25);
        sProperties.put(XMLStreamProperties.XSP_SUPPORT_XMLID, 26);
        sProperties.put("org.codehaus.stax2.propDtdOverride", 27);
        sProperties.put(XMLConstants.FEATURE_SECURE_PROCESSING, 30);
        sProperties.put(WstxInputProperties.P_CACHE_DTDS, 42);
        sProperties.put(WstxInputProperties.P_CACHE_DTDS_BY_PUBLIC_ID, 43);
        sProperties.put("com.ctc.wstx.lazyParsing", 44);
        sProperties.put(WstxInputProperties.P_TREAT_CHAR_REFS_AS_ENTS, 46);
        sProperties.put(WstxInputProperties.P_ALLOW_XML11_ESCAPED_CHARS_IN_XML10, 47);
        sProperties.put(WstxInputProperties.P_NORMALIZE_LFS, 40);
        sProperties.put(WstxInputProperties.P_INPUT_BUFFER_LENGTH, 50);
        sProperties.put(WstxInputProperties.P_MIN_TEXT_SEGMENT, 52);
        sProperties.put(WstxInputProperties.P_MAX_ATTRIBUTES_PER_ELEMENT, 60);
        sProperties.put(WstxInputProperties.P_MAX_ATTRIBUTE_SIZE, 65);
        sProperties.put(WstxInputProperties.P_MAX_CHILDREN_PER_ELEMENT, 61);
        sProperties.put(WstxInputProperties.P_MAX_TEXT_LENGTH, 66);
        sProperties.put(WstxInputProperties.P_MAX_ELEMENT_COUNT, 62);
        sProperties.put(WstxInputProperties.P_MAX_ELEMENT_DEPTH, 63);
        sProperties.put(WstxInputProperties.P_MAX_ENTITY_DEPTH, 68);
        sProperties.put(WstxInputProperties.P_MAX_ENTITY_COUNT, 67);
        sProperties.put(WstxInputProperties.P_MAX_DTD_DEPTH, 69);
        sProperties.put(WstxInputProperties.P_MAX_CHARACTERS, 64);
        sProperties.put(WstxInputProperties.P_CUSTOM_INTERNAL_ENTITIES, 53);
        sProperties.put(WstxInputProperties.P_DTD_RESOLVER, 54);
        sProperties.put(WstxInputProperties.P_ENTITY_RESOLVER, 55);
        sProperties.put(WstxInputProperties.P_UNDECLARED_ENTITY_RESOLVER, 56);
        sProperties.put(WstxInputProperties.P_BASE_URL, 57);
        sProperties.put(WstxInputProperties.P_INPUT_PARSING_MODE, 58);
        mRecyclerRef = new ThreadLocal<>();
    }

    private ReaderConfig(ReaderConfig base, boolean j2meSubset, SymbolTable symbols, int configFlags, int configFlagMods, int inputBufLen, int minTextSegmentLen) {
        super(base);
        this.mMaxAttributesPerElement = 1000;
        this.mMaxAttributeSize = 524288;
        this.mMaxChildrenPerElement = WinNT.MAXLONG;
        this.mMaxElementDepth = 1000;
        this.mMaxElementCount = Long.MAX_VALUE;
        this.mMaxCharacters = Long.MAX_VALUE;
        this.mMaxTextLength = WinNT.MAXLONG;
        this.mMaxEntityDepth = 500;
        this.mMaxEntityCount = 100000L;
        this.mMaxDtdDepth = 500;
        this.mParsingMode = WstxInputProperties.PARSING_MODE_DOCUMENT;
        this.mXml11 = false;
        this.mDtdResolver = null;
        this.mEntityResolver = null;
        this.mSpecialProperties = null;
        this.mCurrRecycler = null;
        this.mIsJ2MESubset = j2meSubset;
        this.mSymbols = symbols;
        this.mConfigFlags = configFlags;
        this.mConfigFlagMods = configFlagMods;
        this.mInputBufferLen = inputBufLen;
        this.mMinTextSegmentLen = minTextSegmentLen;
        if (base != null) {
            this.mMaxAttributesPerElement = base.mMaxAttributesPerElement;
            this.mMaxAttributeSize = base.mMaxAttributeSize;
            this.mMaxChildrenPerElement = base.mMaxChildrenPerElement;
            this.mMaxElementCount = base.mMaxElementCount;
            this.mMaxElementDepth = base.mMaxElementDepth;
            this.mMaxCharacters = base.mMaxCharacters;
            this.mMaxTextLength = base.mMaxTextLength;
            this.mMaxEntityDepth = base.mMaxEntityDepth;
            this.mMaxEntityCount = base.mMaxEntityCount;
            this.mMaxDtdDepth = base.mMaxDtdDepth;
        }
        SoftReference<BufferRecycler> ref = mRecyclerRef.get();
        if (ref != null) {
            this.mCurrRecycler = ref.get();
        }
    }

    public static ReaderConfig createJ2MEDefaults() {
        ReaderConfig rc = new ReaderConfig(null, true, null, 2973213, 0, 2000, 64);
        return rc;
    }

    public static ReaderConfig createFullDefaults() {
        ReaderConfig rc = new ReaderConfig(null, false, null, 2973213, 0, WinError.ERROR_WINS_INTERNAL, 64);
        return rc;
    }

    public ReaderConfig createNonShared(SymbolTable sym) {
        ReaderConfig rc = new ReaderConfig(this, this.mIsJ2MESubset, sym, this.mConfigFlags, this.mConfigFlagMods, this.mInputBufferLen, this.mMinTextSegmentLen);
        rc.mReporter = this.mReporter;
        rc.mDtdResolver = this.mDtdResolver;
        rc.mEntityResolver = this.mEntityResolver;
        rc.mBaseURL = this.mBaseURL;
        rc.mParsingMode = this.mParsingMode;
        rc.mMaxAttributesPerElement = this.mMaxAttributesPerElement;
        rc.mMaxAttributeSize = this.mMaxAttributeSize;
        rc.mMaxChildrenPerElement = this.mMaxChildrenPerElement;
        rc.mMaxElementCount = this.mMaxElementCount;
        rc.mMaxCharacters = this.mMaxCharacters;
        rc.mMaxTextLength = this.mMaxTextLength;
        rc.mMaxElementDepth = this.mMaxElementDepth;
        rc.mMaxEntityDepth = this.mMaxEntityDepth;
        rc.mMaxEntityCount = this.mMaxEntityCount;
        rc.mMaxDtdDepth = this.mMaxDtdDepth;
        if (this.mSpecialProperties != null) {
            int len = this.mSpecialProperties.length;
            Object[] specProps = new Object[len];
            System.arraycopy(this.mSpecialProperties, 0, specProps, 0, len);
            rc.mSpecialProperties = specProps;
        }
        return rc;
    }

    public void resetState() {
        this.mXml11 = false;
    }

    @Override // com.ctc.wstx.api.CommonConfig
    protected int findPropertyId(String propName) {
        Integer I = sProperties.get(propName);
        if (I == null) {
            return -1;
        }
        return I.intValue();
    }

    public SymbolTable getSymbols() {
        return this.mSymbols;
    }

    public int getDtdCacheSize() {
        return this.mIsJ2MESubset ? 5 : 12;
    }

    public int getConfigFlags() {
        return this.mConfigFlags;
    }

    public boolean willCoalesceText() {
        return _hasConfigFlag(2);
    }

    public boolean willSupportNamespaces() {
        return _hasConfigFlag(1);
    }

    public boolean willReplaceEntityRefs() {
        return _hasConfigFlag(4);
    }

    public boolean willSupportExternalEntities() {
        return _hasConfigFlag(8);
    }

    public boolean willSupportDTDs() {
        return _hasConfigFlag(16);
    }

    public boolean willValidateWithDTD() {
        return _hasConfigFlag(32);
    }

    public boolean willReportCData() {
        return _hasConfigFlag(512);
    }

    public boolean willParseLazily() {
        return _hasConfigFlag(262144);
    }

    public boolean willInternNames() {
        return _hasConfigFlag(1024);
    }

    public boolean willInternNsURIs() {
        return _hasConfigFlag(2048);
    }

    public boolean willPreserveLocation() {
        return _hasConfigFlag(4096);
    }

    public boolean willAutoCloseInput() {
        return _hasConfigFlag(8192);
    }

    public boolean willProcessSecurely() {
        return _hasConfigFlag(33554432);
    }

    public boolean willReportPrologWhitespace() {
        return _hasConfigFlag(256);
    }

    public boolean willCacheDTDs() {
        return _hasConfigFlag(65536);
    }

    public boolean willCacheDTDsByPublicId() {
        return _hasConfigFlag(131072);
    }

    public boolean willDoXmlIdTyping() {
        return _hasConfigFlag(2097152);
    }

    public boolean willDoXmlIdUniqChecks() {
        return _hasConfigFlag(4194304);
    }

    public boolean willSupportDTDPP() {
        return _hasConfigFlag(524288);
    }

    public boolean willNormalizeLFs() {
        return _hasConfigFlag(16384);
    }

    public boolean willTreatCharRefsAsEnts() {
        return _hasConfigFlag(8388608);
    }

    public boolean willAllowXml11EscapedCharsInXml10() {
        return _hasConfigFlag(16777216);
    }

    public int getInputBufferLength() {
        return this.mInputBufferLen;
    }

    public int getShortestReportedTextSegment() {
        return this.mMinTextSegmentLen;
    }

    public int getMaxAttributesPerElement() {
        return this.mMaxAttributesPerElement;
    }

    public int getMaxAttributeSize() {
        return this.mMaxAttributeSize;
    }

    public int getMaxChildrenPerElement() {
        return this.mMaxChildrenPerElement;
    }

    public int getMaxElementDepth() {
        return this.mMaxElementDepth;
    }

    public long getMaxElementCount() {
        return this.mMaxElementCount;
    }

    public int getMaxEntityDepth() {
        return this.mMaxEntityDepth;
    }

    public long getMaxEntityCount() {
        return this.mMaxEntityCount;
    }

    public int getMaxDtdDepth() {
        return this.mMaxDtdDepth;
    }

    public long getMaxCharacters() {
        return this.mMaxCharacters;
    }

    public long getMaxTextLength() {
        return this.mMaxTextLength;
    }

    public Map<String, EntityDecl> getCustomInternalEntities() {
        Map<String, EntityDecl> custEnt = (Map) _getSpecialProperty(0);
        if (custEnt == null) {
            return Collections.emptyMap();
        }
        int len = custEnt.size();
        HashMap<String, EntityDecl> m = new HashMap<>(len + (len >> 2), 0.81f);
        for (Map.Entry<String, EntityDecl> me : custEnt.entrySet()) {
            m.put(me.getKey(), me.getValue());
        }
        return m;
    }

    public EntityDecl findCustomInternalEntity(String id) {
        Map<String, EntityDecl> custEnt = (Map) _getSpecialProperty(0);
        if (custEnt == null) {
            return null;
        }
        return custEnt.get(id);
    }

    public XMLReporter getXMLReporter() {
        return this.mReporter;
    }

    public XMLResolver getXMLResolver() {
        return this.mEntityResolver;
    }

    public XMLResolver getDtdResolver() {
        return this.mDtdResolver;
    }

    public XMLResolver getEntityResolver() {
        return this.mEntityResolver;
    }

    public XMLResolver getUndeclaredEntityResolver() {
        return (XMLResolver) _getSpecialProperty(1);
    }

    public URL getBaseURL() {
        return this.mBaseURL;
    }

    public WstxInputProperties.ParsingMode getInputParsingMode() {
        return this.mParsingMode;
    }

    public boolean inputParsingModeDocuments() {
        return this.mParsingMode == WstxInputProperties.PARSING_MODE_DOCUMENTS;
    }

    public boolean inputParsingModeFragment() {
        return this.mParsingMode == WstxInputProperties.PARSING_MODE_FRAGMENT;
    }

    public boolean isXml11() {
        return this.mXml11;
    }

    public DTDEventListener getDTDEventListener() {
        return (DTDEventListener) _getSpecialProperty(2);
    }

    public DTDValidationSchema getDTDOverride() {
        return (DTDValidationSchema) _getSpecialProperty(3);
    }

    public boolean hasInternNamesBeenEnabled() {
        return _hasExplicitConfigFlag(1024);
    }

    public boolean hasInternNsURIsBeenEnabled() {
        return _hasExplicitConfigFlag(2048);
    }

    public boolean isCoalescingExplicitlyDisabled() {
        return (_hasConfigFlag(2) || (this.mConfigFlagMods & 2) == 0) ? false : true;
    }

    public void setConfigFlag(int flag) {
        this.mConfigFlags |= flag;
        this.mConfigFlagMods |= flag;
    }

    public void clearConfigFlag(int flag) {
        this.mConfigFlags &= flag ^ (-1);
        this.mConfigFlagMods |= flag;
    }

    public void doCoalesceText(boolean state) {
        setConfigFlag(2, state);
    }

    public void doSupportNamespaces(boolean state) {
        setConfigFlag(1, state);
    }

    public void doReplaceEntityRefs(boolean state) {
        setConfigFlag(4, state);
    }

    public void doSupportExternalEntities(boolean state) {
        setConfigFlag(8, state);
    }

    public void doSupportDTDs(boolean state) {
        setConfigFlag(16, state);
    }

    public void doValidateWithDTD(boolean state) {
        setConfigFlag(32, state);
    }

    public void doProcessSecurely(boolean value) {
        setConfigFlag(33554432, value);
        if (value) {
            doSupportExternalEntities(false);
        }
    }

    public void doInternNames(boolean state) {
        setConfigFlag(1024, state);
    }

    public void doInternNsURIs(boolean state) {
        setConfigFlag(2048, state);
    }

    public void doReportPrologWhitespace(boolean state) {
        setConfigFlag(256, state);
    }

    public void doReportCData(boolean state) {
        setConfigFlag(512, state);
    }

    public void doCacheDTDs(boolean state) {
        setConfigFlag(65536, state);
    }

    public void doCacheDTDsByPublicId(boolean state) {
        setConfigFlag(131072, state);
    }

    public void doParseLazily(boolean state) {
        setConfigFlag(262144, state);
    }

    public void doXmlIdTyping(boolean state) {
        setConfigFlag(2097152, state);
    }

    public void doXmlIdUniqChecks(boolean state) {
        setConfigFlag(4194304, state);
    }

    public void doPreserveLocation(boolean state) {
        setConfigFlag(4096, state);
    }

    public void doAutoCloseInput(boolean state) {
        setConfigFlag(8192, state);
    }

    public void doSupportDTDPP(boolean state) {
        setConfigFlag(524288, state);
    }

    public void doTreatCharRefsAsEnts(boolean state) {
        setConfigFlag(8388608, state);
    }

    public void doAllowXml11EscapedCharsInXml10(boolean state) {
        setConfigFlag(16777216, state);
    }

    public void doNormalizeLFs(boolean state) {
        setConfigFlag(16384, state);
    }

    public void setInputBufferLength(int value) {
        if (value < 8) {
            value = 8;
        }
        this.mInputBufferLen = value;
    }

    public void setShortestReportedTextSegment(int value) {
        this.mMinTextSegmentLen = value;
    }

    public void setMaxAttributesPerElement(int value) {
        this.mMaxAttributesPerElement = value;
    }

    public void setMaxAttributeSize(int value) {
        this.mMaxAttributeSize = value;
    }

    public void setMaxChildrenPerElement(int value) {
        this.mMaxChildrenPerElement = value;
    }

    public void setMaxElementDepth(int value) {
        this.mMaxElementDepth = value;
    }

    public void setMaxElementCount(long value) {
        this.mMaxElementCount = value;
    }

    public void setMaxCharacters(long value) {
        this.mMaxCharacters = value;
    }

    public void setMaxTextLength(int value) {
        this.mMaxTextLength = value;
    }

    public void setMaxEntityDepth(int value) {
        this.mMaxEntityDepth = value;
    }

    public void setMaxEntityCount(long value) {
        this.mMaxEntityCount = value;
    }

    public void setMaxDtdDepth(int value) {
        this.mMaxDtdDepth = value;
    }

    public void setCustomInternalEntities(Map<String, ?> m) {
        Map<String, EntityDecl> entMap;
        char[] charArray;
        if (m == null || m.size() < 1) {
            entMap = Collections.emptyMap();
        } else {
            int len = m.size();
            entMap = new HashMap<>(len + (len >> 1), 0.75f);
            for (Map.Entry<String, ?> me : m.entrySet()) {
                Object val = me.getValue();
                if (val == null) {
                    charArray = DataUtil.getEmptyCharArray();
                } else if (val instanceof char[]) {
                    charArray = (char[]) val;
                } else {
                    String str = val.toString();
                    charArray = str.toCharArray();
                }
                char[] ch = charArray;
                String name = me.getKey();
                entMap.put(name, IntEntity.create(name, ch));
            }
        }
        _setSpecialProperty(0, entMap);
    }

    public void setXMLReporter(XMLReporter r) {
        this.mReporter = r;
    }

    public void setXMLResolver(XMLResolver r) {
        this.mEntityResolver = r;
        this.mDtdResolver = r;
    }

    public void setDtdResolver(XMLResolver r) {
        this.mDtdResolver = r;
    }

    public void setEntityResolver(XMLResolver r) {
        this.mEntityResolver = r;
    }

    public void setUndeclaredEntityResolver(XMLResolver r) {
        _setSpecialProperty(1, r);
    }

    public void setBaseURL(URL baseURL) {
        this.mBaseURL = baseURL;
    }

    public void setInputParsingMode(WstxInputProperties.ParsingMode mode) {
        this.mParsingMode = mode;
    }

    public void enableXml11(boolean state) {
        this.mXml11 = state;
    }

    public void setDTDEventListener(DTDEventListener l) {
        _setSpecialProperty(2, l);
    }

    public void setDTDOverride(DTDValidationSchema schema) {
        _setSpecialProperty(3, schema);
    }

    public void configureForXmlConformance() {
        doSupportNamespaces(true);
        doSupportDTDs(true);
        doSupportExternalEntities(true);
        doReplaceEntityRefs(true);
        doXmlIdTyping(true);
        doXmlIdUniqChecks(true);
    }

    public void configureForConvenience() {
        doCoalesceText(true);
        doReplaceEntityRefs(true);
        doReportCData(false);
        doReportPrologWhitespace(false);
        doPreserveLocation(true);
        doParseLazily(false);
    }

    public void configureForSpeed() {
        doCoalesceText(false);
        doPreserveLocation(false);
        doReportPrologWhitespace(false);
        doInternNsURIs(true);
        doXmlIdUniqChecks(false);
        doCacheDTDs(true);
        doParseLazily(true);
        setShortestReportedTextSegment(16);
        setInputBufferLength(8000);
    }

    public void configureForLowMemUsage() {
        doCoalesceText(false);
        doPreserveLocation(false);
        doCacheDTDs(false);
        doParseLazily(true);
        doXmlIdUniqChecks(false);
        setShortestReportedTextSegment(64);
        setInputBufferLength(512);
    }

    public void configureForRoundTripping() {
        doCoalesceText(false);
        doReplaceEntityRefs(false);
        doReportCData(true);
        doReportPrologWhitespace(true);
        doTreatCharRefsAsEnts(true);
        doNormalizeLFs(false);
        setShortestReportedTextSegment(WinNT.MAXLONG);
    }

    public char[] allocSmallCBuffer(int minSize) {
        char[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getSmallCBuffer(minSize)) != null) {
            return result;
        }
        return new char[minSize];
    }

    public void freeSmallCBuffer(char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = createRecycler();
        }
        this.mCurrRecycler.returnSmallCBuffer(buffer);
    }

    public char[] allocMediumCBuffer(int minSize) {
        char[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getMediumCBuffer(minSize)) != null) {
            return result;
        }
        return new char[minSize];
    }

    public void freeMediumCBuffer(char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = createRecycler();
        }
        this.mCurrRecycler.returnMediumCBuffer(buffer);
    }

    public char[] allocFullCBuffer(int minSize) {
        char[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getFullCBuffer(minSize)) != null) {
            return result;
        }
        return new char[minSize];
    }

    public void freeFullCBuffer(char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = createRecycler();
        }
        this.mCurrRecycler.returnFullCBuffer(buffer);
    }

    public byte[] allocFullBBuffer(int minSize) {
        byte[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getFullBBuffer(minSize)) != null) {
            return result;
        }
        return new byte[minSize];
    }

    public void freeFullBBuffer(byte[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = createRecycler();
        }
        this.mCurrRecycler.returnFullBBuffer(buffer);
    }

    private BufferRecycler createRecycler() {
        BufferRecycler recycler = new BufferRecycler();
        mRecyclerRef.set(new SoftReference<>(recycler));
        return recycler;
    }

    private void setConfigFlag(int flag, boolean state) {
        if (state) {
            this.mConfigFlags |= flag;
        } else {
            this.mConfigFlags &= flag ^ (-1);
        }
        this.mConfigFlagMods |= flag;
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public Object getProperty(int id) {
        switch (id) {
            case 1:
                return willCoalesceText() ? Boolean.TRUE : Boolean.FALSE;
            case 2:
                return willSupportNamespaces() ? Boolean.TRUE : Boolean.FALSE;
            case 3:
                return willReplaceEntityRefs() ? Boolean.TRUE : Boolean.FALSE;
            case 4:
                return willSupportExternalEntities() ? Boolean.TRUE : Boolean.FALSE;
            case 5:
                return willValidateWithDTD() ? Boolean.TRUE : Boolean.FALSE;
            case 6:
                return willSupportDTDs() ? Boolean.TRUE : Boolean.FALSE;
            case 7:
                return null;
            case 8:
                return getXMLReporter();
            case 9:
                return getXMLResolver();
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 28:
            case 29:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 41:
            case 45:
            case 48:
            case 49:
            case 51:
            case 59:
            default:
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            case 20:
                return willInternNsURIs() ? Boolean.TRUE : Boolean.FALSE;
            case 21:
                return willInternNames() ? Boolean.TRUE : Boolean.FALSE;
            case 22:
                return willReportCData() ? Boolean.TRUE : Boolean.FALSE;
            case 23:
                return willReportPrologWhitespace() ? Boolean.TRUE : Boolean.FALSE;
            case 24:
                return willPreserveLocation() ? Boolean.TRUE : Boolean.FALSE;
            case 25:
                return willAutoCloseInput() ? Boolean.TRUE : Boolean.FALSE;
            case 26:
                if (_hasConfigFlag(2097152)) {
                    return _hasConfigFlag(4194304) ? XMLStreamProperties.XSP_V_XMLID_FULL : XMLStreamProperties.XSP_V_XMLID_TYPING;
                }
                return XMLStreamProperties.XSP_V_XMLID_NONE;
            case 27:
                return getDTDOverride();
            case 30:
                return Boolean.valueOf(_hasConfigFlag(33554432));
            case 40:
                return willNormalizeLFs() ? Boolean.TRUE : Boolean.FALSE;
            case 42:
                return willCacheDTDs() ? Boolean.TRUE : Boolean.FALSE;
            case 43:
                return willCacheDTDsByPublicId() ? Boolean.TRUE : Boolean.FALSE;
            case 44:
                return willParseLazily() ? Boolean.TRUE : Boolean.FALSE;
            case 46:
                return willTreatCharRefsAsEnts() ? Boolean.TRUE : Boolean.FALSE;
            case 47:
                return willAllowXml11EscapedCharsInXml10() ? Boolean.TRUE : Boolean.FALSE;
            case 50:
                return Integer.valueOf(getInputBufferLength());
            case 52:
                return Integer.valueOf(getShortestReportedTextSegment());
            case 53:
                return getCustomInternalEntities();
            case 54:
                return getDtdResolver();
            case 55:
                return getEntityResolver();
            case 56:
                return getUndeclaredEntityResolver();
            case 57:
                return getBaseURL();
            case 58:
                return getInputParsingMode();
            case 60:
                return Integer.valueOf(getMaxAttributesPerElement());
            case 61:
                return Integer.valueOf(getMaxChildrenPerElement());
            case 62:
                return Long.valueOf(getMaxElementCount());
            case 63:
                return Integer.valueOf(getMaxElementDepth());
            case 64:
                return Long.valueOf(getMaxCharacters());
            case 65:
                return Integer.valueOf(getMaxAttributeSize());
            case 66:
                return Long.valueOf(getMaxTextLength());
            case 67:
                return Long.valueOf(getMaxEntityCount());
            case 68:
                return Integer.valueOf(getMaxEntityDepth());
            case 69:
                return Integer.valueOf(getMaxDtdDepth());
        }
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public boolean setProperty(String propName, int id, Object value) {
        URL u;
        boolean uniq;
        boolean typing;
        switch (id) {
            case 1:
                doCoalesceText(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 2:
                doSupportNamespaces(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 3:
                doReplaceEntityRefs(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 4:
                doSupportExternalEntities(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 5:
                doValidateWithDTD(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 6:
                doSupportDTDs(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 7:
                return false;
            case 8:
                setXMLReporter((XMLReporter) value);
                return true;
            case 9:
                setXMLResolver((XMLResolver) value);
                return true;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 28:
            case 29:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 41:
            case 45:
            case 48:
            case 49:
            case 51:
            case 59:
            default:
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            case 20:
                doInternNsURIs(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 21:
                doInternNames(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 22:
                doReportCData(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 23:
                doReportPrologWhitespace(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 24:
                doPreserveLocation(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 25:
                doAutoCloseInput(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 26:
                if (XMLStreamProperties.XSP_V_XMLID_NONE.equals(value)) {
                    uniq = false;
                    typing = false;
                } else if (XMLStreamProperties.XSP_V_XMLID_TYPING.equals(value)) {
                    typing = true;
                    uniq = false;
                } else if (XMLStreamProperties.XSP_V_XMLID_FULL.equals(value)) {
                    uniq = true;
                    typing = true;
                } else {
                    throw new IllegalArgumentException("Illegal argument ('" + value + "') to set property " + XMLStreamProperties.XSP_SUPPORT_XMLID + " to: has to be one of '" + XMLStreamProperties.XSP_V_XMLID_NONE + "', '" + XMLStreamProperties.XSP_V_XMLID_TYPING + "' or '" + XMLStreamProperties.XSP_V_XMLID_FULL + OperatorName.SHOW_TEXT_LINE);
                }
                setConfigFlag(2097152, typing);
                setConfigFlag(4194304, uniq);
                return true;
            case 27:
                setDTDOverride((DTDValidationSchema) value);
                return true;
            case 30:
                doProcessSecurely(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 40:
                doNormalizeLFs(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 42:
                doCacheDTDs(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 43:
                doCacheDTDsByPublicId(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 44:
                doParseLazily(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 46:
                doTreatCharRefsAsEnts(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 47:
                doAllowXml11EscapedCharsInXml10(ArgUtil.convertToBoolean(propName, value));
                return true;
            case 50:
                setInputBufferLength(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 52:
                setShortestReportedTextSegment(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 53:
                Map<String, ?> arg = (Map) value;
                setCustomInternalEntities(arg);
                return true;
            case 54:
                setDtdResolver((XMLResolver) value);
                return true;
            case 55:
                setEntityResolver((XMLResolver) value);
                return true;
            case 56:
                setUndeclaredEntityResolver((XMLResolver) value);
                return true;
            case 57:
                if (value == null) {
                    u = null;
                } else {
                    if (value instanceof URL) {
                        u = (URL) value;
                    } else {
                        try {
                            u = new URL(value.toString());
                        } catch (Exception ioe) {
                            throw new IllegalArgumentException(ioe.getMessage(), ioe);
                        }
                    }
                    break;
                }
                setBaseURL(u);
                return true;
            case 58:
                setInputParsingMode((WstxInputProperties.ParsingMode) value);
                return true;
            case 60:
                setMaxAttributesPerElement(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 61:
                setMaxChildrenPerElement(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 62:
                setMaxElementCount(ArgUtil.convertToLong(propName, value, 1L));
                return true;
            case 63:
                setMaxElementDepth(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 64:
                setMaxCharacters(ArgUtil.convertToLong(propName, value, 1L));
                return true;
            case 65:
                setMaxAttributeSize(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 66:
                setMaxTextLength(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 67:
                setMaxEntityCount(ArgUtil.convertToLong(propName, value, 1L));
                return true;
            case 68:
                setMaxEntityDepth(ArgUtil.convertToInt(propName, value, 1));
                return true;
            case 69:
                setMaxDtdDepth(ArgUtil.convertToInt(propName, value, 1));
                return true;
        }
    }

    protected boolean _hasConfigFlag(int flag) {
        return (this.mConfigFlags & flag) != 0;
    }

    protected boolean _hasExplicitConfigFlag(int flag) {
        return _hasConfigFlag(flag) && (this.mConfigFlagMods & flag) != 0;
    }

    private final Object _getSpecialProperty(int ix) {
        if (this.mSpecialProperties == null) {
            return null;
        }
        return this.mSpecialProperties[ix];
    }

    private final void _setSpecialProperty(int ix, Object value) {
        if (this.mSpecialProperties == null) {
            this.mSpecialProperties = new Object[4];
        }
        this.mSpecialProperties[ix] = value;
    }
}
