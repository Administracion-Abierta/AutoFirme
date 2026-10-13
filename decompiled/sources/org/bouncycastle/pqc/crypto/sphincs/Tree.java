package org.bouncycastle.pqc.crypto.sphincs;

import com.sun.jna.platform.win32.LMErr;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/Tree.class */
class Tree {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/Tree$leafaddr.class */
    static class leafaddr {
        int level;
        long subtree;
        long subleaf;

        public leafaddr() {
        }

        public leafaddr(leafaddr leafaddrVar) {
            this.level = leafaddrVar.level;
            this.subtree = leafaddrVar.subtree;
            this.subleaf = leafaddrVar.subleaf;
        }
    }

    Tree() {
    }

    static void l_tree(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3) {
        int i4;
        int i5 = 67;
        for (int i6 = 0; i6 < 7; i6++) {
            for (int i7 = 0; i7 < (i5 >>> 1); i7++) {
                hashFunctions.hash_2n_n_mask(bArr2, i2 + (i7 * 32), bArr2, i2 + (i7 * 2 * 32), bArr3, i3 + (i6 * 2 * 32));
            }
            if ((i5 & 1) != 0) {
                System.arraycopy(bArr2, i2 + ((i5 - 1) * 32), bArr2, i2 + ((i5 >>> 1) * 32), 32);
                i4 = (i5 >>> 1) + 1;
            } else {
                i4 = i5 >>> 1;
            }
            i5 = i4;
        }
        System.arraycopy(bArr2, i2, bArr, i, 32);
    }

    static void treehash(HashFunctions hashFunctions, byte[] bArr, int i, int i2, byte[] bArr2, leafaddr leafaddrVar, byte[] bArr3, int i3) {
        leafaddr leafaddrVar2 = new leafaddr(leafaddrVar);
        byte[] bArr4 = new byte[(i2 + 1) * 32];
        int[] iArr = new int[i2 + 1];
        int i4 = 0;
        int i5 = (int) (leafaddrVar2.subleaf + ((long) (1 << i2)));
        while (leafaddrVar2.subleaf < i5) {
            gen_leaf_wots(hashFunctions, bArr4, i4 * 32, bArr3, i3, bArr2, leafaddrVar2);
            iArr[i4] = 0;
            i4++;
            while (i4 > 1 && iArr[i4 - 1] == iArr[i4 - 2]) {
                hashFunctions.hash_2n_n_mask(bArr4, (i4 - 2) * 32, bArr4, (i4 - 2) * 32, bArr3, i3 + (2 * (iArr[i4 - 1] + 7) * 32));
                int i6 = i4 - 2;
                iArr[i6] = iArr[i6] + 1;
                i4--;
            }
            leafaddrVar2.subleaf++;
        }
        for (int i7 = 0; i7 < 32; i7++) {
            bArr[i + i7] = bArr4[i7];
        }
    }

    static void gen_leaf_wots(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, leafaddr leafaddrVar) {
        byte[] bArr4 = new byte[32];
        byte[] bArr5 = new byte[LMErr.NERR_DupNameReboot];
        Wots wots = new Wots();
        Seed.get_seed(hashFunctions, bArr4, 0, bArr3, leafaddrVar);
        wots.wots_pkgen(hashFunctions, bArr5, 0, bArr4, 0, bArr2, i2);
        l_tree(hashFunctions, bArr, i, bArr5, 0, bArr2, i2);
    }
}
