package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/Polynomial.class */
public abstract class Polynomial {
    public short[] coeffs;
    protected NTRUParameterSet params;

    public Polynomial(NTRUParameterSet nTRUParameterSet) {
        this.coeffs = new short[nTRUParameterSet.n()];
        this.params = nTRUParameterSet;
    }

    static short bothNegativeMask(short s, short s2) {
        return (short) ((s & s2) >>> 15);
    }

    static short mod3(short s) {
        return (short) ((s & 65535) % 3);
    }

    static byte mod3(byte b) {
        return (byte) ((b & 255) % 3);
    }

    static int modQ(int i, int i2) {
        return i % i2;
    }

    public void mod3PhiN() {
        int iN = this.params.n();
        for (int i = 0; i < iN; i++) {
            this.coeffs[i] = mod3((short) (this.coeffs[i] + (2 * this.coeffs[iN - 1])));
        }
    }

    public void modQPhiN() {
        int iN = this.params.n();
        for (int i = 0; i < iN; i++) {
            this.coeffs[i] = (short) (this.coeffs[i] - this.coeffs[iN - 1]);
        }
    }

    public abstract byte[] sqToBytes(int i);

    public abstract void sqFromBytes(byte[] bArr);

    public byte[] rqSumZeroToBytes(int i) {
        return sqToBytes(i);
    }

    public void rqSumZeroFromBytes(byte[] bArr) {
        int length = this.coeffs.length;
        sqFromBytes(bArr);
        this.coeffs[length - 1] = 0;
        for (int i = 0; i < this.params.packDegree(); i++) {
            short[] sArr = this.coeffs;
            int i2 = length - 1;
            sArr[i2] = (short) (sArr[i2] - this.coeffs[i]);
        }
    }

    public byte[] s3ToBytes(int i) {
        byte[] bArr = new byte[i];
        s3ToBytes(bArr, 0);
        return bArr;
    }

    public void s3ToBytes(byte[] bArr, int i) {
        int iPackDegree = this.params.packDegree();
        int i2 = iPackDegree - 5;
        int i3 = 0;
        while (i3 <= i2) {
            int i4 = this.coeffs[i3 + 0] & 255;
            int i5 = (this.coeffs[i3 + 1] & 255) * 3;
            int i6 = (this.coeffs[i3 + 2] & 255) * 9;
            int i7 = i;
            i++;
            bArr[i7] = (byte) (i4 + i5 + i6 + ((this.coeffs[i3 + 3] & 255) * 27) + ((this.coeffs[i3 + 4] & 255) * 81));
            i3 += 5;
        }
        if (i3 >= iPackDegree) {
            return;
        }
        int i8 = iPackDegree - 1;
        int i9 = this.coeffs[i8] & 255;
        while (true) {
            int i10 = i9;
            i8--;
            if (i8 < i3) {
                int i11 = i;
                int i12 = i + 1;
                bArr[i11] = (byte) i10;
                return;
            }
            i9 = (i10 * 3) + (this.coeffs[i8] & 255);
        }
    }

    public void s3FromBytes(byte[] bArr) {
        int length = this.coeffs.length;
        for (int i = 0; i < this.params.packDegree() / 5; i++) {
            byte b = bArr[i];
            this.coeffs[(5 * i) + 0] = b;
            this.coeffs[(5 * i) + 1] = (short) (((b & 255) * 171) >>> 9);
            this.coeffs[(5 * i) + 2] = (short) (((b & 255) * 57) >>> 9);
            this.coeffs[(5 * i) + 3] = (short) (((b & 255) * 19) >>> 9);
            this.coeffs[(5 * i) + 4] = (short) (((b & 255) * 203) >>> 14);
        }
        if (this.params.packDegree() > (this.params.packDegree() / 5) * 5) {
            int iPackDegree = this.params.packDegree() / 5;
            byte b2 = bArr[iPackDegree];
            for (int i2 = 0; (5 * iPackDegree) + i2 < this.params.packDegree(); i2++) {
                this.coeffs[(5 * iPackDegree) + i2] = b2;
                b2 = (byte) (((b2 & 255) * 171) >> 9);
            }
        }
        this.coeffs[length - 1] = 0;
        mod3PhiN();
    }

    public void sqMul(Polynomial polynomial, Polynomial polynomial2) {
        rqMul(polynomial, polynomial2);
        modQPhiN();
    }

    public void rqMul(Polynomial polynomial, Polynomial polynomial2) {
        int length = this.coeffs.length;
        for (int i = 0; i < length; i++) {
            this.coeffs[i] = 0;
            for (int i2 = 1; i2 < length - i; i2++) {
                short[] sArr = this.coeffs;
                int i3 = i;
                sArr[i3] = (short) (sArr[i3] + (polynomial.coeffs[i + i2] * polynomial2.coeffs[length - i2]));
            }
            for (int i4 = 0; i4 < i + 1; i4++) {
                short[] sArr2 = this.coeffs;
                int i5 = i;
                sArr2[i5] = (short) (sArr2[i5] + (polynomial.coeffs[i - i4] * polynomial2.coeffs[i4]));
            }
        }
    }

