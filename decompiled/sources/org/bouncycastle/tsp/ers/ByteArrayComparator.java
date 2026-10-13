package org.bouncycastle.tsp.ers;

import java.util.Comparator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ByteArrayComparator.class */
class ByteArrayComparator implements Comparator {
    ByteArrayComparator() {
    }

    @Override // java.util.Comparator
    public int compare(Object obj, Object obj2) {
        byte[] bArr = (byte[]) obj;
        byte[] bArr2 = (byte[]) obj2;
        for (int i = 0; i < bArr.length && i < bArr2.length; i++) {
            int i2 = bArr[i] & 255;
            int i3 = bArr2[i] & 255;
            if (i2 != i3) {
                return i2 - i3;
            }
        }
        return bArr.length - bArr2.length;
    }
}
