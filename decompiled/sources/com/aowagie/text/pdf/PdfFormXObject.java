package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfFormXObject.class */
class PdfFormXObject extends PdfStream {
    private static final PdfNumber ONE = new PdfNumber(1);
    private static final PdfLiteral MATRIX = new PdfLiteral("[1 0 0 1 0 0]");

    PdfFormXObject(PdfTemplate template, int compressionLevel) {
        put(PdfName.TYPE, PdfName.XOBJECT);
        put(PdfName.SUBTYPE, PdfName.FORM);
        put(PdfName.RESOURCES, template.getResources());
        put(PdfName.BBOX, new PdfRectangle(template.getBoundingBox()));
        put(PdfName.FORMTYPE, ONE);
        if (template.getLayer() != null) {
            put(PdfName.OC, template.getLayer().getRef());
        }
        if (template.getGroup() != null) {
            put(PdfName.GROUP, template.getGroup());
        }
        PdfArray matrix = template.getMatrix();
        if (matrix == null) {
            put(PdfName.MATRIX, MATRIX);
        } else {
            put(PdfName.MATRIX, matrix);
        }
        this.bytes = template.toPdf(null);
        put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        flateCompress(compressionLevel);
    }
}
