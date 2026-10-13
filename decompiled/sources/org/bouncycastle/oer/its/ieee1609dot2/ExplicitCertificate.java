package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/ExplicitCertificate.class */
public class ExplicitCertificate extends CertificateBase {
    public ExplicitCertificate(CertificateBase certificateBase) {
        this(certificateBase.getVersion(), certificateBase.getIssuer(), certificateBase.getToBeSigned(), certificateBase.getSignature());
    }

    public ExplicitCertificate(UINT8 uint8, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uint8, CertificateType.explicit, issuerIdentifier, toBeSignedCertificate, signature);
    }

    protected ExplicitCertificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        if (!getType().equals((ASN1Primitive) CertificateType.explicit)) {
            throw new IllegalArgumentException("object was certificate base but the type was not explicit");
        }
    }

    public static ExplicitCertificate getInstance(Object obj) {
        if (obj instanceof ExplicitCertificate) {
            return (ExplicitCertificate) obj;
        }
        if (obj != null) {
            return new ExplicitCertificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
