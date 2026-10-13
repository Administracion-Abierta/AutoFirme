package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/destination/PDPageFitWidthDestination.class */
public class PDPageFitWidthDestination extends PDPageDestination {
    protected static final String TYPE = "FitH";
    protected static final String TYPE_BOUNDED = "FitBH";

    public PDPageFitWidthDestination() {
        this.array.growToSize(3);
        this.array.setName(1, TYPE);
    }

    public PDPageFitWidthDestination(COSArray arr) {
        super(arr);
    }

    public int getTop() {
        return this.array.getInt(2);
    }

    public void setTop(int y) {
        this.array.growToSize(3);
        if (y == -1) {
            this.array.set(2, (COSBase) null);
        } else {
            this.array.setInt(2, y);
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
