package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/TBSCertList.class */
public class TBSCertList extends ASN1Object {
    ASN1Integer version;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time thisUpdate;
    Time nextUpdate;
    ASN1Sequence revokedCertificates;
    Extensions crlExtensions;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/TBSCertList$CRLEntry.class */
    public static class CRLEntry extends ASN1Object {
        ASN1Sequence seq;
        Extensions crlEntryExtensions;

        private CRLEntry(ASN1Sequence aSN1Sequence) {
            if (aSN1Sequence.size() < 2 || aSN1Sequence.size() > 3) {
                throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
            }
            this.seq = aSN1Sequence;
        }

        public static CRLEntry getInstance(Object obj) {
            if (obj instanceof CRLEntry) {
                return (CRLEntry) obj;
            }
            if (obj != null) {
                return new CRLEntry(ASN1Sequence.getInstance(obj));
            }
            return null;
        }

        public ASN1Integer getUserCertificate() {
            return ASN1Integer.getInstance(this.seq.getObjectAt(0));
        }

        public Time getRevocationDate() {
            return Time.getInstance(this.seq.getObjectAt(1));
        }

        public Extensions getExtensions() {
            if (this.crlEntryExtensions == null && this.seq.size() == 3) {
                this.crlEntryExtensions = Extensions.getInstance(this.seq.getObjectAt(2));
            }
            return this.crlEntryExtensions;
        }

        @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
        public ASN1Primitive toASN1Primitive() {
            return this.seq;
        }

        public boolean hasExtensions() {
            return this.seq.size() == 3;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/TBSCertList$EmptyEnumeration.class */
    private static class EmptyEnumeration implements Enumeration {
        private EmptyEnumeration() {
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            throw new NoSuchElementException("Empty Enumeration");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/TBSCertList$RevokedCertificatesEnumeration.class */
    private static class RevokedCertificatesEnumeration implements Enumeration {
        private final Enumeration en;

        RevokedCertificatesEnumeration(Enumeration enumeration) {
            this.en = enumeration;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.en.hasMoreElements();
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            return CRLEntry.getInstance(this.en.nextElement());
        }
    }

    public static TBSCertList getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static TBSCertList getInstance(Object obj) {
        if (obj instanceof TBSCertList) {
            return (TBSCertList) obj;
        }
        if (obj != null) {
            return new TBSCertList(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TBSCertList(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 3 || aSN1Sequence.size() > 7) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        int i = 0;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
            i = 0 + 1;
            this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        } else {
            this.version = null;
        }
        int i2 = i;
        int i3 = i + 1;
        this.signature = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i2));
        int i4 = i3 + 1;
        this.issuer = X500Name.getInstance(aSN1Sequence.getObjectAt(i3));
        int i5 = i4 + 1;
        this.thisUpdate = Time.getInstance(aSN1Sequence.getObjectAt(i4));
        if (i5 < aSN1Sequence.size() && ((aSN1Sequence.getObjectAt(i5) instanceof ASN1UTCTime) || (aSN1Sequence.getObjectAt(i5) instanceof ASN1GeneralizedTime) || (aSN1Sequence.getObjectAt(i5) instanceof Time))) {
            i5++;
            this.nextUpdate = Time.getInstance(aSN1Sequence.getObjectAt(i5));
        }
        if (i5 < aSN1Sequence.size() && !(aSN1Sequence.getObjectAt(i5) instanceof ASN1TaggedObject)) {
            int i6 = i5;
            i5++;
            this.revokedCertificates = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(i6));
        }
        if (i5 >= aSN1Sequence.size() || !(aSN1Sequence.getObjectAt(i5) instanceof ASN1TaggedObject)) {
            return;
        }
        this.crlExtensions = Extensions.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(i5), true));
    }

    public int getVersionNumber() {
        if (this.version == null) {
            return 1;
        }
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Time getThisUpdate() {
        return this.thisUpdate;
    }

    public Time getNextUpdate() {
        return this.nextUpdate;
    }

    public CRLEntry[] getRevokedCertificates() {
        if (this.revokedCertificates == null) {
            return new CRLEntry[0];
        }
        CRLEntry[] cRLEntryArr = new CRLEntry[this.revokedCertificates.size()];
        for (int i = 0; i < cRLEntryArr.length; i++) {
            cRLEntryArr[i] = CRLEntry.getInstance(this.revokedCertificates.getObjectAt(i));
        }
        return cRLEntryArr;
    }

    public Enumeration getRevokedCertificateEnumeration() {
        return this.revokedCertificates == null ? new EmptyEnumeration() : new RevokedCertificatesEnumeration(this.revokedCertificates.getObjects());
    }

    public Extensions getExtensions() {
        return this.crlExtensions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(7);
        if (this.version != null) {
            aSN1EncodableVector.add(this.version);
        }
        aSN1EncodableVector.add(this.signature);
        aSN1EncodableVector.add(this.issuer);
        aSN1EncodableVector.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            aSN1EncodableVector.add(this.nextUpdate);
        }
        if (this.revokedCertificates != null) {
            aSN1EncodableVector.add(this.revokedCertificates);
        }
        if (this.crlExtensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(0, this.crlExtensions));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
