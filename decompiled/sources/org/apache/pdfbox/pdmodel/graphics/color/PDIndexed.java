package org.apache.pdfbox.pdmodel.graphics.color;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDIndexed.class */
public final class PDIndexed extends PDSpecialColorSpace {
    private final PDColor initialColor;
    private PDColorSpace baseColorSpace;
    private byte[] lookupData;
    private float[][] colorTable;
    private int actualMaxIndex;
    private int[][] rgbColorTable;

    public PDIndexed() {
        this.initialColor = new PDColor(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, this);
        this.baseColorSpace = null;
        this.array = new COSArray();
        this.array.add((COSBase) COSName.INDEXED);
        this.array.add((COSBase) COSName.DEVICERGB);
        this.array.add((COSBase) COSInteger.get(255L));
        this.array.add((COSBase) COSNull.NULL);
    }

    public PDIndexed(COSArray indexedArray) throws IOException {
        this(indexedArray, null);
    }

    public PDIndexed(COSArray indexedArray, PDResources resources) throws IOException {
        this.initialColor = new PDColor(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, this);
        this.baseColorSpace = null;
        this.array = indexedArray;
        this.baseColorSpace = PDColorSpace.create(this.array.get(1), resources);
        readColorTable();
        initRgbColorTable();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public String getName() {
        return COSName.INDEXED.getName();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public int getNumberOfComponents() {
        return 1;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, ((float) Math.pow(2.0d, bitsPerComponent)) - 1.0f};
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    private void initRgbColorTable() throws IOException {
        int numBaseComponents = this.baseColorSpace.getNumberOfComponents();
        try {
            WritableRaster baseRaster = Raster.createBandedRaster(0, this.actualMaxIndex + 1, 1, numBaseComponents, new Point(0, 0));
            int[] base = new int[numBaseComponents];
            int n = this.actualMaxIndex;
            for (int i = 0; i <= n; i++) {
                for (int c = 0; c < numBaseComponents; c++) {
                    base[c] = (int) (this.colorTable[i][c] * 255.0f);
                }
                baseRaster.setPixel(i, 0, base);
            }
            BufferedImage rgbImage = this.baseColorSpace.toRGBImage(baseRaster);
            WritableRaster rgbRaster = rgbImage.getRaster();
            this.rgbColorTable = new int[this.actualMaxIndex + 1][3];
            int n2 = this.actualMaxIndex;
            for (int i2 = 0; i2 <= n2; i2++) {
                this.rgbColorTable[i2] = rgbRaster.getPixel(i2, 0, (int[]) null);
            }
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] toRGB(float[] value) {
        if (value.length > 1) {
            throw new IllegalArgumentException("Indexed color spaces must have one color value");
        }
        int index = Math.round(value[0]);
        int[] rgb = this.rgbColorTable[Math.min(Math.max(index, 0), this.actualMaxIndex)];
        return new float[]{rgb[0] / 255.0f, rgb[1] / 255.0f, rgb[2] / 255.0f};
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        int[] src = new int[1];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, src);
                int index = Math.min(src[0], this.actualMaxIndex);
                rgbRaster.setPixel(x, y, this.rgbColorTable[index]);
            }
        }
        return rgbImage;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRawImage(WritableRaster raster) {
        if ((this.baseColorSpace instanceof PDICCBased) && ((PDICCBased) this.baseColorSpace).isSRGB()) {
            byte[] r = new byte[this.colorTable.length];
            byte[] g = new byte[this.colorTable.length];
            byte[] b = new byte[this.colorTable.length];
            for (int i = 0; i < this.colorTable.length; i++) {
                r[i] = (byte) (((int) (this.colorTable[i][0] * 255.0f)) & 255);
                g[i] = (byte) (((int) (this.colorTable[i][1] * 255.0f)) & 255);
                b[i] = (byte) (((int) (this.colorTable[i][2] * 255.0f)) & 255);
            }
            return new BufferedImage(new IndexColorModel(8, this.colorTable.length, r, g, b), raster, false, (Hashtable) null);
        }
        return null;
    }

    public PDColorSpace getBaseColorSpace() {
        return this.baseColorSpace;
    }

    private int getHival() {
        return ((COSNumber) this.array.getObject(2)).intValue();
    }

    private void readLookupData() throws IOException {
        if (this.lookupData == null) {
            COSBase lookupTable = this.array.getObject(3);
            if (lookupTable instanceof COSString) {
                this.lookupData = ((COSString) lookupTable).getBytes();
            } else if (lookupTable instanceof COSStream) {
                this.lookupData = new PDStream((COSStream) lookupTable).toByteArray();
            } else {
                if (lookupTable == null) {
                    this.lookupData = new byte[0];
                    return;
                }
                throw new IOException("Error: Unknown type for lookup table " + lookupTable);
            }
        }
    }

    private void readColorTable() throws IOException {
        readLookupData();
        int maxIndex = Math.min(getHival(), 255);
        int numComponents = this.baseColorSpace.getNumberOfComponents();
        if (this.lookupData.length / numComponents < maxIndex + 1) {
            maxIndex = (this.lookupData.length / numComponents) - 1;
        }
        this.actualMaxIndex = maxIndex;
        this.colorTable = new float[maxIndex + 1][numComponents];
        int offset = 0;
        for (int i = 0; i <= maxIndex; i++) {
            for (int c = 0; c < numComponents; c++) {
                this.colorTable[i][c] = (this.lookupData[offset] & 255) / 255.0f;
                offset++;
            }
        }
    }

    public void setBaseColorSpace(PDColorSpace base) {
        this.array.set(1, base.getCOSObject());
        this.baseColorSpace = base;
    }

    public void setHighValue(int high) {
        this.array.set(2, high);
    }

    public String toString() {
        return "Indexed{base:" + this.baseColorSpace + " hival:" + getHival() + " lookup:(" + this.colorTable.length + " entries)}";
    }
}
