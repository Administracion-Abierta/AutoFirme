package org.bouncycastle.pqc.crypto.sphincsplus;

import java.util.LinkedList;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/HT.class */
class HT {
    private final byte[] skSeed;
    private final byte[] pkSeed;
    SPHINCSPlusEngine engine;
    WotsPlus wots;
    final byte[] htPubKey;

    public HT(SPHINCSPlusEngine sPHINCSPlusEngine, byte[] bArr, byte[] bArr2) {
        this.skSeed = bArr;
        this.pkSeed = bArr2;
        this.engine = sPHINCSPlusEngine;
        this.wots = new WotsPlus(sPHINCSPlusEngine);
        ADRS adrs = new ADRS();
        adrs.setLayerAddress(sPHINCSPlusEngine.D - 1);
        adrs.setTreeAddress(0L);
        if (bArr != null) {
            this.htPubKey = xmss_PKgen(bArr, bArr2, adrs);
        } else {
            this.htPubKey = null;
        }
    }

    byte[] sign(byte[] bArr, long j, int i) {
        ADRS adrs = new ADRS();
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        SIG_XMSS sig_xmssXmss_sign = xmss_sign(bArr, this.skSeed, i, this.pkSeed, adrs);
        SIG_XMSS[] sig_xmssArr = new SIG_XMSS[this.engine.D];
        sig_xmssArr[0] = sig_xmssXmss_sign;
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        byte[] bArrXmss_pkFromSig = xmss_pkFromSig(i, sig_xmssXmss_sign, bArr, this.pkSeed, adrs);
        for (int i2 = 1; i2 < this.engine.D; i2++) {
            int i3 = (int) (j & ((long) ((1 << this.engine.H_PRIME) - 1)));
            j >>>= this.engine.H_PRIME;
            adrs.setLayerAddress(i2);
            adrs.setTreeAddress(j);
            SIG_XMSS sig_xmssXmss_sign2 = xmss_sign(bArrXmss_pkFromSig, this.skSeed, i3, this.pkSeed, adrs);
            sig_xmssArr[i2] = sig_xmssXmss_sign2;
            if (i2 < this.engine.D - 1) {
                bArrXmss_pkFromSig = xmss_pkFromSig(i3, sig_xmssXmss_sign2, bArrXmss_pkFromSig, this.pkSeed, adrs);
            }
        }
        byte[][] bArr2 = new byte[sig_xmssArr.length][];
        for (int i4 = 0; i4 != bArr2.length; i4++) {
            bArr2[i4] = Arrays.concatenate(sig_xmssArr[i4].sig, Arrays.concatenate(sig_xmssArr[i4].auth));
        }
        return Arrays.concatenate(bArr2);
    }

    byte[] xmss_PKgen(byte[] bArr, byte[] bArr2, ADRS adrs) {
        return treehash(bArr, 0, this.engine.H_PRIME, bArr2, adrs);
    }

    byte[] xmss_pkFromSig(int i, SIG_XMSS sig_xmss, byte[] bArr, byte[] bArr2, ADRS adrs) {
        byte[] bArrH;
        ADRS adrs2 = new ADRS(adrs);
        adrs2.setTypeAndClear(0);
        adrs2.setKeyPairAddress(i);
        byte[] wOTSSig = sig_xmss.getWOTSSig();
        byte[][] xmssauth = sig_xmss.getXMSSAUTH();
        byte[] bArrPkFromSig = this.wots.pkFromSig(wOTSSig, bArr, bArr2, adrs2);
        adrs2.setTypeAndClear(2);
        adrs2.setTreeIndex(i);
        for (int i2 = 0; i2 < this.engine.H_PRIME; i2++) {
            adrs2.setTreeHeight(i2 + 1);
            if ((i / (1 << i2)) % 2 == 0) {
                adrs2.setTreeIndex(adrs2.getTreeIndex() / 2);
                bArrH = this.engine.H(bArr2, adrs2, bArrPkFromSig, xmssauth[i2]);
            } else {
                adrs2.setTreeIndex((adrs2.getTreeIndex() - 1) / 2);
                bArrH = this.engine.H(bArr2, adrs2, xmssauth[i2], bArrPkFromSig);
            }
            bArrPkFromSig = bArrH;
        }
        return bArrPkFromSig;
    }

    SIG_XMSS xmss_sign(byte[] bArr, byte[] bArr2, int i, byte[] bArr3, ADRS adrs) {
        byte[][] bArr4 = new byte[this.engine.H_PRIME][];
        ADRS adrs2 = new ADRS(adrs);
        adrs2.setTypeAndClear(2);
        adrs2.setLayerAddress(adrs.getLayerAddress());
        adrs2.setTreeAddress(adrs.getTreeAddress());
        for (int i2 = 0; i2 < this.engine.H_PRIME; i2++) {
            bArr4[i2] = treehash(bArr2, ((i >>> i2) ^ 1) << i2, i2, bArr3, adrs2);
        }
        ADRS adrs3 = new ADRS(adrs);
        adrs3.setTypeAndClear(0);
        adrs3.setKeyPairAddress(i);
        return new SIG_XMSS(this.wots.sign(bArr, bArr2, bArr3, adrs3), bArr4);
    }

    byte[] treehash(byte[] bArr, int i, int i2, byte[] bArr2, ADRS adrs) {
        if (((i >>> i2) << i2) != i) {
            return null;
        }
        LinkedList linkedList = new LinkedList();
        ADRS adrs2 = new ADRS(adrs);
        for (int i3 = 0; i3 < (1 << i2); i3++) {
            adrs2.setTypeAndClear(0);
            adrs2.setKeyPairAddress(i + i3);
            byte[] bArrPkGen = this.wots.pkGen(bArr, bArr2, adrs2);
            adrs2.setTypeAndClear(2);
            adrs2.setTreeHeight(1);
            adrs2.setTreeIndex(i + i3);
            int i4 = 1;
            int i5 = i + i3;
            while (!linkedList.isEmpty() && ((NodeEntry) linkedList.get(0)).nodeHeight == i4) {
                i5 = (i5 - 1) / 2;
                adrs2.setTreeIndex(i5);
                bArrPkGen = this.engine.H(bArr2, adrs2, ((NodeEntry) linkedList.remove(0)).nodeValue, bArrPkGen);
                i4++;
                adrs2.setTreeHeight(i4);
            }
            linkedList.add(0, new NodeEntry(bArrPkGen, i4));
        }
        return ((NodeEntry) linkedList.get(0)).nodeValue;
    }

    public boolean verify(byte[] bArr, SIG_XMSS[] sig_xmssArr, byte[] bArr2, long j, int i, byte[] bArr3) {
        ADRS adrs = new ADRS();
        SIG_XMSS sig_xmss = sig_xmssArr[0];
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        byte[] bArrXmss_pkFromSig = xmss_pkFromSig(i, sig_xmss, bArr, bArr2, adrs);
        for (int i2 = 1; i2 < this.engine.D; i2++) {
            int i3 = (int) (j & ((long) ((1 << this.engine.H_PRIME) - 1)));
            j >>>= this.engine.H_PRIME;
            SIG_XMSS sig_xmss2 = sig_xmssArr[i2];
            adrs.setLayerAddress(i2);
            adrs.setTreeAddress(j);
            bArrXmss_pkFromSig = xmss_pkFromSig(i3, sig_xmss2, bArrXmss_pkFromSig, bArr2, adrs);
        }
        return Arrays.areEqual(bArr3, bArrXmss_pkFromSig);
    }
}
