package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDTableAttributeObject.class */
public class PDTableAttributeObject extends PDStandardAttributeObject {
    public static final String OWNER_TABLE = "Table";
    protected static final String ROW_SPAN = "RowSpan";
    protected static final String COL_SPAN = "ColSpan";
    protected static final String HEADERS = "Headers";
    protected static final String SCOPE = "Scope";
    protected static final String SUMMARY = "Summary";
    public static final String SCOPE_BOTH = "Both";
    public static final String SCOPE_COLUMN = "Column";
    public static final String SCOPE_ROW = "Row";

    public PDTableAttributeObject() {
        setOwner("Table");
    }

    public PDTableAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public int getRowSpan() {
        return getInteger(ROW_SPAN, 1);
    }

    public void setRowSpan(int rowSpan) {
        setInteger(ROW_SPAN, rowSpan);
    }

    public int getColSpan() {
        return getInteger(COL_SPAN, 1);
    }

    public void setColSpan(int colSpan) {
        setInteger(COL_SPAN, colSpan);
    }

    public String[] getHeaders() {
        return getArrayOfString(HEADERS);
    }

    public void setHeaders(String[] headers) {
        setArrayOfString(HEADERS, headers);
    }

    public String getScope() {
        return getName(SCOPE);
    }

    public void setScope(String scope) {
        setName(SCOPE, scope);
    }

    public String getSummary() {
        return getString(SUMMARY);
    }

    public void setSummary(String summary) {
        setString(SUMMARY, summary);
    }

    @Override // org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (isSpecified(ROW_SPAN)) {
            sb.append(", RowSpan=").append(getRowSpan());
        }
        if (isSpecified(COL_SPAN)) {
            sb.append(", ColSpan=").append(getColSpan());
        }
        if (isSpecified(HEADERS)) {
            sb.append(", Headers=").append(arrayToString(getHeaders()));
        }
        if (isSpecified(SCOPE)) {
            sb.append(", Scope=").append(getScope());
        }
        if (isSpecified(SUMMARY)) {
            sb.append(", Summary=").append(getSummary());
        }
        return sb.toString();
    }
}
