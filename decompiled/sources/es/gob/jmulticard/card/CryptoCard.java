package es.gob.jmulticard.card;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/CryptoCard.class */
public interface CryptoCard {
    String[] getAliases() throws CryptoCardException;

    X509Certificate getCertificate(String str) throws CryptoCardException, PinException;

    PrivateKeyReference getPrivateKey(String str) throws CryptoCardException;

    byte[] sign(byte[] bArr, String str, PrivateKeyReference privateKeyReference) throws CryptoCardException, PinException;
}
