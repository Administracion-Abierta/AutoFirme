package org.bouncycastle.tsp.ers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/IndexedHash.class */
class IndexedHash {
    final int order;
    final byte[] digest;

    IndexedHash(int i, byte[] bArr) {
        this.order = i;
        this.digest = bArr;
    }
}
