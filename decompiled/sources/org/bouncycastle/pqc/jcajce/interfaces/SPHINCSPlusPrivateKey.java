package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/SPHINCSPlusPrivateKey.class */
public interface SPHINCSPlusPrivateKey extends PrivateKey, SPHINCSPlusKey {
    SPHINCSPlusPublicKey getPublicKey();
}
