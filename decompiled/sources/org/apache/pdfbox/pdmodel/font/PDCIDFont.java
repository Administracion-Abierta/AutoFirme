package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDFont.class */
public abstract class PDCIDFont implements COSObjectable, PDFontLike, PDVectorFont {
    private static final Log LOG = LogFactory.getLog(PDCIDFont.class);
    protected final PDType0Font parent;
    private Map<Integer, Float> widths;
    private float defaultWidth;
    private float averageWidth;
    private final Map<Integer, Float> verticalDisplacementY = new HashMap();
    private final Map<Integer, Vector> positionVectors = new HashMap();
    private float[] dw2 = {880.0f, -1000.0f};
    protected final COSDictionary dict;
    private PDFontDescriptor fontDescriptor;

    public abstract int codeToCID(int i);

    public abstract int codeToGID(int i) throws IOException;

    protected abstract byte[] encode(int i) throws IOException;

    PDCIDFont(COSDictionary fontDictionary, PDType0Font parent) {
        this.dict = fontDictionary;
        this.parent = parent;
        readWidths();
        readVerticalDisplacements();
    }

    private void readWidths() {
        this.widths = new HashMap();
        COSBase wBase = this.dict.getDictionaryObject(COSName.W);
        if (wBase instanceof COSArray) {
            COSArray wArray = (COSArray) wBase;
            int size = wArray.size();
            int counter = 0;
            while (counter < size) {
                int i = counter;
                counter++;
                COSBase firstCodeBase = wArray.getObject(i);
                if (!(firstCodeBase instanceof COSNumber)) {
                    LOG.warn("Expected a number array member, got " + firstCodeBase);
                } else {
                    COSNumber firstCode = (COSNumber) firstCodeBase;
                    counter++;
                    COSBase next = wArray.getObject(counter);
                    if (next instanceof COSArray) {
                        COSArray array = (COSArray) next;
                        int startRange = firstCode.intValue();
                        int arraySize = array.size();
                        for (int i2 = 0; i2 < arraySize; i2++) {
                            COSBase widthBase = array.getObject(i2);
                            if (widthBase instanceof COSNumber) {
                                COSNumber width = (COSNumber) widthBase;
                                this.widths.put(Integer.valueOf(startRange + i2), Float.valueOf(width.floatValue()));
                            } else {
                                LOG.warn("Expected a number array member, got " + widthBase);
                            }
                        }
                    } else {
                        counter++;
                        COSBase rangeWidthBase = wArray.getObject(counter);
                        if (!(next instanceof COSNumber) || !(rangeWidthBase instanceof COSNumber)) {
                            LOG.warn("Expected two numbers, got " + next + " and " + rangeWidthBase);
                        } else {
                            COSNumber secondCode = (COSNumber) next;
                            COSNumber rangeWidth = (COSNumber) rangeWidthBase;
                            int startRange2 = firstCode.intValue();
                            int endRange = secondCode.intValue();
                            float width2 = rangeWidth.floatValue();
                            for (int i3 = startRange2; i3 <= endRange; i3++) {
                                this.widths.put(Integer.valueOf(i3), Float.valueOf(width2));
                            }
                        }
                    }
                }
            }
        }
    }

