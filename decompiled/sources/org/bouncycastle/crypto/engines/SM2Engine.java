package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/SM2Engine.class */
public class SM2Engine {
    private final Digest digest;
    private final Mode mode;
    private boolean forEncryption;
    private ECKeyParameters ecKey;
    private ECDomainParameters ecParams;
    private int curveLength;
    private SecureRandom random;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/SM2Engine$Mode.class */
    public enum Mode {
        C1C2C3,
        C1C3C2
    }

    public SM2Engine() {
        this(new SM3Digest());
    }

    public SM2Engine(Mode mode) {
        this(new SM3Digest(), mode);
    }

    public SM2Engine(Digest digest) {
        this(digest, Mode.C1C2C3);
    }

    public SM2Engine(Digest digest, Mode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode cannot be NULL");
        }
        this.digest = digest;
        this.mode = mode;
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        this.forEncryption = z;
        if (z) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.ecKey = (ECKeyParameters) parametersWithRandom.getParameters();
            this.ecParams = this.ecKey.getParameters();
            if (((ECPublicKeyParameters) this.ecKey).getQ().multiply(this.ecParams.getH()).isInfinity()) {
                throw new IllegalArgumentException("invalid key: [h]Q at infinity");
            }
            this.random = parametersWithRandom.getRandom();
        } else {
            this.ecKey = (ECKeyParameters) cipherParameters;
            this.ecParams = this.ecKey.getParameters();
        }
        this.curveLength = this.ecParams.getCurve().getFieldElementEncodingLength();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("SM2", ConstraintUtils.bitsOfSecurityFor(this.ecParams.getCurve()), this.ecKey, Utils.getPurpose(z)));
    }

    public byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (i + i2 > bArr.length || i2 == 0) {
            throw new DataLengthException("input buffer too short");
        }
        return this.forEncryption ? encrypt(bArr, i, i2) : decrypt(bArr, i, i2);
    }

    public int getOutputSize(int i) {
        return 1 + (2 * this.curveLength) + i + this.digest.getDigestSize();
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    private byte[] encrypt(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] encoded;
        ECPoint eCPointNormalize;
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        ECMultiplier eCMultiplierCreateBasePointMultiplier = createBasePointMultiplier();
        do {
            BigInteger bigIntegerNextK = nextK();
            encoded = eCMultiplierCreateBasePointMultiplier.multiply(this.ecParams.getG(), bigIntegerNextK).normalize().getEncoded(false);
            eCPointNormalize = ((ECPublicKeyParameters) this.ecKey).getQ().multiply(bigIntegerNextK).normalize();
            kdf(this.digest, eCPointNormalize, bArr2);
        } while (notEncrypted(bArr2, bArr, i));
        byte[] bArr3 = new byte[this.digest.getDigestSize()];
        addFieldElement(this.digest, eCPointNormalize.getAffineXCoord());
        this.digest.update(bArr, i, i2);
        addFieldElement(this.digest, eCPointNormalize.getAffineYCoord());
        this.digest.doFinal(bArr3, 0);
        switch (this.mode.ordinal()) {
            case 1:
                return Arrays.concatenate(encoded, bArr3, bArr2);
            default:
                return Arrays.concatenate(encoded, bArr2, bArr3);
        }
    }

    private byte[] decrypt(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] bArr2 = new byte[(this.curveLength * 2) + 1];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        ECPoint eCPointDecodePoint = this.ecParams.getCurve().decodePoint(bArr2);
        if (eCPointDecodePoint.multiply(this.ecParams.getH()).isInfinity()) {
            throw new InvalidCipherTextException("[h]C1 at infinity");
        }
        ECPoint eCPointNormalize = eCPointDecodePoint.multiply(((ECPrivateKeyParameters) this.ecKey).getD()).normalize();
        int digestSize = this.digest.getDigestSize();
        byte[] bArr3 = new byte[(i2 - bArr2.length) - digestSize];
        if (this.mode == Mode.C1C3C2) {
            System.arraycopy(bArr, i + bArr2.length + digestSize, bArr3, 0, bArr3.length);
        } else {
            System.arraycopy(bArr, i + bArr2.length, bArr3, 0, bArr3.length);
        }
        kdf(this.digest, eCPointNormalize, bArr3);
        byte[] bArr4 = new byte[this.digest.getDigestSize()];
        addFieldElement(this.digest, eCPointNormalize.getAffineXCoord());
        this.digest.update(bArr3, 0, bArr3.length);
        addFieldElement(this.digest, eCPointNormalize.getAffineYCoord());
        this.digest.doFinal(bArr4, 0);
        int i3 = 0;
        if (this.mode == Mode.C1C3C2) {
            for (int i4 = 0; i4 != bArr4.length; i4++) {
                i3 |= bArr4[i4] ^ bArr[(i + bArr2.length) + i4];
            }
        } else {
            for (int i5 = 0; i5 != bArr4.length; i5++) {
                i3 |= bArr4[i5] ^ bArr[((i + bArr2.length) + bArr3.length) + i5];
            }
        }
        Arrays.fill(bArr2, (byte) 0);
        Arrays.fill(bArr4, (byte) 0);
        if (i3 == 0) {
            return bArr3;
        }
        Arrays.fill(bArr3, (byte) 0);
        throw new InvalidCipherTextException("invalid cipher text");
    }

    private boolean notEncrypted(byte[] bArr, byte[] bArr2, int i) {
        for (int i2 = 0; i2 != bArr.length; i2++) {
            if (bArr[i2] != bArr2[i + i2]) {
                return false;
            }
        }
        return true;
    }

    private void kdf(Digest digest, ECPoint eCPoint, byte[] bArr) {
        int digestSize = digest.getDigestSize();
        byte[] bArr2 = new byte[Math.max(4, digestSize)];
        int i = 0;
        Memoable memoable = null;
        Memoable memoableCopy = null;
        if (digest instanceof Memoable) {
            addFieldElement(digest, eCPoint.getAffineXCoord());
            addFieldElement(digest, eCPoint.getAffineYCoord());
            memoable = (Memoable) digest;
            memoableCopy = memoable.copy();
        }
        int i2 = 0;
        while (i < bArr.length) {
            if (memoable != null) {
                memoable.reset(memoableCopy);
            } else {
                addFieldElement(digest, eCPoint.getAffineXCoord());
                addFieldElement(digest, eCPoint.getAffineYCoord());
            }
            i2++;
            Pack.intToBigEndian(i2, bArr2, 0);
            digest.update(bArr2, 0, 4);
            digest.doFinal(bArr2, 0);
            int iMin = Math.min(digestSize, bArr.length - i);
            Bytes.xorTo(iMin, bArr2, 0, bArr, i);
            i += iMin;
        }
    }

    private BigInteger nextK() {
        int iBitLength = this.ecParams.getN().bitLength();
        while (true) {
            BigInteger bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(iBitLength, this.random);
            if (!bigIntegerCreateRandomBigInteger.equals(BigIntegers.ZERO) && bigIntegerCreateRandomBigInteger.compareTo(this.ecParams.getN()) < 0) {
                return bigIntegerCreateRandomBigInteger;
            }
        }
    }

    private void addFieldElement(Digest digest, ECFieldElement eCFieldElement) {
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(this.curveLength, eCFieldElement.toBigInteger());
        digest.update(bArrAsUnsignedByteArray, 0, bArrAsUnsignedByteArray.length);
    }
}
