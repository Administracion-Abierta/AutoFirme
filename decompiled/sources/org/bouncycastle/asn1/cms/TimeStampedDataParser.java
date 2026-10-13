package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/TimeStampedDataParser.class */
public class TimeStampedDataParser {
    private ASN1Integer version;
    private ASN1IA5String dataUri;
    private MetaData metaData;
    private ASN1OctetStringParser content;
    private Evidence temporalEvidence;
    private ASN1SequenceParser parser;

    private TimeStampedDataParser(ASN1SequenceParser aSN1SequenceParser) throws IOException {
        this.parser = aSN1SequenceParser;
        this.version = ASN1Integer.getInstance(aSN1SequenceParser.readObject());
        ASN1Encodable object = aSN1SequenceParser.readObject();
        if (object instanceof ASN1IA5String) {
            this.dataUri = ASN1IA5String.getInstance(object);
            object = aSN1SequenceParser.readObject();
        }
        if ((object instanceof MetaData) || (object instanceof ASN1SequenceParser)) {
            this.metaData = MetaData.getInstance(object.toASN1Primitive());
            object = aSN1SequenceParser.readObject();
        }
        if (object instanceof ASN1OctetStringParser) {
            this.content = (ASN1OctetStringParser) object;
        }
    }

    public static TimeStampedDataParser getInstance(Object obj) throws IOException {
        if (obj instanceof ASN1Sequence) {
            return new TimeStampedDataParser(((ASN1Sequence) obj).parser());
        }
        if (obj instanceof ASN1SequenceParser) {
            return new TimeStampedDataParser((ASN1SequenceParser) obj);
        }
        return null;
    }

    public int getVersion() {
        return this.version.getValue().intValue();
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

    public ASN1OctetStringParser getContent() {
        return this.content;
    }

    public Evidence getTemporalEvidence() throws IOException {
        if (this.temporalEvidence == null) {
            this.temporalEvidence = Evidence.getInstance(this.parser.readObject().toASN1Primitive());
        }
        return this.temporalEvidence;
    }
}
