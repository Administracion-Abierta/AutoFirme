package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Blake2bpDigest.class */
public class Blake2bpDigest implements ExtendedDigest {
    private int digestLength;
    private int fanout;
    private int depth;
    private long innerHashLength;
    private Blake2bDigest root;
    private byte[] buffer;
    private byte[] param;
    private int bufferPos = 0;
    private int keyLength = 0;
    private int nodeOffset = 0;
    private Blake2bDigest[] S = new Blake2bDigest[4];
    private byte[] salt = null;
    private byte[] key = null;
    private final int BLAKE2B_BLOCKBYTES = 128;
    private final int BLAKE2B_KEYBYTES = 64;
    private final int BLAKE2B_OUTBYTES = 64;
    private final int PARALLELISM_DEGREE = 4;
    private final byte[] singleByte = new byte[1];

    public Blake2bpDigest(byte[] bArr) {
        this.buffer = null;
        this.param = null;
        this.param = new byte[64];
        this.buffer = new byte[512];
        init(bArr);
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "BLAKE2bp";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.singleByte[0] = b;
        update(this.singleByte, 0, 1);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        int i3 = this.bufferPos;
        int i4 = 1024 - i3;
        if (i3 != 0 && i2 >= i4) {
            System.arraycopy(bArr, i, this.buffer, i3, i4);
            for (int i5 = 0; i5 < 4; i5++) {
                this.S[i5].update(this.buffer, i5 * 128, 128);
            }
            i += i4;
            i2 -= i4;
            i3 = 0;
        }
        for (int i6 = 0; i6 < 4; i6++) {
            int i7 = i + (i6 * 128);
            for (int i8 = i2; i8 >= 512; i8 -= 512) {
                this.S[i6].update(bArr, i7, 128);
                i7 += 512;
            }
        }
        int i9 = i + (i2 - (i2 % 512));
        int i10 = i2 % 512;
        if (i10 > 0) {
            System.arraycopy(bArr, i9, this.buffer, i3, i10);
        }
        this.bufferPos = i3 + i10;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        byte[][] bArr2 = new byte[4][64];
        for (int i2 = 0; i2 < 4; i2++) {
            if (this.bufferPos > i2 * 128) {
                int i3 = this.bufferPos - (i2 * 128);
                if (i3 > 128) {
                    i3 = 128;
                }
                this.S[i2].update(this.buffer, i2 * 128, i3);
            }
            this.S[i2].doFinal(bArr2[i2], 0);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.root.update(bArr2[i4], 0, 64);
        }
        int iDoFinal = this.root.doFinal(bArr, i);
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.bufferPos = 0;
        this.digestLength = 64;
        this.root.reset();
        for (int i = 0; i < 4; i++) {
            this.S[i].reset();
        }
        this.root.setAsLastNode();
        this.S[3].setAsLastNode();
        if (this.key != null) {
            byte[] bArr = new byte[128];
            System.arraycopy(this.key, 0, bArr, 0, this.keyLength);
            for (int i2 = 0; i2 < 4; i2++) {
                this.S[i2].update(bArr, 0, 128);
            }
        }
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 0;
    }

    private void init(byte[] bArr) {
        if (bArr != null && bArr.length > 0) {
            this.keyLength = bArr.length;
            if (this.keyLength > 64) {
                throw new IllegalArgumentException("Keys > 64 bytes are not supported");
            }
            this.key = Arrays.clone(bArr);
        }
        this.bufferPos = 0;
        this.digestLength = 64;
        this.fanout = 4;
        this.depth = 2;
        this.innerHashLength = 64L;
        this.param[0] = (byte) this.digestLength;
        this.param[1] = (byte) this.keyLength;
        this.param[2] = (byte) this.fanout;
        this.param[3] = (byte) this.depth;
        this.param[16] = 1;
        this.param[17] = (byte) this.innerHashLength;
        this.root = new Blake2bDigest((byte[]) null, this.param);
        Pack.intToLittleEndian(this.nodeOffset, this.param, 8);
        this.param[16] = 0;
        for (int i = 0; i < 4; i++) {
            Pack.intToLittleEndian(i, this.param, 8);
            this.S[i] = new Blake2bDigest((byte[]) null, this.param);
        }
        this.root.setAsLastNode();
        this.S[3].setAsLastNode();
        if (bArr == null || this.keyLength <= 0) {
            return;
        }
        byte[] bArr2 = new byte[128];
        System.arraycopy(bArr, 0, bArr2, 0, this.keyLength);
        for (int i2 = 0; i2 < 4; i2++) {
            this.S[i2].update(bArr2, 0, 128);
        }
    }
}
