package org.spongycastle.jce.interfaces;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/MQVPublicKey.class */
public interface MQVPublicKey extends PublicKey {
    PublicKey getStaticKey();

    PublicKey getEphemeralKey();
}
