package com.ctc.wstx.io;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/CharArraySource.class */
public final class CharArraySource extends BaseInputSource {
    int mOffset;
    final Location mContentStart;

    protected CharArraySource(WstxInputSource parent, String fromEntity, char[] chars, int offset, int len, Location loc, SystemId sysId) {
        super(parent, fromEntity, loc.getPublicId(), sysId);
        this.mBuffer = chars;
        this.mOffset = offset;
        this.mInputLast = offset + len;
        this.mContentStart = loc;
    }

    @Override // com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    public boolean fromInternalEntity() {
        return true;
    }

    @Override // com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    protected void doInitInputLocation(WstxInputData reader) {
        reader.mCurrInputProcessed = this.mContentStart.getCharacterOffset();
        reader.mCurrInputRow = this.mContentStart.getLineNumber();
        reader.mCurrInputRowStart = (-this.mContentStart.getColumnNumber()) + 1;
    }

    @Override // com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    public int readInto(WstxInputData reader) {
        int len;
        if (this.mBuffer == null || (len = this.mInputLast - this.mOffset) < 1) {
            return -1;
        }
        reader.mInputBuffer = this.mBuffer;
        reader.mInputPtr = this.mOffset;
        reader.mInputEnd = this.mInputLast;
        this.mOffset = this.mInputLast;
        return len;
    }

    @Override // com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    public boolean readMore(WstxInputData reader, int minAmount) {
        if (reader.mInputPtr >= reader.mInputEnd) {
            int len = this.mInputLast - this.mOffset;
            return len >= minAmount && readInto(reader) > 0;
        }
        return false;
    }

    @Override // com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    public void close() {
        this.mBuffer = null;
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public void closeCompletely() {
        close();
    }
}
