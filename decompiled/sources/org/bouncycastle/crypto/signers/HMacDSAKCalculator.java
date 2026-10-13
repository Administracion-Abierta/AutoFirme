package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/HMacDSAKCalculator.class */
public class HMacDSAKCalculator implements DSAKCalculator {
    private final HMac hMac;
    private final byte[] K;
    private final byte[] V;
    private BigInteger n;

    public HMacDSAKCalculator(Digest digest) {
        this.hMac = new HMac(digest);
        int macSize = this.hMac.getMacSize();
        this.V = new byte[macSize];
        this.K = new byte[macSize];
    }

    @Override // org.bouncycastle.crypto.signers.DSAKCalculator
    public boolean isDeterministic() {
        return true;
    }

    @Override // org.bouncycastle.crypto.signers.DSAKCalculator
    public void init(BigInteger bigInteger, SecureRandom secureRandom) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override // org.bouncycastle.crypto.signers.DSAKCalculator
    public void init(BigInteger bigInteger, BigInteger bigInteger2, byte[] bArr) {
        this.n = bigInteger;
        BigInteger bigIntegerBitsToInt = bitsToInt(bArr);
        if (bigIntegerBitsToInt.compareTo(bigInteger) >= 0) {
            bigIntegerBitsToInt = bigIntegerBitsToInt.subtract(bigInteger);
        }
        int unsignedByteLength = BigIntegers.getUnsignedByteLength(bigInteger);
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(unsignedByteLength, bigInteger2);
        byte[] bArrAsUnsignedByteArray2 = BigIntegers.asUnsignedByteArray(unsignedByteLength, bigIntegerBitsToInt);
        Arrays.fill(this.K, (byte) 0);
        Arrays.fill(this.V, (byte) 1);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte) 0);
        this.hMac.update(bArrAsUnsignedByteArray, 0, bArrAsUnsignedByteArray.length);
        this.hMac.update(bArrAsUnsignedByteArray2, 0, bArrAsUnsignedByteArray2.length);
        initAdditionalInput0(this.hMac);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte) 1);
        this.hMac.update(bArrAsUnsignedByteArray, 0, bArrAsUnsignedByteArray.length);
        this.hMac.update(bArrAsUnsignedByteArray2, 0, bArrAsUnsignedByteArray2.length);
        initAdditionalInput1(this.hMac);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
    }

    @Override // org.bouncycastle.crypto.signers.DSAKCalculator
    public BigInteger nextK() {
        byte[] bArr = new byte[BigIntegers.getUnsignedByteLength(this.n)];
        while (true) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= bArr.length) {
                    break;
                }
                this.hMac.update(this.V, 0, this.V.length);
                this.hMac.doFinal(this.V, 0);
                int iMin = Math.min(bArr.length - i2, this.V.length);
                System.arraycopy(this.V, 0, bArr, i2, iMin);
                i = i2 + iMin;
            }
            BigInteger bigIntegerBitsToInt = bitsToInt(bArr);
            if (bigIntegerBitsToInt.signum() > 0 && bigIntegerBitsToInt.compareTo(this.n) < 0) {
                return bigIntegerBitsToInt;
            }
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.update((byte) 0);
            this.hMac.doFinal(this.K, 0);
            this.hMac.init(new KeyParameter(this.K));
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.doFinal(this.V, 0);
        }
    }

    protected void initAdditionalInput0(HMac hMac) {
    }

    protected void initAdditionalInput1(HMac hMac) {
    }

    private BigInteger bitsToInt(byte[] bArr) {
        int length = bArr.length * 8;
        int iBitLength = this.n.bitLength();
        BigInteger bigIntegerFromUnsignedByteArray = BigIntegers.fromUnsignedByteArray(bArr);
        if (length > iBitLength) {
            bigIntegerFromUnsignedByteArray = bigIntegerFromUnsignedByteArray.shiftRight(length - iBitLength);
        }
        return bigIntegerFromUnsignedByteArray;
    }
}
