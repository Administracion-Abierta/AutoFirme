package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.macs.CMac;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/modes/EAXBlockCipher.class */
public class EAXBlockCipher implements AEADBlockCipher {
    private static final byte nTAG = 0;
    private static final byte hTAG = 1;
    private static final byte cTAG = 2;
    private SICBlockCipher cipher;
    private boolean forEncryption;
    private int blockSize;
    private Mac mac;
    private byte[] nonceMac;
    private byte[] associatedTextMac;
    private byte[] macBlock;
    private int macSize;
    private byte[] bufBlock;
    private int bufOff;
    private boolean cipherInitialized;
    private byte[] initialAssociatedText;

    public EAXBlockCipher(BlockCipher cipher) {
        this.blockSize = cipher.getBlockSize();
        this.mac = new CMac(cipher);
        this.macBlock = new byte[this.blockSize];
        this.associatedTextMac = new byte[this.mac.getMacSize()];
        this.nonceMac = new byte[this.mac.getMacSize()];
        this.cipher = new SICBlockCipher(cipher);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "/EAX";
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher.getUnderlyingCipher();
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        byte[] nonce;
        CipherParameters keyParam;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters) params;
            nonce = param.getNonce();
            this.initialAssociatedText = param.getAssociatedText();
            this.macSize = param.getMacSize() / 8;
            keyParam = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param2 = (ParametersWithIV) params;
            nonce = param2.getIV();
            this.initialAssociatedText = null;
            this.macSize = this.mac.getMacSize() / 2;
            keyParam = param2.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to EAX");
        }
        this.bufBlock = new byte[forEncryption ? this.blockSize : this.blockSize + this.macSize];
        byte[] tag = new byte[this.blockSize];
        this.mac.init(keyParam);
        tag[this.blockSize - 1] = 0;
        this.mac.update(tag, 0, this.blockSize);
        this.mac.update(nonce, 0, nonce.length);
        this.mac.doFinal(this.nonceMac, 0);
        this.cipher.init(true, new ParametersWithIV(null, this.nonceMac));
        reset();
    }

    private void initCipher() {
        if (this.cipherInitialized) {
            return;
        }
        this.cipherInitialized = true;
        this.mac.doFinal(this.associatedTextMac, 0);
        byte[] tag = new byte[this.blockSize];
        tag[this.blockSize - 1] = 2;
        this.mac.update(tag, 0, this.blockSize);
    }

    private void calculateMac() {
        byte[] outC = new byte[this.blockSize];
        this.mac.doFinal(outC, 0);
        for (int i = 0; i < this.macBlock.length; i++) {
            this.macBlock[i] = (byte) ((this.nonceMac[i] ^ this.associatedTextMac[i]) ^ outC[i]);
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void reset() {
        reset(true);
    }

    private void reset(boolean clearMac) {
        this.cipher.reset();
        this.mac.reset();
        this.bufOff = 0;
        Arrays.fill(this.bufBlock, (byte) 0);
        if (clearMac) {
            Arrays.fill(this.macBlock, (byte) 0);
        }
        byte[] tag = new byte[this.blockSize];
        tag[this.blockSize - 1] = 1;
        this.mac.update(tag, 0, this.blockSize);
        this.cipherInitialized = false;
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void processAADByte(byte in) {
        if (this.cipherInitialized) {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        this.mac.update(in);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void processAADBytes(byte[] in, int inOff, int len) {
        if (this.cipherInitialized) {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        this.mac.update(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        initCipher();
        return process(in, out, outOff);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        initCipher();
        if (in.length < inOff + len) {
            throw new DataLengthException("Input buffer too short");
        }
        int resultLen = 0;
        for (int i = 0; i != len; i++) {
            resultLen += process(in[inOff + i], out, outOff + resultLen);
        }
        return resultLen;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        initCipher();
        int extra = this.bufOff;
        byte[] tmp = new byte[this.bufBlock.length];
        this.bufOff = 0;
        if (this.forEncryption) {
            if (out.length < outOff + extra + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            this.cipher.processBlock(this.bufBlock, 0, tmp, 0);
            System.arraycopy(tmp, 0, out, outOff, extra);
            this.mac.update(tmp, 0, extra);
            calculateMac();
            System.arraycopy(this.macBlock, 0, out, outOff + extra, this.macSize);
            reset(false);
            return extra + this.macSize;
        }
        if (extra < this.macSize) {
            throw new InvalidCipherTextException("data too short");
        }
        if (out.length < (outOff + extra) - this.macSize) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (extra > this.macSize) {
            this.mac.update(this.bufBlock, 0, extra - this.macSize);
            this.cipher.processBlock(this.bufBlock, 0, tmp, 0);
            System.arraycopy(tmp, 0, out, outOff, extra - this.macSize);
        }
        calculateMac();
        if (!verifyMac(this.bufBlock, extra - this.macSize)) {
            throw new InvalidCipherTextException("mac check in EAX failed");
        }
        reset(false);
        return extra - this.macSize;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, this.macSize);
        return mac;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getUpdateOutputSize(int len) {
        int totalData = len + this.bufOff;
        if (!this.forEncryption) {
            if (totalData < this.macSize) {
                return 0;
            }
            totalData -= this.macSize;
        }
        return totalData - (totalData % this.blockSize);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getOutputSize(int len) {
        int totalData = len + this.bufOff;
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        if (totalData < this.macSize) {
            return 0;
        }
        return totalData - this.macSize;
    }

    private int process(byte b, byte[] out, int outOff) {
        int size;
        byte[] bArr = this.bufBlock;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = b;
        if (this.bufOff == this.bufBlock.length) {
            if (out.length < outOff + this.blockSize) {
                throw new OutputLengthException("Output buffer is too short");
            }
            if (this.forEncryption) {
                size = this.cipher.processBlock(this.bufBlock, 0, out, outOff);
                this.mac.update(out, outOff, this.blockSize);
            } else {
                this.mac.update(this.bufBlock, 0, this.blockSize);
                size = this.cipher.processBlock(this.bufBlock, 0, out, outOff);
            }
            this.bufOff = 0;
            if (!this.forEncryption) {
                System.arraycopy(this.bufBlock, this.blockSize, this.bufBlock, 0, this.macSize);
                this.bufOff = this.macSize;
            }
            return size;
        }
        return 0;
    }

    private boolean verifyMac(byte[] mac, int off) {
        int nonEqual = 0;
        for (int i = 0; i < this.macSize; i++) {
            nonEqual |= this.macBlock[i] ^ mac[off + i];
        }
        return nonEqual == 0;
    }
}
