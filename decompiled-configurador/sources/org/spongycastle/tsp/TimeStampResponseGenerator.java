package org.spongycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.asn1.cmp.PKIFreeText;
import org.spongycastle.asn1.cmp.PKIStatusInfo;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.tsp.TimeStampResp;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/TimeStampResponseGenerator.class */
public class TimeStampResponseGenerator {
    int status;
    ASN1EncodableVector statusStrings;
    int failInfo;
    private TimeStampTokenGenerator tokenGenerator;
    private Set acceptedAlgorithms;
    private Set acceptedPolicies;
    private Set acceptedExtensions;

    public TimeStampResponseGenerator(TimeStampTokenGenerator tokenGenerator, Set acceptedAlgorithms) {
        this(tokenGenerator, acceptedAlgorithms, null, null);
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator tokenGenerator, Set acceptedAlgorithms, Set acceptedPolicies) {
        this(tokenGenerator, acceptedAlgorithms, acceptedPolicies, null);
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator tokenGenerator, Set acceptedAlgorithms, Set acceptedPolicies, Set acceptedExtensions) {
        this.tokenGenerator = tokenGenerator;
        this.acceptedAlgorithms = convert(acceptedAlgorithms);
        this.acceptedPolicies = convert(acceptedPolicies);
        this.acceptedExtensions = convert(acceptedExtensions);
        this.statusStrings = new ASN1EncodableVector();
    }

    private void addStatusString(String statusString) {
        this.statusStrings.add(new DERUTF8String(statusString));
    }

    private void setFailInfoField(int field) {
        this.failInfo |= field;
    }

    private PKIStatusInfo getPKIStatusInfo() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.status));
        if (this.statusStrings.size() > 0) {
            v.add(PKIFreeText.getInstance(new DERSequence(this.statusStrings)));
        }
        if (this.failInfo != 0) {
            DERBitString failInfoBitString = new FailInfo(this.failInfo);
            v.add(failInfoBitString);
        }
        return PKIStatusInfo.getInstance(new DERSequence(v));
    }

    public TimeStampResponse generate(TimeStampRequest request, BigInteger serialNumber, Date genTime) throws TSPException {
        try {
            return generateGrantedResponse(request, serialNumber, genTime, "Operation Okay");
        } catch (Exception e) {
            return generateRejectedResponse(e);
        }
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest request, BigInteger serialNumber, Date genTime) throws TSPException {
        return generateGrantedResponse(request, serialNumber, genTime, null);
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest request, BigInteger serialNumber, Date genTime, String statusString) throws TSPException {
        return generateGrantedResponse(request, serialNumber, genTime, statusString, null);
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest request, BigInteger serialNumber, Date genTime, String statusString, Extensions additionalExtensions) throws TSPException {
        if (genTime == null) {
            throw new TSPValidationException("The time source is not available.", 512);
        }
        request.validate(this.acceptedAlgorithms, this.acceptedPolicies, this.acceptedExtensions);
        this.status = 0;
        this.statusStrings = new ASN1EncodableVector();
        if (statusString != null) {
            addStatusString(statusString);
        }
        PKIStatusInfo pkiStatusInfo = getPKIStatusInfo();
        try {
            ContentInfo tstTokenContentInfo = this.tokenGenerator.generate(request, serialNumber, genTime, additionalExtensions).toCMSSignedData().toASN1Structure();
            try {
                return new TimeStampResponse(new DLSequence(new ASN1Encodable[]{pkiStatusInfo.toASN1Primitive(), tstTokenContentInfo.toASN1Primitive()}));
            } catch (IOException e) {
                throw new TSPException("created badly formatted response!");
            }
        } catch (TSPException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new TSPException("Timestamp token received cannot be converted to ContentInfo", e3);
        }
    }

    public TimeStampResponse generateRejectedResponse(Exception exception) throws TSPException {
        if (exception instanceof TSPValidationException) {
            return generateFailResponse(2, ((TSPValidationException) exception).getFailureCode(), exception.getMessage());
        }
        return generateFailResponse(2, 1073741824, exception.getMessage());
    }

    public TimeStampResponse generateFailResponse(int status, int failInfoField, String statusString) throws TSPException {
        this.status = status;
        this.statusStrings = new ASN1EncodableVector();
        setFailInfoField(failInfoField);
        if (statusString != null) {
            addStatusString(statusString);
        }
        PKIStatusInfo pkiStatusInfo = getPKIStatusInfo();
        TimeStampResp resp = new TimeStampResp(pkiStatusInfo, null);
        try {
            return new TimeStampResponse(resp);
        } catch (IOException e) {
            throw new TSPException("created badly formatted response!");
        }
    }

    private Set convert(Set orig) {
        if (orig == null) {
            return orig;
        }
        Set con = new HashSet(orig.size());
        for (Object o : orig) {
            if (o instanceof String) {
                con.add(new ASN1ObjectIdentifier((String) o));
            } else {
                con.add(o);
            }
        }
        return con;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/TimeStampResponseGenerator$FailInfo.class */
    class FailInfo extends DERBitString {
        FailInfo(int failInfoValue) {
            super(getBytes(failInfoValue), getPadBits(failInfoValue));
        }
    }
}
