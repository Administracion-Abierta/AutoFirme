package org.bouncycastle.pqc.legacy.crypto.ntru;

import java.nio.ByteBuffer;
import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/NTRUSignerPrng.class */
public class NTRUSignerPrng {
    private int counter = 0;
    private byte[] seed;
    private Digest hashAlg;

    NTRUSignerPrng(byte[] bArr, Digest digest) {
        this.seed = bArr;
        this.hashAlg = digest;
    }

    byte[] nextBytes(int i) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i);
        while (byteBufferAllocate.hasRemaining()) {
            ByteBuffer byteBufferAllocate2 = ByteBuffer.allocate(this.seed.length + 4);
            byteBufferAllocate2.put(this.seed);
            byteBufferAllocate2.putInt(this.counter);
            byte[] bArrArray = byteBufferAllocate2.array();
            byte[] bArr = new byte[this.hashAlg.getDigestSize()];
            this.hashAlg.update(bArrArray, 0, bArrArray.length);
            this.hashAlg.doFinal(bArr, 0);
            if (byteBufferAllocate.remaining() < bArr.length) {
                byteBufferAllocate.put(bArr, 0, byteBufferAllocate.remaining());
            } else {
                byteBufferAllocate.put(bArr);
            }
            this.counter++;
        }
        return byteBufferAllocate.array();
    }
}
