package com.ctc.wstx.api;

import com.ctc.wstx.cfg.OutputConfigFlags;
import com.ctc.wstx.io.BufferRecycler;
import com.ctc.wstx.util.ArgUtil;
import com.ctc.wstx.util.DataUtil;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import javax.xml.stream.XMLReporter;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamProperties;
import org.codehaus.stax2.io.EscapingWriterFactory;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/WriterConfig.class */
public final class WriterConfig extends CommonConfig implements OutputConfigFlags {
    protected static final String DEFAULT_AUTOMATIC_NS_PREFIX = "wstxns";
    static final int PROP_AUTOMATIC_NS = 1;
    static final int PROP_AUTOMATIC_EMPTY_ELEMENTS = 2;
    static final int PROP_AUTO_CLOSE_OUTPUT = 3;
    static final int PROP_ENABLE_NS = 4;
    static final int PROP_AUTOMATIC_NS_PREFIX = 5;
    static final int PROP_TEXT_ESCAPER = 6;
    static final int PROP_ATTR_VALUE_ESCAPER = 7;
    static final int PROP_PROBLEM_REPORTER = 8;
    static final int PROP_USE_DOUBLE_QUOTES_IN_XML_DECL = 10;
    static final int PROP_OUTPUT_CDATA_AS_TEXT = 11;
    static final int PROP_COPY_DEFAULT_ATTRS = 12;
    static final int PROP_ESCAPE_CR = 13;
    static final int PROP_ADD_SPACE_AFTER_EMPTY_ELEM = 14;
    static final int PROP_AUTOMATIC_END_ELEMENTS = 15;
    static final int PROP_VALIDATE_STRUCTURE = 16;
    static final int PROP_VALIDATE_CONTENT = 17;
    static final int PROP_VALIDATE_ATTR = 18;
    static final int PROP_VALIDATE_NAMES = 19;
    static final int PROP_FIX_CONTENT = 20;
    static final int PROP_OUTPUT_INVALID_CHAR_HANDLER = 21;
    static final int PROP_OUTPUT_EMPTY_ELEMENT_HANDLER = 22;
    static final int PROP_UNDERLYING_STREAM = 30;
    static final int PROP_UNDERLYING_WRITER = 31;
    static final boolean DEFAULT_USE_DOUBLE_QUOTES_IN_XML_DECL = false;
    static final boolean DEFAULT_OUTPUT_CDATA_AS_TEXT = false;
    static final boolean DEFAULT_COPY_DEFAULT_ATTRS = false;
    static final boolean DEFAULT_ESCAPE_CR = true;
    static final boolean DEFAULT_ADD_SPACE_AFTER_EMPTY_ELEM = false;
    static final boolean DEFAULT_VALIDATE_STRUCTURE = true;
    static final boolean DEFAULT_VALIDATE_CONTENT = true;
    static final boolean DEFAULT_VALIDATE_ATTR = false;
    static final boolean DEFAULT_VALIDATE_NAMES = false;
    static final boolean DEFAULT_FIX_CONTENT = false;
    static final int DEFAULT_FLAGS_J2ME = 933;
    static final int DEFAULT_FLAGS_FULL = 933;
    static final HashMap<String, Integer> sProperties = new HashMap<>(8);
    final boolean mIsJ2MESubset;
    protected int mConfigFlags;
    Object[] mSpecialProperties;
    private static final int SPEC_PROC_COUNT = 6;
    private static final int SP_IX_AUTO_NS_PREFIX = 0;
    private static final int SP_IX_TEXT_ESCAPER_FACTORY = 1;
    private static final int SP_IX_ATTR_VALUE_ESCAPER_FACTORY = 2;
    private static final int SP_IX_PROBLEM_REPORTER = 3;
    private static final int SP_IX_INVALID_CHAR_HANDLER = 4;
    private static final int SP_IX_EMPTY_ELEMENT_HANDLER = 5;
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
        sProperties.put("javax.xml.stream.isRepairingNamespaces", DataUtil.Integer(1));
        sProperties.put(XMLStreamProperties.XSP_NAMESPACE_AWARE, DataUtil.Integer(4));
        sProperties.put(XMLOutputFactory2.P_AUTOMATIC_EMPTY_ELEMENTS, DataUtil.Integer(2));
        sProperties.put(XMLOutputFactory2.P_AUTO_CLOSE_OUTPUT, DataUtil.Integer(3));
        sProperties.put(XMLOutputFactory2.P_AUTOMATIC_NS_PREFIX, DataUtil.Integer(5));
        sProperties.put(XMLOutputFactory2.P_TEXT_ESCAPER, DataUtil.Integer(6));
        sProperties.put(XMLOutputFactory2.P_ATTR_VALUE_ESCAPER, DataUtil.Integer(7));
        sProperties.put(XMLStreamProperties.XSP_PROBLEM_REPORTER, DataUtil.Integer(8));
        sProperties.put(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL, DataUtil.Integer(10));
        sProperties.put(WstxOutputProperties.P_OUTPUT_CDATA_AS_TEXT, DataUtil.Integer(11));
        sProperties.put(WstxOutputProperties.P_COPY_DEFAULT_ATTRS, DataUtil.Integer(12));
        sProperties.put(WstxOutputProperties.P_OUTPUT_ESCAPE_CR, DataUtil.Integer(13));
        sProperties.put(WstxOutputProperties.P_ADD_SPACE_AFTER_EMPTY_ELEM, DataUtil.Integer(14));
        sProperties.put(WstxOutputProperties.P_AUTOMATIC_END_ELEMENTS, DataUtil.Integer(15));
        sProperties.put(WstxOutputProperties.P_OUTPUT_INVALID_CHAR_HANDLER, DataUtil.Integer(21));
        sProperties.put(WstxOutputProperties.P_OUTPUT_EMPTY_ELEMENT_HANDLER, DataUtil.Integer(22));
        sProperties.put(WstxOutputProperties.P_OUTPUT_VALIDATE_STRUCTURE, DataUtil.Integer(16));
        sProperties.put(WstxOutputProperties.P_OUTPUT_VALIDATE_CONTENT, DataUtil.Integer(17));
        sProperties.put(WstxOutputProperties.P_OUTPUT_VALIDATE_ATTR, DataUtil.Integer(18));
        sProperties.put(WstxOutputProperties.P_OUTPUT_VALIDATE_NAMES, DataUtil.Integer(19));
        sProperties.put(WstxOutputProperties.P_OUTPUT_FIX_CONTENT, DataUtil.Integer(20));
        sProperties.put(WstxOutputProperties.P_OUTPUT_UNDERLYING_STREAM, DataUtil.Integer(30));
        sProperties.put(WstxOutputProperties.P_OUTPUT_UNDERLYING_STREAM, DataUtil.Integer(30));
        mRecyclerRef = new ThreadLocal<>();
    }

    private WriterConfig(WriterConfig base, boolean j2meSubset, int flags, Object[] specProps) {
        super(base);
        this.mSpecialProperties = null;
        this.mCurrRecycler = null;
        this.mIsJ2MESubset = j2meSubset;
        this.mConfigFlags = flags;
        this.mSpecialProperties = specProps;
        SoftReference<BufferRecycler> ref = mRecyclerRef.get();
        if (ref != null) {
            this.mCurrRecycler = ref.get();
        }
    }

    public static WriterConfig createJ2MEDefaults() {
        return new WriterConfig(null, true, 933, null);
    }

    public static WriterConfig createFullDefaults() {
        return new WriterConfig(null, false, 933, null);
    }

    public WriterConfig createNonShared() {
        Object[] specProps;
        if (this.mSpecialProperties != null) {
            int len = this.mSpecialProperties.length;
            specProps = new Object[len];
            System.arraycopy(this.mSpecialProperties, 0, specProps, 0, len);
        } else {
            specProps = null;
        }
        return new WriterConfig(this, this.mIsJ2MESubset, this.mConfigFlags, specProps);
    }

    @Override // com.ctc.wstx.api.CommonConfig
    protected int findPropertyId(String propName) {
        Integer I = sProperties.get(propName);
        if (I == null) {
            return -1;
        }
        return I.intValue();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public Object getProperty(int id) {
        switch (id) {
            case 1:
                return automaticNamespacesEnabled() ? Boolean.TRUE : Boolean.FALSE;
            case 2:
                return automaticEmptyElementsEnabled() ? Boolean.TRUE : Boolean.FALSE;
            case 3:
                return willAutoCloseOutput() ? Boolean.TRUE : Boolean.FALSE;
            case 4:
                return willSupportNamespaces() ? Boolean.TRUE : Boolean.FALSE;
            case 5:
                return getAutomaticNsPrefix();
            case 6:
                return getTextEscaperFactory();
            case 7:
                return getAttrValueEscaperFactory();
            case 8:
                return getProblemReporter();
            case 9:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            default:
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            case 10:
                return willUseDoubleQuotesInXmlDecl() ? Boolean.TRUE : Boolean.FALSE;
            case 11:
                return willOutputCDataAsText() ? Boolean.TRUE : Boolean.FALSE;
            case 12:
                return willCopyDefaultAttrs() ? Boolean.TRUE : Boolean.FALSE;
            case 13:
                return willEscapeCr() ? Boolean.TRUE : Boolean.FALSE;
            case 14:
                return willAddSpaceAfterEmptyElem() ? Boolean.TRUE : Boolean.FALSE;
            case 15:
                return automaticEndElementsEnabled() ? Boolean.TRUE : Boolean.FALSE;
            case 16:
                return willValidateStructure() ? Boolean.TRUE : Boolean.FALSE;
            case 17:
                return willValidateContent() ? Boolean.TRUE : Boolean.FALSE;
            case 18:
                return willValidateAttributes() ? Boolean.TRUE : Boolean.FALSE;
            case 19:
                return willValidateNames() ? Boolean.TRUE : Boolean.FALSE;
            case 20:
                return willFixContent() ? Boolean.TRUE : Boolean.FALSE;
            case 21:
                return getInvalidCharHandler();
            case 22:
                return getEmptyElementHandler();
            case 30:
            case 31:
                throw new IllegalStateException("Can not access per-stream-writer properties via factory");
        }
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public boolean setProperty(String name, int id, Object value) {
        switch (id) {
            case 1:
                enableAutomaticNamespaces(ArgUtil.convertToBoolean(name, value));
                return true;
            case 2:
                enableAutomaticEmptyElements(ArgUtil.convertToBoolean(name, value));
                return true;
            case 3:
                doAutoCloseOutput(ArgUtil.convertToBoolean(name, value));
                return true;
            case 4:
                doSupportNamespaces(ArgUtil.convertToBoolean(name, value));
                return true;
            case 5:
                setAutomaticNsPrefix(value.toString());
                return true;
            case 6:
                setTextEscaperFactory((EscapingWriterFactory) value);
                return true;
            case 7:
                setAttrValueEscaperFactory((EscapingWriterFactory) value);
                return true;
            case 8:
                setProblemReporter((XMLReporter) value);
                return true;
            case 9:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            default:
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            case 10:
                doUseDoubleQuotesInXmlDecl(ArgUtil.convertToBoolean(name, value));
                return true;
            case 11:
                doOutputCDataAsText(ArgUtil.convertToBoolean(name, value));
                return true;
            case 12:
                doCopyDefaultAttrs(ArgUtil.convertToBoolean(name, value));
                return true;
            case 13:
                doEscapeCr(ArgUtil.convertToBoolean(name, value));
                return true;
            case 14:
                doAddSpaceAfterEmptyElem(ArgUtil.convertToBoolean(name, value));
                return true;
            case 15:
                enableAutomaticEndElements(ArgUtil.convertToBoolean(name, value));
                return true;
            case 16:
                doValidateStructure(ArgUtil.convertToBoolean(name, value));
                return true;
            case 17:
                doValidateContent(ArgUtil.convertToBoolean(name, value));
                return true;
            case 18:
                doValidateAttributes(ArgUtil.convertToBoolean(name, value));
                return true;
            case 19:
                doValidateNames(ArgUtil.convertToBoolean(name, value));
                return true;
            case 20:
                doFixContent(ArgUtil.convertToBoolean(name, value));
                return true;
            case 21:
                setInvalidCharHandler((InvalidCharHandler) value);
                return true;
            case 22:
                setEmptyElementHandler((EmptyElementHandler) value);
                return true;
            case 30:
            case 31:
                throw new IllegalStateException("Can not modify per-stream-writer properties via factory");
        }
    }

    public int getConfigFlags() {
        return this.mConfigFlags;
    }

    public boolean automaticNamespacesEnabled() {
        return hasConfigFlag(2);
    }

    public boolean automaticEmptyElementsEnabled() {
        return hasConfigFlag(4);
    }

    public boolean willAutoCloseOutput() {
        return hasConfigFlag(8192);
    }

    public boolean willSupportNamespaces() {
        return hasConfigFlag(1);
    }

    public boolean willUseDoubleQuotesInXmlDecl() {
        return hasConfigFlag(16384);
    }

    public boolean willOutputCDataAsText() {
        return hasConfigFlag(8);
    }

    public boolean willCopyDefaultAttrs() {
        return hasConfigFlag(16);
    }

    public boolean willEscapeCr() {
        return hasConfigFlag(32);
    }

    public boolean willAddSpaceAfterEmptyElem() {
        return hasConfigFlag(64);
    }

    public boolean automaticEndElementsEnabled() {
        return hasConfigFlag(128);
    }

    public boolean willValidateStructure() {
        return hasConfigFlag(256);
    }

    public boolean willValidateContent() {
        return hasConfigFlag(512);
    }

    public boolean willValidateAttributes() {
        return hasConfigFlag(2048);
    }

    public boolean willValidateNames() {
        return hasConfigFlag(1024);
    }

    public boolean willFixContent() {
        return hasConfigFlag(4096);
    }

    public String getAutomaticNsPrefix() {
        String prefix = (String) getSpecialProperty(0);
        if (prefix == null) {
            prefix = DEFAULT_AUTOMATIC_NS_PREFIX;
        }
        return prefix;
    }

    public EscapingWriterFactory getTextEscaperFactory() {
        return (EscapingWriterFactory) getSpecialProperty(1);
    }

    public EscapingWriterFactory getAttrValueEscaperFactory() {
        return (EscapingWriterFactory) getSpecialProperty(2);
    }

    public XMLReporter getProblemReporter() {
        return (XMLReporter) getSpecialProperty(3);
    }

    public InvalidCharHandler getInvalidCharHandler() {
        return (InvalidCharHandler) getSpecialProperty(4);
    }

    public EmptyElementHandler getEmptyElementHandler() {
        return (EmptyElementHandler) getSpecialProperty(5);
    }

    public void enableAutomaticNamespaces(boolean state) {
        setConfigFlag(2, state);
    }

    public void enableAutomaticEmptyElements(boolean state) {
        setConfigFlag(4, state);
    }

    public void doAutoCloseOutput(boolean state) {
        setConfigFlag(8192, state);
    }

    public void doSupportNamespaces(boolean state) {
        setConfigFlag(1, state);
    }

    public void doUseDoubleQuotesInXmlDecl(boolean state) {
        setConfigFlag(16384, state);
    }

    public void doOutputCDataAsText(boolean state) {
        setConfigFlag(8, state);
    }

    public void doCopyDefaultAttrs(boolean state) {
        setConfigFlag(16, state);
    }

    public void doEscapeCr(boolean state) {
        setConfigFlag(32, state);
    }

    public void doAddSpaceAfterEmptyElem(boolean state) {
        setConfigFlag(64, state);
    }

    public void enableAutomaticEndElements(boolean state) {
        setConfigFlag(128, state);
    }

    public void doValidateStructure(boolean state) {
        setConfigFlag(256, state);
    }

    public void doValidateContent(boolean state) {
        setConfigFlag(512, state);
    }

    public void doValidateAttributes(boolean state) {
        setConfigFlag(2048, state);
    }

    public void doValidateNames(boolean state) {
        setConfigFlag(1024, state);
    }

    public void doFixContent(boolean state) {
        setConfigFlag(4096, state);
    }

    public void setAutomaticNsPrefix(String prefix) {
        setSpecialProperty(0, prefix);
    }

    public void setTextEscaperFactory(EscapingWriterFactory f) {
        setSpecialProperty(1, f);
    }

    public void setAttrValueEscaperFactory(EscapingWriterFactory f) {
        setSpecialProperty(2, f);
    }

    public void setProblemReporter(XMLReporter rep) {
        setSpecialProperty(3, rep);
    }

    public void setInvalidCharHandler(InvalidCharHandler h) {
        setSpecialProperty(4, h);
    }

    public void setEmptyElementHandler(EmptyElementHandler h) {
        setSpecialProperty(5, h);
    }

    public void configureForXmlConformance() {
        doValidateAttributes(true);
        doValidateContent(true);
        doValidateStructure(true);
        doValidateNames(true);
    }

    public void configureForRobustness() {
        doValidateAttributes(true);
        doValidateStructure(true);
        doValidateNames(true);
        doValidateContent(true);
        doFixContent(true);
    }

    public void configureForSpeed() {
        doValidateAttributes(false);
        doValidateContent(false);
        doValidateNames(false);
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
    }

    private final boolean hasConfigFlag(int flag) {
        return (this.mConfigFlags & flag) == flag;
    }

    private final Object getSpecialProperty(int ix) {
        if (this.mSpecialProperties == null) {
            return null;
        }
        return this.mSpecialProperties[ix];
    }

    private final void setSpecialProperty(int ix, Object value) {
        if (this.mSpecialProperties == null) {
            this.mSpecialProperties = new Object[6];
        }
        this.mSpecialProperties[ix] = value;
    }
}
