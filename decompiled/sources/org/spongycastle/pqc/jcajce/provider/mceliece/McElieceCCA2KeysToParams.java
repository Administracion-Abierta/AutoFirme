package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/McElieceCCA2KeysToParams.class */
public class McElieceCCA2KeysToParams {
    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof BCMcElieceCCA2PublicKey) {
            BCMcElieceCCA2PublicKey k = (BCMcElieceCCA2PublicKey) key;
            return k.getKeyParams();
        }
        throw new InvalidKeyException("can't identify McElieceCCA2 public key: " + key.getClass().getName());
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof BCMcElieceCCA2PrivateKey) {
            BCMcElieceCCA2PrivateKey k = (BCMcElieceCCA2PrivateKey) key;
            return k.getKeyParams();
        }
        throw new InvalidKeyException("can't identify McElieceCCA2 private key.");
    }
}
