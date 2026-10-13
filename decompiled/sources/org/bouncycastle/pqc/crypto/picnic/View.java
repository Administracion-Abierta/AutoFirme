package org.bouncycastle.pqc.crypto.picnic;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/View.class */
class View {
    final int[] inputShare;
    final byte[] communicatedBits;
    final int[] outputShare;

    public View(PicnicEngine picnicEngine) {
        this.inputShare = new int[picnicEngine.stateSizeWords];
        this.communicatedBits = new byte[picnicEngine.andSizeBytes];
        this.outputShare = new int[picnicEngine.stateSizeWords];
    }
}
