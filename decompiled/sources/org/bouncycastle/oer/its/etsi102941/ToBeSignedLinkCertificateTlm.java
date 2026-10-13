package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/ToBeSignedLinkCertificateTlm.class */
public class ToBeSignedLinkCertificateTlm extends ToBeSignedLinkCertificate {
    public ToBeSignedLinkCertificateTlm(Time32 time32, HashedData hashedData) {
        super(time32, hashedData);
    }

    protected ToBeSignedLinkCertificateTlm(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    private ToBeSignedLinkCertificateTlm(ToBeSignedLinkCertificate toBeSignedLinkCertificate) {
        super(toBeSignedLinkCertificate.getExpiryTime(), toBeSignedLinkCertificate.getCertificateHash());
    }

    public static ToBeSignedLinkCertificateTlm getInstance(Object obj) {
        if (obj instanceof ToBeSignedLinkCertificateTlm) {
            return (ToBeSignedLinkCertificateTlm) obj;
        }
        if (obj instanceof ToBeSignedLinkCertificate) {
            return new ToBeSignedLinkCertificateTlm((ToBeSignedLinkCertificate) obj);
        }
        if (obj != null) {
            return new ToBeSignedLinkCertificateTlm(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
