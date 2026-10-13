package org.spongycastle.jcajce.spec;

import javax.crypto.SecretKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/spec/RepeatedSecretKeySpec.class */
public class RepeatedSecretKeySpec implements SecretKey {
    private String algorithm;

    public RepeatedSecretKeySpec(String algorithm) {
        this.algorithm = algorithm;
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
