package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/AltSignatureValue.class */
public class AltSignatureValue extends ASN1Object {
    private final ASN1BitString signature;

    public static AltSignatureValue getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1BitString.getInstance(aSN1TaggedObject, z));
    }

    public static AltSignatureValue getInstance(Object obj) {
        if (obj instanceof AltSignatureValue) {
            return (AltSignatureValue) obj;
        }
        if (obj != null) {
            return new AltSignatureValue(ASN1BitString.getInstance(obj));
        }
        return null;
    }

    public static AltSignatureValue fromExtensions(Extensions extensions) {
        return getInstance(Extensions.getExtensionParsedValue(extensions, Extension.altSignatureValue));
    }

    private AltSignatureValue(ASN1BitString aSN1BitString) {
        this.signature = aSN1BitString;
    }

    public AltSignatureValue(byte[] bArr) {
        this.signature = new DERBitString(bArr);
    }

    public ASN1BitString getSignature() {
        return this.signature;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.signature;
    }
}
