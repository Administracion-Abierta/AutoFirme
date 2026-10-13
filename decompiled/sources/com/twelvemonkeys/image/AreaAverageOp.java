package com.twelvemonkeys.image;

import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.Winspool;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
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

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/AreaAverageOp.class */
public class AreaAverageOp implements BufferedImageOp, RasterOp {
    private final int width;
    private final int height;
    private Rectangle sourceRegion;

    public AreaAverageOp(int i, int i2) {
        this.width = i;
        this.height = i2;
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
        long jCurrentTimeMillis = System.currentTimeMillis();
        filterImpl(bufferedImage.getRaster(), bufferedImageCreateCompatibleDestImage.getRaster());
        System.out.println("time: " + (System.currentTimeMillis() - jCurrentTimeMillis));
        return bufferedImageCreateCompatibleDestImage;
    }

    private void resample(BufferedImage bufferedImage, BufferedImage bufferedImage2, AffineTransform affineTransform) {
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        try {
            graphics2DCreateGraphics.drawImage(bufferedImage, affineTransform, (ImageObserver) null);
        } finally {
            graphics2DCreateGraphics.dispose();
        }
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
        int i7 = ((width + this.width) - 1) / this.width;
        int i8 = ((height + this.height) - 1) / this.height;
        boolean z2 = width % this.width != 0;
        boolean z3 = height % this.height != 0;
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
        for (int i9 = 0; i9 < this.height; i9++) {
            if (!z3 || i9 < this.height) {
                i = i8;
            } else {
                i = height - (i9 * i8);
            }
            for (int i10 = 0; i10 < this.width; i10++) {
                if (!z2 || i10 < this.width) {
                    i2 = i7;
                } else {
                    i2 = width - (i10 * i7);
                }
                int i11 = i2 * i;
                int i12 = i11 * numDataElements;
                try {
                    dataElements = objCreateChild.getDataElements(i10 * i7, i9 * i8, i2, i, dataElements);
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
                                    bArr[0] = (byte) clamp((int) d5);
                                    if (numBands > 1) {
                                        bArr[1] = (byte) clamp((int) d2);
                                        bArr[2] = (byte) clamp((int) d3);
                                        if (numBands > 3) {
                                            bArr[3] = (byte) clamp((int) d4);
                                        }
                                    }
                                }
                                break;
                            }
                            break;
                        case 1:
                            if (bitMasks != null) {
                                short[] sArr = (short[]) dataElements;
                                int i15 = 0;
                                while (true) {
                                    int i16 = i15;
                                    if (i16 < i12) {
                                        d += (double) ((sArr[i16] & bitMasks[0]) >> bitOffsets[0]);
                                        if (bitMasks.length > 1) {
                                            d2 += (double) ((sArr[i16] & bitMasks[1]) >> bitOffsets[1]);
                                            d3 += (double) ((sArr[i16] & bitMasks[2]) >> bitOffsets[2]);
                                            if (bitMasks.length > 3) {
                                                d4 += (double) ((sArr[i16] & bitMasks[3]) >> bitOffsets[3]);
                                            }
                                        }
                                        i15 = i16 + numDataElements;
                                    } else {
                                        double d6 = d / ((double) i11);
                                        double d7 = d2 / ((double) i11);
                                        double d8 = d3 / ((double) i11);
                                        double d9 = d4 / ((double) i11);
                                        sArr[0] = (short) ((((int) d6) << bitOffsets[0]) & bitMasks[0]);
                                        if (bitMasks.length > 1) {
                                            sArr[0] = (short) (sArr[0] | ((short) ((((int) d7) << bitOffsets[1]) & bitMasks[1])));
                                            sArr[0] = (short) (sArr[0] | ((short) ((((int) d8) << bitOffsets[2]) & bitMasks[2])));
                                            if (bitMasks.length > 3) {
                                                sArr[0] = (short) (sArr[0] | ((short) ((((int) d9) << bitOffsets[3]) & bitMasks[3])));
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
                            int i17 = 0;
                            while (true) {
                                int i18 = i17;
                                if (i18 < i12) {
                                    d += (double) ((iArr[i18] & Ddeml.MF_MASK) >> 24);
                                    d2 += (double) ((iArr[i18] & Winspool.PRINTER_ENUM_ICONMASK) >> 16);
                                    d3 += (double) ((iArr[i18] & 65280) >> 8);
                                    d4 += (double) (iArr[i18] & 255);
                                    i17 = i18 + numDataElements;
                                } else {
                                    iArr[0] = clamp((int) (d / ((double) i11))) << 24;
                                    iArr[0] = iArr[0] | (clamp((int) (d2 / ((double) i11))) << 16);
                                    iArr[0] = iArr[0] | (clamp((int) (d3 / ((double) i11))) << 8);
                                    iArr[0] = iArr[0] | clamp((int) (d4 / ((double) i11)));
                                }
                                break;
                            }
                            break;
                    }
                    writableRaster.setDataElements(i10, i9, 1, 1, dataElements);
                } catch (IndexOutOfBoundsException e) {
                }
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
        return new BufferedImage(colorModel2, ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel2, this.width, this.height), colorModel2.isAlphaPremultiplied(), (Hashtable) null);
    }

    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster(this.width, this.height);
    }

    public Rectangle2D getBounds2D(Raster raster) {
        return new Rectangle(this.width, this.height);
    }

    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return new Rectangle(this.width, this.height);
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
        for (int i = 0; i < 100; i++) {
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        BufferedImage bufferedImageFilter = new AreaAverageOp(500, 600).filter(bufferedImage, (BufferedImage) null);
        System.out.println("time: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
        JFrame jFrame = new JFrame("Test");
        jFrame.setDefaultCloseOperation(3);
        jFrame.setContentPane(new JScrollPane(new JLabel(new BufferedImageIcon(bufferedImageFilter))));
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
