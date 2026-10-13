package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/ExtendedFailInfo.class */
public class ExtendedFailInfo extends ASN1Object {
    private final ASN1ObjectIdentifier failInfoOID;
    private final ASN1Encodable failInfoValue;

    public ExtendedFailInfo(ASN1ObjectIdentifier failInfoOID, ASN1Encodable failInfoValue) {
        this.failInfoOID = failInfoOID;
        this.failInfoValue = failInfoValue;
    }

    private ExtendedFailInfo(ASN1Sequence s) {
        if (s.size() != 2) {
            throw new IllegalArgumentException("Sequence must be 2 elements.");
        }
        this.failInfoOID = ASN1ObjectIdentifier.getInstance(s.getObjectAt(0));
        this.failInfoValue = s.getObjectAt(1);
    }

    public static ExtendedFailInfo getInstance(Object obj) {
        if (obj instanceof ExtendedFailInfo) {
            return (ExtendedFailInfo) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Encodable asn1Value = ((ASN1Encodable) obj).toASN1Primitive();
            if (asn1Value instanceof ASN1Sequence) {
                return new ExtendedFailInfo((ASN1Sequence) asn1Value);
            }
            return null;
        }
        if (obj instanceof byte[]) {
            return getInstance(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.failInfoOID, this.failInfoValue});
    }

    public ASN1ObjectIdentifier getFailInfoOID() {
        return this.failInfoOID;
    }

    public ASN1Encodable getFailInfoValue() {
        return this.failInfoValue;
    }
}
