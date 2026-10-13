package org.spongycastle.pqc.math.ntru.polynomial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/ProductFormPolynomial.class */
public class ProductFormPolynomial implements Polynomial {
    private SparseTernaryPolynomial f1;
    private SparseTernaryPolynomial f2;
    private SparseTernaryPolynomial f3;

    public ProductFormPolynomial(SparseTernaryPolynomial f1, SparseTernaryPolynomial f2, SparseTernaryPolynomial f3) {
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
    }

    public static ProductFormPolynomial generateRandom(int N, int df1, int df2, int df3Ones, int df3NegOnes, SecureRandom random) {
        SparseTernaryPolynomial f1 = SparseTernaryPolynomial.generateRandom(N, df1, df1, random);
        SparseTernaryPolynomial f2 = SparseTernaryPolynomial.generateRandom(N, df2, df2, random);
        SparseTernaryPolynomial f3 = SparseTernaryPolynomial.generateRandom(N, df3Ones, df3NegOnes, random);
        return new ProductFormPolynomial(f1, f2, f3);
    }

    public static ProductFormPolynomial fromBinary(byte[] data, int N, int df1, int df2, int df3Ones, int df3NegOnes) throws IOException {
        return fromBinary(new ByteArrayInputStream(data), N, df1, df2, df3Ones, df3NegOnes);
    }

    public static ProductFormPolynomial fromBinary(InputStream is, int N, int df1, int df2, int df3Ones, int df3NegOnes) throws IOException {
        SparseTernaryPolynomial f1 = SparseTernaryPolynomial.fromBinary(is, N, df1, df1);
        SparseTernaryPolynomial f2 = SparseTernaryPolynomial.fromBinary(is, N, df2, df2);
        SparseTernaryPolynomial f3 = SparseTernaryPolynomial.fromBinary(is, N, df3Ones, df3NegOnes);
        return new ProductFormPolynomial(f1, f2, f3);
    }

    public byte[] toBinary() {
        byte[] f1Bin = this.f1.toBinary();
        byte[] f2Bin = this.f2.toBinary();
        byte[] f3Bin = this.f3.toBinary();
        byte[] all = Arrays.copyOf(f1Bin, f1Bin.length + f2Bin.length + f3Bin.length);
        System.arraycopy(f2Bin, 0, all, f1Bin.length, f2Bin.length);
        System.arraycopy(f3Bin, 0, all, f1Bin.length + f2Bin.length, f3Bin.length);
        return all;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial b) {
        IntegerPolynomial c = this.f2.mult(this.f1.mult(b));
        c.add(this.f3.mult(b));
        return c;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public BigIntPolynomial mult(BigIntPolynomial b) {
        BigIntPolynomial c = this.f2.mult(this.f1.mult(b));
        c.add(this.f3.mult(b));
        return c;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial toIntegerPolynomial() {
        IntegerPolynomial i = this.f1.mult(this.f2.toIntegerPolynomial());
        i.add(this.f3.toIntegerPolynomial());
        return i;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial poly2, int modulus) {
        IntegerPolynomial c = mult(poly2);
        c.mod(modulus);
        return c;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.f1 == null ? 0 : this.f1.hashCode());
        return (31 * ((31 * result) + (this.f2 == null ? 0 : this.f2.hashCode()))) + (this.f3 == null ? 0 : this.f3.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProductFormPolynomial other = (ProductFormPolynomial) obj;
        if (this.f1 == null) {
            if (other.f1 != null) {
                return false;
            }
        } else if (!this.f1.equals(other.f1)) {
            return false;
        }
        if (this.f2 == null) {
            if (other.f2 != null) {
                return false;
            }
        } else if (!this.f2.equals(other.f2)) {
            return false;
        }
        if (this.f3 == null) {
            if (other.f3 != null) {
                return false;
            }
            return true;
        }
        if (!this.f3.equals(other.f3)) {
            return false;
        }
        return true;
    }
}
