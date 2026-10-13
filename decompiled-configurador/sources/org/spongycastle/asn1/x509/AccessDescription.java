package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/AccessDescription.class */
public class AccessDescription extends ASN1Object {
    public static final ASN1ObjectIdentifier id_ad_caIssuers = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.2");
    public static final ASN1ObjectIdentifier id_ad_ocsp = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.1");
    ASN1ObjectIdentifier accessMethod;
    GeneralName accessLocation;

    public static AccessDescription getInstance(Object obj) {
        if (obj instanceof AccessDescription) {
            return (AccessDescription) obj;
        }
        if (obj != null) {
            return new AccessDescription(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private AccessDescription(ASN1Sequence seq) {
        this.accessMethod = null;
        this.accessLocation = null;
        if (seq.size() != 2) {
            throw new IllegalArgumentException("wrong number of elements in sequence");
        }
        this.accessMethod = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        this.accessLocation = GeneralName.getInstance(seq.getObjectAt(1));
    }

    public AccessDescription(ASN1ObjectIdentifier oid, GeneralName location) {
        this.accessMethod = null;
        this.accessLocation = null;
        this.accessMethod = oid;
        this.accessLocation = location;
    }

    public ASN1ObjectIdentifier getAccessMethod() {
        return this.accessMethod;
    }

    public GeneralName getAccessLocation() {
        return this.accessLocation;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector accessDescription = new ASN1EncodableVector();
        accessDescription.add(this.accessMethod);
        accessDescription.add(this.accessLocation);
        return new DERSequence(accessDescription);
    }

    public String toString() {
        return "AccessDescription: Oid(" + this.accessMethod.getId() + ")";
    }
}
