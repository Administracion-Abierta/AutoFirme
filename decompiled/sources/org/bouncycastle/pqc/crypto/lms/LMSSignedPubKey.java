package org.bouncycastle.pqc.crypto.lms;

import java.io.IOException;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSSignedPubKey.class */
class LMSSignedPubKey implements Encodable {
    private final LMSSignature signature;
    private final LMSPublicKeyParameters publicKey;

    public LMSSignedPubKey(LMSSignature lMSSignature, LMSPublicKeyParameters lMSPublicKeyParameters) {
        this.signature = lMSSignature;
        this.publicKey = lMSPublicKeyParameters;
    }

    public LMSSignature getSignature() {
        return this.signature;
    }

    public LMSPublicKeyParameters getPublicKey() {
        return this.publicKey;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LMSSignedPubKey lMSSignedPubKey = (LMSSignedPubKey) obj;
        if (this.signature != null) {
            if (!this.signature.equals(lMSSignedPubKey.signature)) {
                return false;
            }
        } else if (lMSSignedPubKey.signature != null) {
            return false;
        }
        if (this.publicKey != null) {
            return this.publicKey.equals(lMSSignedPubKey.publicKey);
        }
        return lMSSignedPubKey.publicKey == null;
    }

    public int hashCode() {
        return (31 * (this.signature != null ? this.signature.hashCode() : 0)) + (this.publicKey != null ? this.publicKey.hashCode() : 0);
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return Composer.compose().bytes(this.signature.getEncoded()).bytes(this.publicKey.getEncoded()).build();
    }
}
