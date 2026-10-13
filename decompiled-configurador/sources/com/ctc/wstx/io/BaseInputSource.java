package com.ctc.wstx.io;

import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/BaseInputSource.class */
public abstract class BaseInputSource extends WstxInputSource {
    final String mPublicId;
    SystemId mSystemId;
    protected char[] mBuffer;
    protected int mInputLast;
    long mSavedInputProcessed;
    int mSavedInputRow;
    int mSavedInputRowStart;
    int mSavedInputPtr;
    transient WstxInputLocation mParentLocation;

    @Override // com.ctc.wstx.io.WstxInputSource
    public abstract boolean fromInternalEntity();

    @Override // com.ctc.wstx.io.WstxInputSource
    protected abstract void doInitInputLocation(WstxInputData wstxInputData);

    @Override // com.ctc.wstx.io.WstxInputSource
    public abstract int readInto(WstxInputData wstxInputData) throws XMLStreamException, IOException;

    @Override // com.ctc.wstx.io.WstxInputSource
    public abstract boolean readMore(WstxInputData wstxInputData, int i) throws XMLStreamException, IOException;

    @Override // com.ctc.wstx.io.WstxInputSource
    public abstract void close() throws IOException;

    protected BaseInputSource(WstxInputSource parent, String fromEntity, String publicId, SystemId systemId) {
        super(parent, fromEntity);
        this.mSavedInputProcessed = 0L;
        this.mSavedInputRow = 1;
        this.mSavedInputRowStart = 0;
        this.mSavedInputPtr = 0;
        this.mParentLocation = null;
        this.mSystemId = systemId;
        this.mPublicId = publicId;
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public void overrideSource(URL src) {
        this.mSystemId = SystemId.construct(src);
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public URL getSource() throws IOException {
        if (this.mSystemId == null) {
            return null;
        }
        return this.mSystemId.asURL();
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public String getPublicId() {
        return this.mPublicId;
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public String getSystemId() {
        if (this.mSystemId == null) {
            return null;
        }
        return this.mSystemId.toString();
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public void saveContext(WstxInputData reader) {
        this.mSavedInputPtr = reader.mInputPtr;
        this.mSavedInputProcessed = reader.mCurrInputProcessed;
        this.mSavedInputRow = reader.mCurrInputRow;
        this.mSavedInputRowStart = reader.mCurrInputRowStart;
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public void restoreContext(WstxInputData reader) {
        reader.mInputBuffer = this.mBuffer;
        reader.mInputEnd = this.mInputLast;
        reader.mInputPtr = this.mSavedInputPtr;
        reader.mCurrInputProcessed = this.mSavedInputProcessed;
        reader.mCurrInputRow = this.mSavedInputRow;
        reader.mCurrInputRowStart = this.mSavedInputRowStart;
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    protected final WstxInputLocation getLocation() {
        return getLocation((this.mSavedInputProcessed + ((long) this.mSavedInputPtr)) - 1, this.mSavedInputRow, (this.mSavedInputPtr - this.mSavedInputRowStart) + 1);
    }

    @Override // com.ctc.wstx.io.WstxInputSource
    public final WstxInputLocation getLocation(long total, int row, int col) {
        WstxInputLocation pl;
        if (this.mParent == null) {
            pl = null;
        } else {
            WstxInputLocation pl2 = this.mParentLocation;
            if (pl2 == null) {
                this.mParentLocation = this.mParent.getLocation();
            }
            pl = this.mParent.getLocation();
        }
        return new WstxInputLocation(pl, getPublicId(), getSystemId(), total, row, col);
    }
}
