package org.apache.pdfbox.pdmodel.encryption;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/ProtectionPolicy.class */
public abstract class ProtectionPolicy {
    private static final short DEFAULT_KEY_LENGTH = 40;
    private short encryptionKeyLength = 40;
    private boolean preferAES = false;

    public void setEncryptionKeyLength(int l) {
        if (l != 40 && l != 128 && l != 256) {
            throw new IllegalArgumentException("Invalid key length '" + l + "' value must be 40, 128 or 256!");
        }
        this.encryptionKeyLength = (short) l;
    }

    public int getEncryptionKeyLength() {
        return this.encryptionKeyLength;
    }

    public boolean isPreferAES() {
        return this.preferAES;
    }

    public void setPreferAES(boolean preferAES) {
        this.preferAES = preferAES;
    }
}
