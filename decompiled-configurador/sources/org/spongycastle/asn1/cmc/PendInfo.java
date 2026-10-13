package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/PendInfo.class */
public class PendInfo extends ASN1Object {
    private final byte[] pendToken;
    private final ASN1GeneralizedTime pendTime;

    public PendInfo(byte[] pendToken, ASN1GeneralizedTime pendTime) {
        this.pendToken = Arrays.clone(pendToken);
        this.pendTime = pendTime;
    }

    private PendInfo(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pendToken = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
        this.pendTime = ASN1GeneralizedTime.getInstance(seq.getObjectAt(1));
    }

    public static PendInfo getInstance(Object o) {
        if (o instanceof PendInfo) {
            return (PendInfo) o;
        }
        if (o != null) {
            return new PendInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new DEROctetString(this.pendToken));
        v.add(this.pendTime);
        return new DERSequence(v);
    }

    public byte[] getPendToken() {
        return this.pendToken;
    }

    public ASN1GeneralizedTime getPendTime() {
        return this.pendTime;
    }
}
