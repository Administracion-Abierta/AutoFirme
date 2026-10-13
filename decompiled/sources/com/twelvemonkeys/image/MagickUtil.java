package com.twelvemonkeys.image;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.util.Hashtable;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/MagickUtil.class */
public final class MagickUtil {
    private static final IndexColorModel CM_MONOCHROME = MonochromeColorModel.getInstance();
    private static final ColorModel CM_COLOR_ALPHA = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{8, 8, 8, 8}, true, true, 3, 0);
    private static final ColorModel CM_COLOR_OPAQUE = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{8, 8, 8}, false, false, 1, 0);
    private static final ColorModel CM_GRAY_ALPHA = new ComponentColorModel(ColorSpace.getInstance(1003), true, true, 3, 0);
    private static final ColorModel CM_GRAY_OPAQUE = new ComponentColorModel(ColorSpace.getInstance(1003), false, false, 1, 0);
    private static final int[] BAND_OFF_TRANS = {3, 2, 1, 0};
    private static final int[] BAND_OFF_OPAQUE = {2, 1, 0};
    private static final Point LOCATION_UPPER_LEFT = new Point(0, 0);
    private static final boolean DEBUG = Magick.DEBUG;

    private MagickUtil() {
    }

    public static BufferedImage toBuffered(MagickImage magickImage) throws MagickException {
        BufferedImage bufferedImageCmykToBuffered;
        if (magickImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        long jCurrentTimeMillis = 0;
        if (DEBUG) {
            jCurrentTimeMillis = System.currentTimeMillis();
        }
        try {
            switch (magickImage.getImageType()) {
                case 1:
                    bufferedImageCmykToBuffered = bilevelToBuffered(magickImage);
                    break;
                case 2:
                    bufferedImageCmykToBuffered = grayToBuffered(magickImage, false);
                    break;
                case 3:
                    bufferedImageCmykToBuffered = grayToBuffered(magickImage, true);
                    break;
                case 4:
                    bufferedImageCmykToBuffered = paletteToBuffered(magickImage, false);
                    break;
                case 5:
                    bufferedImageCmykToBuffered = paletteToBuffered(magickImage, true);
                    break;
                case 6:
                    bufferedImageCmykToBuffered = rgbToBuffered(magickImage, false);
                    break;
                case 7:
                    bufferedImageCmykToBuffered = rgbToBuffered(magickImage, true);
                    break;
                case 8:
                    bufferedImageCmykToBuffered = cmykToBuffered(magickImage, false);
                    break;
                case 9:
                    bufferedImageCmykToBuffered = cmykToBuffered(magickImage, true);
                    break;
                case 10:
                default:
                    throw new IllegalArgumentException("Unknown JMagick image type: " + magickImage.getImageType());
            }
            if (DEBUG) {
                long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
                System.out.println("Converted JMagick image type: " + magickImage.getImageType() + " to BufferedImage: " + bufferedImageCmykToBuffered);
                System.out.println("Conversion to BufferedImage: " + jCurrentTimeMillis2 + " ms");
            }
            return bufferedImageCmykToBuffered;
        } catch (Throwable th) {
            if (DEBUG) {
                long jCurrentTimeMillis3 = System.currentTimeMillis() - jCurrentTimeMillis;
                System.out.println("Converted JMagick image type: " + magickImage.getImageType() + " to BufferedImage: " + ((Object) null));
                System.out.println("Conversion to BufferedImage: " + jCurrentTimeMillis3 + " ms");
            }
            throw th;
        }
    }

    public static MagickImage toMagick(BufferedImage bufferedImage) throws MagickException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        long jCurrentTimeMillis = 0;
        if (DEBUG) {
            jCurrentTimeMillis = System.currentTimeMillis();
        }
        try {
            IndexColorModel colorModel = bufferedImage.getColorModel();
            if (colorModel instanceof IndexColorModel) {
                MagickImage magickImageIndexedToMagick = indexedToMagick(bufferedImage, colorModel, colorModel.hasAlpha());
                if (DEBUG) {
                    System.out.println("Conversion to MagickImage: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                }
                return magickImageIndexedToMagick;
            }
            switch (colorModel.getColorSpace().getType()) {
                case 5:
                    MagickImage magickImageRgbToMagic = rgbToMagic(bufferedImage, colorModel.hasAlpha());
                    if (DEBUG) {
                        System.out.println("Conversion to MagickImage: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                    }
                    return magickImageRgbToMagic;
                case 6:
                    MagickImage magickImageGrayToMagick = grayToMagick(bufferedImage, colorModel.hasAlpha());
                    if (DEBUG) {
                        System.out.println("Conversion to MagickImage: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                    }
                    return magickImageGrayToMagick;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                default:
                    throw new IllegalArgumentException("Unknown buffered image type: " + bufferedImage);
            }
        } catch (Throwable th) {
            if (DEBUG) {
                System.out.println("Conversion to MagickImage: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
            }
            throw th;
        }
    }

    private static MagickImage rgbToMagic(BufferedImage bufferedImage, boolean z) throws MagickException {
        Raster raster;
        MagickImage magickImage = new MagickImage();
        BufferedImage buffered = ImageUtil.toBuffered(bufferedImage, z ? 6 : 5);
        if (buffered.getRaster().getParent() != null) {
            raster = buffered.getData(new Rectangle(buffered.getWidth(), buffered.getHeight()));
        } else {
            raster = buffered.getRaster();
        }
        magickImage.constituteImage(buffered.getWidth(), buffered.getHeight(), z ? "ABGR" : "BGR", raster.getDataBuffer().getData());
        return magickImage;
    }

    private static MagickImage grayToMagick(BufferedImage bufferedImage, boolean z) throws MagickException {
        Raster raster;
        MagickImage magickImage = new MagickImage();
        BufferedImage buffered = ImageUtil.toBuffered(bufferedImage, z ? 6 : 10);
        if (buffered.getRaster().getParent() != null) {
            raster = buffered.getData(new Rectangle(buffered.getWidth(), buffered.getHeight()));
        } else {
            raster = buffered.getRaster();
        }
        magickImage.constituteImage(buffered.getWidth(), buffered.getHeight(), z ? "ABGR" : "I", raster.getDataBuffer().getData());
        return magickImage;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: magick.MagickException */
    private static MagickImage indexedToMagick(BufferedImage bufferedImage, IndexColorModel indexColorModel, boolean z) throws MagickException {
        MagickImage magickImageRgbToMagic = rgbToMagic(bufferedImage, z);
        magickImageRgbToMagic.setNumberColors(indexColorModel.getMapSize());
        return magickImageRgbToMagic;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: magick.MagickException */
    private static BufferedImage bilevelToBuffered(MagickImage magickImage) throws MagickException {
        BufferedImage bufferedImageGrayToBuffered = grayToBuffered(magickImage, false);
        BufferedImage bufferedImage = new BufferedImage(bufferedImageGrayToBuffered.getWidth(), bufferedImageGrayToBuffered.getHeight(), 12, CM_MONOCHROME);
        ImageUtil.drawOnto(bufferedImage, bufferedImageGrayToBuffered);
        return bufferedImage;
    }

    private static BufferedImage grayToBuffered(MagickImage magickImage, boolean z) throws MagickException {
        Dimension dimension = magickImage.getDimension();
        int i = dimension.width * dimension.height;
        int i2 = z ? 2 : 1;
        byte[] bArr = new byte[i * i2];
        magickImage.dispatchImage(0, 0, dimension.width, dimension.height, z ? "AI" : "I", bArr);
        return new BufferedImage(z ? CM_GRAY_ALPHA : CM_GRAY_OPAQUE, Raster.createInterleavedRaster(new DataBufferByte(bArr, bArr.length), dimension.width, dimension.height, dimension.width * i2, i2, z ? new int[]{1, 0} : new int[]{0}, LOCATION_UPPER_LEFT), z, (Hashtable) null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: magick.MagickException */
    private static BufferedImage paletteToBuffered(MagickImage magickImage, boolean z) throws MagickException {
        BufferedImage bufferedImage;
        try {
            IndexColorModel indexColorModelCreateIndexColorModel = createIndexColorModel(magickImage.getColormap(), z);
            BufferedImage bufferedImageRgbToBuffered = rgbToBuffered(magickImage, z);
            if (indexColorModelCreateIndexColorModel.getMapSize() <= 16) {
                bufferedImage = new BufferedImage(bufferedImageRgbToBuffered.getWidth(), bufferedImageRgbToBuffered.getHeight(), 12, indexColorModelCreateIndexColorModel);
            } else {
                bufferedImage = new BufferedImage(bufferedImageRgbToBuffered.getWidth(), bufferedImageRgbToBuffered.getHeight(), 13, indexColorModelCreateIndexColorModel);
            }
            if (z) {
                Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
                try {
                    graphics2DCreateGraphics.setComposite(AlphaComposite.Clear);
                    graphics2DCreateGraphics.fillRect(0, 0, bufferedImageRgbToBuffered.getWidth(), bufferedImageRgbToBuffered.getHeight());
                    graphics2DCreateGraphics.dispose();
                } catch (Throwable th) {
                    graphics2DCreateGraphics.dispose();
                    throw th;
                }
            }
            new CopyDither(indexColorModelCreateIndexColorModel).filter(bufferedImageRgbToBuffered, bufferedImage);
            return bufferedImage;
        } catch (MagickException e) {
            return rgbToBuffered(magickImage, z);
        }
    }

    public static IndexColorModel createIndexColorModel(PixelPacket[] pixelPacketArr, boolean z) {
        int[] iArr = new int[pixelPacketArr.length];
        int length = z ? iArr.length - 1 : -1;
        for (int length2 = pixelPacketArr.length - 1; length2 != 0; length2--) {
            PixelPacket pixelPacket = pixelPacketArr[length2];
            if (z) {
                iArr[length2] = ((255 - (pixelPacket.getOpacity() & 255)) << 24) | ((pixelPacket.getRed() & 255) << 16) | ((pixelPacket.getGreen() & 255) << 8) | (pixelPacket.getBlue() & 255);
            } else {
                iArr[length2] = ((pixelPacket.getRed() & 255) << 16) | ((pixelPacket.getGreen() & 255) << 8) | (pixelPacket.getBlue() & 255);
            }
        }
        return new InverseColorMapIndexColorModel(8, iArr.length, iArr, 0, z, length, 0);
    }

    private static BufferedImage rgbToBuffered(MagickImage magickImage, boolean z) throws MagickException {
        Dimension dimension = magickImage.getDimension();
        int i = dimension.width * dimension.height;
        int i2 = z ? 4 : 3;
        byte[] bArr = new byte[i * i2];
        magickImage.dispatchImage(0, 0, dimension.width, dimension.height, z ? "ABGR" : "BGR", bArr);
        return new BufferedImage(z ? CM_COLOR_ALPHA : CM_COLOR_OPAQUE, Raster.createInterleavedRaster(new DataBufferByte(bArr, bArr.length), dimension.width, dimension.height, dimension.width * i2, i2, z ? BAND_OFF_TRANS : BAND_OFF_OPAQUE, LOCATION_UPPER_LEFT), z, (Hashtable) null);
    }

    private static BufferedImage cmykToBuffered(MagickImage magickImage, boolean z) throws MagickException {
        Dimension dimension = magickImage.getDimension();
        int i = dimension.width * dimension.height;
        ICC_ColorSpace iCC_ColorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(magickImage.getColorProfile().getInfo()));
        int numComponents = iCC_ColorSpace.getNumComponents() + (z ? 1 : 0);
        int[] iArr = new int[numComponents];
        for (int i2 = 0; i2 < numComponents; i2++) {
            iArr[i2] = 8;
        }
        ComponentColorModel componentColorModel = z ? new ComponentColorModel(iCC_ColorSpace, iArr, true, true, 3, 0) : new ComponentColorModel(iCC_ColorSpace, iArr, false, false, 1, 0);
        byte[] bArr = new byte[i * numComponents];
        magickImage.dispatchImage(0, 0, dimension.width, dimension.height, z ? "ACMYK" : "CMYK", bArr);
        return new BufferedImage(componentColorModel, Raster.createInterleavedRaster(new DataBufferByte(bArr, bArr.length), dimension.width, dimension.height, dimension.width * numComponents, numComponents, z ? new int[]{0, 1, 2, 3, 4} : new int[]{0, 1, 2, 3}, LOCATION_UPPER_LEFT), z, (Hashtable) null);
    }
}