    private void readVerticalDisplacements() {
        COSBase dw2Base = this.dict.getDictionaryObject(COSName.DW2);
        if (dw2Base instanceof COSArray) {
            COSArray dw2Array = (COSArray) dw2Base;
            COSBase base0 = dw2Array.getObject(0);
            COSBase base1 = dw2Array.getObject(1);
            if ((base0 instanceof COSNumber) && (base1 instanceof COSNumber)) {
                this.dw2[0] = ((COSNumber) base0).floatValue();
                this.dw2[1] = ((COSNumber) base1).floatValue();
            }
        }
        COSBase w2Base = this.dict.getDictionaryObject(COSName.W2);
        if (w2Base instanceof COSArray) {
            COSArray w2Array = (COSArray) w2Base;
            int i = 0;
            while (i < w2Array.size()) {
                COSNumber c = (COSNumber) w2Array.getObject(i);
                int i2 = i + 1;
                COSBase next = w2Array.getObject(i2);
                if (next instanceof COSArray) {
                    COSArray array = (COSArray) next;
                    int j = 0;
                    while (j < array.size()) {
                        int cid = c.intValue() + (j / 3);
                        COSNumber w1y = (COSNumber) array.getObject(j);
                        int j2 = j + 1;
                        COSNumber v1x = (COSNumber) array.getObject(j2);
                        int j3 = j2 + 1;
                        COSNumber v1y = (COSNumber) array.getObject(j3);
                        this.verticalDisplacementY.put(Integer.valueOf(cid), Float.valueOf(w1y.floatValue()));
                        this.positionVectors.put(Integer.valueOf(cid), new Vector(v1x.floatValue(), v1y.floatValue()));
                        j = j3 + 1;
                    }
                } else {
                    int first = c.intValue();
                    int last = ((COSNumber) next).intValue();
                    int i3 = i2 + 1;
                    COSNumber w1y2 = (COSNumber) w2Array.getObject(i3);
                    int i4 = i3 + 1;
                    COSNumber v1x2 = (COSNumber) w2Array.getObject(i4);
                    i2 = i4 + 1;
                    COSNumber v1y2 = (COSNumber) w2Array.getObject(i2);
                    for (int cid2 = first; cid2 <= last; cid2++) {
                        this.verticalDisplacementY.put(Integer.valueOf(cid2), Float.valueOf(w1y2.floatValue()));
                        this.positionVectors.put(Integer.valueOf(cid2), new Vector(v1x2.floatValue(), v1y2.floatValue()));
                    }
                }
                i = i2 + 1;
            }
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    public String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public String getName() {
        return getBaseFont();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public PDFontDescriptor getFontDescriptor() {
        COSDictionary fd;
        if (this.fontDescriptor == null && (fd = (COSDictionary) this.dict.getDictionaryObject(COSName.FONT_DESC)) != null) {
            this.fontDescriptor = new PDFontDescriptor(fd);
        }
        return this.fontDescriptor;
    }

    public final PDType0Font getParent() {
        return this.parent;
    }

    private float getDefaultWidth() {
        if (this.defaultWidth == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            COSBase base = this.dict.getDictionaryObject(COSName.DW);
            if (base instanceof COSNumber) {
                this.defaultWidth = ((COSNumber) base).floatValue();
            } else {
                this.defaultWidth = 1000.0f;
            }
        }
        return this.defaultWidth;
    }

    private Vector getDefaultPositionVector(int cid) {
        return new Vector(getWidthForCID(cid) / 2.0f, this.dw2[0]);
    }

    private float getWidthForCID(int cid) {
        Float width = this.widths.get(Integer.valueOf(cid));
        if (width == null) {
            width = Float.valueOf(getDefaultWidth());
        }
        return width.floatValue();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean hasExplicitWidth(int code) throws IOException {
        return this.widths.get(Integer.valueOf(codeToCID(code))) != null;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public Vector getPositionVector(int code) {
        int cid = codeToCID(code);
        Vector v = this.positionVectors.get(Integer.valueOf(cid));
        if (v == null) {
            v = getDefaultPositionVector(cid);
        }
        return v;
    }

    public float getVerticalDisplacementVectorY(int code) {
        int cid = codeToCID(code);
        Float w1y = this.verticalDisplacementY.get(Integer.valueOf(cid));
        if (w1y == null) {
            w1y = Float.valueOf(this.dw2[1]);
        }
        return w1y.floatValue();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidth(int code) throws IOException {
        return getWidthForCID(codeToCID(code));
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getAverageFontWidth() {
        if (this.averageWidth == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            float totalWidths = 0.0f;
            int characterCount = 0;
            if (this.widths != null) {
                for (Float width : this.widths.values()) {
                    if (width.floatValue() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        totalWidths += width.floatValue();
                        characterCount++;
                    }
                }
            }
            if (characterCount != 0) {
                this.averageWidth = totalWidths / characterCount;
            }
            if (this.averageWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO || Float.isNaN(this.averageWidth)) {
                this.averageWidth = getDefaultWidth();
            }
        }
        return this.averageWidth;
    }

    public PDCIDSystemInfo getCIDSystemInfo() {
        COSBase base = this.dict.getDictionaryObject(COSName.CIDSYSTEMINFO);
        if (base instanceof COSDictionary) {
            return new PDCIDSystemInfo((COSDictionary) base);
        }
        return null;
    }

    final int[] readCIDToGIDMap() throws IOException {
        int[] cid2gid = null;
        COSBase map = this.dict.getDictionaryObject(COSName.CID_TO_GID_MAP);
        if (map instanceof COSStream) {
            COSStream stream = (COSStream) map;
            InputStream is = stream.createInputStream();
            byte[] mapAsBytes = IOUtils.toByteArray(is);
            IOUtils.closeQuietly(is);
            int numberOfInts = mapAsBytes.length / 2;
            cid2gid = new int[numberOfInts];
            int offset = 0;
            for (int index = 0; index < numberOfInts; index++) {
                int gid = ((mapAsBytes[offset] & 255) << 8) | (mapAsBytes[offset + 1] & 255);
                cid2gid[index] = gid;
                offset += 2;
            }
        }
        return cid2gid;
    }
}
