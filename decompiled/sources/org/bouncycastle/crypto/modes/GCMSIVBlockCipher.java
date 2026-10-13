package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/GCMSIVBlockCipher.class */
public class GCMSIVBlockCipher implements AEADBlockCipher {
    private static final int BUFLEN = 16;
    private static final int HALFBUFLEN = 8;
    private static final int NONCELEN = 12;
    private static final int MAX_DATALEN = 2147483623;
    private static final byte MASK = -128;
    private static final byte ADD = -31;
    private static final int INIT = 1;
    private static final int AEAD_COMPLETE = 2;
    private final BlockCipher theCipher;
    private final GCMMultiplier theMultiplier;
    private final byte[] theGHash;
    private final byte[] theReverse;
    private final GCMSIVHasher theAEADHasher;
    private final GCMSIVHasher theDataHasher;
    private GCMSIVCache thePlain;
    private GCMSIVCache theEncData;
    private boolean forEncryption;
    private byte[] theInitialAEAD;
    private byte[] theNonce;
    private int theFlags;
    private byte[] macBlock;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/GCMSIVBlockCipher$GCMSIVCache.class */
    private static class GCMSIVCache extends ByteArrayOutputStream {
        GCMSIVCache() {
        }

        byte[] getBuffer() {
            return this.buf;
        }

        void clearBuffer() {
            Arrays.fill(getBuffer(), (byte) 0);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/GCMSIVBlockCipher$GCMSIVHasher.class */
    private class GCMSIVHasher {
        private final byte[] theBuffer;
        private final byte[] theByte;
        private int numActive;
        private long numHashed;

        private GCMSIVHasher() {
            this.theBuffer = new byte[16];
            this.theByte = new byte[1];
        }

        long getBytesProcessed() {
            return this.numHashed;
        }

        void reset() {
            this.numActive = 0;
            this.numHashed = 0L;
        }

        void updateHash(byte b) {
            this.theByte[0] = b;
            updateHash(this.theByte, 0, 1);
        }

        void updateHash(byte[] bArr, int i, int i2) {
            int i3 = 16 - this.numActive;
            int i4 = 0;
            int i5 = i2;
            if (this.numActive > 0 && i2 >= i3) {
                System.arraycopy(bArr, i, this.theBuffer, this.numActive, i3);
                GCMSIVBlockCipher.fillReverse(this.theBuffer, 0, 16, GCMSIVBlockCipher.this.theReverse);
                GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
                i4 = 0 + i3;
                i5 -= i3;
                this.numActive = 0;
            }
            while (i5 >= 16) {
                GCMSIVBlockCipher.fillReverse(bArr, i + i4, 16, GCMSIVBlockCipher.this.theReverse);
                GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
                i4 += 16;
                i5 -= 16;
            }
            if (i5 > 0) {
                System.arraycopy(bArr, i + i4, this.theBuffer, this.numActive, i5);
                this.numActive += i5;
            }
            this.numHashed += (long) i2;
        }

        void completeHash() {
            if (this.numActive > 0) {
                Arrays.fill(GCMSIVBlockCipher.this.theReverse, (byte) 0);
                GCMSIVBlockCipher.fillReverse(this.theBuffer, 0, this.numActive, GCMSIVBlockCipher.this.theReverse);
                GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
            }
        }
    }

    public GCMSIVBlockCipher() {
        this(AESEngine.newInstance());
    }

    public GCMSIVBlockCipher(BlockCipher blockCipher) {
        this(blockCipher, new Tables4kGCMMultiplier());
    }

    public GCMSIVBlockCipher(BlockCipher blockCipher, GCMMultiplier gCMMultiplier) {
        this.theGHash = new byte[16];
        this.theReverse = new byte[16];
        this.macBlock = new byte[16];
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("Cipher required with a block size of 16.");
        }
        this.theCipher = blockCipher;
        this.theMultiplier = gCMMultiplier;
        this.theAEADHasher = new GCMSIVHasher();
        this.theDataHasher = new GCMSIVHasher();
    }

