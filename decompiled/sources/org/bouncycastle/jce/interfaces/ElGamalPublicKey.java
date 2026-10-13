package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/interfaces/ElGamalPublicKey.class */
public interface ElGamalPublicKey extends ElGamalKey, DHPublicKey {
    BigInteger getY();
}
