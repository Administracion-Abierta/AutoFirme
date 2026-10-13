package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ColorDetails.class */
class ColorDetails {
    private final PdfIndirectReference indirectReference;
    private final PdfName colorName;
    private final PdfSpotColor spotcolor;

    ColorDetails(PdfName colorName, PdfIndirectReference indirectReference, PdfSpotColor scolor) {
        this.colorName = colorName;
        this.indirectReference = indirectReference;
        this.spotcolor = scolor;
    }

    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }

    PdfName getColorName() {
        return this.colorName;
    }

    PdfObject getSpotColor(PdfWriter writer) {
        return this.spotcolor.getSpotObject(writer);
    }
}
