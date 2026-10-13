package org.apache.pdfbox.pdmodel.encryption;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/AccessPermission.class */
public class AccessPermission {
    private static final int DEFAULT_PERMISSIONS = -4;
    private static final int PRINT_BIT = 3;
    private static final int MODIFICATION_BIT = 4;
    private static final int EXTRACT_BIT = 5;
    private static final int MODIFY_ANNOTATIONS_BIT = 6;
    private static final int FILL_IN_FORM_BIT = 9;
    private static final int EXTRACT_FOR_ACCESSIBILITY_BIT = 10;
    private static final int ASSEMBLE_DOCUMENT_BIT = 11;
    private static final int DEGRADED_PRINT_BIT = 12;
    private int bytes;
    private boolean readOnly;

    public AccessPermission() {
        this.readOnly = false;
        this.bytes = -4;
    }

    public AccessPermission(byte[] b) {
        this.readOnly = false;
        this.bytes = 0;
        this.bytes |= b[0] & 255;
        this.bytes <<= 8;
        this.bytes |= b[1] & 255;
        this.bytes <<= 8;
        this.bytes |= b[2] & 255;
        this.bytes <<= 8;
        this.bytes |= b[3] & 255;
    }

    public AccessPermission(int permissions) {
        this.readOnly = false;
        this.bytes = permissions;
    }

    private boolean isPermissionBitOn(int bit) {
        return (this.bytes & (1 << (bit - 1))) != 0;
    }

    private boolean setPermissionBit(int bit, boolean value) {
        int permissions;
        int permissions2 = this.bytes;
        if (value) {
            permissions = permissions2 | (1 << (bit - 1));
        } else {
            permissions = permissions2 & ((1 << (bit - 1)) ^ (-1));
        }
        this.bytes = permissions;
        return (this.bytes & (1 << (bit - 1))) != 0;
    }

    public boolean isOwnerPermission() {
        return canAssembleDocument() && canExtractContent() && canExtractForAccessibility() && canFillInForm() && canModify() && canModifyAnnotations() && canPrint() && canPrintDegraded();
    }

    public static AccessPermission getOwnerAccessPermission() {
        AccessPermission ret = new AccessPermission();
        ret.setCanAssembleDocument(true);
        ret.setCanExtractContent(true);
        ret.setCanExtractForAccessibility(true);
        ret.setCanFillInForm(true);
        ret.setCanModify(true);
        ret.setCanModifyAnnotations(true);
        ret.setCanPrint(true);
        ret.setCanPrintDegraded(true);
        return ret;
    }

    public int getPermissionBytesForPublicKey() {
        setPermissionBit(1, true);
        setPermissionBit(7, false);
        setPermissionBit(8, false);
        for (int i = 13; i <= 32; i++) {
            setPermissionBit(i, false);
        }
        return this.bytes;
    }

    public int getPermissionBytes() {
        return this.bytes;
    }

    public boolean canPrint() {
        return isPermissionBitOn(3);
    }

    public void setCanPrint(boolean allowPrinting) {
        if (!this.readOnly) {
            setPermissionBit(3, allowPrinting);
        }
    }

    public boolean canModify() {
        return isPermissionBitOn(4);
    }

    public void setCanModify(boolean allowModifications) {
        if (!this.readOnly) {
            setPermissionBit(4, allowModifications);
        }
    }

    public boolean canExtractContent() {
        return isPermissionBitOn(5);
    }

    public void setCanExtractContent(boolean allowExtraction) {
        if (!this.readOnly) {
            setPermissionBit(5, allowExtraction);
        }
    }

    public boolean canModifyAnnotations() {
        return isPermissionBitOn(6);
    }

    public void setCanModifyAnnotations(boolean allowAnnotationModification) {
        if (!this.readOnly) {
            setPermissionBit(6, allowAnnotationModification);
        }
    }

    public boolean canFillInForm() {
        return isPermissionBitOn(9);
    }

    public void setCanFillInForm(boolean allowFillingInForm) {
        if (!this.readOnly) {
            setPermissionBit(9, allowFillingInForm);
        }
    }

    public boolean canExtractForAccessibility() {
        return isPermissionBitOn(10);
    }

    public void setCanExtractForAccessibility(boolean allowExtraction) {
        if (!this.readOnly) {
            setPermissionBit(10, allowExtraction);
        }
    }

    public boolean canAssembleDocument() {
        return isPermissionBitOn(11);
    }

    public void setCanAssembleDocument(boolean allowAssembly) {
        if (!this.readOnly) {
            setPermissionBit(11, allowAssembly);
        }
    }

    public boolean canPrintDegraded() {
        return isPermissionBitOn(12);
    }

    public void setCanPrintDegraded(boolean canPrintDegraded) {
        if (!this.readOnly) {
            setPermissionBit(12, canPrintDegraded);
        }
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    protected boolean hasAnyRevision3PermissionSet() {
        if (canFillInForm() || canExtractForAccessibility() || canAssembleDocument()) {
            return true;
        }
        return canPrintDegraded();
    }
}
