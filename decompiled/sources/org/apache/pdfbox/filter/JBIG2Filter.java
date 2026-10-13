package org.apache.pdfbox.filter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/JBIG2Filter.class */
final class JBIG2Filter extends Filter {
    private static final Log LOG = LogFactory.getLog(JBIG2Filter.class);
    private static boolean levigoLogged = false;

    JBIG2Filter() {
    }

    private static synchronized void logLevigoDonated() {
        if (!levigoLogged) {
            LOG.info("The Levigo JBIG2 plugin has been donated to the Apache Foundation");
            LOG.info("and an improved version is available for download at https://pdfbox.apache.org/download.cgi");
            levigoLogged = true;
        }
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        ImageReader reader = findImageReader("JBIG2", "jbig2-imageio is not installed");
        if (reader.getClass().getName().contains("levigo")) {
            logLevigoDonated();
        }
        int bits = parameters.getInt(COSName.BITS_PER_COMPONENT, 1);
        COSDictionary params = getDecodeParams(parameters, index);
        ImageReadParam irp = reader.getDefaultReadParam();
        irp.setSourceSubsampling(options.getSubsamplingX(), options.getSubsamplingY(), options.getSubsamplingOffsetX(), options.getSubsamplingOffsetY());
        irp.setSourceRegion(options.getSourceRegion());
        options.setFilterSubsampled(true);
        COSStream globals = null;
        if (params != null) {
            globals = (COSStream) params.getDictionaryObject(COSName.JBIG2_GLOBALS);
        }
        ImageInputStream iis = null;
        try {
            if (globals != null) {
                iis = ImageIO.createImageInputStream(new SequenceInputStream(globals.createInputStream(), encoded));
                reader.setInput(iis);
            } else {
                iis = ImageIO.createImageInputStream(encoded);
                reader.setInput(iis);
            }
            try {
                BufferedImage image = reader.read(0, irp);
                if (image.getColorModel().getPixelSize() != bits) {
                    if (bits != 1) {
                        LOG.warn("Attempting to handle a JBIG2 with more than 1-bit depth");
                    }
                    BufferedImage packedImage = new BufferedImage(image.getWidth(), image.getHeight(), 12);
                    Graphics graphics = packedImage.getGraphics();
                    graphics.drawImage(image, 0, 0, (ImageObserver) null);
                    graphics.dispose();
                    image = packedImage;
                }
                DataBufferByte dataBuffer = image.getData().getDataBuffer();
                if (dataBuffer.getDataType() == 0) {
                    decoded.write(dataBuffer.getData());
                    if (iis != null) {
                        iis.close();
                    }
                    reader.dispose();
                    return new DecodeResult(parameters);
                }
                throw new IOException("Unexpected image buffer type");
            } catch (Exception e) {
                throw new IOException("Could not read JBIG2 image", e);
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
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        return decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        throw new UnsupportedOperationException("JBIG2 encoding not implemented");
    }
}
