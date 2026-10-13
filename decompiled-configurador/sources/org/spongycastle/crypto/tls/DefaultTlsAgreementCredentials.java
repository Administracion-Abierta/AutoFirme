package org.spongycastle.crypto.tls;

import java.math.BigInteger;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.agreement.DHBasicAgreement;
import org.spongycastle.crypto.agreement.ECDHBasicAgreement;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DefaultTlsAgreementCredentials.class */
public class DefaultTlsAgreementCredentials extends AbstractTlsAgreementCredentials {
    protected Certificate certificate;
    protected AsymmetricKeyParameter privateKey;
    protected BasicAgreement basicAgreement;
    protected boolean truncateAgreement;

    public DefaultTlsAgreementCredentials(Certificate certificate, AsymmetricKeyParameter privateKey) {
        if (certificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        if (certificate.isEmpty()) {
            throw new IllegalArgumentException("'certificate' cannot be empty");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("'privateKey' cannot be null");
        }
        if (!privateKey.isPrivate()) {
            throw new IllegalArgumentException("'privateKey' must be private");
        }
        if (privateKey instanceof DHPrivateKeyParameters) {
            this.basicAgreement = new DHBasicAgreement();
            this.truncateAgreement = true;
        } else if (privateKey instanceof ECPrivateKeyParameters) {
            this.basicAgreement = new ECDHBasicAgreement();
            this.truncateAgreement = false;
        } else {
            throw new IllegalArgumentException("'privateKey' type not supported: " + privateKey.getClass().getName());
        }
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    @Override // org.spongycastle.crypto.tls.TlsCredentials
    public Certificate getCertificate() {
        return this.certificate;
    }

    @Override // org.spongycastle.crypto.tls.TlsAgreementCredentials
    public byte[] generateAgreement(AsymmetricKeyParameter peerPublicKey) {
        this.basicAgreement.init(this.privateKey);
        BigInteger agreementValue = this.basicAgreement.calculateAgreement(peerPublicKey);
        if (this.truncateAgreement) {
            return BigIntegers.asUnsignedByteArray(agreementValue);
        }
        return BigIntegers.asUnsignedByteArray(this.basicAgreement.getFieldSize(), agreementValue);
    }
}
