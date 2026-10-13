package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Rectangle;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPageEventHelper.class */
public class PdfPageEventHelper implements PdfPageEvent {
    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onOpenDocument(PdfWriter writer, Document document) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onStartPage(PdfWriter writer, Document document) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onEndPage(PdfWriter writer, Document document) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onCloseDocument(PdfWriter writer, Document document) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onChapterEnd(PdfWriter writer, Document document, float position) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onSectionEnd(PdfWriter writer, Document document, float position) {
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
    }
}
