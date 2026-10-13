package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/CertificationRequest.class */
public class CertificationRequest extends ASN1Object {
    private static final ASN1Integer ZERO = new ASN1Integer(0);
    private final CertificationRequestInfo certificationRequestInfo;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final ASN1BitString signature;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/CertificationRequest$CertificationRequestInfo.class */
    private static class CertificationRequestInfo extends ASN1Object {
        private final ASN1Integer version;
        private final X500Name subject;
        private final ASN1Sequence subjectPublicKeyInfo;
        private final ASN1Set attributes;

        private CertificationRequestInfo(ASN1Sequence aSN1Sequence) {
            if (aSN1Sequence.size() != 4) {
                throw new IllegalArgumentException("incorrect sequence size for CertificationRequestInfo");
            }
            this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
            this.subject = X500Name.getInstance(aSN1Sequence.getObjectAt(1));
            this.subjectPublicKeyInfo = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(2));
            if (this.subjectPublicKeyInfo.size() != 2) {
                throw new IllegalArgumentException("incorrect subjectPublicKeyInfo size for CertificationRequestInfo");
            }
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Sequence.getObjectAt(3);
            if (aSN1TaggedObject.getTagNo() != 0) {
                throw new IllegalArgumentException("incorrect tag number on attributes for CertificationRequestInfo");
            }
            this.attributes = ASN1Set.getInstance(aSN1TaggedObject, false);
        }

        private CertificationRequestInfo(X500Name x500Name, AlgorithmIdentifier algorithmIdentifier, ASN1BitString aSN1BitString, ASN1Set aSN1Set) {
            this.version = CertificationRequest.ZERO;
            this.subject = x500Name;
            this.subjectPublicKeyInfo = new DERSequence(new ASN1Encodable[]{algorithmIdentifier, aSN1BitString});
            this.attributes = aSN1Set;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ASN1Integer getVersion() {
            return this.version;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public X500Name getSubject() {
            return this.subject;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ASN1Sequence getSubjectPublicKeyInfo() {
            return this.subjectPublicKeyInfo;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ASN1Set getAttributes() {
            return this.attributes;
        }

        @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
        public ASN1Primitive toASN1Primitive() {
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
            aSN1EncodableVector.add(this.version);
            aSN1EncodableVector.add(this.subject);
            aSN1EncodableVector.add(this.subjectPublicKeyInfo);
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.attributes));
            return new DERSequence(aSN1EncodableVector);
        }
    }

    public CertificationRequest(X500Name x500Name, AlgorithmIdentifier algorithmIdentifier, ASN1BitString aSN1BitString, ASN1Set aSN1Set, AlgorithmIdentifier algorithmIdentifier2, ASN1BitString aSN1BitString2) {
        this.certificationRequestInfo = new CertificationRequestInfo(x500Name, algorithmIdentifier, aSN1BitString, aSN1Set);
        this.signatureAlgorithm = algorithmIdentifier2;
        this.signature = aSN1BitString2;
    }

    private CertificationRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.certificationRequestInfo = new CertificationRequestInfo(ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(0)));
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        this.signature = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public static CertificationRequest getInstance(Object obj) {
        if (obj instanceof CertificationRequest) {
            return (CertificationRequest) obj;
        }
        if (obj != null) {
            return new CertificationRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public BigInteger getVersion() {
        return this.certificationRequestInfo.getVersion().getValue();
    }

    public X500Name getSubject() {
        return this.certificationRequestInfo.getSubject();
    }

    public ASN1Set getAttributes() {
        return this.certificationRequestInfo.getAttributes();
    }

    public AlgorithmIdentifier getSubjectPublicKeyAlgorithm() {
        return AlgorithmIdentifier.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(0));
    }

    public ASN1BitString getSubjectPublicKey() {
        return ASN1BitString.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(1));
    }

    public ASN1Primitive parsePublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(getSubjectPublicKey().getOctets());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public ASN1BitString getSignature() {
        return this.signature;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.certificationRequestInfo);
        aSN1EncodableVector.add(this.signatureAlgorithm);
        aSN1EncodableVector.add(this.signature);
        return new DERSequence(aSN1EncodableVector);
    }
}
