package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/CertificateRepMessage.class */
public class CertificateRepMessage {
    private final CertResponse[] resps;
    private final CMPCertificate[] caCerts;

    public CertificateRepMessage(CertRepMessage certRepMessage) {
        this.resps = certRepMessage.getResponse();
        this.caCerts = certRepMessage.getCaPubs();
    }

    public static CertificateRepMessage fromPKIBody(PKIBody pKIBody) {
        if (isCertificateRepMessage(pKIBody.getType())) {
            return new CertificateRepMessage(CertRepMessage.getInstance(pKIBody.getContent()));
        }
        throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
    }

    public static boolean isCertificateRepMessage(int i) {
        switch (i) {
            case 1:
            case 3:
            case 8:
            case 14:
                return true;
            default:
                return false;
        }
    }

    public CertificateResponse[] getResponses() {
        CertificateResponse[] certificateResponseArr = new CertificateResponse[this.resps.length];
        for (int i = 0; i != certificateResponseArr.length; i++) {
            certificateResponseArr[i] = new CertificateResponse(this.resps[i]);
        }
        return certificateResponseArr;
    }

    public X509CertificateHolder[] getX509Certificates() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i != this.caCerts.length; i++) {
            if (this.caCerts[i].isX509v3PKCert()) {
                arrayList.add(new X509CertificateHolder(this.caCerts[i].getX509v3PKCert()));
            }
        }
        return (X509CertificateHolder[]) arrayList.toArray(new X509CertificateHolder[0]);
    }

    public boolean isOnlyX509PKCertificates() {
        boolean zIsX509v3PKCert = true;
        for (int i = 0; i != this.caCerts.length; i++) {
            zIsX509v3PKCert &= this.caCerts[i].isX509v3PKCert();
        }
        return zIsX509v3PKCert;
    }

    public CMPCertificate[] getCMPCertificates() {
        CMPCertificate[] cMPCertificateArr = new CMPCertificate[this.caCerts.length];
        System.arraycopy(this.caCerts, 0, cMPCertificateArr, 0, cMPCertificateArr.length);
        return cMPCertificateArr;
    }

    public CertRepMessage toASN1Structure() {
        return new CertRepMessage(this.caCerts, this.resps);
    }
}
