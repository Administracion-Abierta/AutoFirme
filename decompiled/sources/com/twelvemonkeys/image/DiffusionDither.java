package com.twelvemonkeys.image;

import com.sun.jna.platform.win32.Winspool;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.util.Random;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/DiffusionDither.class */
public class DiffusionDither implements BufferedImageOp, RasterOp {
    private static final int FS_SCALE = 256;
    private static final Random RANDOM = new Random();
    protected final IndexColorModel indexColorModel;
    private boolean alternateScans;

    public DiffusionDither(IndexColorModel indexColorModel) {
        this.alternateScans = true;
        this.indexColorModel = indexColorModel;
    }

    public DiffusionDither() {
        this(null);
    }

    public void setAlternateScans(boolean z) {
        this.alternateScans = z;
    }

    public final BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        if (colorModel == null) {
            return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 13, getICM(bufferedImage));
        }
        if (colorModel instanceof IndexColorModel) {
            return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 13, (IndexColorModel) colorModel);
        }
        throw new ImageFilterException("Only IndexColorModel allowed.");
    }

    public final WritableRaster createCompatibleDestRaster(Raster raster) {
        return createCompatibleDestRaster(raster, getICM(raster));
    }

    public final WritableRaster createCompatibleDestRaster(Raster raster, IndexColorModel indexColorModel) {
        return indexColorModel.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
    }

    public final Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return getBounds2D((Raster) bufferedImage.getRaster());
    }

    public final Rectangle2D getBounds2D(Raster raster) {
        return raster.getBounds();
    }

    public final Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = new Point2D.Float();
        }
        point2D2.setLocation(point2D.getX(), point2D.getY());
        return point2D2;
    }

    public final RenderingHints getRenderingHints() {
        return null;
    }

    private static int[] toRGBArray(int i, int[] iArr) {
        iArr[0] = (i & Winspool.PRINTER_ENUM_ICONMASK) >> 16;
        iArr[1] = (i & 65280) >> 8;
        iArr[2] = i & 255;
        return iArr;
    }

    private static int toIntARGB(int[] iArr) {
        return (-16777216) | (iArr[0] << 16) | (iArr[1] << 8) | iArr[2];
    }

    public final BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (bufferedImage2 == null) {
            bufferedImage2 = createCompatibleDestImage(bufferedImage, getICM(bufferedImage));
        } else if (!(bufferedImage2.getColorModel() instanceof IndexColorModel)) {
            throw new ImageFilterException("Only IndexColorModel allowed.");
        }
        filter(bufferedImage.getRaster(), bufferedImage2.getRaster(), (IndexColorModel) bufferedImage2.getColorModel());
        return bufferedImage2;
    }

    public final WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return filter(raster, writableRaster, getICM(raster));
    }

    private IndexColorModel getICM(BufferedImage bufferedImage) {
        return this.indexColorModel != null ? this.indexColorModel : IndexImage.getIndexColorModel((Image) bufferedImage, 256, 131072);
    }

    private IndexColorModel getICM(Raster raster) {
        return this.indexColorModel != null ? this.indexColorModel : createIndexColorModel(raster);
    }

    private IndexColorModel createIndexColorModel(Raster raster) {
        BufferedImage bufferedImage = new BufferedImage(raster.getWidth(), raster.getHeight(), 2);
        bufferedImage.setData(raster);
        return IndexImage.getIndexColorModel((Image) bufferedImage, 256, 131072);
    }

    public final WritableRaster filter(Raster raster, WritableRaster writableRaster, IndexColorModel indexColorModel) {
        int i;
        int i2;
        int width = raster.getWidth();
        int height = raster.getHeight();
        if (writableRaster == null) {
            writableRaster = createCompatibleDestRaster(raster, indexColorModel);
        }
        int[][] iArr = new int[width + 2][3];
        int[][] iArr2 = new int[width + 2][3];
        for (int i3 = 0; i3 < width + 2; i3++) {
            iArr[i3][0] = RANDOM.nextInt(512) - 256;
            iArr[i3][1] = RANDOM.nextInt(512) - 256;
            iArr[i3][2] = RANDOM.nextInt(512) - 256;
        }
        int[] iArr3 = new int[3];
        int[] iArr4 = new int[4];
        int[] iArr5 = new int[4];
        Object dataElements = null;
        boolean z = true;
        for (int i4 = 0; i4 < height; i4++) {
            int length = iArr2.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                iArr2[length][0] = 0;
                iArr2[length][1] = 0;
                iArr2[length][2] = 0;
            }
            if (z) {
                i = 0;
                i2 = width;
            } else {
                i = width - 1;
                i2 = -1;
            }
            while (true) {
                raster.getPixel(i, i4, iArr4);
                for (int i5 = 0; i5 < 3; i5++) {
                    iArr4[i5] = (((iArr4[i5] << 4) + iArr[i + 1][i5]) + 8) >> 4;
                    if (iArr4[i5] > 255) {
                        iArr4[i5] = 255;
                    } else if (iArr4[i5] < 0) {
                        iArr4[i5] = 0;
                    }
                }
                dataElements = indexColorModel.getDataElements(toIntARGB(iArr4), dataElements);
                writableRaster.setDataElements(i, i4, dataElements);
                writableRaster.getPixel(i, i4, iArr5);
                toRGBArray(indexColorModel.getRGB(iArr5[0]), iArr5);
                iArr3[0] = iArr4[0] - iArr5[0];
                iArr3[1] = iArr4[1] - iArr5[1];
                iArr3[2] = iArr4[2] - iArr5[2];
                if (z) {
                    int[] iArr6 = iArr[i + 2];
                    iArr6[0] = iArr6[0] + (iArr3[0] * 7);
                    int[] iArr7 = iArr[i + 2];
                    iArr7[1] = iArr7[1] + (iArr3[1] * 7);
                    int[] iArr8 = iArr[i + 2];
                    iArr8[2] = iArr8[2] + (iArr3[2] * 7);
                    int[] iArr9 = iArr2[i];
                    iArr9[0] = iArr9[0] + (iArr3[0] * 3);
                    int[] iArr10 = iArr2[i];
                    iArr10[1] = iArr10[1] + (iArr3[1] * 3);
                    int[] iArr11 = iArr2[i];
                    iArr11[2] = iArr11[2] + (iArr3[2] * 3);
                    int[] iArr12 = iArr2[i + 1];
                    iArr12[0] = iArr12[0] + (iArr3[0] * 5);
                    int[] iArr13 = iArr2[i + 1];
                    iArr13[1] = iArr13[1] + (iArr3[1] * 5);
                    int[] iArr14 = iArr2[i + 1];
                    iArr14[2] = iArr14[2] + (iArr3[2] * 5);
                    int[] iArr15 = iArr2[i + 2];
                    iArr15[0] = iArr15[0] + iArr3[0];
                    int[] iArr16 = iArr2[i + 2];
                    iArr16[1] = iArr16[1] + iArr3[1];
                    int[] iArr17 = iArr2[i + 2];
                    iArr17[2] = iArr17[2] + iArr3[2];
                    i++;
                    if (i >= i2) {
                        break;
                    }
                } else {
                    int[] iArr18 = iArr[i];
                    iArr18[0] = iArr18[0] + (iArr3[0] * 7);
                    int[] iArr19 = iArr[i];
                    iArr19[1] = iArr19[1] + (iArr3[1] * 7);
                    int[] iArr20 = iArr[i];
                    iArr20[2] = iArr20[2] + (iArr3[2] * 7);
                    int[] iArr21 = iArr2[i + 2];
                    iArr21[0] = iArr21[0] + (iArr3[0] * 3);
                    int[] iArr22 = iArr2[i + 2];
                    iArr22[1] = iArr22[1] + (iArr3[1] * 3);
                    int[] iArr23 = iArr2[i + 2];
                    iArr23[2] = iArr23[2] + (iArr3[2] * 3);
                    int[] iArr24 = iArr2[i + 1];
                    iArr24[0] = iArr24[0] + (iArr3[0] * 5);
                    int[] iArr25 = iArr2[i + 1];
                    iArr25[1] = iArr25[1] + (iArr3[1] * 5);
                    int[] iArr26 = iArr2[i + 1];
                    iArr26[2] = iArr26[2] + (iArr3[2] * 5);
                    int[] iArr27 = iArr2[i];
                    iArr27[0] = iArr27[0] + iArr3[0];
                    int[] iArr28 = iArr2[i];
                    iArr28[1] = iArr28[1] + iArr3[1];
                    int[] iArr29 = iArr2[i];
                    iArr29[2] = iArr29[2] + iArr3[2];
                    i--;
                    if (i <= i2) {
                        break;
                    }
                }
            }
            int[][] iArr30 = iArr;
            iArr = iArr2;
            iArr2 = iArr30;
            if (this.alternateScans) {
                z = !z;
            }
        }
        return writableRaster;
    }
}
