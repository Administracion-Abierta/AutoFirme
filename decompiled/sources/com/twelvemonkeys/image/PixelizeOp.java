package com.twelvemonkeys.image;

import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.Winspool;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/PixelizeOp.class */
public class PixelizeOp implements BufferedImageOp, RasterOp {
    private final int pixelSizeX;
    private final int pixelSizeY;
    private Rectangle sourceRegion;

    public PixelizeOp(int i) {
        this(i, i);
    }

    public PixelizeOp(int i, int i2) {
        this.pixelSizeX = i;
        this.pixelSizeY = i2;
    }

    public Rectangle getSourceRegion() {
        if (this.sourceRegion == null) {
            return null;
        }
        return new Rectangle(this.sourceRegion);
    }

    public void setSourceRegion(Rectangle rectangle) {
        if (rectangle == null) {
            this.sourceRegion = null;
        } else if (this.sourceRegion == null) {
            this.sourceRegion = new Rectangle(rectangle);
        } else {
            this.sourceRegion.setBounds(rectangle);
        }
    }

    public BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        BufferedImage bufferedImageCreateCompatibleDestImage = bufferedImage2 != null ? bufferedImage2 : createCompatibleDestImage(bufferedImage, null);
        filterImpl(bufferedImage.getRaster(), bufferedImageCreateCompatibleDestImage.getRaster());
        return bufferedImageCreateCompatibleDestImage;
    }

    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return filterImpl(raster, writableRaster != null ? writableRaster : createCompatibleDestRaster(raster));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private WritableRaster filterImpl(Raster raster, WritableRaster writableRaster) {
        int i;
        int i2;
        Object objCreateChild = raster;
        if (this.sourceRegion != null) {
            int i3 = this.sourceRegion.x;
            int i4 = this.sourceRegion.y;
            int i5 = this.sourceRegion.width;
            int i6 = this.sourceRegion.height;
            boolean z = raster == writableRaster;
            writableRaster = writableRaster.createWritableChild(i3, i4, i5, i6, 0, 0, (int[]) null);
            objCreateChild = z ? writableRaster : raster.createChild(i3, i4, i5, i6, 0, 0, (int[]) null);
        }
        int width = objCreateChild.getWidth();
        int height = objCreateChild.getHeight();
        int i7 = ((width + this.pixelSizeX) - 1) / this.pixelSizeX;
        int i8 = ((height + this.pixelSizeY) - 1) / this.pixelSizeY;
        boolean z2 = width % i7 != 0;
        boolean z3 = height % i8 != 0;
        int numDataElements = objCreateChild.getNumDataElements();
        int numBands = objCreateChild.getNumBands();
        int transferType = objCreateChild.getTransferType();
        Object dataElements = null;
        int[] bitMasks = null;
        int[] bitOffsets = null;
        if (objCreateChild.getTransferType() == 1) {
            if (objCreateChild.getSampleModel() instanceof SinglePixelPackedSampleModel) {
                SinglePixelPackedSampleModel sampleModel = objCreateChild.getSampleModel();
                bitMasks = sampleModel.getBitMasks();
                bitOffsets = sampleModel.getBitOffsets();
            } else {
                bitMasks = new int[]{65535};
                bitOffsets = new int[]{0};
            }
        }
        for (int i9 = 0; i9 < i8; i9++) {
            if (!z3 || i9 + 1 < i8) {
                i = this.pixelSizeY;
            } else {
                i = height - (i9 * this.pixelSizeY);
            }
            for (int i10 = 0; i10 < i7; i10++) {
                if (!z2 || i10 + 1 < i7) {
                    i2 = this.pixelSizeX;
                } else {
                    i2 = width - (i10 * this.pixelSizeX);
                }
                int i11 = i2 * i;
                int i12 = i11 * numDataElements;
                dataElements = objCreateChild.getDataElements(i10 * this.pixelSizeX, i9 * this.pixelSizeY, i2, i, dataElements);
                double d = 0.0d;
                double d2 = 0.0d;
                double d3 = 0.0d;
                double d4 = 0.0d;
                switch (transferType) {
                    case 0:
                        byte[] bArr = (byte[]) dataElements;
                        int i13 = 0;
                        while (true) {
                            int i14 = i13;
                            if (i14 < i12) {
                                d += (double) (bArr[i14] & 255);
                                if (numBands > 1) {
                                    d2 += (double) (bArr[i14 + 1] & 255);
                                    d3 += (double) (bArr[i14 + 2] & 255);
                                    if (numBands > 3) {
                                        d4 += (double) (bArr[i14 + 3] & 255);
                                    }
                                }
                                i13 = i14 + numDataElements;
                            } else {
                                double d5 = d / ((double) i11);
                                if (numBands > 1) {
                                    d2 /= (double) i11;
                                    d3 /= (double) i11;
                                    if (numBands > 3) {
                                        d4 /= (double) i11;
                                    }
                                }
                                int i15 = 0;
                                while (true) {
                                    int i16 = i15;
                                    if (i16 < i12) {
                                        bArr[i16] = (byte) clamp((int) d5);
                                        if (numBands > 1) {
                                            bArr[i16 + 1] = (byte) clamp((int) d2);
                                            bArr[i16 + 2] = (byte) clamp((int) d3);
                                            if (numBands > 3) {
                                                bArr[i16 + 3] = (byte) clamp((int) d4);
                                            }
                                        }
                                        i15 = i16 + numDataElements;
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                        if (bitMasks != null) {
                            short[] sArr = (short[]) dataElements;
                            int i17 = 0;
                            while (true) {
                                int i18 = i17;
                                if (i18 < i12) {
                                    d += (double) ((sArr[i18] & bitMasks[0]) >> bitOffsets[0]);
                                    if (bitMasks.length > 1) {
                                        d2 += (double) ((sArr[i18] & bitMasks[1]) >> bitOffsets[1]);
                                        d3 += (double) ((sArr[i18] & bitMasks[2]) >> bitOffsets[2]);
                                        if (bitMasks.length > 3) {
                                            d4 += (double) ((sArr[i18] & bitMasks[3]) >> bitOffsets[3]);
                                        }
                                    }
                                    i17 = i18 + numDataElements;
                                } else {
                                    double d6 = d / ((double) i11);
                                    double d7 = d2 / ((double) i11);
                                    double d8 = d3 / ((double) i11);
                                    double d9 = d4 / ((double) i11);
                                    int i19 = 0;
                                    while (true) {
                                        int i20 = i19;
                                        if (i20 < i12) {
                                            sArr[i20] = (short) ((((int) d6) << bitOffsets[0]) & bitMasks[0]);
                                            if (bitMasks.length > 1) {
                                                sArr[i20] = (short) (sArr[i20] | ((short) ((((int) d7) << bitOffsets[1]) & bitMasks[1])));
                                                sArr[i20] = (short) (sArr[i20] | ((short) ((((int) d8) << bitOffsets[2]) & bitMasks[2])));
                                                if (bitMasks.length > 3) {
                                                    sArr[i20] = (short) (sArr[i20] | ((short) ((((int) d9) << bitOffsets[3]) & bitMasks[3])));
                                                }
                                            }
                                            i19 = i20 + numDataElements;
                                        }
                                    }
                                }
                            }
                        } else {
                            throw new IllegalArgumentException("TransferType not supported: " + transferType);
                        }
                        break;
                    case 2:
                    default:
                        throw new IllegalArgumentException("TransferType not supported: " + transferType);
                    case 3:
                        int[] iArr = (int[]) dataElements;
                        int i21 = 0;
                        while (true) {
                            int i22 = i21;
                            if (i22 < i12) {
                                d += (double) ((iArr[i22] & Ddeml.MF_MASK) >> 24);
                                d2 += (double) ((iArr[i22] & Winspool.PRINTER_ENUM_ICONMASK) >> 16);
                                d3 += (double) ((iArr[i22] & 65280) >> 8);
                                d4 += (double) (iArr[i22] & 255);
                                i21 = i22 + numDataElements;
                            } else {
                                double d10 = d / ((double) i11);
                                double d11 = d2 / ((double) i11);
                                double d12 = d3 / ((double) i11);
                                double d13 = d4 / ((double) i11);
                                int i23 = 0;
                                while (true) {
                                    int i24 = i23;
                                    if (i24 < i12) {
                                        iArr[i24] = clamp((int) d10) << 24;
                                        iArr[i24] = iArr[i24] | (clamp((int) d11) << 16);
                                        iArr[i24] = iArr[i24] | (clamp((int) d12) << 8);
                                        iArr[i24] = iArr[i24] | clamp((int) d13);
                                        i23 = i24 + numDataElements;
                                    }
                                }
                            }
                        }
                        break;
                }
                writableRaster.setDataElements(i10 * this.pixelSizeX, i9 * this.pixelSizeY, i2, i, dataElements);
            }
        }
        return writableRaster;
    }

    private static int clamp(int i) {
        if (i > 255) {
            return 255;
        }
        return i;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

    public BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        ColorModel colorModel2 = colorModel != null ? colorModel : bufferedImage.getColorModel();
        return new BufferedImage(colorModel2, ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel2, bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel2.isAlphaPremultiplied(), (Hashtable) null);
    }

    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster();
    }

    public Rectangle2D getBounds2D(Raster raster) {
        return new Rectangle(raster.getWidth(), raster.getHeight());
    }

    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            if (point2D instanceof Point2D.Double) {
                point2D2 = new Point2D.Double();
            } else {
                point2D2 = new Point2D.Float();
            }
        }
        point2D2.setLocation(point2D);
        return point2D2;
    }

    public static void main(String[] strArr) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("2006-Lamborghini-Gallardo-Spyder-Y-T-1600x1200.png"));
        for (int i = 0; i < 10; i++) {
            new ResampleOp(bufferedImage.getWidth() / 10, bufferedImage.getHeight() / 10, 9).filter(bufferedImage, null);
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        BufferedImage bufferedImageFilter = new ResampleOp(bufferedImage.getWidth() / 4, bufferedImage.getHeight() / 4, 9).filter(bufferedImage, null);
        System.out.println("time: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
        JFrame jFrame = new JFrame("Test");
        jFrame.setDefaultCloseOperation(3);
        jFrame.setContentPane(new JScrollPane(new JLabel(new BufferedImageIcon(bufferedImageFilter))));
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
