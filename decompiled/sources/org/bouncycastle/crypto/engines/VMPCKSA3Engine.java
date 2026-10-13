package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/VMPCKSA3Engine.class */
public class VMPCKSA3Engine extends VMPCEngine {
    @Override // org.bouncycastle.crypto.engines.VMPCEngine, org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "VMPC-KSA3";
    }

    @Override // org.bouncycastle.crypto.engines.VMPCEngine
    protected void initKey(byte[] bArr, byte[] bArr2) {
        this.s = (byte) 0;
        this.P = new byte[256];
        for (int i = 0; i < 256; i++) {
            this.P[i] = (byte) i;
        }
        for (int i2 = 0; i2 < 768; i2++) {
            this.s = this.P[(this.s + this.P[i2 & 255] + bArr[i2 % bArr.length]) & 255];
            byte b = this.P[i2 & 255];
            this.P[i2 & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = b;
        }
        for (int i3 = 0; i3 < 768; i3++) {
            this.s = this.P[(this.s + this.P[i3 & 255] + bArr2[i3 % bArr2.length]) & 255];
            byte b2 = this.P[i3 & 255];
            this.P[i3 & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = b2;
        }
        for (int i4 = 0; i4 < 768; i4++) {
            this.s = this.P[(this.s + this.P[i4 & 255] + bArr[i4 % bArr.length]) & 255];
            byte b3 = this.P[i4 & 255];
            this.P[i4 & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = b3;
        }
        this.n = (byte) 0;
    }
}
