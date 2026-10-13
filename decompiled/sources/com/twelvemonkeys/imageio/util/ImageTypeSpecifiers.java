package com.twelvemonkeys.imageio.util;

import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.Winspool;
import com.twelvemonkeys.imageio.color.DiscreteAlphaIndexColorModel;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import javax.imageio.ImageTypeSpecifier;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/ImageTypeSpecifiers.class */
public final class ImageTypeSpecifiers {
    private static final ImageTypeSpecifier TYPE_INT_RGB = createPackedOddBits(ColorSpace.getInstance(1000), 24, Winspool.PRINTER_ENUM_ICONMASK, 65280, 255, 0, 3, false);
    private static final ImageTypeSpecifier TYPE_INT_BGR = createPackedOddBits(ColorSpace.getInstance(1000), 24, 255, 65280, Winspool.PRINTER_ENUM_ICONMASK, 0, 3, false);
    private static final ImageTypeSpecifier TYPE_USHORT_565_RGB = createPackedOddBits(ColorSpace.getInstance(1000), 16, 63488, WinError.ERROR_PROFILE_NOT_FOUND, 31, 0, 1, false);
    private static final ImageTypeSpecifier TYPE_USHORT_555_RGB = createPackedOddBits(ColorSpace.getInstance(1000), 15, 31744, 992, 31, 0, 1, false);

    private ImageTypeSpecifiers() {
    }

    public static ImageTypeSpecifier createFromBufferedImageType(int i) {
        switch (i) {
            case 1:
                return TYPE_INT_RGB;
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                return ImageTypeSpecifier.createFromBufferedImageType(i);
            case 4:
                return TYPE_INT_BGR;
            case 8:
                return TYPE_USHORT_565_RGB;
            case 9:
                return TYPE_USHORT_555_RGB;
        }
    }

    public static ImageTypeSpecifier createPacked(ColorSpace colorSpace, int i, int i2, int i3, int i4, int i5, boolean z) {
        int iCalculateRequiredBits = calculateRequiredBits(i | i2 | i3 | i4);
        if (iCalculateRequiredBits != 32) {
            return createPackedOddBits(colorSpace, iCalculateRequiredBits, i, i2, i3, i4, i5, z);
        }
        return ImageTypeSpecifier.createPacked(colorSpace, i, i2, i3, i4, i5, z);
    }

    private static int calculateRequiredBits(int i) {
        int i2 = 1;
        while (true) {
            int i3 = i >>> 1;
            i = i3;
            if (i3 != 0) {
                i2++;
            } else {
                return i2;
            }
        }
    }

    static ImageTypeSpecifier createPackedOddBits(ColorSpace colorSpace, int i, int i2, int i3, int i4, int i5, int i6, boolean z) {
        Validate.notNull(colorSpace, "colorSpace");
        Validate.isTrue(colorSpace.getType() == 5, colorSpace, "ColorSpace must be TYPE_RGB");
        Validate.isTrue((i2 == 0 && i3 == 0 && i4 == 0 && i5 == 0) ? false : true, "No mask has at least 1 bit set");
        DirectColorModel directColorModel = new DirectColorModel(colorSpace, i, i2, i3, i4, i5, z, i6);
        return new ImageTypeSpecifier(directColorModel, directColorModel.createCompatibleSampleModel(1, 1));
    }

    public static ImageTypeSpecifier createInterleaved(ColorSpace colorSpace, int[] iArr, int i, boolean z, boolean z2) {
        if (i == 3) {
            return UInt32ImageTypeSpecifier.createInterleaved(colorSpace, iArr, z, z2);
        }
        return ImageTypeSpecifier.createInterleaved(colorSpace, iArr, i, z, z2);
    }

    public static ImageTypeSpecifier createBanded(ColorSpace colorSpace, int[] iArr, int[] iArr2, int i, boolean z, boolean z2) {
        if (i == 3) {
            return UInt32ImageTypeSpecifier.createBanded(colorSpace, iArr, iArr2, z, z2);
        }
        return ImageTypeSpecifier.createBanded(colorSpace, iArr, iArr2, i, z, z2);
    }

