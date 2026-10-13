package com.twelvemonkeys.imageio.metadata.xmp;

import com.ctc.wstx.io.CharsetNames;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/XMPScanner.class */
public final class XMPScanner {
    private static final byte[] XMP_PACKET_BEGIN = {60, 63, 120, 112, 97, 99, 107, 101, 116, 32, 98, 101, 103, 105, 110, 61};
    private static final byte[] XMP_PACKET_END = {60, 63, 120, 112, 97, 99, 107, 101, 116, 32, 101, 110, 100, 61};

    public static Reader scanForXMPPacket(Object obj) throws IOException {
        ImageInputStream imageInputStreamCreateImageInputStream = obj instanceof ImageInputStream ? (ImageInputStream) obj : ImageIO.createImageInputStream(obj);
        if (scanForSequence(imageInputStreamCreateImageInputStream, XMP_PACKET_BEGIN) >= 0) {
            byte b = imageInputStreamCreateImageInputStream.readByte();
            if (b == 39 || b == 34) {
                Charset charsetForName = null;
                byte[] bArr = new byte[4];
                imageInputStreamCreateImageInputStream.readFully(bArr);
                if ((bArr[0] == -17 && bArr[1] == -69 && bArr[2] == -65 && bArr[3] == b) || bArr[0] == b) {
                    charsetForName = StandardCharsets.UTF_8;
                } else if (bArr[0] == -2 && bArr[1] == -1 && bArr[2] == 0 && bArr[3] == b) {
                    charsetForName = StandardCharsets.UTF_16BE;
                } else if (bArr[0] == 0 && bArr[1] == -1 && bArr[2] == -2 && bArr[3] == b) {
                    imageInputStreamCreateImageInputStream.skipBytes(1);
                    charsetForName = StandardCharsets.UTF_16LE;
                } else if (bArr[0] == 0 && bArr[1] == 0 && bArr[2] == -2 && bArr[3] == -1) {
                    charsetForName = Charset.forName(CharsetNames.CS_UTF32BE);
                } else if (bArr[0] == 0 && bArr[1] == 0 && bArr[2] == 0 && bArr[3] == -1 && imageInputStreamCreateImageInputStream.read() == 254) {
                    imageInputStreamCreateImageInputStream.skipBytes(2);
                    charsetForName = Charset.forName(CharsetNames.CS_UTF32LE);
                }
                if (charsetForName != null) {
                    imageInputStreamCreateImageInputStream.mark();
                    long jScanForSequence = scanForSequence(imageInputStreamCreateImageInputStream, XMP_PACKET_END);
                    imageInputStreamCreateImageInputStream.reset();
                    InputStreamReader inputStreamReader = new InputStreamReader(IIOUtil.createStreamAdapter(imageInputStreamCreateImageInputStream, jScanForSequence - imageInputStreamCreateImageInputStream.getStreamPosition()), charsetForName);
                    while (inputStreamReader.read() != 62) {
                    }
                    return inputStreamReader;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private static long scanForSequence(ImageInputStream imageInputStream, byte[] bArr) throws IOException {
        long streamPosition = -1;
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = imageInputStream.read();
            if (i3 >= 0) {
                if (bArr[i] == ((byte) i3)) {
                    if (streamPosition == -1) {
                        streamPosition = imageInputStream.getStreamPosition() - 1;
                    }
                    if (i2 == 1 || i2 == 3) {
                        imageInputStream.skipBytes(i2);
                    }
                    i++;
                    if (i == bArr.length) {
                        return streamPosition;
                    }
                } else if (i == 1 && i3 == 0 && i2 < 3) {
                    i2++;
                } else if (i != 0) {
                    i = 0;
                    streamPosition = -1;
                    i2 = 0;
                }
            } else {
                return -1L;
            }
        }
    }

    public static void main(String[] strArr) throws IOException {
        ImageInputStream imageInputStreamCreateImageInputStream = ImageIO.createImageInputStream(new File(strArr[0]));
        while (true) {
            Reader readerScanForXMPPacket = scanForXMPPacket(imageInputStreamCreateImageInputStream);
            if (readerScanForXMPPacket != null) {
                BufferedReader bufferedReader = new BufferedReader(readerScanForXMPPacket);
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        System.out.println(line);
                    }
                }
            } else {
                imageInputStreamCreateImageInputStream.close();
                return;
            }
        }
    }
}
