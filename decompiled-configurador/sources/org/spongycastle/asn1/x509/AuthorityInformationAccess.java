package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/AuthorityInformationAccess.class */
public class AuthorityInformationAccess extends ASN1Object {
    private AccessDescription[] descriptions;

    public static AuthorityInformationAccess getInstance(Object obj) {
        if (obj instanceof AuthorityInformationAccess) {
            return (AuthorityInformationAccess) obj;
        }
        if (obj != null) {
            return new AuthorityInformationAccess(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static AuthorityInformationAccess fromExtensions(Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.authorityInfoAccess));
    }

    private AuthorityInformationAccess(ASN1Sequence seq) {
        if (seq.size() < 1) {
            throw new IllegalArgumentException("sequence may not be empty");
        }
        this.descriptions = new AccessDescription[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            this.descriptions[i] = AccessDescription.getInstance(seq.getObjectAt(i));
        }
    }

    public AuthorityInformationAccess(AccessDescription description) {
        this(new AccessDescription[]{description});
    }

    public AuthorityInformationAccess(AccessDescription[] descriptions) {
        this.descriptions = new AccessDescription[descriptions.length];
        System.arraycopy(descriptions, 0, this.descriptions, 0, descriptions.length);
    }

    public AuthorityInformationAccess(ASN1ObjectIdentifier oid, GeneralName location) {
        this(new AccessDescription(oid, location));
    }

    public AccessDescription[] getAccessDescriptions() {
        return this.descriptions;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector();
        for (int i = 0; i != this.descriptions.length; i++) {
            vec.add(this.descriptions[i]);
        }
        return new DERSequence(vec);
    }

    public String toString() {
        return "AuthorityInformationAccess: Oid(" + this.descriptions[0].getAccessMethod().getId() + ")";
    }
}
