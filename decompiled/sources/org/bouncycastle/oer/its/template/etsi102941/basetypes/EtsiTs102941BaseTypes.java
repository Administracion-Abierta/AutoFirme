package org.bouncycastle.oer.its.template.etsi102941.basetypes;

import com.aowagie.text.ElementTags;
import org.apache.fontbox.afm.AFMParser;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/etsi102941/basetypes/EtsiTs102941BaseTypes.class */
public class EtsiTs102941BaseTypes {
    public static final OERDefinition.Builder CertificateFormat = OERDefinition.integer(0, 255).typeName("CertificateFormat");
    public static final OERDefinition.Builder CertificateSubjectAttributes = OERDefinition.seq(OERDefinition.optional(IEEE1609dot2.CertificateId.label(ElementTags.ID), Ieee1609Dot2BaseTypes.ValidityPeriod.label("validityPeriod"), Ieee1609Dot2BaseTypes.GeographicRegion.label("region"), Ieee1609Dot2BaseTypes.SubjectAssurance.label("assuranceLevel"), Ieee1609Dot2BaseTypes.SequenceOfPsidSsp.label("appPermissions"), IEEE1609dot2.SequenceOfPsidGroupPermissions.label("certIssuePermissions"), OERDefinition.extension(new Object[0]))).typeName("CertificateSubjectAttributes");
    public static final OERDefinition.Builder EcSignature = OERDefinition.choice(EtsiTs103097Module.EtsiTs103097Data_Encrypted.label("encryptedEcSignature"), EtsiTs103097Module.EtsiTs103097Data_SignedExternalPayload.label("ecSignature")).typeName("EcSignature");
    public static final OERDefinition.Builder PublicKeys = OERDefinition.seq(Ieee1609Dot2BaseTypes.PublicVerificationKey.label("verificationKey"), OERDefinition.optional(Ieee1609Dot2BaseTypes.PublicEncryptionKey.label("encryptionKey"))).typeName("PublicKeys");
    public static final OERDefinition.Builder Version = OERDefinition.integer(0, 255).typeName(AFMParser.VERSION);
}
