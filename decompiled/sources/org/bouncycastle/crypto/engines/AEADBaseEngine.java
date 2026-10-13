package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AEADBaseEngine.class */
abstract class AEADBaseEngine implements AEADCipher {
    protected boolean forEncryption;
    protected String algorithmName;
    protected int KEY_SIZE;
    protected int IV_SIZE;
    protected int MAC_SIZE;
    protected byte[] initialAssociatedText;
    protected byte[] mac;

    AEADBaseEngine() {
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    public int getKeyBytesSize() {
        return this.KEY_SIZE;
    }

    public int getIVBytesSize() {
        return this.IV_SIZE;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        return this.mac;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        reset(true);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        return processBytes(new byte[]{b}, 0, 1, bArr, i);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        KeyParameter key;
        byte[] iv;
        this.forEncryption = z;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters) cipherParameters;
            key = aEADParameters.getKey();
            iv = aEADParameters.getNonce();
            this.initialAssociatedText = aEADParameters.getAssociatedText();
            int macSize = aEADParameters.getMacSize();
            if (macSize != this.MAC_SIZE * 8) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSize);
            }
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to " + this.algorithmName);
            }
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            key = (KeyParameter) parametersWithIV.getParameters();
            iv = parametersWithIV.getIV();
            this.initialAssociatedText = null;
        }
        if (key == null) {
            throw new IllegalArgumentException(this.algorithmName + " Init parameters must include a key");
        }
        if (iv == null || iv.length != this.IV_SIZE) {
            throw new IllegalArgumentException(this.algorithmName + " requires exactly " + this.IV_SIZE + " bytes of IV");
        }
        byte[] key2 = key.getKey();
        if (key2.length != this.KEY_SIZE) {
            throw new IllegalArgumentException(this.algorithmName + " key must be " + this.KEY_SIZE + " bytes long");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 128, cipherParameters, Utils.getPurpose(z)));
        init(key2, iv);
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    protected abstract void init(byte[] bArr, byte[] bArr2);

    protected void reset(boolean z) {
        if (z) {
            this.mac = null;
        }
    }
}
