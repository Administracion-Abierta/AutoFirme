package org.spongycastle.jce.spec;

import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.spongycastle.jce.interfaces.MQVPublicKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/spec/MQVPublicKeySpec.class */
public class MQVPublicKeySpec implements KeySpec, MQVPublicKey {
    private PublicKey staticKey;
    private PublicKey ephemeralKey;

    public MQVPublicKeySpec(PublicKey staticKey, PublicKey ephemeralKey) {
        this.staticKey = staticKey;
        this.ephemeralKey = ephemeralKey;
    }

    @Override // org.spongycastle.jce.interfaces.MQVPublicKey
    public PublicKey getStaticKey() {
        return this.staticKey;
    }

    @Override // org.spongycastle.jce.interfaces.MQVPublicKey
    public PublicKey getEphemeralKey() {
        return this.ephemeralKey;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "ECMQV";
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
