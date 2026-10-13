package org.spongycastle.asn1.ocsp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ocsp/CrlID.class */
public class CrlID extends ASN1Object {
    private DERIA5String crlUrl;
    private ASN1Integer crlNum;
    private ASN1GeneralizedTime crlTime;

    private CrlID(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1TaggedObject o = (ASN1TaggedObject) e.nextElement();
            switch (o.getTagNo()) {
                case 0:
                    this.crlUrl = DERIA5String.getInstance(o, true);
                    break;
                case 1:
                    this.crlNum = ASN1Integer.getInstance(o, true);
                    break;
                case 2:
                    this.crlTime = ASN1GeneralizedTime.getInstance(o, true);
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag number: " + o.getTagNo());
            }
        }
    }

    public static CrlID getInstance(Object obj) {
        if (obj instanceof CrlID) {
            return (CrlID) obj;
        }
        if (obj != null) {
            return new CrlID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DERIA5String getCrlUrl() {
        return this.crlUrl;
    }

    public ASN1Integer getCrlNum() {
        return this.crlNum;
    }

    public ASN1GeneralizedTime getCrlTime() {
        return this.crlTime;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.crlUrl != null) {
            v.add(new DERTaggedObject(true, 0, this.crlUrl));
        }
        if (this.crlNum != null) {
            v.add(new DERTaggedObject(true, 1, this.crlNum));
        }
        if (this.crlTime != null) {
            v.add(new DERTaggedObject(true, 2, this.crlTime));
        }
        return new DERSequence(v);
    }
}
