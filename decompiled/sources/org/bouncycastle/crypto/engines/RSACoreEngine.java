package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RSACoreEngine.class */
class RSACoreEngine {
    private RSAKeyParameters key;
    private boolean forEncryption;

    RSACoreEngine() {
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom) cipherParameters).getParameters();
        }
        this.forEncryption = z;
        this.key = (RSAKeyParameters) cipherParameters;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", ConstraintUtils.bitsOfSecurityFor(this.key.getModulus()), this.key, getPurpose(this.key.isPrivate(), z)));
    }

    public int getInputBlockSize() {
        int iBitLength = this.key.getModulus().bitLength();
        return this.forEncryption ? ((iBitLength + 7) / 8) - 1 : (iBitLength + 7) / 8;
    }

    public int getOutputBlockSize() {
        int iBitLength = this.key.getModulus().bitLength();
        return this.forEncryption ? (iBitLength + 7) / 8 : ((iBitLength + 7) / 8) - 1;
    }

    public BigInteger convertInput(byte[] bArr, int i, int i2) {
        byte[] bArr2;
        if (i2 > getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (i2 == getInputBlockSize() + 1 && !this.forEncryption) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (i == 0 && i2 == bArr.length) {
            bArr2 = bArr;
        } else {
            bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
        }
        BigInteger bigInteger = new BigInteger(1, bArr2);
        if (bigInteger.compareTo(this.key.getModulus()) >= 0) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        return bigInteger;
    }

    public byte[] convertOutput(BigInteger bigInteger) {
        byte[] bArr;
        byte[] byteArray = bigInteger.toByteArray();
        if (!this.forEncryption) {
            if (byteArray[0] == 0) {
                bArr = new byte[byteArray.length - 1];
                System.arraycopy(byteArray, 1, bArr, 0, bArr.length);
            } else {
                bArr = new byte[byteArray.length];
                System.arraycopy(byteArray, 0, bArr, 0, bArr.length);
            }
            Arrays.fill(byteArray, (byte) 0);
            return bArr;
        }
        if (byteArray[0] == 0 && byteArray.length > getOutputBlockSize()) {
            byte[] bArr2 = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, bArr2, 0, bArr2.length);
            return bArr2;
        }
        if (byteArray.length >= getOutputBlockSize()) {
            return byteArray;
        }
        byte[] bArr3 = new byte[getOutputBlockSize()];
        System.arraycopy(byteArray, 0, bArr3, bArr3.length - byteArray.length, byteArray.length);
        return bArr3;
    }

    public BigInteger processBlock(BigInteger bigInteger) {
        RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters;
        BigInteger publicExponent;
        if (!(this.key instanceof RSAPrivateCrtKeyParameters) || (publicExponent = (rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters) this.key).getPublicExponent()) == null) {
            return bigInteger.modPow(this.key.getExponent(), this.key.getModulus());
        }
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

    private CryptoServicePurpose getPurpose(boolean z, boolean z2) {
        boolean z3 = z && z2;
        boolean z4 = !z && z2;
        boolean z5 = (z || z2) ? false : true;
        if (z3) {
            return CryptoServicePurpose.SIGNING;
        }
        if (z4) {
            return CryptoServicePurpose.ENCRYPTION;
        }
        return z5 ? CryptoServicePurpose.VERIFYING : CryptoServicePurpose.DECRYPTION;
    }
}
