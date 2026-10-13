package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.HexUtils;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/IcaoUtils.class */
final class IcaoUtils {
    private static final String JPEG2K_HEADER = "0000000C6A502020";
    private static final String JFIF_HEADER = "FFD8FFE000104A464946";

    private IcaoUtils() {
    }

    static byte[] extractJpeg2kImage(byte[] photo) throws IOException {
        return extractImage(photo, JPEG2K_HEADER);
    }

    static byte[] extractJfifImage(byte[] photo) throws IOException {
        return extractImage(photo, JFIF_HEADER);
    }

    private static byte[] extractImage(byte[] photo, String magic) throws IOException {
        if (photo == null) {
            throw new IllegalArgumentException("Los datos de entrada no pueden ser nulos");
        }
        int headerIndex = HexUtils.hexify(photo, false).indexOf(magic);
        if (headerIndex == -1) {
            throw new IOException("Los datos de entrada no contienen una foto en el formato indicado");
        }
        int headerSize = headerIndex / 2;
        byte[] photoBytes = new byte[photo.length - headerSize];
        System.arraycopy(photo, headerSize, photoBytes, 0, photoBytes.length);
        return photoBytes;
    }
}
