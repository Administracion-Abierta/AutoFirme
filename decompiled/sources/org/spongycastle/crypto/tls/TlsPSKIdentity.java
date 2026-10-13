package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsPSKIdentity.class */
public interface TlsPSKIdentity {
    void skipIdentityHint();

    void notifyIdentityHint(byte[] bArr);

    byte[] getPSKIdentity();

    byte[] getPSK();
}
