package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/ValidityPeriod.class */
public class ValidityPeriod extends ASN1Object {
    private final Time32 start;
    private final Duration duration;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/ValidityPeriod$Builder.class */
    public static class Builder {
        private Time32 start;
        private Duration duration;

        public Builder setStart(Time32 time32) {
            this.start = time32;
            return this;
        }

        public Builder setDuration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public ValidityPeriod createValidityPeriod() {
            return new ValidityPeriod(this.start, this.duration);
        }
    }

    public ValidityPeriod(Time32 time32, Duration duration) {
        this.start = time32;
        this.duration = duration;
    }

    public static ValidityPeriod getInstance(Object obj) {
        if (obj instanceof ValidityPeriod) {
            return (ValidityPeriod) obj;
        }
        if (obj != null) {
            return new ValidityPeriod(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private ValidityPeriod(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.start = Time32.getInstance((Object) aSN1Sequence.getObjectAt(0));
        this.duration = Duration.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Time32 getStart() {
        return this.start;
    }

    public Duration getDuration() {
        return this.duration;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.start, this.duration});
    }

    public String toString() {
        return "ValidityPeriod[" + this.start + " " + this.duration + "]";
    }
}
