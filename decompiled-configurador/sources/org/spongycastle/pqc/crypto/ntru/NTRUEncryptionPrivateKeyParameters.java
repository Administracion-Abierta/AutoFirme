package org.spongycastle.pqc.crypto.ntru;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.pqc.math.ntru.polynomial.DenseTernaryPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.Polynomial;
import org.spongycastle.pqc.math.ntru.polynomial.ProductFormPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.SparseTernaryPolynomial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/ntru/NTRUEncryptionPrivateKeyParameters.class */
public class NTRUEncryptionPrivateKeyParameters extends NTRUEncryptionKeyParameters {
    public Polynomial t;
    public IntegerPolynomial fp;
    public IntegerPolynomial h;

    public NTRUEncryptionPrivateKeyParameters(IntegerPolynomial h, Polynomial t, IntegerPolynomial fp, NTRUEncryptionParameters params) {
        super(true, params);
        this.h = h;
        this.t = t;
        this.fp = fp;
    }

    public NTRUEncryptionPrivateKeyParameters(byte[] b, NTRUEncryptionParameters params) throws IOException {
        this(new ByteArrayInputStream(b), params);
    }

    public NTRUEncryptionPrivateKeyParameters(InputStream is, NTRUEncryptionParameters params) throws IOException {
        super(true, params);
        if (params.polyType == 1) {
            int N = params.N;
            int df1 = params.df1;
            int df2 = params.df2;
            int df3Ones = params.df3;
            int df3NegOnes = params.fastFp ? params.df3 : params.df3 - 1;
            this.h = IntegerPolynomial.fromBinary(is, params.N, params.q);
            this.t = ProductFormPolynomial.fromBinary(is, N, df1, df2, df3Ones, df3NegOnes);
        } else {
            this.h = IntegerPolynomial.fromBinary(is, params.N, params.q);
            IntegerPolynomial fInt = IntegerPolynomial.fromBinary3Tight(is, params.N);
            this.t = params.sparse ? new SparseTernaryPolynomial(fInt) : new DenseTernaryPolynomial(fInt);
        }
        init();
    }

    private void init() {
        if (this.params.fastFp) {
            this.fp = new IntegerPolynomial(this.params.N);
            this.fp.coeffs[0] = 1;
        } else {
            this.fp = this.t.toIntegerPolynomial().invertF3();
        }
    }

    public byte[] getEncoded() {
        byte[] tBytes;
        byte[] hBytes = this.h.toBinary(this.params.q);
        if (this.t instanceof ProductFormPolynomial) {
            tBytes = ((ProductFormPolynomial) this.t).toBinary();
        } else {
            tBytes = this.t.toIntegerPolynomial().toBinary3Tight();
        }
        byte[] res = new byte[hBytes.length + tBytes.length];
        System.arraycopy(hBytes, 0, res, 0, hBytes.length);
        System.arraycopy(tBytes, 0, res, hBytes.length, tBytes.length);
        return res;
    }

    public void writeTo(OutputStream os) throws IOException {
        os.write(getEncoded());
    }

    public int hashCode() {
        int result = (31 * 1) + (this.params == null ? 0 : this.params.hashCode());
        return (31 * ((31 * result) + (this.t == null ? 0 : this.t.hashCode()))) + (this.h == null ? 0 : this.h.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NTRUEncryptionPrivateKeyParameters)) {
            return false;
        }
        NTRUEncryptionPrivateKeyParameters other = (NTRUEncryptionPrivateKeyParameters) obj;
        if (this.params == null) {
            if (other.params != null) {
                return false;
            }
        } else if (!this.params.equals(other.params)) {
            return false;
        }
        if (this.t == null) {
            if (other.t != null) {
                return false;
            }
        } else if (!this.t.equals(other.t)) {
            return false;
        }
        if (!this.h.equals(other.h)) {
            return false;
        }
        return true;
    }
}
