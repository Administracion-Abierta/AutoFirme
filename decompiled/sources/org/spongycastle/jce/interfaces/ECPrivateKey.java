package org.spongycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/ECPrivateKey.class */
public interface ECPrivateKey extends ECKey, PrivateKey {
    BigInteger getD();
}
