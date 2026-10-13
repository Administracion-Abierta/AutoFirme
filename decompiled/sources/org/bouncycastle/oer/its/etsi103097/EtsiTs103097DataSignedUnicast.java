package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi103097/EtsiTs103097DataSignedUnicast.class */
public class EtsiTs103097DataSignedUnicast extends EtsiTs103097Data {
    public EtsiTs103097DataSignedUnicast(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected EtsiTs103097DataSignedUnicast(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097DataSignedUnicast getInstance(Object obj) {
        if (obj instanceof EtsiTs103097DataSignedUnicast) {
            return (EtsiTs103097DataSignedUnicast) obj;
        }
        if (obj != null) {
            return new EtsiTs103097DataSignedUnicast(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
