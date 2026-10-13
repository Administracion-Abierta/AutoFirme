package org.bouncycastle.its.bc;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/bc/BcITSExplicitCertificateBuilder.class */
public class BcITSExplicitCertificateBuilder extends ITSExplicitCertificateBuilder {
    public BcITSExplicitCertificateBuilder(ITSContentSigner iTSContentSigner, ToBeSignedCertificate.Builder builder) {
        super(iTSContentSigner, builder);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKeyParameters eCPublicKeyParameters) {
        return build(certificateId, eCPublicKeyParameters, (ECPublicKeyParameters) null);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKeyParameters eCPublicKeyParameters, ECPublicKeyParameters eCPublicKeyParameters2) {
        BcITSPublicEncryptionKey bcITSPublicEncryptionKey = null;
        if (eCPublicKeyParameters2 != null) {
            bcITSPublicEncryptionKey = new BcITSPublicEncryptionKey(eCPublicKeyParameters2);
        }
        return super.build(certificateId, new BcITSPublicVerificationKey(eCPublicKeyParameters), bcITSPublicEncryptionKey);
    }
}
