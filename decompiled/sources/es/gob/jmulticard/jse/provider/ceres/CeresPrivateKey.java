package es.gob.jmulticard.jse.provider.ceres;

import es.gob.jmulticard.card.CryptoCard;
import es.gob.jmulticard.card.fnmt.ceres.Ceres;
import es.gob.jmulticard.card.fnmt.ceres.CeresPrivateKeyReference;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/ceres/CeresPrivateKey.class */
public final class CeresPrivateKey implements RSAPrivateKey {
    private static final long serialVersionUID = 4403051294889801855L;
    private final transient Ceres ceres;
    private final transient CeresPrivateKeyReference keyRef;
    private final BigInteger modulus;

    CeresPrivateKey(CeresPrivateKeyReference keyReference, Ceres card, BigInteger mod) {
        this.keyRef = keyReference;
        this.ceres = card;
        this.modulus = mod;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "RSA";
    }

    CryptoCard getCryptoCard() {
        return this.ceres;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return null;
    }

    @Override // java.security.Key
    public String getFormat() {
        return null;
    }

    CeresPrivateKeyReference getReference() {
        return this.keyRef;
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override // java.security.interfaces.RSAPrivateKey
    public BigInteger getPrivateExponent() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return this.keyRef.toString();
    }

    private void writeObject(ObjectOutputStream outStream) throws IOException {
        throw new NotSerializableException();
    }
}
