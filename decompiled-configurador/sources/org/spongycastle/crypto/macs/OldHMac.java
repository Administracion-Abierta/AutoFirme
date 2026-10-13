package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/macs/OldHMac.class */
public class OldHMac implements Mac {
    private static final int BLOCK_LENGTH = 64;
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private Digest digest;
    private int digestSize;
    private byte[] inputPad = new byte[64];
    private byte[] outputPad = new byte[64];

    public OldHMac(Digest digest) {
        this.digest = digest;
        this.digestSize = digest.getDigestSize();
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/HMAC";
    }

    public Digest getUnderlyingDigest() {
        return this.digest;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) {
        this.digest.reset();
        byte[] key = ((KeyParameter) params).getKey();
        if (key.length > 64) {
            this.digest.update(key, 0, key.length);
            this.digest.doFinal(this.inputPad, 0);
            for (int i = this.digestSize; i < this.inputPad.length; i++) {
                this.inputPad[i] = 0;
            }
        } else {
            System.arraycopy(key, 0, this.inputPad, 0, key.length);
            for (int i2 = key.length; i2 < this.inputPad.length; i2++) {
                this.inputPad[i2] = 0;
            }
        }
        this.outputPad = new byte[this.inputPad.length];
        System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
        for (int i3 = 0; i3 < this.inputPad.length; i3++) {
            byte[] bArr = this.inputPad;
            int i4 = i3;
            bArr[i4] = (byte) (bArr[i4] ^ 54);
        }
        for (int i5 = 0; i5 < this.outputPad.length; i5++) {
            byte[] bArr2 = this.outputPad;
            int i6 = i5;
            bArr2[i6] = (byte) (bArr2[i6] ^ OPAD);
        }
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.digestSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) {
        this.digest.update(in);
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) {
        this.digest.update(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) {
        byte[] tmp = new byte[this.digestSize];
        this.digest.doFinal(tmp, 0);
        this.digest.update(this.outputPad, 0, this.outputPad.length);
        this.digest.update(tmp, 0, tmp.length);
        int len = this.digest.doFinal(out, outOff);
        reset();
        return len;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        this.digest.reset();
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
}
