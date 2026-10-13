package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/cmp/CertificateConfirmationContent.class */
public class CertificateConfirmationContent {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertConfirmContent content;

    public CertificateConfirmationContent(CertConfirmContent certConfirmContent) {
        this(certConfirmContent, new DefaultDigestAlgorithmIdentifierFinder());
    }

    public CertificateConfirmationContent(CertConfirmContent certConfirmContent, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        this.digestAlgFinder = digestAlgorithmIdentifierFinder;
        this.content = certConfirmContent;
    }

    public static CertificateConfirmationContent fromPKIBody(PKIBody pKIBody) {
        return fromPKIBody(pKIBody, new DefaultDigestAlgorithmIdentifierFinder());
    }

    public static CertificateConfirmationContent fromPKIBody(PKIBody pKIBody, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        if (isCertificateConfirmationContent(pKIBody.getType())) {
            return new CertificateConfirmationContent(CertConfirmContent.getInstance(pKIBody.getContent()), digestAlgorithmIdentifierFinder);
        }
        throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
    }

    public static boolean isCertificateConfirmationContent(int i) {
        switch (i) {
            case 24:
                return true;
            default:
                return false;
        }
    }

    public CertConfirmContent toASN1Structure() {
        return this.content;
    }

    public CertificateStatus[] getStatusMessages() {
        CertStatus[] certStatusArray = this.content.toCertStatusArray();
        CertificateStatus[] certificateStatusArr = new CertificateStatus[certStatusArray.length];
        for (int i = 0; i != certificateStatusArr.length; i++) {
            certificateStatusArr[i] = new CertificateStatus(this.digestAlgFinder, certStatusArray[i]);
        }
        return certificateStatusArr;
    }
}
