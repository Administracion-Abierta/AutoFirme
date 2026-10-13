package org.spongycastle.pqc.crypto.ntru;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.math.ntru.polynomial.DenseTernaryPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.Polynomial;
import org.spongycastle.pqc.math.ntru.polynomial.ProductFormPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.SparseTernaryPolynomial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningPrivateKeyParameters.class */
public class NTRUSigningPrivateKeyParameters extends AsymmetricKeyParameter {
    private List<Basis> bases;
    private NTRUSigningPublicKeyParameters publicKey;

    public NTRUSigningPrivateKeyParameters(byte[] b, NTRUSigningKeyGenerationParameters params) throws IOException {
        this(new ByteArrayInputStream(b), params);
    }

    public NTRUSigningPrivateKeyParameters(InputStream is, NTRUSigningKeyGenerationParameters params) throws IOException {
        super(true);
        this.bases = new ArrayList();
        int i = 0;
        while (i <= params.B) {
            add(new Basis(is, params, i != 0));
            i++;
        }
        this.publicKey = new NTRUSigningPublicKeyParameters(is, params.getSigningParameters());
    }

    public NTRUSigningPrivateKeyParameters(List<Basis> bases, NTRUSigningPublicKeyParameters publicKey) {
        super(true);
        this.bases = new ArrayList(bases);
        this.publicKey = publicKey;
    }

    private void add(Basis b) {
        this.bases.add(b);
    }

    public Basis getBasis(int i) {
        return this.bases.get(i);
    }

    public NTRUSigningPublicKeyParameters getPublicKey() {
        return this.publicKey;
    }

    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int i = 0;
        while (i < this.bases.size()) {
            this.bases.get(i).encode(os, i != 0);
            i++;
        }
        os.write(this.publicKey.getEncoded());
        return os.toByteArray();
    }

    public void writeTo(OutputStream os) throws IOException {
        os.write(getEncoded());
    }

    public int hashCode() {
        int result = 31 * 1;
        if (this.bases == null) {
            return result;
        }
        int result2 = result + this.bases.hashCode();
        for (Basis basis : this.bases) {
            result2 += basis.hashCode();
        }
        return result2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NTRUSigningPrivateKeyParameters other = (NTRUSigningPrivateKeyParameters) obj;
        if ((this.bases == null) != (other.bases == null)) {
            return false;
        }
        if (this.bases == null) {
            return true;
        }
        if (this.bases.size() != other.bases.size()) {
            return false;
        }
        for (int i = 0; i < this.bases.size(); i++) {
            Basis basis1 = this.bases.get(i);
            Basis basis2 = other.bases.get(i);
            if (!basis1.f.equals(basis2.f) || !basis1.fPrime.equals(basis2.fPrime)) {
                return false;
            }
            if ((i != 0 && !basis1.h.equals(basis2.h)) || !basis1.params.equals(basis2.params)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningPrivateKeyParameters$Basis.class */
    public static class Basis {
        public Polynomial f;
        public Polynomial fPrime;
        public IntegerPolynomial h;
        NTRUSigningKeyGenerationParameters params;

        protected Basis(Polynomial f, Polynomial fPrime, IntegerPolynomial h, NTRUSigningKeyGenerationParameters params) {
            this.f = f;
            this.fPrime = fPrime;
            this.h = h;
            this.params = params;
        }

        Basis(InputStream is, NTRUSigningKeyGenerationParameters params, boolean include_h) throws IOException {
            int N = params.N;
            int q = params.q;
            int d1 = params.d1;
            int d2 = params.d2;
            int d3 = params.d3;
            boolean sparse = params.sparse;
            this.params = params;
            if (params.polyType == 1) {
                this.f = ProductFormPolynomial.fromBinary(is, N, d1, d2, d3 + 1, d3);
            } else {
                IntegerPolynomial fInt = IntegerPolynomial.fromBinary3Tight(is, N);
                this.f = sparse ? new SparseTernaryPolynomial(fInt) : new DenseTernaryPolynomial(fInt);
            }
            if (params.basisType == 0) {
                IntegerPolynomial fPrimeInt = IntegerPolynomial.fromBinary(is, N, q);
                for (int i = 0; i < fPrimeInt.coeffs.length; i++) {
                    int[] iArr = fPrimeInt.coeffs;
                    int i2 = i;
                    iArr[i2] = iArr[i2] - (q / 2);
                }
                this.fPrime = fPrimeInt;
            } else if (params.polyType == 1) {
                this.fPrime = ProductFormPolynomial.fromBinary(is, N, d1, d2, d3 + 1, d3);
            } else {
                this.fPrime = IntegerPolynomial.fromBinary3Tight(is, N);
            }
            if (include_h) {
                this.h = IntegerPolynomial.fromBinary(is, N, q);
            }
        }

        void encode(OutputStream os, boolean include_h) throws IOException {
            int q = this.params.q;
            os.write(getEncoded(this.f));
            if (this.params.basisType == 0) {
                IntegerPolynomial fPrimeInt = this.fPrime.toIntegerPolynomial();
                for (int i = 0; i < fPrimeInt.coeffs.length; i++) {
                    int[] iArr = fPrimeInt.coeffs;
                    int i2 = i;
                    iArr[i2] = iArr[i2] + (q / 2);
                }
                os.write(fPrimeInt.toBinary(q));
            } else {
                os.write(getEncoded(this.fPrime));
            }
            if (include_h) {
                os.write(this.h.toBinary(q));
            }
        }

        private byte[] getEncoded(Polynomial p) {
            if (p instanceof ProductFormPolynomial) {
                return ((ProductFormPolynomial) p).toBinary();
            }
            return p.toIntegerPolynomial().toBinary3Tight();
        }

        public int hashCode() {
            int result = (31 * 1) + (this.f == null ? 0 : this.f.hashCode());
            return (31 * ((31 * ((31 * result) + (this.fPrime == null ? 0 : this.fPrime.hashCode()))) + (this.h == null ? 0 : this.h.hashCode()))) + (this.params == null ? 0 : this.params.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof Basis)) {
                return false;
            }
            Basis other = (Basis) obj;
            if (this.f == null) {
                if (other.f != null) {
                    return false;
                }
            } else if (!this.f.equals(other.f)) {
                return false;
            }
            if (this.fPrime == null) {
                if (other.fPrime != null) {
                    return false;
                }
            } else if (!this.fPrime.equals(other.fPrime)) {
                return false;
            }
            if (this.h == null) {
                if (other.h != null) {
                    return false;
                }
            } else if (!this.h.equals(other.h)) {
                return false;
            }
            if (this.params == null) {
                if (other.params != null) {
                    return false;
                }
                return true;
            }
            if (!this.params.equals(other.params)) {
                return false;
            }
            return true;
        }
    }
}
