package com.aowagie.text.pdf.collection;

import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfBoolean;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfObject;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/collection/PdfCollectionSort.class */
class PdfCollectionSort extends PdfDictionary {
    PdfCollectionSort() {
    }

    public void setSortOrder(boolean ascending) {
        PdfObject o = get(PdfName.S);
        if (o instanceof PdfName) {
            put(PdfName.A, new PdfBoolean(ascending));
            return;
        }
        throw new IllegalArgumentException("You have to define a boolean array for this collection sort dictionary.");
    }

    public void setSortOrder(boolean[] ascending) {
        PdfObject o = get(PdfName.S);
        if (o instanceof PdfArray) {
            if (((PdfArray) o).size() != ascending.length) {
                throw new IllegalArgumentException("The number of booleans in this array doesn't correspond with the number of fields.");
            }
            PdfArray array = new PdfArray();
            for (boolean element : ascending) {
                array.add(new PdfBoolean(element));
            }
            put(PdfName.A, array);
            return;
        }
        throw new IllegalArgumentException("You need a single boolean for this collection sort dictionary.");
    }
}
