package org.apache.pdfbox.filter;

import com.aowagie.text.pdf.ColumnText;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.mozilla.javascript.ES6Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/DCTFilter.class */
final class DCTFilter extends Filter {
    private static final Log LOG = LogFactory.getLog(DCTFilter.class);
    private static final int POS_TRANSFORM = 11;
    private static final String ADOBE = "Adobe";

    DCTFilter() {
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        Raster raster;
        Integer transform;
        ImageReader reader = findImageReader("JPEG", "a suitable JAI I/O image filter is not installed");
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(encoded);
            if (iis.read() != 10) {
                iis.seek(0L);
            }
            reader.setInput(iis);
            ImageReadParam irp = reader.getDefaultReadParam();
            irp.setSourceSubsampling(options.getSubsamplingX(), options.getSubsamplingY(), options.getSubsamplingOffsetX(), options.getSubsamplingOffsetY());
            irp.setSourceRegion(options.getSourceRegion());
            options.setFilterSubsampled(true);
            String numChannels = getNumChannels(reader);
            ImageIO.setUseCache(false);
            if ("3".equals(numChannels) || numChannels.isEmpty()) {
                try {
                    BufferedImage image = reader.read(0, irp);
                    raster = image.getRaster();
                } catch (IIOException e) {
                    raster = reader.readRaster(0, irp);
                }
            } else {
                raster = reader.readRaster(0, irp);
            }
            if (raster.getNumBands() == 4) {
                try {
                    transform = getAdobeTransform(reader.getImageMetadata(0));
                } catch (IIOException e2) {
                    transform = Integer.valueOf(getAdobeTransformByBruteForce(iis));
                } catch (NegativeArraySizeException e3) {
                    transform = Integer.valueOf(getAdobeTransformByBruteForce(iis));
                }
                int colorTransform = transform != null ? transform.intValue() : 0;
                switch (colorTransform) {
                    case 0:
                        break;
                    case 1:
                        raster = fromYCbCrtoCMYK(raster);
                        break;
                    case 2:
                        raster = fromYCCKtoCMYK(raster);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown colorTransform");
                }
            } else if (raster.getNumBands() == 3) {
                raster = fromBGRtoRGB(raster);
            }
            DataBufferByte dataBuffer = raster.getDataBuffer();
            decoded.write(dataBuffer.getData());
            if (iis != null) {
                iis.close();
            }
            reader.dispose();
            return new DecodeResult(parameters);
        } catch (Throwable th) {
            if (iis != null) {
                iis.close();
            }
            reader.dispose();
            throw th;
        }
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        return decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    private Integer getAdobeTransform(IIOMetadata metadata) {
        Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
        Element markerSequence = (Element) tree.getElementsByTagName("markerSequence").item(0);
        NodeList app14AdobeNodeList = markerSequence.getElementsByTagName("app14Adobe");
        if (app14AdobeNodeList != null && app14AdobeNodeList.getLength() > 0) {
            Element adobe = (Element) app14AdobeNodeList.item(0);
            return Integer.valueOf(Integer.parseInt(adobe.getAttribute("transform")));
        }
        return 0;
    }

    private int getAdobeTransformByBruteForce(ImageInputStream iis) throws IOException {
        int a = 0;
        iis.seek(0L);
        while (true) {
            int by = iis.read();
            if (by != -1) {
                if (ADOBE.charAt(a) == by) {
                    a++;
                    if (a != ADOBE.length()) {
                        continue;
                    } else {
                        a = 0;
                        long afterAdobePos = iis.getStreamPosition();
                        iis.seek(iis.getStreamPosition() - 9);
                        int tag = iis.readUnsignedShort();
                        if (tag != 65518) {
                            iis.seek(afterAdobePos);
                        } else {
                            int len = iis.readUnsignedShort();
                            if (len >= 12) {
                                byte[] app14 = new byte[Math.max(len, 12)];
                                if (iis.read(app14) >= 12) {
                                    return app14[11];
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                } else {
                    a = 0;
                }
            } else {
                return 0;
            }
        }
    }

    private WritableRaster fromYCCKtoCMYK(Raster raster) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        int[] value = new int[4];
        int height = raster.getHeight();
        for (int y = 0; y < height; y++) {
            int width = raster.getWidth();
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, value);
                float Y = value[0];
                float Cb = value[1];
                float Cr = value[2];
                float K = value[3];
                int r = clamp((Y + (1.402f * Cr)) - 179.456f);
                int g = clamp(((Y - (0.34414f * Cb)) - (0.71414f * Cr)) + 135.45984f);
                int b = clamp((Y + (1.772f * Cb)) - 226.816f);
                int cyan = 255 - r;
                int magenta = 255 - g;
                int yellow = 255 - b;
                value[0] = cyan;
                value[1] = magenta;
                value[2] = yellow;
                value[3] = (int) K;
                writableRaster.setPixel(x, y, value);
            }
        }
        return writableRaster;
    }

    private WritableRaster fromYCbCrtoCMYK(Raster raster) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        int[] value = new int[4];
        int height = raster.getHeight();
        for (int y = 0; y < height; y++) {
            int width = raster.getWidth();
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, value);
                float Y = value[0];
                float Cb = value[1];
                float Cr = value[2];
                float K = value[3];
                int r = clamp((1.164f * (Y - 16.0f)) + (1.596f * (Cr - 128.0f)));
                int g = clamp((1.164f * (Y - 16.0f)) + ((-0.392f) * (Cb - 128.0f)) + ((-0.813f) * (Cr - 128.0f)));
                int b = clamp((1.164f * (Y - 16.0f)) + (2.017f * (Cb - 128.0f)));
                int cyan = 255 - r;
                int magenta = 255 - g;
                int yellow = 255 - b;
                value[0] = cyan;
                value[1] = magenta;
                value[2] = yellow;
                value[3] = (int) K;
                writableRaster.setPixel(x, y, value);
            }
        }
        return writableRaster;
    }

    private WritableRaster fromBGRtoRGB(Raster raster) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int w3 = width * 3;
        int[] tab = new int[w3];
        for (int y = 0; y < height; y++) {
            raster.getPixels(0, y, width, 1, tab);
            for (int off = 0; off < w3; off += 3) {
                int tmp = tab[off];
                tab[off] = tab[off + 2];
                tab[off + 2] = tmp;
            }
            writableRaster.setPixels(0, y, width, 1, tab);
        }
        return writableRaster;
    }

    private String getNumChannels(ImageReader reader) {
        try {
            IIOMetadata imageMetadata = reader.getImageMetadata(0);
            if (imageMetadata == null) {
                return "";
            }
            IIOMetadataNode metaTree = imageMetadata.getAsTree("javax_imageio_1.0");
            Element numChannelsItem = (Element) metaTree.getElementsByTagName("NumChannels").item(0);
            if (numChannelsItem == null) {
                return "";
            }
            return numChannelsItem.getAttribute(ES6Iterator.VALUE_PROPERTY);
        } catch (IOException e) {
            return "";
        } catch (NegativeArraySizeException e2) {
            return "";
        }
    }

    private int clamp(float value) {
        float f;
        if (value < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            f = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        } else {
            f = value > 255.0f ? 255.0f : value;
        }
        return (int) f;
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        throw new UnsupportedOperationException("DCTFilter encoding not implemented, use the JPEGFactory methods instead");
    }
}
