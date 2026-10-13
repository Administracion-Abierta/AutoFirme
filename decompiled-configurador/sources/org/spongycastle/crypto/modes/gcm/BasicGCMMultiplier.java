package org.spongycastle.crypto.modes.gcm;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/gcm/BasicGCMMultiplier.class */
public class BasicGCMMultiplier implements GCMMultiplier {
    private int[] H;

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        this.H = GCMUtil.asInts(H);
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        int[] t = GCMUtil.asInts(x);
        GCMUtil.multiply(t, this.H);
        GCMUtil.asBytes(t, x);
    }
}
