package com.twelvemonkeys.imageio.plugins.jpeg;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGLosslessDecoderWrapper.class */
final class JPEGLosslessDecoderWrapper {
    private final JPEGImageReader listenerDelegate;

    JPEGLosslessDecoderWrapper(JPEGImageReader jPEGImageReader) {
        this.listenerDelegate = jPEGImageReader;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    BufferedImage readImage(List<Segment> list, ImageInputStream imageInputStream) throws IOException, IIOException {
        JPEGLosslessDecoder jPEGLosslessDecoder = new JPEGLosslessDecoder(list, imageInputStream, this.listenerDelegate);
        int[][] iArrDecode = jPEGLosslessDecoder.decode();
        int dimX = jPEGLosslessDecoder.getDimX();
        int dimY = jPEGLosslessDecoder.getDimY();
        if (jPEGLosslessDecoder.getNumComponents() == 1) {
            switch (jPEGLosslessDecoder.getPrecision()) {
                case 8:
                    return to8Bit1ComponentGrayScale(iArrDecode, dimX, dimY);
                case 10:
                case 12:
                case 14:
                case 16:
                    return to16Bit1ComponentGrayScale(iArrDecode, jPEGLosslessDecoder.getPrecision(), dimX, dimY);
            }
        }
        if (jPEGLosslessDecoder.getNumComponents() == 3) {
            switch (jPEGLosslessDecoder.getPrecision()) {
                case 8:
                    return to24Bit3ComponentRGB(iArrDecode, dimX, dimY);
            }
        }
        throw new IIOException("JPEG Lossless with " + jPEGLosslessDecoder.getPrecision() + " bit precision and " + jPEGLosslessDecoder.getNumComponents() + " component(s) not supported");
    }

    Raster readRaster(List<Segment> list, ImageInputStream imageInputStream) throws IOException {
        return readImage(list, imageInputStream).getRaster();
    }

    private BufferedImage to16Bit1ComponentGrayScale(int[][] iArr, int i, int i2, int i3) {
        BufferedImage bufferedImage;
        if (i == 16) {
            bufferedImage = new BufferedImage(i2, i3, 11);
        } else {
            ComponentColorModel componentColorModel = new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{i}, false, false, 1, 1);
            bufferedImage = new BufferedImage(componentColorModel, componentColorModel.createCompatibleWritableRaster(i2, i3), componentColorModel.isAlphaPremultiplied(), (Hashtable) null);
        }
        short[] data = bufferedImage.getRaster().getDataBuffer().getData();
        for (int i4 = 0; i4 < data.length; i4++) {
            data[i4] = (short) iArr[0][i4];
        }
        return bufferedImage;
    }

    private BufferedImage to8Bit1ComponentGrayScale(int[][] iArr, int i, int i2) {
        BufferedImage bufferedImage = new BufferedImage(i, i2, 10);
        byte[] data = bufferedImage.getRaster().getDataBuffer().getData();
        for (int i3 = 0; i3 < data.length; i3++) {
            data[i3] = (byte) iArr[0][i3];
        }
        return bufferedImage;
    }

    private BufferedImage to24Bit3ComponentRGB(int[][] iArr, int i, int i2) {
        BufferedImage bufferedImage = new BufferedImage(i, i2, 5);
        byte[] data = bufferedImage.getRaster().getDataBuffer().getData();
        for (int i3 = 0; i3 < data.length / 3; i3++) {
            data[(i3 * 3) + 2] = (byte) iArr[0][i3];
            data[(i3 * 3) + 1] = (byte) iArr[1][i3];
            data[i3 * 3] = (byte) iArr[2][i3];
        }
        return bufferedImage;
    }
}