    @Override // org.bouncycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.theCipher;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        byte[] iv;
        KeyParameter key;
        byte[] associatedText = null;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters) cipherParameters;
            associatedText = aEADParameters.getAssociatedText();
            iv = aEADParameters.getNonce();
            key = aEADParameters.getKey();
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to GCM-SIV");
            }
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            iv = parametersWithIV.getIV();
            key = (KeyParameter) parametersWithIV.getParameters();
        }
        if (iv == null || iv.length != 12) {
            throw new IllegalArgumentException("Invalid nonce");
        }
        if (key == null || !(key.getKeyLength() == 16 || key.getKeyLength() == 32)) {
            throw new IllegalArgumentException("Invalid key");
        }
        this.forEncryption = z;
        this.theInitialAEAD = associatedText;
        this.theNonce = iv;
        deriveKeys(key);
        resetStreams();
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return this.theCipher.getAlgorithmName() + "-GCM-SIV";
    }

    private void checkAEADStatus(int i) {
        if ((this.theFlags & 1) == 0) {
            throw new IllegalStateException("Cipher is not initialised");
        }
        if ((this.theFlags & 2) != 0) {
            throw new IllegalStateException("AEAD data cannot be processed after ordinary data");
        }
        if (this.theAEADHasher.getBytesProcessed() - Long.MIN_VALUE > ((long) (MAX_DATALEN - i)) - Long.MIN_VALUE) {
            throw new IllegalStateException("AEAD byte count exceeded");
        }
    }

    private void checkStatus(int i) {
        if ((this.theFlags & 1) == 0) {
            throw new IllegalStateException("Cipher is not initialised");
        }
        if ((this.theFlags & 2) == 0) {
            this.theAEADHasher.completeHash();
            this.theFlags |= 2;
        }
        long j = 2147483623;
        long size = this.thePlain.size();
        if (!this.forEncryption) {
            j = 2147483623 + 16;
            size = this.theEncData.size();
        }
        if (size - Long.MIN_VALUE > (j - ((long) i)) - Long.MIN_VALUE) {
            throw new IllegalStateException("byte count exceeded");
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        checkAEADStatus(1);
        this.theAEADHasher.updateHash(b);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        checkAEADStatus(i2);
        checkBuffer(bArr, i, i2, false);
        this.theAEADHasher.updateHash(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        checkStatus(1);
        if (!this.forEncryption) {
            this.theEncData.write(b);
            return 0;
        }
        this.thePlain.write(b);
        this.theDataHasher.updateHash(b);
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        checkStatus(i2);
        checkBuffer(bArr, i, i2, false);
        if (!this.forEncryption) {
            this.theEncData.write(bArr, i, i2);
            return 0;
        }
        this.thePlain.write(bArr, i, i2);
        this.theDataHasher.updateHash(bArr, i, i2);
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        checkStatus(0);
        checkBuffer(bArr, i, getOutputSize(0), true);
        if (!this.forEncryption) {
            decryptPlain();
            int size = this.thePlain.size();
            System.arraycopy(this.thePlain.getBuffer(), 0, bArr, i, size);
            resetStreams();
            return size;
        }
        byte[] bArrCalculateTag = calculateTag();
        int iEncryptPlain = 16 + encryptPlain(bArrCalculateTag, bArr, i);
        System.arraycopy(bArrCalculateTag, 0, bArr, i + this.thePlain.size(), 16);
        System.arraycopy(bArrCalculateTag, 0, this.macBlock, 0, this.macBlock.length);
        resetStreams();
        return iEncryptPlain;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        return Arrays.clone(this.macBlock);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        if (this.forEncryption) {
            return i + this.thePlain.size() + 16;
        }
        int size = i + this.theEncData.size();
        if (size > 16) {
            return size - 16;
        }
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        resetStreams();
    }

    private void resetStreams() {
        if (this.thePlain != null) {
            this.thePlain.clearBuffer();
        }
        this.theAEADHasher.reset();
        this.theDataHasher.reset();
        this.thePlain = new GCMSIVCache();
        this.theEncData = this.forEncryption ? null : new GCMSIVCache();
        this.theFlags &= -3;
        Arrays.fill(this.theGHash, (byte) 0);
        if (this.theInitialAEAD != null) {
            this.theAEADHasher.updateHash(this.theInitialAEAD, 0, this.theInitialAEAD.length);
        }
    }

    private static int bufLength(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        return bArr.length;
    }

    private static void checkBuffer(byte[] bArr, int i, int i2, boolean z) {
        int iBufLength = bufLength(bArr);
        int i3 = i + i2;
        if ((i2 < 0 || i < 0 || i3 < 0) || i3 > iBufLength) {
            if (!z) {
                throw new DataLengthException("Input buffer too short.");
            }
        }
    }

    private int encryptPlain(byte[] bArr, byte[] bArr2, int i) {
        byte[] buffer = this.thePlain.getBuffer();
        byte[] bArrClone = Arrays.clone(bArr);
        bArrClone[15] = (byte) (bArrClone[15] | (-128));
        byte[] bArr3 = new byte[16];
        int size = this.thePlain.size();
        int i2 = 0;
        while (size > 0) {
            this.theCipher.processBlock(bArrClone, 0, bArr3, 0);
            int iMin = Math.min(16, size);
            xorBlock(bArr3, buffer, i2, iMin);
            System.arraycopy(bArr3, 0, bArr2, i + i2, iMin);
            size -= iMin;
            i2 += iMin;
            incrementCounter(bArrClone);
        }
        return this.thePlain.size();
    }

    private void decryptPlain() throws InvalidCipherTextException {
        byte[] buffer = this.theEncData.getBuffer();
        int size = this.theEncData.size() - 16;
        if (size < 0) {
            throw new InvalidCipherTextException("Data too short");
        }
        byte[] bArrCopyOfRange = Arrays.copyOfRange(buffer, size, size + 16);
        byte[] bArrClone = Arrays.clone(bArrCopyOfRange);
        bArrClone[15] = (byte) (bArrClone[15] | (-128));
        byte[] bArr = new byte[16];
        int i = 0;
        while (size > 0) {
            this.theCipher.processBlock(bArrClone, 0, bArr, 0);
            int iMin = Math.min(16, size);
            xorBlock(bArr, buffer, i, iMin);
            this.thePlain.write(bArr, 0, iMin);
            this.theDataHasher.updateHash(bArr, 0, iMin);
            size -= iMin;
            i += iMin;
            incrementCounter(bArrClone);
        }
        byte[] bArrCalculateTag = calculateTag();
        if (Arrays.constantTimeAreEqual(bArrCalculateTag, bArrCopyOfRange)) {
            System.arraycopy(bArrCalculateTag, 0, this.macBlock, 0, this.macBlock.length);
        } else {
            reset();
            throw new InvalidCipherTextException("mac check failed");
        }
    }

    private byte[] calculateTag() {
        this.theDataHasher.completeHash();
        byte[] bArrCompletePolyVal = completePolyVal();
        byte[] bArr = new byte[16];
        for (int i = 0; i < 12; i++) {
            int i2 = i;
            bArrCompletePolyVal[i2] = (byte) (bArrCompletePolyVal[i2] ^ this.theNonce[i]);
        }
        bArrCompletePolyVal[15] = (byte) (bArrCompletePolyVal[15] & (-129));
        this.theCipher.processBlock(bArrCompletePolyVal, 0, bArr, 0);
        return bArr;
    }

    private byte[] completePolyVal() {
        byte[] bArr = new byte[16];
        gHashLengths();
        fillReverse(this.theGHash, 0, 16, bArr);
        return bArr;
    }

    private void gHashLengths() {
        byte[] bArr = new byte[16];
        Pack.longToBigEndian(8 * this.theDataHasher.getBytesProcessed(), bArr, 0);
        Pack.longToBigEndian(8 * this.theAEADHasher.getBytesProcessed(), bArr, 8);
        gHASH(bArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void gHASH(byte[] bArr) {
        xorBlock(this.theGHash, bArr);
        this.theMultiplier.multiplyH(this.theGHash);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void fillReverse(byte[] bArr, int i, int i2, byte[] bArr2) {
        int i3 = 0;
        int i4 = 15;
        while (i3 < i2) {
            bArr2[i4] = bArr[i + i3];
            i3++;
            i4--;
        }
    }

    private static void xorBlock(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < 16; i++) {
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ bArr2[i]);
        }
    }

    private static void xorBlock(byte[] bArr, byte[] bArr2, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i3;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i3 + i]);
        }
    }

    private static void incrementCounter(byte[] bArr) {
        for (int i = 0; i < 4; i++) {
            int i2 = i;
            byte b = (byte) (bArr[i2] + 1);
            bArr[i2] = b;
            if (b != 0) {
                return;
            }
        }
    }

    private static void mulX(byte[] bArr) {
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            byte b = bArr[i2];
            bArr[i2] = (byte) (((b >> 1) & 127) | i);
            i = (b & 1) == 0 ? 0 : -128;
        }
        if (i != 0) {
            bArr[0] = (byte) (bArr[0] ^ ADD);
        }
    }

    private void deriveKeys(KeyParameter keyParameter) {
        byte[] bArr = new byte[16];
        byte[] bArr2 = new byte[16];
        byte[] bArr3 = new byte[16];
        byte[] bArr4 = new byte[keyParameter.getKeyLength()];
        System.arraycopy(this.theNonce, 0, bArr, 4, 12);
        this.theCipher.init(true, keyParameter);
        this.theCipher.processBlock(bArr, 0, bArr2, 0);
        System.arraycopy(bArr2, 0, bArr3, 0, 8);
        bArr[0] = (byte) (bArr[0] + 1);
        this.theCipher.processBlock(bArr, 0, bArr2, 0);
        System.arraycopy(bArr2, 0, bArr3, 0 + 8, 8);
        bArr[0] = (byte) (bArr[0] + 1);
        this.theCipher.processBlock(bArr, 0, bArr2, 0);
        System.arraycopy(bArr2, 0, bArr4, 0, 8);
        bArr[0] = (byte) (bArr[0] + 1);
        int i = 0 + 8;
        this.theCipher.processBlock(bArr, 0, bArr2, 0);
        System.arraycopy(bArr2, 0, bArr4, i, 8);
        if (bArr4.length == 32) {
            bArr[0] = (byte) (bArr[0] + 1);
            int i2 = i + 8;
            this.theCipher.processBlock(bArr, 0, bArr2, 0);
            System.arraycopy(bArr2, 0, bArr4, i2, 8);
            bArr[0] = (byte) (bArr[0] + 1);
            this.theCipher.processBlock(bArr, 0, bArr2, 0);
            System.arraycopy(bArr2, 0, bArr4, i2 + 8, 8);
        }
        this.theCipher.init(true, new KeyParameter(bArr4));
        fillReverse(bArr3, 0, 16, bArr2);
        mulX(bArr2);
        this.theMultiplier.init(bArr2);
        this.theFlags |= 1;
    }
}
