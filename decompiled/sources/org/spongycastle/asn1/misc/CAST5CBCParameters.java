package org.spongycastle.asn1.misc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/misc/CAST5CBCParameters.class */
public class CAST5CBCParameters extends ASN1Object {
    ASN1Integer keyLength;
    ASN1OctetString iv;

    public static CAST5CBCParameters getInstance(Object o) {
        if (o instanceof CAST5CBCParameters) {
            return (CAST5CBCParameters) o;
        }
        if (o != null) {
            return new CAST5CBCParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public CAST5CBCParameters(byte[] iv, int keyLength) {
        this.iv = new DEROctetString(Arrays.clone(iv));
        this.keyLength = new ASN1Integer(keyLength);
    }

    public CAST5CBCParameters(ASN1Sequence seq) {
        this.iv = (ASN1OctetString) seq.getObjectAt(0);
        this.keyLength = (ASN1Integer) seq.getObjectAt(1);
    }

    public byte[] getIV() {
        return Arrays.clone(this.iv.getOctets());
    }

    public int getKeyLength() {
        return this.keyLength.getValue().intValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.iv);
        v.add(this.keyLength);
        return new DERSequence(v);
    }
}
