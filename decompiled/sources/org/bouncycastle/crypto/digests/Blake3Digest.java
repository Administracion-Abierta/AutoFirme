package org.bouncycastle.crypto.digests;

import java.util.Iterator;
import java.util.Stack;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Blake3Digest.class */
public class Blake3Digest implements ExtendedDigest, Memoable, Xof {
    private static final String ERR_OUTPUTTING = "Already outputting";
    private static final int NUMWORDS = 8;
    private static final int ROUNDS = 7;
    private static final int BLOCKLEN = 64;
    private static final int CHUNKLEN = 1024;
    private static final int CHUNKSTART = 1;
    private static final int CHUNKEND = 2;
    private static final int PARENT = 4;
    private static final int ROOT = 8;
    private static final int KEYEDHASH = 16;
    private static final int DERIVECONTEXT = 32;
    private static final int DERIVEKEY = 64;
    private static final int CHAINING0 = 0;
    private static final int CHAINING1 = 1;
    private static final int CHAINING2 = 2;
    private static final int CHAINING3 = 3;
    private static final int CHAINING4 = 4;
    private static final int CHAINING5 = 5;
    private static final int CHAINING6 = 6;
    private static final int CHAINING7 = 7;
    private static final int IV0 = 8;
    private static final int IV1 = 9;
    private static final int IV2 = 10;
    private static final int IV3 = 11;
    private static final int COUNT0 = 12;
    private static final int COUNT1 = 13;
    private static final int DATALEN = 14;
    private static final int FLAGS = 15;
    private static final byte[] SIGMA = {2, 6, 3, 10, 7, 0, 4, 13, 1, 11, 12, 5, 9, 14, 15, 8};
    private static final int[] IV = {1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
    private final byte[] theBuffer;
    private final int[] theK;
    private final int[] theChaining;
    private final int[] theV;
    private final int[] theM;
    private final byte[] theIndices;
    private final Stack theStack;
    private final int theDigestLen;
    private boolean outputting;
    private long outputAvailable;
    private int theMode;
    private int theOutputMode;
    private int theOutputDataLen;
    private long theCounter;
    private int theCurrBytes;
    private int thePos;
    private final CryptoServicePurpose purpose;

    public Blake3Digest() {
        this(256);
    }

    public Blake3Digest(int i) {
        this(i > 100 ? i : i * 8, CryptoServicePurpose.ANY);
    }

    public Blake3Digest(int i, CryptoServicePurpose cryptoServicePurpose) {
        this.theBuffer = new byte[64];
        this.theK = new int[8];
        this.theChaining = new int[8];
        this.theV = new int[16];
        this.theM = new int[16];
        this.theIndices = new byte[16];
        this.theStack = new Stack();
        this.purpose = cryptoServicePurpose;
        this.theDigestLen = i / 8;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, getDigestSize() * 8, cryptoServicePurpose));
        init(null);
    }

    public Blake3Digest(Blake3Digest blake3Digest) {
        this.theBuffer = new byte[64];
        this.theK = new int[8];
        this.theChaining = new int[8];
        this.theV = new int[16];
        this.theM = new int[16];
        this.theIndices = new byte[16];
        this.theStack = new Stack();
        this.theDigestLen = blake3Digest.theDigestLen;
        this.purpose = blake3Digest.purpose;
        reset(blake3Digest);
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "BLAKE3";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.theDigestLen;
    }

    public void init(Blake3Parameters blake3Parameters) {
        byte[] key = blake3Parameters == null ? null : blake3Parameters.getKey();
        byte[] context = blake3Parameters == null ? null : blake3Parameters.getContext();
        reset();
        if (key != null) {
            initKey(key);
            Arrays.fill(key, (byte) 0);
        } else {
            if (context == null) {
                initNullKey();
                this.theMode = 0;
                return;
            }
            initNullKey();
            this.theMode = 32;
            update(context, 0, context.length);
            doFinal(this.theBuffer, 0);
            initKeyFromContext();
            reset();
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (this.outputting) {
            throw new IllegalStateException(ERR_OUTPUTTING);
        }
        if (this.theBuffer.length - this.thePos == 0) {
            compressBlock(this.theBuffer, 0);
            Arrays.fill(this.theBuffer, (byte) 0);
            this.thePos = 0;
        }
        this.theBuffer[this.thePos] = b;
        this.thePos++;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (bArr == null || i2 == 0) {
            return;
        }
        if (this.outputting) {
            throw new IllegalStateException(ERR_OUTPUTTING);
        }
        int i3 = 0;
        if (this.thePos != 0) {
            i3 = 64 - this.thePos;
            if (i3 >= i2) {
                System.arraycopy(bArr, i, this.theBuffer, this.thePos, i2);
                this.thePos += i2;
                return;
            } else {
                System.arraycopy(bArr, i, this.theBuffer, this.thePos, i3);
                compressBlock(this.theBuffer, 0);
                this.thePos = 0;
                Arrays.fill(this.theBuffer, (byte) 0);
            }
        }
        int i4 = (i + i2) - 64;
        int i5 = i + i3;
        while (i5 < i4) {
            compressBlock(bArr, i5);
            i5 += 64;
        }
        int i6 = i2 - i5;
        System.arraycopy(bArr, i5, this.theBuffer, 0, i + i6);
        this.thePos += i + i6;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        return doFinal(bArr, i, getDigestSize());
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doFinal(byte[] bArr, int i, int i2) {
        int iDoOutput = doOutput(bArr, i, i2);
        reset();
        return iDoOutput;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doOutput(byte[] bArr, int i, int i2) {
        if (i > bArr.length - i2) {
            throw new OutputLengthException("output buffer too short");
        }
        if (!this.outputting) {
            compressFinalBlock(this.thePos);
        }
        if (i2 < 0 || (this.outputAvailable >= 0 && i2 > this.outputAvailable)) {
            throw new IllegalArgumentException("Insufficient bytes remaining");
        }
        int i3 = i2;
        int i4 = i;
        if (this.thePos < 64) {
            int iMin = Math.min(i3, 64 - this.thePos);
            System.arraycopy(this.theBuffer, this.thePos, bArr, i4, iMin);
            this.thePos += iMin;
            i4 += iMin;
            i3 -= iMin;
        }
        while (i3 > 0) {
            nextOutputBlock();
            int iMin2 = Math.min(i3, 64);
            System.arraycopy(this.theBuffer, 0, bArr, i4, iMin2);
            this.thePos += iMin2;
            i4 += iMin2;
            i3 -= iMin2;
        }
        this.outputAvailable -= (long) i2;
        return i2;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        resetBlockCount();
        this.thePos = 0;
        this.outputting = false;
        Arrays.fill(this.theBuffer, (byte) 0);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        Blake3Digest blake3Digest = (Blake3Digest) memoable;
        this.theCounter = blake3Digest.theCounter;
        this.theCurrBytes = blake3Digest.theCurrBytes;
        this.theMode = blake3Digest.theMode;
        this.outputting = blake3Digest.outputting;
        this.outputAvailable = blake3Digest.outputAvailable;
        this.theOutputMode = blake3Digest.theOutputMode;
        this.theOutputDataLen = blake3Digest.theOutputDataLen;
        System.arraycopy(blake3Digest.theChaining, 0, this.theChaining, 0, this.theChaining.length);
        System.arraycopy(blake3Digest.theK, 0, this.theK, 0, this.theK.length);
        System.arraycopy(blake3Digest.theM, 0, this.theM, 0, this.theM.length);
        this.theStack.clear();
        Iterator it = blake3Digest.theStack.iterator();
        while (it.hasNext()) {
            this.theStack.push(Arrays.clone((int[]) it.next()));
        }
        System.arraycopy(blake3Digest.theBuffer, 0, this.theBuffer, 0, this.theBuffer.length);
        this.thePos = blake3Digest.thePos;
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new Blake3Digest(this);
    }

    private void compressBlock(byte[] bArr, int i) {
        initChunkBlock(64, false);
        initM(bArr, i);
        compress();
        if (this.theCurrBytes == 0) {
            adjustStack();
        }
    }

    private void adjustStack() {
        long j = this.theCounter;
        while (true) {
            long j2 = j;
            if (j2 <= 0 || (j2 & 1) == 1) {
                break;
            }
            System.arraycopy((int[]) this.theStack.pop(), 0, this.theM, 0, 8);
            System.arraycopy(this.theChaining, 0, this.theM, 8, 8);
            initParentBlock();
            compress();
            j = j2 >> 1;
        }
        this.theStack.push(Arrays.copyOf(this.theChaining, 8));
    }

    private void compressFinalBlock(int i) {
        initChunkBlock(i, true);
        initM(this.theBuffer, 0);
        compress();
        processStack();
    }

    private void processStack() {
        while (!this.theStack.isEmpty()) {
            System.arraycopy((int[]) this.theStack.pop(), 0, this.theM, 0, 8);
            System.arraycopy(this.theChaining, 0, this.theM, 8, 8);
            initParentBlock();
            if (this.theStack.isEmpty()) {
                setRoot();
            }
            compress();
        }
    }

    private void compress() {
        initIndices();
        for (int i = 0; i < 6; i++) {
            performRound();
            permuteIndices();
        }
        performRound();
        adjustChaining();
    }

    private void performRound() {
        mixG(0, 0, 4, 8, 12);
        mixG(1, 1, 5, 9, 13);
        mixG(2, 2, 6, 10, 14);
        mixG(3, 3, 7, 11, 15);
        mixG(4, 0, 5, 10, 15);
        mixG(5, 1, 6, 11, 12);
        mixG(6, 2, 7, 8, 13);
        mixG(7, 3, 4, 9, 14);
    }

    private void initM(byte[] bArr, int i) {
        Pack.littleEndianToInt(bArr, i, this.theM);
    }

    private void adjustChaining() {
        if (!this.outputting) {
            for (int i = 0; i < 8; i++) {
                this.theChaining[i] = this.theV[i] ^ this.theV[i + 8];
            }
            return;
        }
        for (int i2 = 0; i2 < 8; i2++) {
            int[] iArr = this.theV;
            int i3 = i2;
            iArr[i3] = iArr[i3] ^ this.theV[i2 + 8];
            int[] iArr2 = this.theV;
            int i4 = i2 + 8;
            iArr2[i4] = iArr2[i4] ^ this.theChaining[i2];
        }
        Pack.intToLittleEndian(this.theV, this.theBuffer, 0);
        this.thePos = 0;
    }

    private void mixG(int i, int i2, int i3, int i4, int i5) {
        int i6 = i << 1;
        int[] iArr = this.theV;
        int i7 = i6 + 1;
        iArr[i2] = iArr[i2] + this.theV[i3] + this.theM[this.theIndices[i6]];
        this.theV[i5] = Integers.rotateRight(this.theV[i5] ^ this.theV[i2], 16);
        int[] iArr2 = this.theV;
        iArr2[i4] = iArr2[i4] + this.theV[i5];
        this.theV[i3] = Integers.rotateRight(this.theV[i3] ^ this.theV[i4], 12);
        int[] iArr3 = this.theV;
        iArr3[i2] = iArr3[i2] + this.theV[i3] + this.theM[this.theIndices[i7]];
        this.theV[i5] = Integers.rotateRight(this.theV[i5] ^ this.theV[i2], 8);
        int[] iArr4 = this.theV;
        iArr4[i4] = iArr4[i4] + this.theV[i5];
        this.theV[i3] = Integers.rotateRight(this.theV[i3] ^ this.theV[i4], 7);
    }

    private void initIndices() {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.theIndices.length) {
                return;
            }
            this.theIndices[b2] = b2;
            b = (byte) (b2 + 1);
        }
    }

    private void permuteIndices() {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.theIndices.length) {
                return;
            }
            this.theIndices[b2] = SIGMA[this.theIndices[b2]];
            b = (byte) (b2 + 1);
        }
    }

    private void initNullKey() {
        System.arraycopy(IV, 0, this.theK, 0, 8);
    }

    private void initKey(byte[] bArr) {
        Pack.littleEndianToInt(bArr, 0, this.theK);
        this.theMode = 16;
    }

    private void initKeyFromContext() {
        System.arraycopy(this.theV, 0, this.theK, 0, 8);
        this.theMode = 64;
    }

    private void initChunkBlock(int i, boolean z) {
        System.arraycopy(this.theCurrBytes == 0 ? this.theK : this.theChaining, 0, this.theV, 0, 8);
        System.arraycopy(IV, 0, this.theV, 8, 4);
        this.theV[12] = (int) this.theCounter;
        this.theV[13] = (int) (this.theCounter >> 32);
        this.theV[14] = i;
        this.theV[15] = this.theMode + (this.theCurrBytes == 0 ? 1 : 0) + (z ? 2 : 0);
        this.theCurrBytes += i;
        if (this.theCurrBytes >= 1024) {
            incrementBlockCount();
            int[] iArr = this.theV;
            iArr[15] = iArr[15] | 2;
        }
        if (z && this.theStack.isEmpty()) {
            setRoot();
        }
    }

    private void initParentBlock() {
        System.arraycopy(this.theK, 0, this.theV, 0, 8);
        System.arraycopy(IV, 0, this.theV, 8, 4);
        this.theV[12] = 0;
        this.theV[13] = 0;
        this.theV[14] = 64;
        this.theV[15] = this.theMode | 4;
    }

    private void nextOutputBlock() {
        this.theCounter++;
        System.arraycopy(this.theChaining, 0, this.theV, 0, 8);
        System.arraycopy(IV, 0, this.theV, 8, 4);
        this.theV[12] = (int) this.theCounter;
        this.theV[13] = (int) (this.theCounter >> 32);
        this.theV[14] = this.theOutputDataLen;
        this.theV[15] = this.theOutputMode;
        compress();
    }

    private void incrementBlockCount() {
        this.theCounter++;
        this.theCurrBytes = 0;
    }

    private void resetBlockCount() {
        this.theCounter = 0L;
        this.theCurrBytes = 0;
    }

    private void setRoot() {
        int[] iArr = this.theV;
        iArr[15] = iArr[15] | 8;
        this.theOutputMode = this.theV[15];
        this.theOutputDataLen = this.theV[14];
        this.theCounter = 0L;
        this.outputting = true;
        this.outputAvailable = -1L;
        System.arraycopy(this.theV, 0, this.theChaining, 0, 8);
    }
}
