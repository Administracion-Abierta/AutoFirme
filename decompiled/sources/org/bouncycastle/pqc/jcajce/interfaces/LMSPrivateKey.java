package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/LMSPrivateKey.class */
public interface LMSPrivateKey extends LMSKey, PrivateKey {
    long getIndex();

    long getUsagesRemaining();

    LMSPrivateKey extractKeyShard(int i);
}
