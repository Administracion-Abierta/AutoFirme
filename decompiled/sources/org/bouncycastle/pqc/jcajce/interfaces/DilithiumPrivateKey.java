package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/DilithiumPrivateKey.class */
public interface DilithiumPrivateKey extends PrivateKey, DilithiumKey {
    DilithiumPublicKey getPublicKey();
}
