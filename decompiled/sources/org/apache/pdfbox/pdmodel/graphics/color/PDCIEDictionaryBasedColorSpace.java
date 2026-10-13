package org.apache.pdfbox.pdmodel.graphics.color;

import com.aowagie.text.pdf.ColumnText;
import java.awt.color.ColorSpace;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDCIEDictionaryBasedColorSpace.class */
public abstract class PDCIEDictionaryBasedColorSpace extends PDCIEBasedColorSpace {
    protected COSDictionary dictionary;
    private static final ColorSpace CIEXYZ = ColorSpace.getInstance(1001);
    protected float wpX = 1.0f;
    protected float wpY = 1.0f;
    protected float wpZ = 1.0f;

    protected PDCIEDictionaryBasedColorSpace(COSName cosName) {
        this.array = new COSArray();
        this.dictionary = new COSDictionary();
        this.array.add((COSBase) cosName);
        this.array.add((COSBase) this.dictionary);
        fillWhitepointCache(getWhitepoint());
    }

    protected PDCIEDictionaryBasedColorSpace(COSArray rgb) {
        this.array = rgb;
        this.dictionary = (COSDictionary) this.array.getObject(1);
        fillWhitepointCache(getWhitepoint());
    }

    private void fillWhitepointCache(PDTristimulus whitepoint) {
        this.wpX = whitepoint.getX();
        this.wpY = whitepoint.getY();
        this.wpZ = whitepoint.getZ();
    }

    protected float[] convXYZtoRGB(float x, float y, float z) {
        if (x < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            x = 0.0f;
        }
        if (y < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            y = 0.0f;
        }
        if (z < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            z = 0.0f;
        }
        return CIEXYZ.toRGB(new float[]{x, y, z});
    }

    public final PDTristimulus getWhitepoint() {
        COSArray wp = (COSArray) this.dictionary.getDictionaryObject(COSName.WHITE_POINT);
        if (wp == null) {
            wp = new COSArray();
            wp.add((COSBase) new COSFloat(1.0f));
            wp.add((COSBase) new COSFloat(1.0f));
            wp.add((COSBase) new COSFloat(1.0f));
        }
        return new PDTristimulus(wp);
    }

    public final PDTristimulus getBlackPoint() {
        COSArray bp = (COSArray) this.dictionary.getDictionaryObject(COSName.BLACK_POINT);
        if (bp == null) {
            bp = new COSArray();
            bp.add((COSBase) new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            bp.add((COSBase) new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            bp.add((COSBase) new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        }
        return new PDTristimulus(bp);
    }

    public void setWhitePoint(PDTristimulus whitepoint) {
        if (whitepoint == null) {
            throw new IllegalArgumentException("Whitepoint may not be null");
        }
        this.dictionary.setItem(COSName.WHITE_POINT, whitepoint);
        fillWhitepointCache(whitepoint);
    }

    public void setBlackPoint(PDTristimulus blackpoint) {
        this.dictionary.setItem(COSName.BLACK_POINT, blackpoint);
    }
}
