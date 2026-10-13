package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/NHPrivateKey.class */
public interface NHPrivateKey extends NHKey, PrivateKey {
    short[] getSecretData();
}
