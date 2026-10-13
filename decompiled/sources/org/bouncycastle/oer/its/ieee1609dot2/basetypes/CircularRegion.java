package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/CircularRegion.class */
public class CircularRegion extends ASN1Object implements RegionInterface {
    private final TwoDLocation center;
    private final UINT16 radius;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/CircularRegion$Builder.class */
    public static class Builder {
        private TwoDLocation center;
        private UINT16 radius;

        public Builder setCenter(TwoDLocation twoDLocation) {
            this.center = twoDLocation;
            return this;
        }

        public Builder setRadius(UINT16 uint16) {
            this.radius = uint16;
            return this;
        }

        public CircularRegion createCircularRegion() {
            return new CircularRegion(this.center, this.radius);
        }
    }

    public CircularRegion(TwoDLocation twoDLocation, UINT16 uint16) {
        this.center = twoDLocation;
        this.radius = uint16;
    }

    private CircularRegion(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.center = TwoDLocation.getInstance(aSN1Sequence.getObjectAt(0));
        this.radius = UINT16.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static CircularRegion getInstance(Object obj) {
        if (obj instanceof CircularRegion) {
            return (CircularRegion) obj;
        }
        if (obj != null) {
            return new CircularRegion(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TwoDLocation getCenter() {
        return this.center;
    }

    public UINT16 getRadius() {
        return this.radius;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.center, this.radius);
    }

    public static Builder builder() {
        return new Builder();
    }
}
