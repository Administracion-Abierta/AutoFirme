package org.spongycastle.jce.spec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.spongycastle.jce.interfaces.MQVPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/MQVPrivateKeySpec.class */
public class MQVPrivateKeySpec implements KeySpec, MQVPrivateKey {
    private PrivateKey staticPrivateKey;
    private PrivateKey ephemeralPrivateKey;
    private PublicKey ephemeralPublicKey;

    public MQVPrivateKeySpec(PrivateKey staticPrivateKey, PrivateKey ephemeralPrivateKey) {
        this(staticPrivateKey, ephemeralPrivateKey, null);
    }

    public MQVPrivateKeySpec(PrivateKey staticPrivateKey, PrivateKey ephemeralPrivateKey, PublicKey ephemeralPublicKey) {
        this.staticPrivateKey = staticPrivateKey;
        this.ephemeralPrivateKey = ephemeralPrivateKey;
        this.ephemeralPublicKey = ephemeralPublicKey;
    }

    @Override // org.spongycastle.jce.interfaces.MQVPrivateKey
    public PrivateKey getStaticPrivateKey() {
        return this.staticPrivateKey;
    }

    @Override // org.spongycastle.jce.interfaces.MQVPrivateKey
    public PrivateKey getEphemeralPrivateKey() {
        return this.ephemeralPrivateKey;
    }

    @Override // org.spongycastle.jce.interfaces.MQVPrivateKey
    public PublicKey getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
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
