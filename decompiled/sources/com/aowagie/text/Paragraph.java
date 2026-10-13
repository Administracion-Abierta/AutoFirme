package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Paragraph.class */
public class Paragraph extends Phrase {
    private static final long serialVersionUID = 7852314969733375514L;
    private int alignment;
    private float multipliedLeading;
    private float indentationLeft;
    private float indentationRight;
    private float firstLineIndent;
    private float spacingBefore;
    private float spacingAfter;
    private float extraParagraphSpace;
    private boolean keeptogether;

    public Paragraph() {
        this.alignment = -1;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.firstLineIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.extraParagraphSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.keeptogether = false;
    }

    Paragraph(float leading) {
        super(leading);
        this.alignment = -1;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.firstLineIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.extraParagraphSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.keeptogether = false;
    }

    public Paragraph(Chunk chunk) {
        super(chunk);
        this.alignment = -1;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.firstLineIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.extraParagraphSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.keeptogether = false;
    }

    public Paragraph(String string) {
        super(string);
        this.alignment = -1;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.firstLineIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.extraParagraphSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.keeptogether = false;
    }

    public Paragraph(String string, Font font) {
        super(string, font);
        this.alignment = -1;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.firstLineIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.extraParagraphSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.keeptogether = false;
    }

    public Paragraph(Phrase phrase) {
        super(phrase);
        this.alignment = -1;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.firstLineIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.extraParagraphSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.keeptogether = false;
        if (phrase instanceof Paragraph) {
            Paragraph p = (Paragraph) phrase;
            setAlignment(p.alignment);
            setLeading(phrase.getLeading(), p.multipliedLeading);
            setIndentationLeft(p.getIndentationLeft());
            setIndentationRight(p.getIndentationRight());
            setFirstLineIndent(p.getFirstLineIndent());
            setSpacingAfter(p.spacingAfter());
            setSpacingBefore(p.spacingBefore());
            setExtraParagraphSpace(p.getExtraParagraphSpace());
        }
    }

    @Override // com.aowagie.text.Phrase, com.aowagie.text.Element
    public int type() {
        return 12;
    }

    @Override // com.aowagie.text.Phrase, java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List, com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        if (o instanceof List) {
            List list = (List) o;
            list.setIndentationLeft(list.getIndentationLeft() + this.indentationLeft);
            list.setIndentationRight(this.indentationRight);
            return super.add(list);
        }
        if (o instanceof Image) {
            super.addSpecial(o);
            return true;
        }
        if (o instanceof Paragraph) {
            super.add(o);
            java.util.List chunks = getChunks();
            if (!chunks.isEmpty()) {
                Chunk tmp = (Chunk) chunks.get(chunks.size() - 1);
                super.add(new Chunk("\n", tmp.getFont()));
                return true;
            }
            super.add(Chunk.NEWLINE);
            return true;
        }
        return super.add(o);
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public void setAlignment(String alignment) {
        if ("Center".equalsIgnoreCase(alignment)) {
            this.alignment = 1;
            return;
        }
        if (ElementTags.ALIGN_RIGHT.equalsIgnoreCase(alignment)) {
            this.alignment = 2;
            return;
        }
        if ("Justify".equalsIgnoreCase(alignment)) {
            this.alignment = 3;
        } else if ("JustifyAll".equalsIgnoreCase(alignment)) {
            this.alignment = 8;
        } else {
            this.alignment = 0;
        }
    }

    @Override // com.aowagie.text.Phrase
    public void setLeading(float fixedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public void setMultipliedLeading(float multipliedLeading) {
        this.leading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.multipliedLeading = multipliedLeading;
    }

    private void setLeading(float fixedLeading, float multipliedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }

    public void setIndentationLeft(float indentation) {
        this.indentationLeft = indentation;
    }

    public void setIndentationRight(float indentation) {
        this.indentationRight = indentation;
    }

    public void setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    public void setKeepTogether(boolean keeptogether) {
        this.keeptogether = keeptogether;
    }

    public boolean getKeepTogether() {
        return this.keeptogether;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }

    public float getTotalLeading() {
        float m = this.font == null ? 12.0f * this.multipliedLeading : this.font.getCalculatedLeading(this.multipliedLeading);
        if (m > ColumnText.GLOBAL_SPACE_CHAR_RATIO && !hasLeading()) {
            return m;
        }
        return getLeading() + m;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public float getFirstLineIndent() {
        return this.firstLineIndent;
    }

    public float getSpacingBefore() {
        return this.spacingBefore;
    }

    public float getSpacingAfter() {
        return this.spacingAfter;
    }

    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }

    @Deprecated
    private float spacingBefore() {
        return getSpacingBefore();
    }

    @Deprecated
    private float spacingAfter() {
        return this.spacingAfter;
    }
}
