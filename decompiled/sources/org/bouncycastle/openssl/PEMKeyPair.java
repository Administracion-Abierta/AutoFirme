package org.bouncycastle.openssl;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMKeyPair.class */
public class PEMKeyPair {
    private final SubjectPublicKeyInfo publicKeyInfo;
    private final PrivateKeyInfo privateKeyInfo;

    public PEMKeyPair(SubjectPublicKeyInfo subjectPublicKeyInfo, PrivateKeyInfo privateKeyInfo) {
        this.publicKeyInfo = subjectPublicKeyInfo;
        this.privateKeyInfo = privateKeyInfo;
    }

    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }

    public SubjectPublicKeyInfo getPublicKeyInfo() {
        return this.publicKeyInfo;
    }
}
