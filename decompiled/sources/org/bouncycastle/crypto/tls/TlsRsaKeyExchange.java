package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/tls/TlsRsaKeyExchange.class */
public abstract class TlsRsaKeyExchange {
    public static final int PRE_MASTER_SECRET_LENGTH = 48;
    private static final BigInteger ONE = BigInteger.valueOf(1);

    private TlsRsaKeyExchange() {
    }

    public static byte[] decryptPreMasterSecret(byte[] bArr, int i, int i2, RSAKeyParameters rSAKeyParameters, int i3, SecureRandom secureRandom) {
        if (bArr == null || i2 < 1 || i2 > getInputLimit(rSAKeyParameters) || i < 0 || i > bArr.length - i2) {
            throw new IllegalArgumentException("input not a valid EncryptedPreMasterSecret");
        }
        if (!rSAKeyParameters.isPrivate()) {
            throw new IllegalArgumentException("'privateKey' must be an RSA private key");
        }
        BigInteger modulus = rSAKeyParameters.getModulus();
        int iBitLength = modulus.bitLength();
        if (iBitLength < 512) {
            throw new IllegalArgumentException("'privateKey' must be at least 512 bits");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", ConstraintUtils.bitsOfSecurityFor(modulus), rSAKeyParameters, CryptoServicePurpose.DECRYPTION));
        if ((i3 & 65535) != i3) {
            throw new IllegalArgumentException("'protocolVersion' must be a 16 bit value");
        }
        SecureRandom secureRandom2 = CryptoServicesRegistrar.getSecureRandom(secureRandom);
        byte[] bArr2 = new byte[48];
        secureRandom2.nextBytes(bArr2);
        try {
            byte[] bArrRsaBlinded = rsaBlinded(rSAKeyParameters, convertInput(modulus, bArr, i, i2), secureRandom2);
            int i4 = (iBitLength - 1) / 8;
            int length = bArrRsaBlinded.length - 48;
            int iCheckPkcs1Encoding2 = checkPkcs1Encoding2(bArrRsaBlinded, i4, 48) | ((-((Pack.bigEndianToShort(bArrRsaBlinded, length) ^ i3) & 65535)) >> 31);
            for (int i5 = 0; i5 < 48; i5++) {
                bArr2[i5] = (byte) ((bArr2[i5] & iCheckPkcs1Encoding2) | (bArrRsaBlinded[length + i5] & (iCheckPkcs1Encoding2 ^ (-1))));
            }
            Arrays.fill(bArrRsaBlinded, (byte) 0);
        } catch (Exception e) {
        }
        return bArr2;
    }

    public static int getInputLimit(RSAKeyParameters rSAKeyParameters) {
        return (rSAKeyParameters.getModulus().bitLength() + 7) / 8;
    }

    private static int caddTo(int i, int i2, byte[] bArr, byte[] bArr2) {
        int i3 = i2 & 255;
        int i4 = 0;
        for (int i5 = i - 1; i5 >= 0; i5--) {
            int i6 = i4 + (bArr2[i5] & 255) + (bArr[i5] & i3);
            bArr2[i5] = (byte) i6;
            i4 = i6 >>> 8;
        }
        return i4;
    }

    private static int checkPkcs1Encoding2(byte[] bArr, int i, int i2) {
        int i3 = (i - i2) - 10;
        int length = bArr.length - i;
        int length2 = (bArr.length - 1) - i2;
        for (int i4 = 0; i4 < length; i4++) {
            i3 |= -(bArr[i4] & 255);
        }
        int i5 = i3 | (-((bArr[length] & 255) ^ 2));
        for (int i6 = length + 1; i6 < length2; i6++) {
            i5 |= (bArr[i6] & 255) - 1;
        }
        return (i5 | (-(bArr[length2] & 255))) >> 31;
    }

    private static BigInteger convertInput(BigInteger bigInteger, byte[] bArr, int i, int i2) {
        BigInteger bigIntegerFromUnsignedByteArray = BigIntegers.fromUnsignedByteArray(bArr, i, i2);
        if (bigIntegerFromUnsignedByteArray.compareTo(bigInteger) < 0) {
            return bigIntegerFromUnsignedByteArray;
        }
        throw new DataLengthException("input too large for RSA cipher.");
    }

    private static BigInteger rsa(RSAKeyParameters rSAKeyParameters, BigInteger bigInteger) {
        return bigInteger.modPow(rSAKeyParameters.getExponent(), rSAKeyParameters.getModulus());
    }

    private static byte[] rsaBlinded(RSAKeyParameters rSAKeyParameters, BigInteger bigInteger, SecureRandom secureRandom) {
        RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters;
        BigInteger publicExponent;
        BigInteger modulus = rSAKeyParameters.getModulus();
        int iBitLength = (modulus.bitLength() / 8) + 1;
        if (!(rSAKeyParameters instanceof RSAPrivateCrtKeyParameters) || (publicExponent = (rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters) rSAKeyParameters).getPublicExponent()) == null) {
            return toBytes(rsa(rSAKeyParameters, bigInteger), iBitLength);
        }
        BigInteger bigIntegerCreateRandomInRange = BigIntegers.createRandomInRange(ONE, modulus.subtract(ONE), secureRandom);
        BigInteger bigIntegerModPow = bigIntegerCreateRandomInRange.modPow(publicExponent, modulus);
        BigInteger bigIntegerModOddInverse = BigIntegers.modOddInverse(modulus, bigIntegerCreateRandomInRange);
        BigInteger bigIntegerRsaCrt = rsaCrt(rSAPrivateCrtKeyParameters, bigIntegerModPow.multiply(bigInteger).mod(modulus));
        BigInteger bigIntegerMod = bigIntegerModOddInverse.add(ONE).multiply(bigIntegerRsaCrt).mod(modulus);
        byte[] bytes = toBytes(bigIntegerRsaCrt, iBitLength);
        byte[] bytes2 = toBytes(modulus, iBitLength);
        byte[] bytes3 = toBytes(bigIntegerMod, iBitLength);
        caddTo(iBitLength, subFrom(iBitLength, bytes, bytes3), bytes2, bytes3);
        return bytes3;
    }

