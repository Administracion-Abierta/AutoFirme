package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/MLDSAPrivateKey.class */
public interface MLDSAPrivateKey extends PrivateKey, MLDSAKey {
    MLDSAPublicKey getPublicKey();

    byte[] getPrivateData();

    byte[] getSeed();
}
