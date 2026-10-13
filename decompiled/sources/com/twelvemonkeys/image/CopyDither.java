package com.twelvemonkeys.image;

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

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/CopyDither.class */
public class CopyDither implements BufferedImageOp, RasterOp {
    protected IndexColorModel indexColorModel;

    public CopyDither(IndexColorModel indexColorModel) {
        this.indexColorModel = null;
        this.indexColorModel = indexColorModel;
    }

    public CopyDither() {
        this.indexColorModel = null;
    }

    public final BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        if (colorModel == null) {
            return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 13, this.indexColorModel);
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
        return this.indexColorModel != null ? this.indexColorModel : IndexImage.getIndexColorModel((Image) bufferedImage, 256, 131584);
    }

    private IndexColorModel getICM(Raster raster) {
        return this.indexColorModel != null ? this.indexColorModel : createIndexColorModel(raster);
    }

    private IndexColorModel createIndexColorModel(Raster raster) {
        BufferedImage bufferedImage = new BufferedImage(raster.getWidth(), raster.getHeight(), 2);
        bufferedImage.setData(raster);
        return IndexImage.getIndexColorModel((Image) bufferedImage, 256, 131584);
    }

    public final WritableRaster filter(Raster raster, WritableRaster writableRaster, IndexColorModel indexColorModel) {
        int width = raster.getWidth();
        int height = raster.getHeight();
        if (writableRaster == null) {
            writableRaster = createCompatibleDestRaster(raster, indexColorModel);
        }
        int[] iArr = new int[4];
        Object dataElements = null;
        for (int i = 0; i < height; i++) {
            for (int i2 = 0; i2 < width; i2++) {
                raster.getPixel(i2, i, iArr);
                dataElements = indexColorModel.getDataElements(toIntARGB(iArr), dataElements);
                writableRaster.setDataElements(i2, i, dataElements);
            }
        }
        return writableRaster;
    }
}
