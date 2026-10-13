package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/CountryAndRegions.class */
public class CountryAndRegions extends ASN1Object implements RegionInterface {
    private final CountryOnly countryOnly;
    private final SequenceOfUint8 regions;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/CountryAndRegions$Builder.class */
    public static class Builder {
        private SequenceOfUint8 regionList;
        private CountryOnly countryOnly;

        public Builder setCountryOnly(CountryOnly countryOnly) {
            this.countryOnly = countryOnly;
            return this;
        }

        public Builder setRegions(SequenceOfUint8 sequenceOfUint8) {
            this.regionList = sequenceOfUint8;
            return this;
        }

        public CountryAndRegions createCountryAndRegions() {
            return new CountryAndRegions(this.countryOnly, this.regionList);
        }
    }

    public CountryAndRegions(CountryOnly countryOnly, SequenceOfUint8 sequenceOfUint8) {
        this.countryOnly = countryOnly;
        this.regions = SequenceOfUint8.getInstance(sequenceOfUint8);
    }

    private CountryAndRegions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.countryOnly = CountryOnly.getInstance((Object) aSN1Sequence.getObjectAt(0));
        this.regions = SequenceOfUint8.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static CountryAndRegions getInstance(Object obj) {
        if (obj instanceof CountryAndRegions) {
            return (CountryAndRegions) obj;
        }
        if (obj != null) {
            return new CountryAndRegions(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.countryOnly, this.regions);
    }

    public CountryOnly getCountryOnly() {
        return this.countryOnly;
    }

    public SequenceOfUint8 getRegions() {
        return this.regions;
    }
}
