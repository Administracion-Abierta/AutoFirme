package org.bouncycastle.jcajce.spec;

import javax.crypto.SecretKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/RepeatedSecretKeySpec.class */
public class RepeatedSecretKeySpec implements SecretKey {
    private String algorithm;

    public RepeatedSecretKeySpec(String str) {
        this.algorithm = str;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return null;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return null;
    }
}
