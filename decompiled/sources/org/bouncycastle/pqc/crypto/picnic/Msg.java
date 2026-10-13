package org.bouncycastle.pqc.crypto.picnic;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Msg.class */
class Msg {
    byte[][] msgs;
    int pos = 0;
    int unopened = -1;

    public Msg(PicnicEngine picnicEngine) {
        this.msgs = new byte[picnicEngine.numMPCParties][picnicEngine.andSizeBytes];
    }
}
