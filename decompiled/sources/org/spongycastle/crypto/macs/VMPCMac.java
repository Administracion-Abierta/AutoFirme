package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/macs/VMPCMac.class */
public class VMPCMac implements Mac {
    private byte g;
    private byte n = 0;
    private byte[] P = null;
    private byte s = 0;
    private byte[] T;
    private byte[] workingIV;
    private byte[] workingKey;
    private byte x1;
    private byte x2;
    private byte x3;
    private byte x4;

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        for (int r = 1; r < 25; r++) {
            this.s = this.P[(this.s + this.P[this.n & 255]) & 255];
            this.x4 = this.P[(this.x4 + this.x3 + r) & 255];
            this.x3 = this.P[(this.x3 + this.x2 + r) & 255];
            this.x2 = this.P[(this.x2 + this.x1 + r) & 255];
            this.x1 = this.P[(this.x1 + this.s + r) & 255];
            this.T[this.g & 31] = (byte) (this.T[this.g & 31] ^ this.x1);
            this.T[(this.g + 1) & 31] = (byte) (this.T[(this.g + 1) & 31] ^ this.x2);
            this.T[(this.g + 2) & 31] = (byte) (this.T[(this.g + 2) & 31] ^ this.x3);
            this.T[(this.g + 3) & 31] = (byte) (this.T[(this.g + 3) & 31] ^ this.x4);
            this.g = (byte) ((this.g + 4) & 31);
            byte temp = this.P[this.n & 255];
            this.P[this.n & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp;
            this.n = (byte) ((this.n + 1) & 255);
        }
        for (int m = 0; m < 768; m++) {
            this.s = this.P[(this.s + this.P[m & 255] + this.T[m & 31]) & 255];
            byte temp2 = this.P[m & 255];
            this.P[m & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp2;
        }
        byte[] M = new byte[20];
        for (int i = 0; i < 20; i++) {
            this.s = this.P[(this.s + this.P[i & 255]) & 255];
            M[i] = this.P[(this.P[this.P[this.s & 255] & 255] + 1) & 255];
            byte temp3 = this.P[i & 255];
            this.P[i & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp3;
        }
        System.arraycopy(M, 0, out, outOff, M.length);
        reset();
        return M.length;
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return "VMPC-MAC";
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return 20;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV) params;
        KeyParameter key = (KeyParameter) ivParams.getParameters();
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include a key");
        }
        this.workingIV = ivParams.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC-MAC requires 1 to 768 bytes of IV");
        }
        this.workingKey = key.getKey();
        reset();
    }

    private void initKey(byte[] keyBytes, byte[] ivBytes) {
        this.s = (byte) 0;
        this.P = new byte[256];
        for (int i = 0; i < 256; i++) {
            this.P[i] = (byte) i;
        }
        for (int m = 0; m < 768; m++) {
            this.s = this.P[(this.s + this.P[m & 255] + keyBytes[m % keyBytes.length]) & 255];
            byte temp = this.P[m & 255];
            this.P[m & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp;
        }
        for (int m2 = 0; m2 < 768; m2++) {
            this.s = this.P[(this.s + this.P[m2 & 255] + ivBytes[m2 % ivBytes.length]) & 255];
            byte temp2 = this.P[m2 & 255];
            this.P[m2 & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp2;
        }
        this.n = (byte) 0;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        initKey(this.workingKey, this.workingIV);
        this.n = (byte) 0;
        this.x4 = (byte) 0;
        this.x3 = (byte) 0;
        this.x2 = (byte) 0;
        this.x1 = (byte) 0;
        this.g = (byte) 0;
        this.T = new byte[32];
        for (int i = 0; i < 32; i++) {
            this.T[i] = 0;
        }
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) throws IllegalStateException {
        this.s = this.P[(this.s + this.P[this.n & 255]) & 255];
        byte c = (byte) (in ^ this.P[(this.P[this.P[this.s & 255] & 255] + 1) & 255]);
        this.x4 = this.P[(this.x4 + this.x3) & 255];
        this.x3 = this.P[(this.x3 + this.x2) & 255];
        this.x2 = this.P[(this.x2 + this.x1) & 255];
        this.x1 = this.P[(this.x1 + this.s + c) & 255];
        this.T[this.g & 31] = (byte) (this.T[this.g & 31] ^ this.x1);
        this.T[(this.g + 1) & 31] = (byte) (this.T[(this.g + 1) & 31] ^ this.x2);
        this.T[(this.g + 2) & 31] = (byte) (this.T[(this.g + 2) & 31] ^ this.x3);
        this.T[(this.g + 3) & 31] = (byte) (this.T[(this.g + 3) & 31] ^ this.x4);
        this.g = (byte) ((this.g + 4) & 31);
        byte temp = this.P[this.n & 255];
        this.P[this.n & 255] = this.P[this.s & 255];
        this.P[this.s & 255] = temp;
        this.n = (byte) ((this.n + 1) & 255);
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) throws IllegalStateException, DataLengthException {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (int i = 0; i < len; i++) {
            update(in[inOff + i]);
        }
    }
}
