package org.spongycastle.cert.crmf;

import java.io.IOException;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.spongycastle.asn1.crmf.EncKeyWithID;
import org.spongycastle.asn1.crmf.EncryptedKey;
import org.spongycastle.asn1.crmf.PKIArchiveOptions;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.cms.CMSEnvelopedData;
import org.spongycastle.cms.CMSEnvelopedDataGenerator;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.RecipientInfoGenerator;
import org.spongycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/PKIArchiveControlBuilder.class */
public class PKIArchiveControlBuilder {
    private CMSEnvelopedDataGenerator envGen;
    private CMSProcessableByteArray keyContent;

    public PKIArchiveControlBuilder(PrivateKeyInfo privateKeyInfo, GeneralName generalName) {
        EncKeyWithID encKeyWithID = new EncKeyWithID(privateKeyInfo, generalName);
        try {
            this.keyContent = new CMSProcessableByteArray(CRMFObjectIdentifiers.id_ct_encKeyWithID, encKeyWithID.getEncoded());
            this.envGen = new CMSEnvelopedDataGenerator();
        } catch (IOException e) {
            throw new IllegalStateException("unable to encode key and general name info");
        }
    }

    public PKIArchiveControlBuilder addRecipientGenerator(RecipientInfoGenerator recipientGen) {
        this.envGen.addRecipientInfoGenerator(recipientGen);
        return this;
    }

    public PKIArchiveControl build(OutputEncryptor contentEncryptor) throws CMSException {
        CMSEnvelopedData envContent = this.envGen.generate(this.keyContent, contentEncryptor);
        EnvelopedData envD = EnvelopedData.getInstance(envContent.toASN1Structure().getContent());
        return new PKIArchiveControl(new PKIArchiveOptions(new EncryptedKey(envD)));
    }
}
