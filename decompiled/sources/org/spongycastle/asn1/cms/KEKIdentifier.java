package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/KEKIdentifier.class */
public class KEKIdentifier extends ASN1Object {
    private ASN1OctetString keyIdentifier;
    private ASN1GeneralizedTime date;
    private OtherKeyAttribute other;

    public KEKIdentifier(byte[] keyIdentifier, ASN1GeneralizedTime date, OtherKeyAttribute other) {
        this.keyIdentifier = new DEROctetString(keyIdentifier);
        this.date = date;
        this.other = other;
    }

    private KEKIdentifier(ASN1Sequence seq) {
        this.keyIdentifier = (ASN1OctetString) seq.getObjectAt(0);
        switch (seq.size()) {
            case 1:
                return;
            case 2:
                if (seq.getObjectAt(1) instanceof ASN1GeneralizedTime) {
                    this.date = (ASN1GeneralizedTime) seq.getObjectAt(1);
                    return;
                } else {
                    this.other = OtherKeyAttribute.getInstance(seq.getObjectAt(1));
                    return;
                }
            case 3:
                this.date = (ASN1GeneralizedTime) seq.getObjectAt(1);
                this.other = OtherKeyAttribute.getInstance(seq.getObjectAt(2));
                return;
            default:
                throw new IllegalArgumentException("Invalid KEKIdentifier");
        }
    }

    public static KEKIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static KEKIdentifier getInstance(Object obj) {
        if (obj == null || (obj instanceof KEKIdentifier)) {
            return (KEKIdentifier) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new KEKIdentifier((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid KEKIdentifier: " + obj.getClass().getName());
    }

    public ASN1OctetString getKeyIdentifier() {
        return this.keyIdentifier;
    }

    public ASN1GeneralizedTime getDate() {
        return this.date;
    }

    public OtherKeyAttribute getOther() {
        return this.other;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.keyIdentifier);
        if (this.date != null) {
            v.add(this.date);
        }
        if (this.other != null) {
            v.add(this.other);
        }
        return new DERSequence(v);
    }
}
