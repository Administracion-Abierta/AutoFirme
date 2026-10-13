package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMExtractor.class */
public class MLKEMExtractor implements EncapsulatedSecretExtractor {
    private final MLKEMPrivateKeyParameters privateKey;
    private final MLKEMEngine engine;

    public MLKEMExtractor(MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters) {
        if (mLKEMPrivateKeyParameters == null) {
            throw new NullPointerException("'privateKey' cannot be null");
        }
        this.privateKey = mLKEMPrivateKeyParameters;
        this.engine = mLKEMPrivateKeyParameters.getParameters().getEngine();
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretExtractor
    public byte[] extractSecret(byte[] bArr) {
        return this.engine.kemDecrypt(this.privateKey.getEncoded(), bArr);
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretExtractor
    public int getEncapsulationLength() {
        return this.engine.getCryptoCipherTextBytes();
    }
}
