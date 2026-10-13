package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/BitmapSspRange.class */
public class BitmapSspRange extends ASN1Object {
    private final ASN1OctetString sspValue;
    private final ASN1OctetString sspBitMask;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/BitmapSspRange$Builder.class */
    public static class Builder {
        private ASN1OctetString sspValue;
        private ASN1OctetString sspBitMask;

        public Builder setSspValue(ASN1OctetString aSN1OctetString) {
            this.sspValue = aSN1OctetString;
            return this;
        }

        public Builder setSspBitMask(ASN1OctetString aSN1OctetString) {
            this.sspBitMask = aSN1OctetString;
            return this;
        }

        public Builder setSspValue(byte[] bArr) {
            this.sspValue = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public Builder setSspBitMask(byte[] bArr) {
            this.sspBitMask = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public BitmapSspRange createBitmapSspRange() {
            return new BitmapSspRange(this.sspValue, this.sspBitMask);
        }
    }

    public BitmapSspRange(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2) {
        this.sspValue = aSN1OctetString;
        this.sspBitMask = aSN1OctetString2;
    }

    private BitmapSspRange(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        this.sspValue = ASN1OctetString.getInstance(it.next());
        this.sspBitMask = ASN1OctetString.getInstance(it.next());
    }

    public static BitmapSspRange getInstance(Object obj) {
        if (obj instanceof BitmapSspRange) {
            return (BitmapSspRange) obj;
        }
        if (obj != null) {
            return new BitmapSspRange(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getSspValue() {
        return this.sspValue;
    }

    public ASN1OctetString getSspBitMask() {
        return this.sspBitMask;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.sspValue, this.sspBitMask);
    }

    public static Builder builder() {
        return new Builder();
    }
}
