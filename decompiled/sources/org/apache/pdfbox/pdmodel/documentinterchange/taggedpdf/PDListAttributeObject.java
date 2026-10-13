package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDListAttributeObject.class */
public class PDListAttributeObject extends PDStandardAttributeObject {
    public static final String OWNER_LIST = "List";
    protected static final String LIST_NUMBERING = "ListNumbering";
    public static final String LIST_NUMBERING_CIRCLE = "Circle";
    public static final String LIST_NUMBERING_DECIMAL = "Decimal";
    public static final String LIST_NUMBERING_DISC = "Disc";
    public static final String LIST_NUMBERING_LOWER_ALPHA = "LowerAlpha";
    public static final String LIST_NUMBERING_LOWER_ROMAN = "LowerRoman";
    public static final String LIST_NUMBERING_NONE = "None";
    public static final String LIST_NUMBERING_SQUARE = "Square";
    public static final String LIST_NUMBERING_UPPER_ALPHA = "UpperAlpha";
    public static final String LIST_NUMBERING_UPPER_ROMAN = "UpperRoman";

    public PDListAttributeObject() {
        setOwner(OWNER_LIST);
    }

    public PDListAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getListNumbering() {
        return getName(LIST_NUMBERING, "None");
    }

    public void setListNumbering(String listNumbering) {
        setName(LIST_NUMBERING, listNumbering);
    }

    @Override // org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (isSpecified(LIST_NUMBERING)) {
            sb.append(", ListNumbering=").append(getListNumbering());
        }
        return sb.toString();
    }
}
