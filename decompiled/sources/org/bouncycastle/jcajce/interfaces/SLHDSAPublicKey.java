package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/SLHDSAPublicKey.class */
public interface SLHDSAPublicKey extends PublicKey, SLHDSAKey {
    byte[] getPublicData();
}
