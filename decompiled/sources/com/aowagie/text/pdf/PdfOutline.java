package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.Paragraph;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfOutline.class */
public class PdfOutline extends PdfDictionary {
    private PdfIndirectReference reference;
    private int count;
    private PdfOutline parent;
    private PdfDestination destination;
    private PdfAction action;
    protected ArrayList kids;
    protected PdfWriter writer;
    private String tag;
    private boolean open;
    private Color color;
    private int style;

    PdfOutline(PdfWriter writer) {
        super(OUTLINES);
        this.count = 0;
        this.kids = new ArrayList();
        this.style = 0;
        this.open = true;
        this.parent = null;
        this.writer = writer;
    }

    public PdfOutline(PdfOutline parent, PdfAction action, String title) {
        this(parent, action, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, String title, boolean open) {
        this.count = 0;
        this.kids = new ArrayList();
        this.style = 0;
        this.action = action;
        initOutline(parent, title, open);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, String title) {
        this(parent, destination, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, String title, boolean open) {
        this.count = 0;
        this.kids = new ArrayList();
        this.style = 0;
        this.destination = destination;
        initOutline(parent, title, open);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, PdfString title) {
        this(parent, action, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, PdfString title, boolean open) {
        this(parent, action, title.toString(), open);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, PdfString title) {
        this(parent, destination, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, PdfString title, boolean open) {
        this(parent, destination, title.toString(), true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, Paragraph title) {
        this(parent, action, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, Paragraph title, boolean open) {
        this.count = 0;
        this.kids = new ArrayList();
        this.style = 0;
        StringBuffer buf = new StringBuffer();
        for (Chunk chunk : title.getChunks()) {
            buf.append(chunk.getContent());
        }
        this.action = action;
        initOutline(parent, buf.toString(), open);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, Paragraph title) {
        this(parent, destination, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, Paragraph title, boolean open) {
        this.count = 0;
        this.kids = new ArrayList();
        this.style = 0;
        StringBuffer buf = new StringBuffer();
        for (Chunk chunk : title.getChunks()) {
            buf.append(chunk.getContent());
        }
        this.destination = destination;
        initOutline(parent, buf.toString(), open);
    }

    void initOutline(PdfOutline parent, String title, boolean open) {
        this.open = open;
        this.parent = parent;
        this.writer = parent.writer;
        put(PdfName.TITLE, new PdfString(title, PdfObject.TEXT_UNICODE));
        parent.addKid(this);
        if (this.destination != null && !this.destination.hasPage()) {
            setDestinationPage(this.writer.getCurrentPage());
        }
    }

    public void setIndirectReference(PdfIndirectReference reference) {
        this.reference = reference;
    }

    public PdfIndirectReference indirectReference() {
        return this.reference;
    }

    public PdfOutline parent() {
        return this.parent;
    }

    public boolean setDestinationPage(PdfIndirectReference pageReference) {
        if (this.destination == null) {
            return false;
        }
        return this.destination.addPage(pageReference);
    }

    public PdfDestination getPdfDestination() {
        return this.destination;
    }

    int getCount() {
        return this.count;
    }

    void setCount(int count) {
        this.count = count;
    }

    public int level() {
        if (this.parent == null) {
            return 0;
        }
        return this.parent.level() + 1;
    }

    @Override // com.aowagie.text.pdf.PdfDictionary, com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        if (this.color != null && !this.color.equals(Color.black)) {
            put(PdfName.C, new PdfArray(new float[]{this.color.getRed() / 255.0f, this.color.getGreen() / 255.0f, this.color.getBlue() / 255.0f}));
        }
        int flag = 0;
        if ((this.style & 1) != 0) {
            flag = 0 | 2;
        }
        if ((this.style & 2) != 0) {
            flag |= 1;
        }
        if (flag != 0) {
            put(PdfName.F, new PdfNumber(flag));
        }
        if (this.parent != null) {
            put(PdfName.PARENT, this.parent.indirectReference());
        }
        if (this.destination != null && this.destination.hasPage()) {
            put(PdfName.DEST, this.destination);
        }
        if (this.action != null) {
            put(PdfName.A, this.action);
        }
        if (this.count != 0) {
            put(PdfName.COUNT, new PdfNumber(this.count));
        }
        super.toPdf(writer, os);
    }

    public void addKid(PdfOutline outline) {
        this.kids.add(outline);
    }

    public ArrayList getKids() {
        return this.kids;
    }

    public void setKids(ArrayList kids) {
        this.kids = kids;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        PdfString title = (PdfString) get(PdfName.TITLE);
        return title.toString();
    }

    public void setTitle(String title) {
        put(PdfName.TITLE, new PdfString(title, PdfObject.TEXT_UNICODE));
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
