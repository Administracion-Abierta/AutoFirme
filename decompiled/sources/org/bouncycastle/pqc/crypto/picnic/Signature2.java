package org.bouncycastle.pqc.crypto.picnic;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Signature2.class */
class Signature2 {
    byte[] salt = new byte[32];
    byte[] iSeedInfo;
    int iSeedInfoLen;
    byte[] cvInfo;
    int cvInfoLen;
    byte[] challengeHash;
    int[] challengeC;
    int[] challengeP;
    Proof2[] proofs;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Signature2$Proof2.class */
    public static class Proof2 {
        byte[] seedInfo = null;
        int seedInfoLen = 0;
        byte[] aux;
        byte[] C;
        byte[] input;
        byte[] msgs;

        public Proof2(PicnicEngine picnicEngine) {
            this.C = new byte[picnicEngine.digestSizeBytes];
            this.input = new byte[picnicEngine.stateSizeBytes];
            this.aux = new byte[picnicEngine.andSizeBytes];
            this.msgs = new byte[picnicEngine.andSizeBytes];
        }
    }

    public Signature2(PicnicEngine picnicEngine) {
        this.challengeHash = new byte[picnicEngine.digestSizeBytes];
        this.challengeC = new int[picnicEngine.numOpenedRounds];
        this.challengeP = new int[picnicEngine.numOpenedRounds];
        this.proofs = new Proof2[picnicEngine.numMPCRounds];
    }
}
