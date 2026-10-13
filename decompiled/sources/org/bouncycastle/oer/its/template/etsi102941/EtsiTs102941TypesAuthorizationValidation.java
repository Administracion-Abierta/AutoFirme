package org.bouncycastle.oer.its.template.etsi102941;

import java.math.BigInteger;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.etsi102941.basetypes.EtsiTs102941BaseTypes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/etsi102941/EtsiTs102941TypesAuthorizationValidation.class */
public class EtsiTs102941TypesAuthorizationValidation {
    public static final OERDefinition.Builder AuthorizationValidationResponseCode = OERDefinition.enumeration(OERDefinition.enumItem("ok", BigInteger.ZERO), "cantparse", "badcontenttype", "imnottherecipient", "unknownencryptionalgorithm", "decryptionfailed", "invalidaa", "invalidaasignature", "wrongea", "unknownits", "invalidsignature", "invalidencryptionkey", "deniedpermissions", "deniedtoomanycerts", "deniedrequest").typeName("AuthorizationValidationResponseCode");
    public static final OERDefinition.Builder AuthorizationValidationRequest = OERDefinition.seq(EtsiTs102941TypesAuthorization.SharedAtRequest.label("sharedAtRequest"), EtsiTs102941BaseTypes.EcSignature.label("ecSignature"), OERDefinition.extension(new Object[0])).typeName("AuthorizationValidationRequest");
    public static final OERDefinition.Builder AuthorizationValidationResponse = OERDefinition.seq(OERDefinition.octets(16).label("requestHash"), AuthorizationValidationResponseCode.label("responseCode"), OERDefinition.optional(EtsiTs102941BaseTypes.CertificateSubjectAttributes.label("confirmedSubjectAttributes")), OERDefinition.extension(new Object[0])).typeName("AuthorizationValidationResponse");
}
