package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SkeinParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/SkeinMac.class */
public class SkeinMac implements Mac {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private SkeinEngine engine;

    public SkeinMac(int i, int i2) {
        this.engine = new SkeinEngine(i, i2);
    }

    public SkeinMac(SkeinMac skeinMac) {
        this.engine = new SkeinEngine(skeinMac.engine);
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "Skein-MAC-" + (this.engine.getBlockSize() * 8) + "-" + (this.engine.getOutputSize() * 8);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        SkeinParameters skeinParametersBuild;
        if (cipherParameters instanceof SkeinParameters) {
            skeinParametersBuild = (SkeinParameters) cipherParameters;
        } else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - " + cipherParameters.getClass().getName());
            }
            skeinParametersBuild = new SkeinParameters.Builder().setKey(((KeyParameter) cipherParameters).getKey()).build();
        }
        if (skeinParametersBuild.getKey() == null) {
            throw new IllegalArgumentException("Skein MAC requires a key parameter.");
        }
        this.engine.init(skeinParametersBuild);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.engine.getOutputSize();
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        this.engine.reset();
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) {
        this.engine.update(b);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        this.engine.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) {
        return this.engine.doFinal(bArr, i);
    }
}
