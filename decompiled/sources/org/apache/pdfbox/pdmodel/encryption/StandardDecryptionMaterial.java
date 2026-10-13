package org.apache.pdfbox.pdmodel.encryption;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/StandardDecryptionMaterial.class */
public class StandardDecryptionMaterial extends DecryptionMaterial {
    private final String password;

    public StandardDecryptionMaterial(String pwd) {
        this.password = pwd;
    }

    public String getPassword() {
        return this.password;
    }
}
