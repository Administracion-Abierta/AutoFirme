package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/IetfAttrSyntax.class */
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

    private IetfAttrSyntax(ASN1Sequence aSN1Sequence) {
        int i;
        this.policyAuthority = null;
        this.valueChoice = -1;
        int i2 = 0;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.policyAuthority = GeneralNames.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(0), false);
            i2 = 0 + 1;
        } else if (aSN1Sequence.size() == 2) {
            this.policyAuthority = GeneralNames.getInstance(aSN1Sequence.getObjectAt(0));
            i2 = 0 + 1;
        }
        if (!(aSN1Sequence.getObjectAt(i2) instanceof ASN1Sequence)) {
            throw new IllegalArgumentException("Non-IetfAttrSyntax encoding");
        }
        Enumeration objects = ((ASN1Sequence) aSN1Sequence.getObjectAt(i2)).getObjects();
        while (objects.hasMoreElements()) {
            ASN1Primitive aSN1Primitive = (ASN1Primitive) objects.nextElement();
            if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
                i = 2;
            } else if (aSN1Primitive instanceof ASN1UTF8String) {
                i = 3;
            } else {
                if (!(aSN1Primitive instanceof DEROctetString)) {
                    throw new IllegalArgumentException("Bad value type encoding IetfAttrSyntax");
                }
                i = 1;
            }
            if (this.valueChoice < 0) {
                this.valueChoice = i;
            }
            if (i != this.valueChoice) {
                throw new IllegalArgumentException("Mix of value types in IetfAttrSyntax");
            }
            this.values.addElement(aSN1Primitive);
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
            ASN1OctetString[] aSN1OctetStringArr = new ASN1OctetString[this.values.size()];
            for (int i = 0; i != aSN1OctetStringArr.length; i++) {
                aSN1OctetStringArr[i] = (ASN1OctetString) this.values.elementAt(i);
            }
            return aSN1OctetStringArr;
        }
        if (getValueType() == 2) {
            ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr = new ASN1ObjectIdentifier[this.values.size()];
            for (int i2 = 0; i2 != aSN1ObjectIdentifierArr.length; i2++) {
                aSN1ObjectIdentifierArr[i2] = (ASN1ObjectIdentifier) this.values.elementAt(i2);
            }
            return aSN1ObjectIdentifierArr;
        }
        ASN1UTF8String[] aSN1UTF8StringArr = new ASN1UTF8String[this.values.size()];
        for (int i3 = 0; i3 != aSN1UTF8StringArr.length; i3++) {
            aSN1UTF8StringArr[i3] = (ASN1UTF8String) this.values.elementAt(i3);
        }
        return aSN1UTF8StringArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        if (this.policyAuthority != null) {
            aSN1EncodableVector.add(new DERTaggedObject(0, this.policyAuthority));
        }
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector(this.values.size());
        Enumeration enumerationElements = this.values.elements();
        while (enumerationElements.hasMoreElements()) {
            aSN1EncodableVector2.add((ASN1Encodable) enumerationElements.nextElement());
        }
        aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
        return new DERSequence(aSN1EncodableVector);
    }
}
