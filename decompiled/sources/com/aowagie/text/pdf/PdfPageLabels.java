package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPageLabels.class */
class PdfPageLabels {
    private static final int DECIMAL_ARABIC_NUMERALS = 0;
    private static PdfName[] numberingStyle = {PdfName.D, PdfName.R, new PdfName(PDPageLabelRange.STYLE_ROMAN_LOWER), PdfName.A, new PdfName(PDPageLabelRange.STYLE_LETTERS_LOWER)};
    private final HashMap map = new LinkedHashMap();

    public PdfPageLabels() {
        addPageLabel(1, 0, null, 1);
    }

    private void addPageLabel(int page, int numberStyle, String text, int firstPage) {
        if (page < 1 || firstPage < 1) {
            throw new IllegalArgumentException("In a page label the page numbers must be greater or equal to 1.");
        }
        PdfDictionary dic = new PdfDictionary();
        if (numberStyle >= 0 && numberStyle < numberingStyle.length) {
            dic.put(PdfName.S, numberingStyle[numberStyle]);
        }
        if (text != null) {
            dic.put(PdfName.P, new PdfString(text, PdfObject.TEXT_UNICODE));
        }
        if (firstPage != 1) {
            dic.put(PdfName.ST, new PdfNumber(firstPage));
        }
        this.map.put(Integer.valueOf(page - 1), dic);
    }

    PdfDictionary getDictionary(PdfWriter writer) {
        try {
            return PdfNumberTree.writeTree(this.map, writer);
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPageLabels$PdfPageLabelFormat.class */
    private static class PdfPageLabelFormat {
        private final int physicalPage;
        private final int numberStyle;
        private final String prefix;
        private final int logicalPage;

        private PdfPageLabelFormat(int physicalPage, int numberStyle, String prefix, int logicalPage) {
            this.physicalPage = physicalPage;
            this.numberStyle = numberStyle;
            this.prefix = prefix;
            this.logicalPage = logicalPage;
        }
    }
}
