package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/FalconPrivateKey.class */
public interface FalconPrivateKey extends PrivateKey, FalconKey {
    FalconPublicKey getPublicKey();
}
