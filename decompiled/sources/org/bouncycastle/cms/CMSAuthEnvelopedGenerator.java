package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthEnvelopedGenerator.class */
public class CMSAuthEnvelopedGenerator extends CMSEnvelopedGenerator {
    public static final String AES128_GCM = NISTObjectIdentifiers.id_aes128_GCM.getId();
    public static final String AES192_GCM = NISTObjectIdentifiers.id_aes192_GCM.getId();
    public static final String AES256_GCM = NISTObjectIdentifiers.id_aes256_GCM.getId();
    protected CMSAttributeTableGenerator authAttrsGenerator = null;
    protected CMSAttributeTableGenerator unauthAttrsGenerator = null;
    protected OriginatorInfo originatorInfo;

    protected CMSAuthEnvelopedGenerator() {
    }

    public void setAuthenticatedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.authAttrsGenerator = cMSAttributeTableGenerator;
    }

    public void setUnauthenticatedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.unauthAttrsGenerator = cMSAttributeTableGenerator;
    }

    @Override // org.bouncycastle.cms.CMSEnvelopedGenerator
    public void setOriginatorInfo(OriginatorInformation originatorInformation) {
        this.originatorInfo = originatorInformation.toASN1Structure();
    }

    @Override // org.bouncycastle.cms.CMSEnvelopedGenerator
    public void addRecipientInfoGenerator(RecipientInfoGenerator recipientInfoGenerator) {
        this.recipientInfoGenerators.add(recipientInfoGenerator);
    }
}
