package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/ToBeSignedTlmCtl.class */
public class ToBeSignedTlmCtl extends CtlFormat {
    public ToBeSignedTlmCtl(Version version, Time32 time32, ASN1Boolean aSN1Boolean, UINT8 uint8, SequenceOfCtlCommand sequenceOfCtlCommand) {
        super(version, time32, aSN1Boolean, uint8, sequenceOfCtlCommand);
    }

    public ToBeSignedTlmCtl(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static ToBeSignedTlmCtl getInstance(Object obj) {
        if (obj instanceof ToBeSignedTlmCtl) {
            return (ToBeSignedTlmCtl) obj;
        }
        if (obj != null) {
            return new ToBeSignedTlmCtl(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
