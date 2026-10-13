package org.spongycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/interfaces/ECPrivateKey.class */
public interface ECPrivateKey extends ECKey, PrivateKey {
    BigInteger getD();
}
