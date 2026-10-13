package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Rectangle;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPageEvent.class */
public interface PdfPageEvent {
    void onOpenDocument(PdfWriter pdfWriter, Document document);

    void onStartPage(PdfWriter pdfWriter, Document document);

    void onEndPage(PdfWriter pdfWriter, Document document);

    void onCloseDocument(PdfWriter pdfWriter, Document document);

    void onParagraph(PdfWriter pdfWriter, Document document, float f);

    void onParagraphEnd(PdfWriter pdfWriter, Document document, float f);

    void onChapter(PdfWriter pdfWriter, Document document, float f, Paragraph paragraph);

    void onChapterEnd(PdfWriter pdfWriter, Document document, float f);

    void onSection(PdfWriter pdfWriter, Document document, float f, int i, Paragraph paragraph);

    void onSectionEnd(PdfWriter pdfWriter, Document document, float f);

    void onGenericTag(PdfWriter pdfWriter, Document document, Rectangle rectangle, String str);
}
