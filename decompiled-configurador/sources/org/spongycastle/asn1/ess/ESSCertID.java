package org.spongycastle.asn1.ess;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.IssuerSerial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/ess/ESSCertID.class */
public class ESSCertID extends ASN1Object {
    private ASN1OctetString certHash;
    private IssuerSerial issuerSerial;

    public static ESSCertID getInstance(Object o) {
        if (o instanceof ESSCertID) {
            return (ESSCertID) o;
        }
        if (o != null) {
            return new ESSCertID(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private ESSCertID(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.certHash = ASN1OctetString.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.issuerSerial = IssuerSerial.getInstance(seq.getObjectAt(1));
        }
    }

    public ESSCertID(byte[] hash) {
        this.certHash = new DEROctetString(hash);
    }

    public ESSCertID(byte[] hash, IssuerSerial issuerSerial) {
        this.certHash = new DEROctetString(hash);
        this.issuerSerial = issuerSerial;
    }

    public byte[] getCertHash() {
        return this.certHash.getOctets();
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certHash);
        if (this.issuerSerial != null) {
            v.add(this.issuerSerial);
        }
        return new DERSequence(v);
    }
}
