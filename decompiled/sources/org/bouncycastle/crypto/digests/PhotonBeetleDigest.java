package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/PhotonBeetleDigest.class */
public class PhotonBeetleDigest implements Digest {
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final int INITIAL_RATE_INBYTES = 16;
    private int RATE_INBYTES = 4;
    private int SQUEEZE_RATE_INBYTES = 16;
    private int STATE_INBYTES = 32;
    private int TAG_INBYTES = 32;
    private int LAST_THREE_BITS_OFFSET = 5;
    private int ROUND = 12;
    private int D = 8;
    private int Dq = 3;
    private int Dr = 7;
    private int DSquare = 64;
    private int S = 4;
    private int S_1 = 3;
    private byte[][] RC = {new byte[]{1, 3, 7, 14, 13, 11, 6, 12, 9, 2, 5, 10}, new byte[]{0, 2, 6, 15, 12, 10, 7, 13, 8, 3, 4, 11}, new byte[]{2, 0, 4, 13, 14, 8, 5, 15, 10, 1, 6, 9}, new byte[]{6, 4, 0, 9, 10, 12, 1, 11, 14, 5, 2, 13}, new byte[]{14, 12, 8, 1, 2, 4, 9, 3, 6, 13, 10, 5}, new byte[]{15, 13, 9, 0, 3, 5, 8, 2, 7, 12, 11, 4}, new byte[]{13, 15, 11, 2, 1, 7, 10, 0, 5, 14, 9, 6}, new byte[]{9, 11, 15, 6, 5, 3, 14, 4, 1, 10, 13, 2}};
    private byte[][] MixColMatrix = {new byte[]{2, 4, 2, 11, 2, 8, 5, 6}, new byte[]{12, 9, 8, 13, 7, 7, 5, 2}, new byte[]{4, 4, 13, 13, 9, 4, 13, 9}, new byte[]{1, 6, 5, 1, 12, 13, 15, 14}, new byte[]{15, 12, 9, 13, 14, 5, 14, 13}, new byte[]{9, 14, 5, 15, 4, 12, 9, 6}, new byte[]{12, 2, 2, 10, 3, 1, 1, 14}, new byte[]{15, 1, 13, 10, 5, 10, 2, 3}};
    private byte[] sbox = {12, 5, 6, 11, 9, 0, 10, 13, 3, 14, 15, 8, 4, 7, 1, 2};
    private byte[] state = new byte[this.STATE_INBYTES];
    private byte[][] state_2d = new byte[this.D][this.D];

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Photon-Beetle Hash";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.TAG_INBYTES;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.buffer.write(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        this.buffer.write(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (32 + i > bArr.length) {
            throw new OutputLengthException("output buffer is too short");
        }
        byte[] byteArray = this.buffer.toByteArray();
        int length = byteArray.length;
        if (length == 0) {
            byte[] bArr2 = this.state;
            int i2 = this.STATE_INBYTES - 1;
            bArr2[i2] = (byte) (bArr2[i2] ^ (1 << this.LAST_THREE_BITS_OFFSET));
        } else if (length <= 16) {
            System.arraycopy(byteArray, 0, this.state, 0, length);
            if (length < 16) {
                byte[] bArr3 = this.state;
                bArr3[length] = (byte) (bArr3[length] ^ 1);
            }
            byte[] bArr4 = this.state;
            int i3 = this.STATE_INBYTES - 1;
            bArr4[i3] = (byte) (bArr4[i3] ^ ((length < 16 ? 1 : 2) << this.LAST_THREE_BITS_OFFSET));
        } else {
            System.arraycopy(byteArray, 0, this.state, 0, 16);
            int i4 = length - 16;
            int i5 = ((i4 + this.RATE_INBYTES) - 1) / this.RATE_INBYTES;
            int i6 = 0;
            while (i6 < i5 - 1) {
                PHOTON_Permutation();
                Bytes.xorTo(this.RATE_INBYTES, byteArray, 16 + (i6 * this.RATE_INBYTES), this.state, 0);
                i6++;
            }
            PHOTON_Permutation();
            int i7 = i4 - (i6 * this.RATE_INBYTES);
            Bytes.xorTo(i7, byteArray, 16 + (i6 * this.RATE_INBYTES), this.state, 0);
            if (i7 < this.RATE_INBYTES) {
                byte[] bArr5 = this.state;
                bArr5[i7] = (byte) (bArr5[i7] ^ 1);
            }
            byte[] bArr6 = this.state;
            int i8 = this.STATE_INBYTES - 1;
            bArr6[i8] = (byte) (bArr6[i8] ^ ((i4 % this.RATE_INBYTES == 0 ? 1 : 2) << this.LAST_THREE_BITS_OFFSET));
        }
        PHOTON_Permutation();
        System.arraycopy(this.state, 0, bArr, i, this.SQUEEZE_RATE_INBYTES);
        PHOTON_Permutation();
        System.arraycopy(this.state, 0, bArr, i + this.SQUEEZE_RATE_INBYTES, this.TAG_INBYTES - this.SQUEEZE_RATE_INBYTES);
        reset();
        return this.TAG_INBYTES;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.buffer.reset();
        Arrays.fill(this.state, (byte) 0);
    }

    void PHOTON_Permutation() {
        for (int i = 0; i < this.DSquare; i++) {
            this.state_2d[i >>> this.Dq][i & this.Dr] = (byte) (((this.state[i >> 1] & 255) >>> (4 * (i & 1))) & 15);
        }
        for (int i2 = 0; i2 < this.ROUND; i2++) {
            for (int i3 = 0; i3 < this.D; i3++) {
                byte[] bArr = this.state_2d[i3];
                bArr[0] = (byte) (bArr[0] ^ this.RC[i3][i2]);
            }
            for (int i4 = 0; i4 < this.D; i4++) {
                for (int i5 = 0; i5 < this.D; i5++) {
                    this.state_2d[i4][i5] = this.sbox[this.state_2d[i4][i5]];
                }
            }
            for (int i6 = 1; i6 < this.D; i6++) {
                System.arraycopy(this.state_2d[i6], 0, this.state, 0, this.D);
                System.arraycopy(this.state, i6, this.state_2d[i6], 0, this.D - i6);
                System.arraycopy(this.state, 0, this.state_2d[i6], this.D - i6, i6);
            }
            for (int i7 = 0; i7 < this.D; i7++) {
                for (int i8 = 0; i8 < this.D; i8++) {
                    int i9 = 0;
                    for (int i10 = 0; i10 < this.D; i10++) {
                        byte b = this.MixColMatrix[i8][i10];
                        byte b2 = this.state_2d[i10][i7];
                        i9 = (((i9 ^ (b * (b2 & 1))) ^ (b * (b2 & 2))) ^ (b * (b2 & 4))) ^ (b * (b2 & 8));
                    }
                    int i11 = i9 >>> 4;
                    int i12 = ((i9 & 15) ^ i11) ^ (i11 << 1);
                    int i13 = i12 >>> 4;
                    this.state[i8] = (byte) (((i12 & 15) ^ i13) ^ (i13 << 1));
                }
                for (int i14 = 0; i14 < this.D; i14++) {
                    this.state_2d[i14][i7] = this.state[i14];
                }
            }
        }
        for (int i15 = 0; i15 < this.DSquare; i15 += 2) {
            this.state[i15 >>> 1] = (byte) ((this.state_2d[i15 >>> this.Dq][i15 & this.Dr] & 15) | ((this.state_2d[i15 >>> this.Dq][(i15 + 1) & this.Dr] & 15) << 4));
        }
    }
}