    public void s3Mul(Polynomial polynomial, Polynomial polynomial2) {
        rqMul(polynomial, polynomial2);
        mod3PhiN();
    }

    public abstract void lift(Polynomial polynomial);

    public void rqToS3(Polynomial polynomial) {
        int length = this.coeffs.length;
        for (int i = 0; i < length; i++) {
            this.coeffs[i] = (short) modQ(polynomial.coeffs[i] & 65535, this.params.q());
            short sLogQ = (short) (this.coeffs[i] >>> (this.params.logQ() - 1));
            short[] sArr = this.coeffs;
            int i2 = i;
            sArr[i2] = (short) (sArr[i2] + (sLogQ << (1 - (this.params.logQ() & 1))));
        }
        mod3PhiN();
    }

    public void r2Inv(Polynomial polynomial) {
        r2Inv(polynomial, this.params.createPolynomial(), this.params.createPolynomial(), this.params.createPolynomial(), this.params.createPolynomial());
    }

    public void rqInv(Polynomial polynomial) {
        rqInv(polynomial, this.params.createPolynomial(), this.params.createPolynomial(), this.params.createPolynomial(), this.params.createPolynomial());
    }

    public void s3Inv(Polynomial polynomial) {
        s3Inv(polynomial, this.params.createPolynomial(), this.params.createPolynomial(), this.params.createPolynomial(), this.params.createPolynomial());
    }

