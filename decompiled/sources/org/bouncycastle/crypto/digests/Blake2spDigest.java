package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Blake2spDigest.class */
public class Blake2spDigest implements ExtendedDigest {
    private int digestLength;
    private int fanout;
    private int depth;
    private long innerHashLength;
    private Blake2sDigest root;
    private byte[] buffer;
    private byte[] param;
    private int bufferPos = 0;
    private int keyLength = 0;
    private int nodeOffset = 0;
    private Blake2sDigest[] S = new Blake2sDigest[8];
    private byte[] salt = null;
    private byte[] key = null;
    private final int BLAKE2S_BLOCKBYTES = 64;
    private final int BLAKE2S_KEYBYTES = 32;
    private final int BLAKE2S_OUTBYTES = 32;
    private final int PARALLELISM_DEGREE = 8;
    private final byte[] singleByte = new byte[1];

    public Blake2spDigest(byte[] bArr) {
        this.buffer = null;
        this.param = null;
        this.param = new byte[32];
        this.buffer = new byte[512];
        init(bArr);
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "BLAKE2sp";
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
        int i4 = 512 - i3;
        if (i3 != 0 && i2 >= i4) {
            System.arraycopy(bArr, i, this.buffer, i3, i4);
            for (int i5 = 0; i5 < 8; i5++) {
                this.S[i5].update(this.buffer, i5 * 64, 64);
            }
            i += i4;
            i2 -= i4;
            i3 = 0;
        }
        for (int i6 = 0; i6 < 8; i6++) {
            int i7 = i + (i6 * 64);
            for (int i8 = i2; i8 >= 512; i8 -= 512) {
                this.S[i6].update(bArr, i7, 64);
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
        byte[][] bArr2 = new byte[8][32];
        for (int i2 = 0; i2 < 8; i2++) {
            if (this.bufferPos > i2 * 64) {
                int i3 = this.bufferPos - (i2 * 64);
                if (i3 > 64) {
                    i3 = 64;
                }
                this.S[i2].update(this.buffer, i2 * 64, i3);
            }
            this.S[i2].doFinal(bArr2[i2], 0);
        }
        for (int i4 = 0; i4 < 8; i4++) {
            this.root.update(bArr2[i4], 0, 32);
        }
        int iDoFinal = this.root.doFinal(bArr, i);
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.bufferPos = 0;
        this.digestLength = 32;
        this.root.reset();
        for (int i = 0; i < 8; i++) {
            this.S[i].reset();
        }
        this.root.setAsLastNode();
        this.S[7].setAsLastNode();
        if (this.key != null) {
            byte[] bArr = new byte[64];
            System.arraycopy(this.key, 0, bArr, 0, this.keyLength);
            for (int i2 = 0; i2 < 8; i2++) {
                this.S[i2].update(bArr, 0, 64);
            }
        }
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }

    private void init(byte[] bArr) {
        if (bArr != null && bArr.length > 0) {
            this.keyLength = bArr.length;
            if (this.keyLength > 32) {
                throw new IllegalArgumentException("Keys > 32 bytes are not supported");
            }
            this.key = Arrays.clone(bArr);
        }
        this.bufferPos = 0;
        this.digestLength = 32;
        this.fanout = 8;
        this.depth = 2;
        this.innerHashLength = 32L;
        this.param[0] = (byte) this.digestLength;
        this.param[1] = (byte) this.keyLength;
        this.param[2] = (byte) this.fanout;
        this.param[3] = (byte) this.depth;
        Pack.intToLittleEndian(0, this.param, 8);
        this.param[14] = 1;
        this.param[15] = (byte) this.innerHashLength;
        this.root = new Blake2sDigest((byte[]) null, this.param);
        Pack.intToLittleEndian(this.nodeOffset, this.param, 8);
        this.param[14] = 0;
        for (int i = 0; i < 8; i++) {
            Pack.intToLittleEndian(i, this.param, 8);
            this.S[i] = new Blake2sDigest((byte[]) null, this.param);
        }
        this.root.setAsLastNode();
        this.S[7].setAsLastNode();
        if (bArr == null || this.keyLength <= 0) {
            return;
        }
        byte[] bArr2 = new byte[64];
        System.arraycopy(bArr, 0, bArr2, 0, this.keyLength);
        for (int i2 = 0; i2 < 8; i2++) {
            this.S[i2].update(bArr2, 0, 64);
        }
    }
}
