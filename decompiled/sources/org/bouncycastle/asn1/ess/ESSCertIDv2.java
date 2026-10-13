package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ess/ESSCertIDv2.class */
public class ESSCertIDv2 extends ASN1Object {
    private static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    private AlgorithmIdentifier hashAlgorithm;
    private ASN1OctetString certHash;
    private IssuerSerial issuerSerial;

    public static ESSCertIDv2 from(ESSCertID eSSCertID) {
        return new ESSCertIDv2(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), eSSCertID.getCertHashObject(), eSSCertID.getIssuerSerial());
    }

    public static ESSCertIDv2 getInstance(Object obj) {
        if (obj instanceof ESSCertIDv2) {
            return (ESSCertIDv2) obj;
        }
        if (obj != null) {
            return new ESSCertIDv2(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private ESSCertIDv2(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        int i = 0;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1OctetString) {
            this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
        } else {
            i = 0 + 1;
            this.hashAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        }
        int i2 = i;
        int i3 = i + 1;
        this.certHash = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(i2));
        if (aSN1Sequence.size() > i3) {
            this.issuerSerial = IssuerSerial.getInstance(aSN1Sequence.getObjectAt(i3));
        }
    }

    public ESSCertIDv2(byte[] bArr) {
        this((AlgorithmIdentifier) null, bArr, (IssuerSerial) null);
    }

    public ESSCertIDv2(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this(algorithmIdentifier, bArr, (IssuerSerial) null);
    }

    public ESSCertIDv2(byte[] bArr, IssuerSerial issuerSerial) {
        this((AlgorithmIdentifier) null, bArr, issuerSerial);
    }

    public ESSCertIDv2(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, IssuerSerial issuerSerial) {
        this.hashAlgorithm = algorithmIdentifier == null ? DEFAULT_HASH_ALGORITHM : algorithmIdentifier;
        this.certHash = new DEROctetString(Arrays.clone(bArr));
        this.issuerSerial = issuerSerial;
    }

    public ESSCertIDv2(AlgorithmIdentifier algorithmIdentifier, ASN1OctetString aSN1OctetString, IssuerSerial issuerSerial) {
        algorithmIdentifier = algorithmIdentifier == null ? DEFAULT_HASH_ALGORITHM : algorithmIdentifier;
        if (aSN1OctetString == null) {
            throw new NullPointerException("'certHash' cannot be null");
        }
        this.hashAlgorithm = algorithmIdentifier;
        this.certHash = aSN1OctetString;
        this.issuerSerial = issuerSerial;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public ASN1OctetString getCertHashObject() {
        return this.certHash;
    }

    public byte[] getCertHash() {
        return Arrays.clone(this.certHash.getOctets());
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (!DEFAULT_HASH_ALGORITHM.equals(this.hashAlgorithm)) {
            aSN1EncodableVector.add(this.hashAlgorithm);
        }
        aSN1EncodableVector.add(this.certHash);
        if (this.issuerSerial != null) {
            aSN1EncodableVector.add(this.issuerSerial);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
