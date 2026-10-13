package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsECDSASigner.class */
public class TlsECDSASigner extends TlsDSASigner {
    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey) {
        return publicKey instanceof ECPublicKeyParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsDSASigner
    protected DSA createDSAImpl(short hashAlgorithm) {
        return new ECDSASigner(new HMacDSAKCalculator(TlsUtils.createHash(hashAlgorithm)));
    }

    @Override // org.spongycastle.crypto.tls.TlsDSASigner
    protected short getSignatureAlgorithm() {
        return (short) 3;
    }
}
