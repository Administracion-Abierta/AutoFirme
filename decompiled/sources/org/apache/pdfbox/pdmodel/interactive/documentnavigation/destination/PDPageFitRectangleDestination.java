package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/destination/PDPageFitRectangleDestination.class */
public class PDPageFitRectangleDestination extends PDPageDestination {
    protected static final String TYPE = "FitR";

    public PDPageFitRectangleDestination() {
        this.array.growToSize(6);
        this.array.setName(1, TYPE);
    }

    public PDPageFitRectangleDestination(COSArray arr) {
        super(arr);
    }

    public int getLeft() {
        return this.array.getInt(2);
    }

    public void setLeft(int x) {
        this.array.growToSize(6);
        if (x == -1) {
            this.array.set(2, (COSBase) null);
        } else {
            this.array.setInt(2, x);
        }
    }

    public int getBottom() {
        return this.array.getInt(3);
    }

    public void setBottom(int y) {
        this.array.growToSize(6);
        if (y == -1) {
            this.array.set(3, (COSBase) null);
        } else {
            this.array.setInt(3, y);
        }
    }

    public int getRight() {
        return this.array.getInt(4);
    }

    public void setRight(int x) {
        this.array.growToSize(6);
        if (x == -1) {
            this.array.set(4, (COSBase) null);
        } else {
            this.array.setInt(4, x);
        }
    }

    public int getTop() {
        return this.array.getInt(5);
    }

    public void setTop(int y) {
        this.array.growToSize(6);
        if (y == -1) {
            this.array.set(5, (COSBase) null);
        } else {
            this.array.setInt(5, y);
        }
    }
}
