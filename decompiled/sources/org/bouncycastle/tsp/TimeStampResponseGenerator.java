package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/TimeStampResponseGenerator.class */
public class TimeStampResponseGenerator {
    int status;
    ASN1EncodableVector statusStrings;
    int failInfo;
    private TimeStampTokenGenerator tokenGenerator;
    private Set acceptedAlgorithms;
    private Set acceptedPolicies;
    private Set acceptedExtensions;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/TimeStampResponseGenerator$FailInfo.class */
    static class FailInfo extends DERBitString {
        FailInfo(int i) {
            super(getBytes(i), getPadBits(i));
        }
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator timeStampTokenGenerator, Set set) {
        this(timeStampTokenGenerator, set, null, null);
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator timeStampTokenGenerator, Set set, Set set2) {
        this(timeStampTokenGenerator, set, set2, null);
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator timeStampTokenGenerator, Set set, Set set2, Set set3) {
        this.tokenGenerator = timeStampTokenGenerator;
        this.acceptedAlgorithms = convert(set);
        this.acceptedPolicies = convert(set2);
        this.acceptedExtensions = convert(set3);
        this.statusStrings = new ASN1EncodableVector();
    }

    private void addStatusString(String str) {
        this.statusStrings.add(new DERUTF8String(str));
    }

    private void setFailInfoField(int i) {
        this.failInfo |= i;
    }

    private PKIStatusInfo getPKIStatusInfo() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.status));
        if (this.statusStrings.size() > 0) {
            aSN1EncodableVector.add(PKIFreeText.getInstance(new DERSequence(this.statusStrings)));
        }
        if (this.failInfo != 0) {
            aSN1EncodableVector.add(new FailInfo(this.failInfo));
        }
        return PKIStatusInfo.getInstance(new DERSequence(aSN1EncodableVector));
    }

    public TimeStampResponse generate(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date) throws TSPException {
        try {
            return generateGrantedResponse(timeStampRequest, bigInteger, date, "Operation Okay");
        } catch (Exception e) {
            return generateRejectedResponse(e);
        }
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date) throws TSPException {
        return generateGrantedResponse(timeStampRequest, bigInteger, date, null);
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date, String str) throws TSPException {
        return generateGrantedResponse(timeStampRequest, bigInteger, date, str, null);
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date, String str, Extensions extensions) throws TSPException {
        if (date == null) {
            throw new TSPValidationException("The time source is not available.", 512);
        }
        timeStampRequest.validate(this.acceptedAlgorithms, this.acceptedPolicies, this.acceptedExtensions);
        this.status = 0;
        this.statusStrings = new ASN1EncodableVector();
        if (str != null) {
            addStatusString(str);
        }
        try {
            try {
                return new TimeStampResponse(new DLSequence(new ASN1Encodable[]{getPKIStatusInfo().toASN1Primitive(), this.tokenGenerator.generate(timeStampRequest, bigInteger, date, extensions).toCMSSignedData().toASN1Structure().toASN1Primitive()}));
            } catch (IOException e) {
                throw new TSPException("created badly formatted response!");
            }
        } catch (TSPException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new TSPException("Timestamp token received cannot be converted to ContentInfo", e3);
        }
    }

    public TimeStampResponse generateRejectedResponse(Exception exc) throws TSPException {
        return exc instanceof TSPValidationException ? generateFailResponse(2, ((TSPValidationException) exc).getFailureCode(), exc.getMessage()) : generateFailResponse(2, 1073741824, exc.getMessage());
    }

    public TimeStampResponse generateFailResponse(int i, int i2, String str) throws TSPException {
        this.status = i;
        this.statusStrings = new ASN1EncodableVector();
        setFailInfoField(i2);
        if (str != null) {
            addStatusString(str);
        }
        try {
            return new TimeStampResponse(new TimeStampResp(getPKIStatusInfo(), null));
        } catch (IOException e) {
            throw new TSPException("created badly formatted response!");
        }
    }

    private Set convert(Set set) {
        if (set == null) {
            return set;
        }
        HashSet hashSet = new HashSet(set.size());
        for (Object obj : set) {
            if (obj instanceof String) {
                hashSet.add(new ASN1ObjectIdentifier((String) obj));
            } else {
                hashSet.add(obj);
            }
        }
        return hashSet;
    }
}
