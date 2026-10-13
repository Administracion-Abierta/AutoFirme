package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/SLHDSAPrivateKey.class */
public interface SLHDSAPrivateKey extends PrivateKey, SLHDSAKey {
    SLHDSAPublicKey getPublicKey();
}
