package org.bouncycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEncryptedGenerator.class */
public class CMSEncryptedGenerator {
    protected CMSAttributeTableGenerator unprotectedAttributeGenerator = null;

    protected CMSEncryptedGenerator() {
    }

    public void setUnprotectedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.unprotectedAttributeGenerator = cMSAttributeTableGenerator;
    }
}
