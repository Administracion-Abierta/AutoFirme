package org.spongycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPrivateKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/interfaces/ElGamalPrivateKey.class */
public interface ElGamalPrivateKey extends ElGamalKey, DHPrivateKey {
    BigInteger getX();
}
