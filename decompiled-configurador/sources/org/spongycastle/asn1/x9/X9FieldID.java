package org.spongycastle.asn1.x9;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x9/X9FieldID.class */
public class X9FieldID extends ASN1Object implements X9ObjectIdentifiers {
    private ASN1ObjectIdentifier id;
    private ASN1Primitive parameters;

    public X9FieldID(BigInteger primeP) {
        this.id = prime_field;
        this.parameters = new ASN1Integer(primeP);
    }

    public X9FieldID(int m, int k1) {
        this(m, k1, 0, 0);
    }

    public X9FieldID(int m, int k1, int k2, int k3) {
        this.id = characteristic_two_field;
        ASN1EncodableVector fieldIdParams = new ASN1EncodableVector();
        fieldIdParams.add(new ASN1Integer(m));
        if (k2 == 0) {
            if (k3 != 0) {
                throw new IllegalArgumentException("inconsistent k values");
            }
            fieldIdParams.add(tpBasis);
            fieldIdParams.add(new ASN1Integer(k1));
        } else {
            if (k2 <= k1 || k3 <= k2) {
                throw new IllegalArgumentException("inconsistent k values");
            }
            fieldIdParams.add(ppBasis);
            ASN1EncodableVector pentanomialParams = new ASN1EncodableVector();
            pentanomialParams.add(new ASN1Integer(k1));
            pentanomialParams.add(new ASN1Integer(k2));
            pentanomialParams.add(new ASN1Integer(k3));
            fieldIdParams.add(new DERSequence(pentanomialParams));
        }
        this.parameters = new DERSequence(fieldIdParams);
    }

    private X9FieldID(ASN1Sequence seq) {
        this.id = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        this.parameters = seq.getObjectAt(1).toASN1Primitive();
    }

    public static X9FieldID getInstance(Object obj) {
        if (obj instanceof X9FieldID) {
            return (X9FieldID) obj;
        }
        if (obj != null) {
            return new X9FieldID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getIdentifier() {
        return this.id;
    }

    public ASN1Primitive getParameters() {
        return this.parameters;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.id);
        v.add(this.parameters);
        return new DERSequence(v);
    }
}
