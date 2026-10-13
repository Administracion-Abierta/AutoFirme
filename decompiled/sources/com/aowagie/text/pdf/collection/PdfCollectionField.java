package com.aowagie.text.pdf.collection;

import com.aowagie.text.pdf.PdfBoolean;
import com.aowagie.text.pdf.PdfDate;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfString;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/collection/PdfCollectionField.class */
class PdfCollectionField extends PdfDictionary {
    private static final int TEXT = 0;
    public static final int DATE = 1;
    public static final int NUMBER = 2;
    protected int fieldType;

    PdfCollectionField() {
    }

    public void setOrder(int i) {
        put(PdfName.O, new PdfNumber(i));
    }

    public void setVisible(boolean visible) {
        put(PdfName.V, new PdfBoolean(visible));
    }

    public void setEditable(boolean editable) {
        put(PdfName.E, new PdfBoolean(editable));
    }

    public boolean isCollectionItem() {
        switch (this.fieldType) {
            case 0:
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    public PdfObject getValue(String v) {
        switch (this.fieldType) {
            case 0:
                return new PdfString(v, PdfObject.TEXT_UNICODE);
            case 1:
                return new PdfDate(PdfDate.decode(v));
            case 2:
                return new PdfNumber(v);
            default:
                throw new IllegalArgumentException(v + " is not an acceptable value for the field " + get(PdfName.N).toString());
        }
    }
}
