package org.spongycastle.pqc.crypto.ntru;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.math.ntru.polynomial.DenseTernaryPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.Polynomial;
import org.spongycastle.pqc.math.ntru.polynomial.ProductFormPolynomial;
import org.spongycastle.pqc.math.ntru.util.Util;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/ntru/NTRUEncryptionKeyPairGenerator.class */
public class NTRUEncryptionKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NTRUEncryptionKeyGenerationParameters params;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.params = (NTRUEncryptionKeyGenerationParameters) param;
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        Polynomial t;
        IntegerPolynomial f;
        IntegerPolynomial fq;
        DenseTernaryPolynomial g;
        int N = this.params.N;
        int q = this.params.q;
        int df = this.params.df;
        int df1 = this.params.df1;
        int df2 = this.params.df2;
        int df3 = this.params.df3;
        int dg = this.params.dg;
        boolean fastFp = this.params.fastFp;
        boolean sparse = this.params.sparse;
        IntegerPolynomial fp = null;
        while (true) {
            if (fastFp) {
                t = this.params.polyType == 0 ? Util.generateRandomTernary(N, df, df, sparse, this.params.getRandom()) : ProductFormPolynomial.generateRandom(N, df1, df2, df3, df3, this.params.getRandom());
                f = t.toIntegerPolynomial();
                f.mult(3);
                int[] iArr = f.coeffs;
                iArr[0] = iArr[0] + 1;
            } else {
                t = this.params.polyType == 0 ? Util.generateRandomTernary(N, df, df - 1, sparse, this.params.getRandom()) : ProductFormPolynomial.generateRandom(N, df1, df2, df3, df3 - 1, this.params.getRandom());
                f = t.toIntegerPolynomial();
                fp = f.invertF3();
                if (fp == null) {
                    continue;
                }
            }
            fq = f.invertFq(q);
            if (fq != null) {
                break;
            }
        }
        if (fastFp) {
            fp = new IntegerPolynomial(N);
            fp.coeffs[0] = 1;
        }
        do {
            g = DenseTernaryPolynomial.generateRandom(N, dg, dg - 1, this.params.getRandom());
        } while (g.invertFq(q) == null);
        IntegerPolynomial h = g.mult(fq, q);
        h.mult3(q);
        h.ensurePositive(q);
        g.clear();
        fq.clear();
        NTRUEncryptionPrivateKeyParameters priv = new NTRUEncryptionPrivateKeyParameters(h, t, fp, this.params.getEncryptionParameters());
        NTRUEncryptionPublicKeyParameters pub = new NTRUEncryptionPublicKeyParameters(h, this.params.getEncryptionParameters());
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pub, (AsymmetricKeyParameter) priv);
    }
}
