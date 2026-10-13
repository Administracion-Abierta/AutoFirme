package com.ctc.wstx.dtd;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.ent.ParsedExtEntity;
import com.ctc.wstx.ent.UnparsedExtEntity;
import com.ctc.wstx.evt.WNotationDeclaration;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import com.ctc.wstx.util.InternCache;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.TextBuffer;
import com.ctc.wstx.util.WordResolver;
import com.sun.jna.platform.win32.WinUser;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.codehaus.stax2.validation.XMLValidationProblem;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/FullDTDReader.class */
public class FullDTDReader extends MinimalDTDReader {
    static final boolean INTERN_SHARED_NAMES = false;
    static final Boolean ENTITY_EXP_GE = Boolean.FALSE;
    static final Boolean ENTITY_EXP_PE = Boolean.TRUE;
    final int mConfigFlags;
    final boolean mCfgSupportDTDPP;
    final boolean mCfgFullyValidating;
    HashMap<String, EntityDecl> mParamEntities;
    final HashMap<String, EntityDecl> mPredefdPEs;
    Set<String> mRefdPEs;
    HashMap<String, EntityDecl> mGeneralEntities;
    final HashMap<String, EntityDecl> mPredefdGEs;
    Set<String> mRefdGEs;
    boolean mUsesPredefdEntities;
    HashMap<String, NotationDeclaration> mNotations;
    final HashMap<String, NotationDeclaration> mPredefdNotations;
    boolean mUsesPredefdNotations;
    HashMap<String, Location> mNotationForwardRefs;
    HashMap<PrefixedName, PrefixedName> mSharedNames;
    LinkedHashMap<PrefixedName, DTDElement> mElements;
    HashMap<String, String> mSharedEnumValues;
    DefaultAttrValue mCurrAttrDefault;
    boolean mExpandingPE;
    TextBuffer mValueBuffer;
    int mIncludeCount;
    boolean mCheckForbiddenPEs;
    String mCurrDeclaration;
    boolean mAnyDTDppFeatures;
    String mDefaultNsURI;
    HashMap<String, String> mNamespaces;
    DTDWriter mFlattenWriter;
    final DTDEventListener mEventListener;
    transient TextBuffer mTextBuffer;
    final PrefixedName mAccessKey;

    private FullDTDReader(WstxInputSource input, ReaderConfig cfg, boolean constructFully, int xmlVersion) {
        this(input, cfg, false, null, constructFully, xmlVersion);
    }

    private FullDTDReader(WstxInputSource input, ReaderConfig cfg, DTDSubset intSubset, boolean constructFully, int xmlVersion) {
        this(input, cfg, true, intSubset, constructFully, xmlVersion);
        input.initInputLocation(this, this.mCurrDepth, 0);
    }

    private FullDTDReader(WstxInputSource input, ReaderConfig cfg, boolean isExt, DTDSubset intSubset, boolean constructFully, int xmlVersion) {
        super(input, cfg, isExt);
        this.mUsesPredefdEntities = false;
        this.mUsesPredefdNotations = false;
        this.mSharedNames = null;
        this.mSharedEnumValues = null;
        this.mCurrAttrDefault = null;
        this.mExpandingPE = false;
        this.mValueBuffer = null;
        this.mIncludeCount = 0;
        this.mCheckForbiddenPEs = false;
        this.mAnyDTDppFeatures = false;
        this.mDefaultNsURI = "";
        this.mNamespaces = null;
        this.mFlattenWriter = null;
        this.mTextBuffer = null;
        this.mAccessKey = new PrefixedName(null, null);
        this.mDocXmlVersion = xmlVersion;
        this.mXml11 = cfg.isXml11();
        int cfgFlags = cfg.getConfigFlags();
        this.mConfigFlags = cfgFlags;
        this.mCfgSupportDTDPP = (cfgFlags & 524288) != 0;
        this.mCfgFullyValidating = constructFully;
        this.mUsesPredefdEntities = false;
        this.mParamEntities = null;
        this.mRefdPEs = null;
        this.mRefdGEs = null;
        this.mGeneralEntities = null;
        HashMap<String, EntityDecl> pes = intSubset == null ? null : intSubset.getParameterEntityMap();
        if (pes == null || pes.isEmpty()) {
            this.mPredefdPEs = null;
        } else {
            this.mPredefdPEs = pes;
        }
        HashMap<String, EntityDecl> ges = intSubset == null ? null : intSubset.getGeneralEntityMap();
        if (ges == null || ges.isEmpty()) {
            this.mPredefdGEs = null;
        } else {
            this.mPredefdGEs = ges;
        }
        HashMap<String, NotationDeclaration> not = intSubset == null ? null : intSubset.getNotationMap();
        if (not == null || not.isEmpty()) {
            this.mPredefdNotations = null;
        } else {
            this.mPredefdNotations = not;
        }
        this.mEventListener = this.mConfig.getDTDEventListener();
    }

    public static DTDSubset readInternalSubset(WstxInputData srcData, WstxInputSource input, ReaderConfig cfg, boolean constructFully, int xmlVersion) throws XMLStreamException {
        FullDTDReader r = new FullDTDReader(input, cfg, constructFully, xmlVersion);
        r.copyBufferStateFrom(srcData);
        try {
            DTDSubset ss = r.parseDTD();
            return ss;
        } finally {
            srcData.copyBufferStateFrom(r);
        }
    }

