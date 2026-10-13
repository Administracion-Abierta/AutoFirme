package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/MLKEMPrivateKey.class */
public interface MLKEMPrivateKey extends PrivateKey, MLKEMKey {
    MLKEMPublicKey getPublicKey();

    byte[] getPrivateData();

    byte[] getSeed();
}
