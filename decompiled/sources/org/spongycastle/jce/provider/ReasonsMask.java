package org.spongycastle.jce.provider;

import org.spongycastle.asn1.x509.ReasonFlags;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/ReasonsMask.class */
class ReasonsMask {
    private int _reasons;
    static final ReasonsMask allReasons = new ReasonsMask(33023);

    ReasonsMask(ReasonFlags reasons) {
        this._reasons = reasons.intValue();
    }

    private ReasonsMask(int reasons) {
        this._reasons = reasons;
    }

    ReasonsMask() {
        this(0);
    }

    void addReasons(ReasonsMask mask) {
        this._reasons |= mask.getReasons();
    }

    boolean isAllReasons() {
        return this._reasons == allReasons._reasons;
    }

    ReasonsMask intersect(ReasonsMask mask) {
        ReasonsMask _mask = new ReasonsMask();
        _mask.addReasons(new ReasonsMask(this._reasons & mask.getReasons()));
        return _mask;
    }

    boolean hasNewReasons(ReasonsMask mask) {
        return (this._reasons | (mask.getReasons() ^ this._reasons)) != 0;
    }

    int getReasons() {
        return this._reasons;
    }
}
