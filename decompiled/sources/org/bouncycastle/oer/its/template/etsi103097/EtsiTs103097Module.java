package org.bouncycastle.oer.its.template.etsi103097;

import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/etsi103097/EtsiTs103097Module.class */
public class EtsiTs103097Module {
    public static final OERDefinition.Builder EtsiTs103097Certificate = IEEE1609dot2.ExplicitCertificate.typeName("EtsiTs103097Certificate");
    public static final OERDefinition.Builder EtsiTs103097Data = IEEE1609dot2.Ieee1609Dot2Data.typeName("EtsiTs103097Data");
    public static final OERDefinition.Builder EtsiTs103097Data_Unsecured = EtsiTs103097Data.typeName("EtsiTs103097DataUnsecured");
    public static final OERDefinition.Builder EtsiTs103097Data_Signed = EtsiTs103097Data.typeName("EtsiTs103097DataSigned");
    public static final OERDefinition.Builder EtsiTs103097Data_SignedExternalPayload = EtsiTs103097Data.typeName("EtsiTs103097DataSignedExternalPayload");
    public static final OERDefinition.Builder EtsiTs103097Data_Encrypted = EtsiTs103097Data.typeName("EtsiTs103097DataEncrypted");
    public static final OERDefinition.Builder EtsiTs103097Data_SignedAndEncrypted = EtsiTs103097Data.typeName("EtsiTs103097DataSignedAndEncrypted");
    public static final OERDefinition.Builder EtsiTs103097Data_Encrypted_Unicast = EtsiTs103097Data.typeName("EtsiTs103097DataEncryptedUnicast");
    public static final OERDefinition.Builder EtsiTs103097Data_SignedAndEncrypted_Unicast = EtsiTs103097Data.typeName("EtsiTs103097DataSignedAndEncryptedUnicast");
}
