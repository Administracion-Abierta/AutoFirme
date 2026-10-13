package org.apache.pdfbox.pdmodel.graphics.color;

import java.io.IOException;
import java.util.Arrays;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDColor.class */
public final class PDColor {
    private final float[] components;
    private final COSName patternName;
    private final PDColorSpace colorSpace;

    public PDColor(COSArray array, PDColorSpace colorSpace) {
        if (array.size() > 0 && (array.get(array.size() - 1) instanceof COSName)) {
            this.components = new float[array.size() - 1];
            for (int i = 0; i < this.components.length; i++) {
                this.components[i] = ((COSNumber) array.get(i)).floatValue();
            }
            this.patternName = (COSName) array.get(array.size() - 1);
        } else {
            this.components = new float[array.size()];
            for (int i2 = 0; i2 < array.size(); i2++) {
                this.components[i2] = ((COSNumber) array.get(i2)).floatValue();
            }
            this.patternName = null;
        }
        this.colorSpace = colorSpace;
    }

    public PDColor(float[] components, PDColorSpace colorSpace) {
        this.components = (float[]) components.clone();
        this.patternName = null;
        this.colorSpace = colorSpace;
    }

    public PDColor(COSName patternName, PDColorSpace colorSpace) {
        this.components = new float[0];
        this.patternName = patternName;
        this.colorSpace = colorSpace;
    }

    public PDColor(float[] components, COSName patternName, PDColorSpace colorSpace) {
        this.components = (float[]) components.clone();
        this.patternName = patternName;
        this.colorSpace = colorSpace;
    }

    public float[] getComponents() {
        if ((this.colorSpace instanceof PDPattern) || this.colorSpace == null) {
            return (float[]) this.components.clone();
        }
        return Arrays.copyOf(this.components, this.colorSpace.getNumberOfComponents());
    }

    public COSName getPatternName() {
        return this.patternName;
    }

    public boolean isPattern() {
        return this.patternName != null;
    }

    public int toRGB() throws IOException {
        float[] floats = this.colorSpace.toRGB(this.components);
        int r = Math.round(floats[0] * 255.0f);
        int g = Math.round(floats[1] * 255.0f);
        int b = Math.round(floats[2] * 255.0f);
        int rgb = (r << 8) + g;
        return (rgb << 8) + b;
    }

    public COSArray toCOSArray() {
        COSArray array = new COSArray();
        array.setFloatArray(this.components);
        if (this.patternName != null) {
            array.add((COSBase) this.patternName);
        }
        return array;
    }

    public PDColorSpace getColorSpace() {
        return this.colorSpace;
    }

    public String toString() {
        return "PDColor{components=" + Arrays.toString(this.components) + ", patternName=" + this.patternName + "}";
    }
}
