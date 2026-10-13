package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/CertificateList.class */
public class CertificateList extends ASN1Object {
    TBSCertList tbsCertList;
    AlgorithmIdentifier sigAlgId;
    ASN1BitString sig;
    boolean isHashCodeSet = false;
    int hashCodeValue;

    public static CertificateList getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static CertificateList getInstance(Object obj) {
        if (obj instanceof CertificateList) {
            return (CertificateList) obj;
        }
        if (obj != null) {
            return new CertificateList(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CertificateList(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("sequence wrong size for CertificateList");
        }
        this.tbsCertList = TBSCertList.getInstance(aSN1Sequence.getObjectAt(0));
        this.sigAlgId = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        this.sig = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public TBSCertList getTBSCertList() {
        return this.tbsCertList;
    }

    public TBSCertList.CRLEntry[] getRevokedCertificates() {
        return this.tbsCertList.getRevokedCertificates();
    }

    public Enumeration getRevokedCertificateEnumeration() {
        return this.tbsCertList.getRevokedCertificateEnumeration();
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.sigAlgId;
    }

    public ASN1BitString getSignature() {
        return this.sig;
    }

    public int getVersionNumber() {
        return this.tbsCertList.getVersionNumber();
    }

    public X500Name getIssuer() {
        return this.tbsCertList.getIssuer();
    }

    public Time getThisUpdate() {
        return this.tbsCertList.getThisUpdate();
    }

    public Time getNextUpdate() {
        return this.tbsCertList.getNextUpdate();
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.tbsCertList);
        aSN1EncodableVector.add(this.sigAlgId);
        aSN1EncodableVector.add(this.sig);
        return new DERSequence(aSN1EncodableVector);
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.hashCodeValue = super.hashCode();
            this.isHashCodeSet = true;
        }
        return this.hashCodeValue;
    }
}
