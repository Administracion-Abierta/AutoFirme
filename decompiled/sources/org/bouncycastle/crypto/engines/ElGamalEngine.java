package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ElGamalKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ElGamalEngine.class */
public class ElGamalEngine implements AsymmetricBlockCipher {
    private ElGamalKeyParameters key;
    private SecureRandom random;
    private boolean forEncryption;
    private int bitSize;
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.key = (ElGamalKeyParameters) parametersWithRandom.getParameters();
            this.random = parametersWithRandom.getRandom();
        } else {
            this.key = (ElGamalKeyParameters) cipherParameters;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        this.forEncryption = z;
        this.bitSize = this.key.getParameters().getP().bitLength();
        if (z) {
            if (!(this.key instanceof ElGamalPublicKeyParameters)) {
                throw new IllegalArgumentException("ElGamalPublicKeyParameters are required for encryption.");
            }
        } else if (!(this.key instanceof ElGamalPrivateKeyParameters)) {
            throw new IllegalArgumentException("ElGamalPrivateKeyParameters are required for decryption.");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", ConstraintUtils.bitsOfSecurityFor(this.key.getParameters().getP()), this.key, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        return this.forEncryption ? (this.bitSize - 1) / 8 : 2 * ((this.bitSize + 7) / 8);
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        return this.forEncryption ? 2 * ((this.bitSize + 7) / 8) : (this.bitSize - 1) / 8;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) {
        byte[] bArr2;
        BigInteger bigInteger;
        if (this.key == null) {
            throw new IllegalStateException("ElGamal engine not initialised");
        }
        if (i2 > (this.forEncryption ? ((this.bitSize - 1) + 7) / 8 : getInputBlockSize())) {
            throw new DataLengthException("input too large for ElGamal cipher.\n");
        }
        BigInteger p = this.key.getParameters().getP();
        if (this.key instanceof ElGamalPrivateKeyParameters) {
            byte[] bArr3 = new byte[i2 / 2];
            byte[] bArr4 = new byte[i2 / 2];
            System.arraycopy(bArr, i, bArr3, 0, bArr3.length);
            System.arraycopy(bArr, i + bArr3.length, bArr4, 0, bArr4.length);
            return BigIntegers.asUnsignedByteArray(new BigInteger(1, bArr3).modPow(p.subtract(ONE).subtract(((ElGamalPrivateKeyParameters) this.key).getX()), p).multiply(new BigInteger(1, bArr4)).mod(p));
        }
        if (i == 0 && i2 == bArr.length) {
            bArr2 = bArr;
        } else {
            bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
        }
        BigInteger bigInteger2 = new BigInteger(1, bArr2);
        if (bigInteger2.compareTo(p) >= 0) {
            throw new DataLengthException("input too large for ElGamal cipher.\n");
        }
        ElGamalPublicKeyParameters elGamalPublicKeyParameters = (ElGamalPublicKeyParameters) this.key;
        int iBitLength = p.bitLength();
        BigInteger bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(iBitLength, this.random);
        while (true) {
            bigInteger = bigIntegerCreateRandomBigInteger;
            if (!bigInteger.equals(ZERO) && bigInteger.compareTo(p.subtract(TWO)) <= 0) {
                break;
            }
            bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(iBitLength, this.random);
        }
        BigInteger bigIntegerModPow = this.key.getParameters().getG().modPow(bigInteger, p);
        BigInteger bigIntegerMod = bigInteger2.multiply(elGamalPublicKeyParameters.getY().modPow(bigInteger, p)).mod(p);
        byte[] byteArray = bigIntegerModPow.toByteArray();
        byte[] byteArray2 = bigIntegerMod.toByteArray();
        byte[] bArr5 = new byte[getOutputBlockSize()];
        if (byteArray.length > bArr5.length / 2) {
            System.arraycopy(byteArray, 1, bArr5, (bArr5.length / 2) - (byteArray.length - 1), byteArray.length - 1);
        } else {
            System.arraycopy(byteArray, 0, bArr5, (bArr5.length / 2) - byteArray.length, byteArray.length);
        }
        if (byteArray2.length > bArr5.length / 2) {
            System.arraycopy(byteArray2, 1, bArr5, bArr5.length - (byteArray2.length - 1), byteArray2.length - 1);
        } else {
            System.arraycopy(byteArray2, 0, bArr5, bArr5.length - byteArray2.length, byteArray2.length);
        }
        return bArr5;
    }
}
