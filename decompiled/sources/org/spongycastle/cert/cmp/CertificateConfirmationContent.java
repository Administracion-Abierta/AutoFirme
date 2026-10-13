package org.spongycastle.cert.cmp;

import org.spongycastle.asn1.cmp.CertConfirmContent;
import org.spongycastle.asn1.cmp.CertStatus;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestAlgorithmIdentifierFinder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/cmp/CertificateConfirmationContent.class */
public class CertificateConfirmationContent {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertConfirmContent content;

    public CertificateConfirmationContent(CertConfirmContent content) {
        this(content, new DefaultDigestAlgorithmIdentifierFinder());
    }

    public CertificateConfirmationContent(CertConfirmContent content, DigestAlgorithmIdentifierFinder digestAlgFinder) {
        this.digestAlgFinder = digestAlgFinder;
        this.content = content;
    }

    public CertConfirmContent toASN1Structure() {
        return this.content;
    }

    public CertificateStatus[] getStatusMessages() {
        CertStatus[] statusArray = this.content.toCertStatusArray();
        CertificateStatus[] ret = new CertificateStatus[statusArray.length];
        for (int i = 0; i != ret.length; i++) {
            ret[i] = new CertificateStatus(this.digestAlgFinder, statusArray[i]);
        }
        return ret;
    }
}
