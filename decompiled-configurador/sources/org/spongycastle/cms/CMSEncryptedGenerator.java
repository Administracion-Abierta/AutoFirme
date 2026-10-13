package org.spongycastle.cms;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSEncryptedGenerator.class */
public class CMSEncryptedGenerator {
    protected CMSAttributeTableGenerator unprotectedAttributeGenerator = null;

    protected CMSEncryptedGenerator() {
    }

    public void setUnprotectedAttributeGenerator(CMSAttributeTableGenerator unprotectedAttributeGenerator) {
        this.unprotectedAttributeGenerator = unprotectedAttributeGenerator;
    }
}
