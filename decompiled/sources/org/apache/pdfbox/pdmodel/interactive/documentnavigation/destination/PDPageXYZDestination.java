package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/destination/PDPageXYZDestination.class */
public class PDPageXYZDestination extends PDPageDestination {
    protected static final String TYPE = "XYZ";

    public PDPageXYZDestination() {
        this.array.growToSize(5);
        this.array.setName(1, TYPE);
    }

    public PDPageXYZDestination(COSArray arr) {
        super(arr);
    }

    public int getLeft() {
        return this.array.getInt(2);
    }

    public void setLeft(int x) {
        this.array.growToSize(5);
        if (x == -1) {
            this.array.set(2, (COSBase) null);
        } else {
            this.array.setInt(2, x);
        }
    }

    public int getTop() {
        return this.array.getInt(3);
    }

    public void setTop(int y) {
        this.array.growToSize(5);
        if (y == -1) {
            this.array.set(3, (COSBase) null);
        } else {
            this.array.setInt(3, y);
        }
    }

    public float getZoom() {
        COSBase obj = this.array.getObject(4);
        if (obj instanceof COSNumber) {
            return ((COSNumber) obj).floatValue();
        }
        return -1.0f;
    }

    public void setZoom(float zoom) {
        this.array.growToSize(5);
        if (zoom == -1.0f) {
            this.array.set(4, (COSBase) null);
        } else {
            this.array.set(4, (COSBase) new COSFloat(zoom));
        }
    }
}
