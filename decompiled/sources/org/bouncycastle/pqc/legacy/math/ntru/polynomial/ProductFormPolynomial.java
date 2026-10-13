package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/ProductFormPolynomial.class */
public class ProductFormPolynomial implements Polynomial {
    private SparseTernaryPolynomial f1;
    private SparseTernaryPolynomial f2;
    private SparseTernaryPolynomial f3;

    public ProductFormPolynomial(SparseTernaryPolynomial sparseTernaryPolynomial, SparseTernaryPolynomial sparseTernaryPolynomial2, SparseTernaryPolynomial sparseTernaryPolynomial3) {
        this.f1 = sparseTernaryPolynomial;
        this.f2 = sparseTernaryPolynomial2;
        this.f3 = sparseTernaryPolynomial3;
    }

    public static ProductFormPolynomial generateRandom(int i, int i2, int i3, int i4, int i5, SecureRandom secureRandom) {
        return new ProductFormPolynomial(SparseTernaryPolynomial.generateRandom(i, i2, i2, secureRandom), SparseTernaryPolynomial.generateRandom(i, i3, i3, secureRandom), SparseTernaryPolynomial.generateRandom(i, i4, i5, secureRandom));
    }

    public static ProductFormPolynomial fromBinary(byte[] bArr, int i, int i2, int i3, int i4, int i5) throws IOException {
        return fromBinary(new ByteArrayInputStream(bArr), i, i2, i3, i4, i5);
    }

    public static ProductFormPolynomial fromBinary(InputStream inputStream, int i, int i2, int i3, int i4, int i5) throws IOException {
        return new ProductFormPolynomial(SparseTernaryPolynomial.fromBinary(inputStream, i, i2, i2), SparseTernaryPolynomial.fromBinary(inputStream, i, i3, i3), SparseTernaryPolynomial.fromBinary(inputStream, i, i4, i5));
    }

    public byte[] toBinary() {
        byte[] binary = this.f1.toBinary();
        byte[] binary2 = this.f2.toBinary();
        byte[] binary3 = this.f3.toBinary();
        byte[] bArrCopyOf = Arrays.copyOf(binary, binary.length + binary2.length + binary3.length);
        System.arraycopy(binary2, 0, bArrCopyOf, binary.length, binary2.length);
        System.arraycopy(binary3, 0, bArrCopyOf, binary.length + binary2.length, binary3.length);
        return bArrCopyOf;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial integerPolynomial) {
        IntegerPolynomial integerPolynomialMult = this.f2.mult(this.f1.mult(integerPolynomial));
        integerPolynomialMult.add(this.f3.mult(integerPolynomial));
        return integerPolynomialMult;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public BigIntPolynomial mult(BigIntPolynomial bigIntPolynomial) {
        BigIntPolynomial bigIntPolynomialMult = this.f2.mult(this.f1.mult(bigIntPolynomial));
        bigIntPolynomialMult.add(this.f3.mult(bigIntPolynomial));
        return bigIntPolynomialMult;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public IntegerPolynomial toIntegerPolynomial() {
        IntegerPolynomial integerPolynomialMult = this.f1.mult(this.f2.toIntegerPolynomial());
        integerPolynomialMult.add(this.f3.toIntegerPolynomial());
        return integerPolynomialMult;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial integerPolynomial, int i) {
        IntegerPolynomial integerPolynomialMult = mult(integerPolynomial);
        integerPolynomialMult.mod(i);
        return integerPolynomialMult;
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * 1) + (this.f1 == null ? 0 : this.f1.hashCode()))) + (this.f2 == null ? 0 : this.f2.hashCode()))) + (this.f3 == null ? 0 : this.f3.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProductFormPolynomial productFormPolynomial = (ProductFormPolynomial) obj;
        if (this.f1 == null) {
            if (productFormPolynomial.f1 != null) {
                return false;
            }
        } else if (!this.f1.equals(productFormPolynomial.f1)) {
            return false;
        }
        if (this.f2 == null) {
            if (productFormPolynomial.f2 != null) {
                return false;
            }
        } else if (!this.f2.equals(productFormPolynomial.f2)) {
            return false;
        }
        if (this.f3 == null) {
            return productFormPolynomial.f3 == null;
        }
        return this.f3.equals(productFormPolynomial.f3);
    }
}
