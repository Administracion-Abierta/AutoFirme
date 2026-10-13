package es.gob.afirma.core.signers;

import es.gob.afirma.core.signers.der.DerInputStream;
import es.gob.afirma.core.signers.der.DerOutputStream;
import es.gob.afirma.core.signers.der.DerValue;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SignatureException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/Pkcs1Utils.class */
public final class Pkcs1Utils {
    private Pkcs1Utils() {
    }

    public static byte[] encodeSignature(byte[] bArr) throws SignatureException {
        try {
            int length = bArr.length >> 1;
            byte[] bArr2 = new byte[length];
            System.arraycopy(bArr, 0, bArr2, 0, length);
            BigInteger bigInteger = new BigInteger(1, bArr2);
            System.arraycopy(bArr, length, bArr2, 0, length);
            BigInteger bigInteger2 = new BigInteger(1, bArr2);
            DerOutputStream derOutputStream = new DerOutputStream(bArr.length + 10);
            derOutputStream.putInteger(bigInteger);
            derOutputStream.putInteger(bigInteger2);
            return new DerValue((byte) 48, derOutputStream.toByteArray()).toByteArray();
        } catch (Exception e) {
            throw new SignatureException("Could not encode signature", e);
        }
    }

    public static byte[] decodeSignature(byte[] bArr) throws SignatureException {
        try {
            DerInputStream derInputStream = new DerInputStream(bArr, 0, bArr.length, false);
            DerValue[] sequence = derInputStream.getSequence(2);
            if (sequence.length != 2 || derInputStream.available() != 0) {
                throw new IOException("Invalid encoding for signature");
            }
            BigInteger positiveBigInteger = sequence[0].getPositiveBigInteger();
            BigInteger positiveBigInteger2 = sequence[1].getPositiveBigInteger();
            byte[] bArrTrimZeroes = trimZeroes(positiveBigInteger.toByteArray());
            byte[] bArrTrimZeroes2 = trimZeroes(positiveBigInteger2.toByteArray());
            int iMax = Math.max(bArrTrimZeroes.length, bArrTrimZeroes2.length);
            byte[] bArr2 = new byte[iMax << 1];
            System.arraycopy(bArrTrimZeroes, 0, bArr2, iMax - bArrTrimZeroes.length, bArrTrimZeroes.length);
            System.arraycopy(bArrTrimZeroes2, 0, bArr2, bArr2.length - bArrTrimZeroes2.length, bArrTrimZeroes2.length);
            return bArr2;
        } catch (Exception e) {
            throw new SignatureException("Invalid encoding for signature", e);
        }
    }

    private static byte[] trimZeroes(byte[] bArr) {
        int i = 0;
        while (i < bArr.length - 1 && bArr[i] == 0) {
            i++;
        }
        if (i == 0) {
            return bArr;
        }
        byte[] bArr2 = new byte[bArr.length - i];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        return bArr2;
    }
}
