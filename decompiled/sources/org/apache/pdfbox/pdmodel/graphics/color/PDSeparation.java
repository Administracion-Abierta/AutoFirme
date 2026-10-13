package org.apache.pdfbox.pdmodel.graphics.color;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDSeparation.class */
public class PDSeparation extends PDSpecialColorSpace {
    private final PDColor initialColor;
    private static final int COLORANT_NAMES = 1;
    private static final int ALTERNATE_CS = 2;
    private static final int TINT_TRANSFORM = 3;
    private PDColorSpace alternateColorSpace;
    private PDFunction tintTransform;
    private Map<Integer, float[]> toRGBMap;

    public PDSeparation() {
        this.initialColor = new PDColor(new float[]{1.0f}, this);
        this.alternateColorSpace = null;
        this.tintTransform = null;
        this.toRGBMap = null;
        this.array = new COSArray();
        this.array.add((COSBase) COSName.SEPARATION);
        this.array.add((COSBase) COSName.getPDFName(""));
        this.array.add((COSBase) COSNull.NULL);
        this.array.add((COSBase) COSNull.NULL);
    }

    public PDSeparation(COSArray separation) throws IOException {
        this.initialColor = new PDColor(new float[]{1.0f}, this);
        this.alternateColorSpace = null;
        this.tintTransform = null;
        this.toRGBMap = null;
        this.array = separation;
        this.alternateColorSpace = PDColorSpace.create(this.array.getObject(2));
        this.tintTransform = PDFunction.create(this.array.getObject(3));
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public String getName() {
        return COSName.SEPARATION.getName();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public int getNumberOfComponents() {
        return 1;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f};
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] toRGB(float[] value) throws IOException {
        if (this.toRGBMap == null) {
            this.toRGBMap = new HashMap();
        }
        int key = (int) (value[0] * 255.0f);
        float[] retval = this.toRGBMap.get(Integer.valueOf(key));
        if (retval != null) {
            return retval;
        }
        float[] altColor = this.tintTransform.eval(value);
        float[] retval2 = this.alternateColorSpace.toRGB(altColor);
        this.toRGBMap.put(Integer.valueOf(key), retval2);
        return retval2;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        if (this.alternateColorSpace instanceof PDLab) {
            return toRGBImage2(raster);
        }
        WritableRaster altRaster = Raster.createBandedRaster(0, raster.getWidth(), raster.getHeight(), this.alternateColorSpace.getNumberOfComponents(), new Point(0, 0));
        int numAltComponents = this.alternateColorSpace.getNumberOfComponents();
        int width = raster.getWidth();
        int height = raster.getHeight();
        float[] samples = new float[1];
        Map<Integer, int[]> calculatedValues = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, samples);
                Integer hash = Integer.valueOf(Float.floatToIntBits(samples[0]));
                int[] alt = calculatedValues.get(hash);
                if (alt == null) {
                    alt = new int[numAltComponents];
                    tintTransform(samples, alt);
                    calculatedValues.put(hash, alt);
                }
                altRaster.setPixel(x, y, alt);
            }
        }
        return this.alternateColorSpace.toRGBImage(altRaster);
    }

    private BufferedImage toRGBImage2(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        float[] samples = new float[1];
        Map<Integer, int[]> calculatedValues = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, samples);
                Integer hash = Integer.valueOf(Float.floatToIntBits(samples[0]));
                int[] rgb = calculatedValues.get(hash);
                if (rgb == null) {
                    samples[0] = samples[0] / 255.0f;
                    float[] altColor = this.tintTransform.eval(samples);
                    float[] fltab = this.alternateColorSpace.toRGB(altColor);
                    rgb = new int[]{(int) (fltab[0] * 255.0f), (int) (fltab[1] * 255.0f), (int) (fltab[2] * 255.0f)};
                    calculatedValues.put(hash, rgb);
                }
                rgbRaster.setPixel(x, y, rgb);
            }
        }
        return rgbImage;
    }

    protected void tintTransform(float[] samples, int[] alt) throws IOException {
        samples[0] = samples[0] / 255.0f;
        float[] result = this.tintTransform.eval(samples);
        for (int s = 0; s < alt.length; s++) {
            alt[s] = (int) (result[s] * 255.0f);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRawImage(WritableRaster raster) {
        return toRawImage(raster, ColorSpace.getInstance(1003));
    }

    public PDColorSpace getAlternateColorSpace() {
        return this.alternateColorSpace;
    }

    public String getColorantName() {
        COSName name = (COSName) this.array.getObject(1);
        return name.getName();
    }

    public void setColorantName(String name) {
        this.array.set(1, (COSBase) COSName.getPDFName(name));
    }

    public void setAlternateColorSpace(PDColorSpace colorSpace) {
        this.alternateColorSpace = colorSpace;
        COSBase space = null;
        if (colorSpace != null) {
            space = colorSpace.getCOSObject();
        }
        this.array.set(2, space);
    }

    public void setTintTransform(PDFunction tint) {
        this.tintTransform = tint;
        this.array.set(3, tint);
    }

    public String toString() {
        return getName() + "{\"" + getColorantName() + "\" " + this.alternateColorSpace.getName() + " " + this.tintTransform + "}";
    }
}
