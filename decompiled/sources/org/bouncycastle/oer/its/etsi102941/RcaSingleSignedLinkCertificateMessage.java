package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/RcaSingleSignedLinkCertificateMessage.class */
public class RcaSingleSignedLinkCertificateMessage extends EtsiTs103097DataSigned {
    public RcaSingleSignedLinkCertificateMessage(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected RcaSingleSignedLinkCertificateMessage(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static RcaSingleSignedLinkCertificateMessage getInstance(Object obj) {
        if (obj instanceof RcaSingleSignedLinkCertificateMessage) {
            return (RcaSingleSignedLinkCertificateMessage) obj;
        }
        if (obj != null) {
            return new RcaSingleSignedLinkCertificateMessage(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
