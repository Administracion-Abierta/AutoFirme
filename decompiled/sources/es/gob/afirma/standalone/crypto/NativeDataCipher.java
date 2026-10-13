package es.gob.afirma.standalone.crypto;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.standalone.plugins.DataCipher;
import es.gob.afirma.standalone.plugins.EncryptingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/NativeDataCipher.class */
public final class NativeDataCipher implements DataCipher {
    private static final char PADDING_CHAR_SEPARATOR = '.';
    private final byte[] cipherKey;

    public NativeDataCipher(byte[] bArr) {
        this.cipherKey = bArr != null ? (byte[]) bArr.clone() : null;
    }

    @Override // es.gob.afirma.standalone.plugins.DataCipher
    public String cipher(byte[] bArr) throws EncryptingException {
        try {
            return cipherData(bArr, this.cipherKey);
        } catch (Exception e) {
            throw new EncryptingException("Error durante el cifrado de los datos", e);
        }
    }

    @Override // es.gob.afirma.standalone.plugins.DataCipher
    public byte[] decipher(byte[] bArr) throws EncryptingException {
        try {
            return decipherData(bArr, this.cipherKey);
        } catch (Exception e) {
            throw new EncryptingException("Error durante el descifrado de los datos", e);
        }
    }

    private static byte[] decipherData(byte[] bArr, byte[] bArr2) throws GeneralSecurityException, IOException {
        String strReplace = new String(bArr, StandardCharsets.UTF_8).replace("_", "/").replace("-", Marker.ANY_NON_NULL_MARKER);
        if (bArr2 != null) {
            return decipherData(strReplace, bArr2);
        }
        return Base64.decode(strReplace);
    }

    private static byte[] decipherData(String str, byte[] bArr) throws GeneralSecurityException, IOException, IllegalArgumentException {
        int i = 0;
        int iIndexOf = str.indexOf(46);
        if (iIndexOf != -1) {
            i = Integer.parseInt(str.substring(0, iIndexOf));
        }
        byte[] bArrDecipher = DesCipher.decipher(Base64.decode(str.substring(iIndexOf + 1).replace('-', '+').replace('_', '/')), bArr);
        return i == 0 ? bArrDecipher : Arrays.copyOf(bArrDecipher, bArrDecipher.length - i);
    }

    private static String cipherData(byte[] bArr, byte[] bArr2) throws GeneralSecurityException {
        return new StringBuilder((int) (((double) bArr.length) * 1.2d)).append(Integer.toString((DesCipher.getPaddingLength() - (bArr.length % DesCipher.getPaddingLength())) % DesCipher.getPaddingLength())).append('.').append(Base64.encode(DesCipher.cipher(bArr, bArr2), true)).toString();
    }
}
