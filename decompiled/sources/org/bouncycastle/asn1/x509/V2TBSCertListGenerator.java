package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/V2TBSCertListGenerator.class */
public class V2TBSCertListGenerator {
    private AlgorithmIdentifier signature;
    private X500Name issuer;
    private Time thisUpdate;
    private static final ASN1Sequence[] reasons = new ASN1Sequence[11];
    private ASN1Integer version = new ASN1Integer(1);
    private Time nextUpdate = null;
    private Extensions extensions = null;
    private ASN1EncodableVector crlentries = new ASN1EncodableVector();

    public void setSignature(AlgorithmIdentifier algorithmIdentifier) {
        this.signature = algorithmIdentifier;
    }

    public void setIssuer(X509Name x509Name) {
        this.issuer = X500Name.getInstance(x509Name.toASN1Primitive());
    }

    public void setIssuer(X500Name x500Name) {
        this.issuer = x500Name;
    }

    public void setThisUpdate(ASN1UTCTime aSN1UTCTime) {
        this.thisUpdate = new Time(aSN1UTCTime);
    }

    public void setNextUpdate(ASN1UTCTime aSN1UTCTime) {
        this.nextUpdate = new Time(aSN1UTCTime);
    }

    public void setThisUpdate(Time time) {
        this.thisUpdate = time;
    }

    public void setNextUpdate(Time time) {
        this.nextUpdate = time;
    }

    public void addCRLEntry(ASN1Sequence aSN1Sequence) {
        this.crlentries.add(aSN1Sequence);
    }

    public void addCRLEntry(ASN1Integer aSN1Integer, ASN1UTCTime aSN1UTCTime, int i) {
        addCRLEntry(aSN1Integer, new Time(aSN1UTCTime), i);
    }

    public void addCRLEntry(ASN1Integer aSN1Integer, Time time, int i) {
        addCRLEntry(aSN1Integer, time, i, null);
    }

    public void addCRLEntry(ASN1Integer aSN1Integer, Time time, int i, ASN1GeneralizedTime aSN1GeneralizedTime) {
        if (i == 0) {
            if (aSN1GeneralizedTime != null) {
                internalAddCRLEntry(aSN1Integer, time, new DERSequence(createInvalidityDateExtension(aSN1GeneralizedTime)));
                return;
            } else {
                addCRLEntry(aSN1Integer, time, (Extensions) null);
                return;
            }
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        if (i >= reasons.length) {
            aSN1EncodableVector.add(createReasonExtension(i));
        } else {
            if (i < 0) {
                throw new IllegalArgumentException("invalid reason value: " + i);
            }
            aSN1EncodableVector.add(reasons[i]);
        }
        if (aSN1GeneralizedTime != null) {
            aSN1EncodableVector.add(createInvalidityDateExtension(aSN1GeneralizedTime));
        }
        internalAddCRLEntry(aSN1Integer, time, new DERSequence(aSN1EncodableVector));
    }

    private void internalAddCRLEntry(ASN1Integer aSN1Integer, Time time, ASN1Sequence aSN1Sequence) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(aSN1Integer);
        aSN1EncodableVector.add(time);
        if (aSN1Sequence != null) {
            aSN1EncodableVector.add(aSN1Sequence);
        }
        addCRLEntry(new DERSequence(aSN1EncodableVector));
    }

    public void addCRLEntry(ASN1Integer aSN1Integer, Time time, Extensions extensions) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(aSN1Integer);
        aSN1EncodableVector.add(time);
        if (extensions != null) {
            aSN1EncodableVector.add(extensions);
        }
        addCRLEntry(new DERSequence(aSN1EncodableVector));
    }

    public void setExtensions(X509Extensions x509Extensions) {
        setExtensions(Extensions.getInstance(x509Extensions));
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public TBSCertList generateTBSCertList() {
        if (this.signature == null || this.issuer == null || this.thisUpdate == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 TBSCertList generator");
        }
        return new TBSCertList(generateTBSCertStructure());
    }

    public ASN1Sequence generatePreTBSCertList() {
        if (this.signature != null) {
            throw new IllegalStateException("signature should not be set in PreTBSCertList generator");
        }
        if (this.issuer == null || this.thisUpdate == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 PreTBSCertList generator");
        }
        return generateTBSCertStructure();
    }

    private ASN1Sequence generateTBSCertStructure() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(7);
        aSN1EncodableVector.add(this.version);
        if (this.signature != null) {
            aSN1EncodableVector.add(this.signature);
        }
        aSN1EncodableVector.add(this.issuer);
        aSN1EncodableVector.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            aSN1EncodableVector.add(this.nextUpdate);
        }
        if (this.crlentries.size() != 0) {
            aSN1EncodableVector.add(new DERSequence(this.crlentries));
        }
        if (this.extensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(0, this.extensions));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    private static ASN1Sequence createReasonExtension(int i) {
        try {
            return new DERSequence(Extension.reasonCode, new DEROctetString(CRLReason.lookup(i).getEncoded()));
        } catch (IOException e) {
            throw new IllegalArgumentException("error encoding reason: " + e);
        }
    }

    private static ASN1Sequence createInvalidityDateExtension(ASN1GeneralizedTime aSN1GeneralizedTime) {
        try {
            return new DERSequence(Extension.invalidityDate, new DEROctetString(aSN1GeneralizedTime.getEncoded()));
        } catch (IOException e) {
            throw new IllegalArgumentException("error encoding reason: " + e);
        }
    }

    static {
        reasons[0] = createReasonExtension(0);
        reasons[1] = createReasonExtension(1);
        reasons[2] = createReasonExtension(2);
        reasons[3] = createReasonExtension(3);
        reasons[4] = createReasonExtension(4);
        reasons[5] = createReasonExtension(5);
        reasons[6] = createReasonExtension(6);
        reasons[7] = createReasonExtension(7);
        reasons[8] = createReasonExtension(8);
        reasons[9] = createReasonExtension(9);
        reasons[10] = createReasonExtension(10);
    }
}