    private static BigInteger rsaCrt(RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters, BigInteger bigInteger) {
        BigInteger publicExponent = rSAPrivateCrtKeyParameters.getPublicExponent();
        BigInteger p = rSAPrivateCrtKeyParameters.getP();
        BigInteger q = rSAPrivateCrtKeyParameters.getQ();
        BigInteger dp = rSAPrivateCrtKeyParameters.getDP();
        BigInteger dq = rSAPrivateCrtKeyParameters.getDQ();
        BigInteger qInv = rSAPrivateCrtKeyParameters.getQInv();
        BigInteger bigIntegerModPow = bigInteger.remainder(p).modPow(dp, p);
        BigInteger bigIntegerModPow2 = bigInteger.remainder(q).modPow(dq, q);
        BigInteger bigIntegerAdd = bigIntegerModPow.subtract(bigIntegerModPow2).multiply(qInv).mod(p).multiply(q).add(bigIntegerModPow2);
        if (bigIntegerAdd.modPow(publicExponent, rSAPrivateCrtKeyParameters.getModulus()).equals(bigInteger)) {
            return bigIntegerAdd;
        }
        throw new IllegalStateException("RSA engine faulty decryption/signing detected");
    }

    private static int subFrom(int i, byte[] bArr, byte[] bArr2) {
        int i2 = 0;
        for (int i3 = i - 1; i3 >= 0; i3--) {
            int i4 = i2 + ((bArr2[i3] & 255) - (bArr[i3] & 255));
            bArr2[i3] = (byte) i4;
            i2 = i4 >> 8;
        }
        return i2;
    }

    private static byte[] toBytes(BigInteger bigInteger, int i) {
        byte[] byteArray = bigInteger.toByteArray();
        byte[] bArr = new byte[i];
        System.arraycopy(byteArray, 0, bArr, bArr.length - byteArray.length, byteArray.length);
        return bArr;
    }
}
