package org.bouncycastle.pqc.crypto.sphincsplus;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/NodeEntry.class */
class NodeEntry {
    final byte[] nodeValue;
    final int nodeHeight;

    NodeEntry(byte[] bArr, int i) {
        this.nodeValue = bArr;
        this.nodeHeight = i;
    }
}
