package org.spongycastle.asn1.cmc;

import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/CertificationRequest.class */
public class CertificationRequest extends ASN1Object {
    private static final ASN1Integer ZERO = new ASN1Integer(0);
    private final CertificationRequestInfo certificationRequestInfo;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final DERBitString signature;

    public CertificationRequest(X500Name subject, AlgorithmIdentifier subjectPublicAlgorithm, DERBitString subjectPublicKey, ASN1Set attributes, AlgorithmIdentifier signatureAlgorithm, DERBitString signature) {
        this.certificationRequestInfo = new CertificationRequestInfo(subject, subjectPublicAlgorithm, subjectPublicKey, attributes);
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = signature;
    }

    private CertificationRequest(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.certificationRequestInfo = new CertificationRequestInfo(ASN1Sequence.getInstance(seq.getObjectAt(0)));
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.signature = DERBitString.getInstance(seq.getObjectAt(2));
    }

    public static CertificationRequest getInstance(Object o) {
        if (o instanceof CertificationRequest) {
            return (CertificationRequest) o;
        }
        if (o != null) {
            return new CertificationRequest(ASN1Sequence.getInstance(o));
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

    public DERBitString getSubjectPublicKey() {
        return DERBitString.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(1));
    }

    public ASN1Primitive parsePublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(getSubjectPublicKey().getOctets());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public DERBitString getSignature() {
        return this.signature;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certificationRequestInfo);
        v.add(this.signatureAlgorithm);
        v.add(this.signature);
        return new DERSequence(v);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/CertificationRequest$CertificationRequestInfo.class */
    private class CertificationRequestInfo extends ASN1Object {
        private final ASN1Integer version;
        private final X500Name subject;
        private final ASN1Sequence subjectPublicKeyInfo;
        private final ASN1Set attributes;

        private CertificationRequestInfo(ASN1Sequence seq) {
            if (seq.size() != 4) {
                throw new IllegalArgumentException("incorrect sequence size for CertificationRequestInfo");
            }
            this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
            this.subject = X500Name.getInstance(seq.getObjectAt(1));
            this.subjectPublicKeyInfo = ASN1Sequence.getInstance(seq.getObjectAt(2));
            if (this.subjectPublicKeyInfo.size() != 2) {
                throw new IllegalArgumentException("incorrect subjectPublicKeyInfo size for CertificationRequestInfo");
            }
            ASN1TaggedObject tagobj = (ASN1TaggedObject) seq.getObjectAt(3);
            if (tagobj.getTagNo() != 0) {
                throw new IllegalArgumentException("incorrect tag number on attributes for CertificationRequestInfo");
            }
            this.attributes = ASN1Set.getInstance(tagobj, false);
        }

        private CertificationRequestInfo(X500Name subject, AlgorithmIdentifier algorithm, DERBitString subjectPublicKey, ASN1Set attributes) {
            this.version = CertificationRequest.ZERO;
            this.subject = subject;
            this.subjectPublicKeyInfo = new DERSequence(new ASN1Encodable[]{algorithm, subjectPublicKey});
            this.attributes = attributes;
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

        @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
        public ASN1Primitive toASN1Primitive() {
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(this.version);
            v.add(this.subject);
            v.add(this.subjectPublicKeyInfo);
            v.add(new DERTaggedObject(false, 0, this.attributes));
            return new DERSequence(v);
        }
    }
}
