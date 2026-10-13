package es.gob.jmulticard.jse.provider;

import es.gob.jmulticard.card.CryptoCard;
import es.gob.jmulticard.card.Location;
import es.gob.jmulticard.card.dnie.DniePrivateKeyReference;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/DniePrivateKey.class */
public final class DniePrivateKey implements RSAPrivateKey {
    private static final long serialVersionUID = 4403051294889801855L;
    private final transient DniePrivateKeyReference dniPrivateKeyReference;
    private final BigInteger modulus;

    public DniePrivateKey(DniePrivateKeyReference keyReference, BigInteger mod) {
        this.dniPrivateKeyReference = keyReference;
        this.modulus = mod;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "RSA";
    }

    public CryptoCard getCryptoCard() {
        return this.dniPrivateKeyReference.getDnieCard();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return null;
    }

    @Override // java.security.Key
    public String getFormat() {
        return null;
    }

    byte[] getId() {
        return this.dniPrivateKeyReference.getIdentifier();
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.modulus;
    }

    Location getPath() {
        return this.dniPrivateKeyReference.getKeyPath();
    }

    @Override // java.security.interfaces.RSAPrivateKey
    public BigInteger getPrivateExponent() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return this.dniPrivateKeyReference.getLabel();
    }

    private void writeObject(ObjectOutputStream outStream) throws IOException {
        throw new NotSerializableException();
    }

    byte getKeyReference() {
        return this.dniPrivateKeyReference.getKeyReference();
    }

    int getKeyBitSize() {
        return this.dniPrivateKeyReference.getKeyBitSize();
    }

    public DniePrivateKeyReference getDniePrivateKeyReference() {
        return this.dniPrivateKeyReference;
    }
}
