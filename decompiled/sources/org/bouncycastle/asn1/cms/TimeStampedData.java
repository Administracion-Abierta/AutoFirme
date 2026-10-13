package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/TimeStampedData.class */
public class TimeStampedData extends ASN1Object {
    private ASN1Integer version;
    private ASN1IA5String dataUri;
    private MetaData metaData;
    private ASN1OctetString content;
    private Evidence temporalEvidence;

    public TimeStampedData(ASN1IA5String aSN1IA5String, MetaData metaData, ASN1OctetString aSN1OctetString, Evidence evidence) {
        this.version = new ASN1Integer(1L);
        this.dataUri = aSN1IA5String;
        this.metaData = metaData;
        this.content = aSN1OctetString;
        this.temporalEvidence = evidence;
    }

    private TimeStampedData(ASN1Sequence aSN1Sequence) {
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        int i = 1;
        if (aSN1Sequence.getObjectAt(1) instanceof ASN1IA5String) {
            i = 1 + 1;
            this.dataUri = ASN1IA5String.getInstance(aSN1Sequence.getObjectAt(1));
        }
        if ((aSN1Sequence.getObjectAt(i) instanceof MetaData) || (aSN1Sequence.getObjectAt(i) instanceof ASN1Sequence)) {
            int i2 = i;
            i++;
            this.metaData = MetaData.getInstance(aSN1Sequence.getObjectAt(i2));
        }
        if (aSN1Sequence.getObjectAt(i) instanceof ASN1OctetString) {
            int i3 = i;
            i++;
            this.content = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(i3));
        }
        this.temporalEvidence = Evidence.getInstance(aSN1Sequence.getObjectAt(i));
    }

    public static TimeStampedData getInstance(Object obj) {
        return (obj == null || (obj instanceof TimeStampedData)) ? (TimeStampedData) obj : new TimeStampedData(ASN1Sequence.getInstance(obj));
    }

    public DERIA5String getDataUri() {
        return (null == this.dataUri || (this.dataUri instanceof DERIA5String)) ? (DERIA5String) this.dataUri : new DERIA5String(this.dataUri.getString(), false);
    }

    public ASN1IA5String getDataUriIA5() {
        return this.dataUri;
    }

    public MetaData getMetaData() {
        return this.metaData;
    }

    public ASN1OctetString getContent() {
        return this.content;
    }

    public Evidence getTemporalEvidence() {
        return this.temporalEvidence;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(5);
        aSN1EncodableVector.add(this.version);
        if (this.dataUri != null) {
            aSN1EncodableVector.add(this.dataUri);
        }
        if (this.metaData != null) {
            aSN1EncodableVector.add(this.metaData);
        }
        if (this.content != null) {
            aSN1EncodableVector.add(this.content);
        }
        aSN1EncodableVector.add(this.temporalEvidence);
        return new BERSequence(aSN1EncodableVector);
    }
}
