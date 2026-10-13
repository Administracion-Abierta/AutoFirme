package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/CertTemplateBuilder.class */
public class CertTemplateBuilder {
    private ASN1Integer version;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signingAlg;
    private X500Name issuer;
    private OptionalValidity validity;
    private X500Name subject;
    private SubjectPublicKeyInfo publicKey;
    private DERBitString issuerUID;
    private DERBitString subjectUID;
    private Extensions extensions;

    public CertTemplateBuilder setVersion(int ver) {
        this.version = new ASN1Integer(ver);
        return this;
    }

    public CertTemplateBuilder setSerialNumber(ASN1Integer ser) {
        this.serialNumber = ser;
        return this;
    }

    public CertTemplateBuilder setSigningAlg(AlgorithmIdentifier aid) {
        this.signingAlg = aid;
        return this;
    }

    public CertTemplateBuilder setIssuer(X500Name name) {
        this.issuer = name;
        return this;
    }

    public CertTemplateBuilder setValidity(OptionalValidity v) {
        this.validity = v;
        return this;
    }

    public CertTemplateBuilder setSubject(X500Name name) {
        this.subject = name;
        return this;
    }

    public CertTemplateBuilder setPublicKey(SubjectPublicKeyInfo spki) {
        this.publicKey = spki;
        return this;
    }

    public CertTemplateBuilder setIssuerUID(DERBitString uid) {
        this.issuerUID = uid;
        return this;
    }

    public CertTemplateBuilder setSubjectUID(DERBitString uid) {
        this.subjectUID = uid;
        return this;
    }

    public CertTemplateBuilder setExtensions(X509Extensions extens) {
        return setExtensions(Extensions.getInstance(extens));
    }

    public CertTemplateBuilder setExtensions(Extensions extens) {
        this.extensions = extens;
        return this;
    }

    public CertTemplate build() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        addOptional(v, 0, false, this.version);
        addOptional(v, 1, false, this.serialNumber);
        addOptional(v, 2, false, this.signingAlg);
        addOptional(v, 3, true, this.issuer);
        addOptional(v, 4, false, this.validity);
        addOptional(v, 5, true, this.subject);
        addOptional(v, 6, false, this.publicKey);
        addOptional(v, 7, false, this.issuerUID);
        addOptional(v, 8, false, this.subjectUID);
        addOptional(v, 9, false, this.extensions);
        return CertTemplate.getInstance(new DERSequence(v));
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, boolean isExplicit, ASN1Encodable obj) {
        if (obj != null) {
            v.add(new DERTaggedObject(isExplicit, tagNo, obj));
        }
    }
}
