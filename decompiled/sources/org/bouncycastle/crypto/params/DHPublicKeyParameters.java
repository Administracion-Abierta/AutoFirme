package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/DHPublicKeyParameters.class */
public class DHPublicKeyParameters extends DHKeyParameters {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private BigInteger y;

    public DHPublicKeyParameters(BigInteger bigInteger, DHParameters dHParameters) {
        super(false, dHParameters);
        this.y = validate(bigInteger, dHParameters);
    }

    private BigInteger validate(BigInteger bigInteger, DHParameters dHParameters) {
        if (bigInteger == null) {
            throw new NullPointerException("y value cannot be null");
        }
        BigInteger p = dHParameters.getP();
        if (bigInteger.compareTo(TWO) < 0 || bigInteger.compareTo(p.subtract(TWO)) > 0) {
            throw new IllegalArgumentException("invalid DH public key");
        }
        BigInteger q = dHParameters.getQ();
        if (q == null) {
            return bigInteger;
        }
        if (p.testBit(0) && p.bitLength() - 1 == q.bitLength() && p.shiftRight(1).equals(q)) {
            if (1 == legendre(bigInteger, p)) {
                return bigInteger;
            }
        } else if (ONE.equals(bigInteger.modPow(q, p))) {
            return bigInteger;
        }
        throw new IllegalArgumentException("Y value does not appear to be in correct group");
    }

    public BigInteger getY() {
        return this.y;
    }

    @Override // org.bouncycastle.crypto.params.DHKeyParameters
    public int hashCode() {
        return this.y.hashCode() ^ super.hashCode();
    }

    @Override // org.bouncycastle.crypto.params.DHKeyParameters
    public boolean equals(Object obj) {
        return (obj instanceof DHPublicKeyParameters) && ((DHPublicKeyParameters) obj).getY().equals(this.y) && super.equals(obj);
    }

    private static int legendre(BigInteger bigInteger, BigInteger bigInteger2) {
        int iBitLength = bigInteger2.bitLength();
        int[] iArrFromBigInteger = Nat.fromBigInteger(iBitLength, bigInteger);
        int[] iArrFromBigInteger2 = Nat.fromBigInteger(iBitLength, bigInteger2);
        int i = 0;
        int length = iArrFromBigInteger2.length;
        while (true) {
            if (iArrFromBigInteger[0] == 0) {
                Nat.shiftDownWord(length, iArrFromBigInteger, 0);
            } else {
                int iNumberOfTrailingZeros = Integers.numberOfTrailingZeros(iArrFromBigInteger[0]);
                if (iNumberOfTrailingZeros > 0) {
                    Nat.shiftDownBits(length, iArrFromBigInteger, iNumberOfTrailingZeros, 0);
                    int i2 = iArrFromBigInteger2[0];
                    i ^= (i2 ^ (i2 >>> 1)) & (iNumberOfTrailingZeros << 1);
                }
                int iCompare = Nat.compare(length, iArrFromBigInteger, iArrFromBigInteger2);
                if (iCompare == 0) {
                    break;
                }
                if (iCompare < 0) {
                    i ^= iArrFromBigInteger[0] & iArrFromBigInteger2[0];
                    int[] iArr = iArrFromBigInteger;
                    iArrFromBigInteger = iArrFromBigInteger2;
                    iArrFromBigInteger2 = iArr;
                }
                while (iArrFromBigInteger[length - 1] == 0) {
                    length--;
                }
                Nat.sub(length, iArrFromBigInteger, iArrFromBigInteger2, iArrFromBigInteger);
            }
        }
        if (Nat.isOne(length, iArrFromBigInteger2)) {
            return 1 - (i & 2);
        }
        return 0;
    }
}