    public static ImageTypeSpecifier createGrayscale(int i, int i2) {
        if (i == 16 && i2 == 2) {
            return new Int16ImageTypeSpecifier(ColorSpace.getInstance(1003), new int[]{0}, false, false);
        }
        if (i == 32 && i2 == 3) {
            return UInt32ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0}, false, false);
        }
        return (i2 == 4 || i2 == 5) ? ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0}, i2, false, false) : ImageTypeSpecifier.createGrayscale(i, i2, false);
    }

    public static ImageTypeSpecifier createGrayscale(int i, int i2, boolean z) {
        if (i == 16 && i2 == 2) {
            return new Int16ImageTypeSpecifier(ColorSpace.getInstance(1003), new int[]{0, 1}, true, z);
        }
        if (i == 32 && i2 == 3) {
            return UInt32ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0, 1}, true, z);
        }
        return (i2 == 4 || i2 == 5) ? ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0, 1}, i2, true, z) : ImageTypeSpecifier.createGrayscale(i, i2, false, z);
    }

    public static ImageTypeSpecifier createPackedGrayscale(ColorSpace colorSpace, int i, int i2) {
        IndexColorModel indexColorModel;
        Validate.notNull(colorSpace, "colorSpace");
        Validate.isTrue(colorSpace.getType() == 6, colorSpace, "ColorSpace must be TYPE_GRAY");
        Validate.isTrue(i == 1 || i == 2 || i == 4, Integer.valueOf(i), "bits must be 1, 2, or 4: %s");
        Validate.isTrue(i2 == 0, Integer.valueOf(i2), "dataType must be TYPE_BYTE: %s");
        int i3 = 1 << i;
        if (ColorSpace.getInstance(1003).equals(colorSpace)) {
            byte[] bArr = new byte[i3];
            for (int i4 = 0; i4 < i3; i4++) {
                bArr[i4] = (byte) ((i4 * 255) / (i3 - 1));
            }
            indexColorModel = new IndexColorModel(i, i3, bArr, bArr, bArr);
        } else {
            byte[] bArr2 = new byte[i3];
            byte[] bArr3 = new byte[i3];
            byte[] bArr4 = new byte[i3];
            for (int i5 = 0; i5 < i3; i5++) {
                float[] rgb = colorSpace.toRGB(new float[]{i5 / (i3 - 1)});
                bArr2[i5] = (byte) Math.round(rgb[0] * 255.0f);
                bArr3[i5] = (byte) Math.round(rgb[1] * 255.0f);
                bArr4[i5] = (byte) Math.round(rgb[2] * 255.0f);
            }
            indexColorModel = new IndexColorModel(i, i3, bArr2, bArr3, bArr4);
        }
        return new ImageTypeSpecifier(indexColorModel, new MultiPixelPackedSampleModel(i2, 1, 1, i));
    }

    public static ImageTypeSpecifier createIndexed(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, int i, int i2) {
        return ImageTypeSpecifier.createIndexed(bArr, bArr2, bArr3, bArr4, i, i2);
    }

    public static ImageTypeSpecifier createIndexed(int[] iArr, boolean z, int i, int i2, int i3) {
        return createFromIndexColorModel(new IndexColorModel(i2, iArr.length, iArr, 0, z, i, i3));
    }

    public static ImageTypeSpecifier createFromIndexColorModel(IndexColorModel indexColorModel) {
        return new IndexedImageTypeSpecifier(indexColorModel);
    }

    public static ImageTypeSpecifier createDiscreteAlphaIndexedFromIndexColorModel(IndexColorModel indexColorModel) {
        DiscreteAlphaIndexColorModel discreteAlphaIndexColorModel = new DiscreteAlphaIndexColorModel(indexColorModel);
        return new ImageTypeSpecifier(discreteAlphaIndexColorModel, discreteAlphaIndexColorModel.createCompatibleSampleModel(1, 1));
    }

    public static ImageTypeSpecifier createDiscreteExtraSamplesIndexedFromIndexColorModel(IndexColorModel indexColorModel, int i, boolean z) {
        DiscreteAlphaIndexColorModel discreteAlphaIndexColorModel = new DiscreteAlphaIndexColorModel(indexColorModel, i, z);
        return new ImageTypeSpecifier(discreteAlphaIndexColorModel, discreteAlphaIndexColorModel.createCompatibleSampleModel(1, 1));
    }

    public static ImageTypeSpecifier createFromRenderedImage(RenderedImage renderedImage) {
        int type;
        if (renderedImage == null) {
            throw new IllegalArgumentException("image == null!");
        }
        if ((renderedImage instanceof BufferedImage) && (type = ((BufferedImage) renderedImage).getType()) != 0 && type != 12 && type != 13) {
            return createFromBufferedImageType(type);
        }
        return new ImageTypeSpecifier(renderedImage);
    }
}
