package org.bouncycastle.pqc.math.ntru.parameters;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.pqc.math.ntru.HPS4096Polynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/parameters/NTRUHPS40961229.class */
public class NTRUHPS40961229 extends NTRUHPSParameterSet {
    public NTRUHPS40961229() {
        super(WinError.ERROR_CONNECTION_INVALID, 12, 32, 32, 32);
    }

    @Override // org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet, org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet
    public Polynomial createPolynomial() {
        return new HPS4096Polynomial(this);
    }
}
