package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDExportFormatAttributeObject.class */
public class PDExportFormatAttributeObject extends PDLayoutAttributeObject {
    public static final String OWNER_XML_1_00 = "XML-1.00";
    public static final String OWNER_HTML_3_20 = "HTML-3.2";
    public static final String OWNER_HTML_4_01 = "HTML-4.01";
    public static final String OWNER_OEB_1_00 = "OEB-1.00";
    public static final String OWNER_RTF_1_05 = "RTF-1.05";
    public static final String OWNER_CSS_1_00 = "CSS-1.00";
    public static final String OWNER_CSS_2_00 = "CSS-2.00";

    public PDExportFormatAttributeObject(String owner) {
        setOwner(owner);
    }

    public PDExportFormatAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getListNumbering() {
        return getName("ListNumbering", "None");
    }

    public void setListNumbering(String listNumbering) {
        setName("ListNumbering", listNumbering);
    }

    public int getRowSpan() {
        return getInteger("RowSpan", 1);
    }

    public void setRowSpan(int rowSpan) {
        setInteger("RowSpan", rowSpan);
    }

    public int getColSpan() {
        return getInteger("ColSpan", 1);
    }

    public void setColSpan(int colSpan) {
        setInteger("ColSpan", colSpan);
    }

    public String[] getHeaders() {
        return getArrayOfString("Headers");
    }

    public void setHeaders(String[] headers) {
        setArrayOfString("Headers", headers);
    }

    public String getScope() {
        return getName("Scope");
    }

    public void setScope(String scope) {
        setName("Scope", scope);
    }

    public String getSummary() {
        return getString("Summary");
    }

    public void setSummary(String summary) {
        setString("Summary", summary);
    }

    @Override // org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject, org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (isSpecified("ListNumbering")) {
            sb.append(", ListNumbering=").append(getListNumbering());
        }
        if (isSpecified("RowSpan")) {
            sb.append(", RowSpan=").append(getRowSpan());
        }
        if (isSpecified("ColSpan")) {
            sb.append(", ColSpan=").append(getColSpan());
        }
        if (isSpecified("Headers")) {
            sb.append(", Headers=").append(arrayToString(getHeaders()));
        }
        if (isSpecified("Scope")) {
            sb.append(", Scope=").append(getScope());
        }
        if (isSpecified("Summary")) {
            sb.append(", Summary=").append(getSummary());
        }
        return sb.toString();
    }
}
