package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Zuc256CoreEngine;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/Zuc256Mac.class */
public final class Zuc256Mac implements Mac {
    private static final int TOPBIT = 128;
    private final InternalZuc256Engine theEngine;
    private final int theMacLength;
    private final int[] theMac;
    private final int[] theKeyStream;
    private Zuc256CoreEngine theState;
    private int theWordIndex;
    private int theByteIndex;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/Zuc256Mac$InternalZuc256Engine.class */
    private static class InternalZuc256Engine extends Zuc256CoreEngine {
        public InternalZuc256Engine(int i) {
            super(i);
        }

        int createKeyStreamWord() {
            return super.makeKeyStreamWord();
        }
    }

    public Zuc256Mac(int i) {
        this.theEngine = new InternalZuc256Engine(i);
        this.theMacLength = i;
        int i2 = i / 32;
        this.theMac = new int[i2];
        this.theKeyStream = new int[i2 + 1];
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "Zuc256Mac-" + this.theMacLength;
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.theMacLength / 8;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) {
        this.theEngine.init(true, cipherParameters);
        this.theState = (Zuc256CoreEngine) this.theEngine.copy();
        initKeyStream();
    }

    private void initKeyStream() {
        for (int i = 0; i < this.theMac.length; i++) {
            this.theMac[i] = this.theEngine.createKeyStreamWord();
        }
        for (int i2 = 0; i2 < this.theKeyStream.length - 1; i2++) {
            this.theKeyStream[i2] = this.theEngine.createKeyStreamWord();
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

    private void shift4Final() {
        this.theByteIndex = (this.theByteIndex + 1) % 4;
        if (this.theByteIndex == 0) {
            this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
        }
    }

    private void updateMac(int i) {
        for (int i2 = 0; i2 < this.theMac.length; i2++) {
            int[] iArr = this.theMac;
            int i3 = i2;
            iArr[i3] = iArr[i3] ^ getKeyStreamWord(i2, i);
        }
    }

    private int getKeyStreamWord(int i, int i2) {
        int i3 = this.theKeyStream[(this.theWordIndex + i) % this.theKeyStream.length];
        if (i2 == 0) {
            return i3;
        }
        return (i3 << i2) | (this.theKeyStream[((this.theWordIndex + i) + 1) % this.theKeyStream.length] >>> (32 - i2));
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            update(bArr[i + i3]);
        }
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) {
        shift4Final();
        updateMac(this.theByteIndex * 8);
        for (int i2 = 0; i2 < this.theMac.length; i2++) {
            Zuc256CoreEngine.encode32be(this.theMac[i2], bArr, i + (i2 * 4));
        }
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
