package org.spongycastle.cert.crmf;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.spongycastle.asn1.crmf.EncryptedKey;
import org.spongycastle.asn1.crmf.PKIArchiveOptions;
import org.spongycastle.cms.CMSEnvelopedData;
import org.spongycastle.cms.CMSException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/PKIArchiveControl.class */
public class PKIArchiveControl implements Control {
    public static final int encryptedPrivKey = 0;
    public static final int keyGenParameters = 1;
    public static final int archiveRemGenPrivKey = 2;
    private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions;
    private final PKIArchiveOptions pkiArchiveOptions;

    public PKIArchiveControl(PKIArchiveOptions pkiArchiveOptions) {
        this.pkiArchiveOptions = pkiArchiveOptions;
    }

    @Override // org.spongycastle.cert.crmf.Control
    public ASN1ObjectIdentifier getType() {
        return type;
    }

    @Override // org.spongycastle.cert.crmf.Control
    public ASN1Encodable getValue() {
        return this.pkiArchiveOptions;
    }

    public int getArchiveType() {
        return this.pkiArchiveOptions.getType();
    }

    public boolean isEnvelopedData() {
        EncryptedKey encKey = EncryptedKey.getInstance(this.pkiArchiveOptions.getValue());
        return !encKey.isEncryptedValue();
    }

    public CMSEnvelopedData getEnvelopedData() throws CRMFException {
        try {
            EncryptedKey encKey = EncryptedKey.getInstance(this.pkiArchiveOptions.getValue());
            EnvelopedData data = EnvelopedData.getInstance(encKey.getValue());
            return new CMSEnvelopedData(new ContentInfo(CMSObjectIdentifiers.envelopedData, data));
        } catch (CMSException e) {
            throw new CRMFException("CMS parsing error: " + e.getMessage(), e.getCause());
        } catch (Exception e2) {
            throw new CRMFException("CRMF parsing error: " + e2.getMessage(), e2);
        }
    }
}
