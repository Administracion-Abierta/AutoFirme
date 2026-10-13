package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/parameters/NTRUHPSParameterSet.class */
public abstract class NTRUHPSParameterSet extends NTRUParameterSet {
    NTRUHPSParameterSet(int i, int i2, int i3, int i4, int i5) {
        super(i, i2, i3, i4, i5);
    }

    @Override // org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet
    public Polynomial createPolynomial() {
        return new HPSPolynomial(this);
    }

    @Override // org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet
    public int sampleFgBytes() {
        return sampleIidBytes() + sampleFixedTypeBytes();
    }

    @Override // org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet
    public int sampleRmBytes() {
        return sampleIidBytes() + sampleFixedTypeBytes();
    }

    public int weight() {
        return (q() / 8) - 2;
    }
}
