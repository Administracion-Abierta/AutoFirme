package org.spongycastle.pqc.crypto.ntru;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/ntru/NTRUEncryptionPublicKeyParameters.class */
public class NTRUEncryptionPublicKeyParameters extends NTRUEncryptionKeyParameters {
    public IntegerPolynomial h;

    public NTRUEncryptionPublicKeyParameters(IntegerPolynomial h, NTRUEncryptionParameters params) {
        super(false, params);
        this.h = h;
    }

    public NTRUEncryptionPublicKeyParameters(byte[] b, NTRUEncryptionParameters params) {
        super(false, params);
        this.h = IntegerPolynomial.fromBinary(b, params.N, params.q);
    }

    public NTRUEncryptionPublicKeyParameters(InputStream is, NTRUEncryptionParameters params) throws IOException {
        super(false, params);
        this.h = IntegerPolynomial.fromBinary(is, params.N, params.q);
    }

    public byte[] getEncoded() {
        return this.h.toBinary(this.params.q);
    }

    public void writeTo(OutputStream os) throws IOException {
        os.write(getEncoded());
    }

    public int hashCode() {
        int result = (31 * 1) + (this.h == null ? 0 : this.h.hashCode());
        return (31 * result) + (this.params == null ? 0 : this.params.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NTRUEncryptionPublicKeyParameters)) {
            return false;
        }
        NTRUEncryptionPublicKeyParameters other = (NTRUEncryptionPublicKeyParameters) obj;
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
