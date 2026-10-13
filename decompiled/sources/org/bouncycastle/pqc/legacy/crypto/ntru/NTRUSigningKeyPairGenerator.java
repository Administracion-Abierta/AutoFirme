package org.bouncycastle.pqc.legacy.crypto.ntru;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.legacy.math.ntru.euclid.BigIntEuclidean;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.BigDecimalPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.BigIntPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.DenseTernaryPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.IntegerPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.ProductFormPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.Resultant;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUSigningKeyPairGenerator.class */
public class NTRUSigningKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NTRUSigningKeyGenerationParameters params;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUSigningKeyPairGenerator$BasisGenerationTask.class */
    private class BasisGenerationTask implements Callable<NTRUSigningPrivateKeyParameters.Basis> {
        private BasisGenerationTask() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public NTRUSigningPrivateKeyParameters.Basis call() throws Exception {
            return NTRUSigningKeyPairGenerator.this.generateBoundedBasis();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUSigningKeyPairGenerator$FGBasis.class */
    public static class FGBasis extends NTRUSigningPrivateKeyParameters.Basis {
        public IntegerPolynomial F;
        public IntegerPolynomial G;

        FGBasis(Polynomial polynomial, Polynomial polynomial2, IntegerPolynomial integerPolynomial, IntegerPolynomial integerPolynomial2, IntegerPolynomial integerPolynomial3, NTRUSigningKeyGenerationParameters nTRUSigningKeyGenerationParameters) {
            super(polynomial, polynomial2, integerPolynomial, nTRUSigningKeyGenerationParameters);
            this.F = integerPolynomial2;
            this.G = integerPolynomial3;
        }

        boolean isNormOk() {
            double d = this.params.keyNormBoundSq;
            int i = this.params.q;
            return ((double) this.F.centeredNormSq(i)) < d && ((double) this.G.centeredNormSq(i)) < d;
        }
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (NTRUSigningKeyGenerationParameters) keyGenerationParameters;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        NTRUSigningPublicKeyParameters nTRUSigningPublicKeyParameters = null;
        ExecutorService executorServiceNewCachedThreadPool = Executors.newCachedThreadPool();
        ArrayList arrayList = new ArrayList();
        for (int i = this.params.B; i >= 0; i--) {
            arrayList.add(executorServiceNewCachedThreadPool.submit(new BasisGenerationTask()));
        }
        executorServiceNewCachedThreadPool.shutdown();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = this.params.B; i2 >= 0; i2--) {
            Future future = (Future) arrayList.get(i2);
            try {
                arrayList2.add((NTRUSigningPrivateKeyParameters.Basis) future.get());
                if (i2 == this.params.B) {
                    nTRUSigningPublicKeyParameters = new NTRUSigningPublicKeyParameters(((NTRUSigningPrivateKeyParameters.Basis) future.get()).h, this.params.getSigningParameters());
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) nTRUSigningPublicKeyParameters, (AsymmetricKeyParameter) new NTRUSigningPrivateKeyParameters(arrayList2, nTRUSigningPublicKeyParameters));
    }

    public AsymmetricCipherKeyPair generateKeyPairSingleThread() {
        ArrayList arrayList = new ArrayList();
        NTRUSigningPublicKeyParameters nTRUSigningPublicKeyParameters = null;
        for (int i = this.params.B; i >= 0; i--) {
            NTRUSigningPrivateKeyParameters.Basis basisGenerateBoundedBasis = generateBoundedBasis();
            arrayList.add(basisGenerateBoundedBasis);
            if (i == 0) {
                nTRUSigningPublicKeyParameters = new NTRUSigningPublicKeyParameters(basisGenerateBoundedBasis.h, this.params.getSigningParameters());
            }
        }
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) nTRUSigningPublicKeyParameters, (AsymmetricKeyParameter) new NTRUSigningPrivateKeyParameters(arrayList, nTRUSigningPublicKeyParameters));
    }

    private void minimizeFG(IntegerPolynomial integerPolynomial, IntegerPolynomial integerPolynomial2, IntegerPolynomial integerPolynomial3, IntegerPolynomial integerPolynomial4, int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            i2 += 2 * i * ((integerPolynomial.coeffs[i3] * integerPolynomial.coeffs[i3]) + (integerPolynomial2.coeffs[i3] * integerPolynomial2.coeffs[i3]));
        }
        int i4 = i2 - 4;
        IntegerPolynomial integerPolynomial5 = (IntegerPolynomial) integerPolynomial.clone();
        IntegerPolynomial integerPolynomial6 = (IntegerPolynomial) integerPolynomial2.clone();
        int i5 = 0;
        int i6 = 0;
        while (i6 < i && i5 < i) {
            int i7 = 0;
            for (int i8 = 0; i8 < i; i8++) {
                i7 += 4 * i * ((integerPolynomial3.coeffs[i8] * integerPolynomial.coeffs[i8]) + (integerPolynomial4.coeffs[i8] * integerPolynomial2.coeffs[i8]));
            }
            int iSumCoeffs = i7 - (4 * (integerPolynomial3.sumCoeffs() + integerPolynomial4.sumCoeffs()));
            if (iSumCoeffs > i4) {
                integerPolynomial3.sub(integerPolynomial5);
                integerPolynomial4.sub(integerPolynomial6);
                i6++;
                i5 = 0;
            } else if (iSumCoeffs < (-i4)) {
                integerPolynomial3.add(integerPolynomial5);
                integerPolynomial4.add(integerPolynomial6);
                i6++;
                i5 = 0;
            }
            i5++;
            integerPolynomial5.rotate1();
            integerPolynomial6.rotate1();
        }
    }

    private FGBasis generateBasis() {
        Polynomial polynomialGenerateRandom;
        IntegerPolynomial integerPolynomial;
        IntegerPolynomial integerPolynomialInvertFq;
        Polynomial polynomialGenerateRandom2;
        IntegerPolynomial integerPolynomial2;
        Resultant resultant;
        BigIntEuclidean bigIntEuclideanCalculate;
        BigIntPolynomial bigIntPolynomialRound;
        Polynomial polynomial;
        IntegerPolynomial integerPolynomialMult;
        int i = this.params.N;
        int i2 = this.params.q;
        int i3 = this.params.d;
        int i4 = this.params.d1;
        int i5 = this.params.d2;
        int i6 = this.params.d3;
        int i7 = this.params.basisType;
        int i8 = (2 * i) + 1;
        boolean z = this.params.primeCheck;
        while (true) {
            polynomialGenerateRandom = this.params.polyType == 0 ? DenseTernaryPolynomial.generateRandom(i, i3 + 1, i3, CryptoServicesRegistrar.getSecureRandom()) : ProductFormPolynomial.generateRandom(i, i4, i5, i6 + 1, i6, CryptoServicesRegistrar.getSecureRandom());
            integerPolynomial = polynomialGenerateRandom.toIntegerPolynomial();
            if (!z || !integerPolynomial.resultant(i8).res.equals(BigInteger.ZERO)) {
                integerPolynomialInvertFq = integerPolynomial.invertFq(i2);
                if (integerPolynomialInvertFq != null) {
                    break;
                }
            }
        }
        Resultant resultant2 = integerPolynomial.resultant();
        while (true) {
            polynomialGenerateRandom2 = this.params.polyType == 0 ? DenseTernaryPolynomial.generateRandom(i, i3 + 1, i3, CryptoServicesRegistrar.getSecureRandom()) : ProductFormPolynomial.generateRandom(i, i4, i5, i6 + 1, i6, CryptoServicesRegistrar.getSecureRandom());
            integerPolynomial2 = polynomialGenerateRandom2.toIntegerPolynomial();
            if (!z || !integerPolynomial2.resultant(i8).res.equals(BigInteger.ZERO)) {
                if (integerPolynomial2.invertFq(i2) != null) {
                    resultant = integerPolynomial2.resultant();
                    bigIntEuclideanCalculate = BigIntEuclidean.calculate(resultant2.res, resultant.res);
                    if (bigIntEuclideanCalculate.gcd.equals(BigInteger.ONE)) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        BigIntPolynomial bigIntPolynomial = (BigIntPolynomial) resultant2.rho.clone();
        bigIntPolynomial.mult(bigIntEuclideanCalculate.x.multiply(BigInteger.valueOf(i2)));
        BigIntPolynomial bigIntPolynomial2 = (BigIntPolynomial) resultant.rho.clone();
        bigIntPolynomial2.mult(bigIntEuclideanCalculate.y.multiply(BigInteger.valueOf(-i2)));
        if (this.params.keyGenAlg == 0) {
            int[] iArr = new int[i];
            int[] iArr2 = new int[i];
            iArr[0] = integerPolynomial.coeffs[0];
            iArr2[0] = integerPolynomial2.coeffs[0];
            for (int i9 = 1; i9 < i; i9++) {
                iArr[i9] = integerPolynomial.coeffs[i - i9];
                iArr2[i9] = integerPolynomial2.coeffs[i - i9];
            }
            IntegerPolynomial integerPolynomial3 = new IntegerPolynomial(iArr);
            IntegerPolynomial integerPolynomial4 = new IntegerPolynomial(iArr2);
            IntegerPolynomial integerPolynomialMult2 = polynomialGenerateRandom.mult(integerPolynomial3);
            integerPolynomialMult2.add(polynomialGenerateRandom2.mult(integerPolynomial4));
            Resultant resultant3 = integerPolynomialMult2.resultant();
            BigIntPolynomial bigIntPolynomialMult = integerPolynomial3.mult(bigIntPolynomial2);
            bigIntPolynomialMult.add(integerPolynomial4.mult(bigIntPolynomial));
            bigIntPolynomialRound = bigIntPolynomialMult.mult(resultant3.rho);
            bigIntPolynomialRound.div(resultant3.res);
        } else {
            int i10 = 0;
            int i11 = 1;
            while (true) {
                int i12 = i11;
                if (i12 >= i) {
                    break;
                }
                i10++;
                i11 = i12 * 10;
            }
            BigDecimalPolynomial bigDecimalPolynomialDiv = resultant2.rho.div(new BigDecimal(resultant2.res), bigIntPolynomial2.getMaxCoeffLength() + 1 + i10);
            BigDecimalPolynomial bigDecimalPolynomialDiv2 = resultant.rho.div(new BigDecimal(resultant.res), bigIntPolynomial.getMaxCoeffLength() + 1 + i10);
            BigDecimalPolynomial bigDecimalPolynomialMult = bigDecimalPolynomialDiv.mult(bigIntPolynomial2);
            bigDecimalPolynomialMult.add(bigDecimalPolynomialDiv2.mult(bigIntPolynomial));
            bigDecimalPolynomialMult.halve();
            bigIntPolynomialRound = bigDecimalPolynomialMult.round();
        }
        BigIntPolynomial bigIntPolynomial3 = (BigIntPolynomial) bigIntPolynomial2.clone();
        bigIntPolynomial3.sub(polynomialGenerateRandom.mult(bigIntPolynomialRound));
        BigIntPolynomial bigIntPolynomial4 = (BigIntPolynomial) bigIntPolynomial.clone();
        bigIntPolynomial4.sub(polynomialGenerateRandom2.mult(bigIntPolynomialRound));
        IntegerPolynomial integerPolynomial5 = new IntegerPolynomial(bigIntPolynomial3);
        IntegerPolynomial integerPolynomial6 = new IntegerPolynomial(bigIntPolynomial4);
        minimizeFG(integerPolynomial, integerPolynomial2, integerPolynomial5, integerPolynomial6, i);
        if (i7 == 0) {
            polynomial = integerPolynomial5;
            integerPolynomialMult = polynomialGenerateRandom2.mult(integerPolynomialInvertFq, i2);
        } else {
            polynomial = polynomialGenerateRandom2;
            integerPolynomialMult = integerPolynomial5.mult(integerPolynomialInvertFq, i2);
        }
        integerPolynomialMult.modPositive(i2);
        return new FGBasis(polynomialGenerateRandom, polynomial, integerPolynomialMult, integerPolynomial5, integerPolynomial6, this.params);
    }

    public NTRUSigningPrivateKeyParameters.Basis generateBoundedBasis() {
        FGBasis fGBasisGenerateBasis;
        do {
            fGBasisGenerateBasis = generateBasis();
        } while (!fGBasisGenerateBasis.isNormOk());
        return fGBasisGenerateBasis;
    }
}
