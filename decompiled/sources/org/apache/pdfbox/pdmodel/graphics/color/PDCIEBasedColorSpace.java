package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDCIEBasedColorSpace.class */
public abstract class PDCIEBasedColorSpace extends PDColorSpace {
    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        float[] abc = new float[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, abc);
                abc[0] = abc[0] / 255.0f;
                abc[1] = abc[1] / 255.0f;
                abc[2] = abc[2] / 255.0f;
                float[] rgb = toRGB(abc);
                rgb[0] = rgb[0] * 255.0f;
                rgb[1] = rgb[1] * 255.0f;
                rgb[2] = rgb[2] * 255.0f;
                rgbRaster.setPixel(x, y, rgb);
            }
        }
        return rgbImage;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        return null;
    }

    public String toString() {
        return getName();
    }
}
