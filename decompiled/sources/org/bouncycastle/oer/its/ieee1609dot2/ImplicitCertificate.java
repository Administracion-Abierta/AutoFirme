package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/ImplicitCertificate.class */
public class ImplicitCertificate extends CertificateBase {
    public ImplicitCertificate(CertificateBase certificateBase) {
        this(certificateBase.getVersion(), certificateBase.getIssuer(), certificateBase.getToBeSigned(), certificateBase.getSignature());
    }

    public ImplicitCertificate(UINT8 uint8, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uint8, CertificateType.implicit, issuerIdentifier, toBeSignedCertificate, signature);
    }

    private ImplicitCertificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        if (!getType().equals((ASN1Primitive) CertificateType.implicit)) {
            throw new IllegalArgumentException("object was certificate base but the type was not implicit");
        }
    }

    public static ImplicitCertificate getInstance(Object obj) {
        if (obj instanceof ImplicitCertificate) {
            return (ImplicitCertificate) obj;
        }
        if (obj != null) {
            return new ImplicitCertificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
