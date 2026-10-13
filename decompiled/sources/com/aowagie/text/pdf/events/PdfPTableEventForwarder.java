package com.aowagie.text.pdf.events;

import com.aowagie.text.pdf.PdfContentByte;
import com.aowagie.text.pdf.PdfPTable;
import com.aowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/events/PdfPTableEventForwarder.class */
public class PdfPTableEventForwarder implements PdfPTableEvent {
    private final ArrayList events = new ArrayList();

    public void addTableEvent(PdfPTableEvent event) {
        this.events.add(event);
    }

    @Override // com.aowagie.text.pdf.PdfPTableEvent
    public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
        for (PdfPTableEvent event : this.events) {
            event.tableLayout(table, widths, heights, headerRows, rowStart, canvases);
        }
    }
}
