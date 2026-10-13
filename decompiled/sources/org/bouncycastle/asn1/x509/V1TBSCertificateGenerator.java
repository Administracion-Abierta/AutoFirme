package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/V1TBSCertificateGenerator.class */
public class V1TBSCertificateGenerator {
    DERTaggedObject version = new DERTaggedObject(true, 0, (ASN1Encodable) new ASN1Integer(0));
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Validity validity;
    Time startDate;
    Time endDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;

    public void setSerialNumber(ASN1Integer aSN1Integer) {
        this.serialNumber = aSN1Integer;
    }

    public void setSignature(AlgorithmIdentifier algorithmIdentifier) {
        this.signature = algorithmIdentifier;
    }

    public void setIssuer(X509Name x509Name) {
        this.issuer = X500Name.getInstance(x509Name.toASN1Primitive());
    }

    public void setIssuer(X500Name x500Name) {
        this.issuer = x500Name;
    }

    public void setValidity(Validity validity) {
        this.validity = validity;
        this.startDate = null;
        this.endDate = null;
    }

    public void setStartDate(Time time) {
        this.validity = null;
        this.startDate = time;
    }

    public void setStartDate(ASN1UTCTime aSN1UTCTime) {
        setStartDate(new Time(aSN1UTCTime));
    }

    public void setEndDate(Time time) {
        this.validity = null;
        this.endDate = time;
    }

    public void setEndDate(ASN1UTCTime aSN1UTCTime) {
        setEndDate(new Time(aSN1UTCTime));
    }

    public void setSubject(X509Name x509Name) {
        this.subject = X500Name.getInstance(x509Name.toASN1Primitive());
    }

    public void setSubject(X500Name x500Name) {
        this.subject = x500Name;
    }

    public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
    }

    public TBSCertificate generateTBSCertificate() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || ((this.validity == null && (this.startDate == null || this.endDate == null)) || this.subject == null || this.subjectPublicKeyInfo == null)) {
            throw new IllegalStateException("not all mandatory fields set in V1 TBScertificate generator");
        }
        return new TBSCertificate(new ASN1Integer(0L), this.serialNumber, this.signature, this.issuer, this.validity != null ? this.validity : new Validity(this.startDate, this.endDate), this.subject, this.subjectPublicKeyInfo, null, null, null);
    }
}
