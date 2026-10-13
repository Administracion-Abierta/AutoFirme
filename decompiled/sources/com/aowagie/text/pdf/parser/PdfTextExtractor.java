package com.aowagie.text.pdf.parser;

import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfTextExtractor.class */
class PdfTextExtractor {
    private final PdfReader reader;
    private final SimpleTextExtractingPdfContentStreamProcessor extractionProcessor = new SimpleTextExtractingPdfContentStreamProcessor();

    public PdfTextExtractor(PdfReader reader) {
        this.reader = reader;
    }

    private byte[] getContentBytesForPage(int pageNum) throws IOException {
        RandomAccessFileOrArray f = this.reader.getSafeFile();
        byte[] contentBytes = this.reader.getPageContent(pageNum, f);
        f.close();
        return contentBytes;
    }

    public String getTextFromPage(int page) throws IOException {
        PdfDictionary pageDic = this.reader.getPageN(page);
        PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
        this.extractionProcessor.processContent(getContentBytesForPage(page), resourcesDic);
        return this.extractionProcessor.getResultantText();
    }
}
