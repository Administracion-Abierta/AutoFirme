package org.apache.pdfbox.pdmodel.encryption;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/PublicKeyRecipient.class */
public class PublicKeyRecipient {
    private X509Certificate x509;
    private AccessPermission permission;

    public X509Certificate getX509() {
        return this.x509;
    }

    public void setX509(X509Certificate aX509) {
        this.x509 = aX509;
    }

    public AccessPermission getPermission() {
        return this.permission;
    }

    public void setPermission(AccessPermission permissions) {
        this.permission = permissions;
    }
}
