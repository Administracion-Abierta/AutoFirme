package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi103097/extension/EtsiTs102941DeltaCtlRequest.class */
public class EtsiTs102941DeltaCtlRequest extends EtsiTs102941CtlRequest {
    private EtsiTs102941DeltaCtlRequest(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public EtsiTs102941DeltaCtlRequest(EtsiTs102941CtlRequest etsiTs102941CtlRequest) {
        super(etsiTs102941CtlRequest.getIssuerId(), etsiTs102941CtlRequest.getLastKnownCtlSequence());
    }

    public EtsiTs102941DeltaCtlRequest(HashedId8 hashedId8, ASN1Integer aSN1Integer) {
        super(hashedId8, aSN1Integer);
    }

    public static EtsiTs102941DeltaCtlRequest getInstance(Object obj) {
        if (obj instanceof EtsiTs102941DeltaCtlRequest) {
            return (EtsiTs102941DeltaCtlRequest) obj;
        }
        if (obj != null) {
            return new EtsiTs102941DeltaCtlRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
