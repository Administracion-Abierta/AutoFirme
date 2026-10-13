package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1UTCTime;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/V3TBSCertificateGenerator.class */
public class V3TBSCertificateGenerator {
    DERTaggedObject version = new DERTaggedObject(true, 0, new ASN1Integer(2));
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time startDate;
    Time endDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    Extensions extensions;
    private boolean altNamePresentAndCritical;
    private DERBitString issuerUniqueID;
    private DERBitString subjectUniqueID;

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(X509Name issuer) {
        this.issuer = X500Name.getInstance(issuer);
    }

    public void setIssuer(X500Name issuer) {
        this.issuer = issuer;
    }

    public void setStartDate(ASN1UTCTime startDate) {
        this.startDate = new Time(startDate);
    }

    public void setStartDate(Time startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(ASN1UTCTime endDate) {
        this.endDate = new Time(endDate);
    }

    public void setEndDate(Time endDate) {
        this.endDate = endDate;
    }

    public void setSubject(X509Name subject) {
        this.subject = X500Name.getInstance(subject.toASN1Primitive());
    }

    public void setSubject(X500Name subject) {
        this.subject = subject;
    }

    public void setIssuerUniqueID(DERBitString uniqueID) {
        this.issuerUniqueID = uniqueID;
    }

    public void setSubjectUniqueID(DERBitString uniqueID) {
        this.subjectUniqueID = uniqueID;
    }

    public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo pubKeyInfo) {
        this.subjectPublicKeyInfo = pubKeyInfo;
    }

    public void setExtensions(X509Extensions extensions) {
        setExtensions(Extensions.getInstance(extensions));
    }

    public void setExtensions(Extensions extensions) {
        Extension altName;
        this.extensions = extensions;
        if (extensions != null && (altName = extensions.getExtension(Extension.subjectAlternativeName)) != null && altName.isCritical()) {
            this.altNamePresentAndCritical = true;
        }
    }

    public TBSCertificate generateTBSCertificate() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || ((this.subject == null && !this.altNamePresentAndCritical) || this.subjectPublicKeyInfo == null)) {
            throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.serialNumber);
        v.add(this.signature);
        v.add(this.issuer);
        ASN1EncodableVector validity = new ASN1EncodableVector();
        validity.add(this.startDate);
        validity.add(this.endDate);
        v.add(new DERSequence(validity));
        if (this.subject != null) {
            v.add(this.subject);
        } else {
            v.add(new DERSequence());
        }
        v.add(this.subjectPublicKeyInfo);
        if (this.issuerUniqueID != null) {
            v.add(new DERTaggedObject(false, 1, this.issuerUniqueID));
        }
        if (this.subjectUniqueID != null) {
            v.add(new DERTaggedObject(false, 2, this.subjectUniqueID));
        }
        if (this.extensions != null) {
            v.add(new DERTaggedObject(true, 3, this.extensions));
        }
        return TBSCertificate.getInstance(new DERSequence(v));
    }
}
