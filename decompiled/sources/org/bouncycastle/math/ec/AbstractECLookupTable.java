package org.bouncycastle.math.ec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/AbstractECLookupTable.class */
public abstract class AbstractECLookupTable implements ECLookupTable {
    @Override // org.bouncycastle.math.ec.ECLookupTable
    public ECPoint lookupVar(int i) {
        return lookup(i);
    }
}
