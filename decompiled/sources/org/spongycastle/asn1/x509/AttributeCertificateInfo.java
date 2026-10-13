package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/AttributeCertificateInfo.class */
public class AttributeCertificateInfo extends ASN1Object {
    private ASN1Integer version;
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private AttCertValidityPeriod attrCertValidityPeriod;
    private ASN1Sequence attributes;
    private DERBitString issuerUniqueID;
    private Extensions extensions;

    public static AttributeCertificateInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
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

    private AttributeCertificateInfo(ASN1Sequence seq) {
        int start;
        if (seq.size() < 6 || seq.size() > 9) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        if (seq.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
            start = 1;
        } else {
            this.version = new ASN1Integer(0L);
            start = 0;
        }
        this.holder = Holder.getInstance(seq.getObjectAt(start));
        this.issuer = AttCertIssuer.getInstance(seq.getObjectAt(start + 1));
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(start + 2));
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(start + 3));
        this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(seq.getObjectAt(start + 4));
        this.attributes = ASN1Sequence.getInstance(seq.getObjectAt(start + 5));
        for (int i = start + 6; i < seq.size(); i++) {
            ASN1Encodable obj = seq.getObjectAt(i);
            if (obj instanceof DERBitString) {
                this.issuerUniqueID = DERBitString.getInstance(seq.getObjectAt(i));
            } else if ((obj instanceof ASN1Sequence) || (obj instanceof Extensions)) {
                this.extensions = Extensions.getInstance(seq.getObjectAt(i));
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

    public DERBitString getIssuerUniqueID() {
        return this.issuerUniqueID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.version.getValue().intValue() != 0) {
            v.add(this.version);
        }
        v.add(this.holder);
        v.add(this.issuer);
        v.add(this.signature);
        v.add(this.serialNumber);
        v.add(this.attrCertValidityPeriod);
        v.add(this.attributes);
        if (this.issuerUniqueID != null) {
            v.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            v.add(this.extensions);
        }
        return new DERSequence(v);
    }
}
