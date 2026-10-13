package org.spongycastle.crypto.engines;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ChaCha7539Engine.class */
public class ChaCha7539Engine extends Salsa20Engine {
    @Override // org.spongycastle.crypto.engines.Salsa20Engine, org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "ChaCha7539-" + this.rounds;
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected int getNonceSize() {
        return 12;
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void advanceCounter(long diff) {
        int hi = (int) (diff >>> 32);
        int lo = (int) diff;
        if (hi > 0) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
        }
        int oldState = this.engineState[12];
        int[] iArr = this.engineState;
        iArr[12] = iArr[12] + lo;
        if (oldState != 0 && this.engineState[12] < oldState) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
        }
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void advanceCounter() {
        int[] iArr = this.engineState;
        int i = iArr[12] + 1;
        iArr[12] = i;
        if (i == 0) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
        }
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void retreatCounter(long diff) {
        int hi = (int) (diff >>> 32);
        int lo = (int) diff;
        if (hi != 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        if ((((long) this.engineState[12]) & 4294967295L) >= (((long) lo) & 4294967295L)) {
            int[] iArr = this.engineState;
            iArr[12] = iArr[12] - lo;
            return;
        }
        throw new IllegalStateException("attempt to reduce counter past zero.");
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void retreatCounter() {
        if (this.engineState[12] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        int[] iArr = this.engineState;
        iArr[12] = iArr[12] - 1;
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected long getCounter() {
        return ((long) this.engineState[12]) & 4294967295L;
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void resetCounter() {
        this.engineState[12] = 0;
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void setKey(byte[] keyBytes, byte[] ivBytes) {
        if (keyBytes != null) {
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException(getAlgorithmName() + " requires 256 bit key");
            }
            packTauOrSigma(keyBytes.length, this.engineState, 0);
            Pack.littleEndianToInt(keyBytes, 0, this.engineState, 4, 8);
        }
        Pack.littleEndianToInt(ivBytes, 0, this.engineState, 13, 3);
    }

    @Override // org.spongycastle.crypto.engines.Salsa20Engine
    protected void generateKeyStream(byte[] output) {
        ChaChaEngine.chachaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, output, 0);
    }
}
