package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/interfaces/GOST3410PublicKey.class */
public interface GOST3410PublicKey extends GOST3410Key, PublicKey {
    BigInteger getY();
}
