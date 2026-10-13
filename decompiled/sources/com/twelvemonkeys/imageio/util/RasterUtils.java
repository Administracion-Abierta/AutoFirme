package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/RasterUtils.class */
public final class RasterUtils {
    private RasterUtils() {
    }

    public static Raster asByteRaster(Raster raster) {
        return asByteRaster0(raster);
    }

    public static WritableRaster asByteRaster(WritableRaster writableRaster) {
        return asByteRaster0(writableRaster);
    }

    private static Raster asByteRaster0(Raster raster) {
        switch (raster.getTransferType()) {
            case 0:
                return raster;
            case 3:
                SinglePixelPackedSampleModel sampleModel = raster.getSampleModel();
                if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
                    throw new IllegalArgumentException(String.format("Requires SinglePixelPackedSampleModel, %s not supported", sampleModel.getClass().getSimpleName()));
                }
                final DataBufferInt dataBuffer = raster.getDataBuffer();
                int width = raster.getWidth();
                return new WritableRaster(new PixelInterleavedSampleModel(0, width, raster.getHeight(), 4, width * 4, createBandOffsets(sampleModel)), new DataBuffer(0, dataBuffer.getSize() * 4) { // from class: com.twelvemonkeys.imageio.util.RasterUtils.1
                    final int[] MASKS = {-256, -65281, -16711681, 16777215};

                    public int getElem(int i, int i2) {
                        return (dataBuffer.getElem(i2 / 4) >>> ((i2 % 4) * 8)) & 255;
                    }

                    public void setElem(int i, int i2, int i3) {
                        int i4 = i2 / 4;
                        int i5 = i2 % 4;
                        dataBuffer.setElem(i4, (dataBuffer.getElem(i4) & this.MASKS[i5]) | ((i3 & 255) << (i5 * 8)));
                    }
                }, new Point()) { // from class: com.twelvemonkeys.imageio.util.RasterUtils.2
                };
            default:
                throw new IllegalArgumentException(String.format("Raster type %d not supported", Integer.valueOf(raster.getTransferType())));
        }
    }

    private static int[] createBandOffsets(SinglePixelPackedSampleModel singlePixelPackedSampleModel) {
        Validate.notNull(singlePixelPackedSampleModel, "sampleModel");
        int[] bitMasks = singlePixelPackedSampleModel.getBitMasks();
        int[] iArr = new int[bitMasks.length];
        for (int i = 0; i < bitMasks.length; i++) {
            int i2 = bitMasks[i];
            int i3 = 0;
            if (i2 != 0) {
                while ((i2 & 255) == 0) {
                    i2 >>>= 8;
                    i3++;
                }
            }
            iArr[i] = i3;
        }
        return iArr;
    }
}
