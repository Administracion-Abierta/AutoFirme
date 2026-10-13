package org.bouncycastle.math.ec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECLookupTable.class */
public interface ECLookupTable {
    int getSize();

    ECPoint lookup(int i);

    ECPoint lookupVar(int i);
}
