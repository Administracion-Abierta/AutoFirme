package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/gcm/BasicGCMExponentiator.class */
public class BasicGCMExponentiator implements GCMExponentiator {
    private int[] x;

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void init(byte[] x) {
        this.x = GCMUtil.asInts(x);
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void exponentiateX(long pow, byte[] output) {
        int[] y = GCMUtil.oneAsInts();
        if (pow > 0) {
            int[] powX = Arrays.clone(this.x);
            do {
                if ((pow & 1) != 0) {
                    GCMUtil.multiply(y, powX);
                }
                GCMUtil.multiply(powX, powX);
                pow >>>= 1;
            } while (pow > 0);
        }
        GCMUtil.asBytes(y, output);
    }
}
