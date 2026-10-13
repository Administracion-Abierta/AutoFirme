package org.spongycastle.crypto.modes;

import java.util.Vector;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/OCBBlockCipher.class */
public class OCBBlockCipher implements AEADBlockCipher {
    private static final int BLOCK_SIZE = 16;
    private BlockCipher hashCipher;
    private BlockCipher mainCipher;
    private boolean forEncryption;
    private int macSize;
    private byte[] initialAssociatedText;
    private Vector L;
    private byte[] L_Asterisk;
    private byte[] L_Dollar;
    private byte[] hashBlock;
    private byte[] mainBlock;
    private int hashBlockPos;
    private int mainBlockPos;
    private long hashBlockCount;
    private long mainBlockCount;
    private byte[] OffsetHASH;
    private byte[] Sum;
    private byte[] Checksum;
    private byte[] macBlock;
    private byte[] KtopInput = null;
    private byte[] Stretch = new byte[24];
    private byte[] OffsetMAIN_0 = new byte[16];
    private byte[] OffsetMAIN = new byte[16];

    public OCBBlockCipher(BlockCipher hashCipher, BlockCipher mainCipher) {
        if (hashCipher == null) {
            throw new IllegalArgumentException("'hashCipher' cannot be null");
        }
        if (hashCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("'hashCipher' must have a block size of 16");
        }
        if (mainCipher == null) {
            throw new IllegalArgumentException("'mainCipher' cannot be null");
        }
        if (mainCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("'mainCipher' must have a block size of 16");
        }
        if (!hashCipher.getAlgorithmName().equals(mainCipher.getAlgorithmName())) {
            throw new IllegalArgumentException("'hashCipher' and 'mainCipher' must be the same algorithm");
        }
        this.hashCipher = hashCipher;
        this.mainCipher = mainCipher;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.mainCipher;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public String getAlgorithmName() {
        return this.mainCipher.getAlgorithmName() + "/OCB";
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters parameters) throws IllegalArgumentException {
        byte[] N;
        KeyParameter keyParameter;
        boolean oldForEncryption = this.forEncryption;
        this.forEncryption = forEncryption;
        this.macBlock = null;
        if (parameters instanceof AEADParameters) {
            AEADParameters aeadParameters = (AEADParameters) parameters;
            N = aeadParameters.getNonce();
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            int macSizeBits = aeadParameters.getMacSize();
            if (macSizeBits < 64 || macSizeBits > 128 || macSizeBits % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits / 8;
            keyParameter = aeadParameters.getKey();
        } else if (parameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) parameters;
            N = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            keyParameter = (KeyParameter) parametersWithIV.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to OCB");
        }
        this.hashBlock = new byte[16];
        this.mainBlock = new byte[forEncryption ? 16 : 16 + this.macSize];
        if (N == null) {
            N = new byte[0];
        }
        if (N.length > 15) {
            throw new IllegalArgumentException("IV must be no more than 15 bytes");
        }
        if (keyParameter != null) {
            this.hashCipher.init(true, keyParameter);
            this.mainCipher.init(forEncryption, keyParameter);
            this.KtopInput = null;
        } else if (oldForEncryption != forEncryption) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
        }
        this.L_Asterisk = new byte[16];
        this.hashCipher.processBlock(this.L_Asterisk, 0, this.L_Asterisk, 0);
        this.L_Dollar = OCB_double(this.L_Asterisk);
        this.L = new Vector();
        this.L.addElement(OCB_double(this.L_Dollar));
        int bottom = processNonce(N);
        int bits = bottom % 8;
        int bytes = bottom / 8;
        if (bits == 0) {
            System.arraycopy(this.Stretch, bytes, this.OffsetMAIN_0, 0, 16);
        } else {
            for (int i = 0; i < 16; i++) {
                int b1 = this.Stretch[bytes] & 255;
                bytes++;
                int b2 = this.Stretch[bytes] & 255;
                this.OffsetMAIN_0[i] = (byte) ((b1 << bits) | (b2 >>> (8 - bits)));
            }
        }
        this.hashBlockPos = 0;
        this.mainBlockPos = 0;
        this.hashBlockCount = 0L;
        this.mainBlockCount = 0L;
        this.OffsetHASH = new byte[16];
        this.Sum = new byte[16];
        System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
        this.Checksum = new byte[16];
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    protected int processNonce(byte[] N) {
        byte[] nonce = new byte[16];
        System.arraycopy(N, 0, nonce, nonce.length - N.length, N.length);
        nonce[0] = (byte) (this.macSize << 4);
        int length = 15 - N.length;
        nonce[length] = (byte) (nonce[length] | 1);
        int bottom = nonce[15] & 63;
        nonce[15] = (byte) (nonce[15] & 192);
        if (this.KtopInput == null || !Arrays.areEqual(nonce, this.KtopInput)) {
            byte[] Ktop = new byte[16];
            this.KtopInput = nonce;
            this.hashCipher.processBlock(this.KtopInput, 0, Ktop, 0);
            System.arraycopy(Ktop, 0, this.Stretch, 0, 16);
            for (int i = 0; i < 8; i++) {
                this.Stretch[16 + i] = (byte) (Ktop[i] ^ Ktop[i + 1]);
            }
        }
        return bottom;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public byte[] getMac() {
        if (this.macBlock == null) {
            return new byte[this.macSize];
        }
        return Arrays.clone(this.macBlock);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getOutputSize(int len) {
        int totalData = len + this.mainBlockPos;
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        if (totalData < this.macSize) {
            return 0;
        }
        return totalData - this.macSize;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getUpdateOutputSize(int len) {
        int totalData = len + this.mainBlockPos;
        if (!this.forEncryption) {
            if (totalData < this.macSize) {
                return 0;
            }
            totalData -= this.macSize;
        }
        return totalData - (totalData % 16);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void processAADByte(byte input) {
        this.hashBlock[this.hashBlockPos] = input;
        int i = this.hashBlockPos + 1;
        this.hashBlockPos = i;
        if (i == this.hashBlock.length) {
            processHashBlock();
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void processAADBytes(byte[] input, int off, int len) {
        for (int i = 0; i < len; i++) {
            this.hashBlock[this.hashBlockPos] = input[off + i];
            int i2 = this.hashBlockPos + 1;
            this.hashBlockPos = i2;
            if (i2 == this.hashBlock.length) {
                processHashBlock();
            }
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processByte(byte input, byte[] output, int outOff) throws DataLengthException {
        this.mainBlock[this.mainBlockPos] = input;
        int i = this.mainBlockPos + 1;
        this.mainBlockPos = i;
        if (i == this.mainBlock.length) {
            processMainBlock(output, outOff);
            return 16;
        }
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processBytes(byte[] input, int inOff, int len, byte[] output, int outOff) throws DataLengthException {
        if (input.length < inOff + len) {
            throw new DataLengthException("Input buffer too short");
        }
        int resultLen = 0;
        for (int i = 0; i < len; i++) {
            this.mainBlock[this.mainBlockPos] = input[inOff + i];
            int i2 = this.mainBlockPos + 1;
            this.mainBlockPos = i2;
            if (i2 == this.mainBlock.length) {
                processMainBlock(output, outOff + resultLen);
                resultLen += 16;
            }
        }
        return resultLen;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int doFinal(byte[] output, int outOff) throws IllegalStateException, InvalidCipherTextException {
        byte[] tag = null;
        if (!this.forEncryption) {
            if (this.mainBlockPos < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            this.mainBlockPos -= this.macSize;
            tag = new byte[this.macSize];
            System.arraycopy(this.mainBlock, this.mainBlockPos, tag, 0, this.macSize);
        }
        if (this.hashBlockPos > 0) {
            OCB_extend(this.hashBlock, this.hashBlockPos);
            updateHASH(this.L_Asterisk);
        }
        if (this.mainBlockPos > 0) {
            if (this.forEncryption) {
                OCB_extend(this.mainBlock, this.mainBlockPos);
                xor(this.Checksum, this.mainBlock);
            }
            xor(this.OffsetMAIN, this.L_Asterisk);
            byte[] Pad = new byte[16];
            this.hashCipher.processBlock(this.OffsetMAIN, 0, Pad, 0);
            xor(this.mainBlock, Pad);
            if (output.length < outOff + this.mainBlockPos) {
                throw new OutputLengthException("Output buffer too short");
            }
            System.arraycopy(this.mainBlock, 0, output, outOff, this.mainBlockPos);
            if (!this.forEncryption) {
                OCB_extend(this.mainBlock, this.mainBlockPos);
                xor(this.Checksum, this.mainBlock);
            }
        }
        xor(this.Checksum, this.OffsetMAIN);
        xor(this.Checksum, this.L_Dollar);
        this.hashCipher.processBlock(this.Checksum, 0, this.Checksum, 0);
        xor(this.Checksum, this.Sum);
        this.macBlock = new byte[this.macSize];
        System.arraycopy(this.Checksum, 0, this.macBlock, 0, this.macSize);
        int resultLen = this.mainBlockPos;
        if (this.forEncryption) {
            if (output.length < outOff + resultLen + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            System.arraycopy(this.macBlock, 0, output, outOff + resultLen, this.macSize);
            resultLen += this.macSize;
        } else if (!Arrays.constantTimeAreEqual(this.macBlock, tag)) {
            throw new InvalidCipherTextException("mac check in OCB failed");
        }
        reset(false);
        return resultLen;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void reset() {
        reset(true);
    }

    protected void clear(byte[] bs) {
        if (bs != null) {
            Arrays.fill(bs, (byte) 0);
        }
    }

    protected byte[] getLSub(int n) {
        while (n >= this.L.size()) {
            this.L.addElement(OCB_double((byte[]) this.L.lastElement()));
        }
        return (byte[]) this.L.elementAt(n);
    }

    protected void processHashBlock() {
        long j = this.hashBlockCount + 1;
        this.hashBlockCount = j;
        updateHASH(getLSub(OCB_ntz(j)));
        this.hashBlockPos = 0;
    }

    protected void processMainBlock(byte[] output, int outOff) {
        if (output.length < outOff + 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            xor(this.Checksum, this.mainBlock);
            this.mainBlockPos = 0;
        }
        byte[] bArr = this.OffsetMAIN;
        long j = this.mainBlockCount + 1;
        this.mainBlockCount = j;
        xor(bArr, getLSub(OCB_ntz(j)));
        xor(this.mainBlock, this.OffsetMAIN);
        this.mainCipher.processBlock(this.mainBlock, 0, this.mainBlock, 0);
        xor(this.mainBlock, this.OffsetMAIN);
        System.arraycopy(this.mainBlock, 0, output, outOff, 16);
        if (!this.forEncryption) {
            xor(this.Checksum, this.mainBlock);
            System.arraycopy(this.mainBlock, 16, this.mainBlock, 0, this.macSize);
            this.mainBlockPos = this.macSize;
        }
    }

    protected void reset(boolean clearMac) {
        this.hashCipher.reset();
        this.mainCipher.reset();
        clear(this.hashBlock);
        clear(this.mainBlock);
        this.hashBlockPos = 0;
        this.mainBlockPos = 0;
        this.hashBlockCount = 0L;
        this.mainBlockCount = 0L;
        clear(this.OffsetHASH);
        clear(this.Sum);
        System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
        clear(this.Checksum);
        if (clearMac) {
            this.macBlock = null;
        }
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    protected void updateHASH(byte[] LSub) {
        xor(this.OffsetHASH, LSub);
        xor(this.hashBlock, this.OffsetHASH);
        this.hashCipher.processBlock(this.hashBlock, 0, this.hashBlock, 0);
        xor(this.Sum, this.hashBlock);
    }

    protected static byte[] OCB_double(byte[] block) {
        byte[] result = new byte[16];
        int carry = shiftLeft(block, result);
        result[15] = (byte) (result[15] ^ (CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA >>> ((1 - carry) << 3)));
        return result;
    }

    protected static void OCB_extend(byte[] block, int pos) {
        block[pos] = -128;
        while (true) {
            pos++;
            if (pos < 16) {
                block[pos] = 0;
            } else {
                return;
            }
        }
    }

    protected static int OCB_ntz(long x) {
        if (x == 0) {
            return 64;
        }
        int n = 0;
        while ((x & 1) == 0) {
            n++;
            x >>>= 1;
        }
        return n;
    }

    protected static int shiftLeft(byte[] block, byte[] output) {
        int i = 16;
        int i2 = 0;
        while (true) {
            int bit = i2;
            i--;
            if (i >= 0) {
                int b = block[i] & 255;
                output[i] = (byte) ((b << 1) | bit);
                i2 = (b >>> 7) & 1;
            } else {
                return bit;
            }
        }
    }

    protected static void xor(byte[] block, byte[] val) {
        for (int i = 15; i >= 0; i--) {
            int i2 = i;
            block[i2] = (byte) (block[i2] ^ val[i]);
        }
    }
}
