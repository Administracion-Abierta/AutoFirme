package com.twelvemonkeys.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/AffineTransformOp.class */
public class AffineTransformOp implements BufferedImageOp, RasterOp {
    final java.awt.image.AffineTransformOp delegate;
    public static final int TYPE_NEAREST_NEIGHBOR = 1;
    public static final int TYPE_BILINEAR = 2;
    public static final int TYPE_BICUBIC = 3;

    public AffineTransformOp(AffineTransform affineTransform, RenderingHints renderingHints) {
        this.delegate = new java.awt.image.AffineTransformOp(affineTransform, renderingHints);
    }

    public AffineTransformOp(AffineTransform affineTransform, int i) {
        this.delegate = new java.awt.image.AffineTransformOp(affineTransform, i);
    }

    public BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        try {
            return this.delegate.filter(bufferedImage, bufferedImage2);
        } catch (ImagingOpException e) {
            if (bufferedImage2 == null) {
                bufferedImage2 = createCompatibleDestImage(bufferedImage, bufferedImage.getColorModel());
            }
            Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
            try {
                int interpolationType = this.delegate.getInterpolationType();
                if (interpolationType > 0) {
                    Object obj = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                    switch (interpolationType) {
                        case 2:
                            obj = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                            break;
                        case 3:
                            obj = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                            break;
                    }
                    graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, obj);
                } else if (getRenderingHints() != null) {
                    graphics2DCreateGraphics.setRenderingHints(getRenderingHints());
                }
                graphics2DCreateGraphics.drawImage(bufferedImage, this.delegate.getTransform(), (ImageObserver) null);
                return bufferedImage2;
            } finally {
                graphics2DCreateGraphics.dispose();
            }
        }
    }

    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return this.delegate.getBounds2D(bufferedImage);
    }

    public BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        return this.delegate.createCompatibleDestImage(bufferedImage, colorModel);
    }

    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return this.delegate.filter(raster, writableRaster);
    }

    public Rectangle2D getBounds2D(Raster raster) {
        return this.delegate.getBounds2D(raster);
    }

    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return this.delegate.createCompatibleDestRaster(raster);
    }

    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        return this.delegate.getPoint2D(point2D, point2D2);
    }

    public RenderingHints getRenderingHints() {
        return this.delegate.getRenderingHints();
    }
}
