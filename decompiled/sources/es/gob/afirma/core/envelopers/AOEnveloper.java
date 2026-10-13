package es.gob.afirma.core.envelopers;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.ciphers.CipherConstants;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.zip.DataFormatException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/envelopers/AOEnveloper.class */
public interface AOEnveloper {
    byte[] envelop(byte[] bArr, String str, String str2, KeyStore.PrivateKeyEntry privateKeyEntry, X509Certificate[] x509CertificateArr, CipherConstants.AOCipherAlgorithm aOCipherAlgorithm, String str3, Properties properties) throws AOException;

    byte[] encrypt(byte[] bArr, String str, String str2, CipherConstants.AOCipherAlgorithm aOCipherAlgorithm, String str3) throws AOException;

    byte[] recoverData(byte[] bArr, KeyStore.PrivateKeyEntry privateKeyEntry) throws InvalidKeySpecException, DataFormatException, InvalidKeyException, IOException, AOException;
}
