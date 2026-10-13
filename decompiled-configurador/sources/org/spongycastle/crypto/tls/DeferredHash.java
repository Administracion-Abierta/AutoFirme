package org.spongycastle.crypto.tls;

import java.util.Enumeration;
import java.util.Hashtable;
import org.spongycastle.crypto.Digest;
import org.spongycastle.util.Shorts;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DeferredHash.class */
class DeferredHash implements TlsHandshakeHash {
    protected static final int BUFFERING_HASH_LIMIT = 4;
    protected TlsContext context;
    private DigestInputBuffer buf;
    private Hashtable hashes;
    private Short prfHashAlgorithm;

    DeferredHash() {
        this.buf = new DigestInputBuffer();
        this.hashes = new Hashtable();
        this.prfHashAlgorithm = null;
    }

    private DeferredHash(Short prfHashAlgorithm, Digest prfHash) {
        this.buf = null;
        this.hashes = new Hashtable();
        this.prfHashAlgorithm = prfHashAlgorithm;
        this.hashes.put(prfHashAlgorithm, prfHash);
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public void init(TlsContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public TlsHandshakeHash notifyPRFDetermined() {
        int prfAlgorithm = this.context.getSecurityParameters().getPrfAlgorithm();
        if (prfAlgorithm == 0) {
            CombinedHash legacyHash = new CombinedHash();
            legacyHash.init(this.context);
            this.buf.updateDigest(legacyHash);
            return legacyHash.notifyPRFDetermined();
        }
        this.prfHashAlgorithm = Shorts.valueOf(TlsUtils.getHashAlgorithmForPRFAlgorithm(prfAlgorithm));
        checkTrackingHash(this.prfHashAlgorithm);
        return this;
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public void trackHashAlgorithm(short hashAlgorithm) {
        if (this.buf == null) {
            throw new IllegalStateException("Too late to track more hash algorithms");
        }
        checkTrackingHash(Shorts.valueOf(hashAlgorithm));
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public void sealHashAlgorithms() {
        checkStopBuffering();
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public TlsHandshakeHash stopTracking() {
        Digest prfHash = TlsUtils.cloneHash(this.prfHashAlgorithm.shortValue(), (Digest) this.hashes.get(this.prfHashAlgorithm));
        if (this.buf != null) {
            this.buf.updateDigest(prfHash);
        }
        DeferredHash result = new DeferredHash(this.prfHashAlgorithm, prfHash);
        result.init(this.context);
        return result;
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public Digest forkPRFHash() {
        checkStopBuffering();
        if (this.buf != null) {
            Digest prfHash = TlsUtils.createHash(this.prfHashAlgorithm.shortValue());
            this.buf.updateDigest(prfHash);
            return prfHash;
        }
        return TlsUtils.cloneHash(this.prfHashAlgorithm.shortValue(), (Digest) this.hashes.get(this.prfHashAlgorithm));
    }

    @Override // org.spongycastle.crypto.tls.TlsHandshakeHash
    public byte[] getFinalHash(short hashAlgorithm) {
        Digest d = (Digest) this.hashes.get(Shorts.valueOf(hashAlgorithm));
        if (d == null) {
            throw new IllegalStateException("HashAlgorithm." + HashAlgorithm.getText(hashAlgorithm) + " is not being tracked");
        }
        Digest d2 = TlsUtils.cloneHash(hashAlgorithm, d);
        if (this.buf != null) {
            this.buf.updateDigest(d2);
        }
        byte[] bs = new byte[d2.getDigestSize()];
        d2.doFinal(bs, 0);
        return bs;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        throw new IllegalStateException("Use fork() to get a definite Digest");
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        throw new IllegalStateException("Use fork() to get a definite Digest");
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte input) {
        if (this.buf != null) {
            this.buf.write(input);
            return;
        }
        Enumeration e = this.hashes.elements();
        while (e.hasMoreElements()) {
            Digest hash = (Digest) e.nextElement();
            hash.update(input);
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] input, int inOff, int len) {
        if (this.buf != null) {
            this.buf.write(input, inOff, len);
            return;
        }
        Enumeration e = this.hashes.elements();
        while (e.hasMoreElements()) {
            Digest hash = (Digest) e.nextElement();
            hash.update(input, inOff, len);
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] output, int outOff) {
        throw new IllegalStateException("Use fork() to get a definite Digest");
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        if (this.buf != null) {
            this.buf.reset();
            return;
        }
        Enumeration e = this.hashes.elements();
        while (e.hasMoreElements()) {
            Digest hash = (Digest) e.nextElement();
            hash.reset();
        }
    }

    protected void checkStopBuffering() {
        if (this.buf != null && this.hashes.size() <= 4) {
            Enumeration e = this.hashes.elements();
            while (e.hasMoreElements()) {
                Digest hash = (Digest) e.nextElement();
                this.buf.updateDigest(hash);
            }
            this.buf = null;
        }
    }

    protected void checkTrackingHash(Short hashAlgorithm) {
        if (!this.hashes.containsKey(hashAlgorithm)) {
            Digest hash = TlsUtils.createHash(hashAlgorithm.shortValue());
            this.hashes.put(hashAlgorithm, hash);
        }
    }
}
