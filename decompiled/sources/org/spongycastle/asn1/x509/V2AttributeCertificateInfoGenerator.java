package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/V2AttributeCertificateInfoGenerator.class */
public class V2AttributeCertificateInfoGenerator {
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private DERBitString issuerUniqueID;
    private Extensions extensions;
    private ASN1GeneralizedTime startDate;
    private ASN1GeneralizedTime endDate;
    private ASN1Integer version = new ASN1Integer(1);
    private ASN1EncodableVector attributes = new ASN1EncodableVector();

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public void addAttribute(String oid, ASN1Encodable value) {
        this.attributes.add(new Attribute(new ASN1ObjectIdentifier(oid), new DERSet(value)));
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(AttCertIssuer issuer) {
        this.issuer = issuer;
    }

    public void setStartDate(ASN1GeneralizedTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(ASN1GeneralizedTime endDate) {
        this.endDate = endDate;
    }

    public void setIssuerUniqueID(DERBitString issuerUniqueID) {
        this.issuerUniqueID = issuerUniqueID;
    }

    public void setExtensions(X509Extensions extensions) {
        this.extensions = Extensions.getInstance(extensions.toASN1Primitive());
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public AttributeCertificateInfo generateAttributeCertificateInfo() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.holder == null || this.attributes == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator");
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.holder);
        v.add(this.issuer);
        v.add(this.signature);
        v.add(this.serialNumber);
        AttCertValidityPeriod validity = new AttCertValidityPeriod(this.startDate, this.endDate);
        v.add(validity);
        v.add(new DERSequence(this.attributes));
        if (this.issuerUniqueID != null) {
            v.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            v.add(this.extensions);
        }
        return AttributeCertificateInfo.getInstance(new DERSequence(v));
    }
}
