package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/CertificateResponse.class */
public class CertificateResponse {
    private final CertResponse certResponse;

    public CertificateResponse(CertResponse certResponse) {
        this.certResponse = certResponse;
    }

    public boolean hasEncryptedCertificate() {
        return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().hasEncryptedCertificate();
    }

    public CMSEnvelopedData getEncryptedCertificate() throws CMSException {
        if (!hasEncryptedCertificate()) {
            throw new IllegalStateException("encrypted certificate asked for, none found");
        }
        CMSEnvelopedData cMSEnvelopedData = new CMSEnvelopedData(new ContentInfo(PKCSObjectIdentifiers.envelopedData, this.certResponse.getCertifiedKeyPair().getCertOrEncCert().getEncryptedCert().getValue()));
        if (cMSEnvelopedData.getRecipientInfos().size() != 1) {
            throw new IllegalStateException("data encrypted for more than one recipient");
        }
        return cMSEnvelopedData;
    }

    public CMPCertificate getCertificate(Recipient recipient) throws CMSException {
        return CMPCertificate.getInstance(getEncryptedCertificate().getRecipientInfos().getRecipients().iterator().next().getContent(recipient));
    }

    public CMPCertificate getCertificate() throws CMSException {
        if (hasEncryptedCertificate()) {
            throw new IllegalStateException("plaintext certificate asked for, none found");
        }
        return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
    }

    public CertResponse toASN1Structure() {
        return this.certResponse;
    }
}
