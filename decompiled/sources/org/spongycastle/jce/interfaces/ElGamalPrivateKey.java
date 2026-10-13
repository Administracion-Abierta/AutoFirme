package org.spongycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/ElGamalPrivateKey.class */
public interface ElGamalPrivateKey extends ElGamalKey, DHPrivateKey {
    BigInteger getX();
}
