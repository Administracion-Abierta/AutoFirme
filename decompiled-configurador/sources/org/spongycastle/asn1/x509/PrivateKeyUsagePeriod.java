package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/PrivateKeyUsagePeriod.class */
public class PrivateKeyUsagePeriod extends ASN1Object {
    private ASN1GeneralizedTime _notBefore;
    private ASN1GeneralizedTime _notAfter;

    public static PrivateKeyUsagePeriod getInstance(Object obj) {
        if (obj instanceof PrivateKeyUsagePeriod) {
            return (PrivateKeyUsagePeriod) obj;
        }
        if (obj != null) {
            return new PrivateKeyUsagePeriod(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PrivateKeyUsagePeriod(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject) en.nextElement();
            if (tObj.getTagNo() == 0) {
                this._notBefore = ASN1GeneralizedTime.getInstance(tObj, false);
            } else if (tObj.getTagNo() == 1) {
                this._notAfter = ASN1GeneralizedTime.getInstance(tObj, false);
            }
        }
    }

    public ASN1GeneralizedTime getNotBefore() {
        return this._notBefore;
    }

    public ASN1GeneralizedTime getNotAfter() {
        return this._notAfter;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this._notBefore != null) {
            v.add(new DERTaggedObject(false, 0, this._notBefore));
        }
        if (this._notAfter != null) {
            v.add(new DERTaggedObject(false, 1, this._notAfter));
        }
        return new DERSequence(v);
    }
}
