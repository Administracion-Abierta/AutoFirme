package org.spongycastle.crypto.macs;

import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.Winspool;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithSBox;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/macs/GOST28147Mac.class */
public class GOST28147Mac implements Mac {
    private int blockSize = 8;
    private int macSize = 4;
    private boolean firstStep = true;
    private int[] workingKey = null;
    private byte[] macIV = null;
    private byte[] S = {9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 6, 2, 3, 9, 1, 7, 13, 4};
    private byte[] mac = new byte[this.blockSize];
    private byte[] buf = new byte[this.blockSize];
    private int bufOff = 0;

    private int[] generateWorkingKey(byte[] userKey) {
        if (userKey.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        int[] key = new int[8];
        for (int i = 0; i != 8; i++) {
            key[i] = bytesToint(userKey, i * 4);
        }
        return key;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) throws IllegalArgumentException {
        reset();
        this.buf = new byte[this.blockSize];
        this.macIV = null;
        if (params instanceof ParametersWithSBox) {
            ParametersWithSBox param = (ParametersWithSBox) params;
            System.arraycopy(param.getSBox(), 0, this.S, 0, param.getSBox().length);
            if (param.getParameters() != null) {
                this.workingKey = generateWorkingKey(((KeyParameter) param.getParameters()).getKey());
                return;
            }
            return;
        }
        if (params instanceof KeyParameter) {
            this.workingKey = generateWorkingKey(((KeyParameter) params).getKey());
        } else {
            if (params instanceof ParametersWithIV) {
                ParametersWithIV p = (ParametersWithIV) params;
                this.workingKey = generateWorkingKey(((KeyParameter) p.getParameters()).getKey());
                System.arraycopy(p.getIV(), 0, this.mac, 0, this.mac.length);
                this.macIV = p.getIV();
                return;
            }
            throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + params.getClass().getName());
        }
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return "GOST28147Mac";
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    private int gost28147_mainStep(int n1, int key) {
        int cm = key + n1;
        int om = (this.S[0 + ((cm >> 0) & 15)] << 0) + (this.S[16 + ((cm >> 4) & 15)] << 4) + (this.S[32 + ((cm >> 8) & 15)] << 8) + (this.S[48 + ((cm >> 12) & 15)] << 12) + (this.S[64 + ((cm >> 16) & 15)] << 16) + (this.S[80 + ((cm >> 20) & 15)] << 20) + (this.S[96 + ((cm >> 24) & 15)] << 24) + (this.S[112 + ((cm >> 28) & 15)] << 28);
        return (om << 11) | (om >>> 21);
    }

    private void gost28147MacFunc(int[] workingKey, byte[] in, int inOff, byte[] out, int outOff) {
        int N1 = bytesToint(in, inOff);
        int N2 = bytesToint(in, inOff + 4);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < 8; j++) {
                int tmp = N1;
                N1 = N2 ^ gost28147_mainStep(N1, workingKey[j]);
                N2 = tmp;
            }
        }
        intTobytes(N1, out, outOff);
        intTobytes(N2, out, outOff + 4);
    }

    private int bytesToint(byte[] in, int inOff) {
        return ((in[inOff + 3] << 24) & Ddeml.MF_MASK) + ((in[inOff + 2] << 16) & Winspool.PRINTER_ENUM_ICONMASK) + ((in[inOff + 1] << 8) & 65280) + (in[inOff] & 255);
    }

    private void intTobytes(int num, byte[] out, int outOff) {
        out[outOff + 3] = (byte) (num >>> 24);
        out[outOff + 2] = (byte) (num >>> 16);
        out[outOff + 1] = (byte) (num >>> 8);
        out[outOff] = (byte) num;
    }

    private byte[] CM5func(byte[] buf, int bufOff, byte[] mac) {
        byte[] sum = new byte[buf.length - bufOff];
        System.arraycopy(buf, bufOff, sum, 0, mac.length);
        for (int i = 0; i != mac.length; i++) {
            sum[i] = (byte) (sum[i] ^ mac[i]);
        }
        return sum;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) throws IllegalStateException {
        if (this.bufOff == this.buf.length) {
            byte[] sumbuf = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, sumbuf, 0, this.mac.length);
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    sumbuf = CM5func(this.buf, 0, this.macIV);
                }
            } else {
                sumbuf = CM5func(this.buf, 0, this.mac);
            }
            gost28147MacFunc(this.workingKey, sumbuf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) throws IllegalStateException, DataLengthException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int gapLen = this.blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            byte[] sumbuf = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, sumbuf, 0, this.mac.length);
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    sumbuf = CM5func(this.buf, 0, this.macIV);
                }
            } else {
                sumbuf = CM5func(this.buf, 0, this.mac);
            }
            gost28147MacFunc(this.workingKey, sumbuf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            int i = inOff;
            int i2 = gapLen;
            while (true) {
                inOff = i + i2;
                if (len <= this.blockSize) {
                    break;
                }
                gost28147MacFunc(this.workingKey, CM5func(in, inOff, this.mac), 0, this.mac, 0);
                len -= this.blockSize;
                i = inOff;
                i2 = this.blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        while (this.bufOff < this.blockSize) {
            this.buf[this.bufOff] = 0;
            this.bufOff++;
        }
        byte[] sumbuf = new byte[this.buf.length];
        System.arraycopy(this.buf, 0, sumbuf, 0, this.mac.length);
        if (this.firstStep) {
            this.firstStep = false;
        } else {
            sumbuf = CM5func(this.buf, 0, this.mac);
        }
        gost28147MacFunc(this.workingKey, sumbuf, 0, this.mac, 0);
        System.arraycopy(this.mac, (this.mac.length / 2) - this.macSize, out, outOff, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.firstStep = true;
    }
}
