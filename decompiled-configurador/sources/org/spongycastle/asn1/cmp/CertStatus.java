package org.spongycastle.asn1.cmp;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/CertStatus.class */
public class CertStatus extends ASN1Object {
    private ASN1OctetString certHash;
    private ASN1Integer certReqId;
    private PKIStatusInfo statusInfo;

    private CertStatus(ASN1Sequence seq) {
        this.certHash = ASN1OctetString.getInstance(seq.getObjectAt(0));
        this.certReqId = ASN1Integer.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.statusInfo = PKIStatusInfo.getInstance(seq.getObjectAt(2));
        }
    }

    public CertStatus(byte[] certHash, BigInteger certReqId) {
        this.certHash = new DEROctetString(certHash);
        this.certReqId = new ASN1Integer(certReqId);
    }

    public CertStatus(byte[] certHash, BigInteger certReqId, PKIStatusInfo statusInfo) {
        this.certHash = new DEROctetString(certHash);
        this.certReqId = new ASN1Integer(certReqId);
        this.statusInfo = statusInfo;
    }

    public static CertStatus getInstance(Object o) {
        if (o instanceof CertStatus) {
            return (CertStatus) o;
        }
        if (o != null) {
            return new CertStatus(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ASN1OctetString getCertHash() {
        return this.certHash;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public PKIStatusInfo getStatusInfo() {
        return this.statusInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certHash);
        v.add(this.certReqId);
        if (this.statusInfo != null) {
            v.add(this.statusInfo);
        }
        return new DERSequence(v);
    }
}
