package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/HeaderFooter.class */
public class HeaderFooter extends Rectangle {
    private final boolean numbered;
    private Phrase before;
    private int pageN;
    private final Phrase after;
    private int alignment;

    public HeaderFooter(Phrase before, boolean numbered) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.before = null;
        this.after = null;
        setBorder(3);
        setBorderWidth(1.0f);
        this.numbered = numbered;
        this.before = before;
    }

    public boolean isNumbered() {
        return this.numbered;
    }

    public Phrase getBefore() {
        return this.before;
    }

    public Phrase getAfter() {
        return this.after;
    }

    public void setPageNumber(int pageN) {
        this.pageN = pageN;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public Paragraph paragraph() {
        Paragraph paragraph = new Paragraph(this.before.getLeading());
        paragraph.add(this.before);
        if (this.numbered) {
            paragraph.addSpecial(new Chunk(String.valueOf(this.pageN), this.before.getFont()));
        }
        if (this.after != null) {
            paragraph.addSpecial(this.after);
        }
        paragraph.setAlignment(this.alignment);
        return paragraph;
    }
}
