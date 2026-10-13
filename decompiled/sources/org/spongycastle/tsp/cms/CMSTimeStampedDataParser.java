package org.spongycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfoParser;
import org.spongycastle.asn1.cms.TimeStampedDataParser;
import org.spongycastle.cms.CMSContentInfoParser;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.tsp.TimeStampToken;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/cms/CMSTimeStampedDataParser.class */
public class CMSTimeStampedDataParser extends CMSContentInfoParser {
    private TimeStampedDataParser timeStampedData;
    private TimeStampDataUtil util;

    public CMSTimeStampedDataParser(InputStream in) throws CMSException {
        super(in);
        initialize(this._contentInfo);
    }

    public CMSTimeStampedDataParser(byte[] baseData) throws CMSException {
        this(new ByteArrayInputStream(baseData));
    }

    private void initialize(ContentInfoParser contentInfo) throws CMSException {
        try {
            if (CMSObjectIdentifiers.timestampedData.equals(contentInfo.getContentType())) {
                this.timeStampedData = TimeStampedDataParser.getInstance(contentInfo.getContent(16));
                return;
            }
            throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
        } catch (IOException e) {
            throw new CMSException("parsing exception: " + e.getMessage(), e);
        }
    }

    public byte[] calculateNextHash(DigestCalculator calculator) throws CMSException {
        return this.util.calculateNextHash(calculator);
    }

    public InputStream getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctetStream();
        }
        return null;
    }

    public URI getDataUri() throws URISyntaxException {
        DERIA5String dataURI = this.timeStampedData.getDataUri();
        if (dataURI != null) {
            return new URI(dataURI.getString());
        }
        return null;
    }

    public String getFileName() {
        return this.util.getFileName();
    }

    public String getMediaType() {
        return this.util.getMediaType();
    }

    public AttributeTable getOtherMetaData() {
        return this.util.getOtherMetaData();
    }

    public void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(calculator);
    }

    public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider calculatorProvider) throws OperatorCreationException {
        try {
            parseTimeStamps();
            return this.util.getMessageImprintDigestCalculator(calculatorProvider);
        } catch (CMSException e) {
            throw new OperatorCreationException("unable to extract algorithm ID: " + e.getMessage(), e);
        }
    }

    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        parseTimeStamps();
        return this.util.getTimeStampTokens();
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest) throws ImprintDigestInvalidException, CMSException {
        parseTimeStamps();
        this.util.validate(calculatorProvider, dataDigest);
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        parseTimeStamps();
        this.util.validate(calculatorProvider, dataDigest, timeStampToken);
    }

    private void parseTimeStamps() throws CMSException {
        try {
            if (this.util == null) {
                InputStream cont = getContent();
                if (cont != null) {
                    Streams.drain(cont);
                }
                this.util = new TimeStampDataUtil(this.timeStampedData);
            }
        } catch (IOException e) {
            throw new CMSException("unable to parse evidence block: " + e.getMessage(), e);
        }
    }
}
