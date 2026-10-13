package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSContext.class */
public class LMSContext implements Digest {
    private final byte[] C;
    private final LMOtsPrivateKey key;
    private final LMSigParameters sigParams;
    private final byte[][] path;
    private final LMOtsPublicKey publicKey;
    private final Object signature;
    private LMSSignedPubKey[] signedPubKeys;
    private volatile Digest digest;

    LMSContext(LMOtsPrivateKey lMOtsPrivateKey, LMSigParameters lMSigParameters, Digest digest, byte[] bArr, byte[][] bArr2) {
        this.key = lMOtsPrivateKey;
        this.sigParams = lMSigParameters;
        this.digest = digest;
        this.C = bArr;
        this.path = bArr2;
        this.publicKey = null;
        this.signature = null;
    }

    LMSContext(LMOtsPublicKey lMOtsPublicKey, Object obj, Digest digest) {
        this.publicKey = lMOtsPublicKey;
        this.signature = obj;
        this.digest = digest;
        this.C = null;
        this.key = null;
        this.sigParams = null;
        this.path = null;
    }

    byte[] getC() {
        return this.C;
    }

    byte[] getQ() {
        byte[] bArr = new byte[34];
        this.digest.doFinal(bArr, 0);
        this.digest = null;
        return bArr;
    }

    byte[][] getPath() {
        return this.path;
    }

    LMOtsPrivateKey getPrivateKey() {
        return this.key;
    }

    LMOtsPublicKey getPublicKey() {
        return this.publicKey;
    }

    LMSigParameters getSigParams() {
        return this.sigParams;
    }

    public Object getSignature() {
        return this.signature;
    }

    LMSSignedPubKey[] getSignedPubKeys() {
        return this.signedPubKeys;
    }

    LMSContext withSignedPublicKeys(LMSSignedPubKey[] lMSSignedPubKeyArr) {
        this.signedPubKeys = lMSSignedPubKeyArr;
        return this;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName();
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.digest.getDigestSize();
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.digest.update(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        this.digest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        return this.digest.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.digest.reset();
    }
}
