package org.spongycastle.crypto.modes.gcm;

import java.util.Vector;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/gcm/Tables1kGCMExponentiator.class */
public class Tables1kGCMExponentiator implements GCMExponentiator {
    private Vector lookupPowX2;

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void init(byte[] x) {
        int[] y = GCMUtil.asInts(x);
        if (this.lookupPowX2 != null && Arrays.areEqual(y, (int[]) this.lookupPowX2.elementAt(0))) {
            return;
        }
        this.lookupPowX2 = new Vector(8);
        this.lookupPowX2.addElement(y);
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void exponentiateX(long pow, byte[] output) {
        int[] y = GCMUtil.oneAsInts();
        int bit = 0;
        while (pow > 0) {
            if ((pow & 1) != 0) {
                ensureAvailable(bit);
                GCMUtil.multiply(y, (int[]) this.lookupPowX2.elementAt(bit));
            }
            bit++;
            pow >>>= 1;
        }
        GCMUtil.asBytes(y, output);
    }

    private void ensureAvailable(int bit) {
        int count = this.lookupPowX2.size();
        if (count <= bit) {
            int[] tmp = (int[]) this.lookupPowX2.elementAt(count - 1);
            do {
                tmp = Arrays.clone(tmp);
                GCMUtil.multiply(tmp, tmp);
                this.lookupPowX2.addElement(tmp);
                count++;
            } while (count <= bit);
        }
    }
}
