package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/MLKEMPublicKey.class */
public interface MLKEMPublicKey extends PublicKey, MLKEMKey {
    byte[] getPublicData();
}
