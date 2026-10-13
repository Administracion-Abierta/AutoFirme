package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/CertificateReqMessages.class */
public class CertificateReqMessages {
    private final CertReqMsg[] reqs;

    public CertificateReqMessages(CertReqMessages certReqMessages) {
        this.reqs = certReqMessages.toCertReqMsgArray();
    }

    public static CertificateReqMessages fromPKIBody(PKIBody pKIBody) {
        if (isCertificateRequestMessages(pKIBody.getType())) {
            return new CertificateReqMessages(CertReqMessages.getInstance(pKIBody.getContent()));
        }
        throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
    }

    public static boolean isCertificateRequestMessages(int i) {
        switch (i) {
            case 0:
            case 2:
            case 7:
            case 9:
            case 13:
                return true;
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            case 10:
            case 11:
            case 12:
            default:
                return false;
        }
    }

    public CertificateRequestMessage[] getRequests() {
        CertificateRequestMessage[] certificateRequestMessageArr = new CertificateRequestMessage[this.reqs.length];
        for (int i = 0; i != certificateRequestMessageArr.length; i++) {
            certificateRequestMessageArr[i] = new CertificateRequestMessage(this.reqs[i]);
        }
        return certificateRequestMessageArr;
    }

    public CertReqMessages toASN1Structure() {
        return new CertReqMessages(this.reqs);
    }
}
