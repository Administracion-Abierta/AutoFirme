package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/esf/SignaturePolicyIdentifier.class */
public class SignaturePolicyIdentifier extends ASN1Object {
    private SignaturePolicyId signaturePolicyId;
    private boolean isSignaturePolicyImplied = true;

    public static SignaturePolicyIdentifier getInstance(Object obj) {
        if (obj instanceof SignaturePolicyIdentifier) {
            return (SignaturePolicyIdentifier) obj;
        }
        if ((obj instanceof ASN1Null) || hasEncodedTagValue(obj, 5)) {
            return new SignaturePolicyIdentifier();
        }
        if (obj != null) {
            return new SignaturePolicyIdentifier(SignaturePolicyId.getInstance(obj));
        }
        return null;
    }

    public SignaturePolicyIdentifier() {
    }

    public SignaturePolicyIdentifier(SignaturePolicyId signaturePolicyId) {
        this.signaturePolicyId = signaturePolicyId;
    }

    public SignaturePolicyId getSignaturePolicyId() {
        return this.signaturePolicyId;
    }

    public boolean isSignaturePolicyImplied() {
        return this.isSignaturePolicyImplied;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.isSignaturePolicyImplied ? DERNull.INSTANCE : this.signaturePolicyId.toASN1Primitive();
    }
}
