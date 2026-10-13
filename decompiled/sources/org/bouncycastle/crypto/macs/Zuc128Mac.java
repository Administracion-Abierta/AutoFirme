package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Zuc128CoreEngine;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/Zuc128Mac.class */
public final class Zuc128Mac implements Mac {
    private static final int TOPBIT = 128;
    private int theMac;
    private Zuc128CoreEngine theState;
    private int theWordIndex;
    private int theByteIndex;
    private final InternalZuc128Engine theEngine = new InternalZuc128Engine();
    private final int[] theKeyStream = new int[2];

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/Zuc128Mac$InternalZuc128Engine.class */
    private static class InternalZuc128Engine extends Zuc128CoreEngine {
        private InternalZuc128Engine() {
        }

        int createKeyStreamWord() {
            return super.makeKeyStreamWord();
        }
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "Zuc128Mac";
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return 4;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) {
        this.theEngine.init(true, cipherParameters);
        this.theState = (Zuc128CoreEngine) this.theEngine.copy();
        initKeyStream();
    }

    private void initKeyStream() {
        this.theMac = 0;
        for (int i = 0; i < this.theKeyStream.length - 1; i++) {
            this.theKeyStream[i] = this.theEngine.createKeyStreamWord();
        }
        this.theWordIndex = this.theKeyStream.length - 1;
        this.theByteIndex = 3;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) {
        shift4NextByte();
        int i = this.theByteIndex * 8;
        int i2 = 128;
        int i3 = 0;
        while (i2 > 0) {
            if ((b & i2) != 0) {
                updateMac(i + i3);
            }
            i2 >>= 1;
            i3++;
        }
    }

    private void shift4NextByte() {
        this.theByteIndex = (this.theByteIndex + 1) % 4;
        if (this.theByteIndex == 0) {
            this.theKeyStream[this.theWordIndex] = this.theEngine.createKeyStreamWord();
            this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
        }
    }

    private void updateMac(int i) {
        this.theMac ^= getKeyStreamWord(i);
    }

    private int getKeyStreamWord(int i) {
        int i2 = this.theKeyStream[this.theWordIndex];
        if (i == 0) {
            return i2;
        }
        return (i2 << i) | (this.theKeyStream[(this.theWordIndex + 1) % this.theKeyStream.length] >>> (32 - i));
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            update(bArr[i + i3]);
        }
    }

    private int getFinalWord() {
        if (this.theByteIndex != 0) {
            return this.theEngine.createKeyStreamWord();
        }
        this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
        return this.theKeyStream[this.theWordIndex];
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) {
        shift4NextByte();
        this.theMac ^= getKeyStreamWord(this.theByteIndex * 8);
        this.theMac ^= getFinalWord();
        Zuc128CoreEngine.encode32be(this.theMac, bArr, i);
        reset();
        return getMacSize();
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        if (this.theState != null) {
            this.theEngine.reset(this.theState);
        }
        initKeyStream();
    }
}
