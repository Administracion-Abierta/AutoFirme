package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDestination.class */
public class PdfDestination extends PdfArray {
    public static final int XYZ = 0;
    public static final int FIT = 1;
    public static final int FITH = 2;
    public static final int FITV = 3;
    public static final int FITR = 4;
    public static final int FITB = 5;
    public static final int FITBH = 6;
    public static final int FITBV = 7;
    private boolean status;

    public PdfDestination(int type) {
        this.status = false;
        if (type == 5) {
            add(PdfName.FITB);
        } else {
            add(PdfName.FIT);
        }
    }

    public PdfDestination(int type, float parameter) {
        super(new PdfNumber(parameter));
        this.status = false;
        switch (type) {
            case 3:
                addFirst(PdfName.FITV);
                break;
            case 4:
            case 5:
            default:
                addFirst(PdfName.FITH);
                break;
            case 6:
                addFirst(PdfName.FITBH);
                break;
            case 7:
                addFirst(PdfName.FITBV);
                break;
        }
    }

    public PdfDestination(int type, float left, float top, float zoom) {
        super(PdfName.XYZ);
        this.status = false;
        if (left < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            add(PdfNull.PDFNULL);
        } else {
            add(new PdfNumber(left));
        }
        if (top < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            add(PdfNull.PDFNULL);
        } else {
            add(new PdfNumber(top));
        }
        add(new PdfNumber(zoom));
    }

    public PdfDestination(int type, float left, float bottom, float right, float top) {
        super(PdfName.FITR);
        this.status = false;
        add(new PdfNumber(left));
        add(new PdfNumber(bottom));
        add(new PdfNumber(right));
        add(new PdfNumber(top));
    }

    public boolean hasPage() {
        return this.status;
    }

    public boolean addPage(PdfIndirectReference page) {
        if (!this.status) {
            addFirst(page);
            this.status = true;
            return true;
        }
        return false;
    }
}
