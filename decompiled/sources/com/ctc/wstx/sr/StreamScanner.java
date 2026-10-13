package com.ctc.wstx.sr;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.exc.WstxLazyException;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.exc.WstxUnexpectedCharException;
import com.ctc.wstx.exc.WstxValidationException;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.TextBuffer;
import com.sun.jna.platform.win32.WinUser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.codehaus.stax2.XMLReporter2;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.XMLValidationProblem;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/StreamScanner.class */
public abstract class StreamScanner extends WstxInputData implements InputProblemReporter, InputConfigFlags, ParsingErrorMsgs {
    public static final char CHAR_CR_LF_OR_NULL = '\r';
    public static final int INT_CR_LF_OR_NULL = 13;
    protected static final char CHAR_FIRST_PURE_TEXT = '?';
    protected static final char CHAR_LOWEST_LEGAL_LOCALNAME_CHAR = '-';
    private static final int VALID_CHAR_COUNT = 256;
    private static final byte NAME_CHAR_INVALID_B = 0;
    private static final byte NAME_CHAR_ALL_VALID_B = 1;
    private static final byte NAME_CHAR_VALID_NONFIRST_B = -1;
    private static final byte[] sCharValidity = new byte[256];
    private static final int VALID_PUBID_CHAR_COUNT = 128;
    private static final byte[] sPubidValidity;
    private static final byte PUBID_CHAR_VALID_B = 1;
    protected final ReaderConfig mConfig;
    protected final boolean mCfgNsEnabled;
    protected boolean mCfgReplaceEntities;
    final SymbolTable mSymbols;
    protected String mCurrName;
    protected WstxInputSource mInput;
    protected final WstxInputSource mRootInput;
    protected XMLResolver mEntityResolver;
    protected int mCurrDepth;
    protected int mInputTopDepth;
    protected int mEntityExpansionCount;
    protected boolean mNormalizeLFs;
    protected boolean mAllowXml11EscapedCharsInXml10;
    protected char[] mNameBuffer = null;
    protected long mTokenInputTotal = 0;
    protected int mTokenInputRow = 1;
    protected int mTokenInputCol = 0;
    protected String mDocInputEncoding = null;
    protected String mDocXmlEncoding = null;
    protected int mDocXmlVersion = 0;
    protected Map<String, IntEntity> mCachedEntities;
    protected boolean mCfgTreatCharRefsAsEntities;
    protected EntityDecl mCurrEntity;

    public abstract Location getLocation();

    protected abstract EntityDecl findEntity(String str, Object obj) throws XMLStreamException;

    protected abstract void handleUndeclaredEntity(String str) throws XMLStreamException;

    protected abstract void handleIncompleteEntityProblem(WstxInputSource wstxInputSource) throws XMLStreamException;

    static {
        sCharValidity[95] = 1;
        for (int i = 0; i <= 25; i++) {
            sCharValidity[65 + i] = 1;
            sCharValidity[97 + i] = 1;
        }
        for (int i2 = 192; i2 < 246; i2++) {
            sCharValidity[i2] = 1;
        }
        sCharValidity[215] = 0;
        sCharValidity[247] = 0;
        sCharValidity[45] = -1;
        sCharValidity[46] = -1;
        sCharValidity[183] = -1;
        for (int i3 = 48; i3 <= 57; i3++) {
            sCharValidity[i3] = -1;
        }
        sPubidValidity = new byte[128];
        for (int i4 = 0; i4 <= 25; i4++) {
            sPubidValidity[65 + i4] = 1;
            sPubidValidity[97 + i4] = 1;
        }
        for (int i5 = 48; i5 <= 57; i5++) {
            sPubidValidity[i5] = 1;
        }
        sPubidValidity[10] = 1;
        sPubidValidity[13] = 1;
        sPubidValidity[32] = 1;
        sPubidValidity[45] = 1;
        sPubidValidity[39] = 1;
        sPubidValidity[40] = 1;
        sPubidValidity[41] = 1;
        sPubidValidity[43] = 1;
        sPubidValidity[44] = 1;
        sPubidValidity[46] = 1;
        sPubidValidity[47] = 1;
        sPubidValidity[58] = 1;
        sPubidValidity[61] = 1;
        sPubidValidity[63] = 1;
        sPubidValidity[59] = 1;
        sPubidValidity[33] = 1;
        sPubidValidity[42] = 1;
        sPubidValidity[35] = 1;
        sPubidValidity[64] = 1;
        sPubidValidity[36] = 1;
        sPubidValidity[95] = 1;
        sPubidValidity[37] = 1;
    }

    protected StreamScanner(WstxInputSource input, ReaderConfig cfg, XMLResolver res) {
        this.mEntityResolver = null;
        this.mInput = input;
        this.mRootInput = input;
        this.mConfig = cfg;
        this.mSymbols = cfg.getSymbols();
        int cf = cfg.getConfigFlags();
        this.mCfgNsEnabled = (cf & 1) != 0;
        this.mCfgReplaceEntities = (cf & 4) != 0;
        this.mAllowXml11EscapedCharsInXml10 = this.mConfig.willAllowXml11EscapedCharsInXml10();
        this.mNormalizeLFs = this.mConfig.willNormalizeLFs();
        this.mInputBuffer = null;
        this.mInputEnd = 0;
        this.mInputPtr = 0;
        this.mEntityResolver = res;
        this.mCfgTreatCharRefsAsEntities = this.mConfig.willTreatCharRefsAsEnts();
        if (this.mCfgTreatCharRefsAsEntities) {
            this.mCachedEntities = new HashMap();
        } else {
            this.mCachedEntities = Collections.emptyMap();
        }
    }

