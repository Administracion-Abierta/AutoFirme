package org.spongycastle.jce.provider;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPrivateKeyParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/DHUtil.class */
public class DHUtil {
    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof DHPublicKey) {
            DHPublicKey k = (DHPublicKey) key;
            return new DHPublicKeyParameters(k.getY(), new DHParameters(k.getParams().getP(), k.getParams().getG(), null, k.getParams().getL()));
        }
        throw new InvalidKeyException("can't identify DH public key.");
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof DHPrivateKey) {
            DHPrivateKey k = (DHPrivateKey) key;
            return new DHPrivateKeyParameters(k.getX(), new DHParameters(k.getParams().getP(), k.getParams().getG(), null, k.getParams().getL()));
        }
        throw new InvalidKeyException("can't identify DH private key.");
    }
}
