package org.bouncycastle.jce.spec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jce.interfaces.IESKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/IEKeySpec.class */
public class IEKeySpec implements KeySpec, IESKey {
    private PublicKey pubKey;
    private PrivateKey privKey;

    public IEKeySpec(PrivateKey privateKey, PublicKey publicKey) {
        this.privKey = privateKey;
        this.pubKey = publicKey;
    }

    @Override // org.bouncycastle.jce.interfaces.IESKey
    public PublicKey getPublic() {
        return this.pubKey;
    }

    @Override // org.bouncycastle.jce.interfaces.IESKey
    public PrivateKey getPrivate() {
        return this.privKey;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "IES";
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
