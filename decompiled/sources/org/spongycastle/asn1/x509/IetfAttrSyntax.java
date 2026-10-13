package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/IetfAttrSyntax.class */
public class IetfAttrSyntax extends ASN1Object {
    public static final int VALUE_OCTETS = 1;
    public static final int VALUE_OID = 2;
    public static final int VALUE_UTF8 = 3;
    GeneralNames policyAuthority;
    Vector values = new Vector();
    int valueChoice;

    public static IetfAttrSyntax getInstance(Object obj) {
        if (obj instanceof IetfAttrSyntax) {
            return (IetfAttrSyntax) obj;
        }
        if (obj != null) {
            return new IetfAttrSyntax(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private IetfAttrSyntax(ASN1Sequence seq) {
        int type;
        this.policyAuthority = null;
        this.valueChoice = -1;
        int i = 0;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.policyAuthority = GeneralNames.getInstance((ASN1TaggedObject) seq.getObjectAt(0), false);
            i = 0 + 1;
        } else if (seq.size() == 2) {
            this.policyAuthority = GeneralNames.getInstance(seq.getObjectAt(0));
            i = 0 + 1;
        }
        if (!(seq.getObjectAt(i) instanceof ASN1Sequence)) {
            throw new IllegalArgumentException("Non-IetfAttrSyntax encoding");
        }
        Enumeration e = ((ASN1Sequence) seq.getObjectAt(i)).getObjects();
        while (e.hasMoreElements()) {
            ASN1Primitive obj = (ASN1Primitive) e.nextElement();
            if (obj instanceof ASN1ObjectIdentifier) {
                type = 2;
            } else if (obj instanceof DERUTF8String) {
                type = 3;
            } else if (obj instanceof DEROctetString) {
                type = 1;
            } else {
                throw new IllegalArgumentException("Bad value type encoding IetfAttrSyntax");
            }
            if (this.valueChoice < 0) {
                this.valueChoice = type;
            }
            if (type != this.valueChoice) {
                throw new IllegalArgumentException("Mix of value types in IetfAttrSyntax");
            }
            this.values.addElement(obj);
        }
    }

    public GeneralNames getPolicyAuthority() {
        return this.policyAuthority;
    }

    public int getValueType() {
        return this.valueChoice;
    }

    public Object[] getValues() {
        if (getValueType() == 1) {
            ASN1OctetString[] tmp = new ASN1OctetString[this.values.size()];
            for (int i = 0; i != tmp.length; i++) {
                tmp[i] = (ASN1OctetString) this.values.elementAt(i);
            }
            return tmp;
        }
        if (getValueType() == 2) {
            ASN1ObjectIdentifier[] tmp2 = new ASN1ObjectIdentifier[this.values.size()];
            for (int i2 = 0; i2 != tmp2.length; i2++) {
                tmp2[i2] = (ASN1ObjectIdentifier) this.values.elementAt(i2);
            }
            return tmp2;
        }
        DERUTF8String[] tmp3 = new DERUTF8String[this.values.size()];
        for (int i3 = 0; i3 != tmp3.length; i3++) {
            tmp3[i3] = (DERUTF8String) this.values.elementAt(i3);
        }
        return tmp3;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.policyAuthority != null) {
            v.add(new DERTaggedObject(0, this.policyAuthority));
        }
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        Enumeration i = this.values.elements();
        while (i.hasMoreElements()) {
            v2.add((ASN1Encodable) i.nextElement());
        }
        v.add(new DERSequence(v2));
        return new DERSequence(v);
    }
}
