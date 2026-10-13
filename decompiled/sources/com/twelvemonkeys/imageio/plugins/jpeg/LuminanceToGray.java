package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.lang.Validate;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/LuminanceToGray.class */
final class LuminanceToGray implements RasterOp {
    LuminanceToGray() {
    }

    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        Validate.notNull(raster, "src may not be null");
        Validate.isTrue(raster != writableRaster, "src and dest raster may not be same");
        Validate.isTrue(raster.getNumDataElements() >= 3, Integer.valueOf(raster.getNumDataElements()), "luminance raster must have at least 3 data elements: %s");
        if (writableRaster == null) {
            writableRaster = createCompatibleDestRaster(raster);
        }
        writableRaster.setRect(0, 0, raster.createChild(0, 0, raster.getWidth(), raster.getHeight(), 0, 0, (raster.getNumBands() <= 3 || writableRaster.getNumBands() <= 1) ? new int[]{0} : new int[]{0, 3}));
        return writableRaster;
    }

    public Rectangle2D getBounds2D(Raster raster) {
        return raster.getBounds();
    }

    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster().createWritableChild(0, 0, raster.getWidth(), raster.getHeight(), 0, 0, new int[]{0});
    }

    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = new Point2D.Double(point2D.getX(), point2D.getY());
        } else {
            point2D2.setLocation(point2D);
        }
        return point2D2;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }
}
