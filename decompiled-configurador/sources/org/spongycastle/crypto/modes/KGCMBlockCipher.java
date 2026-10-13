package org.spongycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/KGCMBlockCipher.class */
public class KGCMBlockCipher implements AEADBlockCipher {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger MASK_1_128 = new BigInteger("340282366920938463463374607431768211456", 10);
    private static final BigInteger MASK_2_128 = new BigInteger("340282366920938463463374607431768211455", 10);
    private static final BigInteger POLYRED_128 = new BigInteger("135", 10);
    private static final BigInteger MASK_1_256 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639936", 10);
    private static final BigInteger MASK_2_256 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639935", 10);
    private static final BigInteger POLYRED_256 = new BigInteger("1061", 10);
    private static final BigInteger MASK_1_512 = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084096", 10);
    private static final BigInteger MASK_2_512 = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084095", 10);
    private static final BigInteger POLYRED_512 = new BigInteger("293", 10);
    private static final int MIN_MAC_BITS = 64;
    private static final int BITS_IN_BYTE = 8;
    private BlockCipher engine;
    private BufferedBlockCipher ctrEngine;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private byte[] iv;
    private byte[] H;
    private byte[] b;
    private byte[] temp;
    private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();
    private int macSize = 0;
    private int lambda_c = 0;
    private int lambda_o = 0;
    private byte[] macBlock = null;

