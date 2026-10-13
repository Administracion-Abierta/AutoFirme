package org.spongycastle.asn1.ess;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.IssuerSerial;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ess/ESSCertIDv2.class */
public class ESSCertIDv2 extends ASN1Object {
    private AlgorithmIdentifier hashAlgorithm;
    private byte[] certHash;
    private IssuerSerial issuerSerial;
    private static final AlgorithmIdentifier DEFAULT_ALG_ID = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);

    public static ESSCertIDv2 getInstance(Object o) {
        if (o instanceof ESSCertIDv2) {
            return (ESSCertIDv2) o;
        }
        if (o != null) {
            return new ESSCertIDv2(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private ESSCertIDv2(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int count = 0;
        if (!(seq.getObjectAt(0) instanceof ASN1OctetString)) {
            count = 0 + 1;
            this.hashAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0).toASN1Primitive());
        } else {
            this.hashAlgorithm = DEFAULT_ALG_ID;
        }
        int i = count;
        int count2 = count + 1;
        this.certHash = ASN1OctetString.getInstance(seq.getObjectAt(i).toASN1Primitive()).getOctets();
        if (seq.size() > count2) {
            this.issuerSerial = IssuerSerial.getInstance(seq.getObjectAt(count2));
        }
    }

    public ESSCertIDv2(byte[] certHash) {
        this(null, certHash, null);
    }

    public ESSCertIDv2(AlgorithmIdentifier algId, byte[] certHash) {
        this(algId, certHash, null);
    }

    public ESSCertIDv2(byte[] certHash, IssuerSerial issuerSerial) {
        this(null, certHash, issuerSerial);
    }

    public ESSCertIDv2(AlgorithmIdentifier algId, byte[] certHash, IssuerSerial issuerSerial) {
        if (algId == null) {
            this.hashAlgorithm = DEFAULT_ALG_ID;
        } else {
            this.hashAlgorithm = algId;
        }
        this.certHash = Arrays.clone(certHash);
        this.issuerSerial = issuerSerial;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[] getCertHash() {
        return Arrays.clone(this.certHash);
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (!this.hashAlgorithm.equals(DEFAULT_ALG_ID)) {
            v.add(this.hashAlgorithm);
        }
        v.add(new DEROctetString(this.certHash).toASN1Primitive());
        if (this.issuerSerial != null) {
            v.add(this.issuerSerial);
        }
        return new DERSequence(v);
    }
}
