package es.gob.jmulticard.card;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.JmcLogger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/CompressionUtils.class */
public final class CompressionUtils {
    private CompressionUtils() {
    }

    public static X509Certificate getCertificateFromCompressedOrNotData(byte[] data, CryptoHelper cryptoHelper) throws IOException, CertificateException {
        byte[] rawData;
        if (data == null || data.length < 1) {
            throw new IOException("Los datos del certificado eran nulos o vacios");
        }
        if (cryptoHelper == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        try {
            rawData = deflate(data);
        } catch (Exception e) {
            JmcLogger.warning("Ha fallado la descompresion del certificado, se probara sin descomprimir: " + e);
            rawData = data;
        }
        return CryptoHelper.generateCertificate(rawData);
    }

    private static byte[] deflate(byte[] compressedCertificate) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedCertificate, 8, compressedCertificate.length - 8);
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                if (count == 0) {
                    throw new DataFormatException();
                }
                buffer.write(buf, 0, count);
            } catch (DataFormatException ex) {
                throw new IOException("Error al descomprimir el certificado", ex);
            }
        }
        return buffer.toByteArray();
    }
}
