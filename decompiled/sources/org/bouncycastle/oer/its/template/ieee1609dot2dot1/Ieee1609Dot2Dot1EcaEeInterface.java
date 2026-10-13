package org.bouncycastle.oer.its.template.ieee1609dot2dot1;

import com.ctc.wstx.cfg.XmlConsts;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/ieee1609dot2dot1/Ieee1609Dot2Dot1EcaEeInterface.class */
public class Ieee1609Dot2Dot1EcaEeInterface {
    public static final OERDefinition.Builder EeEcaCertRequest = OERDefinition.seq(Ieee1609Dot2BaseTypes.UINT8.label(XmlConsts.XML_DECL_KW_VERSION).validSwitchValue(new ASN1Integer(2)), Ieee1609Dot2BaseTypes.Time32.label("generationTime"), IEEE1609dot2.CertificateType.label("type"), IEEE1609dot2.ToBeSignedCertificate.label("tbsCert"), OERDefinition.optional(OERDefinition.ia5String().label("canonicalId")), OERDefinition.extension(new Object[0])).typeName("EeEcaCertRequest");
}