    public ReaderConfig getConfig() {
        return this.mConfig;
    }

    protected WstxInputLocation getLastCharLocation() {
        return this.mInput.getLocation((this.mCurrInputProcessed + ((long) this.mInputPtr)) - 1, this.mCurrInputRow, this.mInputPtr - this.mCurrInputRowStart);
    }

    protected URL getSource() throws IOException {
        return this.mInput.getSource();
    }

    protected String getSystemId() {
        return this.mInput.getSystemId();
    }

    public XMLStreamLocation2 getStartLocation() {
        return this.mInput.getLocation(this.mTokenInputTotal, this.mTokenInputRow, this.mTokenInputCol + 1);
    }

    public XMLStreamLocation2 getCurrentLocation() {
        return this.mInput.getLocation(this.mCurrInputProcessed + ((long) this.mInputPtr), this.mCurrInputRow, (this.mInputPtr - this.mCurrInputRowStart) + 1);
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.ctc.wstx.exc.WstxException, java.lang.Throwable] */
    public WstxException throwWfcException(String msg, boolean deferErrors) {
        ?? ConstructWfcException = constructWfcException(msg);
        if (!deferErrors) {
            throw ConstructWfcException;
        }
        return ConstructWfcException;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.InputProblemReporter
    public void throwParseError(String msg) throws XMLStreamException {
        throwParseError(msg, null, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.InputProblemReporter
    public void throwParseError(String format, Object arg, Object arg2) throws XMLStreamException {
        String msg = (arg == null && arg2 == null) ? format : MessageFormat.format(format, arg, arg2);
        throw constructWfcException(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportProblem(String probType, String format, Object arg, Object arg2) throws XMLStreamException {
        XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            _reportProblem(rep, probType, MessageFormat.format(format, arg, arg2), null);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.InputProblemReporter
    public void reportProblem(Location loc, String probType, String format, Object arg, Object arg2) throws XMLStreamException {
        XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            String msg = (arg == null && arg2 == null) ? format : MessageFormat.format(format, arg, arg2);
            _reportProblem(rep, probType, msg, loc);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void _reportProblem(XMLReporter rep, String probType, String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = getLastCharLocation();
        }
        _reportProblem(rep, new XMLValidationProblem(loc, msg, 2, probType));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void _reportProblem(XMLReporter rep, XMLValidationProblem prob) throws XMLStreamException {
        if (rep != null) {
            Location loc = prob.getLocation();
            if (loc == null) {
                loc = getLastCharLocation();
                prob.setLocation(loc);
            }
            if (prob.getType() == null) {
                prob.setType(ErrorConsts.WT_VALIDATION);
            }
            if (rep instanceof XMLReporter2) {
                ((XMLReporter2) rep).report(prob);
            } else {
                rep.report(prob.getMessage(), prob.getType(), prob, loc);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.InputProblemReporter
    public void reportValidationProblem(XMLValidationProblem prob) throws XMLStreamException {
        if (prob.getSeverity() > 2) {
            throw WstxValidationException.create(prob);
        }
        XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            _reportProblem(rep, prob);
        } else if (prob.getSeverity() >= 2) {
            throw WstxValidationException.create(prob);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(String msg, int severity) throws XMLStreamException {
        reportValidationProblem(new XMLValidationProblem(getLastCharLocation(), msg, severity));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.InputProblemReporter
    public void reportValidationProblem(String msg) throws XMLStreamException {
        reportValidationProblem(new XMLValidationProblem(getLastCharLocation(), msg, 2));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportValidationProblem(Location loc, String msg) throws XMLStreamException {
        reportValidationProblem(new XMLValidationProblem(loc, msg));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.InputProblemReporter
    public void reportValidationProblem(String format, Object arg, Object arg2) throws XMLStreamException {
        reportValidationProblem(MessageFormat.format(format, arg, arg2));
    }

    protected WstxException constructWfcException(String msg) {
        return new WstxParsingException(msg, getLastCharLocation());
    }

    protected WstxException constructFromIOE(IOException ioe) {
        return new WstxIOException(ioe);
    }

    protected WstxException constructNullCharException() {
        return new WstxUnexpectedCharException("Illegal character (NULL, unicode 0) encountered: not valid in any content", getLastCharLocation(), (char) 0);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwUnexpectedChar(int i, String msg) throws XMLStreamException {
        char c = (char) i;
        String excMsg = "Unexpected character " + getCharDesc(c) + msg;
        throw new WstxUnexpectedCharException(excMsg, getLastCharLocation(), c);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwNullChar() throws XMLStreamException {
        throw constructNullCharException();
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwInvalidSpace(int i) {
        throwInvalidSpace(i, false);
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v17, types: [java.lang.Throwable] */
    protected WstxException throwInvalidSpace(int i, boolean deferErrors) {
        WstxException ex;
        char c = (char) i;
        if (c == 0) {
            ex = constructNullCharException();
        } else {
            String msg = "Illegal character (" + getCharDesc(c) + ")";
            if (this.mXml11) {
                msg = msg + " [note: in XML 1.1, it could be included via entity expansion]";
            }
            ex = new WstxUnexpectedCharException(msg, getLastCharLocation(), c);
        }
        if (!deferErrors) {
            throw ex;
        }
        return ex;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwUnexpectedEOF(String msg) throws XMLStreamException {
        throw new WstxEOFException("Unexpected EOF" + (msg == null ? "" : msg), getLastCharLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwUnexpectedEOB(String msg) throws XMLStreamException {
        throw new WstxEOFException("Unexpected end of input block" + (msg == null ? "" : msg), getLastCharLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwFromIOE(IOException ioe) throws XMLStreamException {
        throw new WstxIOException(ioe);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [com.ctc.wstx.exc.WstxException] */
    protected void throwFromStrE(XMLStreamException strex) throws XMLStreamException {
        if (strex instanceof WstxException) {
            throw ((WstxException) strex);
        }
        throw new WstxException((Throwable) strex);
    }

    protected void throwLazyError(Exception e) {
        if (e instanceof XMLStreamException) {
            WstxLazyException.throwLazily((XMLStreamException) e);
        }
        ExceptionUtil.throwRuntimeException(e);
    }

    protected String tokenTypeDesc(int type) {
        return ErrorConsts.tokenTypeDesc(type);
    }

    public final WstxInputSource getCurrentInput() {
        return this.mInput;
    }

    protected final int inputInBuffer() {
        return this.mInputEnd - this.mInputPtr;
    }

    protected final int getNext() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !loadMore()) {
            return -1;
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    protected final int peekNext() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !loadMoreFromCurrent()) {
            return -1;
        }
        return this.mInputBuffer[this.mInputPtr];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final char getNextChar(String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            loadMore(errorMsg);
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final char getNextCharFromCurrent(String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            loadMoreFromCurrent(errorMsg);
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final int getNextAfterWS() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !loadMore()) {
            return -1;
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        char c = cArr[i];
        while (true) {
            char c2 = c;
            if (c2 <= ' ') {
                if (c2 == '\n' || c2 == '\r') {
                    skipCRLF(c2);
                } else if (c2 != ' ' && c2 != '\t') {
                    throwInvalidSpace(c2);
                }
                if (this.mInputPtr >= this.mInputEnd && !loadMore()) {
                    return -1;
                }
                char[] cArr2 = this.mInputBuffer;
                int i2 = this.mInputPtr;
                this.mInputPtr = i2 + 1;
                c = cArr2[i2];
            } else {
                return c2;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final char getNextCharAfterWS(String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            loadMore(errorMsg);
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        char c = cArr[i];
        while (true) {
            char c2 = c;
            if (c2 <= ' ') {
                if (c2 == '\n' || c2 == '\r') {
                    skipCRLF(c2);
                } else if (c2 != ' ' && c2 != '\t') {
                    throwInvalidSpace(c2);
                }
                if (this.mInputPtr >= this.mInputEnd) {
                    loadMore(errorMsg);
                }
                char[] cArr2 = this.mInputBuffer;
                int i2 = this.mInputPtr;
                this.mInputPtr = i2 + 1;
                c = cArr2[i2];
            } else {
                return c2;
            }
        }
    }

    protected final char getNextInCurrAfterWS(String errorMsg) throws XMLStreamException {
        return getNextInCurrAfterWS(errorMsg, getNextCharFromCurrent(errorMsg));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final char getNextInCurrAfterWS(String errorMsg, char c) throws XMLStreamException {
        while (c <= ' ') {
            if (c == '\n' || c == '\r') {
                skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd) {
                loadMoreFromCurrent(errorMsg);
            }
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            c = cArr[i];
        }
        return c;
    }

    protected final boolean skipCRLF(char c) throws XMLStreamException {
        boolean result;
        if (c == '\r' && peekNext() == 10) {
            this.mInputPtr++;
            result = true;
        } else {
            result = false;
        }
        this.mCurrInputRow++;
        this.mCurrInputRowStart = this.mInputPtr;
        return result;
    }

    protected final void markLF() {
        this.mCurrInputRow++;
        this.mCurrInputRowStart = this.mInputPtr;
    }

    protected final void markLF(int inputPtr) {
        this.mCurrInputRow++;
        this.mCurrInputRowStart = inputPtr;
    }

    protected final void pushback() {
        this.mInputPtr--;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void initInputSource(WstxInputSource newInput, boolean isExt, String entityId) throws XMLStreamException {
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        this.mInputTopDepth = this.mCurrDepth;
        int entityDepth = this.mInput.getEntityDepth() + 1;
        verifyLimit("Maximum entity expansion depth", this.mConfig.getMaxEntityDepth(), entityDepth);
        this.mInput = newInput;
        this.mInput.initInputLocation(this, this.mCurrDepth, entityDepth);
        if (isExt) {
            this.mNormalizeLFs = true;
        } else {
            this.mNormalizeLFs = false;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected boolean loadMore() throws XMLStreamException {
        WstxInputSource input = this.mInput;
        do {
            this.mCurrInputProcessed += (long) this.mInputEnd;
            verifyLimit("Maximum document characters", this.mConfig.getMaxCharacters(), this.mCurrInputProcessed);
            this.mCurrInputRowStart -= this.mInputEnd;
            try {
                int count = input.readInto(this);
                if (count > 0) {
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
    protected final boolean loadMore(String errorMsg) throws XMLStreamException {
        if (!loadMore()) {
            throwUnexpectedEOF(errorMsg);
            return true;
        }
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected boolean loadMoreFromCurrent() throws XMLStreamException {
        this.mCurrInputProcessed += (long) this.mInputEnd;
        this.mCurrInputRowStart -= this.mInputEnd;
        verifyLimit("Maximum document characters", this.mConfig.getMaxCharacters(), this.mCurrInputProcessed);
        try {
            int count = this.mInput.readInto(this);
            return count > 0;
        } catch (IOException ie) {
            throw constructFromIOE(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final boolean loadMoreFromCurrent(String errorMsg) throws XMLStreamException {
        if (!loadMoreFromCurrent()) {
            throwUnexpectedEOB(errorMsg);
            return true;
        }
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected boolean ensureInput(int minAmount) throws XMLStreamException {
        int currAmount = this.mInputEnd - this.mInputPtr;
        if (currAmount >= minAmount) {
            return true;
        }
        try {
            return this.mInput.readMore(this, minAmount);
        } catch (IOException ie) {
            throw constructFromIOE(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void closeAllInput(boolean force) throws XMLStreamException {
        WstxInputSource input = this.mInput;
        while (true) {
            if (force) {
                try {
                    input.closeCompletely();
                } catch (IOException ie) {
                    throw constructFromIOE(ie);
                }
            } else {
                input.close();
            }
            if (input != this.mRootInput) {
                WstxInputSource parent = input.getParent();
                if (parent == null) {
                    throwNullParent(input);
                }
                input = parent;
                this.mInput = parent;
            } else {
                return;
            }
        }
    }

    protected void throwNullParent(WstxInputSource curr) {
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected int resolveSimpleEntity(boolean checkStd) throws XMLStreamException {
        int len;
        int len2;
        char[] buf = this.mInputBuffer;
        int ptr = this.mInputPtr;
        int ptr2 = ptr + 1;
        char c = buf[ptr];
        if (c == '#') {
            int ptr3 = ptr2 + 1;
            char c2 = buf[ptr2];
            int value = 0;
            int inputLen = this.mInputEnd;
            if (c2 == 'x') {
                while (ptr3 < inputLen) {
                    int i = ptr3;
                    ptr3++;
                    c2 = buf[i];
                    if (c2 == ';') {
                        break;
                    }
                    value <<= 4;
                    if (c2 <= '9' && c2 >= '0') {
                        value += c2 - '0';
                    } else if (c2 >= 'a' && c2 <= 'f') {
                        value += 10 + (c2 - 'a');
                    } else if (c2 >= 'A' && c2 <= 'F') {
                        value += 10 + (c2 - 'A');
                    } else {
                        this.mInputPtr = ptr3;
                        throwUnexpectedChar(c2, "; expected a hex digit (0-9a-fA-F).");
                    }
                    if (value > 1114111) {
                        reportUnicodeOverflow();
                    }
                }
            } else {
                while (c2 != ';') {
                    if (c2 <= '9' && c2 >= '0') {
                        value = (value * 10) + (c2 - '0');
                        if (value > 1114111) {
                            reportUnicodeOverflow();
                        }
                    } else {
                        this.mInputPtr = ptr3;
                        throwUnexpectedChar(c2, "; expected a decimal number.");
                    }
                    if (ptr3 >= inputLen) {
                        break;
                    }
                    int i2 = ptr3;
                    ptr3++;
                    c2 = buf[i2];
                }
            }
            if (c2 == ';') {
                this.mInputPtr = ptr3;
                validateChar(value);
                return value;
            }
            return 0;
        }
        if (checkStd) {
            if (c == 'a') {
                int ptr4 = ptr2 + 1;
                char c3 = buf[ptr2];
                if (c3 == 'm') {
                    int ptr5 = ptr4 + 1;
                    if (buf[ptr4] != 'p' || ptr5 >= this.mInputEnd) {
                        return 0;
                    }
                    int ptr6 = ptr5 + 1;
                    if (buf[ptr5] == ';') {
                        this.mInputPtr = ptr6;
                        return 38;
                    }
                    return 0;
                }
                if (c3 == 'p') {
                    int ptr7 = ptr4 + 1;
                    if (buf[ptr4] == 'o' && ptr7 < (len2 = this.mInputEnd)) {
                        int ptr8 = ptr7 + 1;
                        if (buf[ptr7] != 's' || ptr8 >= len2) {
                            return 0;
                        }
                        int ptr9 = ptr8 + 1;
                        if (buf[ptr8] == ';') {
                            this.mInputPtr = ptr9;
                            return 39;
                        }
                        return 0;
                    }
                    return 0;
                }
                return 0;
            }
            if (c == 'g') {
                int ptr10 = ptr2 + 1;
                if (buf[ptr2] != 't') {
                    return 0;
                }
                int ptr11 = ptr10 + 1;
                if (buf[ptr10] == ';') {
                    this.mInputPtr = ptr11;
                    return 62;
                }
                return 0;
            }
            if (c == 'l') {
                int ptr12 = ptr2 + 1;
                if (buf[ptr2] != 't') {
                    return 0;
                }
                int ptr13 = ptr12 + 1;
                if (buf[ptr12] == ';') {
                    this.mInputPtr = ptr13;
                    return 60;
                }
                return 0;
            }
            if (c == 'q') {
                int ptr14 = ptr2 + 1;
                if (buf[ptr2] == 'u') {
                    int ptr15 = ptr14 + 1;
                    if (buf[ptr14] == 'o' && ptr15 < (len = this.mInputEnd)) {
                        int ptr16 = ptr15 + 1;
                        if (buf[ptr15] != 't' || ptr16 >= len) {
                            return 0;
                        }
                        int ptr17 = ptr16 + 1;
                        if (buf[ptr16] == ';') {
                            this.mInputPtr = ptr17;
                            return 34;
                        }
                        return 0;
                    }
                    return 0;
                }
                return 0;
            }
            return 0;
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected int resolveCharOnlyEntity(boolean checkStd) throws XMLStreamException {
        int avail = this.mInputEnd - this.mInputPtr;
        if (avail < 6) {
            this.mInputPtr--;
            if (!ensureInput(6)) {
                avail = inputInBuffer();
                if (avail < 3) {
                    throwUnexpectedEOF(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF);
                }
            } else {
                avail = 6;
            }
            this.mInputPtr++;
        }
        char c = this.mInputBuffer[this.mInputPtr];
        if (c == '#') {
            this.mInputPtr++;
            return resolveCharEnt(null);
        }
        if (checkStd) {
            if (c == 'a') {
                char d = this.mInputBuffer[this.mInputPtr + 1];
                if (d == 'm') {
                    if (avail >= 4 && this.mInputBuffer[this.mInputPtr + 2] == 'p' && this.mInputBuffer[this.mInputPtr + 3] == ';') {
                        this.mInputPtr += 4;
                        return 38;
                    }
                    return 0;
                }
                if (d == 'p' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 's' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                    this.mInputPtr += 5;
                    return 39;
                }
                return 0;
            }
            if (c == 'l') {
                if (this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                    this.mInputPtr += 3;
                    return 60;
                }
                return 0;
            }
            if (c == 'g') {
                if (this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                    this.mInputPtr += 3;
                    return 62;
                }
                return 0;
            }
            if (c == 'q' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 1] == 'u' && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 't' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                this.mInputPtr += 5;
                return 34;
            }
            return 0;
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected EntityDecl resolveNonCharEntity() throws XMLStreamException {
        int avail = this.mInputEnd - this.mInputPtr;
        if (avail < 6) {
            this.mInputPtr--;
            if (!ensureInput(6)) {
                avail = inputInBuffer();
                if (avail < 3) {
                    throwUnexpectedEOF(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF);
                }
            } else {
                avail = 6;
            }
            this.mInputPtr++;
        }
        char c = this.mInputBuffer[this.mInputPtr];
        if (c == '#') {
            return null;
        }
        if (c == 'a') {
            char d = this.mInputBuffer[this.mInputPtr + 1];
            if (d == 'm') {
                if (avail >= 4 && this.mInputBuffer[this.mInputPtr + 2] == 'p' && this.mInputBuffer[this.mInputPtr + 3] == ';') {
                    return null;
                }
            } else if (d == 'p' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 's' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                return null;
            }
        } else if (c == 'l') {
            if (this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                return null;
            }
        } else if (c == 'g') {
            if (this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                return null;
            }
        } else if (c == 'q' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 1] == 'u' && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 't' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
            return null;
        }
        this.mInputPtr++;
        String id = parseEntityName(c);
        this.mCurrName = id;
        return findEntity(id, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected int fullyResolveEntity(boolean allowExt) throws XMLStreamException {
        char c = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF);
        if (c == '#') {
            StringBuffer originalSurface = new StringBuffer("#");
            int ch = resolveCharEnt(originalSurface);
            if (this.mCfgTreatCharRefsAsEntities) {
                char[] originalChars = new char[originalSurface.length()];
                originalSurface.getChars(0, originalSurface.length(), originalChars, 0);
                this.mCurrEntity = getIntEntity(ch, originalChars);
                return 0;
            }
            return ch;
        }
        String id = parseEntityName(c);
        char c2 = id.charAt(0);
        char d = 0;
        if (c2 == 'a') {
            if (id.equals("amp")) {
                d = '&';
            } else if (id.equals("apos")) {
                d = '\'';
            }
        } else if (c2 == 'g') {
            if (id.length() == 2 && id.charAt(1) == 't') {
                d = '>';
            }
        } else if (c2 == 'l') {
            if (id.length() == 2 && id.charAt(1) == 't') {
                d = '<';
            }
        } else if (c2 == 'q' && id.equals("quot")) {
            d = '\"';
        }
        if (d != 0) {
            if (this.mCfgTreatCharRefsAsEntities) {
                char[] originalChars2 = new char[id.length()];
                id.getChars(0, id.length(), originalChars2, 0);
                this.mCurrEntity = getIntEntity(d, originalChars2);
                return 0;
            }
            return d;
        }
        EntityDecl e = expandEntity(id, allowExt, null);
        if (this.mCfgTreatCharRefsAsEntities) {
            this.mCurrEntity = e;
            return 0;
        }
        return 0;
    }

    protected EntityDecl getIntEntity(int ch, char[] originalChars) {
        String repl;
        String cacheKey = new String(originalChars);
        IntEntity entity = this.mCachedEntities.get(cacheKey);
        if (entity == null) {
            if (ch <= 65535) {
                repl = Character.toString((char) ch);
            } else {
                StringBuffer sb = new StringBuffer(2);
                int ch2 = ch - 65536;
                sb.append((char) ((ch2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST));
                sb.append((char) ((ch2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST));
                repl = sb.toString();
            }
            entity = IntEntity.create(new String(originalChars), repl);
            this.mCachedEntities.put(cacheKey, entity);
        }
        return entity;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected EntityDecl expandEntity(String id, boolean allowExt, Object extraArg) throws XMLStreamException {
        this.mCurrName = id;
        EntityDecl ed = findEntity(id, extraArg);
        if (ed == null) {
            if (this.mCfgReplaceEntities) {
                this.mCurrEntity = expandUnresolvedEntity(id);
                return null;
            }
            return null;
        }
        if (!this.mCfgTreatCharRefsAsEntities || (this instanceof MinimalDTDReader)) {
            expandEntity(ed, allowExt);
        }
        return ed;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void expandEntity(EntityDecl ed, boolean allowExt) throws XMLStreamException {
        String id = ed.getName();
        if (this.mInput.isOrIsExpandedFrom(id)) {
            throwRecursionError(id);
        }
        if (!ed.isParsed()) {
            throwParseError("Illegal reference to unparsed external entity \"{0}\"", id, null);
        }
        boolean isExt = ed.isExternal();
        if (isExt) {
            if (!allowExt) {
                throwParseError("Encountered a reference to external parsed entity \"{0}\" when expanding attribute value: not legal as per XML 1.0/1.1 #3.1", id, null);
            }
            if (!this.mConfig.willSupportExternalEntities()) {
                throwParseError("Encountered a reference to external entity \"{0}\", but stream reader has feature \"{1}\" disabled", id, "javax.xml.stream.isSupportingExternalEntities");
            }
        }
        long maxEntityCount = this.mConfig.getMaxEntityCount();
        int i = this.mEntityExpansionCount + 1;
        this.mEntityExpansionCount = i;
        verifyLimit("Maximum entity expansion count", maxEntityCount, i);
        WstxInputSource oldInput = this.mInput;
        oldInput.saveContext(this);
        WstxInputSource newInput = null;
        try {
            newInput = ed.expand(oldInput, this.mEntityResolver, this.mConfig, this.mDocXmlVersion);
        } catch (FileNotFoundException fex) {
            throwParseError("(was {0}) {1}", fex.getClass().getName(), fex.getMessage());
        } catch (IOException ioe) {
            throw constructFromIOE(ioe);
        }
        initInputSource(newInput, isExt, id);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private EntityDecl expandUnresolvedEntity(String id) throws XMLStreamException {
        XMLResolver resolver = this.mConfig.getUndeclaredEntityResolver();
        if (resolver != null) {
            if (this.mInput.isOrIsExpandedFrom(id)) {
                throwRecursionError(id);
            }
            WstxInputSource oldInput = this.mInput;
            oldInput.saveContext(this);
            int xmlVersion = this.mDocXmlVersion;
            if (xmlVersion == 0) {
                xmlVersion = 256;
            }
            try {
                WstxInputSource newInput = DefaultInputResolver.resolveEntityUsing(oldInput, id, null, null, resolver, this.mConfig, xmlVersion);
                if (this.mCfgTreatCharRefsAsEntities) {
                    return new IntEntity(WstxInputLocation.getEmptyLocation(), newInput.getEntityId(), newInput.getSource(), new char[0], WstxInputLocation.getEmptyLocation());
                }
                if (newInput != null) {
                    initInputSource(newInput, true, id);
                    return null;
                }
            } catch (IOException ioe) {
                throw constructFromIOE(ioe);
            }
        }
        handleUndeclaredEntity(id);
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String parseLocalName(char c) throws XMLStreamException {
        if (!isNameStartChar(c)) {
            if (c == ':') {
                throwUnexpectedChar(c, " (missing namespace prefix?)");
            }
            throwUnexpectedChar(c, " (expected a name start character)");
        }
        int ptr = this.mInputPtr;
        int hash = c;
        int inputLen = this.mInputEnd;
        int startPtr = ptr - 1;
        char[] inputBuf = this.mInputBuffer;
        while (ptr < inputLen) {
            char c2 = inputBuf[ptr];
            if (c2 >= '-' && isNameChar(c2)) {
                hash = (hash * 31) + c2;
                ptr++;
            } else {
                this.mInputPtr = ptr;
                return this.mSymbols.findSymbol(this.mInputBuffer, startPtr, ptr - startPtr, hash);
            }
        }
        this.mInputPtr = ptr;
        return parseLocalName2(startPtr, hash);
    }

    protected String parseLocalName2(int start, int hash) throws XMLStreamException {
        char c;
        int ptr = this.mInputEnd - start;
        char[] outBuf = getNameBuffer(ptr + 8);
        if (ptr > 0) {
            System.arraycopy(this.mInputBuffer, start, outBuf, 0, ptr);
        }
        int outLen = outBuf.length;
        while (true) {
            if ((this.mInputPtr >= this.mInputEnd && !loadMoreFromCurrent()) || (c = this.mInputBuffer[this.mInputPtr]) < '-' || !isNameChar(c)) {
                break;
            }
            this.mInputPtr++;
            if (ptr >= outLen) {
                char[] cArrExpandBy50Pct = expandBy50Pct(outBuf);
                outBuf = cArrExpandBy50Pct;
                this.mNameBuffer = cArrExpandBy50Pct;
                outLen = outBuf.length;
            }
            int i = ptr;
            ptr++;
            outBuf[i] = c;
            hash = (hash * 31) + c;
        }
        return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String parseFullName() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            loadMoreFromCurrent();
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return parseFullName(cArr[i]);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String parseFullName(char c) throws XMLStreamException {
        if (!isNameStartChar(c)) {
            if (c == ':') {
                if (this.mCfgNsEnabled) {
                    throwNsColonException(parseFNameForError());
                }
            } else {
                if (c <= ' ') {
                    throwUnexpectedChar(c, " (missing name?)");
                }
                throwUnexpectedChar(c, " (expected a name start character)");
            }
        }
        int ptr = this.mInputPtr;
        int hash = c;
        int inputLen = this.mInputEnd;
        int startPtr = ptr - 1;
        while (ptr < inputLen) {
            char c2 = this.mInputBuffer[ptr];
            if (c2 == ':') {
                if (this.mCfgNsEnabled) {
                    this.mInputPtr = ptr;
                    throwNsColonException(new String(this.mInputBuffer, startPtr, ptr - startPtr) + parseFNameForError());
                }
            } else if (c2 < '-' || !isNameChar(c2)) {
                this.mInputPtr = ptr;
                return this.mSymbols.findSymbol(this.mInputBuffer, startPtr, ptr - startPtr, hash);
            }
            hash = (hash * 31) + c2;
            ptr++;
        }
        this.mInputPtr = ptr;
        return parseFullName2(startPtr, hash);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String parseFullName2(int start, int hash) throws XMLStreamException {
        int ptr = this.mInputEnd - start;
        char[] outBuf = getNameBuffer(ptr + 8);
        if (ptr > 0) {
            System.arraycopy(this.mInputBuffer, start, outBuf, 0, ptr);
        }
        int outLen = outBuf.length;
        while (true) {
            if (this.mInputPtr >= this.mInputEnd && !loadMoreFromCurrent()) {
                break;
            }
            char c = this.mInputBuffer[this.mInputPtr];
            if (c == ':') {
                if (this.mCfgNsEnabled) {
                    throwNsColonException(new String(outBuf, 0, ptr) + c + parseFNameForError());
                }
            } else if (c < '-' || !isNameChar(c)) {
                break;
            }
            this.mInputPtr++;
            if (ptr >= outLen) {
                char[] cArrExpandBy50Pct = expandBy50Pct(outBuf);
                outBuf = cArrExpandBy50Pct;
                this.mNameBuffer = cArrExpandBy50Pct;
                outLen = outBuf.length;
            }
            int i = ptr;
            ptr++;
            outBuf[i] = c;
            hash = (hash * 31) + c;
        }
        return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String parseFNameForError() throws XMLStreamException {
        char c;
        StringBuilder sb = new StringBuilder(100);
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                c = cArr[i];
            } else {
                int i2 = getNext();
                if (i2 < 0) {
                    break;
                }
                c = (char) i2;
            }
            if (c != ':' && !isNameChar(c)) {
                this.mInputPtr--;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final String parseEntityName(char c) throws XMLStreamException {
        String id = parseFullName(c);
        if (this.mInputPtr >= this.mInputEnd && !loadMoreFromCurrent()) {
            throwParseError("Missing semicolon after reference for entity \"{0}\"", id, null);
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        char c2 = cArr[i];
        if (c2 != ';') {
            throwUnexpectedChar(c2, "; expected a semi-colon after the reference for entity '" + id + OperatorName.SHOW_TEXT_LINE);
        }
        return id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected int skipFullName(char c) throws XMLStreamException {
        char nextChar;
        if (!isNameStartChar(c)) {
            this.mInputPtr--;
            return 0;
        }
        int count = 1;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(ParsingErrorMsgs.SUFFIX_EOF_EXP_NAME);
            }
            char c2 = nextChar;
            if (c2 == ':' || isNameChar(c2)) {
                count++;
            } else {
                return count;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final String parseSystemId(char quoteChar, boolean convertLFs, String errorMsg) throws XMLStreamException {
        char nextChar;
        char[] buf = getNameBuffer(-1);
        int ptr = 0;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(errorMsg);
            }
            char c = nextChar;
            if (c == quoteChar) {
                break;
            }
            if (c == '\n') {
                markLF();
            } else if (c == '\r') {
                if (peekNext() == 10) {
                    this.mInputPtr++;
                    if (!convertLFs) {
                        if (ptr >= buf.length) {
                            buf = expandBy50Pct(buf);
                        }
                        int i2 = ptr;
                        ptr++;
                        buf[i2] = '\r';
                    }
                    c = '\n';
                } else if (convertLFs) {
                    c = '\n';
                }
            }
            if (ptr >= buf.length) {
                buf = expandBy50Pct(buf);
            }
            int i3 = ptr;
            ptr++;
            buf[i3] = c;
        }
        return ptr == 0 ? "" : new String(buf, 0, ptr);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final String parsePublicId(char quoteChar, String errorMsg) throws XMLStreamException {
        char nextChar;
        char[] buf = getNameBuffer(-1);
        int ptr = 0;
        boolean spaceToAdd = false;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                nextChar = cArr[i];
            } else {
                nextChar = getNextChar(errorMsg);
            }
            char c = nextChar;
            if (c == quoteChar) {
                break;
            }
            if (c == '\n') {
                markLF();
                spaceToAdd = true;
            } else if (c == '\r') {
                if (peekNext() == 10) {
                    this.mInputPtr++;
                }
                spaceToAdd = true;
            } else if (c == ' ') {
                spaceToAdd = true;
            } else {
                if (c >= 128 || sPubidValidity[c] != 1) {
                    throwUnexpectedChar(c, " in public identifier");
                }
                if (ptr >= buf.length) {
                    buf = expandBy50Pct(buf);
                }
                if (spaceToAdd) {
                    spaceToAdd = false;
                    if (ptr > 0) {
                        int i2 = ptr;
                        ptr++;
                        buf[i2] = ' ';
                        if (ptr >= buf.length) {
                            buf = expandBy50Pct(buf);
                        }
                    }
                }
                int i3 = ptr;
                ptr++;
                buf[i3] = c;
            }
        }
        return ptr == 0 ? "" : new String(buf, 0, ptr);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void parseUntil(TextBuffer tb, char endChar, boolean convertLFs, String errorMsg) throws XMLStreamException {
        char[] inputBuf;
        int ptr;
        int startPtr;
        if (this.mInputPtr >= this.mInputEnd) {
            loadMore(errorMsg);
        }
        loop0: while (true) {
            inputBuf = this.mInputBuffer;
            int inputLen = this.mInputEnd;
            ptr = this.mInputPtr;
            startPtr = ptr;
            while (ptr < inputLen) {
                int i = ptr;
                ptr++;
                char c = inputBuf[i];
                if (c == endChar) {
                    break loop0;
                }
                if (c == '\n') {
                    this.mInputPtr = ptr;
                    markLF();
                } else if (c == '\r') {
                    if (!convertLFs && ptr < inputLen) {
                        if (inputBuf[ptr] == '\n') {
                            ptr++;
                        }
                        this.mInputPtr = ptr;
                        markLF();
                    } else {
                        int thisLen = (ptr - startPtr) - 1;
                        if (thisLen > 0) {
                            tb.append(inputBuf, startPtr, thisLen);
                        }
                        this.mInputPtr = ptr;
                        if (getNextChar(errorMsg) != '\n') {
                            this.mInputPtr--;
                            tb.append(convertLFs ? '\n' : '\r');
                        } else if (convertLFs) {
                            tb.append('\n');
                        } else {
                            tb.append('\r');
                            tb.append('\n');
                        }
                        int i2 = this.mInputPtr;
                        ptr = i2;
                        startPtr = i2;
                        markLF();
                    }
                }
            }
            int thisLen2 = ptr - startPtr;
            if (thisLen2 > 0) {
                tb.append(inputBuf, startPtr, thisLen2);
            }
            loadMore(errorMsg);
            int i3 = this.mInputPtr;
            char[] cArr = this.mInputBuffer;
            int i4 = this.mInputEnd;
        }
        int thisLen3 = (ptr - startPtr) - 1;
        if (thisLen3 > 0) {
            tb.append(inputBuf, startPtr, thisLen3);
        }
        this.mInputPtr = ptr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private int resolveCharEnt(StringBuffer originalCharacters) throws XMLStreamException {
        char nextCharFromCurrent;
        char nextCharFromCurrent2;
        int value = 0;
        char c = getNextChar(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF);
        if (originalCharacters != null) {
            originalCharacters.append(c);
        }
        if (c == 'x') {
            while (true) {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr = this.mInputBuffer;
                    int i = this.mInputPtr;
                    this.mInputPtr = i + 1;
                    nextCharFromCurrent2 = cArr[i];
                } else {
                    nextCharFromCurrent2 = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF);
                }
                char c2 = nextCharFromCurrent2;
                if (c2 == ';') {
                    break;
                }
                if (originalCharacters != null) {
                    originalCharacters.append(c2);
                }
                value <<= 4;
                if (c2 <= '9' && c2 >= '0') {
                    value += c2 - '0';
                } else if (c2 >= 'a' && c2 <= 'f') {
                    value += 10 + (c2 - 'a');
                } else if (c2 >= 'A' && c2 <= 'F') {
                    value += 10 + (c2 - 'A');
                } else {
                    throwUnexpectedChar(c2, "; expected a hex digit (0-9a-fA-F).");
                }
                if (value > 1114111) {
                    reportUnicodeOverflow();
                }
            }
        } else {
            while (c != ';') {
                if (c <= '9' && c >= '0') {
                    value = (value * 10) + (c - '0');
                    if (value > 1114111) {
                        reportUnicodeOverflow();
                    }
                } else {
                    throwUnexpectedChar(c, "; expected a decimal number.");
                }
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr2 = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    nextCharFromCurrent = cArr2[i2];
                } else {
                    nextCharFromCurrent = getNextCharFromCurrent(ParsingErrorMsgs.SUFFIX_IN_ENTITY_REF);
                }
                c = nextCharFromCurrent;
                if (originalCharacters != null && c != ';') {
                    originalCharacters.append(c);
                }
            }
        }
        validateChar(value);
        return value;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final void validateChar(int value) throws XMLStreamException {
        if (value >= 55296) {
            if (value < 57344) {
                reportIllegalChar(value);
            }
            if (value > 65535) {
                if (value > 1114111) {
                    reportUnicodeOverflow();
                    return;
                }
                return;
            } else {
                if (value >= 65534) {
                    reportIllegalChar(value);
                    return;
                }
                return;
            }
        }
        if (value < 32) {
            if (value == 0) {
                throwParseError("Invalid character reference: null character not allowed in XML content.");
            }
            if (!this.mXml11 && !this.mAllowXml11EscapedCharsInXml10 && value != 9 && value != 10 && value != 13) {
                reportIllegalChar(value);
            }
        }
    }

    protected final char[] getNameBuffer(int minSize) {
        char[] buf = this.mNameBuffer;
        if (buf == null) {
            char[] cArr = new char[minSize > 48 ? minSize + 16 : 64];
            buf = cArr;
            this.mNameBuffer = cArr;
        } else if (minSize >= buf.length) {
            int len = buf.length;
            int len2 = len + (len >> 1);
            char[] cArr2 = new char[minSize >= len2 ? minSize + 16 : len2];
            buf = cArr2;
            this.mNameBuffer = cArr2;
        }
        return buf;
    }

    protected final char[] expandBy50Pct(char[] buf) {
        int len = buf.length;
        char[] newBuf = new char[len + (len >> 1)];
        System.arraycopy(buf, 0, newBuf, 0, len);
        return newBuf;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void throwNsColonException(String name) throws XMLStreamException {
        throwParseError("Illegal name \"{0}\" (PI target, entity/notation name): can not contain a colon (XML Namespaces 1.0#6)", name, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void throwRecursionError(String entityName) throws XMLStreamException {
        throwParseError("Illegal entity expansion: entity \"{0}\" expands itself recursively.", entityName, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void reportUnicodeOverflow() throws XMLStreamException {
        throwParseError("Illegal character entity: value higher than max allowed (0x{0})", Integer.toHexString(1114111), null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void reportIllegalChar(int value) throws XMLStreamException {
        throwParseError("Illegal character entity: expansion character (code 0x{0}", Integer.toHexString(value), null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void verifyLimit(String type, long maxValue, long currentValue) throws XMLStreamException {
        if (currentValue > maxValue) {
            throw constructLimitViolation(type, maxValue);
        }
    }

    protected XMLStreamException constructLimitViolation(String type, long limit) throws XMLStreamException {
        return new XMLStreamException(type + " limit (" + limit + ") exceeded");
    }
}
