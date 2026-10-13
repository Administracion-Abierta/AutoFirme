package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/SubjectDirectoryAttributes.class */
public class SubjectDirectoryAttributes extends ASN1Object {
    private Vector attributes = new Vector();

    public static SubjectDirectoryAttributes getInstance(Object obj) {
        if (obj instanceof SubjectDirectoryAttributes) {
            return (SubjectDirectoryAttributes) obj;
        }
        if (obj != null) {
            return new SubjectDirectoryAttributes(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SubjectDirectoryAttributes(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            this.attributes.addElement(Attribute.getInstance(ASN1Sequence.getInstance(objects.nextElement())));
        }
    }

    public SubjectDirectoryAttributes(Vector vector) {
        Enumeration enumerationElements = vector.elements();
        while (enumerationElements.hasMoreElements()) {
            this.attributes.addElement(enumerationElements.nextElement());
        }
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(this.attributes.size());
        Enumeration enumerationElements = this.attributes.elements();
        while (enumerationElements.hasMoreElements()) {
            aSN1EncodableVector.add((Attribute) enumerationElements.nextElement());
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public Vector getAttributes() {
        return this.attributes;
    }
}
