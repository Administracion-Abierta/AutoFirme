package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/OriginatorIdentifierOrKey.class */
public class OriginatorIdentifierOrKey extends ASN1Object implements ASN1Choice {
    private ASN1Encodable id;

    public OriginatorIdentifierOrKey(IssuerAndSerialNumber id) {
        this.id = id;
    }

    public OriginatorIdentifierOrKey(ASN1OctetString id) {
        this(new SubjectKeyIdentifier(id.getOctets()));
    }

    public OriginatorIdentifierOrKey(SubjectKeyIdentifier id) {
        this.id = new DERTaggedObject(false, 0, id);
    }

    public OriginatorIdentifierOrKey(OriginatorPublicKey id) {
        this.id = new DERTaggedObject(false, 1, id);
    }

    public OriginatorIdentifierOrKey(ASN1Primitive id) {
        this.id = id;
    }

    public static OriginatorIdentifierOrKey getInstance(ASN1TaggedObject o, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("Can't implicitly tag OriginatorIdentifierOrKey");
        }
        return getInstance(o.getObject());
    }

    public static OriginatorIdentifierOrKey getInstance(Object o) {
        if (o == null || (o instanceof OriginatorIdentifierOrKey)) {
            return (OriginatorIdentifierOrKey) o;
        }
        if ((o instanceof IssuerAndSerialNumber) || (o instanceof ASN1Sequence)) {
            return new OriginatorIdentifierOrKey(IssuerAndSerialNumber.getInstance(o));
        }
        if (o instanceof ASN1TaggedObject) {
            ASN1TaggedObject tagged = (ASN1TaggedObject) o;
            if (tagged.getTagNo() == 0) {
                return new OriginatorIdentifierOrKey(SubjectKeyIdentifier.getInstance(tagged, false));
            }
            if (tagged.getTagNo() == 1) {
                return new OriginatorIdentifierOrKey(OriginatorPublicKey.getInstance(tagged, false));
            }
        }
        throw new IllegalArgumentException("Invalid OriginatorIdentifierOrKey: " + o.getClass().getName());
    }

    public ASN1Encodable getId() {
        return this.id;
    }

    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        if (this.id instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber) this.id;
        }
        return null;
    }

    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        if ((this.id instanceof ASN1TaggedObject) && ((ASN1TaggedObject) this.id).getTagNo() == 0) {
            return SubjectKeyIdentifier.getInstance((ASN1TaggedObject) this.id, false);
        }
        return null;
    }

    public OriginatorPublicKey getOriginatorKey() {
        if ((this.id instanceof ASN1TaggedObject) && ((ASN1TaggedObject) this.id).getTagNo() == 1) {
            return OriginatorPublicKey.getInstance((ASN1TaggedObject) this.id, false);
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.id.toASN1Primitive();
    }
}
