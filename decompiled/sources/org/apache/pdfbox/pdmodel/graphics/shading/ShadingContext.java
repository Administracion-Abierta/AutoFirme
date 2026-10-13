package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/ShadingContext.class */
public abstract class ShadingContext {
    private float[] background;
    private int rgbBackground;
    private final PDShading shading;
    private ColorModel outputColorModel;
    private PDColorSpace shadingColorSpace;

    public ShadingContext(PDShading shading, ColorModel cm, AffineTransform xform, Matrix matrix) throws IOException {
        this.shading = shading;
        this.shadingColorSpace = shading.getColorSpace();
        ColorSpace outputCS = ColorSpace.getInstance(1000);
        this.outputColorModel = new ComponentColorModel(outputCS, true, false, 3, 0);
        COSArray bg = shading.getBackground();
        if (bg != null) {
            this.background = bg.toFloatArray();
            this.rgbBackground = convertToRGB(this.background);
        }
    }

    PDColorSpace getShadingColorSpace() {
        return this.shadingColorSpace;
    }

    PDShading getShading() {
        return this.shading;
    }

    float[] getBackground() {
        return this.background;
    }

    int getRgbBackground() {
        return this.rgbBackground;
    }

    final int convertToRGB(float[] values) throws IOException {
        float[] rgbValues = this.shadingColorSpace.toRGB(values);
        int normRGBValues = (int) (rgbValues[0] * 255.0f);
        return normRGBValues | (((int) (rgbValues[1] * 255.0f)) << 8) | (((int) (rgbValues[2] * 255.0f)) << 16);
    }

    ColorModel getColorModel() {
        return this.outputColorModel;
    }

    void dispose() {
        this.outputColorModel = null;
        this.shadingColorSpace = null;
    }
}
