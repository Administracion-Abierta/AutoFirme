package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/interfaces/ElGamalPrivateKey.class */
public interface ElGamalPrivateKey extends ElGamalKey, DHPrivateKey {
    BigInteger getX();
}
