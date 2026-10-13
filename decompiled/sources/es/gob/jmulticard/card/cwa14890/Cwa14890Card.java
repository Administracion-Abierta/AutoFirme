package es.gob.jmulticard.card.cwa14890;

import es.gob.jmulticard.connection.ApduConnectionException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/cwa14890/Cwa14890Card.class */
public interface Cwa14890Card {
    void verifyIcc() throws IOException, CertificateException;

    RSAPublicKey getIccCertPublicKey() throws IOException;

    void verifyIfdCertificateChain(Cwa14890PublicConstants cwa14890PublicConstants) throws ApduConnectionException;

    byte[] getInternalAuthenticateMessage(byte[] bArr, byte[] bArr2) throws ApduConnectionException;

    boolean externalAuthentication(byte[] bArr) throws ApduConnectionException;

    void setKeysToAuthentication(byte[] bArr, byte[] bArr2) throws ApduConnectionException;

    byte[] getChallenge() throws ApduConnectionException;

    byte[] getSerialNumber() throws ApduConnectionException;

    byte[] getRefIccPrivateKey(Cwa14890PublicConstants cwa14890PublicConstants);

    byte[] getChrCCvIfd(Cwa14890PublicConstants cwa14890PublicConstants);

    RSAPrivateKey getIfdPrivateKey(Cwa14890PrivateConstants cwa14890PrivateConstants);

    int getIfdKeyLength(Cwa14890PublicConstants cwa14890PublicConstants);
}