    public KGCMBlockCipher(BlockCipher dstu7624Engine) {
        this.engine = dstu7624Engine;
        this.ctrEngine = new BufferedBlockCipher(new KCTRBlockCipher(this.engine));
        this.initialAssociatedText = new byte[this.engine.getBlockSize()];
        this.iv = new byte[this.engine.getBlockSize()];
        this.H = new byte[this.engine.getBlockSize()];
        this.b = new byte[this.engine.getBlockSize()];
        this.temp = new byte[this.engine.getBlockSize()];
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        KeyParameter engineParam;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters) params;
            byte[] iv = param.getNonce();
            int diff = this.iv.length - iv.length;
            Arrays.fill(this.iv, (byte) 0);
            System.arraycopy(iv, 0, this.iv, diff, iv.length);
            this.initialAssociatedText = param.getAssociatedText();
            int macSizeBits = param.getMacSize();
            if (macSizeBits < 64 || macSizeBits > this.engine.getBlockSize() * 8 || macSizeBits % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits / 8;
            engineParam = param.getKey();
            if (this.initialAssociatedText != null) {
                processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param2 = (ParametersWithIV) params;
            byte[] iv2 = param2.getIV();
            int diff2 = this.iv.length - iv2.length;
            Arrays.fill(this.iv, (byte) 0);
            System.arraycopy(iv2, 0, this.iv, diff2, iv2.length);
            this.initialAssociatedText = null;
            this.macSize = this.engine.getBlockSize();
            engineParam = (KeyParameter) param2.getParameters();
        } else {
            throw new IllegalArgumentException("Invalid parameter passed");
        }
        this.macBlock = new byte[this.engine.getBlockSize()];
        this.ctrEngine.init(true, new ParametersWithIV(engineParam, this.iv));
        this.engine.init(true, engineParam);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KGCM";
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void processAADByte(byte in) {
        this.associatedText.write(in);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void processAADBytes(byte[] in, int inOff, int len) {
        this.associatedText.write(in, inOff, len);
    }

    private void processAAD(byte[] authText, int authOff, int len) {
        this.lambda_o = len * 8;
        this.engine.processBlock(this.H, 0, this.H, 0);
        int totalLength = len;
        int blockSize = authOff;
        while (true) {
            int inOff_ = blockSize;
            if (totalLength > 0) {
                for (int byteIndex = 0; byteIndex < this.engine.getBlockSize(); byteIndex++) {
                    byte[] bArr = this.b;
                    int i = byteIndex;
                    bArr[i] = (byte) (bArr[i] ^ authText[inOff_ + byteIndex]);
                }
                multiplyOverField(this.engine.getBlockSize() * 8, this.b, this.H, this.temp);
                this.temp = Arrays.reverse(this.temp);
                System.arraycopy(this.temp, 0, this.b, 0, this.engine.getBlockSize());
                totalLength -= this.engine.getBlockSize();
                blockSize = inOff_ + this.engine.getBlockSize();
            } else {
                return;
            }
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        this.data.write(in);
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (in.length < inOff + inLen) {
            throw new DataLengthException("input buffer too short");
        }
        this.data.write(in, inOff, inLen);
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int resultLen;
        int len = this.data.size();
        if (this.associatedText.size() > 0) {
            processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size());
        }
        if (this.forEncryption) {
            if (out.length - outOff < len + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            this.lambda_c = len * 8;
            int resultLen2 = this.ctrEngine.processBytes(this.data.getBuffer(), 0, len, out, outOff);
            resultLen = resultLen2 + this.ctrEngine.doFinal(out, outOff + resultLen2);
            calculateMac(out, outOff, len);
        } else {
            this.lambda_c = (len - this.macSize) * 8;
            calculateMac(this.data.getBuffer(), 0, len - this.macSize);
            int resultLen3 = this.ctrEngine.processBytes(this.data.getBuffer(), 0, len - this.macSize, out, outOff);
            resultLen = resultLen3 + this.ctrEngine.doFinal(out, outOff + resultLen3);
        }
        int outOff2 = outOff + resultLen;
        if (this.macBlock == null) {
            throw new IllegalStateException("mac is not calculated");
        }
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, out, outOff2, this.macSize);
            reset();
            return resultLen + this.macSize;
        }
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.data.getBuffer(), resultLen, mac, 0, this.macSize);
        byte[] calculatedMac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, calculatedMac, 0, this.macSize);
        if (!Arrays.constantTimeAreEqual(mac, calculatedMac)) {
            throw new InvalidCipherTextException("mac verification failed");
        }
        reset();
        return resultLen;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, this.macSize);
        return mac;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getUpdateOutputSize(int len) {
        return len;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getOutputSize(int len) {
        if (this.forEncryption) {
            return len;
        }
        return len + this.macSize;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void reset() {
        this.H = new byte[this.engine.getBlockSize()];
        this.b = new byte[this.engine.getBlockSize()];
        this.temp = new byte[this.engine.getBlockSize()];
        this.lambda_c = 0;
        this.lambda_o = 0;
        this.engine.reset();
        this.data.reset();
        this.associatedText.reset();
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void calculateMac(byte[] input, int inOff, int len) {
        this.macBlock = new byte[this.engine.getBlockSize()];
        int totalLength = len;
        int blockSize = inOff;
        while (true) {
            int inOff_ = blockSize;
            if (totalLength <= 0) {
                break;
            }
            for (int byteIndex = 0; byteIndex < this.engine.getBlockSize(); byteIndex++) {
                byte[] bArr = this.b;
                int i = byteIndex;
                bArr[i] = (byte) (bArr[i] ^ input[byteIndex + inOff_]);
            }
            multiplyOverField(this.engine.getBlockSize() * 8, this.b, this.H, this.temp);
            this.temp = Arrays.reverse(this.temp);
            System.arraycopy(this.temp, 0, this.b, 0, this.engine.getBlockSize());
            totalLength -= this.engine.getBlockSize();
            blockSize = inOff_ + this.engine.getBlockSize();
        }
        Arrays.fill(this.temp, (byte) 0);
        intToBytes(this.lambda_o, this.temp, 0);
        intToBytes(this.lambda_c, this.temp, this.engine.getBlockSize() / 2);
        for (int byteIndex2 = 0; byteIndex2 < this.engine.getBlockSize(); byteIndex2++) {
            byte[] bArr2 = this.b;
            int i2 = byteIndex2;
            bArr2[i2] = (byte) (bArr2[i2] ^ this.temp[byteIndex2]);
        }
        this.engine.processBlock(this.b, 0, this.macBlock, 0);
    }

    private void intToBytes(int num, byte[] outBytes, int outOff) {
        outBytes[outOff + 3] = (byte) (num >> 24);
        outBytes[outOff + 2] = (byte) (num >> 16);
        outBytes[outOff + 1] = (byte) (num >> 8);
        outBytes[outOff] = (byte) num;
    }

    private void multiplyOverField(int blockBitLength, byte[] x, byte[] y, byte[] x_mult_y) {
        BigInteger mask1;
        BigInteger mask2;
        BigInteger polyred;
        byte[] fieldOperationBuffer1 = new byte[this.engine.getBlockSize()];
        byte[] fieldOperationBuffer2 = new byte[this.engine.getBlockSize()];
        System.arraycopy(x, 0, fieldOperationBuffer1, 0, this.engine.getBlockSize());
        System.arraycopy(y, 0, fieldOperationBuffer2, 0, this.engine.getBlockSize());
        byte[] fieldOperationBuffer3 = Arrays.reverse(fieldOperationBuffer1);
        byte[] fieldOperationBuffer4 = Arrays.reverse(fieldOperationBuffer2);
        switch (blockBitLength) {
            case 128:
                mask1 = MASK_1_128;
                mask2 = MASK_2_128;
                polyred = POLYRED_128;
                break;
            case 256:
                mask1 = MASK_1_256;
                mask2 = MASK_2_256;
                polyred = POLYRED_256;
                break;
            case 512:
                mask1 = MASK_1_512;
                mask2 = MASK_2_512;
                polyred = POLYRED_512;
                break;
            default:
                mask1 = MASK_1_128;
                mask2 = MASK_2_128;
                polyred = POLYRED_128;
                break;
        }
        BigInteger p = ZERO;
        BigInteger p1 = new BigInteger(1, fieldOperationBuffer3);
        BigInteger bigInteger = new BigInteger(1, fieldOperationBuffer4);
        while (true) {
            BigInteger p2 = bigInteger;
            if (!p2.equals(ZERO)) {
                if (p2.and(ONE).equals(ONE)) {
                    p = p.xor(p1);
                }
                p1 = p1.shiftLeft(1);
                if (!p1.and(mask1).equals(ZERO)) {
                    p1 = p1.xor(polyred);
                }
                bigInteger = p2.shiftRight(1);
            } else {
                byte[] got = BigIntegers.asUnsignedByteArray(p.and(mask2));
                Arrays.fill(x_mult_y, (byte) 0);
                System.arraycopy(got, 0, x_mult_y, 0, got.length);
                return;
            }
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/KGCMBlockCipher$ExposedByteArrayOutputStream.class */
    private class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        public ExposedByteArrayOutputStream() {
        }

        public byte[] getBuffer() {
            return this.buf;
        }
    }
}
