package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi103097/EtsiTs103097Data.class */
public class EtsiTs103097Data extends Ieee1609Dot2Data {
    public EtsiTs103097Data(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(new UINT8(3), ieee1609Dot2Content);
    }

    public EtsiTs103097Data(UINT8 uint8, Ieee1609Dot2Content ieee1609Dot2Content) {
        super(uint8, ieee1609Dot2Content);
    }

    protected EtsiTs103097Data(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097Data getInstance(Object obj) {
        if (obj instanceof EtsiTs103097Data) {
            return (EtsiTs103097Data) obj;
        }
        if (obj != null) {
            return new EtsiTs103097Data(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
