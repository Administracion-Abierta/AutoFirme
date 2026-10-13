package org.spongycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/GOST3410PrivateKey.class */
public interface GOST3410PrivateKey extends GOST3410Key, PrivateKey {
    BigInteger getX();
}
