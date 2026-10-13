package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/CertTemplate.class */
public class CertTemplate extends ASN1Object {
    private ASN1Sequence seq;
    private ASN1Integer version;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signingAlg;
    private X500Name issuer;
    private OptionalValidity validity;
    private X500Name subject;
    private SubjectPublicKeyInfo publicKey;
    private ASN1BitString issuerUID;
    private ASN1BitString subjectUID;
    private Extensions extensions;

    private CertTemplate(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objects.nextElement();
            switch (aSN1TaggedObject.getTagNo()) {
                case 0:
                    this.version = ASN1Integer.getInstance(aSN1TaggedObject, false);
                    break;
                case 1:
                    this.serialNumber = ASN1Integer.getInstance(aSN1TaggedObject, false);
                    break;
                case 2:
                    this.signingAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
                    break;
                case 3:
                    this.issuer = X500Name.getInstance(aSN1TaggedObject, true);
                    break;
                case 4:
                    this.validity = OptionalValidity.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, false));
                    break;
                case 5:
                    this.subject = X500Name.getInstance(aSN1TaggedObject, true);
                    break;
                case 6:
                    this.publicKey = SubjectPublicKeyInfo.getInstance(aSN1TaggedObject, false);
                    break;
                case 7:
                    this.issuerUID = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    break;
                case 8:
                    this.subjectUID = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    break;
                case 9:
                    this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag: " + aSN1TaggedObject.getTagNo());
            }
        }
    }

    public static CertTemplate getInstance(Object obj) {
        if (obj instanceof CertTemplate) {
            return (CertTemplate) obj;
        }
        if (obj != null) {
            return new CertTemplate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public int getVersion() {
        if (this.version != null) {
            return this.version.intValueExact();
        }
        return -1;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSigningAlg() {
        return this.signingAlg;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public OptionalValidity getValidity() {
        return this.validity;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getPublicKey() {
        return this.publicKey;
    }

    public ASN1BitString getIssuerUID() {
        return this.issuerUID;
    }

    public ASN1BitString getSubjectUID() {
        return this.subjectUID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
