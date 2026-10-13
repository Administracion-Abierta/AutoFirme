package org.bouncycastle.crypto.fpe;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/fpe/FPEFF1Engine.class */
public class FPEFF1Engine extends FPEEngine {
    public FPEFF1Engine() {
        this(AESEngine.newInstance());
    }

    public FPEFF1Engine(BlockCipher blockCipher) {
        super(blockCipher);
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("base cipher needs to be 128 bits");
        }
        if (Properties.isOverrideSet("org.bouncycastle.fpe.disable") || Properties.isOverrideSet("org.bouncycastle.fpe.disable_ff1")) {
            throw new UnsupportedOperationException("FF1 encryption disabled");
        }
    }

    @Override // org.bouncycastle.crypto.fpe.FPEEngine
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forEncryption = z;
        this.fpeParameters = (FPEParameters) cipherParameters;
        this.baseCipher.init(!this.fpeParameters.isUsingInverseFunction(), this.fpeParameters.getKey());
    }

    @Override // org.bouncycastle.crypto.fpe.FPEEngine
    public String getAlgorithmName() {
        return "FF1";
    }

    @Override // org.bouncycastle.crypto.fpe.FPEEngine
    protected int encryptBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        System.arraycopy(this.fpeParameters.getRadix() > 256 ? toByteArray(SP80038G.encryptFF1w(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), toShortArray(bArr), i, i2 / 2)) : SP80038G.encryptFF1(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), bArr, i, i2), 0, bArr2, i3, i2);
        return i2;
    }

    @Override // org.bouncycastle.crypto.fpe.FPEEngine
    protected int decryptBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        System.arraycopy(this.fpeParameters.getRadix() > 256 ? toByteArray(SP80038G.decryptFF1w(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), toShortArray(bArr), i, i2 / 2)) : SP80038G.decryptFF1(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), bArr, i, i2), 0, bArr2, i3, i2);
        return i2;
    }
}
