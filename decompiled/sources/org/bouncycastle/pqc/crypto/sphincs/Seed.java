package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/Seed.class */
class Seed {
    Seed() {
    }

    static void get_seed(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, Tree.leafaddr leafaddrVar) {
        byte[] bArr3 = new byte[40];
        for (int i2 = 0; i2 < 32; i2++) {
            bArr3[i2] = bArr2[i2];
        }
        Pack.longToLittleEndian(((long) leafaddrVar.level) | (leafaddrVar.subtree << 4) | (leafaddrVar.subleaf << 59), bArr3, 32);
        hashFunctions.varlen_hash(bArr, i, bArr3, bArr3.length);
    }

    static void prg(byte[] bArr, int i, long j, byte[] bArr2, int i2) {
        ChaChaEngine chaChaEngine = new ChaChaEngine(12);
        chaChaEngine.init(true, new ParametersWithIV(new KeyParameter(bArr2, i2, 32), new byte[8]));
        chaChaEngine.processBytes(bArr, i, (int) j, bArr, i);
    }
}
