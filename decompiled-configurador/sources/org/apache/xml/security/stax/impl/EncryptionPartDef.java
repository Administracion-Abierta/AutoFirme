package org.apache.xml.security.stax.impl;

import java.security.Key;
import org.apache.xml.security.stax.ext.SecurePart;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/EncryptionPartDef.class */
public class EncryptionPartDef {
    private SecurePart securePart;
    private SecurePart.Modifier modifier;
    private Key symmetricKey;
    private String keyId;
    private String encRefId;
    private String cipherReferenceId;
    private String mimeType;

    public SecurePart getSecurePart() {
        return this.securePart;
    }

    public void setSecurePart(SecurePart securePart) {
        this.securePart = securePart;
    }

    public SecurePart.Modifier getModifier() {
        return this.modifier;
    }

    public void setModifier(SecurePart.Modifier modifier) {
        this.modifier = modifier;
    }

    public Key getSymmetricKey() {
        return this.symmetricKey;
    }

    public void setSymmetricKey(Key symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEncRefId() {
        return this.encRefId;
    }

    public void setEncRefId(String encRefId) {
        this.encRefId = encRefId;
    }

    public String getCipherReferenceId() {
        return this.cipherReferenceId;
    }

    public void setCipherReferenceId(String cipherReferenceId) {
        this.cipherReferenceId = cipherReferenceId;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
