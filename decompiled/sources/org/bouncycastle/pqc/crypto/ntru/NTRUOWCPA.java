package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUOWCPA.class */
class NTRUOWCPA {
    private final NTRUParameterSet params;
    private final NTRUSampling sampling;

    public NTRUOWCPA(NTRUParameterSet nTRUParameterSet) {
        this.params = nTRUParameterSet;
        this.sampling = new NTRUSampling(nTRUParameterSet);
    }

    public OWCPAKeyPair keypair(byte[] bArr) {
        byte[] bArr2 = new byte[this.params.owcpaSecretKeyBytes()];
        int iN = this.params.n();
        this.params.q();
        Polynomial polynomialCreatePolynomial = this.params.createPolynomial();
        Polynomial polynomialCreatePolynomial2 = this.params.createPolynomial();
        Polynomial polynomialCreatePolynomial3 = this.params.createPolynomial();
        PolynomialPair polynomialPairSampleFg = this.sampling.sampleFg(bArr);
        Polynomial polynomialF = polynomialPairSampleFg.f();
        Polynomial polynomialG = polynomialPairSampleFg.g();
        polynomialCreatePolynomial.s3Inv(polynomialF);
        polynomialF.s3ToBytes(bArr2, 0);
        polynomialCreatePolynomial.s3ToBytes(bArr2, this.params.packTrinaryBytes());
        polynomialF.z3ToZq();
        polynomialG.z3ToZq();
        if (this.params instanceof NTRUHRSSParameterSet) {
            for (int i = iN - 1; i > 0; i--) {
                polynomialG.coeffs[i] = (short) (3 * (polynomialG.coeffs[i - 1] - polynomialG.coeffs[i]));
            }
            polynomialG.coeffs[0] = (short) (-(3 * polynomialG.coeffs[0]));
        } else {
            for (int i2 = 0; i2 < iN; i2++) {
                polynomialG.coeffs[i2] = (short) (3 * polynomialG.coeffs[i2]);
            }
        }
        polynomialCreatePolynomial.rqMul(polynomialG, polynomialF);
        polynomialCreatePolynomial2.rqInv(polynomialCreatePolynomial);
        polynomialCreatePolynomial3.rqMul(polynomialCreatePolynomial2, polynomialF);
        polynomialCreatePolynomial.sqMul(polynomialCreatePolynomial3, polynomialF);
        byte[] bArrSqToBytes = polynomialCreatePolynomial.sqToBytes(bArr2.length - (2 * this.params.packTrinaryBytes()));
        System.arraycopy(bArrSqToBytes, 0, bArr2, 2 * this.params.packTrinaryBytes(), bArrSqToBytes.length);
        polynomialCreatePolynomial3.rqMul(polynomialCreatePolynomial2, polynomialG);
        polynomialCreatePolynomial.rqMul(polynomialCreatePolynomial3, polynomialG);
        return new OWCPAKeyPair(polynomialCreatePolynomial.rqSumZeroToBytes(this.params.owcpaPublicKeyBytes()), bArr2);
    }

    public byte[] encrypt(Polynomial polynomial, Polynomial polynomial2, byte[] bArr) {
        Polynomial polynomialCreatePolynomial = this.params.createPolynomial();
        Polynomial polynomialCreatePolynomial2 = this.params.createPolynomial();
        polynomialCreatePolynomial.rqSumZeroFromBytes(bArr);
        polynomialCreatePolynomial2.rqMul(polynomial, polynomialCreatePolynomial);
        polynomialCreatePolynomial.lift(polynomial2);
        for (int i = 0; i < this.params.n(); i++) {
            short[] sArr = polynomialCreatePolynomial2.coeffs;
            int i2 = i;
            sArr[i2] = (short) (sArr[i2] + polynomialCreatePolynomial.coeffs[i]);
        }
        return polynomialCreatePolynomial2.rqSumZeroToBytes(this.params.ntruCiphertextBytes());
    }

    public OWCPADecryptResult decrypt(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[this.params.owcpaMsgBytes()];
        Polynomial polynomialCreatePolynomial = this.params.createPolynomial();
        Polynomial polynomialCreatePolynomial2 = this.params.createPolynomial();
        Polynomial polynomialCreatePolynomial3 = this.params.createPolynomial();
        Polynomial polynomialCreatePolynomial4 = this.params.createPolynomial();
        polynomialCreatePolynomial.rqSumZeroFromBytes(bArr);
        polynomialCreatePolynomial2.s3FromBytes(bArr2);
        polynomialCreatePolynomial2.z3ToZq();
        polynomialCreatePolynomial3.rqMul(polynomialCreatePolynomial, polynomialCreatePolynomial2);
        polynomialCreatePolynomial2.rqToS3(polynomialCreatePolynomial3);
        polynomialCreatePolynomial3.s3FromBytes(Arrays.copyOfRange(bArr2, this.params.packTrinaryBytes(), bArr2.length));
        polynomialCreatePolynomial4.s3Mul(polynomialCreatePolynomial2, polynomialCreatePolynomial3);
        polynomialCreatePolynomial4.s3ToBytes(bArr3, this.params.packTrinaryBytes());
        int iCheckCiphertext = 0 | checkCiphertext(bArr);
        if (this.params instanceof NTRUHPSParameterSet) {
            iCheckCiphertext |= checkM((HPSPolynomial) polynomialCreatePolynomial4);
        }
        polynomialCreatePolynomial2.lift(polynomialCreatePolynomial4);
        for (int i = 0; i < this.params.n(); i++) {
            polynomialCreatePolynomial.coeffs[i] = (short) (polynomialCreatePolynomial.coeffs[i] - polynomialCreatePolynomial2.coeffs[i]);
        }
        polynomialCreatePolynomial3.sqFromBytes(Arrays.copyOfRange(bArr2, 2 * this.params.packTrinaryBytes(), bArr2.length));
        polynomialCreatePolynomial4.sqMul(polynomialCreatePolynomial, polynomialCreatePolynomial3);
        int iCheckR = iCheckCiphertext | checkR(polynomialCreatePolynomial4);
        polynomialCreatePolynomial4.trinaryZqToZ3();
        polynomialCreatePolynomial4.s3ToBytes(bArr3, 0);
        return new OWCPADecryptResult(bArr3, iCheckR);
    }

    private int checkCiphertext(byte[] bArr) {
        return 1 & (((((short) (bArr[this.params.ntruCiphertextBytes() - 1] & (255 << (8 - (7 & (this.params.logQ() * this.params.packDegree())))))) ^ (-1)) + 1) >>> 15);
    }

    private int checkR(Polynomial polynomial) {
        int iQ = 0;
        for (int i = 0; i < this.params.n() - 1; i++) {
            short s = polynomial.coeffs[i];
            iQ = iQ | ((s + 1) & (this.params.q() - 4)) | ((s + 2) & 4);
        }
        return 1 & ((((iQ | polynomial.coeffs[this.params.n() - 1]) ^ (-1)) + 1) >>> 31);
    }

    private int checkM(HPSPolynomial hPSPolynomial) {
        short s = 0;
        short s2 = 0;
        for (int i = 0; i < this.params.n() - 1; i++) {
            s = (short) (s + (hPSPolynomial.coeffs[i] & 1));
            s2 = (short) (s2 + (hPSPolynomial.coeffs[i] & 2));
        }
        return 1 & (((((0 | (s ^ (s2 >>> 1))) | (s2 ^ ((NTRUHPSParameterSet) this.params).weight())) ^ (-1)) + 1) >>> 31);
    }
}
