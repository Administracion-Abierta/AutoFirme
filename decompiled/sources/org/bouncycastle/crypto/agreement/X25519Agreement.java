package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/X25519Agreement.class */
public final class X25519Agreement implements RawAgreement {
    private X25519PrivateKeyParameters privateKey;

    @Override // org.bouncycastle.crypto.RawAgreement
    public void init(CipherParameters cipherParameters) {
        this.privateKey = (X25519PrivateKeyParameters) cipherParameters;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(XDHParameterSpec.X25519, this.privateKey));
    }

    @Override // org.bouncycastle.crypto.RawAgreement
    public int getAgreementSize() {
        return 32;
    }

    @Override // org.bouncycastle.crypto.RawAgreement
    public void calculateAgreement(CipherParameters cipherParameters, byte[] bArr, int i) {
        this.privateKey.generateSecret((X25519PublicKeyParameters) cipherParameters, bArr, i);
    }
}
