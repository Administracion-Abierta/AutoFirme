package com.aowagie.text.pdf;

import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPage.class */
class PdfPage extends PdfDictionary {
    private static final String[] boxStrings = {"crop", "trim", "art", "bleed"};
    private static final PdfName[] boxNames = {PdfName.CROPBOX, PdfName.TRIMBOX, PdfName.ARTBOX, PdfName.BLEEDBOX};
    private final PdfRectangle mediaBox;

    PdfPage(PdfRectangle mediaBox, HashMap boxSize, PdfDictionary resources, int rotate) {
        super(PAGE);
        this.mediaBox = mediaBox;
        put(PdfName.MEDIABOX, mediaBox);
        put(PdfName.RESOURCES, resources);
        if (rotate != 0) {
            put(PdfName.ROTATE, new PdfNumber(rotate));
        }
        for (int k = 0; k < boxStrings.length; k++) {
            PdfObject rect = (PdfObject) boxSize.get(boxStrings[k]);
            if (rect != null) {
                put(boxNames[k], rect);
            }
        }
    }

    public boolean isParent() {
        return false;
    }

    void add(PdfIndirectReference contents) {
        put(PdfName.CONTENTS, contents);
    }
}
