package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/SignatureAndHashAlgorithm.class */
public class SignatureAndHashAlgorithm {
    protected short hash;
    protected short signature;

    public SignatureAndHashAlgorithm(short hash, short signature) {
        if (!TlsUtils.isValidUint8(hash)) {
            throw new IllegalArgumentException("'hash' should be a uint8");
        }
        if (!TlsUtils.isValidUint8(signature)) {
            throw new IllegalArgumentException("'signature' should be a uint8");
        }
        if (signature == 0) {
            throw new IllegalArgumentException("'signature' MUST NOT be \"anonymous\"");
        }
        this.hash = hash;
        this.signature = signature;
    }

    public short getHash() {
        return this.hash;
    }

    public short getSignature() {
        return this.signature;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SignatureAndHashAlgorithm)) {
            return false;
        }
        SignatureAndHashAlgorithm other = (SignatureAndHashAlgorithm) obj;
        return other.getHash() == getHash() && other.getSignature() == getSignature();
    }

    public int hashCode() {
        return (getHash() << 16) | getSignature();
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(getHash(), output);
        TlsUtils.writeUint8(getSignature(), output);
    }

    public static SignatureAndHashAlgorithm parse(InputStream input) throws IOException {
        short hash = TlsUtils.readUint8(input);
        short signature = TlsUtils.readUint8(input);
        return new SignatureAndHashAlgorithm(hash, signature);
    }
}
