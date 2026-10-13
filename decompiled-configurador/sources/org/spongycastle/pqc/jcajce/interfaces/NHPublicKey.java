package org.spongycastle.pqc.jcajce.interfaces;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/interfaces/NHPublicKey.class */
public interface NHPublicKey extends NHKey, PublicKey {
    byte[] getPublicData();
}
