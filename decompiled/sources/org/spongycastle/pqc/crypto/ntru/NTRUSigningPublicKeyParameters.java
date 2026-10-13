package org.spongycastle.pqc.crypto.ntru;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningPublicKeyParameters.class */
public class NTRUSigningPublicKeyParameters extends AsymmetricKeyParameter {
    private NTRUSigningParameters params;
    public IntegerPolynomial h;

    public NTRUSigningPublicKeyParameters(IntegerPolynomial h, NTRUSigningParameters params) {
        super(false);
        this.h = h;
        this.params = params;
    }

    public NTRUSigningPublicKeyParameters(byte[] b, NTRUSigningParameters params) {
        super(false);
        this.h = IntegerPolynomial.fromBinary(b, params.N, params.q);
        this.params = params;
    }

    public NTRUSigningPublicKeyParameters(InputStream is, NTRUSigningParameters params) throws IOException {
        super(false);
        this.h = IntegerPolynomial.fromBinary(is, params.N, params.q);
        this.params = params;
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
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NTRUSigningPublicKeyParameters other = (NTRUSigningPublicKeyParameters) obj;
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
