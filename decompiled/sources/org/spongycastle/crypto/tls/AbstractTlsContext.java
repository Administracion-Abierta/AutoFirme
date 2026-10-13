package org.spongycastle.crypto.tls;

import java.security.SecureRandom;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.prng.DigestRandomGenerator;
import org.spongycastle.crypto.prng.RandomGenerator;
import org.spongycastle.util.Times;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/AbstractTlsContext.class */
abstract class AbstractTlsContext implements TlsContext {
    private static long counter = Times.nanoTime();
    private RandomGenerator nonceRandom;
    private SecureRandom secureRandom;
    private SecurityParameters securityParameters;
    private ProtocolVersion clientVersion = null;
    private ProtocolVersion serverVersion = null;
    private TlsSession session = null;
    private Object userObject = null;

    private static synchronized long nextCounterValue() {
        long j = counter + 1;
        counter = j;
        return j;
    }

    AbstractTlsContext(SecureRandom secureRandom, SecurityParameters securityParameters) {
        Digest d = TlsUtils.createHash((short) 4);
        byte[] seed = new byte[d.getDigestSize()];
        secureRandom.nextBytes(seed);
        this.nonceRandom = new DigestRandomGenerator(d);
        this.nonceRandom.addSeedMaterial(nextCounterValue());
        this.nonceRandom.addSeedMaterial(Times.nanoTime());
        this.nonceRandom.addSeedMaterial(seed);
        this.secureRandom = secureRandom;
        this.securityParameters = securityParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public RandomGenerator getNonceRandomGenerator() {
        return this.nonceRandom;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public SecurityParameters getSecurityParameters() {
        return this.securityParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public ProtocolVersion getClientVersion() {
        return this.clientVersion;
    }

    void setClientVersion(ProtocolVersion clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public ProtocolVersion getServerVersion() {
        return this.serverVersion;
    }

    void setServerVersion(ProtocolVersion serverVersion) {
        this.serverVersion = serverVersion;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public TlsSession getResumableSession() {
        return this.session;
    }

    void setResumableSession(TlsSession session) {
        this.session = session;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public Object getUserObject() {
        return this.userObject;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public byte[] exportKeyingMaterial(String asciiLabel, byte[] context_value, int length) {
        if (context_value != null && !TlsUtils.isValidUint16(context_value.length)) {
            throw new IllegalArgumentException("'context_value' must have length less than 2^16 (or be null)");
        }
        SecurityParameters sp = getSecurityParameters();
        byte[] cr = sp.getClientRandom();
        byte[] sr = sp.getServerRandom();
        int seedLength = cr.length + sr.length;
        if (context_value != null) {
            seedLength += 2 + context_value.length;
        }
        byte[] seed = new byte[seedLength];
        System.arraycopy(cr, 0, seed, 0, cr.length);
        int seedPos = 0 + cr.length;
        System.arraycopy(sr, 0, seed, seedPos, sr.length);
        int seedPos2 = seedPos + sr.length;
        if (context_value != null) {
            TlsUtils.writeUint16(context_value.length, seed, seedPos2);
            int seedPos3 = seedPos2 + 2;
            System.arraycopy(context_value, 0, seed, seedPos3, context_value.length);
            seedPos2 = seedPos3 + context_value.length;
        }
        if (seedPos2 != seedLength) {
            throw new IllegalStateException("error in calculation of seed for export");
        }
        return TlsUtils.PRF(this, sp.getMasterSecret(), asciiLabel, seed, length);
    }
}
