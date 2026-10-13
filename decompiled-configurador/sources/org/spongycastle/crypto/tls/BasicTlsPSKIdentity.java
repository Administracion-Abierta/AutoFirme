package org.spongycastle.crypto.tls;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/BasicTlsPSKIdentity.class */
public class BasicTlsPSKIdentity implements TlsPSKIdentity {
    protected byte[] identity;
    protected byte[] psk;

    public BasicTlsPSKIdentity(byte[] identity, byte[] psk) {
        this.identity = Arrays.clone(identity);
        this.psk = Arrays.clone(psk);
    }

    public BasicTlsPSKIdentity(String identity, byte[] psk) {
        this.identity = Strings.toUTF8ByteArray(identity);
        this.psk = Arrays.clone(psk);
    }

    @Override // org.spongycastle.crypto.tls.TlsPSKIdentity
    public void skipIdentityHint() {
    }

    @Override // org.spongycastle.crypto.tls.TlsPSKIdentity
    public void notifyIdentityHint(byte[] psk_identity_hint) {
    }

    @Override // org.spongycastle.crypto.tls.TlsPSKIdentity
    public byte[] getPSKIdentity() {
        return this.identity;
    }

    @Override // org.spongycastle.crypto.tls.TlsPSKIdentity
    public byte[] getPSK() {
        return this.psk;
    }
}
