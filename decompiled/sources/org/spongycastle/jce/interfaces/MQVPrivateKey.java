package org.spongycastle.jce.interfaces;

import java.security.PrivateKey;
import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/MQVPrivateKey.class */
public interface MQVPrivateKey extends PrivateKey {
    PrivateKey getStaticPrivateKey();

    PrivateKey getEphemeralPrivateKey();

    PublicKey getEphemeralPublicKey();
}
