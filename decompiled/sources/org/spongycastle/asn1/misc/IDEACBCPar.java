package org.spongycastle.asn1.misc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/misc/IDEACBCPar.class */
public class IDEACBCPar extends ASN1Object {
    ASN1OctetString iv;

    public static IDEACBCPar getInstance(Object o) {
        if (o instanceof IDEACBCPar) {
            return (IDEACBCPar) o;
        }
        if (o != null) {
            return new IDEACBCPar(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public IDEACBCPar(byte[] iv) {
        this.iv = new DEROctetString(iv);
    }

    public IDEACBCPar(ASN1Sequence seq) {
        if (seq.size() == 1) {
            this.iv = (ASN1OctetString) seq.getObjectAt(0);
        } else {
            this.iv = null;
        }
    }

    public byte[] getIV() {
        if (this.iv != null) {
            return Arrays.clone(this.iv.getOctets());
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.iv != null) {
            v.add(this.iv);
        }
        return new DERSequence(v);
    }
}
