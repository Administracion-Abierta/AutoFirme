package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.Validate;
import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/DiscreteAlphaIndexColorModel.class */
public final class DiscreteAlphaIndexColorModel extends ColorModel {
    private final IndexColorModel icm;
    private final int extraSamples;
    private final int samples;

    public DiscreteAlphaIndexColorModel(IndexColorModel indexColorModel) {
        this(indexColorModel, 1, true);
    }

    public DiscreteAlphaIndexColorModel(IndexColorModel indexColorModel, int i, boolean z) {
        super(((IndexColorModel) Validate.notNull(indexColorModel, "IndexColorModel")).getPixelSize() * (1 + i), new int[]{indexColorModel.getPixelSize(), indexColorModel.getPixelSize(), indexColorModel.getPixelSize(), indexColorModel.getPixelSize()}, indexColorModel.getColorSpace(), z, false, z ? 3 : 1, indexColorModel.getTransferType());
        this.icm = indexColorModel;
        this.extraSamples = i;
        this.samples = 1 + i;
    }

    public int getNumComponents() {
        return getNumColorComponents() + this.extraSamples;
    }

    public int getRed(int i) {
        return this.icm.getRed(i);
    }

    public int getGreen(int i) {
        return this.icm.getGreen(i);
    }

    public int getBlue(int i) {
        return this.icm.getBlue(i);
    }

    public int getAlpha(int i) {
        if (hasAlpha()) {
            return (int) (((i / ((1 << getComponentSize(3)) - 1)) * 255.0f) + 0.5f);
        }
        return 255;
    }

    private int getSample(Object obj, int i) {
        int i2;
        switch (this.transferType) {
            case 0:
                i2 = ((byte[]) obj)[i] & 255;
                break;
            case 1:
                i2 = ((short[]) obj)[i] & 65535;
                break;
            case 2:
            default:
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            case 3:
                i2 = ((int[]) obj)[i];
                break;
        }
        return i2;
    }

    public int getRed(Object obj) {
        return getRed(getSample(obj, 0));
    }

    public int getGreen(Object obj) {
        return getGreen(getSample(obj, 0));
    }

    public int getBlue(Object obj) {
        return getBlue(getSample(obj, 0));
    }

    public int getAlpha(Object obj) {
        if (hasAlpha()) {
            return getAlpha(getSample(obj, 1));
        }
        return 255;
    }

    public SampleModel createCompatibleSampleModel(int i, int i2) {
        return new PixelInterleavedSampleModel(this.transferType, i, i2, this.samples, i * this.samples, createOffsets(this.samples));
    }

    private int[] createOffsets(int i) {
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = i2;
        }
        return iArr;
    }

    public boolean isCompatibleSampleModel(SampleModel sampleModel) {
        return (sampleModel instanceof PixelInterleavedSampleModel) && sampleModel.getNumBands() == this.samples;
    }

    public WritableRaster createCompatibleWritableRaster(int i, int i2) {
        return Raster.createWritableRaster(createCompatibleSampleModel(i, i2), new Point(0, 0));
    }

    public boolean isCompatibleRaster(Raster raster) {
        return raster.getTransferType() == this.transferType && raster.getNumBands() == this.samples && (1 << raster.getSampleModel().getSampleSize(0)) >= this.icm.getMapSize();
    }

    public boolean equals(Object obj) {
        return this == obj || (obj != null && getClass() == obj.getClass() && this.icm.equals(((DiscreteAlphaIndexColorModel) obj).icm));
    }

    public String toString() {
        return "DiscreteAlphaIndexColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + getNumComponents() + " color space = " + getColorSpace() + " transparency = " + getTransparency() + " has alpha = " + hasAlpha() + " isAlphaPre = " + isAlphaPremultiplied();
    }
}
