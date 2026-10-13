package com.ctc.wstx.dtd;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.sr.StreamScanner;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/MinimalDTDReader.class */
public class MinimalDTDReader extends StreamScanner {
    final boolean mIsExternal;

    private MinimalDTDReader(WstxInputSource input, ReaderConfig cfg) {
        this(input, cfg, false);
    }

    protected MinimalDTDReader(WstxInputSource input, ReaderConfig cfg, boolean isExt) {
        super(input, cfg, cfg.getDtdResolver());
        this.mIsExternal = isExt;
        this.mCfgReplaceEntities = true;
    }

    public static void skipInternalSubset(WstxInputData srcData, WstxInputSource input, ReaderConfig cfg) throws XMLStreamException {
        MinimalDTDReader r = new MinimalDTDReader(input, cfg);
        r.copyBufferStateFrom(srcData);
        try {
            r.skipInternalSubset();
        } finally {
            srcData.copyBufferStateFrom(r);
        }
    }

    @Override // com.ctc.wstx.sr.StreamScanner, com.ctc.wstx.sr.InputProblemReporter
    public final Location getLocation() {
        return getStartLocation();
    }

    @Override // com.ctc.wstx.sr.StreamScanner
    protected EntityDecl findEntity(String id, Object arg) {
        throwIllegalCall();
        return null;
    }

    @Override // com.ctc.wstx.sr.StreamScanner
    protected void handleUndeclaredEntity(String id) throws XMLStreamException {
    }

    @Override // com.ctc.wstx.sr.StreamScanner
    protected void handleIncompleteEntityProblem(WstxInputSource closing) throws XMLStreamException {
    }

    protected char handleExpandedSurrogate(char first, char second) {
        return first;
    }

    public EntityDecl findEntity(String entName) {
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void skipInternalSubset() throws XMLStreamException {
        while (true) {
            int i = getNextAfterWS();
            if (i < 0) {
                throwUnexpectedEOF(ParsingErrorMsgs.SUFFIX_IN_DTD_INTERNAL);
            }
            if (i == 37) {
                skipPE();
            } else if (i == 60) {
                char c = getNextSkippingPEs();
                if (c == '?') {
                    skipPI();
                } else if (c == '!') {
                    char c2 = getNextSkippingPEs();
                    if (c2 != '[') {
                        if (c2 == '-') {
                            skipComment();
                        } else if (c2 >= 'A' && c2 <= 'Z') {
                            skipDeclaration(c2);
                        } else {
                            skipDeclaration(c2);
                        }
                    }
                } else {
                    this.mInputPtr--;
                }
            } else if (i == 93) {
                break;
            } else {
                throwUnexpectedChar(i, " in internal DTD subset; expected a '<' to start a directive, or \"]>\" to end internal subset.");
            }
        }
        if (this.mInput != this.mRootInput) {
            throwParseError("Encountered int. subset end marker ']]>' in an expanded entity; has to be at main level.");
        }
    }

    protected char dtdNextFromCurr() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            return getNextCharFromCurrent(getErrorMsg());
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    protected char dtdNextChar() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            return getNextChar(getErrorMsg());
        }
        char[] cArr = this.mInputBuffer;
        int i = this.mInputPtr;
        this.mInputPtr = i + 1;
        return cArr[i];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected char getNextSkippingPEs() throws XMLStreamException {
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
            skipPE();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void skipPE() throws XMLStreamException {
        char cDtdNextFromCurr;
        skipDTDName();
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            cDtdNextFromCurr = cArr[i];
        } else {
            cDtdNextFromCurr = dtdNextFromCurr();
        }
        char c = cDtdNextFromCurr;
        if (c != ';') {
            this.mInputPtr--;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void skipComment() throws XMLStreamException {
        char cDtdNextFromCurr;
        skipCommentContent();
        if (this.mInputPtr < this.mInputEnd) {
            char[] cArr = this.mInputBuffer;
            int i = this.mInputPtr;
            this.mInputPtr = i + 1;
            cDtdNextFromCurr = cArr[i];
        } else {
            cDtdNextFromCurr = dtdNextFromCurr();
        }
        char c = cDtdNextFromCurr;
        if (c != '>') {
            throwParseError("String '--' not allowed in comment (missing '>'?)");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void skipCommentContent() throws XMLStreamException {
        char cDtdNextFromCurr;
        char c;
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cDtdNextFromCurr = cArr[i];
            } else {
                cDtdNextFromCurr = dtdNextFromCurr();
            }
            char c2 = cDtdNextFromCurr;
            if (c2 == '-') {
                if (this.mInputPtr < this.mInputEnd) {
                    char[] cArr2 = this.mInputBuffer;
                    int i2 = this.mInputPtr;
                    this.mInputPtr = i2 + 1;
                    c = cArr2[i2];
                } else {
                    c = dtdNextFromCurr();
                }
                if (c == '-') {
                    return;
                }
            } else if (c2 == '\n' || c2 == '\r') {
                skipCRLF(c2);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void skipPI() throws XMLStreamException {
        char cDtdNextFromCurr;
        char cDtdNextFromCurr2;
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
            if (c == '?') {
                do {
                    if (this.mInputPtr < this.mInputEnd) {
                        char[] cArr2 = this.mInputBuffer;
                        int i2 = this.mInputPtr;
                        this.mInputPtr = i2 + 1;
                        cDtdNextFromCurr2 = cArr2[i2];
                    } else {
                        cDtdNextFromCurr2 = dtdNextFromCurr();
                    }
                    c = cDtdNextFromCurr2;
                } while (c == '?');
                if (c == '>') {
                    return;
                }
            }
            if (c == '\n' || c == '\r') {
                skipCRLF(c);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void skipDeclaration(char c) throws XMLStreamException {
        char cDtdNextFromCurr;
        while (c != '>') {
            if (this.mInputPtr < this.mInputEnd) {
                char[] cArr = this.mInputBuffer;
                int i = this.mInputPtr;
                this.mInputPtr = i + 1;
                cDtdNextFromCurr = cArr[i];
            } else {
                cDtdNextFromCurr = dtdNextFromCurr();
            }
            c = cDtdNextFromCurr;
            if (c == '\n' || c == '\r') {
                skipCRLF(c);
            } else if (c == '\'' || c == '\"') {
                skipLiteral(c);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void skipLiteral(char quoteChar) throws XMLStreamException {
        char cDtdNextFromCurr;
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
            if (c == '\n' || c == '\r') {
                skipCRLF(c);
            } else if (c == quoteChar) {
                return;
            }
        }
    }

    private void skipDTDName() throws XMLStreamException {
        skipFullName(getNextChar(getErrorMsg()));
    }

    protected String getErrorMsg() {
        return this.mIsExternal ? ParsingErrorMsgs.SUFFIX_IN_DTD_EXTERNAL : ParsingErrorMsgs.SUFFIX_IN_DTD_INTERNAL;
    }

    protected void throwIllegalCall() throws Error {
        throw new IllegalStateException("Internal error: this method should never be called");
    }
}
