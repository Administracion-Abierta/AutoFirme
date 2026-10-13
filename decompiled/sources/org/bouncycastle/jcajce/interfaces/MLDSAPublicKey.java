package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/MLDSAPublicKey.class */
public interface MLDSAPublicKey extends PublicKey, MLDSAKey {
    byte[] getPublicData();
}
