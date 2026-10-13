package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/Certificate.class */
public class Certificate extends ASN1Object {
    ASN1Sequence seq;
    TBSCertificate tbsCert;
    AlgorithmIdentifier sigAlgId;
    ASN1BitString sig;

    public static Certificate getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static Certificate getInstance(Object obj) {
        if (obj instanceof Certificate) {
            return (Certificate) obj;
        }
        if (obj != null) {
            return new Certificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private Certificate(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("sequence wrong size for a certificate");
        }
        this.tbsCert = TBSCertificate.getInstance(aSN1Sequence.getObjectAt(0));
        this.sigAlgId = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        this.sig = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public Certificate(TBSCertificate tBSCertificate, AlgorithmIdentifier algorithmIdentifier, ASN1BitString aSN1BitString) {
        if (tBSCertificate == null) {
            throw new NullPointerException("'tbsCertificate' cannot be null");
        }
        if (algorithmIdentifier == null) {
            throw new NullPointerException("'signatureAlgorithm' cannot be null");
        }
        if (aSN1BitString == null) {
            throw new NullPointerException("'signature' cannot be null");
        }
        this.tbsCert = tBSCertificate;
        this.sigAlgId = algorithmIdentifier;
        this.sig = aSN1BitString;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(tBSCertificate);
        aSN1EncodableVector.add(algorithmIdentifier);
        aSN1EncodableVector.add(aSN1BitString);
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public TBSCertificate getTBSCertificate() {
        return this.tbsCert;
    }

    public ASN1Integer getVersion() {
        return this.tbsCert.getVersion();
    }

    public int getVersionNumber() {
        return this.tbsCert.getVersionNumber();
    }

    public ASN1Integer getSerialNumber() {
        return this.tbsCert.getSerialNumber();
    }

    public X500Name getIssuer() {
        return this.tbsCert.getIssuer();
    }

    public Validity getValidity() {
        return this.tbsCert.getValidity();
    }

    public Time getStartDate() {
        return this.tbsCert.getStartDate();
    }

    public Time getEndDate() {
        return this.tbsCert.getEndDate();
    }

    public X500Name getSubject() {
        return this.tbsCert.getSubject();
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.tbsCert.getSubjectPublicKeyInfo();
    }

    public ASN1BitString getIssuerUniqueID() {
        return this.tbsCert.getIssuerUniqueId();
    }

    public ASN1BitString getSubjectUniqueID() {
        return this.tbsCert.getSubjectUniqueId();
    }

    public Extensions getExtensions() {
        return this.tbsCert.getExtensions();
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.sigAlgId;
    }

    public ASN1BitString getSignature() {
        return this.sig;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
