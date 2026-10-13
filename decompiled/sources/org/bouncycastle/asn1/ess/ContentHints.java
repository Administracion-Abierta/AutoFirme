package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ess/ContentHints.class */
public class ContentHints extends ASN1Object {
    private ASN1UTF8String contentDescription;
    private ASN1ObjectIdentifier contentType;

    public static ContentHints getInstance(Object obj) {
        if (obj instanceof ContentHints) {
            return (ContentHints) obj;
        }
        if (obj != null) {
            return new ContentHints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private ContentHints(ASN1Sequence aSN1Sequence) {
        ASN1Encodable objectAt = aSN1Sequence.getObjectAt(0);
        if (!(objectAt.toASN1Primitive() instanceof ASN1UTF8String)) {
            this.contentType = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        } else {
            this.contentDescription = ASN1UTF8String.getInstance(objectAt);
            this.contentType = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public ContentHints(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.contentType = aSN1ObjectIdentifier;
        this.contentDescription = null;
    }

    public ContentHints(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1UTF8String aSN1UTF8String) {
        this.contentType = aSN1ObjectIdentifier;
        this.contentDescription = aSN1UTF8String;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public DERUTF8String getContentDescription() {
        return (null == this.contentDescription || (this.contentDescription instanceof DERUTF8String)) ? (DERUTF8String) this.contentDescription : new DERUTF8String(this.contentDescription.getString());
    }

    public ASN1UTF8String getContentDescriptionUTF8() {
        return this.contentDescription;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        if (this.contentDescription != null) {
            aSN1EncodableVector.add(this.contentDescription);
        }
        aSN1EncodableVector.add(this.contentType);
        return new DERSequence(aSN1EncodableVector);
    }
}
