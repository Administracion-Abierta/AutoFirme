package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECFieldElement;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x9/X9FieldElement.class */
public class X9FieldElement extends ASN1Object {
    protected ECFieldElement f;
    private static X9IntegerConverter converter = new X9IntegerConverter();

    public X9FieldElement(ECFieldElement eCFieldElement) {
        this.f = eCFieldElement;
    }

    public ECFieldElement getValue() {
        return this.f;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(converter.integerToBytes(this.f.toBigInteger(), converter.getByteLength(this.f)));
    }
}
