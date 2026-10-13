package com.aowagie.text.pdf.events;

import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.PdfContentByte;
import com.aowagie.text.pdf.PdfPCell;
import com.aowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/events/PdfPCellEventForwarder.class */
public class PdfPCellEventForwarder implements PdfPCellEvent {
    private final ArrayList events = new ArrayList();

    public void addCellEvent(PdfPCellEvent event) {
        this.events.add(event);
    }

    @Override // com.aowagie.text.pdf.PdfPCellEvent
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        for (PdfPCellEvent event : this.events) {
            event.cellLayout(cell, position, canvases);
        }
    }
}
