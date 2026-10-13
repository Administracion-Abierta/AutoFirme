package org.bouncycastle.oer.its.template.etsi102941;

import java.math.BigInteger;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.etsi102941.basetypes.EtsiTs102941BaseTypes;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/etsi102941/EtsiTs102941TypesAuthorization.class */
public class EtsiTs102941TypesAuthorization {
    public static final OERDefinition.Builder AuthorizationResponseCode = OERDefinition.enumeration(OERDefinition.enumItem("ok", BigInteger.ZERO), "its-aa-cantparse", "its-aa-badcontenttype", "its-aa-imnottherecipient", "its-aa-unknownencryptionalgorithm", "its-aa-decryptionfailed", "its-aa-keysdontmatch", "its-aa-incompleterequest", "its-aa-invalidencryptionkey", "its-aa-outofsyncrequest", "its-aa-unknownea", "its-aa-invalidea", "its-aa-deniedpermissions", "aa-ea-cantreachea", "ea-aa-cantparse", "ea-aa-badcontenttype", "ea-aa-imnottherecipient", "ea-aa-unknownencryptionalgorithm", "ea-aa-decryptionfailed", "invalidaa", "invalidaasignature", "wrongea", "unknownits", "invalidsignature", "invalidencryptionkey", "deniedpermissions", "deniedtoomanycerts").typeName("AuthorizationResponseCode");
    public static final OERDefinition.Builder InnerAtResponse = OERDefinition.seq(OERDefinition.octets(16).label("requestHash"), AuthorizationResponseCode.label("responseCode"), OERDefinition.optional(EtsiTs103097Module.EtsiTs103097Certificate.label("certificate")), OERDefinition.extension(new Object[0])).typeName("InnerAtResponse");
    public static final OERDefinition.Builder SharedAtRequest = OERDefinition.seq(Ieee1609Dot2BaseTypes.HashedId8.label("eaId"), OERDefinition.octets(16).label("keyTag"), EtsiTs102941BaseTypes.CertificateFormat.label("certificateFormat"), EtsiTs102941BaseTypes.CertificateSubjectAttributes.label("requestedSubjectAttributes"), OERDefinition.extension(new Object[0])).typeName("SharedAtRequest");
    public static final OERDefinition.Builder InnerAtRequest = OERDefinition.seq(EtsiTs102941BaseTypes.PublicKeys.label("publicKeys"), OERDefinition.octets(32).label("hmacKey"), SharedAtRequest.label("sharedAtRequest"), EtsiTs102941BaseTypes.EcSignature.label("ecSignature"), OERDefinition.extension(new Object[0])).typeName("InnerAtRequest");
}
