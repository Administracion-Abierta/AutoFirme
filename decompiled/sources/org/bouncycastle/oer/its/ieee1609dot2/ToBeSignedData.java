package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/ToBeSignedData.class */
public class ToBeSignedData extends ASN1Object {
    private final SignedDataPayload payload;
    private final HeaderInfo headerInfo;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/ToBeSignedData$Builder.class */
    public static class Builder {
        private SignedDataPayload payload;
        private HeaderInfo headerInfo;

        public Builder setPayload(SignedDataPayload signedDataPayload) {
            this.payload = signedDataPayload;
            return this;
        }

        public Builder setHeaderInfo(HeaderInfo headerInfo) {
            this.headerInfo = headerInfo;
            return this;
        }

        public ToBeSignedData createToBeSignedData() {
            return new ToBeSignedData(this.payload, this.headerInfo);
        }
    }

    public ToBeSignedData(SignedDataPayload signedDataPayload, HeaderInfo headerInfo) {
        this.payload = signedDataPayload;
        this.headerInfo = headerInfo;
    }

    private ToBeSignedData(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.payload = SignedDataPayload.getInstance(aSN1Sequence.getObjectAt(0));
        this.headerInfo = HeaderInfo.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static ToBeSignedData getInstance(Object obj) {
        if (obj instanceof ToBeSignedData) {
            return (ToBeSignedData) obj;
        }
        if (obj != null) {
            return new ToBeSignedData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public SignedDataPayload getPayload() {
        return this.payload;
    }

    public HeaderInfo getHeaderInfo() {
        return this.headerInfo;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.payload, this.headerInfo});
    }

    public static Builder builder() {
        return new Builder();
    }
}
