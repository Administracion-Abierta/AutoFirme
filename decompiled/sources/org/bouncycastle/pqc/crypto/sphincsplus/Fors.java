package org.bouncycastle.pqc.crypto.sphincsplus;

import java.util.LinkedList;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/Fors.class */
class Fors {
    SPHINCSPlusEngine engine;

    public Fors(SPHINCSPlusEngine sPHINCSPlusEngine) {
        this.engine = sPHINCSPlusEngine;
    }

    byte[] treehash(byte[] bArr, int i, int i2, byte[] bArr2, ADRS adrs) {
        if (((i >>> i2) << i2) != i) {
            return null;
        }
        LinkedList linkedList = new LinkedList();
        ADRS adrs2 = new ADRS(adrs);
        for (int i3 = 0; i3 < (1 << i2); i3++) {
            adrs2.setTypeAndClear(6);
            adrs2.setKeyPairAddress(adrs.getKeyPairAddress());
            adrs2.setTreeHeight(0);
            adrs2.setTreeIndex(i + i3);
            byte[] bArrPRF = this.engine.PRF(bArr2, bArr, adrs2);
            adrs2.changeType(3);
            byte[] bArrF = this.engine.F(bArr2, adrs2, bArrPRF);
            adrs2.setTreeHeight(1);
            int i4 = 1;
            int i5 = i + i3;
            while (!linkedList.isEmpty() && ((NodeEntry) linkedList.get(0)).nodeHeight == i4) {
                i5 = (i5 - 1) / 2;
                adrs2.setTreeIndex(i5);
                bArrF = this.engine.H(bArr2, adrs2, ((NodeEntry) linkedList.remove(0)).nodeValue, bArrF);
                i4++;
                adrs2.setTreeHeight(i4);
            }
            linkedList.add(0, new NodeEntry(bArrF, i4));
        }
        return ((NodeEntry) linkedList.get(0)).nodeValue;
    }

    public SIG_FORS[] sign(byte[] bArr, byte[] bArr2, byte[] bArr3, ADRS adrs) {
        ADRS adrs2 = new ADRS(adrs);
        int[] iArrMessage_to_idxs = message_to_idxs(bArr, this.engine.K, this.engine.A);
        SIG_FORS[] sig_forsArr = new SIG_FORS[this.engine.K];
        int i = this.engine.T;
        for (int i2 = 0; i2 < this.engine.K; i2++) {
            int i3 = iArrMessage_to_idxs[i2];
            adrs2.setTypeAndClear(6);
            adrs2.setKeyPairAddress(adrs.getKeyPairAddress());
            adrs2.setTreeHeight(0);
            adrs2.setTreeIndex((i2 * i) + i3);
            byte[] bArrPRF = this.engine.PRF(bArr3, bArr2, adrs2);
            adrs2.changeType(3);
            byte[][] bArr4 = new byte[this.engine.A][];
            for (int i4 = 0; i4 < this.engine.A; i4++) {
                bArr4[i4] = treehash(bArr2, (i2 * i) + (((i3 / (1 << i4)) ^ 1) * (1 << i4)), i4, bArr3, adrs2);
            }
            sig_forsArr[i2] = new SIG_FORS(bArrPRF, bArr4);
        }
        return sig_forsArr;
    }

    public byte[] pkFromSig(SIG_FORS[] sig_forsArr, byte[] bArr, byte[] bArr2, ADRS adrs) {
        byte[][] bArr3 = new byte[2][];
        byte[][] bArr4 = new byte[this.engine.K][];
        int i = this.engine.T;
        int[] iArrMessage_to_idxs = message_to_idxs(bArr, this.engine.K, this.engine.A);
        for (int i2 = 0; i2 < this.engine.K; i2++) {
            int i3 = iArrMessage_to_idxs[i2];
            byte[] sk = sig_forsArr[i2].getSK();
            adrs.setTreeHeight(0);
            adrs.setTreeIndex((i2 * i) + i3);
            bArr3[0] = this.engine.F(bArr2, adrs, sk);
            byte[][] authPath = sig_forsArr[i2].getAuthPath();
            adrs.setTreeIndex((i2 * i) + i3);
            for (int i4 = 0; i4 < this.engine.A; i4++) {
                adrs.setTreeHeight(i4 + 1);
                if ((i3 / (1 << i4)) % 2 == 0) {
                    adrs.setTreeIndex(adrs.getTreeIndex() / 2);
                    bArr3[1] = this.engine.H(bArr2, adrs, bArr3[0], authPath[i4]);
                } else {
                    adrs.setTreeIndex((adrs.getTreeIndex() - 1) / 2);
                    bArr3[1] = this.engine.H(bArr2, adrs, authPath[i4], bArr3[0]);
                }
                bArr3[0] = bArr3[1];
            }
            bArr4[i2] = bArr3[0];
        }
        ADRS adrs2 = new ADRS(adrs);
        adrs2.setTypeAndClear(4);
        adrs2.setKeyPairAddress(adrs.getKeyPairAddress());
        return this.engine.T_l(bArr2, adrs2, Arrays.concatenate(bArr4));
    }

    static int[] message_to_idxs(byte[] bArr, int i, int i2) {
        int i3 = 0;
        int[] iArr = new int[i];
        for (int i4 = 0; i4 < i; i4++) {
            iArr[i4] = 0;
            for (int i5 = 0; i5 < i2; i5++) {
                int i6 = i4;
                iArr[i6] = iArr[i6] ^ (((bArr[i3 >> 3] >> (i3 & 7)) & 1) << i5);
                i3++;
            }
        }
        return iArr;
    }
}
