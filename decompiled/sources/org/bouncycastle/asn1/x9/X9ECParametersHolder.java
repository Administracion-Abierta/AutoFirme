package org.bouncycastle.asn1.x9;

import org.bouncycastle.math.ec.ECCurve;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x9/X9ECParametersHolder.class */
public abstract class X9ECParametersHolder {
    private ECCurve curve;
    private X9ECParameters params;

    public synchronized ECCurve getCurve() {
        if (this.curve == null) {
            this.curve = createCurve();
        }
        return this.curve;
    }

    public synchronized X9ECParameters getParameters() {
        if (this.params == null) {
            this.params = createParameters();
        }
        return this.params;
    }

    protected ECCurve createCurve() {
        return createParameters().getCurve();
    }

    protected abstract X9ECParameters createParameters();
}
