package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMOtsPrivateKey.class */
class LMOtsPrivateKey {
    private final LMOtsParameters parameter;
    private final byte[] I;
    private final int q;
    private final byte[] masterSecret;

    LMOtsPrivateKey(LMOtsParameters lMOtsParameters, byte[] bArr, int i, byte[] bArr2) {
        this.parameter = lMOtsParameters;
        this.I = bArr;
        this.q = i;
        this.masterSecret = bArr2;
    }

    LMSContext getSignatureContext(LMSigParameters lMSigParameters, byte[][] bArr) {
        byte[] bArr2 = new byte[this.parameter.getN()];
        SeedDerive derivationFunction = getDerivationFunction();
        derivationFunction.setJ(-3);
        derivationFunction.deriveSeed(bArr2, false);
        Digest digest = DigestUtil.getDigest(this.parameter);
        LmsUtils.byteArray(getI(), digest);
        LmsUtils.u32str(getQ(), digest);
        LmsUtils.u16str((short) -32383, digest);
        LmsUtils.byteArray(bArr2, digest);
        return new LMSContext(this, lMSigParameters, digest, bArr2, bArr);
    }

    SeedDerive getDerivationFunction() {
        SeedDerive seedDerive = new SeedDerive(this.I, this.masterSecret, DigestUtil.getDigest(this.parameter));
        seedDerive.setQ(this.q);
        return seedDerive;
    }

    public LMOtsParameters getParameter() {
        return this.parameter;
    }

    public byte[] getI() {
        return this.I;
    }

    public int getQ() {
        return this.q;
    }

    public byte[] getMasterSecret() {
        return this.masterSecret;
    }
}
