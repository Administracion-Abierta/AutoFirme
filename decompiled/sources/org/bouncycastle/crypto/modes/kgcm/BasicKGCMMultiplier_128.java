package org.bouncycastle.crypto.modes.kgcm;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/kgcm/BasicKGCMMultiplier_128.class */
public class BasicKGCMMultiplier_128 implements KGCMMultiplier {
    private final long[] H = new long[2];

    @Override // org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier
    public void init(long[] jArr) {
        KGCMUtil_128.copy(jArr, this.H);
    }

    @Override // org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier
    public void multiplyH(long[] jArr) {
        KGCMUtil_128.multiply(jArr, this.H, jArr);
    }
}
