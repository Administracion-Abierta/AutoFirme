package es.gob.afirma.standalone.crypto;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/DesCipher.class */
public final class DesCipher {
    private static final int PADDING_LENGTH = 8;

    private DesCipher() {
    }

    public static byte[] cipher(byte[] bArr, byte[] bArr2) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(1, new SecretKeySpec(bArr2, "DES"));
            return cipher.doFinal(padding(bArr, 8));
        } catch (Exception e) {
            throw new GeneralSecurityException("Algoritmo o formato no soportado por la maquina virtual: " + e);
        }
    }

    public static byte[] decipher(byte[] bArr, byte[] bArr2) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(2, new SecretKeySpec(bArr2, "DES"));
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            throw new GeneralSecurityException("Algoritmo o formato no soportado por la maquina virtual: " + e);
        }
    }

    private static byte[] padding(byte[] bArr, int i) {
        if (bArr.length % i == 0) {
            return bArr;
        }
        return Arrays.copyOf(bArr, ((bArr.length / i) + 1) * i);
    }

    public static int getPaddingLength() {
        return 8;
    }
}
