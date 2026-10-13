package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/AttributeCertificateInfo.class */
public class AttributeCertificateInfo extends ASN1Object {
    private ASN1Integer version;
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private AttCertValidityPeriod attrCertValidityPeriod;
    private ASN1Sequence attributes;
    private ASN1BitString issuerUniqueID;
    private Extensions extensions;

    public static AttributeCertificateInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static AttributeCertificateInfo getInstance(Object obj) {
        if (obj instanceof AttributeCertificateInfo) {
            return (AttributeCertificateInfo) obj;
        }
        if (obj != null) {
            return new AttributeCertificateInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private AttributeCertificateInfo(ASN1Sequence aSN1Sequence) {
        int i;
        if (aSN1Sequence.size() < 6 || aSN1Sequence.size() > 9) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
            i = 1;
        } else {
            this.version = new ASN1Integer(0L);
            i = 0;
        }
        this.holder = Holder.getInstance(aSN1Sequence.getObjectAt(i));
        this.issuer = AttCertIssuer.getInstance(aSN1Sequence.getObjectAt(i + 1));
        this.signature = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i + 2));
        this.serialNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(i + 3));
        this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(aSN1Sequence.getObjectAt(i + 4));
        this.attributes = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(i + 5));
        for (int i2 = i + 6; i2 < aSN1Sequence.size(); i2++) {
            ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i2);
            if (objectAt instanceof ASN1BitString) {
                this.issuerUniqueID = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(i2));
            } else if ((objectAt instanceof ASN1Sequence) || (objectAt instanceof Extensions)) {
                this.extensions = Extensions.getInstance(aSN1Sequence.getObjectAt(i2));
            }
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public Holder getHolder() {
        return this.holder;
    }

    public AttCertIssuer getIssuer() {
        return this.issuer;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AttCertValidityPeriod getAttrCertValidityPeriod() {
        return this.attrCertValidityPeriod;
    }

    public ASN1Sequence getAttributes() {
        return this.attributes;
    }

    public ASN1BitString getIssuerUniqueID() {
        return this.issuerUniqueID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(9);
        if (!this.version.hasValue(0)) {
            aSN1EncodableVector.add(this.version);
        }
        aSN1EncodableVector.add(this.holder);
        aSN1EncodableVector.add(this.issuer);
        aSN1EncodableVector.add(this.signature);
        aSN1EncodableVector.add(this.serialNumber);
        aSN1EncodableVector.add(this.attrCertValidityPeriod);
        aSN1EncodableVector.add(this.attributes);
        if (this.issuerUniqueID != null) {
            aSN1EncodableVector.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            aSN1EncodableVector.add(this.extensions);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
