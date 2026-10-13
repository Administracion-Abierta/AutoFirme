package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.ExplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi103097/EtsiTs103097Certificate.class */
public class EtsiTs103097Certificate extends ExplicitCertificate {
    public EtsiTs103097Certificate(UINT8 uint8, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uint8, issuerIdentifier, toBeSignedCertificate, signature);
    }

    protected EtsiTs103097Certificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097Certificate getInstance(Object obj) {
        if (obj instanceof EtsiTs103097Certificate) {
            return (EtsiTs103097Certificate) obj;
        }
        if (obj != null) {
            return new EtsiTs103097Certificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
