package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/OldHMac.class */
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

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/HMAC";
    }

    public Digest getUnderlyingDigest() {
        return this.digest;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) {
        this.digest.reset();
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        if (key.length > 64) {
            this.digest.update(key, 0, key.length);
            this.digest.doFinal(this.inputPad, 0);
            for (int i = this.digestSize; i < this.inputPad.length; i++) {
                this.inputPad[i] = 0;
            }
        } else {
            System.arraycopy(key, 0, this.inputPad, 0, key.length);
            for (int length = key.length; length < this.inputPad.length; length++) {
                this.inputPad[length] = 0;
            }
        }
        this.outputPad = new byte[this.inputPad.length];
        System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
        for (int i2 = 0; i2 < this.inputPad.length; i2++) {
            byte[] bArr = this.inputPad;
            int i3 = i2;
            bArr[i3] = (byte) (bArr[i3] ^ 54);
        }
        for (int i4 = 0; i4 < this.outputPad.length; i4++) {
            byte[] bArr2 = this.outputPad;
            int i5 = i4;
            bArr2[i5] = (byte) (bArr2[i5] ^ 92);
        }
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.digestSize;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) {
        this.digest.update(b);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        this.digest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) {
        byte[] bArr2 = new byte[this.digestSize];
        this.digest.doFinal(bArr2, 0);
        this.digest.update(this.outputPad, 0, this.outputPad.length);
        this.digest.update(bArr2, 0, bArr2.length);
        int iDoFinal = this.digest.doFinal(bArr, i);
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        this.digest.reset();
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
}
