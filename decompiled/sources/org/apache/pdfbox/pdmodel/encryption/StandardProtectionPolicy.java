package org.apache.pdfbox.pdmodel.encryption;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/StandardProtectionPolicy.class */
public final class StandardProtectionPolicy extends ProtectionPolicy {
    private AccessPermission permissions;
    private String ownerPassword;
    private String userPassword;

    public StandardProtectionPolicy(String ownerPassword, String userPassword, AccessPermission permissions) {
        this.ownerPassword = "";
        this.userPassword = "";
        this.ownerPassword = ownerPassword;
        this.userPassword = userPassword;
        this.permissions = permissions;
    }

    public AccessPermission getPermissions() {
        return this.permissions;
    }

    public void setPermissions(AccessPermission permissions) {
        this.permissions = permissions;
    }

    public String getOwnerPassword() {
        return this.ownerPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
