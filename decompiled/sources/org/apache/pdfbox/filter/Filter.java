package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/Filter.class */
public abstract class Filter {
    private static final Log LOG = LogFactory.getLog(Filter.class);
    public static final String SYSPROP_DEFLATELEVEL = "org.apache.pdfbox.filter.deflatelevel";

    public abstract DecodeResult decode(InputStream inputStream, OutputStream outputStream, COSDictionary cOSDictionary, int i) throws IOException;

    protected abstract void encode(InputStream inputStream, OutputStream outputStream, COSDictionary cOSDictionary) throws IOException;

    protected Filter() {
    }

    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        return decode(encoded, decoded, parameters, index);
    }

    public final void encode(InputStream input, OutputStream encoded, COSDictionary parameters, int index) throws IOException {
        encode(input, encoded, parameters.asUnmodifiableDictionary());
    }

    protected COSDictionary getDecodeParams(COSDictionary dictionary, int index) {
        COSBase filter = dictionary.getDictionaryObject(COSName.F, COSName.FILTER);
        COSBase obj = dictionary.getDictionaryObject(COSName.DP, COSName.DECODE_PARMS);
        if ((filter instanceof COSName) && (obj instanceof COSDictionary)) {
            return (COSDictionary) obj;
        }
        if ((filter instanceof COSArray) && (obj instanceof COSArray)) {
            COSArray array = (COSArray) obj;
            if (index < array.size()) {
                COSBase objAtIndex = array.getObject(index);
                if (objAtIndex instanceof COSDictionary) {
                    return (COSDictionary) array.getObject(index);
                }
            }
        } else if (obj != null && !(filter instanceof COSArray) && !(obj instanceof COSArray)) {
            LOG.error("Expected DecodeParams to be an Array or Dictionary but found " + obj.getClass().getName());
        }
        return new COSDictionary();
    }

    protected static ImageReader findImageReader(String formatName, String errorCause) throws MissingImageReaderException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
        while (readers.hasNext()) {
            ImageReader reader = readers.next();
            if (reader != null && reader.canReadRaster()) {
                return reader;
            }
        }
        throw new MissingImageReaderException("Cannot read " + formatName + " image: " + errorCause);
    }

    public static int getCompressionLevel() {
        int compressionLevel = -1;
        try {
            compressionLevel = Integer.parseInt(System.getProperty(SYSPROP_DEFLATELEVEL, "-1"));
        } catch (NumberFormatException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
        return Math.max(-1, Math.min(9, compressionLevel));
    }
}
