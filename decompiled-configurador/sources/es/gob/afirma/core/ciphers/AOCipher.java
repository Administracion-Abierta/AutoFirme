package es.gob.afirma.core.ciphers;

import es.gob.afirma.core.AOException;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/ciphers/AOCipher.class */
public interface AOCipher {
    AOCipherConfig[] getSupportedConfigs();

    byte[] cipher(byte[] bArr, AOCipherConfig aOCipherConfig, Key key) throws KeyException, AOException;

    byte[] decipher(byte[] bArr, AOCipherConfig aOCipherConfig, Key key) throws KeyException, AOException;

    Key decodeKey(byte[] bArr, AOCipherConfig aOCipherConfig, Object[] objArr) throws KeyException;

    Key decodePassphrase(char[] cArr, AOCipherConfig aOCipherConfig, Object[] objArr) throws AOException;

    Key generateKey(AOCipherConfig aOCipherConfig) throws NoSuchAlgorithmException, AOException;
}
