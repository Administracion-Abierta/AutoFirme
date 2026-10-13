package org.spongycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.Evidence;
import org.spongycastle.asn1.cms.TimeStampAndCRL;
import org.spongycastle.asn1.cms.TimeStampTokenEvidence;
import org.spongycastle.asn1.cms.TimeStampedData;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/cms/CMSTimeStampedData.class */
public class CMSTimeStampedData {
    private TimeStampedData timeStampedData;
    private ContentInfo contentInfo;
    private TimeStampDataUtil util;

    public CMSTimeStampedData(ContentInfo contentInfo) {
        initialize(contentInfo);
    }

    public CMSTimeStampedData(InputStream in) throws IOException {
        try {
            initialize(ContentInfo.getInstance(new ASN1InputStream(in).readObject()));
        } catch (ClassCastException e) {
            throw new IOException("Malformed content: " + e);
        } catch (IllegalArgumentException e2) {
            throw new IOException("Malformed content: " + e2);
        }
    }

    public CMSTimeStampedData(byte[] baseData) throws IOException {
        this(new ByteArrayInputStream(baseData));
    }

    private void initialize(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        if (CMSObjectIdentifiers.timestampedData.equals(contentInfo.getContentType())) {
            this.timeStampedData = TimeStampedData.getInstance(contentInfo.getContent());
            this.util = new TimeStampDataUtil(this.timeStampedData);
            return;
        }
        throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
    }

    public byte[] calculateNextHash(DigestCalculator calculator) throws CMSException {
        return this.util.calculateNextHash(calculator);
    }

    public CMSTimeStampedData addTimeStamp(TimeStampToken token) throws CMSException {
        TimeStampAndCRL[] timeStamps = this.util.getTimeStamps();
        TimeStampAndCRL[] newTimeStamps = new TimeStampAndCRL[timeStamps.length + 1];
        System.arraycopy(timeStamps, 0, newTimeStamps, 0, timeStamps.length);
        newTimeStamps[timeStamps.length] = new TimeStampAndCRL(token.toCMSSignedData().toASN1Structure());
        return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, new TimeStampedData(this.timeStampedData.getDataUri(), this.timeStampedData.getMetaData(), this.timeStampedData.getContent(), new Evidence(new TimeStampTokenEvidence(newTimeStamps)))));
    }

    public byte[] getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctets();
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

    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        return this.util.getTimeStampTokens();
    }

    public void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(calculator);
    }

    public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider calculatorProvider) throws OperatorCreationException {
        return this.util.getMessageImprintDigestCalculator(calculatorProvider);
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(calculatorProvider, dataDigest);
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(calculatorProvider, dataDigest, timeStampToken);
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
