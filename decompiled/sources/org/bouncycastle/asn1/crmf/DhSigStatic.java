package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/DhSigStatic.class */
public class DhSigStatic extends ASN1Object {
    private final IssuerAndSerialNumber issuerAndSerial;
    private final ASN1OctetString hashValue;

    public DhSigStatic(byte[] bArr) {
        this(null, bArr);
    }

    public DhSigStatic(IssuerAndSerialNumber issuerAndSerialNumber, byte[] bArr) {
        this.issuerAndSerial = issuerAndSerialNumber;
        this.hashValue = new DEROctetString(Arrays.clone(bArr));
    }

    public static DhSigStatic getInstance(Object obj) {
        if (obj instanceof DhSigStatic) {
            return (DhSigStatic) obj;
        }
        if (obj != null) {
            return new DhSigStatic(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private DhSigStatic(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() == 1) {
            this.issuerAndSerial = null;
            this.hashValue = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        } else {
            if (aSN1Sequence.size() != 2) {
                throw new IllegalArgumentException("sequence wrong length for DhSigStatic");
            }
            this.issuerAndSerial = IssuerAndSerialNumber.getInstance(aSN1Sequence.getObjectAt(0));
            this.hashValue = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public IssuerAndSerialNumber getIssuerAndSerial() {
        return this.issuerAndSerial;
    }

    public byte[] getHashValue() {
        return Arrays.clone(this.hashValue.getOctets());
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        if (this.issuerAndSerial != null) {
            aSN1EncodableVector.add(this.issuerAndSerial);
        }
        aSN1EncodableVector.add(this.hashValue);
        return new DERSequence(aSN1EncodableVector);
    }
}
