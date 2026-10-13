package org.bouncycastle.asn1;

import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/LimitedInputStream.class */
abstract class LimitedInputStream extends InputStream {
    protected final InputStream _in;
    private int _limit;

    LimitedInputStream(InputStream inputStream, int i) {
        this._in = inputStream;
        this._limit = i;
    }

    int getLimit() {
        return this._limit;
    }

    protected void setParentEofDetect(boolean z) {
        if (this._in instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream) this._in).setEofOn00(z);
        }
    }
}
