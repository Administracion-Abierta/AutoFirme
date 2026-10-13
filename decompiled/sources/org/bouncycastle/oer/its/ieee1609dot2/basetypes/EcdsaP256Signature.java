package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/EcdsaP256Signature.class */
public class EcdsaP256Signature extends ASN1Object {
    private final EccP256CurvePoint rSig;
    private final ASN1OctetString sSig;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/EcdsaP256Signature$Builder.class */
    public static class Builder {
        private EccP256CurvePoint rSig;
        private ASN1OctetString sSig;

        public Builder setRSig(EccP256CurvePoint eccP256CurvePoint) {
            this.rSig = eccP256CurvePoint;
            return this;
        }

        public Builder setSSig(byte[] bArr) {
            this.sSig = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public Builder setSSig(ASN1OctetString aSN1OctetString) {
            this.sSig = aSN1OctetString;
            return this;
        }

        public EcdsaP256Signature createEcdsaP256Signature() {
            return new EcdsaP256Signature(this.rSig, this.sSig);
        }
    }

    public EcdsaP256Signature(EccP256CurvePoint eccP256CurvePoint, ASN1OctetString aSN1OctetString) {
        this.rSig = eccP256CurvePoint;
        this.sSig = aSN1OctetString;
    }

    private EcdsaP256Signature(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.rSig = EccP256CurvePoint.getInstance(aSN1Sequence.getObjectAt(0));
        this.sSig = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static EcdsaP256Signature getInstance(Object obj) {
        if (obj instanceof EcdsaP256Signature) {
            return (EcdsaP256Signature) obj;
        }
        if (obj != null) {
            return new EcdsaP256Signature(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public EccP256CurvePoint getRSig() {
        return this.rSig;
    }

    public ASN1OctetString getSSig() {
        return this.sSig;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.rSig, this.sSig);
    }
}
