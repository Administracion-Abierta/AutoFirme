package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/MissingCrlIdentifier.class */
public class MissingCrlIdentifier extends ASN1Object {
    private final HashedId3 cracaId;
    private final CrlSeries crlSeries;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/MissingCrlIdentifier$Builder.class */
    public static class Builder {
        private HashedId3 cracaId;
        private CrlSeries crlSeries;

        public Builder setCracaId(HashedId3 hashedId3) {
            this.cracaId = hashedId3;
            return this;
        }

        public Builder setCrlSeries(CrlSeries crlSeries) {
            this.crlSeries = crlSeries;
            return this;
        }

        public MissingCrlIdentifier createMissingCrlIdentifier() {
            return new MissingCrlIdentifier(this.cracaId, this.crlSeries);
        }
    }

    public MissingCrlIdentifier(HashedId3 hashedId3, CrlSeries crlSeries) {
        this.cracaId = hashedId3;
        this.crlSeries = crlSeries;
    }

    private MissingCrlIdentifier(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.cracaId = HashedId3.getInstance(aSN1Sequence.getObjectAt(0));
        this.crlSeries = CrlSeries.getInstance((Object) aSN1Sequence.getObjectAt(1));
    }

    public static MissingCrlIdentifier getInstance(Object obj) {
        if (obj instanceof MissingCrlIdentifier) {
            return (MissingCrlIdentifier) obj;
        }
        if (obj != null) {
            return new MissingCrlIdentifier(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.cracaId, this.crlSeries);
    }

    public HashedId3 getCracaId() {
        return this.cracaId;
    }

    public CrlSeries getCrlSeries() {
        return this.crlSeries;
    }

    public static Builder builder() {
        return new Builder();
    }
}
