package org.bouncycastle.pqc.crypto.gemss;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/SecretKeyHFE.class */
class SecretKeyHFE {
    complete_sparse_monic_gf2nx F_struct = new complete_sparse_monic_gf2nx();
    public Pointer F_HFEv;
    public Pointer S;
    public Pointer T;
    public Pointer sk_uncomp;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/SecretKeyHFE$complete_sparse_monic_gf2nx.class */
    static class complete_sparse_monic_gf2nx {
        public Pointer poly;
        public int[] L;
    }

    public SecretKeyHFE(GeMSSEngine geMSSEngine) {
        this.F_struct.L = new int[geMSSEngine.NB_COEFS_HFEPOLY];
    }
}
