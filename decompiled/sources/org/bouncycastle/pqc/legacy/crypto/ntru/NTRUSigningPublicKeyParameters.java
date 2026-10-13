package org.bouncycastle.pqc.legacy.crypto.ntru;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.IntegerPolynomial;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUSigningPublicKeyParameters.class */
public class NTRUSigningPublicKeyParameters extends AsymmetricKeyParameter {
    private NTRUSigningParameters params;
    public IntegerPolynomial h;

    public NTRUSigningPublicKeyParameters(IntegerPolynomial integerPolynomial, NTRUSigningParameters nTRUSigningParameters) {
        super(false);
        this.h = integerPolynomial;
        this.params = nTRUSigningParameters;
    }

    public NTRUSigningPublicKeyParameters(byte[] bArr, NTRUSigningParameters nTRUSigningParameters) {
        super(false);
        this.h = IntegerPolynomial.fromBinary(bArr, nTRUSigningParameters.N, nTRUSigningParameters.q);
        this.params = nTRUSigningParameters;
    }

    public NTRUSigningPublicKeyParameters(InputStream inputStream, NTRUSigningParameters nTRUSigningParameters) throws IOException {
        super(false);
        this.h = IntegerPolynomial.fromBinary(inputStream, nTRUSigningParameters.N, nTRUSigningParameters.q);
        this.params = nTRUSigningParameters;
    }

    public byte[] getEncoded() {
        return this.h.toBinary(this.params.q);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(getEncoded());
    }

    public int hashCode() {
        return (31 * ((31 * 1) + (this.h == null ? 0 : this.h.hashCode()))) + (this.params == null ? 0 : this.params.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NTRUSigningPublicKeyParameters nTRUSigningPublicKeyParameters = (NTRUSigningPublicKeyParameters) obj;
        if (this.h == null) {
            if (nTRUSigningPublicKeyParameters.h != null) {
                return false;
            }
        } else if (!this.h.equals(nTRUSigningPublicKeyParameters.h)) {
            return false;
        }
        if (this.params == null) {
            return nTRUSigningPublicKeyParameters.params == null;
        }
        return this.params.equals(nTRUSigningPublicKeyParameters.params);
    }
}
