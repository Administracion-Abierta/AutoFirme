package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSData.class */
public interface ERSData {
    byte[] getHash(DigestCalculator digestCalculator, byte[] bArr);
}
