package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/SubjectDirectoryAttributes.class */
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

    private SubjectDirectoryAttributes(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1Sequence s = ASN1Sequence.getInstance(e.nextElement());
            this.attributes.addElement(Attribute.getInstance(s));
        }
    }

    public SubjectDirectoryAttributes(Vector attributes) {
        Enumeration e = attributes.elements();
        while (e.hasMoreElements()) {
            this.attributes.addElement(e.nextElement());
        }
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector();
        Enumeration e = this.attributes.elements();
        while (e.hasMoreElements()) {
            vec.add((Attribute) e.nextElement());
        }
        return new DERSequence(vec);
    }

    public Vector getAttributes() {
        return this.attributes;
    }
}
