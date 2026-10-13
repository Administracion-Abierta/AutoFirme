package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/GCMParameters.class */
public class GCMParameters extends ASN1Object {
    private byte[] nonce;
    private int icvLen;

    public static GCMParameters getInstance(Object obj) {
        if (obj instanceof GCMParameters) {
            return (GCMParameters) obj;
        }
        if (obj != null) {
            return new GCMParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private GCMParameters(ASN1Sequence seq) {
        this.nonce = ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets();
        if (seq.size() == 2) {
            this.icvLen = ASN1Integer.getInstance(seq.getObjectAt(1)).getValue().intValue();
        } else {
            this.icvLen = 12;
        }
    }

    public GCMParameters(byte[] nonce, int icvLen) {
        this.nonce = Arrays.clone(nonce);
        this.icvLen = icvLen;
    }

    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }

    public int getIcvLen() {
        return this.icvLen;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new DEROctetString(this.nonce));
        if (this.icvLen != 12) {
            v.add(new ASN1Integer(this.icvLen));
        }
        return new DERSequence(v);
    }
}
