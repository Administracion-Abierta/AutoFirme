package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/ChaCha20Poly1305.class */
public class ChaCha20Poly1305 implements AEADCipher {
    private static final int BUF_SIZE = 64;
    private static final int KEY_SIZE = 32;
    private static final int NONCE_SIZE = 12;
    private static final int MAC_SIZE = 16;
    private static final byte[] ZEROES = new byte[15];
    private static final long AAD_LIMIT = -1;
    private static final long DATA_LIMIT = 274877906880L;
    private final ChaCha7539Engine chacha20;
    private final Mac poly1305;
    private final byte[] key;
    private final byte[] nonce;
    private final byte[] buf;
    private final byte[] mac;
    private byte[] initialAAD;
    private long aadCount;
    private long dataCount;
    private int state;
    private int bufPos;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/ChaCha20Poly1305$State.class */
    private static final class State {
        static final int UNINITIALIZED = 0;
        static final int ENC_INIT = 1;
        static final int ENC_AAD = 2;
        static final int ENC_DATA = 3;
        static final int ENC_FINAL = 4;
        static final int DEC_INIT = 5;
        static final int DEC_AAD = 6;
        static final int DEC_DATA = 7;
        static final int DEC_FINAL = 8;

        private State() {
        }
    }

    public ChaCha20Poly1305() {
        this(new Poly1305());
    }

