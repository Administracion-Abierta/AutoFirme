package org.spongycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/interfaces/NHPrivateKey.class */
public interface NHPrivateKey extends NHKey, PrivateKey {
    short[] getSecretData();
}
