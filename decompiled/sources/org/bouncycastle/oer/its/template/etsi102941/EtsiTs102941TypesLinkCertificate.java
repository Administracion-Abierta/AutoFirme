package org.bouncycastle.oer.its.template.etsi102941;

import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/etsi102941/EtsiTs102941TypesLinkCertificate.class */
public class EtsiTs102941TypesLinkCertificate {
    public static final OERDefinition.Builder ToBeSignedLinkCertificate = OERDefinition.seq(Ieee1609Dot2BaseTypes.Time32.label("expiryTime"), IEEE1609dot2.HashedData.label("certificateHash"), OERDefinition.extension(new Object[0])).typeName("ToBeSignedLinkCertificate");
    public static final OERDefinition.Builder ToBeSignedLinkCertificateTlm = ToBeSignedLinkCertificate.typeName("ToBeSignedLinkCertificateTlm");
    public static final OERDefinition.Builder ToBeSignedLinkCertificateRca = ToBeSignedLinkCertificate.typeName("ToBeSignedLinkCertificateRca");
}
