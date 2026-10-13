package com.aowagie.text.pdf;

import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfColor.class */
class PdfColor extends PdfArray {
    PdfColor(int red, int green, int blue) {
        super(new PdfNumber(((double) (red & 255)) / 255.0d));
        add(new PdfNumber(((double) (green & 255)) / 255.0d));
        add(new PdfNumber(((double) (blue & 255)) / 255.0d));
    }

    PdfColor(Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue());
    }
}
