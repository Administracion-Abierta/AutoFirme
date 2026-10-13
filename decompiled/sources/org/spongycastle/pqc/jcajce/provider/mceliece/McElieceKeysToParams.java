package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/McElieceKeysToParams.class */
public class McElieceKeysToParams {
    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof BCMcEliecePublicKey) {
            BCMcEliecePublicKey k = (BCMcEliecePublicKey) key;
            return k.getKeyParams();
        }
        throw new InvalidKeyException("can't identify McEliece public key: " + key.getClass().getName());
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof BCMcEliecePrivateKey) {
            BCMcEliecePrivateKey k = (BCMcEliecePrivateKey) key;
            return new McEliecePrivateKeyParameters(k.getN(), k.getK(), k.getField(), k.getGoppaPoly(), k.getP1(), k.getP2(), k.getSInv());
        }
        throw new InvalidKeyException("can't identify McEliece private key.");
    }
}
