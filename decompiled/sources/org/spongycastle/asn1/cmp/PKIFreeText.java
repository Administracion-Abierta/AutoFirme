package org.spongycastle.asn1.cmp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/PKIFreeText.class */
public class PKIFreeText extends ASN1Object {
    ASN1Sequence strings;

    public static PKIFreeText getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static PKIFreeText getInstance(Object obj) {
        if (obj instanceof PKIFreeText) {
            return (PKIFreeText) obj;
        }
        if (obj != null) {
            return new PKIFreeText(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PKIFreeText(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            if (!(e.nextElement() instanceof DERUTF8String)) {
                throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText");
            }
        }
        this.strings = seq;
    }

    public PKIFreeText(DERUTF8String p) {
        this.strings = new DERSequence(p);
    }

    public PKIFreeText(String p) {
        this(new DERUTF8String(p));
    }

    public PKIFreeText(DERUTF8String[] strs) {
        this.strings = new DERSequence(strs);
    }

    public PKIFreeText(String[] strs) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (String str : strs) {
            v.add(new DERUTF8String(str));
        }
        this.strings = new DERSequence(v);
    }

    public int size() {
        return this.strings.size();
    }

    public DERUTF8String getStringAt(int i) {
        return (DERUTF8String) this.strings.getObjectAt(i);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.strings;
    }
}
