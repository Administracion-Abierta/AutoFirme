package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/ToBeSignedLinkCertificateRca.class */
public class ToBeSignedLinkCertificateRca extends ToBeSignedLinkCertificate {
    public ToBeSignedLinkCertificateRca(Time32 time32, HashedData hashedData) {
        super(time32, hashedData);
    }

    protected ToBeSignedLinkCertificateRca(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    private ToBeSignedLinkCertificateRca(ToBeSignedLinkCertificate toBeSignedLinkCertificate) {
        super(toBeSignedLinkCertificate.getExpiryTime(), toBeSignedLinkCertificate.getCertificateHash());
    }

    public static ToBeSignedLinkCertificateRca getInstance(Object obj) {
        if (obj instanceof ToBeSignedLinkCertificateRca) {
            return (ToBeSignedLinkCertificateRca) obj;
        }
        if (obj instanceof ToBeSignedLinkCertificate) {
            return new ToBeSignedLinkCertificateRca((ToBeSignedLinkCertificate) obj);
        }
        if (obj != null) {
            return new ToBeSignedLinkCertificateRca(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