    public ChaCha20Poly1305(Mac mac) {
        this.key = new byte[32];
        this.nonce = new byte[12];
        this.buf = new byte[80];
        this.mac = new byte[16];
        this.state = 0;
        if (null == mac) {
            throw new NullPointerException("'poly1305' cannot be null");
        }
        if (16 != mac.getMacSize()) {
            throw new IllegalArgumentException("'poly1305' must be a 128-bit MAC");
        }
        this.chacha20 = new ChaCha7539Engine();
        this.poly1305 = mac;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return "ChaCha20Poly1305";
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        KeyParameter key;
        byte[] iv;
        ParametersWithIV parametersWithIV;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters) cipherParameters;
            int macSize = aEADParameters.getMacSize();
            if (128 != macSize) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSize);
            }
            key = aEADParameters.getKey();
            iv = aEADParameters.getNonce();
            parametersWithIV = new ParametersWithIV(key, iv);
            this.initialAAD = aEADParameters.getAssociatedText();
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to ChaCha20Poly1305");
            }
            ParametersWithIV parametersWithIV2 = (ParametersWithIV) cipherParameters;
            key = (KeyParameter) parametersWithIV2.getParameters();
            iv = parametersWithIV2.getIV();
            parametersWithIV = parametersWithIV2;
            this.initialAAD = null;
        }
        if (null == key) {
            if (0 == this.state) {
                throw new IllegalArgumentException("Key must be specified in initial init");
            }
        } else if (32 != key.getKeyLength()) {
            throw new IllegalArgumentException("Key must be 256 bits");
        }
        if (null == iv || 12 != iv.length) {
            throw new IllegalArgumentException("Nonce must be 96 bits");
        }
        if (0 != this.state && z && Arrays.areEqual(this.nonce, iv) && (null == key || Arrays.areEqual(this.key, key.getKey()))) {
            throw new IllegalArgumentException("cannot reuse nonce for ChaCha20Poly1305 encryption");
        }
        if (null != key) {
            key.copyTo(this.key, 0, 32);
        }
        System.arraycopy(iv, 0, this.nonce, 0, 12);
        this.chacha20.init(true, parametersWithIV);
        this.state = z ? 1 : 5;
        reset(true, false);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        int iMax = Math.max(0, i) + this.bufPos;
        switch (this.state) {
            case 1:
            case 2:
            case 3:
                return iMax + 16;
            case 4:
            default:
                throw new IllegalStateException();
            case 5:
            case 6:
            case 7:
                return Math.max(0, iMax - 16);
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        int iMax = Math.max(0, i) + this.bufPos;
        switch (this.state) {
            case 1:
            case 2:
            case 3:
                break;
            case 4:
            default:
                throw new IllegalStateException();
            case 5:
            case 6:
            case 7:
                iMax = Math.max(0, iMax - 16);
                break;
        }
        return iMax - (iMax % 64);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        checkAAD();
        this.aadCount = incrementCount(this.aadCount, 1, AAD_LIMIT);
        this.poly1305.update(b);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        if (null == bArr) {
            throw new NullPointerException("'in' cannot be null");
        }
        if (i < 0) {
            throw new IllegalArgumentException("'inOff' cannot be negative");
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
        }
        if (i > bArr.length - i2) {
            throw new DataLengthException("Input buffer too short");
        }
        checkAAD();
        if (i2 > 0) {
            this.aadCount = incrementCount(this.aadCount, i2, AAD_LIMIT);
            this.poly1305.update(bArr, i, i2);
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        checkData();
        switch (this.state) {
            case 3:
                this.buf[this.bufPos] = b;
                int i2 = this.bufPos + 1;
                this.bufPos = i2;
                if (i2 != 64) {
                    return 0;
                }
                processData(this.buf, 0, 64, bArr, i);
                this.poly1305.update(bArr, i, 64);
                this.bufPos = 0;
                return 64;
            case 7:
                this.buf[this.bufPos] = b;
                int i3 = this.bufPos + 1;
                this.bufPos = i3;
                if (i3 != this.buf.length) {
                    return 0;
                }
                this.poly1305.update(this.buf, 0, 64);
                processData(this.buf, 0, 64, bArr, i);
                System.arraycopy(this.buf, 64, this.buf, 0, 16);
                this.bufPos = 16;
                return 64;
            default:
                throw new IllegalStateException();
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (null == bArr) {
            throw new NullPointerException("'in' cannot be null");
        }
        if (null == bArr2) {
        }
        if (i < 0) {
            throw new IllegalArgumentException("'inOff' cannot be negative");
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
        }
        if (i > bArr.length - i2) {
            throw new DataLengthException("Input buffer too short");
        }
        if (i3 < 0) {
            throw new IllegalArgumentException("'outOff' cannot be negative");
        }
        checkData();
        int i4 = 0;
        switch (this.state) {
            case 3:
                if (this.bufPos != 0) {
                    while (i2 > 0) {
                        i2--;
                        int i5 = i;
                        i++;
                        this.buf[this.bufPos] = bArr[i5];
                        int i6 = this.bufPos + 1;
                        this.bufPos = i6;
                        if (i6 == 64) {
                            processData(this.buf, 0, 64, bArr2, i3);
                            this.poly1305.update(bArr2, i3, 64);
                            this.bufPos = 0;
                            i4 = 64;
                        }
                    }
                }
                while (i2 >= 64) {
                    processData(bArr, i, 64, bArr2, i3 + i4);
                    this.poly1305.update(bArr2, i3 + i4, 64);
                    i += 64;
                    i2 -= 64;
                    i4 += 64;
                }
                if (i2 > 0) {
                    System.arraycopy(bArr, i, this.buf, 0, i2);
                    this.bufPos = i2;
                }
                break;
            case 7:
                for (int i7 = 0; i7 < i2; i7++) {
                    this.buf[this.bufPos] = bArr[i + i7];
                    int i8 = this.bufPos + 1;
                    this.bufPos = i8;
                    if (i8 == this.buf.length) {
                        this.poly1305.update(this.buf, 0, 64);
                        processData(this.buf, 0, 64, bArr2, i3 + i4);
                        System.arraycopy(this.buf, 64, this.buf, 0, 16);
                        this.bufPos = 16;
                        i4 += 64;
                    }
                }
                break;
            default:
                throw new IllegalStateException();
        }
        return i4;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        int i2;
        if (null == bArr) {
            throw new NullPointerException("'out' cannot be null");
        }
        if (i < 0) {
            throw new IllegalArgumentException("'outOff' cannot be negative");
        }
        checkData();
        Arrays.clear(this.mac);
        switch (this.state) {
            case 3:
                i2 = this.bufPos + 16;
                if (i > bArr.length - i2) {
                    throw new OutputLengthException("Output buffer too short");
                }
                if (this.bufPos > 0) {
                    processData(this.buf, 0, this.bufPos, bArr, i);
                    this.poly1305.update(bArr, i, this.bufPos);
                }
                finishData(4);
                System.arraycopy(this.mac, 0, bArr, i + this.bufPos, 16);
                break;
            case 7:
                if (this.bufPos < 16) {
                    throw new InvalidCipherTextException("data too short");
                }
                i2 = this.bufPos - 16;
                if (i > bArr.length - i2) {
                    throw new OutputLengthException("Output buffer too short");
                }
                if (i2 > 0) {
                    this.poly1305.update(this.buf, 0, i2);
                    processData(this.buf, 0, i2, bArr, i);
                }
                finishData(8);
                if (!Arrays.constantTimeAreEqual(16, this.mac, 0, this.buf, i2)) {
                    throw new InvalidCipherTextException("mac check in ChaCha20Poly1305 failed");
                }
                break;
            default:
                throw new IllegalStateException();
        }
        reset(false, true);
        return i2;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        reset(true, true);
    }

    private void checkAAD() {
        switch (this.state) {
            case 1:
                this.state = 2;
                return;
            case 2:
            case 6:
                return;
            case 3:
            default:
                throw new IllegalStateException();
            case 4:
                throw new IllegalStateException("ChaCha20Poly1305 cannot be reused for encryption");
            case 5:
                this.state = 6;
                return;
        }
    }

    private void checkData() {
        switch (this.state) {
            case 1:
            case 2:
                finishAAD(3);
                return;
            case 3:
            case 7:
                return;
            case 4:
                throw new IllegalStateException("ChaCha20Poly1305 cannot be reused for encryption");
            case 5:
            case 6:
                finishAAD(7);
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void finishAAD(int i) {
        padMAC(this.aadCount);
        this.state = i;
    }

    private void finishData(int i) {
        padMAC(this.dataCount);
        byte[] bArr = new byte[16];
        Pack.longToLittleEndian(this.aadCount, bArr, 0);
        Pack.longToLittleEndian(this.dataCount, bArr, 8);
        this.poly1305.update(bArr, 0, 16);
        this.poly1305.doFinal(this.mac, 0);
        this.state = i;
    }

    private long incrementCount(long j, int i, long j2) {
        if (j - Long.MIN_VALUE > (j2 - ((long) i)) - Long.MIN_VALUE) {
            throw new IllegalStateException("Limit exceeded");
        }
        return j + ((long) i);
    }

    private void initMAC() {
        byte[] bArr = new byte[64];
        try {
            this.chacha20.processBytes(bArr, 0, 64, bArr, 0);
            this.poly1305.init(new KeyParameter(bArr, 0, 32));
        } finally {
            Arrays.clear(bArr);
        }
    }

    private void padMAC(long j) {
        int i = ((int) j) & 15;
        if (0 != i) {
            this.poly1305.update(ZEROES, 0, 16 - i);
        }
    }

    private void processData(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (i3 > bArr2.length - i2) {
            throw new OutputLengthException("Output buffer too short");
        }
        this.chacha20.processBytes(bArr, i, i2, bArr2, i3);
        this.dataCount = incrementCount(this.dataCount, i2, DATA_LIMIT);
    }

    private void reset(boolean z, boolean z2) {
        Arrays.clear(this.buf);
        if (z) {
            Arrays.clear(this.mac);
        }
        this.aadCount = 0L;
        this.dataCount = 0L;
        this.bufPos = 0;
        switch (this.state) {
            case 1:
            case 5:
                break;
            case 2:
            case 3:
            case 4:
                this.state = 4;
                return;
            case 6:
            case 7:
            case 8:
                this.state = 5;
                break;
            default:
                throw new IllegalStateException();
        }
        if (z2) {
            this.chacha20.reset();
        }
        initMAC();
        if (null != this.initialAAD) {
            processAADBytes(this.initialAAD, 0, this.initialAAD.length);
        }
    }
}
