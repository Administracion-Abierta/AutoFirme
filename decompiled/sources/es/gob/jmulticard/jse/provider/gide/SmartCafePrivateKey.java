package es.gob.jmulticard.jse.provider.gide;

import es.gob.jmulticard.card.CryptoCard;
import es.gob.jmulticard.card.gide.smartcafe.SmartCafePkcs15Applet;
import es.gob.jmulticard.card.gide.smartcafe.SmartCafePrivateKeyReference;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/gide/SmartCafePrivateKey.class */
public final class SmartCafePrivateKey implements RSAPrivateKey {
    private static final long serialVersionUID = 4403051294889801855L;
    private final int id;
    private final transient CryptoCard card;

    SmartCafePrivateKey(SmartCafePrivateKeyReference keyReference, SmartCafePkcs15Applet cryptoCard) {
        this.id = keyReference.getKeyOrdinal();
        this.card = cryptoCard;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "RSA";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return null;
    }

    @Override // java.security.Key
    public String getFormat() {
        return null;
    }

    int getId() {
        return this.id;
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.interfaces.RSAPrivateKey
    public BigInteger getPrivateExponent() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "Clave privada para G&D SmartCafe (con Applet PKCS#15) con ordinal " + this.id;
    }

    private void writeObject(ObjectOutputStream outStream) throws IOException {
        throw new NotSerializableException();
    }

    public CryptoCard getCryptoCard() {
        return this.card;
    }
}
