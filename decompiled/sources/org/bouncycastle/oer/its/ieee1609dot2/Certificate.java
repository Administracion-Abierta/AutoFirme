package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/Certificate.class */
public class Certificate extends CertificateBase {
    public Certificate(UINT8 uint8, CertificateType certificateType, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uint8, certificateType, issuerIdentifier, toBeSignedCertificate, signature);
    }

    public Certificate(CertificateBase certificateBase) {
        this(certificateBase.getVersion(), certificateBase.getType(), certificateBase.getIssuer(), certificateBase.getToBeSigned(), certificateBase.getSignature());
    }

    protected Certificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static Certificate getInstance(Object obj) {
        if (obj instanceof Certificate) {
            return (Certificate) obj;
        }
        if (obj != null) {
            return new Certificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
