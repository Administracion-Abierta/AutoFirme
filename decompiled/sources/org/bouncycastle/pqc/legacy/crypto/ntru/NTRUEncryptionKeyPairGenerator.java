package org.bouncycastle.pqc.legacy.crypto.ntru;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.DenseTernaryPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.IntegerPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.ProductFormPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.util.Util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUEncryptionKeyPairGenerator.class */
public class NTRUEncryptionKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NTRUEncryptionKeyGenerationParameters params;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (NTRUEncryptionKeyGenerationParameters) keyGenerationParameters;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        Polynomial polynomialGenerateRandomTernary;
        IntegerPolynomial integerPolynomial;
        IntegerPolynomial integerPolynomialInvertFq;
        DenseTernaryPolynomial denseTernaryPolynomialGenerateRandom;
        int i = this.params.N;
        int i2 = this.params.q;
        int i3 = this.params.df;
        int i4 = this.params.df1;
        int i5 = this.params.df2;
        int i6 = this.params.df3;
        int i7 = this.params.dg;
        boolean z = this.params.fastFp;
        boolean z2 = this.params.sparse;
        IntegerPolynomial integerPolynomial2 = null;
        while (true) {
            if (z) {
                polynomialGenerateRandomTernary = this.params.polyType == 0 ? Util.generateRandomTernary(i, i3, i3, z2, this.params.getRandom()) : ProductFormPolynomial.generateRandom(i, i4, i5, i6, i6, this.params.getRandom());
                integerPolynomial = polynomialGenerateRandomTernary.toIntegerPolynomial();
                integerPolynomial.mult(3);
                int[] iArr = integerPolynomial.coeffs;
                iArr[0] = iArr[0] + 1;
            } else {
                polynomialGenerateRandomTernary = this.params.polyType == 0 ? Util.generateRandomTernary(i, i3, i3 - 1, z2, this.params.getRandom()) : ProductFormPolynomial.generateRandom(i, i4, i5, i6, i6 - 1, this.params.getRandom());
                integerPolynomial = polynomialGenerateRandomTernary.toIntegerPolynomial();
                integerPolynomial2 = integerPolynomial.invertF3();
                if (integerPolynomial2 == null) {
                    continue;
                }
            }
            integerPolynomialInvertFq = integerPolynomial.invertFq(i2);
            if (integerPolynomialInvertFq != null) {
                break;
            }
        }
        if (z) {
            integerPolynomial2 = new IntegerPolynomial(i);
            integerPolynomial2.coeffs[0] = 1;
        }
        do {
            denseTernaryPolynomialGenerateRandom = DenseTernaryPolynomial.generateRandom(i, i7, i7 - 1, this.params.getRandom());
        } while (denseTernaryPolynomialGenerateRandom.invertFq(i2) == null);
        IntegerPolynomial integerPolynomialMult = denseTernaryPolynomialGenerateRandom.mult(integerPolynomialInvertFq, i2);
        integerPolynomialMult.mult3(i2);
        integerPolynomialMult.ensurePositive(i2);
        denseTernaryPolynomialGenerateRandom.clear();
        integerPolynomialInvertFq.clear();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new NTRUEncryptionPublicKeyParameters(integerPolynomialMult, this.params.getEncryptionParameters()), (AsymmetricKeyParameter) new NTRUEncryptionPrivateKeyParameters(integerPolynomialMult, polynomialGenerateRandomTernary, integerPolynomial2, this.params.getEncryptionParameters()));
    }
}