    public static DTDSubset readExternalSubset(WstxInputSource src, ReaderConfig cfg, DTDSubset intSubset, boolean constructFully, int xmlVersion) throws XMLStreamException {
        FullDTDReader r = new FullDTDReader(src, cfg, intSubset, constructFully, xmlVersion);
        return r.parseDTD();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static DTDSubset flattenExternalSubset(WstxInputSource src, Writer flattenWriter, boolean inclComments, boolean inclConditionals, boolean inclPEs) throws XMLStreamException, IOException {
        ReaderConfig cfg = ReaderConfig.createFullDefaults();
        FullDTDReader r = new FullDTDReader(src, cfg.createNonShared(new SymbolTable()), null, true, 0);
        r.setFlattenWriter(flattenWriter, inclComments, inclConditionals, inclPEs);
        DTDSubset ss = r.parseDTD();
        r.flushFlattenWriter();
        flattenWriter.flush();
        return ss;
    }

    private TextBuffer getTextBuffer() {
        if (this.mTextBuffer == null) {
            this.mTextBuffer = TextBuffer.createTemporaryBuffer();
            this.mTextBuffer.resetInitialized();
        } else {
            this.mTextBuffer.resetWithEmpty();
        }
        return this.mTextBuffer;
    }

    public void setFlattenWriter(Writer w, boolean inclComments, boolean inclConditionals, boolean inclPEs) {
        this.mFlattenWriter = new DTDWriter(w, inclComments, inclConditionals, inclPEs);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void flushFlattenWriter() throws XMLStreamException {
        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr);
    }

    @Override // com.ctc.wstx.dtd.MinimalDTDReader
    public EntityDecl findEntity(String entName) {
        EntityDecl decl;
        if (this.mPredefdGEs != null && (decl = this.mPredefdGEs.get(entName)) != null) {
            return decl;
        }
        return this.mGeneralEntities.get(entName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:100:0x00eb A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:103:0x0000 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:18:0x006b  */
    /* JADX WARN: Code duplicated, block: B:25:0x008a  */
    /* JADX WARN: Code duplicated, block: B:32:0x00a2  */
    /* JADX WARN: Code duplicated, block: B:37:0x00b7  */
    /* JADX WARN: Code duplicated, block: B:40:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:43:0x00e0 A[Catch: all -> 0x0120, TryCatch #0 {all -> 0x0120, blocks: (B:41:0x00d5, B:43:0x00e0, B:45:0x00eb, B:48:0x0107), top: B:85:0x00d5 }] */
    /* JADX WARN: Code duplicated, block: B:50:0x0112  */
    /* JADX WARN: Code duplicated, block: B:59:0x013b  */
    /* JADX WARN: Code duplicated, block: B:88:0x002b A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:89:0x0024 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:90:0x0084 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:91:0x007d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:92:0x0076 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:93:0x0054 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:94:0x0134 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:96:0x0134 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:97:0x0107 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:98:0x0107 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:0x00f9 A[SYNTHETIC] */
    protected DTDSubset parseDTD() throws XMLStreamException {
        boolean z;
        boolean z2;
        boolean suppress;
        char c;
        DTDSubset ss;
        while (true) {
            this.mCheckForbiddenPEs = false;
            int i = getNextAfterWS();
            if (i < 0) {
                if (this.mIsExternal) {
                    break;
                }
                throwUnexpectedEOF(ParsingErrorMsgs.SUFFIX_IN_DTD_INTERNAL);
                if (i == 37) {
                    expandPE();
                } else {
                    this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                    this.mTokenInputRow = this.mCurrInputRow;
                    this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                    if (i == 60) {
                        if (this.mIsExternal) {
                            z = false;
                        } else {
                            z = false;
                        }
                        this.mCheckForbiddenPEs = z;
                        if (this.mFlattenWriter == null) {
                            parseDirective();
                        } else {
                            parseDirectiveFlattened();
                        }
                    } else {
                        if (i != 93) {
                            if (this.mIncludeCount != 0) {
                            }
                            if (this.mIncludeCount <= 0) {
                                if (this.mFlattenWriter != null) {
                                    z2 = false;
                                } else {
                                    z2 = false;
                                }
                                suppress = z2;
                                if (suppress) {
                                    this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
                                    this.mFlattenWriter.disableOutput();
                                }
                                c = dtdNextFromCurr();
                                if (c == ']') {
                                    c = dtdNextFromCurr();
                                    if (c == '>') {
                                        this.mIncludeCount--;
                                        if (suppress) {
                                            this.mFlattenWriter.enableOutput(this.mInputPtr);
                                        }
                                    }
                                }
                                throwDTDUnexpectedChar(c, "; expected ']]>' to close conditional include section");
                                if (suppress) {
                                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                                }
                            }
                        }
                        if (this.mIsExternal) {
                            throwDTDUnexpectedChar(i, "; expected a '<' to start a directive");
                        }
                        throwDTDUnexpectedChar(i, "; expected a '<' to start a directive, or \"]>\" to end internal subset");
                    }
                }
            } else if (i == 37) {
                expandPE();
            } else {
                this.mTokenInputTotal = this.mCurrInputProcessed + ((long) this.mInputPtr);
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                if (i == 60) {
                    if (this.mIsExternal || this.mInput != this.mRootInput) {
                        z = false;
                    } else {
                        z = true;
                    }
                    this.mCheckForbiddenPEs = z;
                    if (this.mFlattenWriter == null) {
                        parseDirective();
                    } else {
                        parseDirectiveFlattened();
                    }
                } else {
                    if (i != 93) {
                        if (this.mIncludeCount != 0 && !this.mIsExternal) {
                            break;
                        }
                        if (this.mIncludeCount <= 0) {
                            if (this.mFlattenWriter != null || this.mFlattenWriter.includeConditionals()) {
                                z2 = false;
                            } else {
                                z2 = true;
                            }
                            suppress = z2;
                            if (suppress) {
                                this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
                                this.mFlattenWriter.disableOutput();
                            }
                            try {
                                c = dtdNextFromCurr();
                                if (c == ']') {
                                    c = dtdNextFromCurr();
                                    if (c == '>') {
                                        this.mIncludeCount--;
                                        if (suppress) {
                                            this.mFlattenWriter.enableOutput(this.mInputPtr);
                                        }
                                    }
                                }
                                throwDTDUnexpectedChar(c, "; expected ']]>' to close conditional include section");
                                if (suppress) {
                                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                                }
                            } catch (Throwable th) {
                                if (suppress) {
                                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                                }
                                throw th;
                            }
                        }
                    }
                    if (this.mIsExternal) {
                        throwDTDUnexpectedChar(i, "; expected a '<' to start a directive");
                    }
                    throwDTDUnexpectedChar(i, "; expected a '<' to start a directive, or \"]>\" to end internal subset");
                }
            }
        }
        if (this.mIncludeCount > 0) {
            String suffix = this.mIncludeCount == 1 ? "an INCLUDE block" : "" + this.mIncludeCount + " INCLUDE blocks";
            throwUnexpectedEOF(getErrorMsg() + "; expected closing marker for " + suffix);
        }
        if (this.mNotationForwardRefs != null && this.mNotationForwardRefs.size() > 0) {
            _reportUndefinedNotationRefs();
        }
        if (this.mIsExternal) {
            boolean cachable = (this.mUsesPredefdEntities || this.mUsesPredefdNotations) ? false : true;
            ss = DTDSubsetImpl.constructInstance(cachable, this.mGeneralEntities, this.mRefdGEs, null, this.mRefdPEs, this.mNotations, this.mElements, this.mCfgFullyValidating);
        } else {
            ss = DTDSubsetImpl.constructInstance(false, this.mGeneralEntities, null, this.mParamEntities, null, this.mNotations, this.mElements, this.mCfgFullyValidating);
        }
        return ss;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void parseDirective() throws XMLStreamException {
        char c = dtdNextFromCurr();
        if (c == '?') {
            readPI();
            return;
        }
        if (c != '!') {
            throwDTDUnexpectedChar(c, "; expected '!' to start a directive");
        }
        char c2 = dtdNextFromCurr();
        if (c2 == '-') {
            char c3 = dtdNextFromCurr();
            if (c3 != '-') {
                throwDTDUnexpectedChar(c3, "; expected '-' for a comment");
            }
            if (this.mEventListener != null && this.mEventListener.dtdReportComments()) {
                readComment(this.mEventListener);
                return;
            } else {
                skipComment();
                return;
            }
        }
        if (c2 == '[') {
            checkInclusion();
        } else if (c2 >= 'A' && c2 <= 'Z') {
            handleDeclaration(c2);
        } else {
            throwDTDUnexpectedChar(c2, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void parseDirectiveFlattened() throws XMLStreamException {
        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
        this.mFlattenWriter.disableOutput();
        char c = dtdNextFromCurr();
        if (c == '?') {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<?");
            readPI();
            return;
        }
        if (c != '!') {
            throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
        }
        char c2 = dtdNextFromCurr();
        if (c2 == '-') {
            char c3 = dtdNextFromCurr();
            if (c3 != '-') {
                throwDTDUnexpectedChar(c3, "; expected '-' for a comment");
            }
            boolean comm = this.mFlattenWriter.includeComments();
            if (comm) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<!--");
            }
            try {
                skipComment();
                if (!comm) {
                    return;
                } else {
                    return;
                }
            } finally {
                if (!comm) {
                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                }
            }
        }
        if (c2 == '[') {
            boolean cond = this.mFlattenWriter.includeConditionals();
            if (cond) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<![");
            }
            try {
                checkInclusion();
                if (!cond) {
                    return;
                } else {
                    return;
                }
            } finally {
                if (!cond) {
                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                }
            }
        }
        boolean filterPEs = c2 == 'E' && !this.mFlattenWriter.includeParamEntities();
        if (filterPEs) {
            handleSuppressedDeclaration();
            return;
        }
        if (c2 >= 'A' && c2 <= 'Z') {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!");
            this.mFlattenWriter.output(c2);
            handleDeclaration(c2);
            return;
        }
        throwDTDUnexpectedChar(c2, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.StreamScanner
    protected void initInputSource(WstxInputSource newInput, boolean isExt, String entityId) throws XMLStreamException {
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr);
            this.mFlattenWriter.disableOutput();
            try {
                super.initInputSource(newInput, isExt, entityId);
                return;
            } finally {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        }
        super.initInputSource(newInput, isExt, entityId);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.StreamScanner
    protected boolean loadMore() throws XMLStreamException {
        WstxInputSource input = this.mInput;
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        do {
            this.mCurrInputProcessed += (long) this.mInputEnd;
            this.mCurrInputRowStart -= this.mInputEnd;
            try {
                int count = input.readInto(this);
                if (count > 0) {
                    if (this.mFlattenWriter != null) {
                        this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                        return true;
                    }
                    return true;
                }
                input.close();
                if (input == this.mRootInput) {
                    return false;
                }
                WstxInputSource parent = input.getParent();
                if (parent == null) {
                    throwNullParent(input);
                }
                if (this.mCurrDepth != input.getScopeId()) {
                    handleIncompleteEntityProblem(input);
                }
                input = parent;
                this.mInput = parent;
                input.restoreContext(this);
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                }
                this.mInputTopDepth = input.getScopeId();
                if (!this.mNormalizeLFs) {
                    this.mNormalizeLFs = !input.fromInternalEntity();
                }
            } catch (IOException ioe) {
                throw constructFromIOE(ioe);
            }
        } while (this.mInputPtr >= this.mInputEnd);
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.StreamScanner
    protected boolean loadMoreFromCurrent() throws XMLStreamException {
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        this.mCurrInputProcessed += (long) this.mInputEnd;
        this.mCurrInputRowStart -= this.mInputEnd;
        try {
            int count = this.mInput.readInto(this);
            if (count > 0) {
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                    return true;
                }
                return true;
            }
            return false;
        } catch (IOException ie) {
            throwFromIOE(ie);
            return false;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.StreamScanner
    protected boolean ensureInput(int minAmount) throws XMLStreamException {
        int currAmount = this.mInputEnd - this.mInputPtr;
        if (currAmount >= minAmount) {
            return true;
        }
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        try {
            if (this.mInput.readMore(this, minAmount)) {
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(currAmount);
                    return true;
                }
                return true;
            }
            return false;
        } catch (IOException ie) {
            throwFromIOE(ie);
            return false;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void loadMoreScoped(WstxInputSource currScope, String entityName, Location loc) throws XMLStreamException {
        boolean check = this.mInput == currScope;
        loadMore(getErrorMsg());
        if (check && this.mInput != currScope) {
            _reportWFCViolation("Unterminated entity value for entity '" + entityName + "' (definition started at " + loc + ")");
        }
    }

    private char dtdNextIfAvailable() throws XMLStreamException {
        char c;
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            c = cArr[i];
        } else {
            int i2 = peekNext();
            if (i2 < 0) {
                return (char) 0;
            }
            this.mInputPtr++;
            c = (char) i2;
        }
        if (c == 0) {
            throwNullChar();
        }
        return c;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private char getNextExpanded() throws XMLStreamException {
        char nextChar;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(getErrorMsg());
            }
            char c = nextChar;
            if (c != '%') {
                return c;
            }
            expandPE();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private char skipDtdWs(boolean handlePEs) throws XMLStreamException {
        char nextChar;
        char c;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(getErrorMsg());
            }
            c = nextChar;
            if (c > ' ') {
                if (c != '%' || !handlePEs) {
                    break;
                }
                expandPE();
            } else if (c == '\n' || c == '\r') {
                skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                throwInvalidSpace(c);
            }
        }
        return c;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private char skipObligatoryDtdWs() throws XMLStreamException {
        char c;
        char nextChar;
        int i = peekNext();
        if (i == -1) {
            c = getNextChar(getErrorMsg());
            if (c > ' ' && c != '%') {
                return c;
            }
        } else {
            char[] cArr = this.mInputBuffer;
            int i2 = this.mInputPtr;
            this.mInputPtr = i2 + 1;
            c = cArr[i2];
            if (c > ' ' && c != '%') {
                throwDTDUnexpectedChar(c, "; expected a separating white space");
            }
        }
        while (true) {
            if (c == '%') {
                expandPE();
            } else if (c <= ' ') {
                if (c == '\n' || c == '\r') {
                    skipCRLF(c);
                } else if (c != ' ' && c != '\t') {
                    throwInvalidSpace(c);
                }
            } else {
                return c;
            }
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr2 = this.mInputBuffer;
                int i3 = this.mInputPtr;
                this.mInputPtr = i3 + 1;
                nextChar = cArr2[i3];
            } else {
                nextChar = getNextChar(getErrorMsg());
            }
            c = nextChar;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void expandPE() throws XMLStreamException {
        char cDtdNextFromCurr;
        String id;
        char cDtdNextFromCurr2;
        char c;
        char cDtdNextFromCurr3;
        char cDtdNextFromCurr4;
        if (this.mCheckForbiddenPEs) {
            throwForbiddenPE();
        }
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
            this.mFlattenWriter.disableOutput();
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cDtdNextFromCurr3 = cArr[i];
            } else {
                cDtdNextFromCurr3 = dtdNextFromCurr();
            }
            char c2 = cDtdNextFromCurr3;
            id = readDTDName(c2);
            try {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr2 = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    cDtdNextFromCurr4 = cArr2[i2];
                } else {
                    cDtdNextFromCurr4 = dtdNextFromCurr();
                }
                c = cDtdNextFromCurr4;
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            } catch (Throwable th) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                throw th;
            }
        } else {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr3 = this.mInputBuffer;
                int i3 = this.mInputPtr;
                this.mInputPtr = i3 + 1;
                cDtdNextFromCurr = cArr3[i3];
            } else {
                cDtdNextFromCurr = dtdNextFromCurr();
            }
            char c3 = cDtdNextFromCurr;
            id = readDTDName(c3);
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr4 = this.mInputBuffer;
                int i4 = this.mInputPtr;
                this.mInputPtr = i4 + 1;
                cDtdNextFromCurr2 = cArr4[i4];
            } else {
                cDtdNextFromCurr2 = dtdNextFromCurr();
            }
            c = cDtdNextFromCurr2;
        }
        if (c != ';') {
            throwDTDUnexpectedChar(c, "; expected ';' to end parameter entity name");
        }
        this.mExpandingPE = true;
        expandEntity(id, true, ENTITY_EXP_PE);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String checkDTDKeyword(String exp) throws XMLStreamException {
        int i = 0;
        int len = exp.length();
        char c = ' ';
        while (i < len) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i2 = this.mInputPtr;
                this.mInputPtr = i2 + 1;
                c = cArr[i2];
            } else {
                c = dtdNextIfAvailable();
                if (c == 0) {
                    return exp.substring(0, i);
                }
            }
            if (c != exp.charAt(i)) {
                break;
            }
            i++;
        }
        if (i == len) {
            c = dtdNextIfAvailable();
            if (c == 0) {
                return null;
            }
            if (!isNameChar(c)) {
                this.mInputPtr--;
                return null;
            }
        }
        StringBuilder sb = new StringBuilder(exp.substring(0, i));
        sb.append(c);
        while (true) {
            char c2 = dtdNextIfAvailable();
            if (c2 == 0) {
                break;
            }
            if (!isNameChar(c2) && c2 != ':') {
                this.mInputPtr--;
                break;
            }
            sb.append(c2);
        }
        return sb.toString();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String readDTDKeyword(String prefix) throws XMLStreamException {
        char c;
        StringBuilder sb = new StringBuilder(prefix);
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                c = cArr[i];
            } else {
                c = dtdNextIfAvailable();
                if (c != 0) {
                    break;
                }
                break;
            }
            if (!isNameChar(c) && c != ':') {
                this.mInputPtr--;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private boolean checkPublicSystemKeyword(char c) throws XMLStreamException {
        String errId;
        if (c == 'P') {
            String errId2 = checkDTDKeyword("UBLIC");
            if (errId2 == null) {
                return true;
            }
            errId = "P" + errId2;
        } else if (c == 'S') {
            String errId3 = checkDTDKeyword("YSTEM");
            if (errId3 == null) {
                return false;
            }
            errId = "S" + errId3;
        } else {
            if (!isNameStartChar(c)) {
                throwDTDUnexpectedChar(c, "; expected 'PUBLIC' or 'SYSTEM' keyword");
            }
            errId = readDTDKeyword(String.valueOf(c));
        }
        _reportWFCViolation("Unrecognized keyword '" + errId + "'; expected 'PUBLIC' or 'SYSTEM'");
        return false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private String readDTDName(char c) throws XMLStreamException {
        if (!isNameStartChar(c)) {
            throwDTDUnexpectedChar(c, ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
        }
        return parseFullName(c);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private String readDTDLocalName(char c, boolean checkChar) throws XMLStreamException {
        if (checkChar && !isNameStartChar(c)) {
            throwDTDUnexpectedChar(c, ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
        }
        return parseLocalName(c);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private String readDTDNmtoken(char c) throws XMLStreamException {
        char[] outBuf = getNameBuffer(64);
        int outLen = outBuf.length;
        int outPtr = 0;
        while (true) {
            if (!isNameChar(c) && c != ':') {
                if (outPtr == 0) {
                    throwDTDUnexpectedChar(c, "; expected a NMTOKEN character to start a NMTOKEN");
                }
                this.mInputPtr--;
                break;
            }
            if (outPtr >= outLen) {
                outBuf = expandBy50Pct(outBuf);
                outLen = outBuf.length;
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = c;
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i2 = this.mInputPtr;
                this.mInputPtr = i2 + 1;
                c = cArr[i2];
            } else {
                c = dtdNextIfAvailable();
                if (c == 0) {
                    break;
                }
            }
        }
        return new String(outBuf, 0, outPtr);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private PrefixedName readDTDQName(char firstChar) throws XMLStreamException {
        String localName;
        String prefix;
        if (!this.mCfgNsEnabled) {
            prefix = null;
            localName = parseFullName(firstChar);
        } else {
            localName = parseLocalName(firstChar);
            char c = dtdNextIfAvailable();
            if (c == 0) {
                prefix = null;
            } else if (c == ':') {
                prefix = localName;
                localName = parseLocalName(dtdNextFromCurr());
            } else {
                prefix = null;
                this.mInputPtr--;
            }
        }
        return findSharedName(prefix, localName);
    }

    private char readArity() throws XMLStreamException {
        char nextChar;
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            nextChar = cArr[i];
        } else {
            nextChar = getNextChar(getErrorMsg());
        }
        char c = nextChar;
        if (c == '?' || c == '*' || c == '+') {
            return c;
        }
        this.mInputPtr--;
        return ' ';
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:53:0x0149  */
    private char[] parseEntityValue(String id, Location loc, char quoteChar) throws XMLStreamException {
        WstxInputSource currScope = this.mInput;
        TextBuffer tb = this.mValueBuffer;
        if (tb == null) {
            tb = TextBuffer.createTemporaryBuffer();
        }
        tb.resetInitialized();
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        while (true) {
            if (this.mInputPtr >= this.mInputEnd) {
                loadMoreScoped(currScope, id, loc);
            }
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            char c = cArr[i];
            if (c < '?') {
                if (c == quoteChar) {
                    if (this.mInput == currScope) {
                        break;
                    }
                } else if (c == '&') {
                    int d = resolveCharOnlyEntity(false);
                    if (d != 0) {
                        if (d <= 65535) {
                            c = (char) d;
                        } else {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int d2 = d - 65536;
                            int i2 = outPtr;
                            outPtr++;
                            outBuf[i2] = (char) ((d2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                            c = (char) ((d2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                        }
                    } else {
                        boolean first = true;
                        while (true) {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int i3 = outPtr;
                            outPtr++;
                            outBuf[i3] = c;
                            if (this.mInputPtr >= this.mInputEnd) {
                                loadMoreScoped(currScope, id, loc);
                            }
                            char[] cArr2 = this.mInputBuffer;
                            int i4 = this.mInputPtr;
                            this.mInputPtr = i4 + 1;
                            c = cArr2[i4];
                            if (c == ';') {
                                break;
                            }
                            if (first) {
                                first = false;
                                if (!isNameStartChar(c)) {
                                    if (c == ':' || this.mCfgNsEnabled) {
                                        if (first) {
                                            throwDTDUnexpectedChar(c, "; expected entity name after '&'");
                                        }
                                        throwDTDUnexpectedChar(c, "; expected semi-colon after entity name");
                                    }
                                }
                            } else if (!isNameChar(c)) {
                                if (c == ':') {
                                }
                                if (first) {
                                    throwDTDUnexpectedChar(c, "; expected entity name after '&'");
                                }
                                throwDTDUnexpectedChar(c, "; expected semi-colon after entity name");
                            }
                        }
                    }
                } else if (c == '%') {
                    expandPE();
                } else if (c < ' ') {
                    if (c == '\n') {
                        markLF();
                    } else if (c == '\r') {
                        if (skipCRLF(c)) {
                            if (!this.mNormalizeLFs) {
                                if (outPtr >= outBuf.length) {
                                    outBuf = tb.finishCurrentSegment();
                                    outPtr = 0;
                                }
                                int i5 = outPtr;
                                outPtr++;
                                outBuf[i5] = c;
                            }
                            c = '\n';
                        } else if (this.mNormalizeLFs) {
                            c = '\n';
                        }
                    } else if (c != '\t') {
                        throwInvalidSpace(c);
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
        tb.setCurrentLength(outPtr);
        char c2 = skipDtdWs(true);
        if (c2 != '>') {
            throwDTDUnexpectedChar(c2, "; expected closing '>' after ENTITY declaration");
        }
        char[] result = tb.contentsAsArray();
        this.mValueBuffer = tb;
        return result;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void parseAttrDefaultValue(DefaultAttrValue defVal, char quoteChar, PrefixedName attrName, Location loc, boolean gotFixed) throws XMLStreamException {
        int d;
        if (quoteChar != '\"' && quoteChar != '\'') {
            String msg = "; expected a single or double quote to enclose the default value";
            if (!gotFixed) {
                msg = msg + ", or one of keywords (#REQUIRED, #IMPLIED, #FIXED)";
            }
            throwDTDUnexpectedChar(quoteChar, msg + " (for attribute '" + attrName + "')");
        }
        WstxInputSource currScope = this.mInput;
        TextBuffer tb = this.mValueBuffer;
        if (tb == null) {
            tb = TextBuffer.createTemporaryBuffer();
        }
        tb.resetInitialized();
        int outPtr = 0;
        char[] outBuf = tb.getCurrentSegment();
        int outLen = outBuf.length;
        while (true) {
            if (this.mInputPtr >= this.mInputEnd) {
                boolean check = this.mInput == currScope;
                loadMore(getErrorMsg());
                if (check && this.mInput != currScope) {
                    _reportWFCViolation("Unterminated attribute default value for attribute '" + attrName + "' (definition started at " + loc + ")");
                }
            }
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            char c = cArr[i];
            if (c < '?') {
                if (c <= ' ') {
                    if (c == '\n') {
                        markLF();
                    } else if (c == '\r') {
                        if (getNextChar(ParsingErrorMsgs.SUFFIX_IN_DEF_ATTR_VALUE) != '\n') {
                            this.mInputPtr--;
                            char c2 = this.mNormalizeLFs ? '\n' : '\r';
                        }
                        markLF();
                    } else if (c != ' ' && c != '\t') {
                        throwInvalidSpace(c);
                    }
                    c = ' ';
                } else if (c == quoteChar) {
                    if (this.mInput == currScope) {
                        tb.setCurrentLength(outPtr);
                        defVal.setValue(tb.contentsAsString());
                        this.mValueBuffer = tb;
                        return;
                    }
                } else if (c == '&') {
                    if (inputInBuffer() >= 3) {
                        d = resolveSimpleEntity(true);
                    } else {
                        d = resolveCharOnlyEntity(true);
                    }
                    if (d == 0) {
                        String id = parseEntityName(getNextChar(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF));
                        try {
                            this.mCurrAttrDefault = defVal;
                            this.mExpandingPE = false;
                            expandEntity(id, false, ENTITY_EXP_GE);
                            this.mCurrAttrDefault = null;
                        } catch (Throwable th) {
                            this.mCurrAttrDefault = null;
                            throw th;
                        }
                    } else if (d <= 65535) {
                        c = (char) d;
                    } else {
                        if (outPtr >= outBuf.length) {
                            outBuf = tb.finishCurrentSegment();
                            outPtr = 0;
                        }
                        int d2 = d - 65536;
                        int i2 = outPtr;
                        outPtr++;
                        outBuf[i2] = (char) ((d2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                        c = (char) ((d2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                    }
                } else if (c == '<') {
                    throwDTDUnexpectedChar(c, ParsingErrorMsgs.SUFFIX_IN_DEF_ATTR_VALUE);
                }
            }
            if (outPtr >= outLen) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
                outLen = outBuf.length;
            }
            int i3 = outPtr;
            outPtr++;
            outBuf[i3] = c;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void readPI() throws XMLStreamException {
        char cDtdNextFromCurr;
        char cDtdNextFromCurr2;
        char c;
        char cDtdNextFromCurr3;
        char cDtdNextFromCurr4;
        char cDtdNextFromCurr5;
        String target = parseFullName();
        if (target.length() == 0) {
            _reportWFCViolation(ErrorConsts.ERR_WF_PI_MISSING_TARGET);
        }
        if (target.equalsIgnoreCase("xml")) {
            _reportWFCViolation(ErrorConsts.ERR_WF_PI_XML_TARGET, target);
        }
        char c2 = dtdNextFromCurr();
        if (!isSpaceChar(c2)) {
            if (c2 != '?' || dtdNextFromCurr() != '>') {
                throwUnexpectedChar(c2, ErrorConsts.ERR_WF_PI_XML_MISSING_SPACE);
            }
            if (this.mEventListener != null) {
                this.mEventListener.dtdProcessingInstruction(target, "");
                return;
            }
            return;
        }
        if (this.mEventListener == null) {
            while (true) {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr = this.mInputBuffer;
                    int i = this.mInputPtr;
                    this.mInputPtr = i + 1;
                    cDtdNextFromCurr4 = cArr[i];
                } else {
                    cDtdNextFromCurr4 = dtdNextFromCurr();
                }
                char c3 = cDtdNextFromCurr4;
                if (c3 == '?') {
                    do {
                        if (this.mInputPtr < this.mInputEnd) {
                            char[] cArr2 = this.mInputBuffer;
                            int i2 = this.mInputPtr;
                            this.mInputPtr = i2 + 1;
                            cDtdNextFromCurr5 = cArr2[i2];
                        } else {
                            cDtdNextFromCurr5 = dtdNextFromCurr();
                        }
                        c3 = cDtdNextFromCurr5;
                    } while (c3 == '?');
                    if (c3 == '>') {
                        return;
                    }
                }
                if (c3 < ' ') {
                    if (c3 == '\n' || c3 == '\r') {
                        skipCRLF(c3);
                    } else if (c3 != '\t') {
                        throwInvalidSpace(c3);
                    }
                }
            }
        } else {
            while (c2 <= ' ') {
                if (c2 == '\n' || c2 == '\r') {
                    skipCRLF(c2);
                } else if (c2 != '\t' && c2 != ' ') {
                    throwInvalidSpace(c2);
                }
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr3 = this.mInputBuffer;
                    int i3 = this.mInputPtr;
                    this.mInputPtr = i3 + 1;
                    cDtdNextFromCurr3 = cArr3[i3];
                } else {
                    cDtdNextFromCurr3 = dtdNextFromCurr();
                }
                c2 = cDtdNextFromCurr3;
            }
            TextBuffer tb = getTextBuffer();
            char[] outBuf = tb.getCurrentSegment();
            int outPtr = 0;
            while (true) {
                if (c2 == '?') {
                    while (true) {
                        if (this.mInputPtr < this.mInputEnd) {
                            char[] cArr4 = this.mInputBuffer;
                            int i4 = this.mInputPtr;
                            this.mInputPtr = i4 + 1;
                            cDtdNextFromCurr2 = cArr4[i4];
                        } else {
                            cDtdNextFromCurr2 = dtdNextFromCurr();
                        }
                        c = cDtdNextFromCurr2;
                        if (c != '?') {
                            break;
                        }
                        if (outPtr >= outBuf.length) {
                            outBuf = tb.finishCurrentSegment();
                            outPtr = 0;
                        }
                        int i5 = outPtr;
                        outPtr++;
                        outBuf[i5] = c;
                    }
                    if (c != '>') {
                        this.mInputPtr--;
                        c2 = '?';
                    } else {
                        tb.setCurrentLength(outPtr);
                        String data = tb.contentsAsString();
                        this.mEventListener.dtdProcessingInstruction(target, data);
                        return;
                    }
                } else if (c2 < ' ') {
                    if (c2 == '\n' || c2 == '\r') {
                        skipCRLF(c2);
                        c2 = '\n';
                    } else if (c2 != '\t') {
                        throwInvalidSpace(c2);
                    }
                }
                if (outPtr >= outBuf.length) {
                    outBuf = tb.finishCurrentSegment();
                    outPtr = 0;
                }
                int i6 = outPtr;
                outPtr++;
                outBuf[i6] = c2;
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr5 = this.mInputBuffer;
                    int i7 = this.mInputPtr;
                    this.mInputPtr = i7 + 1;
                    cDtdNextFromCurr = cArr5[i7];
                } else {
                    cDtdNextFromCurr = dtdNextFromCurr();
                }
                c2 = cDtdNextFromCurr;
            }
        }
    }

    protected void readComment(DTDEventListener l) throws XMLStreamException {
        char cDtdNextFromCurr;
        TextBuffer tb = getTextBuffer();
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = 0;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cDtdNextFromCurr = cArr[i];
            } else {
                cDtdNextFromCurr = dtdNextFromCurr();
            }
            char c = cDtdNextFromCurr;
            if (c < ' ') {
                if (c == '\n' || c == '\r') {
                    skipCRLF(c);
                    c = '\n';
                } else if (c != '\t') {
                    throwInvalidSpace(c);
                }
            } else if (c == '-') {
                if (dtdNextFromCurr() == '-') {
                    break;
                }
                c = '-';
                this.mInputPtr--;
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            int i2 = outPtr;
            outPtr++;
            outBuf[i2] = c;
        }
        if (dtdNextFromCurr() != '>') {
            throwParseError(ErrorConsts.ERR_HYPHENS_IN_COMMENT);
        }
        tb.setCurrentLength(outPtr);
        tb.fireDtdCommentEvent(l);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void checkInclusion() throws XMLStreamException {
        String keyword;
        if (!this.mIsExternal && this.mInput == this.mRootInput) {
            _reportWFCViolation("Internal DTD subset can not use (INCLUDE/IGNORE) directives (except via external entities)");
        }
        char c = skipDtdWs(true);
        if (c != 'I') {
            keyword = readDTDKeyword(String.valueOf(c));
        } else {
            char c2 = dtdNextFromCurr();
            if (c2 == 'G') {
                String keyword2 = checkDTDKeyword("NORE");
                if (keyword2 == null) {
                    handleIgnored();
                    return;
                }
                keyword = "IG" + keyword2;
            } else if (c2 == 'N') {
                String keyword3 = checkDTDKeyword("CLUDE");
                if (keyword3 == null) {
                    handleIncluded();
                    return;
                }
                keyword = "IN" + keyword3;
            } else {
                this.mInputPtr--;
                keyword = readDTDKeyword("I");
            }
        }
        _reportWFCViolation("Unrecognized directive '" + keyword + "'; expected either 'IGNORE' or 'INCLUDE'");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleIncluded() throws XMLStreamException {
        char c = skipDtdWs(false);
        if (c != '[') {
            throwDTDUnexpectedChar(c, "; expected '[' to follow 'INCLUDE' directive");
        }
        this.mIncludeCount++;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleIgnored() throws XMLStreamException {
        char nextChar;
        char c = skipDtdWs(false);
        int count = 1;
        if (c != '[') {
            throwDTDUnexpectedChar(c, "; expected '[' to follow 'IGNORE' directive");
        }
        String errorMsg = getErrorMsg();
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(errorMsg);
            }
            char c2 = nextChar;
            if (c2 < ' ') {
                if (c2 == '\n' || c2 == '\r') {
                    skipCRLF(c2);
                } else if (c2 != '\t') {
                    throwInvalidSpace(c2);
                }
            } else if (c2 == ']') {
                if (getNextChar(errorMsg) == ']' && getNextChar(errorMsg) == '>') {
                    count--;
                    if (count < 1) {
                        return;
                    }
                } else {
                    this.mInputPtr--;
                }
            } else if (c2 == '<') {
                if (getNextChar(errorMsg) == '!' && getNextChar(errorMsg) == '[') {
                    count++;
                } else {
                    this.mInputPtr--;
                }
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void _reportUndefinedNotationRefs() throws XMLStreamException {
        int count = this.mNotationForwardRefs.size();
        String id = this.mNotationForwardRefs.keySet().iterator().next();
        String msg = "" + count + " referenced notation" + (count == 1 ? "" : OperatorName.CLOSE_AND_STROKE) + " undefined: first one '" + id + OperatorName.SHOW_TEXT_LINE;
        _reportVCViolation(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void _reportBadDirective(String dir) throws XMLStreamException {
        String msg = "Unrecognized DTD directive '<!" + dir + " >'; expected ATTLIST, ELEMENT, ENTITY or NOTATION";
        if (this.mCfgSupportDTDPP) {
            msg = msg + " (or, for DTD++, TARGETNS)";
        }
        _reportWFCViolation(msg);
    }

    private void _reportVCViolation(String msg) throws XMLStreamException {
        if (this.mCfgFullyValidating) {
            reportValidationProblem(msg, 2);
        } else {
            reportValidationProblem(msg, 1);
        }
    }

    private void _reportWFCViolation(String msg) throws XMLStreamException {
        throwParseError(msg);
    }

    private void _reportWFCViolation(String format, Object arg) throws XMLStreamException {
        throwParseError(format, arg, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void throwDTDElemError(String msg, Object elem) throws XMLStreamException {
        _reportWFCViolation(elemDesc(elem) + ": " + msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void throwDTDAttrError(String msg, DTDElement elem, PrefixedName attrName) throws XMLStreamException {
        _reportWFCViolation(attrDesc(elem, attrName) + ": " + msg);
    }

    private void throwDTDUnexpectedChar(int i, String extraMsg) throws XMLStreamException {
        if (extraMsg == null) {
            throwUnexpectedChar(i, getErrorMsg());
        }
        throwUnexpectedChar(i, getErrorMsg() + extraMsg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void throwForbiddenPE() throws XMLStreamException {
        _reportWFCViolation("Can not have parameter entities in the internal subset, except for defining complete declarations (XML 1.0, #2.8, WFC 'PEs In Internal Subset')");
    }

    private String elemDesc(Object elem) {
        return "Element <" + elem + ">)";
    }

    private String attrDesc(Object elem, PrefixedName attrName) {
        return "Attribute '" + attrName + "' (of element <" + elem + ">)";
    }

    private String entityDesc(WstxInputSource input) {
        return "Entity &" + input.getEntityId() + PolicyIdFilter.OID_SEPARATOR;
    }

    private void handleDeclaration(char c) throws XMLStreamException {
        String keyw;
        this.mCurrDepth = 1;
        try {
            if (c == 'A') {
                String keyw2 = checkDTDKeyword("TTLIST");
                if (keyw2 == null) {
                    this.mCurrDeclaration = "ATTLIST";
                    handleAttlistDecl();
                } else {
                    keyw = "A" + keyw2;
                    _reportBadDirective(keyw);
                }
            } else if (c == 'E') {
                char c2 = dtdNextFromCurr();
                if (c2 == 'N') {
                    String keyw3 = checkDTDKeyword("TITY");
                    if (keyw3 == null) {
                        this.mCurrDeclaration = DTDParser.TYPE_ENTITY;
                        handleEntityDecl(false);
                    } else {
                        keyw = "EN" + keyw3;
                        _reportBadDirective(keyw);
                    }
                } else {
                    if (c2 == 'L') {
                        String keyw4 = checkDTDKeyword("EMENT");
                        if (keyw4 == null) {
                            this.mCurrDeclaration = "ELEMENT";
                            handleElementDecl();
                        } else {
                            keyw = "EL" + keyw4;
                        }
                    } else {
                        keyw = readDTDKeyword("E" + c2);
                    }
                    _reportBadDirective(keyw);
                }
            } else if (c == 'N') {
                String keyw5 = checkDTDKeyword("OTATION");
                if (keyw5 == null) {
                    this.mCurrDeclaration = DTDParser.TYPE_NOTATION;
                    handleNotationDecl();
                } else {
                    keyw = "N" + keyw5;
                    _reportBadDirective(keyw);
                }
            } else {
                if (c == 'T' && this.mCfgSupportDTDPP) {
                    String keyw6 = checkDTDKeyword("ARGETNS");
                    if (keyw6 == null) {
                        this.mCurrDeclaration = "TARGETNS";
                        handleTargetNsDecl();
                    } else {
                        keyw = PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE + keyw6;
                    }
                } else {
                    keyw = readDTDKeyword(String.valueOf(c));
                }
                _reportBadDirective(keyw);
            }
            if (this.mInput.getScopeId() > 0) {
                handleGreedyEntityProblem(this.mInput);
            }
            this.mCurrDepth = 0;
            this.mCurrDeclaration = null;
        } catch (Throwable th) {
            this.mCurrDepth = 0;
            this.mCurrDeclaration = null;
            throw th;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleSuppressedDeclaration() throws XMLStreamException {
        String keyw;
        char c = dtdNextFromCurr();
        if (c == 'N') {
            String keyw2 = checkDTDKeyword("TITY");
            if (keyw2 == null) {
                handleEntityDecl(true);
                return;
            } else {
                keyw = "EN" + keyw2;
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        } else {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!E");
            this.mFlattenWriter.output(c);
            if (c == 'L') {
                String keyw3 = checkDTDKeyword("EMENT");
                if (keyw3 == null) {
                    handleElementDecl();
                    return;
                }
                keyw = "EL" + keyw3;
            } else {
                keyw = readDTDKeyword("E");
            }
        }
        _reportBadDirective(keyw);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleAttlistDecl() throws XMLStreamException {
        PrefixedName elemName = readDTDQName(skipObligatoryDtdWs());
        Location loc = getStartLocation();
        HashMap<PrefixedName, DTDElement> m = getElementMap();
        DTDElement elem = m.get(elemName);
        if (elem == null) {
            elem = DTDElement.createPlaceholder(this.mConfig, loc, elemName);
            m.put(elemName, elem);
        }
        int index = 0;
        while (true) {
            char c = getNextExpanded();
            if (isSpaceChar(c)) {
                this.mInputPtr--;
                c = skipDtdWs(true);
            }
            if (c != '>') {
                handleAttrDecl(elem, c, index, loc);
                index++;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleElementDecl() throws XMLStreamException {
        String keyw;
        DTDElement oldElem;
        PrefixedName elemName = readDTDQName(skipObligatoryDtdWs());
        Location loc = getStartLocation();
        char c = skipObligatoryDtdWs();
        StructValidator val = null;
        int vldContent = 4;
        if (c == '(') {
            if (skipDtdWs(true) == '#') {
                val = readMixedSpec(elemName, this.mCfgFullyValidating);
                vldContent = 4;
            } else {
                this.mInputPtr--;
                ContentSpec spec = readContentSpec(elemName, this.mCfgFullyValidating, 0);
                val = spec.getSimpleValidator();
                if (val == null) {
                    val = new DFAValidator(DFAState.constructDFA(spec));
                }
                vldContent = 1;
            }
        } else if (isNameStartChar(c)) {
            if (c == 'A') {
                String keyw2 = checkDTDKeyword("NY");
                if (keyw2 == null) {
                    vldContent = 4;
                } else {
                    keyw = "A" + keyw2;
                    _reportWFCViolation("Unrecognized DTD content spec keyword '" + keyw + "' (for element <" + elemName + ">); expected ANY or EMPTY");
                }
            } else {
                if (c == 'E') {
                    String keyw3 = checkDTDKeyword("MPTY");
                    if (keyw3 == null) {
                        val = EmptyValidator.getPcdataInstance();
                        vldContent = 0;
                    } else {
                        keyw = "E" + keyw3;
                    }
                } else {
                    this.mInputPtr--;
                    keyw = readDTDKeyword(String.valueOf(c));
                }
                _reportWFCViolation("Unrecognized DTD content spec keyword '" + keyw + "' (for element <" + elemName + ">); expected ANY or EMPTY");
            }
        } else {
            throwDTDUnexpectedChar(c, ": expected '(' to start content specification for element <" + elemName + ">");
        }
        char c2 = skipDtdWs(true);
        if (c2 != '>') {
            throwDTDUnexpectedChar(c2, "; expected '>' to finish the element declaration for <" + elemName + ">");
        }
        LinkedHashMap<PrefixedName, DTDElement> m = getElementMap();
        DTDElement oldElem2 = m.get(elemName);
        if (oldElem2 != null) {
            if (oldElem2.isDefined()) {
                if (this.mCfgFullyValidating) {
                    DTDSubsetImpl.throwElementException(oldElem2, loc);
                } else {
                    return;
                }
            }
            oldElem = oldElem2.define(loc, val, vldContent);
        } else {
            oldElem = DTDElement.createDefined(this.mConfig, loc, elemName, val, vldContent);
        }
        m.put(elemName, oldElem);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleEntityDecl(boolean suppressPEDecl) throws XMLStreamException {
        EntityDecl ent;
        HashMap<String, EntityDecl> m;
        Object old;
        String str;
        char c = dtdNextFromCurr();
        boolean gotSeparator = false;
        boolean isParam = false;
        while (true) {
            if (c == '%') {
                char d = dtdNextIfAvailable();
                if (d == 0 || isSpaceChar(d)) {
                    isParam = true;
                    if (d != '\n' && d != '\r') {
                        break;
                    }
                    skipCRLF(d);
                    break;
                }
                if (!isNameStartChar(d)) {
                    throwDTDUnexpectedChar(d, "; expected a space (for PE declaration) or PE reference name");
                }
                this.mInputPtr--;
                gotSeparator = true;
                expandPE();
                c = dtdNextChar();
            } else {
                if (!isSpaceChar(c)) {
                    break;
                }
                gotSeparator = true;
                c = dtdNextFromCurr();
            }
        }
        if (!gotSeparator) {
            throwDTDUnexpectedChar(c, "; expected a space separating ENTITY keyword and entity name");
        }
        if (isParam) {
            c = skipDtdWs(true);
        }
        if (suppressPEDecl && !isParam) {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!ENTITY ");
            this.mFlattenWriter.output(c);
        }
        String id = readDTDName(c);
        Location evtLoc = getStartLocation();
        try {
            char c2 = skipObligatoryDtdWs();
            if (c2 == '\'' || c2 == '\"') {
                dtdNextFromCurr();
                Location contentLoc = getLastCharLocation();
                this.mInputPtr--;
                char[] contents = parseEntityValue(id, contentLoc, c2);
                try {
                    ent = new IntEntity(evtLoc, id, getSource(), contents, contentLoc);
                } catch (IOException e) {
                    throw new WstxIOException(e);
                }
            } else {
                if (!isNameStartChar(c2)) {
                    throwDTDUnexpectedChar(c2, "; expected either quoted value, or keyword 'PUBLIC' or 'SYSTEM'");
                }
                ent = handleExternalEntityDecl(this.mInput, isParam, id, c2, evtLoc);
            }
            if (this.mIsExternal) {
                ent.markAsExternallyDeclared();
            }
            if (suppressPEDecl && isParam) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
            if (isParam) {
                m = this.mParamEntities;
                if (m == null) {
                    HashMap<String, EntityDecl> map = new HashMap<>();
                    m = map;
                    this.mParamEntities = map;
                }
            } else {
                m = this.mGeneralEntities;
                if (m == null) {
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    m = linkedHashMap;
                    this.mGeneralEntities = linkedHashMap;
                }
            }
            if (m.size() > 0 && (old = m.get(id)) != null) {
                XMLReporter rep = this.mConfig.getXMLReporter();
                if (rep != null) {
                    EntityDecl oldED = (EntityDecl) old;
                    String str2 = " entity '" + id + "' defined more than once: first declaration at " + oldED.getLocation();
                    if (isParam) {
                        str = "Parameter" + str2;
                    } else {
                        str = "General" + str2;
                    }
                    _reportWarning(rep, ErrorConsts.WT_ENT_DECL, str, evtLoc);
                }
            } else {
                m.put(id, ent);
            }
            if (this.mEventListener != null && !ent.isParsed()) {
                try {
                    URL src = this.mInput.getSource();
                    this.mEventListener.dtdUnparsedEntityDecl(id, ent.getPublicId(), ent.getSystemId(), ent.getNotationName(), src);
                } catch (IOException e2) {
                    throw new WstxIOException(e2);
                }
            }
        } catch (Throwable th) {
            if (suppressPEDecl && isParam) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
            throw th;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleNotationDecl() throws XMLStreamException {
        String pubId;
        String sysId;
        NotationDeclaration oldDecl;
        char c = skipObligatoryDtdWs();
        String id = readDTDName(c);
        char c2 = skipObligatoryDtdWs();
        boolean isPublic = checkPublicSystemKeyword(c2);
        char c3 = skipObligatoryDtdWs();
        if (isPublic) {
            if (c3 != '\"' && c3 != '\'') {
                throwDTDUnexpectedChar(c3, "; expected a quote to start the public identifier");
            }
            pubId = parsePublicId(c3, getErrorMsg());
            c3 = skipDtdWs(true);
        } else {
            pubId = null;
        }
        if (c3 == '\"' || c3 == '\'') {
            sysId = parseSystemId(c3, this.mNormalizeLFs, getErrorMsg());
            c3 = skipDtdWs(true);
        } else {
            if (!isPublic) {
                throwDTDUnexpectedChar(c3, "; expected a quote to start the system identifier");
            }
            sysId = null;
        }
        if (c3 != '>') {
            throwDTDUnexpectedChar(c3, "; expected closing '>' after NOTATION declaration");
        }
        try {
            URL baseURL = this.mInput.getSource();
            if (this.mEventListener != null) {
                this.mEventListener.dtdNotationDecl(id, pubId, sysId, baseURL);
            }
            Location evtLoc = getStartLocation();
            NotationDeclaration nd = new WNotationDeclaration(evtLoc, id, pubId, sysId, baseURL);
            if (this.mPredefdNotations != null && (oldDecl = this.mPredefdNotations.get(id)) != null) {
                DTDSubsetImpl.throwNotationException(oldDecl, nd);
            }
            HashMap<String, NotationDeclaration> m = this.mNotations;
            if (m == null) {
                LinkedHashMap linkedHashMap = new LinkedHashMap();
                m = linkedHashMap;
                this.mNotations = linkedHashMap;
            } else {
                NotationDeclaration oldDecl2 = m.get(id);
                if (oldDecl2 != null) {
                    DTDSubsetImpl.throwNotationException(oldDecl2, nd);
                }
            }
            if (this.mNotationForwardRefs != null) {
                this.mNotationForwardRefs.remove(id);
            }
            m.put(id, nd);
        } catch (IOException e) {
            throw new WstxIOException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void handleTargetNsDecl() throws XMLStreamException {
        String name;
        this.mAnyDTDppFeatures = true;
        char c = skipObligatoryDtdWs();
        if (isNameStartChar(c)) {
            name = readDTDLocalName(c, false);
            c = skipObligatoryDtdWs();
        } else {
            name = null;
        }
        if (c != '\"' && c != '\'') {
            if (c == '>') {
                _reportWFCViolation("Missing namespace URI for TARGETNS directive");
            }
            throwDTDUnexpectedChar(c, "; expected a single or double quote to enclose the namespace URI");
        }
        String uri = parseSystemId(c, false, "in namespace URI");
        if ((this.mConfigFlags & 2048) != 0) {
            uri = InternCache.getInstance().intern(uri);
        }
        char c2 = skipDtdWs(true);
        if (c2 != '>') {
            throwDTDUnexpectedChar(c2, "; expected '>' to end TARGETNS directive");
        }
        if (name == null) {
            this.mDefaultNsURI = uri;
            return;
        }
        if (this.mNamespaces == null) {
            this.mNamespaces = new HashMap<>();
        }
        this.mNamespaces.put(name, uri);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:37:0x00ff  */
    private void handleAttrDecl(DTDElement elem, char c, int index, Location loc) throws XMLStreamException {
        DefaultAttrValue defVal;
        DTDAttribute attr;
        PrefixedName attrName = readDTDQName(c);
        char c2 = skipObligatoryDtdWs();
        int type = 0;
        WordResolver enumValues = null;
        if (c2 == '(') {
            enumValues = parseEnumerated(elem, attrName, false);
            type = 1;
        } else {
            String typeStr = readDTDName(c2);
            switch (typeStr.charAt(0)) {
                case 'C':
                    if (typeStr == "CDATA") {
                        type = 0;
                    } else {
                        throwDTDAttrError("Unrecognized attribute type '" + typeStr + OperatorName.SHOW_TEXT_LINE + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
                    }
                    break;
                case 'E':
                    if (typeStr == DTDParser.TYPE_ENTITY) {
                        type = 5;
                    } else if (typeStr == DTDParser.TYPE_ENTITIES) {
                        type = 6;
                    } else {
                        throwDTDAttrError("Unrecognized attribute type '" + typeStr + OperatorName.SHOW_TEXT_LINE + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
                    }
                    break;
                case 'I':
                    if (typeStr == "ID") {
                        type = 2;
                    } else if (typeStr == DTDParser.TYPE_IDREF) {
                        type = 3;
                    } else if (typeStr == DTDParser.TYPE_IDREFS) {
                        type = 4;
                    } else {
                        throwDTDAttrError("Unrecognized attribute type '" + typeStr + OperatorName.SHOW_TEXT_LINE + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
                    }
                    break;
                case 'N':
                    if (typeStr == DTDParser.TYPE_NOTATION) {
                        type = 7;
                        char c3 = skipObligatoryDtdWs();
                        if (c3 != '(') {
                            throwDTDUnexpectedChar(c3, "Excepted '(' to start the list of NOTATION ids");
                        }
                        enumValues = parseEnumerated(elem, attrName, true);
                    } else if (typeStr == DTDParser.TYPE_NMTOKEN) {
                        type = 8;
                    } else if (typeStr == DTDParser.TYPE_NMTOKENS) {
                        type = 9;
                    } else {
                        throwDTDAttrError("Unrecognized attribute type '" + typeStr + OperatorName.SHOW_TEXT_LINE + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
                    }
                    break;
                default:
                    throwDTDAttrError("Unrecognized attribute type '" + typeStr + OperatorName.SHOW_TEXT_LINE + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
                    break;
            }
        }
        char c4 = skipObligatoryDtdWs();
        if (c4 == '#') {
            String defTypeStr = readDTDName(getNextExpanded());
            if (defTypeStr == "REQUIRED") {
                defVal = DefaultAttrValue.constructRequired();
            } else if (defTypeStr == "IMPLIED") {
                defVal = DefaultAttrValue.constructImplied();
            } else if (defTypeStr == "FIXED") {
                defVal = DefaultAttrValue.constructFixed();
                char c5 = skipObligatoryDtdWs();
                parseAttrDefaultValue(defVal, c5, attrName, loc, true);
            } else {
                throwDTDAttrError("Unrecognized attribute default value directive #" + defTypeStr + ErrorConsts.ERR_DTD_DEFAULT_TYPE, elem, attrName);
                return;
            }
        } else {
            defVal = DefaultAttrValue.constructOptional();
            parseAttrDefaultValue(defVal, c4, attrName, loc, false);
        }
        if (type == 2 && defVal.hasDefaultValue()) {
            if (this.mCfgFullyValidating) {
                throwDTDAttrError("has type ID; can not have a default (or #FIXED) value (XML 1.0/#3.3.1)", elem, attrName);
            }
        } else if (this.mConfig.willDoXmlIdTyping() && attrName.isXmlReservedAttr(this.mCfgNsEnabled, ElementTags.ID)) {
            checkXmlIdAttr(type);
        }
        if (attrName.isXmlReservedAttr(this.mCfgNsEnabled, "space")) {
            checkXmlSpaceAttr(type, enumValues);
        }
        if (this.mCfgNsEnabled && attrName.isaNsDeclaration()) {
            if (!defVal.hasDefaultValue()) {
                return;
            } else {
                attr = elem.addNsDefault(this, attrName, type, defVal, this.mCfgFullyValidating);
            }
        } else {
            attr = elem.addAttribute(this, attrName, type, defVal, enumValues, this.mCfgFullyValidating);
        }
        if (attr == null) {
            XMLReporter rep = this.mConfig.getXMLReporter();
            if (rep != null) {
                String msg = MessageFormat.format(ErrorConsts.W_DTD_ATTR_REDECL, attrName, elem);
                _reportWarning(rep, ErrorConsts.WT_ATTR_DECL, msg, loc);
                return;
            }
            return;
        }
        if (defVal.hasDefaultValue()) {
            attr.normalizeDefault();
            if (this.mCfgFullyValidating) {
                attr.validateDefault(this, true);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private WordResolver parseEnumerated(DTDElement elem, PrefixedName attrName, boolean isNotation) throws XMLStreamException {
        HashMap<String, String> sharedEnums;
        TreeSet<String> set = new TreeSet<>();
        char c = skipDtdWs(true);
        if (c == ')') {
            throwDTDUnexpectedChar(c, " (empty list; missing identifier(s))?");
        }
        if (isNotation) {
            sharedEnums = null;
        } else {
            sharedEnums = this.mSharedEnumValues;
            if (sharedEnums == null && !isNotation) {
                HashMap<String, String> map = new HashMap<>();
                sharedEnums = map;
                this.mSharedEnumValues = map;
            }
        }
        set.add(isNotation ? readNotationEntry(c, attrName, elem.getLocation()) : readEnumEntry(c, sharedEnums));
        while (true) {
            char c2 = skipDtdWs(true);
            if (c2 != ')') {
                if (c2 != '|') {
                    throwDTDUnexpectedChar(c2, "; missing '|' separator?");
                }
                char c3 = skipDtdWs(true);
                String id = isNotation ? readNotationEntry(c3, attrName, elem.getLocation()) : readEnumEntry(c3, sharedEnums);
                if (!set.add(id) && this.mCfgFullyValidating) {
                    throwDTDAttrError("Duplicate enumeration value '" + id + OperatorName.SHOW_TEXT_LINE, elem, attrName);
                }
            } else {
                return WordResolver.constructInstance(set);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private String readNotationEntry(char c, PrefixedName attrName, Location refLoc) throws XMLStreamException {
        NotationDeclaration decl;
        String id = readDTDName(c);
        if (this.mPredefdNotations != null && (decl = this.mPredefdNotations.get(id)) != null) {
            this.mUsesPredefdNotations = true;
            return decl.getName();
        }
        NotationDeclaration decl2 = this.mNotations == null ? null : this.mNotations.get(id);
        if (decl2 == null) {
            if (this.mCfgFullyValidating) {
                if (this.mNotationForwardRefs == null) {
                    this.mNotationForwardRefs = new LinkedHashMap();
                }
                this.mNotationForwardRefs.put(id, refLoc);
            }
            return id;
        }
        return decl2.getName();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private String readEnumEntry(char c, HashMap<String, String> sharedEnums) throws XMLStreamException {
        String id = readDTDNmtoken(c);
        String sid = sharedEnums.get(id);
        if (sid == null) {
            sid = id;
            sharedEnums.put(sid, sid);
        }
        return sid;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private StructValidator readMixedSpec(PrefixedName elemName, boolean construct) throws XMLStreamException {
        char c;
        String keyw = checkDTDKeyword("PCDATA");
        if (keyw != null) {
            _reportWFCViolation("Unrecognized directive #" + keyw + "'; expected #PCDATA (or element name)");
        }
        HashMap<PrefixedName, ContentSpec> m = new LinkedHashMap<>();
        while (true) {
            char c2 = skipDtdWs(true);
            if (c2 == ')') {
                break;
            }
            if (c2 == '|') {
                c2 = skipDtdWs(true);
            } else if (c2 == ',') {
                throwDTDUnexpectedChar(c2, " (sequences not allowed within mixed content)");
            } else if (c2 == '(') {
                throwDTDUnexpectedChar(c2, " (sub-content specs not allowed within mixed content)");
            } else {
                throwDTDUnexpectedChar(c2, "; expected either '|' to separate elements, or ')' to close the list");
            }
            PrefixedName n = readDTDQName(c2);
            Object old = m.put(n, TokenContentSpec.construct(' ', n));
            if (old != null && this.mCfgFullyValidating) {
                throwDTDElemError("duplicate child element <" + n + "> in mixed content model", elemName);
            }
        }
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            c = cArr[i];
        } else {
            c = getNextChar(getErrorMsg());
        }
        if (c != '*') {
            if (m.size() > 0) {
                _reportWFCViolation("Missing trailing '*' after a non-empty mixed content specification");
            }
            this.mInputPtr--;
        }
        if (!construct) {
            return null;
        }
        if (m.isEmpty()) {
            return EmptyValidator.getPcdataInstance();
        }
        ContentSpec spec = ChoiceContentSpec.constructMixed(this.mCfgNsEnabled, m.values());
        StructValidator val = spec.getSimpleValidator();
        if (val == null) {
            DFAState dfa = DFAState.constructDFA(spec);
            val = new DFAValidator(dfa);
        }
        return val;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private ContentSpec readContentSpec(PrefixedName elemName, boolean construct, int recursionDepth) throws XMLStreamException {
        ContentSpec cs;
        verifyLimit("Maximum DTD nesting depth (WstxInputProperties.P_MAX_DTD_DEPTH)", this.mConfig.getMaxDtdDepth(), recursionDepth);
        ArrayList<ContentSpec> subSpecs = new ArrayList<>();
        boolean isChoice = false;
        boolean choiceSet = false;
        while (true) {
            char c = skipDtdWs(true);
            if (c == ')') {
                break;
            }
            if (c == '|' || c == ',') {
                boolean newChoice = c == '|';
                if (!choiceSet) {
                    isChoice = newChoice;
                    choiceSet = true;
                } else if (isChoice != newChoice) {
                    _reportWFCViolation("Can not mix content spec separators ('|' and ','); need to use parenthesis groups");
                }
                c = skipDtdWs(true);
            } else if (!subSpecs.isEmpty()) {
                throwDTDUnexpectedChar(c, " (missing separator '|' or ','?)");
            }
            if (c == '(') {
                subSpecs.add(readContentSpec(elemName, construct, recursionDepth + 1));
            } else {
                if (c == '|' || c == ',') {
                    throwDTDUnexpectedChar(c, " (missing element name?)");
                }
                PrefixedName thisName = readDTDQName(c);
                char arity = readArity();
                if (construct) {
                    cs = TokenContentSpec.construct(arity, thisName);
                } else {
                    cs = TokenContentSpec.getDummySpec();
                }
                subSpecs.add(cs);
            }
        }
        if (subSpecs.isEmpty()) {
            _reportWFCViolation("Empty content specification for '" + elemName + "' (need at least one entry)");
        }
        char arity2 = readArity();
        if (!construct) {
            return TokenContentSpec.getDummySpec();
        }
        if (subSpecs.size() == 1) {
            ContentSpec cs2 = subSpecs.get(0);
            char otherArity = cs2.getArity();
            if (arity2 != otherArity) {
                cs2.setArity(combineArities(arity2, otherArity));
            }
            return cs2;
        }
        if (isChoice) {
            return ChoiceContentSpec.constructChoice(this.mCfgNsEnabled, arity2, subSpecs);
        }
        return SeqContentSpec.construct(this.mCfgNsEnabled, arity2, subSpecs);
    }

    private static char combineArities(char arity1, char arity2) {
        if (arity1 == arity2) {
            return arity1;
        }
        if (arity1 == ' ') {
            return arity2;
        }
        if (arity2 == ' ') {
            return arity1;
        }
        if (arity1 == '*' || arity2 == '*') {
            return '*';
        }
        return '*';
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private EntityDecl handleExternalEntityDecl(WstxInputSource inputSource, boolean isParam, String id, char c, Location evtLoc) throws XMLStreamException {
        char c2;
        char c3;
        boolean isPublic = checkPublicSystemKeyword(c);
        String pubId = null;
        if (isPublic) {
            char c4 = skipObligatoryDtdWs();
            if (c4 != '\"' && c4 != '\'') {
                throwDTDUnexpectedChar(c4, "; expected a quote to start the public identifier");
            }
            pubId = parsePublicId(c4, getErrorMsg());
            c2 = getNextExpanded();
            if (c2 <= ' ') {
                c2 = skipDtdWs(true);
            } else if (c2 != '>') {
                this.mInputPtr--;
                c2 = skipObligatoryDtdWs();
            }
            if (c2 == '>') {
                _reportWFCViolation("Unexpected end of ENTITY declaration (expected a system id after public id): trying to use an SGML DTD instead of XML one?");
            }
        } else {
            c2 = skipObligatoryDtdWs();
        }
        if (c2 != '\"' && c2 != '\'') {
            throwDTDUnexpectedChar(c2, "; expected a quote to start the system identifier");
        }
        String sysId = parseSystemId(c2, this.mNormalizeLFs, getErrorMsg());
        String notationId = null;
        if (isParam) {
            c3 = skipDtdWs(true);
        } else {
            int i = peekNext();
            if (i == 62) {
                c3 = '>';
                this.mInputPtr++;
            } else if (i < 0) {
                c3 = skipDtdWs(true);
            } else if (i == 37) {
                c3 = getNextExpanded();
            } else {
                this.mInputPtr++;
                char c5 = (char) i;
                if (!isSpaceChar(c5)) {
                    throwDTDUnexpectedChar(c5, "; expected a separating space or closing '>'");
                }
                c3 = skipDtdWs(true);
            }
            if (c3 != '>') {
                if (!isNameStartChar(c3)) {
                    throwDTDUnexpectedChar(c3, "; expected either NDATA keyword, or closing '>'");
                }
                String keyw = checkDTDKeyword("DATA");
                if (keyw != null) {
                    _reportWFCViolation("Unrecognized keyword '" + keyw + "'; expected NOTATION (or closing '>')");
                }
                char c6 = skipObligatoryDtdWs();
                notationId = readNotationEntry(c6, null, evtLoc);
                c3 = skipDtdWs(true);
            }
        }
        if (c3 != '>') {
            throwDTDUnexpectedChar(c3, "; expected closing '>'");
        }
        try {
            URL ctxt = inputSource.getSource();
            if (notationId == null) {
                return new ParsedExtEntity(evtLoc, id, ctxt, pubId, sysId);
            }
            return new UnparsedExtEntity(evtLoc, id, ctxt, pubId, sysId, notationId);
        } catch (IOException e) {
            throw new WstxIOException(e);
        }
    }

    private LinkedHashMap<PrefixedName, DTDElement> getElementMap() {
        LinkedHashMap<PrefixedName, DTDElement> m = this.mElements;
        if (m == null) {
            LinkedHashMap<PrefixedName, DTDElement> linkedHashMap = new LinkedHashMap<>();
            m = linkedHashMap;
            this.mElements = linkedHashMap;
        }
        return m;
    }

    private PrefixedName findSharedName(String prefix, String localName) {
        HashMap<PrefixedName, PrefixedName> m = this.mSharedNames;
        if (this.mSharedNames == null) {
            HashMap<PrefixedName, PrefixedName> map = new HashMap<>();
            m = map;
            this.mSharedNames = map;
        } else {
            PrefixedName key = this.mAccessKey;
            key.reset(prefix, localName);
            PrefixedName key2 = m.get(key);
            if (key2 != null) {
                return key2;
            }
        }
        PrefixedName result = new PrefixedName(prefix, localName);
        m.put(result, result);
        return result;
    }

    @Override // com.ctc.wstx.dtd.MinimalDTDReader, com.ctc.wstx.sr.StreamScanner
    protected EntityDecl findEntity(String id, Object arg) {
        if (arg == ENTITY_EXP_PE) {
            EntityDecl ed = this.mPredefdPEs == null ? null : this.mPredefdPEs.get(id);
            if (ed != null) {
                this.mUsesPredefdEntities = true;
                this.mRefdPEs = null;
            } else if (this.mParamEntities != null) {
                ed = this.mParamEntities.get(id);
                if (ed != null && !this.mUsesPredefdEntities) {
                    Set<String> used = this.mRefdPEs;
                    if (used == null) {
                        HashSet hashSet = new HashSet();
                        used = hashSet;
                        this.mRefdPEs = hashSet;
                    }
                    used.add(id);
                }
            }
            return ed;
        }
        if (arg == ENTITY_EXP_GE) {
            EntityDecl ed2 = this.mPredefdGEs == null ? null : this.mPredefdGEs.get(id);
            if (ed2 != null) {
                this.mUsesPredefdEntities = true;
                this.mRefdGEs = null;
            } else if (this.mGeneralEntities != null) {
                ed2 = this.mGeneralEntities.get(id);
                if (ed2 != null && !this.mUsesPredefdEntities) {
                    if (this.mRefdGEs == null) {
                        this.mRefdGEs = new HashSet();
                    }
                    this.mRefdGEs.add(id);
                }
            }
            return ed2;
        }
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.MinimalDTDReader, com.ctc.wstx.sr.StreamScanner
    protected void handleUndeclaredEntity(String id) throws XMLStreamException {
        _reportVCViolation("Undeclared parameter entity '" + id + "'.");
        if (this.mCurrAttrDefault != null) {
            Location loc = getLastCharLocation();
            if (this.mExpandingPE) {
                this.mCurrAttrDefault.addUndeclaredPE(id, loc);
            } else {
                this.mCurrAttrDefault.addUndeclaredGE(id, loc);
            }
        }
        if (this.mEventListener != null && this.mExpandingPE) {
            this.mEventListener.dtdSkippedEntity("%" + id);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.MinimalDTDReader, com.ctc.wstx.sr.StreamScanner
    protected void handleIncompleteEntityProblem(WstxInputSource closing) throws XMLStreamException {
        if (closing.getScopeId() == 0) {
            _reportWFCViolation(entityDesc(closing) + ": Incomplete PE: has to fully contain a declaration (as per xml 1.0.3, section 2.8, WFC 'PE Between Declarations')");
        } else if (this.mCfgFullyValidating) {
            _reportVCViolation(entityDesc(closing) + ": Incomplete PE: has to be fully contained in a declaration (as per xml 1.0.3, section 2.8, VC 'Proper Declaration/PE Nesting')");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void handleGreedyEntityProblem(WstxInputSource input) throws XMLStreamException {
        if (this.mCfgFullyValidating) {
            _reportWFCViolation(entityDesc(input) + ": Unbalanced PE: has to be fully contained in a declaration (as per xml 1.0.3, section 2.8, VC 'Proper Declaration/PE Nesting')");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void checkXmlSpaceAttr(int type, WordResolver enumValues) throws XMLStreamException {
        boolean ok = type == 1;
        if (ok) {
            switch (enumValues.size()) {
                case 1:
                    ok = (enumValues.find("preserve") == null && enumValues.find(PreferencesManager.VALUE_KEYSTORE_DEFAULT) == null) ? false : true;
                    break;
                case 2:
                    ok = (enumValues.find("preserve") == null || enumValues.find(PreferencesManager.VALUE_KEYSTORE_DEFAULT) == null) ? false : true;
                    break;
                default:
                    ok = false;
                    break;
            }
        }
        if (!ok) {
            _reportVCViolation(ErrorConsts.ERR_DTD_XML_SPACE);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void checkXmlIdAttr(int type) throws XMLStreamException {
        if (type != 2) {
            _reportVCViolation(ErrorConsts.ERR_DTD_XML_ID);
        }
    }

    private void _reportWarning(XMLReporter rep, String probType, String msg, Location loc) throws XMLStreamException {
        if (rep != null) {
            XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 1, probType);
            rep.report(msg, probType, prob, loc);
        }
    }
}
