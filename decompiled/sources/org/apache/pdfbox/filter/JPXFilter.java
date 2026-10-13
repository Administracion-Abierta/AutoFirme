package org.apache.pdfbox.filter;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDJPXColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/JPXFilter.class */
public final class JPXFilter extends Filter {
    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        DecodeResult result = new DecodeResult(new COSDictionary());
        result.getParameters().addAll(parameters);
        BufferedImage image = readJPX(encoded, options, result);
        WritableRaster raster = image.getRaster();
        switch (raster.getDataBuffer().getDataType()) {
            case 0:
                DataBufferByte byteBuffer = raster.getDataBuffer();
                decoded.write(byteBuffer.getData());
                return result;
            case 1:
                DataBufferUShort wordBuffer = raster.getDataBuffer();
                for (short w : wordBuffer.getData()) {
                    decoded.write(w >> 8);
                    decoded.write(w);
                }
                return result;
            case 2:
            default:
                throw new IOException("Data type " + raster.getDataBuffer().getDataType() + " not implemented");
            case 3:
                int[] ar = new int[raster.getNumBands()];
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        raster.getPixel(x, y, ar);
                        for (int i : ar) {
                            decoded.write(i);
                        }
                    }
                }
                return result;
        }
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        return decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    private BufferedImage readJPX(InputStream input, DecodeOptions options, DecodeResult result) throws IOException {
        ImageReader reader = findImageReader("JPEG2000", "Java Advanced Imaging (JAI) Image I/O Tools are not installed");
        ImageInputStream iis = null;
        try {
            iis = new MemoryCacheImageInputStream(input);
            reader.setInput(iis, true, true);
            ImageReadParam irp = reader.getDefaultReadParam();
            irp.setSourceRegion(options.getSourceRegion());
            irp.setSourceSubsampling(options.getSubsamplingX(), options.getSubsamplingY(), options.getSubsamplingOffsetX(), options.getSubsamplingOffsetY());
            options.setFilterSubsampled(true);
            try {
                BufferedImage image = reader.read(0, irp);
                COSDictionary parameters = result.getParameters();
                int bpc = image.getColorModel().getPixelSize() / image.getRaster().getNumBands();
                parameters.setInt(COSName.BITS_PER_COMPONENT, bpc);
                if (!parameters.getBoolean(COSName.IMAGE_MASK, false)) {
                    parameters.setItem(COSName.DECODE, (COSBase) null);
                }
                parameters.setInt(COSName.WIDTH, reader.getWidth(0));
                parameters.setInt(COSName.HEIGHT, reader.getHeight(0));
                if (!parameters.containsKey(COSName.COLORSPACE)) {
                    if ((image.getSampleModel() instanceof MultiPixelPackedSampleModel) && image.getColorModel().getPixelSize() == 1 && image.getRaster().getNumBands() == 1 && (image.getColorModel() instanceof IndexColorModel)) {
                        result.setColorSpace(new PDJPXColorSpace(ColorSpace.getInstance(1003)));
                    } else {
                        result.setColorSpace(new PDJPXColorSpace(image.getColorModel().getColorSpace()));
                    }
                }
                if (iis != null) {
                    iis.close();
                }
                reader.dispose();
                return image;
            } catch (Exception e) {
                throw new IOException("Could not read JPEG 2000 (JPX) image", e);
            }
        } catch (Throwable th) {
            if (iis != null) {
                iis.close();
            }
            reader.dispose();
            throw th;
        }
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        throw new UnsupportedOperationException("JPX encoding not implemented");
    }
}
