package org.spongycastle.tsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.spongycastle.asn1.cmp.PKIFreeText;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.tsp.TimeStampResp;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/TimeStampResponse.class */
public class TimeStampResponse {
    TimeStampResp resp;
    TimeStampToken timeStampToken;

    public TimeStampResponse(TimeStampResp resp) throws TSPException, IOException {
        this.resp = resp;
        if (resp.getTimeStampToken() != null) {
            this.timeStampToken = new TimeStampToken(resp.getTimeStampToken());
        }
    }

    public TimeStampResponse(byte[] resp) throws TSPException, IOException {
        this(new ByteArrayInputStream(resp));
    }

    public TimeStampResponse(InputStream in) throws TSPException, IOException {
        this(readTimeStampResp(in));
    }

    TimeStampResponse(DLSequence dlSequence) throws TSPException, IOException {
        try {
            this.resp = TimeStampResp.getInstance(dlSequence);
            this.timeStampToken = new TimeStampToken(ContentInfo.getInstance(dlSequence.getObjectAt(1)));
        } catch (ClassCastException e) {
            throw new TSPException("malformed timestamp response: " + e, e);
        } catch (IllegalArgumentException e2) {
            throw new TSPException("malformed timestamp response: " + e2, e2);
        }
    }

    private static TimeStampResp readTimeStampResp(InputStream in) throws TSPException, IOException {
        try {
            return TimeStampResp.getInstance(new ASN1InputStream(in).readObject());
        } catch (ClassCastException e) {
            throw new TSPException("malformed timestamp response: " + e, e);
        } catch (IllegalArgumentException e2) {
            throw new TSPException("malformed timestamp response: " + e2, e2);
        }
    }

    public int getStatus() {
        return this.resp.getStatus().getStatus().intValue();
    }

    public String getStatusString() {
        if (this.resp.getStatus().getStatusString() != null) {
            StringBuffer statusStringBuf = new StringBuffer();
            PKIFreeText text = this.resp.getStatus().getStatusString();
            for (int i = 0; i != text.size(); i++) {
                statusStringBuf.append(text.getStringAt(i).getString());
            }
            return statusStringBuf.toString();
        }
        return null;
    }

    public PKIFailureInfo getFailInfo() {
        if (this.resp.getStatus().getFailInfo() != null) {
            return new PKIFailureInfo(this.resp.getStatus().getFailInfo());
        }
        return null;
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    public void validate(TimeStampRequest request) throws TSPException {
        TimeStampToken tok = getTimeStampToken();
        if (tok != null) {
            TimeStampTokenInfo tstInfo = tok.getTimeStampInfo();
            if (request.getNonce() != null && !request.getNonce().equals(tstInfo.getNonce())) {
                throw new TSPValidationException("response contains wrong nonce value.");
            }
            if (getStatus() != 0 && getStatus() != 1) {
                throw new TSPValidationException("time stamp token found in failed request.");
            }
            if (!Arrays.constantTimeAreEqual(request.getMessageImprintDigest(), tstInfo.getMessageImprintDigest())) {
                throw new TSPValidationException("response for different message imprint digest.");
            }
            if (!tstInfo.getMessageImprintAlgOID().equals(request.getMessageImprintAlgOID())) {
                throw new TSPValidationException("response for different message imprint algorithm.");
            }
            Attribute scV1 = tok.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
            Attribute scV2 = tok.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
            if (scV1 == null && scV2 == null) {
                throw new TSPValidationException("no signing certificate attribute present.");
            }
            if (scV1 == null || scV2 != null) {
            }
            if (request.getReqPolicy() != null && !request.getReqPolicy().equals(tstInfo.getPolicy())) {
                throw new TSPValidationException("TSA policy wrong for request.");
            }
            return;
        }
        if (getStatus() == 0 || getStatus() == 1) {
            throw new TSPValidationException("no time stamp token found and one expected.");
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        if (ASN1Encoding.DL.equals(encoding)) {
            return new DLSequence(new ASN1Encodable[]{this.resp.getStatus(), this.timeStampToken.toCMSSignedData().toASN1Structure()}).getEncoded(encoding);
        }
        return this.resp.getEncoded(encoding);
    }
}
