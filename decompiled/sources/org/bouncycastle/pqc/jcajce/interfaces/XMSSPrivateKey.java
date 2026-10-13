package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/XMSSPrivateKey.class */
public interface XMSSPrivateKey extends XMSSKey, PrivateKey {
    long getIndex();

    long getUsagesRemaining();

    XMSSPrivateKey extractKeyShard(int i);
}
