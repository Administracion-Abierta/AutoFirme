package org.apache.pdfbox.pdmodel.graphics.pattern;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/pattern/PDTilingPattern.class */
public class PDTilingPattern extends PDAbstractPattern implements PDContentStream {
    public static final int PAINT_COLORED = 1;
    public static final int PAINT_UNCOLORED = 2;
    public static final int TILING_CONSTANT_SPACING = 1;
    public static final int TILING_NO_DISTORTION = 2;
    public static final int TILING_CONSTANT_SPACING_FASTER_TILING = 3;
    private final ResourceCache resourceCache;

    public PDTilingPattern() {
        super(new COSStream());
        getCOSObject().setName(COSName.TYPE, COSName.PATTERN.getName());
        getCOSObject().setInt(COSName.PATTERN_TYPE, 1);
        setResources(new PDResources());
        this.resourceCache = null;
    }

    public PDTilingPattern(COSDictionary dictionary) {
        this(dictionary, null);
    }

    public PDTilingPattern(COSDictionary dictionary, ResourceCache resourceCache) {
        super(dictionary);
        this.resourceCache = resourceCache;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern
    public int getPatternType() {
        return 1;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern
    public void setPaintType(int paintType) {
        getCOSObject().setInt(COSName.PAINT_TYPE, paintType);
    }

    public int getPaintType() {
        return getCOSObject().getInt(COSName.PAINT_TYPE, 0);
    }

    public void setTilingType(int tilingType) {
        getCOSObject().setInt(COSName.TILING_TYPE, tilingType);
    }

    public int getTilingType() {
        return getCOSObject().getInt(COSName.TILING_TYPE, 0);
    }

    public void setXStep(float xStep) {
        getCOSObject().setFloat(COSName.X_STEP, xStep);
    }

    public float getXStep() {
        return getCOSObject().getFloat(COSName.X_STEP, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setYStep(float yStep) {
        getCOSObject().setFloat(COSName.Y_STEP, yStep);
    }

    public float getYStep() {
        return getCOSObject().getFloat(COSName.Y_STEP, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public PDStream getContentStream() {
        return new PDStream((COSStream) getCOSObject());
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public InputStream getContents() throws IOException {
        COSDictionary dict = getCOSObject();
        if (dict instanceof COSStream) {
            return ((COSStream) getCOSObject()).createInputStream();
        }
        return null;
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public PDResources getResources() {
        PDResources retval = null;
        COSBase base = getCOSObject().getDictionaryObject(COSName.RESOURCES);
        if (base instanceof COSDictionary) {
            retval = new PDResources((COSDictionary) base);
        }
        return retval;
    }

    public final void setResources(PDResources resources) {
        getCOSObject().setItem(COSName.RESOURCES, resources);
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public PDRectangle getBBox() {
        PDRectangle retval = null;
        COSBase base = getCOSObject().getDictionaryObject(COSName.BBOX);
        if (base instanceof COSArray) {
            retval = new PDRectangle((COSArray) base);
        }
        return retval;
    }

    public void setBBox(PDRectangle bbox) {
        if (bbox == null) {
            getCOSObject().removeItem(COSName.BBOX);
        } else {
            getCOSObject().setItem(COSName.BBOX, (COSBase) bbox.getCOSArray());
        }
    }
}
