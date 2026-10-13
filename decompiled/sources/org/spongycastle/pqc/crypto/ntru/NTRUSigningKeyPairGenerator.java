package org.spongycastle.pqc.crypto.ntru;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.math.ntru.euclid.BigIntEuclidean;
import org.spongycastle.pqc.math.ntru.polynomial.BigDecimalPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.BigIntPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.DenseTernaryPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.Polynomial;
import org.spongycastle.pqc.math.ntru.polynomial.ProductFormPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.Resultant;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningKeyPairGenerator.class */
public class NTRUSigningKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NTRUSigningKeyGenerationParameters params;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.params = (NTRUSigningKeyGenerationParameters) param;
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        NTRUSigningPublicKeyParameters pub = null;
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<NTRUSigningPrivateKeyParameters.Basis>> bases = new ArrayList<>();
        for (int k = this.params.B; k >= 0; k--) {
            bases.add(executor.submit(new BasisGenerationTask()));
        }
        executor.shutdown();
        List<NTRUSigningPrivateKeyParameters.Basis> basises = new ArrayList<>();
        for (int k2 = this.params.B; k2 >= 0; k2--) {
            Future<NTRUSigningPrivateKeyParameters.Basis> basis = bases.get(k2);
            try {
                basises.add(basis.get());
                if (k2 == this.params.B) {
                    pub = new NTRUSigningPublicKeyParameters(basis.get().h, this.params.getSigningParameters());
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        NTRUSigningPrivateKeyParameters priv = new NTRUSigningPrivateKeyParameters(basises, pub);
        AsymmetricCipherKeyPair kp = new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pub, (AsymmetricKeyParameter) priv);
        return kp;
    }

    public AsymmetricCipherKeyPair generateKeyPairSingleThread() {
        List<NTRUSigningPrivateKeyParameters.Basis> basises = new ArrayList<>();
        NTRUSigningPublicKeyParameters pub = null;
        for (int k = this.params.B; k >= 0; k--) {
            NTRUSigningPrivateKeyParameters.Basis basis = generateBoundedBasis();
            basises.add(basis);
            if (k == 0) {
                pub = new NTRUSigningPublicKeyParameters(basis.h, this.params.getSigningParameters());
            }
        }
        NTRUSigningPrivateKeyParameters priv = new NTRUSigningPrivateKeyParameters(basises, pub);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pub, (AsymmetricKeyParameter) priv);
    }

    private void minimizeFG(IntegerPolynomial f, IntegerPolynomial g, IntegerPolynomial F, IntegerPolynomial G, int N) {
        int E = 0;
        for (int j = 0; j < N; j++) {
            E += 2 * N * ((f.coeffs[j] * f.coeffs[j]) + (g.coeffs[j] * g.coeffs[j]));
        }
        int E2 = E - 4;
        IntegerPolynomial u = (IntegerPolynomial) f.clone();
        IntegerPolynomial v = (IntegerPolynomial) g.clone();
        int j2 = 0;
        int k = 0;
        while (k < N && j2 < N) {
            int D = 0;
            for (int i = 0; i < N; i++) {
                int D1 = F.coeffs[i] * f.coeffs[i];
                int D2 = G.coeffs[i] * g.coeffs[i];
                int D3 = 4 * N * (D1 + D2);
                D += D3;
            }
            int D4 = 4 * (F.sumCoeffs() + G.sumCoeffs());
            int D5 = D - D4;
            if (D5 > E2) {
                F.sub(u);
                G.sub(v);
                k++;
                j2 = 0;
            } else if (D5 < (-E2)) {
                F.add(u);
                G.add(v);
                k++;
                j2 = 0;
            }
            j2++;
            u.rotate1();
            v.rotate1();
        }
    }

    private FGBasis generateBasis() {
        Polynomial f;
        IntegerPolynomial fInt;
        IntegerPolynomial fq;
        Polynomial g;
        IntegerPolynomial gInt;
        Resultant rg;
        BigIntEuclidean r;
        BigIntPolynomial C;
        Polynomial fPrime;
        IntegerPolynomial h;
        int N = this.params.N;
        int q = this.params.q;
        int d = this.params.d;
        int d1 = this.params.d1;
        int d2 = this.params.d2;
        int d3 = this.params.d3;
        int basisType = this.params.basisType;
        int _2n1 = (2 * N) + 1;
        boolean primeCheck = this.params.primeCheck;
        while (true) {
            f = this.params.polyType == 0 ? DenseTernaryPolynomial.generateRandom(N, d + 1, d, new SecureRandom()) : ProductFormPolynomial.generateRandom(N, d1, d2, d3 + 1, d3, new SecureRandom());
            fInt = f.toIntegerPolynomial();
            if (!primeCheck || !fInt.resultant(_2n1).res.equals(BigInteger.ZERO)) {
                fq = fInt.invertFq(q);
                if (fq != null) {
                    break;
                }
            }
        }
        Resultant rf = fInt.resultant();
        while (true) {
            g = this.params.polyType == 0 ? DenseTernaryPolynomial.generateRandom(N, d + 1, d, new SecureRandom()) : ProductFormPolynomial.generateRandom(N, d1, d2, d3 + 1, d3, new SecureRandom());
            gInt = g.toIntegerPolynomial();
            if (!primeCheck || !gInt.resultant(_2n1).res.equals(BigInteger.ZERO)) {
                if (gInt.invertFq(q) != null) {
                    rg = gInt.resultant();
                    r = BigIntEuclidean.calculate(rf.res, rg.res);
                    if (r.gcd.equals(BigInteger.ONE)) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        BigIntPolynomial A = (BigIntPolynomial) rf.rho.clone();
        A.mult(r.x.multiply(BigInteger.valueOf(q)));
        BigIntPolynomial B = (BigIntPolynomial) rg.rho.clone();
        B.mult(r.y.multiply(BigInteger.valueOf(-q)));
        if (this.params.keyGenAlg == 0) {
            int[] fRevCoeffs = new int[N];
            int[] gRevCoeffs = new int[N];
            fRevCoeffs[0] = fInt.coeffs[0];
            gRevCoeffs[0] = gInt.coeffs[0];
            for (int i = 1; i < N; i++) {
                fRevCoeffs[i] = fInt.coeffs[N - i];
                gRevCoeffs[i] = gInt.coeffs[N - i];
            }
            IntegerPolynomial fRev = new IntegerPolynomial(fRevCoeffs);
            IntegerPolynomial gRev = new IntegerPolynomial(gRevCoeffs);
            IntegerPolynomial t = f.mult(fRev);
            t.add(g.mult(gRev));
            Resultant rt = t.resultant();
            BigIntPolynomial C2 = fRev.mult(B);
            C2.add(gRev.mult(A));
            C = C2.mult(rt.rho);
            C.div(rt.res);
        } else {
            int log10N = 0;
            int i2 = 1;
            while (true) {
                int i3 = i2;
                if (i3 >= N) {
                    break;
                }
                log10N++;
                i2 = i3 * 10;
            }
            BigDecimalPolynomial fInv = rf.rho.div(new BigDecimal(rf.res), B.getMaxCoeffLength() + 1 + log10N);
            BigDecimalPolynomial gInv = rg.rho.div(new BigDecimal(rg.res), A.getMaxCoeffLength() + 1 + log10N);
            BigDecimalPolynomial Cdec = fInv.mult(B);
            Cdec.add(gInv.mult(A));
            Cdec.halve();
            C = Cdec.round();
        }
        BigIntPolynomial F = (BigIntPolynomial) B.clone();
        F.sub(f.mult(C));
        BigIntPolynomial G = (BigIntPolynomial) A.clone();
        G.sub(g.mult(C));
        IntegerPolynomial FInt = new IntegerPolynomial(F);
        IntegerPolynomial GInt = new IntegerPolynomial(G);
        minimizeFG(fInt, gInt, FInt, GInt, N);
        if (basisType == 0) {
            fPrime = FInt;
            h = g.mult(fq, q);
        } else {
            fPrime = g;
            h = FInt.mult(fq, q);
        }
        h.modPositive(q);
        return new FGBasis(f, fPrime, h, FInt, GInt, this.params);
    }

    public NTRUSigningPrivateKeyParameters.Basis generateBoundedBasis() {
        FGBasis basis;
        do {
            basis = generateBasis();
        } while (!basis.isNormOk());
        return basis;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningKeyPairGenerator$BasisGenerationTask.class */
    private class BasisGenerationTask implements Callable<NTRUSigningPrivateKeyParameters.Basis> {
        private BasisGenerationTask() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public NTRUSigningPrivateKeyParameters.Basis call() throws Exception {
            return NTRUSigningKeyPairGenerator.this.generateBoundedBasis();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningKeyPairGenerator$FGBasis.class */
    public class FGBasis extends NTRUSigningPrivateKeyParameters.Basis {
        public IntegerPolynomial F;
        public IntegerPolynomial G;

        FGBasis(Polynomial f, Polynomial fPrime, IntegerPolynomial h, IntegerPolynomial F, IntegerPolynomial G, NTRUSigningKeyGenerationParameters params) {
            super(f, fPrime, h, params);
            this.F = F;
            this.G = G;
        }

        boolean isNormOk() {
            double keyNormBoundSq = this.params.keyNormBoundSq;
            int q = this.params.q;
            return ((double) this.F.centeredNormSq(q)) < keyNormBoundSq && ((double) this.G.centeredNormSq(q)) < keyNormBoundSq;
        }
    }
}
