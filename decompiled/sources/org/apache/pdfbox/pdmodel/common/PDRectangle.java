package org.apache.pdfbox.pdmodel.common;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDRectangle.class */
public class PDRectangle implements COSObjectable {
    private static final float POINTS_PER_INCH = 72.0f;
    private static final float POINTS_PER_MM = 2.8346457f;
    public static final PDRectangle LETTER = new PDRectangle(612.0f, 792.0f);
    public static final PDRectangle LEGAL = new PDRectangle(612.0f, 1008.0f);
    public static final PDRectangle A0 = new PDRectangle(2383.937f, 3370.3938f);
    public static final PDRectangle A1 = new PDRectangle(1683.7795f, 2383.937f);
    public static final PDRectangle A2 = new PDRectangle(1190.5513f, 1683.7795f);
    public static final PDRectangle A3 = new PDRectangle(841.8898f, 1190.5513f);
    public static final PDRectangle A4 = new PDRectangle(595.27563f, 841.8898f);
    public static final PDRectangle A5 = new PDRectangle(419.52756f, 595.27563f);
    public static final PDRectangle A6 = new PDRectangle(297.63782f, 419.52756f);
    private final COSArray rectArray;

    public PDRectangle() {
        this(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public PDRectangle(float width, float height) {
        this(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, height);
    }

    public PDRectangle(float x, float y, float width, float height) {
        this.rectArray = new COSArray();
        this.rectArray.add((COSBase) new COSFloat(x));
        this.rectArray.add((COSBase) new COSFloat(y));
        this.rectArray.add((COSBase) new COSFloat(x + width));
        this.rectArray.add((COSBase) new COSFloat(y + height));
    }

    public PDRectangle(BoundingBox box) {
        this.rectArray = new COSArray();
        this.rectArray.add((COSBase) new COSFloat(box.getLowerLeftX()));
        this.rectArray.add((COSBase) new COSFloat(box.getLowerLeftY()));
        this.rectArray.add((COSBase) new COSFloat(box.getUpperRightX()));
        this.rectArray.add((COSBase) new COSFloat(box.getUpperRightY()));
    }

    public PDRectangle(COSArray array) {
        float[] values = Arrays.copyOf(array.toFloatArray(), 4);
        this.rectArray = new COSArray();
        this.rectArray.add((COSBase) new COSFloat(Math.min(values[0], values[2])));
        this.rectArray.add((COSBase) new COSFloat(Math.min(values[1], values[3])));
        this.rectArray.add((COSBase) new COSFloat(Math.max(values[0], values[2])));
        this.rectArray.add((COSBase) new COSFloat(Math.max(values[1], values[3])));
    }

    public boolean contains(float x, float y) {
        float llx = getLowerLeftX();
        float urx = getUpperRightX();
        float lly = getLowerLeftY();
        float ury = getUpperRightY();
        return x >= llx && x <= urx && y >= lly && y <= ury;
    }

    public PDRectangle createRetranslatedRectangle() {
        PDRectangle retval = new PDRectangle();
        retval.setUpperRightX(getWidth());
        retval.setUpperRightY(getHeight());
        return retval;
    }

    public COSArray getCOSArray() {
        return this.rectArray;
    }

    public float getLowerLeftX() {
        return ((COSNumber) this.rectArray.get(0)).floatValue();
    }

    public void setLowerLeftX(float value) {
        this.rectArray.set(0, (COSBase) new COSFloat(value));
    }

    public float getLowerLeftY() {
        return ((COSNumber) this.rectArray.get(1)).floatValue();
    }

    public void setLowerLeftY(float value) {
        this.rectArray.set(1, (COSBase) new COSFloat(value));
    }

    public float getUpperRightX() {
        return ((COSNumber) this.rectArray.get(2)).floatValue();
    }

    public void setUpperRightX(float value) {
        this.rectArray.set(2, (COSBase) new COSFloat(value));
    }

    public float getUpperRightY() {
        return ((COSNumber) this.rectArray.get(3)).floatValue();
    }

    public void setUpperRightY(float value) {
        this.rectArray.set(3, (COSBase) new COSFloat(value));
    }

    public float getWidth() {
        return getUpperRightX() - getLowerLeftX();
    }

    public float getHeight() {
        return getUpperRightY() - getLowerLeftY();
    }

    public GeneralPath transform(Matrix matrix) {
        float x1 = getLowerLeftX();
        float y1 = getLowerLeftY();
        float x2 = getUpperRightX();
        float y2 = getUpperRightY();
        Point2D.Float p0 = matrix.transformPoint(x1, y1);
        Point2D.Float p1 = matrix.transformPoint(x2, y1);
        Point2D.Float p2 = matrix.transformPoint(x2, y2);
        Point2D.Float p3 = matrix.transformPoint(x1, y2);
        GeneralPath path = new GeneralPath();
        path.moveTo(p0.getX(), p0.getY());
        path.lineTo(p1.getX(), p1.getY());
        path.lineTo(p2.getX(), p2.getY());
        path.lineTo(p3.getX(), p3.getY());
        path.closePath();
        return path;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.rectArray;
    }

    public GeneralPath toGeneralPath() {
        float x1 = getLowerLeftX();
        float y1 = getLowerLeftY();
        float x2 = getUpperRightX();
        float y2 = getUpperRightY();
        GeneralPath path = new GeneralPath();
        path.moveTo(x1, y1);
        path.lineTo(x2, y1);
        path.lineTo(x2, y2);
        path.lineTo(x1, y2);
        path.closePath();
        return path;
    }

    public String toString() {
        return "[" + getLowerLeftX() + "," + getLowerLeftY() + "," + getUpperRightX() + "," + getUpperRightY() + "]";
    }
}
