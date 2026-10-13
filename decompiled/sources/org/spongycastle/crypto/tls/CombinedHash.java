package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/CombinedHash.class */
class CombinedHash implements TlsHandshakeHash {
    protected TlsContext context;
    protected Digest md5;
    protected Digest sha1;

    CombinedHash() {
        this.md5 = TlsUtils.createHash((short) 1);
        this.sha1 = TlsUtils.createHash((short) 2);
    }

    CombinedHash(CombinedHash t) {
        this.context = t.context;
        this.md5 = TlsUtils.cloneHash((short) 1, t.md5);
        this.sha1 = TlsUtils.cloneHash((short) 2, t.sha1);
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public void init(TlsContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public TlsHandshakeHash notifyPRFDetermined() {
        return this;
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public void trackHashAlgorithm(short hashAlgorithm) {
        throw new IllegalStateException("CombinedHash only supports calculating the legacy PRF for handshake hash");
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public void sealHashAlgorithms() {
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public TlsHandshakeHash stopTracking() {
        return new CombinedHash(this);
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public Digest forkPRFHash() {
        return new CombinedHash(this);
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public byte[] getFinalHash(short hashAlgorithm) {
        throw new IllegalStateException("CombinedHash doesn't support multiple hashes");
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.md5.getAlgorithmName() + " and " + this.sha1.getAlgorithmName();
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.md5.getDigestSize() + this.sha1.getDigestSize();
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte input) {
        this.md5.update(input);
        this.sha1.update(input);
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] input, int inOff, int len) {
        this.md5.update(input, inOff, len);
        this.sha1.update(input, inOff, len);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] output, int outOff) {
        if (this.context != null && TlsUtils.isSSL(this.context)) {
            ssl3Complete(this.md5, SSL3Mac.IPAD, SSL3Mac.OPAD, 48);
            ssl3Complete(this.sha1, SSL3Mac.IPAD, SSL3Mac.OPAD, 40);
        }
        int i1 = this.md5.doFinal(output, outOff);
        int i2 = this.sha1.doFinal(output, outOff + i1);
        return i1 + i2;
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.md5.reset();
        this.sha1.reset();
    }

    protected void ssl3Complete(Digest d, byte[] ipad, byte[] opad, int padLength) {
        byte[] master_secret = this.context.getSecurityParameters().masterSecret;
        d.update(master_secret, 0, master_secret.length);
        d.update(ipad, 0, padLength);
        byte[] tmp = new byte[d.getDigestSize()];
        d.doFinal(tmp, 0);
        d.update(master_secret, 0, master_secret.length);
        d.update(opad, 0, padLength);
        d.update(tmp, 0, tmp.length);
    }
}
