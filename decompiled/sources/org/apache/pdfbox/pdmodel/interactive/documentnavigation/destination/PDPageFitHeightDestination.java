package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/destination/PDPageFitHeightDestination.class */
public class PDPageFitHeightDestination extends PDPageDestination {
    protected static final String TYPE = "FitV";
    protected static final String TYPE_BOUNDED = "FitBV";

    public PDPageFitHeightDestination() {
        this.array.growToSize(3);
        this.array.setName(1, TYPE);
    }

    public PDPageFitHeightDestination(COSArray arr) {
        super(arr);
    }

    public int getLeft() {
        return this.array.getInt(2);
    }

    public void setLeft(int x) {
        this.array.growToSize(3);
        if (x == -1) {
            this.array.set(2, (COSBase) null);
        } else {
            this.array.setInt(2, x);
        }
    }

    public boolean fitBoundingBox() {
        return TYPE_BOUNDED.equals(this.array.getName(1));
    }

    public void setFitBoundingBox(boolean fitBoundingBox) {
        this.array.growToSize(3);
        if (fitBoundingBox) {
            this.array.setName(1, TYPE_BOUNDED);
        } else {
            this.array.setName(1, TYPE);
        }
    }
}
