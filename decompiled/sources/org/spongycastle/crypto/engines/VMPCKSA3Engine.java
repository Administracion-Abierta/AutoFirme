package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/VMPCKSA3Engine.class */
public class VMPCKSA3Engine extends VMPCEngine {
    @Override // org.spongycastle.crypto.engines.VMPCEngine, org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "VMPC-KSA3";
    }

    @Override // org.spongycastle.crypto.engines.VMPCEngine
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
        for (int m3 = 0; m3 < 768; m3++) {
            this.s = this.P[(this.s + this.P[m3 & 255] + keyBytes[m3 % keyBytes.length]) & 255];
            byte temp3 = this.P[m3 & 255];
            this.P[m3 & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = temp3;
        }
        this.n = (byte) 0;
    }
}
