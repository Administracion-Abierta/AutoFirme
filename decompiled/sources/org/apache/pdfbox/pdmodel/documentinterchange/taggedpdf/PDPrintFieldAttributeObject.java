package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDPrintFieldAttributeObject.class */
public class PDPrintFieldAttributeObject extends PDStandardAttributeObject {
    public static final String OWNER_PRINT_FIELD = "PrintField";
    private static final String ROLE = "Role";
    private static final String CHECKED = "checked";
    private static final String DESC = "Desc";
    public static final String ROLE_RB = "rb";
    public static final String ROLE_CB = "cb";
    public static final String ROLE_PB = "pb";
    public static final String ROLE_TV = "tv";
    public static final String CHECKED_STATE_ON = "on";
    public static final String CHECKED_STATE_OFF = "off";
    public static final String CHECKED_STATE_NEUTRAL = "neutral";

    public PDPrintFieldAttributeObject() {
        setOwner(OWNER_PRINT_FIELD);
    }

    public PDPrintFieldAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getRole() {
        return getName(ROLE);
    }

    public void setRole(String role) {
        setName(ROLE, role);
    }

    public String getCheckedState() {
        return getName(CHECKED, CHECKED_STATE_OFF);
    }

    public void setCheckedState(String checkedState) {
        setName(CHECKED, checkedState);
    }

    public String getAlternateName() {
        return getString(DESC);
    }

    public void setAlternateName(String alternateName) {
        setString(DESC, alternateName);
    }

    @Override // org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (isSpecified(ROLE)) {
            sb.append(", Role=").append(getRole());
        }
        if (isSpecified(CHECKED)) {
            sb.append(", Checked=").append(getCheckedState());
        }
        if (isSpecified(DESC)) {
            sb.append(", Desc=").append(getAlternateName());
        }
        return sb.toString();
    }
}
