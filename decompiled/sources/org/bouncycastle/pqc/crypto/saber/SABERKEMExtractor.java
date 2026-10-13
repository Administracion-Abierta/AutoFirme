package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/SABERKEMExtractor.class */
public class SABERKEMExtractor implements EncapsulatedSecretExtractor {
    private SABEREngine engine;
    private SABERKeyParameters key;

    public SABERKEMExtractor(SABERKeyParameters sABERKeyParameters) {
        this.key = sABERKeyParameters;
        initCipher(this.key.getParameters());
    }

    private void initCipher(SABERParameters sABERParameters) {
        this.engine = sABERParameters.getEngine();
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretExtractor
    public byte[] extractSecret(byte[] bArr) {
        byte[] bArr2 = new byte[this.engine.getSessionKeySize()];
        this.engine.crypto_kem_dec(bArr2, bArr, ((SABERPrivateKeyParameters) this.key).getPrivateKey());
        return bArr2;
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretExtractor
    public int getEncapsulationLength() {
        return this.engine.getCipherTextSize();
    }
}
