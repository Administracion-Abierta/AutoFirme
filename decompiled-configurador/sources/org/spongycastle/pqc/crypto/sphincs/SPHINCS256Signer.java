package org.spongycastle.pqc.crypto.sphincs;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.pqc.crypto.MessageSigner;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/sphincs/SPHINCS256Signer.class */
public class SPHINCS256Signer implements MessageSigner {
    private final HashFunctions hashFunctions;
    private byte[] keyData;

    public SPHINCS256Signer(Digest nDigest, Digest twoNDigest) {
        if (nDigest.getDigestSize() != 32) {
            throw new IllegalArgumentException("n-digest needs to produce 32 bytes of output");
        }
        if (twoNDigest.getDigestSize() != 64) {
            throw new IllegalArgumentException("2n-digest needs to produce 64 bytes of output");
        }
        this.hashFunctions = new HashFunctions(nDigest, twoNDigest);
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            this.keyData = ((SPHINCSPrivateKeyParameters) param).getKeyData();
        } else {
            this.keyData = ((SPHINCSPublicKeyParameters) param).getKeyData();
        }
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] message) {
        return crypto_sign(this.hashFunctions, message, this.keyData);
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] message, byte[] signature) {
        return verify(this.hashFunctions, message, signature, this.keyData);
    }

    static void validate_authpath(HashFunctions hs, byte[] root, byte[] leaf, int leafidx, byte[] authpath, int auOff, byte[] masks, int height) {
        byte[] buffer = new byte[64];
        if ((leafidx & 1) != 0) {
            for (int j = 0; j < 32; j++) {
                buffer[32 + j] = leaf[j];
            }
            for (int j2 = 0; j2 < 32; j2++) {
                buffer[j2] = authpath[auOff + j2];
            }
        } else {
            for (int j3 = 0; j3 < 32; j3++) {
                buffer[j3] = leaf[j3];
            }
            for (int j4 = 0; j4 < 32; j4++) {
                buffer[32 + j4] = authpath[auOff + j4];
            }
        }
        int authOff = auOff + 32;
        for (int i = 0; i < height - 1; i++) {
            leafidx >>>= 1;
            if ((leafidx & 1) != 0) {
                hs.hash_2n_n_mask(buffer, 32, buffer, 0, masks, 2 * (7 + i) * 32);
                for (int j5 = 0; j5 < 32; j5++) {
                    buffer[j5] = authpath[authOff + j5];
                }
            } else {
                hs.hash_2n_n_mask(buffer, 0, buffer, 0, masks, 2 * (7 + i) * 32);
                for (int j6 = 0; j6 < 32; j6++) {
                    buffer[j6 + 32] = authpath[authOff + j6];
                }
            }
            authOff += 32;
        }
        hs.hash_2n_n_mask(root, 0, buffer, 0, masks, 2 * ((7 + height) - 1) * 32);
    }

    static void compute_authpath_wots(HashFunctions hs, byte[] root, byte[] authpath, int authOff, Tree.leafaddr a, byte[] sk, byte[] masks, int height) {
        Tree.leafaddr ta = new Tree.leafaddr(a);
        byte[] tree = new byte[2048];
        byte[] seed = new byte[1024];
        byte[] pk = new byte[68608];
        ta.subleaf = 0L;
        while (ta.subleaf < 32) {
            Seed.get_seed(hs, seed, (int) (ta.subleaf * 32), sk, ta);
            ta.subleaf++;
        }
        Wots w = new Wots();
        ta.subleaf = 0L;
        while (ta.subleaf < 32) {
            w.wots_pkgen(hs, pk, (int) (ta.subleaf * 67 * 32), seed, (int) (ta.subleaf * 32), masks, 0);
            ta.subleaf++;
        }
        ta.subleaf = 0L;
        while (ta.subleaf < 32) {
            Tree.l_tree(hs, tree, (int) (1024 + (ta.subleaf * 32)), pk, (int) (ta.subleaf * 67 * 32), masks, 0);
            ta.subleaf++;
        }
        int level = 0;
        int i = 32;
        while (true) {
            int i2 = i;
            if (i2 <= 0) {
                break;
            }
            for (int j = 0; j < i2; j += 2) {
                hs.hash_2n_n_mask(tree, ((i2 >>> 1) * 32) + ((j >>> 1) * 32), tree, (i2 * 32) + (j * 32), masks, 2 * (7 + level) * 32);
            }
            level++;
            i = i2 >>> 1;
        }
        int idx = (int) a.subleaf;
        for (int i3 = 0; i3 < height; i3++) {
            System.arraycopy(tree, ((32 >>> i3) * 32) + (((idx >>> i3) ^ 1) * 32), authpath, authOff + (i3 * 32), 32);
        }
        System.arraycopy(tree, 32, root, 0, 32);
    }

    byte[] crypto_sign(HashFunctions hs, byte[] m, byte[] sk) {
        byte[] sm = new byte[41000];
        byte[] R = new byte[32];
        byte[] m_h = new byte[64];
        long[] rnd = new long[8];
        byte[] root = new byte[32];
        byte[] seed = new byte[32];
        byte[] masks = new byte[1024];
        byte[] tsk = new byte[1088];
        for (int i = 0; i < 1088; i++) {
            tsk[i] = sk[i];
        }
        System.arraycopy(tsk, 1056, sm, 40968, 32);
        Digest d = hs.getMessageHash();
        byte[] bRnd = new byte[d.getDigestSize()];
        d.update(sm, 40968, 32);
        d.update(m, 0, m.length);
        d.doFinal(bRnd, 0);
        zerobytes(sm, 40968, 32);
        for (int j = 0; j != rnd.length; j++) {
            rnd[j] = Pack.littleEndianToLong(bRnd, j * 8);
        }
        long leafidx = rnd[0] & 1152921504606846975L;
        System.arraycopy(bRnd, 16, R, 0, 32);
        System.arraycopy(R, 0, sm, 39912, 32);
        Tree.leafaddr b = new Tree.leafaddr();
        b.level = 11;
        b.subtree = 0L;
        b.subleaf = 0L;
        int pk = 39912 + 32;
        System.arraycopy(tsk, 32, sm, pk, 1024);
        Tree.treehash(hs, sm, pk + 1024, 5, tsk, b, sm, pk);
        Digest d2 = hs.getMessageHash();
        d2.update(sm, 39912, 1088);
        d2.update(m, 0, m.length);
        d2.doFinal(m_h, 0);
        Tree.leafaddr a = new Tree.leafaddr();
        a.level = 12;
        a.subleaf = (int) (leafidx & 31);
        a.subtree = leafidx >>> 5;
        for (int i2 = 0; i2 < 32; i2++) {
            sm[i2] = R[i2];
        }
        System.arraycopy(tsk, 32, masks, 0, 1024);
        for (int i3 = 0; i3 < 8; i3++) {
            sm[32 + i3] = (byte) ((leafidx >>> (8 * i3)) & 255);
        }
        int smOff = 32 + 8;
        Seed.get_seed(hs, seed, 0, tsk, a);
        new Horst();
        int horst_sigbytes = Horst.horst_sign(hs, sm, smOff, root, seed, masks, m_h);
        int smOff2 = smOff + horst_sigbytes;
        Wots w = new Wots();
        for (int i4 = 0; i4 < 12; i4++) {
            a.level = i4;
            Seed.get_seed(hs, seed, 0, tsk, a);
            w.wots_sign(hs, sm, smOff2, root, seed, masks);
            int smOff3 = smOff2 + 2144;
            compute_authpath_wots(hs, root, sm, smOff3, a, tsk, masks, 5);
            smOff2 = smOff3 + CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256;
            a.subleaf = (int) (a.subtree & 31);
            a.subtree >>>= 5;
        }
        zerobytes(tsk, 0, 1088);
        return sm;
    }

    private void zerobytes(byte[] tsk, int off, int cryptoSecretkeybytes) {
        for (int i = 0; i != cryptoSecretkeybytes; i++) {
            tsk[off + i] = 0;
        }
    }

    boolean verify(HashFunctions hs, byte[] m, byte[] sm, byte[] pk) {
        int smlen = sm.length;
        long leafidx = 0;
        byte[] wots_pk = new byte[2144];
        byte[] pkhash = new byte[32];
        byte[] root = new byte[32];
        byte[] sig = new byte[41000];
        byte[] tpk = new byte[1056];
        if (smlen != 41000) {
            throw new IllegalArgumentException("signature wrong size");
        }
        byte[] m_h = new byte[64];
        for (int i = 0; i < 1056; i++) {
            tpk[i] = pk[i];
        }
        byte[] R = new byte[32];
        for (int i2 = 0; i2 < 32; i2++) {
            R[i2] = sm[i2];
        }
        System.arraycopy(sm, 0, sig, 0, 41000);
        Digest mHash = hs.getMessageHash();
        mHash.update(R, 0, 32);
        mHash.update(tpk, 0, 1056);
        mHash.update(m, 0, m.length);
        mHash.doFinal(m_h, 0);
        int sigp = 0 + 32;
        int smlen2 = smlen - 32;
        for (int i3 = 0; i3 < 8; i3++) {
            leafidx ^= ((long) (sig[sigp + i3] & 255)) << (8 * i3);
        }
        new Horst();
        Horst.horst_verify(hs, root, sig, sigp + 8, tpk, m_h);
        int sigp2 = sigp + 8 + 13312;
        int smlen3 = (smlen2 - 8) - 13312;
        Wots w = new Wots();
        for (int i4 = 0; i4 < 12; i4++) {
            w.wots_verify(hs, wots_pk, sig, sigp2, root, tpk);
            int sigp3 = sigp2 + 2144;
            Tree.l_tree(hs, pkhash, 0, wots_pk, 0, tpk, 0);
            validate_authpath(hs, root, pkhash, (int) (leafidx & 31), sig, sigp3, tpk, 5);
            leafidx >>= 5;
            sigp2 = sigp3 + CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256;
            smlen3 = (smlen3 - 2144) - 160;
        }
        boolean verified = true;
        for (int i5 = 0; i5 < 32; i5++) {
            if (root[i5] != tpk[i5 + 1024]) {
                verified = false;
            }
        }
        return verified;
    }
}
