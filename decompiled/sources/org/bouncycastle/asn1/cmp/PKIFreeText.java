package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/PKIFreeText.class */
public class PKIFreeText extends ASN1Object {
    ASN1Sequence strings;

    private PKIFreeText(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            if (!(objects.nextElement() instanceof ASN1UTF8String)) {
                throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText");
            }
        }
        this.strings = aSN1Sequence;
    }

    public PKIFreeText(ASN1UTF8String aSN1UTF8String) {
        this.strings = new DERSequence(aSN1UTF8String);
    }

    public PKIFreeText(String str) {
        this(new DERUTF8String(str));
    }

    public PKIFreeText(ASN1UTF8String[] aSN1UTF8StringArr) {
        this.strings = new DERSequence(aSN1UTF8StringArr);
    }

    public PKIFreeText(String[] strArr) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(strArr.length);
        for (String str : strArr) {
            aSN1EncodableVector.add(new DERUTF8String(str));
        }
        this.strings = new DERSequence(aSN1EncodableVector);
    }

    public static PKIFreeText getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
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

    public int size() {
        return this.strings.size();
    }

    public DERUTF8String getStringAt(int i) {
        ASN1UTF8String stringAtUTF8 = getStringAtUTF8(i);
        return (null == stringAtUTF8 || (stringAtUTF8 instanceof DERUTF8String)) ? (DERUTF8String) stringAtUTF8 : new DERUTF8String(stringAtUTF8.getString());
    }

    public ASN1UTF8String getStringAtUTF8(int i) {
        return (ASN1UTF8String) this.strings.getObjectAt(i);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.strings;
    }
}
