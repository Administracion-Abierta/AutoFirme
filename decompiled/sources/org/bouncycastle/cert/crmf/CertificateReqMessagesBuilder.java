package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/CertificateReqMessagesBuilder.class */
public class CertificateReqMessagesBuilder {
    private final List<CertReqMsg> requests = new ArrayList();

    public void addRequest(CertificateRequestMessage certificateRequestMessage) {
        this.requests.add(certificateRequestMessage.toASN1Structure());
    }

    public CertificateReqMessages build() {
        CertificateReqMessages certificateReqMessages = new CertificateReqMessages(new CertReqMessages((CertReqMsg[]) this.requests.toArray(new CertReqMsg[0])));
        this.requests.clear();
        return certificateReqMessages;
    }
}
