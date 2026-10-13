package com.twelvemonkeys.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.ImageObserver;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ConvolveWithEdgeOp.class */
public class ConvolveWithEdgeOp implements BufferedImageOp, RasterOp {
    public static final int EDGE_ZERO_FILL = 0;
    public static final int EDGE_NO_OP = 1;
    public static final int EDGE_REFLECT = 2;
    public static final int EDGE_WRAP = 3;
    private final Kernel kernel;
    private final int edgeCondition;
    private final ConvolveOp convolve;

    public ConvolveWithEdgeOp(Kernel kernel, int i, RenderingHints renderingHints) {
        int i2;
        switch (i) {
            case 2:
            case 3:
                i2 = 1;
                break;
            default:
                i2 = i;
                break;
        }
        this.kernel = kernel;
        this.edgeCondition = i;
        this.convolve = new ConvolveOp(kernel, i2, renderingHints);
    }

    public ConvolveWithEdgeOp(Kernel kernel) {
        this(kernel, 0, null);
    }

    public BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (bufferedImage == null) {
            throw new NullPointerException("source image is null");
        }
        if (bufferedImage == bufferedImage2) {
            throw new IllegalArgumentException("source image cannot be the same as the destination image");
        }
        int width = this.kernel.getWidth() / 2;
        int height = this.kernel.getHeight() / 2;
        BufferedImage bufferedImageAddBorder = addBorder(bufferedImage, width, height);
        BufferedImage bufferedImageCreateBuffered = bufferedImage2;
        if (bufferedImageAddBorder.getType() == 5) {
            bufferedImageCreateBuffered = ImageUtil.createBuffered(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType(), bufferedImage.getColorModel().getTransparency(), null);
        }
        BufferedImage bufferedImageFilter = this.convolve.filter(bufferedImageAddBorder, bufferedImageCreateBuffered);
        if (bufferedImage != bufferedImageAddBorder) {
            bufferedImageFilter = bufferedImageFilter.getSubimage(width, height, bufferedImage.getWidth(), bufferedImage.getHeight());
        }
        return bufferedImageFilter;
    }

    private BufferedImage addBorder(BufferedImage bufferedImage, int i, int i2) {
        if ((this.edgeCondition & 2) == 0) {
            return bufferedImage;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ColorModel colorModel = bufferedImage.getColorModel();
        BufferedImage bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width + (2 * i), height + (2 * i2)), colorModel.isAlphaPremultiplied(), (Hashtable) null);
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        try {
            graphics2DCreateGraphics.setComposite(AlphaComposite.Src);
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2DCreateGraphics.drawImage(bufferedImage, i, i2, (ImageObserver) null);
            switch (this.edgeCondition) {
                case 2:
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, 0, i + width, i2, 0, 0, width, 1, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, i2, i, height + i2, 0, 0, 1, height, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, i2, (2 * i) + width, height + i2, width - 1, 0, width, height, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, i2 + height, i + width, (2 * i2) + height, 0, height - 1, width, height, (ImageObserver) null);
                    break;
                case 3:
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, (-height) + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, (-height) + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, (-height) + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, height + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, height + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, height + i2, (ImageObserver) null);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal edge operation " + this.edgeCondition);
            }
            graphics2DCreateGraphics.dispose();
            return bufferedImage2;
        } catch (Throwable th) {
            graphics2DCreateGraphics.dispose();
            throw th;
        }
    }

    public int getEdgeCondition() {
        return this.edgeCondition;
    }

    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return this.convolve.filter(raster, writableRaster);
    }

    public BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        return this.convolve.createCompatibleDestImage(bufferedImage, colorModel);
    }

    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return this.convolve.createCompatibleDestRaster(raster);
    }

    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return this.convolve.getBounds2D(bufferedImage);
    }

    public Rectangle2D getBounds2D(Raster raster) {
        return this.convolve.getBounds2D(raster);
    }

    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        return this.convolve.getPoint2D(point2D, point2D2);
    }

    public RenderingHints getRenderingHints() {
        return this.convolve.getRenderingHints();
    }

    public Kernel getKernel() {
        return this.convolve.getKernel();
    }
}
