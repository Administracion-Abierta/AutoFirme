package com.aowagie.text.pdf.events;

import com.aowagie.text.Document;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.PdfPageEvent;
import com.aowagie.text.pdf.PdfWriter;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/events/PdfPageEventForwarder.class */
public class PdfPageEventForwarder implements PdfPageEvent {
    private final ArrayList events = new ArrayList();

    public void addPageEvent(PdfPageEvent event) {
        this.events.add(event);
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onOpenDocument(PdfWriter writer, Document document) {
        for (PdfPageEvent event : this.events) {
            event.onOpenDocument(writer, document);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onStartPage(PdfWriter writer, Document document) {
        for (PdfPageEvent event : this.events) {
            event.onStartPage(writer, document);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onEndPage(PdfWriter writer, Document document) {
        for (PdfPageEvent event : this.events) {
            event.onEndPage(writer, document);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onCloseDocument(PdfWriter writer, Document document) {
        for (PdfPageEvent event : this.events) {
            event.onCloseDocument(writer, document);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
        for (PdfPageEvent event : this.events) {
            event.onParagraph(writer, document, paragraphPosition);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
        for (PdfPageEvent event : this.events) {
            event.onParagraphEnd(writer, document, paragraphPosition);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        for (PdfPageEvent event : this.events) {
            event.onChapter(writer, document, paragraphPosition, title);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onChapterEnd(PdfWriter writer, Document document, float position) {
        for (PdfPageEvent event : this.events) {
            event.onChapterEnd(writer, document, position);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
        for (PdfPageEvent event : this.events) {
            event.onSection(writer, document, paragraphPosition, depth, title);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onSectionEnd(PdfWriter writer, Document document, float position) {
        for (PdfPageEvent event : this.events) {
            event.onSectionEnd(writer, document, position);
        }
    }

    @Override // com.aowagie.text.pdf.PdfPageEvent
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
        for (PdfPageEvent event : this.events) {
            event.onGenericTag(writer, document, rect, text);
        }
    }
}