    void r2Inv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        int length = this.coeffs.length;
        polynomial5.coeffs[0] = 1;
        for (int i = 0; i < length; i++) {
            polynomial2.coeffs[i] = 1;
        }
        for (int i2 = 0; i2 < length - 1; i2++) {
            polynomial3.coeffs[(length - 2) - i2] = (short) ((polynomial.coeffs[i2] ^ polynomial.coeffs[length - 1]) & 1);
        }
        polynomial3.coeffs[length - 1] = 0;
        short s = 1;
        for (int i3 = 0; i3 < (2 * (length - 1)) - 1; i3++) {
            for (int i4 = length - 1; i4 > 0; i4--) {
                polynomial4.coeffs[i4] = polynomial4.coeffs[i4 - 1];
            }
            polynomial4.coeffs[0] = 0;
            short s2 = (short) (polynomial3.coeffs[0] & polynomial2.coeffs[0]);
            short sBothNegativeMask = bothNegativeMask((short) (-s), (short) (-polynomial3.coeffs[0]));
            s = (short) (((short) (s ^ (sBothNegativeMask & (s ^ (-s))))) + 1);
            for (int i5 = 0; i5 < length; i5++) {
                short s3 = (short) (sBothNegativeMask & (polynomial2.coeffs[i5] ^ polynomial3.coeffs[i5]));
                short[] sArr = polynomial2.coeffs;
                int i6 = i5;
                sArr[i6] = (short) (sArr[i6] ^ s3);
                short[] sArr2 = polynomial3.coeffs;
                int i7 = i5;
                sArr2[i7] = (short) (sArr2[i7] ^ s3);
                short s4 = (short) (sBothNegativeMask & (polynomial4.coeffs[i5] ^ polynomial5.coeffs[i5]));
                short[] sArr3 = polynomial4.coeffs;
                int i8 = i5;
                sArr3[i8] = (short) (sArr3[i8] ^ s4);
                short[] sArr4 = polynomial5.coeffs;
                int i9 = i5;
                sArr4[i9] = (short) (sArr4[i9] ^ s4);
            }
            for (int i10 = 0; i10 < length; i10++) {
                polynomial3.coeffs[i10] = (short) (polynomial3.coeffs[i10] ^ (s2 & polynomial2.coeffs[i10]));
            }
            for (int i11 = 0; i11 < length; i11++) {
                polynomial5.coeffs[i11] = (short) (polynomial5.coeffs[i11] ^ (s2 & polynomial4.coeffs[i11]));
            }
            for (int i12 = 0; i12 < length - 1; i12++) {
                polynomial3.coeffs[i12] = polynomial3.coeffs[i12 + 1];
            }
            polynomial3.coeffs[length - 1] = 0;
        }
        for (int i13 = 0; i13 < length - 1; i13++) {
            this.coeffs[i13] = polynomial4.coeffs[(length - 2) - i13];
        }
        this.coeffs[length - 1] = 0;
    }

    void rqInv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        polynomial2.r2Inv(polynomial);
        r2InvToRqInv(polynomial2, polynomial, polynomial3, polynomial4, polynomial5);
    }

    private void r2InvToRqInv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        int length = this.coeffs.length;
        for (int i = 0; i < length; i++) {
            polynomial3.coeffs[i] = (short) (-polynomial2.coeffs[i]);
        }
        for (int i2 = 0; i2 < length; i2++) {
            this.coeffs[i2] = polynomial.coeffs[i2];
        }
        polynomial4.rqMul(this, polynomial3);
        short[] sArr = polynomial4.coeffs;
        sArr[0] = (short) (sArr[0] + 2);
        polynomial5.rqMul(polynomial4, this);
        polynomial4.rqMul(polynomial5, polynomial3);
        short[] sArr2 = polynomial4.coeffs;
        sArr2[0] = (short) (sArr2[0] + 2);
        rqMul(polynomial4, polynomial5);
        polynomial4.rqMul(this, polynomial3);
        short[] sArr3 = polynomial4.coeffs;
        sArr3[0] = (short) (sArr3[0] + 2);
        polynomial5.rqMul(polynomial4, this);
        polynomial4.rqMul(polynomial5, polynomial3);
        short[] sArr4 = polynomial4.coeffs;
        sArr4[0] = (short) (sArr4[0] + 2);
        rqMul(polynomial4, polynomial5);
    }

    void s3Inv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        int length = this.coeffs.length;
        polynomial5.coeffs[0] = 1;
        for (int i = 0; i < length; i++) {
            polynomial2.coeffs[i] = 1;
        }
        for (int i2 = 0; i2 < length - 1; i2++) {
            polynomial3.coeffs[(length - 2) - i2] = mod3((short) ((polynomial.coeffs[i2] & 3) + (2 * (polynomial.coeffs[length - 1] & 3))));
        }
        polynomial3.coeffs[length - 1] = 0;
        short s = 1;
        for (int i3 = 0; i3 < (2 * (length - 1)) - 1; i3++) {
            for (int i4 = length - 1; i4 > 0; i4--) {
                polynomial4.coeffs[i4] = polynomial4.coeffs[i4 - 1];
            }
            polynomial4.coeffs[0] = 0;
            short sMod3 = mod3((byte) (2 * polynomial3.coeffs[0] * polynomial2.coeffs[0]));
            short sBothNegativeMask = bothNegativeMask((short) (-s), (short) (-polynomial3.coeffs[0]));
            s = (short) (((short) (s ^ (sBothNegativeMask & (s ^ (-s))))) + 1);
            for (int i5 = 0; i5 < length; i5++) {
                short s2 = (short) (sBothNegativeMask & (polynomial2.coeffs[i5] ^ polynomial3.coeffs[i5]));
                short[] sArr = polynomial2.coeffs;
                int i6 = i5;
                sArr[i6] = (short) (sArr[i6] ^ s2);
                short[] sArr2 = polynomial3.coeffs;
                int i7 = i5;
                sArr2[i7] = (short) (sArr2[i7] ^ s2);
                short s3 = (short) (sBothNegativeMask & (polynomial4.coeffs[i5] ^ polynomial5.coeffs[i5]));
                short[] sArr3 = polynomial4.coeffs;
                int i8 = i5;
                sArr3[i8] = (short) (sArr3[i8] ^ s3);
                short[] sArr4 = polynomial5.coeffs;
                int i9 = i5;
                sArr4[i9] = (short) (sArr4[i9] ^ s3);
            }
            for (int i10 = 0; i10 < length; i10++) {
                polynomial3.coeffs[i10] = mod3((byte) (polynomial3.coeffs[i10] + (sMod3 * polynomial2.coeffs[i10])));
            }
            for (int i11 = 0; i11 < length; i11++) {
                polynomial5.coeffs[i11] = mod3((byte) (polynomial5.coeffs[i11] + (sMod3 * polynomial4.coeffs[i11])));
            }
            for (int i12 = 0; i12 < length - 1; i12++) {
                polynomial3.coeffs[i12] = polynomial3.coeffs[i12 + 1];
            }
            polynomial3.coeffs[length - 1] = 0;
        }
        short s4 = polynomial2.coeffs[0];
        for (int i13 = 0; i13 < length - 1; i13++) {
            this.coeffs[i13] = mod3((byte) (s4 * polynomial4.coeffs[(length - 2) - i13]));
        }
        this.coeffs[length - 1] = 0;
    }

    public void z3ToZq() {
        int length = this.coeffs.length;
        for (int i = 0; i < length; i++) {
            this.coeffs[i] = (short) (this.coeffs[i] | ((-(this.coeffs[i] >>> 1)) & (this.params.q() - 1)));
        }
    }

    public void trinaryZqToZ3() {
        int length = this.coeffs.length;
        for (int i = 0; i < length; i++) {
            this.coeffs[i] = (short) modQ(this.coeffs[i] & 65535, this.params.q());
            this.coeffs[i] = (short) (3 & (this.coeffs[i] ^ (this.coeffs[i] >>> (this.params.logQ() - 1))));
        }
    }
}
