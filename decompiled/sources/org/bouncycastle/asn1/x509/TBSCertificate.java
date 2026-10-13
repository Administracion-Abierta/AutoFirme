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
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/TBSCertificate.class */
public class TBSCertificate extends ASN1Object {
    ASN1Sequence seq;
    ASN1Integer version;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Validity validity;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    ASN1BitString issuerUniqueId;
    ASN1BitString subjectUniqueId;
    Extensions extensions;

    public static TBSCertificate getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static TBSCertificate getInstance(Object obj) {
        if (obj instanceof TBSCertificate) {
            return (TBSCertificate) obj;
        }
        if (obj != null) {
            return new TBSCertificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TBSCertificate(ASN1Sequence aSN1Sequence) {
        int i = 0;
        this.seq = aSN1Sequence;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.version = ASN1Integer.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(0), true);
        } else {
            i = -1;
            this.version = new ASN1Integer(0L);
        }
        boolean z = false;
        boolean z2 = false;
        if (this.version.hasValue(0)) {
            z = true;
        } else if (this.version.hasValue(1)) {
            z2 = true;
        } else if (!this.version.hasValue(2)) {
            throw new IllegalArgumentException("version number not recognised");
        }
        this.serialNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(i + 1));
        this.signature = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i + 2));
        this.issuer = X500Name.getInstance(aSN1Sequence.getObjectAt(i + 3));
        this.validity = Validity.getInstance(aSN1Sequence.getObjectAt(i + 4));
        this.subject = X500Name.getInstance(aSN1Sequence.getObjectAt(i + 5));
        this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(aSN1Sequence.getObjectAt(i + 6));
        int size = (aSN1Sequence.size() - (i + 6)) - 1;
        if (size != 0 && z) {
            throw new IllegalArgumentException("version 1 certificate contains extra data");
        }
        while (size > 0) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Sequence.getObjectAt(i + 6 + size);
            switch (aSN1TaggedObject.getTagNo()) {
                case 1:
                    this.issuerUniqueId = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    break;
                case 2:
                    this.subjectUniqueId = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    break;
                case 3:
                    if (z2) {
                        throw new IllegalArgumentException("version 2 certificate cannot contain extensions");
                    }
                    this.extensions = Extensions.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, true));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tag encountered in structure: " + aSN1TaggedObject.getTagNo());
            }
            size--;
        }
    }

    public TBSCertificate(ASN1Integer aSN1Integer, ASN1Integer aSN1Integer2, AlgorithmIdentifier algorithmIdentifier, X500Name x500Name, Validity validity, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo, ASN1BitString aSN1BitString, ASN1BitString aSN1BitString2, Extensions extensions) {
        if (aSN1Integer2 == null) {
            throw new NullPointerException("'serialNumber' cannot be null");
        }
        if (algorithmIdentifier == null) {
            throw new NullPointerException("'signature' cannot be null");
        }
        if (x500Name == null) {
            throw new NullPointerException("'issuer' cannot be null");
        }
        if (validity == null) {
            throw new NullPointerException("'validity' cannot be null");
        }
        if (x500Name2 == null) {
            throw new NullPointerException("'subject' cannot be null");
        }
        if (subjectPublicKeyInfo == null) {
            throw new NullPointerException("'subjectPublicKeyInfo' cannot be null");
        }
        this.version = aSN1Integer != null ? aSN1Integer : new ASN1Integer(0L);
        this.serialNumber = aSN1Integer2;
        this.signature = algorithmIdentifier;
        this.issuer = x500Name;
        this.validity = validity;
        this.subject = x500Name2;
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.issuerUniqueId = aSN1BitString;
        this.subjectUniqueId = aSN1BitString2;
        this.extensions = extensions;
        this.seq = null;
    }

    public int getVersionNumber() {
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Validity getValidity() {
        return this.validity;
    }

    public Time getStartDate() {
        return this.validity.getNotBefore();
    }

    public Time getEndDate() {
        return this.validity.getNotAfter();
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public ASN1BitString getIssuerUniqueId() {
        return this.issuerUniqueId;
    }

    public ASN1BitString getSubjectUniqueId() {
        return this.subjectUniqueId;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.seq == null || (Properties.getPropertyValue("org.bouncycastle.x509.allow_non-der_tbscert") != null && !Properties.isOverrideSet("org.bouncycastle.x509.allow_non-der_tbscert"))) {
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(10);
            if (!this.version.hasValue(0)) {
                aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable) this.version));
            }
            aSN1EncodableVector.add(this.serialNumber);
            aSN1EncodableVector.add(this.signature);
            aSN1EncodableVector.add(this.issuer);
            aSN1EncodableVector.add(this.validity);
            aSN1EncodableVector.add(this.subject);
            aSN1EncodableVector.add(this.subjectPublicKeyInfo);
            if (this.issuerUniqueId != null) {
                aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.issuerUniqueId));
            }
            if (this.subjectUniqueId != null) {
                aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable) this.subjectUniqueId));
            }
            if (this.extensions != null) {
                aSN1EncodableVector.add(new DERTaggedObject(true, 3, (ASN1Encodable) this.extensions));
            }
            return new DERSequence(aSN1EncodableVector);
        }
        return this.seq;
    }
}
