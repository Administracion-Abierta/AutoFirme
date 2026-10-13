package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/CertificateRepMessageBuilder.class */
public class CertificateRepMessageBuilder {
    private final List<CertResponse> responses = new ArrayList();
    private final CMPCertificate[] caCerts;

    public CertificateRepMessageBuilder(X509CertificateHolder... x509CertificateHolderArr) {
        this.caCerts = new CMPCertificate[x509CertificateHolderArr.length];
        for (int i = 0; i != x509CertificateHolderArr.length; i++) {
            this.caCerts[i] = new CMPCertificate(x509CertificateHolderArr[i].toASN1Structure());
        }
    }

    public CertificateRepMessageBuilder addCertificateResponse(CertificateResponse certificateResponse) {
        this.responses.add(certificateResponse.toASN1Structure());
        return this;
    }

    public CertificateRepMessage build() {
        CertRepMessage certRepMessage = this.caCerts.length != 0 ? new CertRepMessage(this.caCerts, (CertResponse[]) this.responses.toArray(new CertResponse[0])) : new CertRepMessage(null, (CertResponse[]) this.responses.toArray(new CertResponse[0]));
        this.responses.clear();
        return new CertificateRepMessage(certRepMessage);
    }
}
