package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/DeltaCtl.class */
public class DeltaCtl extends CtlFormat {
    public DeltaCtl(Version version, Time32 time32, UINT8 uint8, SequenceOfCtlCommand sequenceOfCtlCommand) {
        super(version, time32, ASN1Boolean.FALSE, uint8, sequenceOfCtlCommand);
    }

    private DeltaCtl(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static DeltaCtl getInstance(Object obj) {
        if (obj instanceof DeltaCtl) {
            return (DeltaCtl) obj;
        }
        if (obj != null) {
            return new DeltaCtl(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
