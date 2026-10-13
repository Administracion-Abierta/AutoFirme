package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/XMSSMTPrivateKey.class */
public interface XMSSMTPrivateKey extends XMSSMTKey, PrivateKey {
    long getIndex();

    long getUsagesRemaining();

    XMSSMTPrivateKey extractKeyShard(int i);
}
