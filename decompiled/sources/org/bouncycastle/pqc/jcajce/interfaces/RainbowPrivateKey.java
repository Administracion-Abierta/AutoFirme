package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/RainbowPrivateKey.class */
public interface RainbowPrivateKey extends PrivateKey, RainbowKey {
    RainbowPublicKey getPublicKey();
}
