package com.aowagie.text.pdf;

import com.aowagie.text.Rectangle;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfRectangle.class */
public class PdfRectangle extends PdfArray {
    private float llx;
    private float lly;
    private float urx;
    private float ury;

    private PdfRectangle(float llx, float lly, float urx, float ury, int rotation) {
        this.llx = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.lly = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.urx = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.ury = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        if (rotation == 90 || rotation == 270) {
            this.llx = lly;
            this.lly = llx;
            this.urx = ury;
            this.ury = urx;
        } else {
            this.llx = llx;
            this.lly = lly;
            this.urx = urx;
            this.ury = ury;
        }
        super.add(new PdfNumber(this.llx));
        super.add(new PdfNumber(this.lly));
        super.add(new PdfNumber(this.urx));
        super.add(new PdfNumber(this.ury));
    }

    public PdfRectangle(float llx, float lly, float urx, float ury) {
        this(llx, lly, urx, ury, 0);
    }

    PdfRectangle(Rectangle rectangle, int rotation) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), rotation);
    }

    PdfRectangle(Rectangle rectangle) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), 0);
    }

    public Rectangle getRectangle() {
        return new Rectangle(left(), bottom(), right(), top());
    }

    @Override // com.aowagie.text.pdf.PdfArray
    public boolean add(PdfObject object) {
        return false;
    }

    @Override // com.aowagie.text.pdf.PdfArray
    public boolean add(float[] values) {
        return false;
    }

    @Override // com.aowagie.text.pdf.PdfArray
    public boolean add(int[] values) {
        return false;
    }

    @Override // com.aowagie.text.pdf.PdfArray
    public void addFirst(PdfObject object) {
    }

    public float left() {
        return this.llx;
    }

    public float right() {
        return this.urx;
    }

    public float top() {
        return this.ury;
    }

    public float bottom() {
        return this.lly;
    }
}
