package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/NHPublicKey.class */
public interface NHPublicKey extends NHKey, PublicKey {
    byte[] getPublicData();
}
