package org.spongycastle.asn1.pkcs;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509Name;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/pkcs/CertificationRequestInfo.class */
public class CertificationRequestInfo extends ASN1Object {
    ASN1Integer version;
    X500Name subject;
    SubjectPublicKeyInfo subjectPKInfo;
    ASN1Set attributes;

    public static CertificationRequestInfo getInstance(Object obj) {
        if (obj instanceof CertificationRequestInfo) {
            return (CertificationRequestInfo) obj;
        }
        if (obj != null) {
            return new CertificationRequestInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public CertificationRequestInfo(X500Name subject, SubjectPublicKeyInfo pkInfo, ASN1Set attributes) {
        this.version = new ASN1Integer(0L);
        this.attributes = null;
        if (subject == null || pkInfo == null) {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
        validateAttributes(attributes);
        this.subject = subject;
        this.subjectPKInfo = pkInfo;
        this.attributes = attributes;
    }

    public CertificationRequestInfo(X509Name subject, SubjectPublicKeyInfo pkInfo, ASN1Set attributes) {
        this(X500Name.getInstance(subject.toASN1Primitive()), pkInfo, attributes);
    }

    public CertificationRequestInfo(ASN1Sequence seq) {
        this.version = new ASN1Integer(0L);
        this.attributes = null;
        this.version = (ASN1Integer) seq.getObjectAt(0);
        this.subject = X500Name.getInstance(seq.getObjectAt(1));
        this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(2));
        if (seq.size() > 3) {
            ASN1TaggedObject tagobj = (ASN1TaggedObject) seq.getObjectAt(3);
            this.attributes = ASN1Set.getInstance(tagobj, false);
        }
        validateAttributes(this.attributes);
        if (this.subject == null || this.version == null || this.subjectPKInfo == null) {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPKInfo;
    }

    public ASN1Set getAttributes() {
        return this.attributes;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.subject);
        v.add(this.subjectPKInfo);
        if (this.attributes != null) {
            v.add(new DERTaggedObject(false, 0, this.attributes));
        }
        return new DERSequence(v);
    }

    private static void validateAttributes(ASN1Set attributes) {
        if (attributes == null) {
            return;
        }
        Enumeration en = attributes.getObjects();
        while (en.hasMoreElements()) {
            Attribute attr = Attribute.getInstance(en.nextElement());
            if (attr.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_challengePassword) && attr.getAttrValues().size() != 1) {
                throw new IllegalArgumentException("challengePassword attribute must have one value");
            }
        }
    }
}
