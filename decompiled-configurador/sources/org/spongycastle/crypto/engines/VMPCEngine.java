package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/VMPCEngine.class */
public class VMPCEngine implements StreamCipher {
    protected byte n = 0;
    protected byte[] P = null;
    protected byte s = 0;
    protected byte[] workingIV;
    protected byte[] workingKey;

    @Override // org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "VMPC";
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV) params;
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC init parameters must include a key");
        }
        KeyParameter key = (KeyParameter) ivParams.getParameters();
        this.workingIV = ivParams.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC requires 1 to 768 bytes of IV");
        }
        this.workingKey = key.getKey();
        initKey(this.workingKey, this.workingIV);
    }

    protected void initKey(byte[] keyBytes, byte[] ivBytes) {
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

    @Override // org.spongycastle.crypto.StreamCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; i++) {
            this.s = this.P[(this.s + this.P[this.n & 255]) & 255];
            byte z = this.P[(this.P[this.P[this.s & 255] & 255] + 1) & 255];
            byte temp = this.P[this.n & 255];
            this.P[this.n & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp;
            this.n = (byte) ((this.n + 1) & 255);
            out[i + outOff] = (byte) (in[i + inOff] ^ z);
        }
        return len;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void reset() {
        initKey(this.workingKey, this.workingIV);
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public byte returnByte(byte in) {
        this.s = this.P[(this.s + this.P[this.n & 255]) & 255];
        byte z = this.P[(this.P[this.P[this.s & 255] & 255] + 1) & 255];
        byte temp = this.P[this.n & 255];
        this.P[this.n & 255] = this.P[this.s & 255];
        this.P[this.s & 255] = temp;
        this.n = (byte) ((this.n + 1) & 255);
        return (byte) (in ^ z);
    }
}
