package org.bouncycastle.crypto.modes.gcm;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/gcm/Tables1kGCMExponentiator.class */
public class Tables1kGCMExponentiator implements GCMExponentiator {
    private List lookupPowX2;

    @Override // org.bouncycastle.crypto.modes.gcm.GCMExponentiator
    public void init(byte[] bArr) {
        long[] jArrAsLongs = GCMUtil.asLongs(bArr);
        if (this.lookupPowX2 == null || 0 == GCMUtil.areEqual(jArrAsLongs, (long[]) this.lookupPowX2.get(0))) {
            this.lookupPowX2 = new ArrayList(8);
            this.lookupPowX2.add(jArrAsLongs);
        }
    }

    @Override // org.bouncycastle.crypto.modes.gcm.GCMExponentiator
    public void exponentiateX(long j, byte[] bArr) {
        long[] jArrOneAsLongs = GCMUtil.oneAsLongs();
        int i = 0;
        while (j > 0) {
            if ((j & 1) != 0) {
                GCMUtil.multiply(jArrOneAsLongs, getPowX2(i));
            }
            i++;
            j >>>= 1;
        }
        GCMUtil.asBytes(jArrOneAsLongs, bArr);
    }

    private long[] getPowX2(int i) {
        int size = this.lookupPowX2.size() - 1;
        if (size < i) {
            long[] jArr = (long[]) this.lookupPowX2.get(size);
            do {
                long[] jArr2 = new long[2];
                GCMUtil.square(jArr, jArr2);
                this.lookupPowX2.add(jArr2);
                jArr = jArr2;
                size++;
            } while (size < i);
        }
        return (long[]) this.lookupPowX2.get(i);
    }
}
