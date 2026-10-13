package org.bouncycastle.pqc.crypto.picnic;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Signature.class */
class Signature {
    final byte[] challengeBits;
    final byte[] salt = new byte[32];
    final Proof[] proofs;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Signature$Proof.class */
    public static class Proof {
        final byte[] seed1;
        final byte[] seed2;
        final int[] inputShare;
        final byte[] communicatedBits;
        final byte[] view3Commitment;
        final byte[] view3UnruhG;

        Proof(PicnicEngine picnicEngine) {
            this.seed1 = new byte[picnicEngine.seedSizeBytes];
            this.seed2 = new byte[picnicEngine.seedSizeBytes];
            this.inputShare = new int[picnicEngine.stateSizeWords];
            this.communicatedBits = new byte[picnicEngine.andSizeBytes];
            this.view3Commitment = new byte[picnicEngine.digestSizeBytes];
            if (picnicEngine.UnruhGWithInputBytes > 0) {
                this.view3UnruhG = new byte[picnicEngine.UnruhGWithInputBytes];
            } else {
                this.view3UnruhG = null;
            }
        }
    }

    Signature(PicnicEngine picnicEngine) {
        this.challengeBits = new byte[Utils.numBytes(picnicEngine.numMPCRounds * 2)];
        this.proofs = new Proof[picnicEngine.numMPCRounds];
        for (int i = 0; i < this.proofs.length; i++) {
            this.proofs[i] = new Proof(picnicEngine);
        }
    }
}
