package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.macs.GOST28147Mac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.ParametersWithSBox;
import org.spongycastle.crypto.params.ParametersWithUKM;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/GOST28147WrapEngine.class */
public class GOST28147WrapEngine implements Wrapper {
    private GOST28147Engine cipher = new GOST28147Engine();
    private GOST28147Mac mac = new GOST28147Mac();

    @Override // org.spongycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        KeyParameter kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pr = (ParametersWithRandom) param;
            param = pr.getParameters();
        }
        ParametersWithUKM pU = (ParametersWithUKM) param;
        this.cipher.init(forWrapping, pU.getParameters());
        if (pU.getParameters() instanceof ParametersWithSBox) {
            kParam = (KeyParameter) ((ParametersWithSBox) pU.getParameters()).getParameters();
        } else {
            kParam = (KeyParameter) pU.getParameters();
        }
        this.mac.init(new ParametersWithIV(kParam, pU.getUKM()));
    }

    @Override // org.spongycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return "GOST28147Wrap";
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] wrap(byte[] input, int inOff, int inLen) {
        this.mac.update(input, inOff, inLen);
        byte[] wrappedKey = new byte[inLen + this.mac.getMacSize()];
        this.cipher.processBlock(input, inOff, wrappedKey, 0);
        this.cipher.processBlock(input, inOff + 8, wrappedKey, 8);
        this.cipher.processBlock(input, inOff + 16, wrappedKey, 16);
        this.cipher.processBlock(input, inOff + 24, wrappedKey, 24);
        this.mac.doFinal(wrappedKey, inLen);
        return wrappedKey;
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] unwrap(byte[] input, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] decKey = new byte[inLen - this.mac.getMacSize()];
        this.cipher.processBlock(input, inOff, decKey, 0);
        this.cipher.processBlock(input, inOff + 8, decKey, 8);
        this.cipher.processBlock(input, inOff + 16, decKey, 16);
        this.cipher.processBlock(input, inOff + 24, decKey, 24);
        byte[] macResult = new byte[this.mac.getMacSize()];
        this.mac.update(decKey, 0, decKey.length);
        this.mac.doFinal(macResult, 0);
        byte[] macExpected = new byte[this.mac.getMacSize()];
        System.arraycopy(input, (inOff + inLen) - 4, macExpected, 0, this.mac.getMacSize());
        if (!Arrays.constantTimeAreEqual(macResult, macExpected)) {
            throw new IllegalStateException("mac mismatch");
        }
        return decKey;
    }
}
