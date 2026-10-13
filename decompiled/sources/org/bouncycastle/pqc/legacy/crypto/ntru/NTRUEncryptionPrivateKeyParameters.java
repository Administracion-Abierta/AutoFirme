package org.bouncycastle.pqc.legacy.crypto.ntru;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.DenseTernaryPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.IntegerPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.ProductFormPolynomial;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.SparseTernaryPolynomial;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUEncryptionPrivateKeyParameters.class */
public class NTRUEncryptionPrivateKeyParameters extends NTRUEncryptionKeyParameters {
    public Polynomial t;
    public IntegerPolynomial fp;
    public IntegerPolynomial h;

    public NTRUEncryptionPrivateKeyParameters(IntegerPolynomial integerPolynomial, Polynomial polynomial, IntegerPolynomial integerPolynomial2, NTRUEncryptionParameters nTRUEncryptionParameters) {
        super(true, nTRUEncryptionParameters);
        this.h = integerPolynomial;
        this.t = polynomial;
        this.fp = integerPolynomial2;
    }

    public NTRUEncryptionPrivateKeyParameters(byte[] bArr, NTRUEncryptionParameters nTRUEncryptionParameters) throws IOException {
        this(new ByteArrayInputStream(bArr), nTRUEncryptionParameters);
    }

    public NTRUEncryptionPrivateKeyParameters(InputStream inputStream, NTRUEncryptionParameters nTRUEncryptionParameters) throws IOException {
        super(true, nTRUEncryptionParameters);
        if (nTRUEncryptionParameters.polyType == 1) {
            int i = nTRUEncryptionParameters.N;
            int i2 = nTRUEncryptionParameters.df1;
            int i3 = nTRUEncryptionParameters.df2;
            int i4 = nTRUEncryptionParameters.df3;
            int i5 = nTRUEncryptionParameters.fastFp ? nTRUEncryptionParameters.df3 : nTRUEncryptionParameters.df3 - 1;
            this.h = IntegerPolynomial.fromBinary(inputStream, nTRUEncryptionParameters.N, nTRUEncryptionParameters.q);
            this.t = ProductFormPolynomial.fromBinary(inputStream, i, i2, i3, i4, i5);
        } else {
            this.h = IntegerPolynomial.fromBinary(inputStream, nTRUEncryptionParameters.N, nTRUEncryptionParameters.q);
            IntegerPolynomial integerPolynomialFromBinary3Tight = IntegerPolynomial.fromBinary3Tight(inputStream, nTRUEncryptionParameters.N);
            this.t = nTRUEncryptionParameters.sparse ? new SparseTernaryPolynomial(integerPolynomialFromBinary3Tight) : new DenseTernaryPolynomial(integerPolynomialFromBinary3Tight);
        }
        init();
    }

    private void init() {
        if (!this.params.fastFp) {
            this.fp = this.t.toIntegerPolynomial().invertF3();
        } else {
            this.fp = new IntegerPolynomial(this.params.N);
            this.fp.coeffs[0] = 1;
        }
    }

    public byte[] getEncoded() {
        byte[] binary = this.h.toBinary(this.params.q);
        byte[] binary2 = this.t instanceof ProductFormPolynomial ? ((ProductFormPolynomial) this.t).toBinary() : this.t.toIntegerPolynomial().toBinary3Tight();
        byte[] bArr = new byte[binary.length + binary2.length];
        System.arraycopy(binary, 0, bArr, 0, binary.length);
        System.arraycopy(binary2, 0, bArr, binary.length, binary2.length);
        return bArr;
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(getEncoded());
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * 1) + (this.params == null ? 0 : this.params.hashCode()))) + (this.t == null ? 0 : this.t.hashCode()))) + (this.h == null ? 0 : this.h.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NTRUEncryptionPrivateKeyParameters)) {
            return false;
        }
        NTRUEncryptionPrivateKeyParameters nTRUEncryptionPrivateKeyParameters = (NTRUEncryptionPrivateKeyParameters) obj;
        if (this.params == null) {
            if (nTRUEncryptionPrivateKeyParameters.params != null) {
                return false;
            }
        } else if (!this.params.equals(nTRUEncryptionPrivateKeyParameters.params)) {
            return false;
        }
        if (this.t == null) {
            if (nTRUEncryptionPrivateKeyParameters.t != null) {
                return false;
            }
        } else if (!this.t.equals(nTRUEncryptionPrivateKeyParameters.t)) {
            return false;
        }
        return this.h.equals(nTRUEncryptionPrivateKeyParameters.h);
    }
}
