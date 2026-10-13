package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/cms/CMSTimeStampedData.class */
public class CMSTimeStampedData {
    private TimeStampedData timeStampedData;
    private ContentInfo contentInfo;
    private TimeStampDataUtil util;

    public CMSTimeStampedData(ContentInfo contentInfo) {
        initialize(contentInfo);
    }

    public CMSTimeStampedData(InputStream inputStream) throws IOException {
        try {
            initialize(ContentInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
        } catch (ClassCastException e) {
            throw new IOException("Malformed content: " + e);
        } catch (IllegalArgumentException e2) {
            throw new IOException("Malformed content: " + e2);
        }
    }

    public CMSTimeStampedData(byte[] bArr) throws IOException {
        this(new ByteArrayInputStream(bArr));
    }

    private void initialize(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        if (!CMSObjectIdentifiers.timestampedData.equals((ASN1Primitive) contentInfo.getContentType())) {
            throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
        }
        this.timeStampedData = TimeStampedData.getInstance(contentInfo.getContent());
        this.util = new TimeStampDataUtil(this.timeStampedData);
    }

    public byte[] calculateNextHash(DigestCalculator digestCalculator) throws CMSException {
        return this.util.calculateNextHash(digestCalculator);
    }

    public CMSTimeStampedData addTimeStamp(TimeStampToken timeStampToken) throws CMSException {
        TimeStampAndCRL[] timeStamps = this.util.getTimeStamps();
        TimeStampAndCRL[] timeStampAndCRLArr = new TimeStampAndCRL[timeStamps.length + 1];
        System.arraycopy(timeStamps, 0, timeStampAndCRLArr, 0, timeStamps.length);
        timeStampAndCRLArr[timeStamps.length] = new TimeStampAndCRL(timeStampToken.toCMSSignedData().toASN1Structure());
        return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, new TimeStampedData(this.timeStampedData.getDataUriIA5(), this.timeStampedData.getMetaData(), this.timeStampedData.getContent(), new Evidence(new TimeStampTokenEvidence(timeStampAndCRLArr)))));
    }

    public byte[] getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctets();
        }
        return null;
    }

    public URI getDataUri() throws URISyntaxException {
        ASN1IA5String dataUriIA5 = this.timeStampedData.getDataUriIA5();
        if (dataUriIA5 != null) {
            return new URI(dataUriIA5.getString());
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

    public void initialiseMessageImprintDigestCalculator(DigestCalculator digestCalculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(digestCalculator);
    }

    public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider digestCalculatorProvider) throws OperatorCreationException {
        return this.util.getMessageImprintDigestCalculator(digestCalculatorProvider);
    }

    public void validate(DigestCalculatorProvider digestCalculatorProvider, byte[] bArr) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(digestCalculatorProvider, bArr);
    }

    public void validate(DigestCalculatorProvider digestCalculatorProvider, byte[] bArr, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(digestCalculatorProvider, bArr, timeStampToken);
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
