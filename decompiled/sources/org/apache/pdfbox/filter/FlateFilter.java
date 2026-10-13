package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.io.IOUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/FlateFilter.class */
final class FlateFilter extends Filter {
    private static final Log LOG = LogFactory.getLog(FlateFilter.class);

    FlateFilter() {
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        COSDictionary decodeParams = getDecodeParams(parameters, index);
        try {
            decompress(encoded, Predictor.wrapPredictor(decoded, decodeParams));
            return new DecodeResult(parameters);
        } catch (DataFormatException e) {
            LOG.error("FlateFilter: stop reading corrupt stream due to a DataFormatException");
            throw new IOException(e);
        }
    }

    private void decompress(InputStream in, OutputStream out) throws DataFormatException, IOException {
        byte[] buf = new byte[2048];
        in.read();
        in.read();
        int read = in.read(buf);
        if (read > 0) {
            Inflater inflater = new Inflater(true);
            inflater.setInput(buf, 0, read);
            byte[] res = new byte[1024];
            boolean dataWritten = false;
            while (true) {
                try {
                    try {
                        int resRead = inflater.inflate(res);
                        if (resRead != 0) {
                            out.write(res, 0, resRead);
                            dataWritten = true;
                        } else {
                            if (inflater.finished() || inflater.needsDictionary() || in.available() == 0) {
                                break;
                                break;
                                break;
                            }
                            inflater.setInput(buf, 0, in.read(buf));
                        }
                    } catch (DataFormatException exception) {
                        if (dataWritten) {
                            LOG.warn("FlateFilter: premature end of stream due to a DataFormatException");
                        } else {
                            throw exception;
                        }
                    }
                } catch (Throwable th) {
                    inflater.end();
                    throw th;
                }
            }
            inflater.end();
        }
        out.flush();
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        int compressionLevel = getCompressionLevel();
        Deflater deflater = new Deflater(compressionLevel);
        DeflaterOutputStream out = new DeflaterOutputStream(encoded, deflater);
        IOUtils.copy(input, out);
        out.close();
        encoded.flush();
        deflater.end();
    }
}
