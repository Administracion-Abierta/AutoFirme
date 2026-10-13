package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.lang.Validate;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/FastCMYKToRGB.class */
class FastCMYKToRGB implements RasterOp {
    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        int[] bitOffsets;
        Validate.notNull(raster, "src may not be null");
        Validate.isTrue(raster != writableRaster, "src and dest raster may not be same");
        Validate.isTrue(raster.getTransferType() == 0, raster, "only TYPE_BYTE rasters supported as src: %s");
        Validate.isTrue(raster.getNumDataElements() >= 4, Integer.valueOf(raster.getNumDataElements()), "CMYK raster must have at least 4 data elements: %s");
        if (writableRaster == null) {
            writableRaster = createCompatibleDestRaster(raster);
        } else {
            Validate.isTrue((writableRaster.getTransferType() == 0 && writableRaster.getNumDataElements() >= 3) || (writableRaster.getTransferType() == 3 && writableRaster.getNumDataElements() == 1), raster, "only 3 or 4 byte TYPE_BYTE or 1 int TYPE_INT rasters supported as dest: %s");
        }
        int height = raster.getHeight();
        int width = raster.getWidth();
        byte[] bArr = new byte[raster.getNumDataElements()];
        if (writableRaster.getTransferType() == 0) {
            byte[] bArr2 = new byte[writableRaster.getNumDataElements()];
            if (bArr2.length > 3) {
                bArr2[3] = -1;
            }
            for (int minY = writableRaster.getMinY(); minY < height; minY++) {
                for (int minX = writableRaster.getMinX(); minX < width; minX++) {
                    raster.getDataElements(minX, minY, bArr);
                    convertCMYKToRGB(bArr, bArr2);
                    writableRaster.setDataElements(minX, minY, bArr2);
                }
            }
        } else if (writableRaster.getTransferType() == 3) {
            int[] iArr = new int[writableRaster.getNumDataElements()];
            byte[] bArr3 = new byte[3];
            SinglePixelPackedSampleModel sampleModel = writableRaster.getSampleModel();
            if (sampleModel instanceof SinglePixelPackedSampleModel) {
                bitOffsets = sampleModel.getBitOffsets();
            } else {
                bitOffsets = new int[]{0, 8, 16};
            }
            int i = bitOffsets.length > 3 ? 255 : 0;
            for (int minY2 = writableRaster.getMinY(); minY2 < height; minY2++) {
                for (int minX2 = writableRaster.getMinX(); minX2 < width; minX2++) {
                    raster.getDataElements(minX2, minY2, bArr);
                    convertCMYKToRGB(bArr, bArr3);
                    iArr[0] = (i << 24) | ((bArr3[0] & 255) << bitOffsets[0]) | ((bArr3[1] & 255) << bitOffsets[1]) | ((bArr3[2] & 255) << bitOffsets[2]);
                    writableRaster.setDataElements(minX2, minY2, iArr);
                }
            }
        } else {
            throw new AssertionError();
        }
        return writableRaster;
    }

    private void convertCMYKToRGB(byte[] bArr, byte[] bArr2) {
        int i = bArr[3] & 255;
        bArr2[0] = (byte) (255 - ((((bArr[0] & 255) * (255 - i)) / 255) + i));
        bArr2[1] = (byte) (255 - ((((bArr[1] & 255) * (255 - i)) / 255) + i));
        bArr2[2] = (byte) (255 - ((((bArr[2] & 255) * (255 - i)) / 255) + i));
    }

    public Rectangle2D getBounds2D(Raster raster) {
        return raster.getBounds();
    }

    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster().createWritableChild(0, 0, raster.getWidth(), raster.getHeight(), 0, 0, new int[]{0, 1, 2});
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
