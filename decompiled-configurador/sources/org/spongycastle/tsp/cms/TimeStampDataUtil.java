package org.spongycastle.tsp.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.Evidence;
import org.spongycastle.asn1.cms.TimeStampAndCRL;
import org.spongycastle.asn1.cms.TimeStampedData;
import org.spongycastle.asn1.cms.TimeStampedDataParser;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.tsp.TSPException;
import org.spongycastle.tsp.TimeStampToken;
import org.spongycastle.tsp.TimeStampTokenInfo;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/cms/TimeStampDataUtil.class */
class TimeStampDataUtil {
    private final TimeStampAndCRL[] timeStamps;
    private final MetaDataUtil metaDataUtil;

    TimeStampDataUtil(TimeStampedData timeStampedData) {
        this.metaDataUtil = new MetaDataUtil(timeStampedData.getMetaData());
        Evidence evidence = timeStampedData.getTemporalEvidence();
        this.timeStamps = evidence.getTstEvidence().toTimeStampAndCRLArray();
    }

    TimeStampDataUtil(TimeStampedDataParser timeStampedData) throws IOException {
        this.metaDataUtil = new MetaDataUtil(timeStampedData.getMetaData());
        Evidence evidence = timeStampedData.getTemporalEvidence();
        this.timeStamps = evidence.getTstEvidence().toTimeStampAndCRLArray();
    }

    TimeStampToken getTimeStampToken(TimeStampAndCRL timeStampAndCRL) throws CMSException {
        ContentInfo timeStampToken = timeStampAndCRL.getTimeStampToken();
        try {
            TimeStampToken token = new TimeStampToken(timeStampToken);
            return token;
        } catch (IOException e) {
            throw new CMSException("unable to parse token data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("token data invalid: " + e2.getMessage(), e2);
        } catch (TSPException e3) {
            if (e3.getCause() instanceof CMSException) {
                throw ((CMSException) e3.getCause());
            }
            throw new CMSException("token data invalid: " + e3.getMessage(), e3);
        }
    }

    void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        this.metaDataUtil.initialiseMessageImprintDigestCalculator(calculator);
    }

    DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider calculatorProvider) throws OperatorCreationException {
        try {
            TimeStampToken token = getTimeStampToken(this.timeStamps[0]);
            TimeStampTokenInfo info = token.getTimeStampInfo();
            ASN1ObjectIdentifier algOID = info.getMessageImprintAlgOID();
            DigestCalculator calc = calculatorProvider.get(new AlgorithmIdentifier(algOID));
            initialiseMessageImprintDigestCalculator(calc);
            return calc;
        } catch (CMSException e) {
            throw new OperatorCreationException("unable to extract algorithm ID: " + e.getMessage(), e);
        }
    }

    TimeStampToken[] getTimeStampTokens() throws CMSException {
        TimeStampToken[] tokens = new TimeStampToken[this.timeStamps.length];
        for (int i = 0; i < this.timeStamps.length; i++) {
            tokens[i] = getTimeStampToken(this.timeStamps[i]);
        }
        return tokens;
    }

    TimeStampAndCRL[] getTimeStamps() {
        return this.timeStamps;
    }

    byte[] calculateNextHash(DigestCalculator calculator) throws CMSException {
        TimeStampAndCRL tspToken = this.timeStamps[this.timeStamps.length - 1];
        OutputStream out = calculator.getOutputStream();
        try {
            out.write(tspToken.getEncoded(ASN1Encoding.DER));
            out.close();
            return calculator.getDigest();
        } catch (IOException e) {
            throw new CMSException("exception calculating hash: " + e.getMessage(), e);
        }
    }

    void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest) throws ImprintDigestInvalidException, CMSException {
        byte[] currentDigest = dataDigest;
        for (int i = 0; i < this.timeStamps.length; i++) {
            try {
                TimeStampToken token = getTimeStampToken(this.timeStamps[i]);
                if (i > 0) {
                    TimeStampTokenInfo info = token.getTimeStampInfo();
                    DigestCalculator calculator = calculatorProvider.get(info.getHashAlgorithm());
                    calculator.getOutputStream().write(this.timeStamps[i - 1].getEncoded(ASN1Encoding.DER));
                    currentDigest = calculator.getDigest();
                }
                compareDigest(token, currentDigest);
            } catch (IOException e) {
                throw new CMSException("exception calculating hash: " + e.getMessage(), e);
            } catch (OperatorCreationException e2) {
                throw new CMSException("cannot create digest: " + e2.getMessage(), e2);
            }
        }
    }

    void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        byte[] currentDigest = dataDigest;
        try {
            byte[] encToken = timeStampToken.getEncoded();
            for (int i = 0; i < this.timeStamps.length; i++) {
                try {
                    TimeStampToken token = getTimeStampToken(this.timeStamps[i]);
                    if (i > 0) {
                        TimeStampTokenInfo info = token.getTimeStampInfo();
                        DigestCalculator calculator = calculatorProvider.get(info.getHashAlgorithm());
                        calculator.getOutputStream().write(this.timeStamps[i - 1].getEncoded(ASN1Encoding.DER));
                        currentDigest = calculator.getDigest();
                    }
                    compareDigest(token, currentDigest);
                    if (Arrays.areEqual(token.getEncoded(), encToken)) {
                        return;
                    }
                } catch (IOException e) {
                    throw new CMSException("exception calculating hash: " + e.getMessage(), e);
                } catch (OperatorCreationException e2) {
                    throw new CMSException("cannot create digest: " + e2.getMessage(), e2);
                }
            }
            throw new ImprintDigestInvalidException("passed in token not associated with timestamps present", timeStampToken);
        } catch (IOException e3) {
            throw new CMSException("exception encoding timeStampToken: " + e3.getMessage(), e3);
        }
    }

    private void compareDigest(TimeStampToken timeStampToken, byte[] digest) throws ImprintDigestInvalidException {
        TimeStampTokenInfo info = timeStampToken.getTimeStampInfo();
        byte[] tsrMessageDigest = info.getMessageImprintDigest();
        if (!Arrays.areEqual(digest, tsrMessageDigest)) {
            throw new ImprintDigestInvalidException("hash calculated is different from MessageImprintDigest found in TimeStampToken", timeStampToken);
        }
    }

    String getFileName() {
        return this.metaDataUtil.getFileName();
    }

    String getMediaType() {
        return this.metaDataUtil.getMediaType();
    }

    AttributeTable getOtherMetaData() {
        return new AttributeTable(this.metaDataUtil.getOtherMetaData());
    }
}
