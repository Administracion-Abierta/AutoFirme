package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/macs/GMac.class */
public class GMac implements Mac {
    private final GCMBlockCipher cipher;
    private final int macSizeBits;

    public GMac(GCMBlockCipher cipher) {
        this.cipher = cipher;
        this.macSizeBits = 128;
    }

    public GMac(GCMBlockCipher cipher, int macSizeBits) {
        this.cipher = cipher;
        this.macSizeBits = macSizeBits;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            ParametersWithIV param = (ParametersWithIV) params;
            byte[] iv = param.getIV();
            KeyParameter keyParam = (KeyParameter) param.getParameters();
            this.cipher.init(true, new AEADParameters(keyParam, this.macSizeBits, iv));
            return;
        }
        throw new IllegalArgumentException("GMAC requires ParametersWithIV");
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-GMAC";
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.macSizeBits / 8;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) throws IllegalStateException {
        this.cipher.processAADByte(in);
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) throws IllegalStateException, DataLengthException {
        this.cipher.processAADBytes(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        try {
            return this.cipher.doFinal(out, outOff);
        } catch (InvalidCipherTextException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        this.cipher.reset();
    }
}
