package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedAndEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/AuthorizationValidationResponseMessage.class */
public class AuthorizationValidationResponseMessage extends EtsiTs103097DataSignedAndEncryptedUnicast {
    public AuthorizationValidationResponseMessage(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected AuthorizationValidationResponseMessage(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static AuthorizationValidationResponseMessage getInstance(Object obj) {
        if (obj instanceof AuthorizationValidationResponseMessage) {
            return (AuthorizationValidationResponseMessage) obj;
        }
        if (obj != null) {
            return new AuthorizationValidationResponseMessage(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
