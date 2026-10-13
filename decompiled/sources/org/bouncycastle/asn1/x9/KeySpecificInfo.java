package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x9/KeySpecificInfo.class */
public class KeySpecificInfo extends ASN1Object {
    private ASN1ObjectIdentifier algorithm;
    private ASN1OctetString counter;

    public KeySpecificInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1OctetString aSN1OctetString) {
        this.algorithm = aSN1ObjectIdentifier;
        this.counter = aSN1OctetString;
    }

    public static KeySpecificInfo getInstance(Object obj) {
        if (obj instanceof KeySpecificInfo) {
            return (KeySpecificInfo) obj;
        }
        if (obj != null) {
            return new KeySpecificInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private KeySpecificInfo(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        this.algorithm = (ASN1ObjectIdentifier) objects.nextElement();
        this.counter = (ASN1OctetString) objects.nextElement();
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public ASN1OctetString getCounter() {
        return this.counter;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.algorithm, this.counter);
    }
}
