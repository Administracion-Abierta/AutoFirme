package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/RectangularRegion.class */
public class RectangularRegion extends ASN1Object implements RegionInterface {
    private final TwoDLocation northWest;
    private final TwoDLocation southEast;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/RectangularRegion$Builder.class */
    public static class Builder {
        private TwoDLocation northWest;
        private TwoDLocation southEast;

        public Builder setNorthWest(TwoDLocation twoDLocation) {
            this.northWest = twoDLocation;
            return this;
        }

        public Builder setSouthEast(TwoDLocation twoDLocation) {
            this.southEast = twoDLocation;
            return this;
        }

        public RectangularRegion createRectangularRegion() {
            return new RectangularRegion(this.northWest, this.southEast);
        }
    }

    public RectangularRegion(TwoDLocation twoDLocation, TwoDLocation twoDLocation2) {
        this.northWest = twoDLocation;
        this.southEast = twoDLocation2;
    }

    private RectangularRegion(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.northWest = TwoDLocation.getInstance(aSN1Sequence.getObjectAt(0));
        this.southEast = TwoDLocation.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static RectangularRegion getInstance(Object obj) {
        if (obj instanceof RectangularRegion) {
            return (RectangularRegion) obj;
        }
        if (obj != null) {
            return new RectangularRegion(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TwoDLocation getNorthWest() {
        return this.northWest;
    }

    public TwoDLocation getSouthEast() {
        return this.southEast;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.northWest, this.southEast});
    }

    public static Builder builder() {
        return new Builder();
    }
}
