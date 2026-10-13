package org.spongycastle.crypto.engines;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/XSalsa20Engine.class */
public class XSalsa20Engine extends Salsa20Engine {
    @Override // org.spongycastle.crypto.engines.Salsa20Engine, org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "XSalsa20";
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected int getNonceSize() {
        return 24;
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void setKey(byte[] keyBytes, byte[] ivBytes) {
        if (keyBytes == null) {
            throw new IllegalArgumentException(getAlgorithmName() + " doesn't support re-init with null key");
        }
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires a 256 bit key");
        }
        super.setKey(keyBytes, ivBytes);
        Pack.littleEndianToInt(ivBytes, 8, this.engineState, 8, 2);
        int[] hsalsa20Out = new int[this.engineState.length];
        salsaCore(20, this.engineState, hsalsa20Out);
        this.engineState[1] = hsalsa20Out[0] - this.engineState[0];
        this.engineState[2] = hsalsa20Out[5] - this.engineState[5];
        this.engineState[3] = hsalsa20Out[10] - this.engineState[10];
        this.engineState[4] = hsalsa20Out[15] - this.engineState[15];
        this.engineState[11] = hsalsa20Out[6] - this.engineState[6];
        this.engineState[12] = hsalsa20Out[7] - this.engineState[7];
        this.engineState[13] = hsalsa20Out[8] - this.engineState[8];
        this.engineState[14] = hsalsa20Out[9] - this.engineState[9];
        Pack.littleEndianToInt(ivBytes, 16, this.engineState, 6, 2);
    }
}
