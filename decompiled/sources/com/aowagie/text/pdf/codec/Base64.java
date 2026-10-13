package com.aowagie.text.pdf.codec;

import com.ctc.wstx.io.CharsetNames;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/Base64.class */
public class Base64 {
    private static final int NO_OPTIONS = 0;
    private static final int ENCODE = 1;
    private static final int DECODE = 0;
    private static final int GZIP = 2;
    private static final int DONT_BREAK_LINES = 8;
    private static final int URL_SAFE = 16;
    private static final int ORDERED = 32;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final String PREFERRED_ENCODING = StandardCharsets.UTF_8.name();
    private static final byte[] _STANDARD_ALPHABET = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] _STANDARD_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9};
    private static final byte[] _URL_SAFE_ALPHABET = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] _URL_SAFE_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9};
    private static final byte[] _ORDERED_ALPHABET = {45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};
    private static final byte[] _ORDERED_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9};

    /* JADX INFO: Access modifiers changed from: private */
    public static final byte[] getAlphabet(int options) {
        if ((options & 16) == 16) {
            return _URL_SAFE_ALPHABET;
        }
        if ((options & 32) == 32) {
            return _ORDERED_ALPHABET;
        }
        return _STANDARD_ALPHABET;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final byte[] getDecodabet(int options) {
        if ((options & 16) == 16) {
            return _URL_SAFE_DECODABET;
        }
        if ((options & 32) == 32) {
            return _ORDERED_DECODABET;
        }
        return _STANDARD_DECODABET;
    }

    private Base64() {
    }

    public static final void main(String[] args) {
        if (args.length < 3) {
            usage("Not enough arguments.");
            return;
        }
        String flag = args[0];
        String infile = args[1];
        String outfile = args[2];
        if (flag.equals("-e")) {
            encodeFileToFile(infile, outfile);
        } else if (flag.equals("-d")) {
            decodeFileToFile(infile, outfile);
        } else {
            usage("Unknown flag: " + flag);
        }
    }

    private static final void usage(String msg) {
        System.err.println(msg);
        System.err.println("Usage: java Base64 -e|-d inputfile outputfile");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options) {
        byte[] ALPHABET = getAlphabet(options);
        int inBuff = (numSigBytes > 0 ? (source[srcOffset] << 24) >>> 8 : 0) | (numSigBytes > 1 ? (source[srcOffset + 1] << 24) >>> 16 : 0) | (numSigBytes > 2 ? (source[srcOffset + 2] << 24) >>> 24 : 0);
        switch (numSigBytes) {
            case 1:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                destination[destOffset + 2] = 61;
                destination[destOffset + 3] = 61;
                break;
            case 2:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 63];
                destination[destOffset + 3] = 61;
                break;
            case 3:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 63];
                destination[destOffset + 3] = ALPHABET[inBuff & 63];
                break;
            default:
                break;
        }
        return destination;
    }

    public static String encodeBytes(byte[] source) {
        return encodeBytes(source, 0, source.length, 0);
    }

    private static String encodeBytes(byte[] source, int off, int len, int options) {
        int dontBreakLines = options & 8;
        int gzip = options & 2;
        if (gzip == 2) {
            ByteArrayOutputStream baos = null;
            GZIPOutputStream gzos = null;
            OutputStream b64os = null;
            try {
                try {
                    baos = new ByteArrayOutputStream();
                    b64os = new OutputStream(baos, 1 | options);
                    gzos = new GZIPOutputStream(b64os);
                    gzos.write(source, off, len);
                    gzos.close();
                    try {
                        gzos.close();
                    } catch (Exception e) {
                    }
                    try {
                        b64os.close();
                    } catch (Exception e2) {
                    }
                    try {
                        baos.close();
                    } catch (Exception e3) {
                    }
                    try {
                        return new String(baos.toByteArray(), PREFERRED_ENCODING);
                    } catch (UnsupportedEncodingException e4) {
                        return new String(baos.toByteArray());
                    }
                } catch (Throwable th) {
                    try {
                        gzos.close();
                    } catch (Exception e5) {
                    }
                    try {
                        b64os.close();
                    } catch (Exception e6) {
                    }
                    try {
                        baos.close();
                    } catch (Exception e7) {
                    }
                    throw th;
                }
            } catch (IOException e8) {
                e8.printStackTrace();
                try {
                    gzos.close();
                } catch (Exception e9) {
                }
                try {
                    b64os.close();
                } catch (Exception e10) {
                }
                try {
                    baos.close();
                } catch (Exception e11) {
                }
                return null;
            }
        }
        boolean breakLines = dontBreakLines == 0;
        int len43 = (len * 4) / 3;
        byte[] outBuff = new byte[len43 + (len % 3 > 0 ? 4 : 0) + (breakLines ? len43 / 76 : 0)];
        int d = 0;
        int e12 = 0;
        int len2 = len - 2;
        int lineLength = 0;
        while (d < len2) {
            encode3to4(source, d + off, 3, outBuff, e12, options);
            lineLength += 4;
            if (breakLines && lineLength == 76) {
                outBuff[e12 + 4] = 10;
                e12++;
                lineLength = 0;
            }
            d += 3;
            e12 += 4;
        }
        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e12, options);
            e12 += 4;
        }
        try {
            return new String(outBuff, 0, e12, PREFERRED_ENCODING);
        } catch (UnsupportedEncodingException e13) {
            return new String(outBuff, 0, e12);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
        byte[] DECODABET = getDecodabet(options);
        if (source[srcOffset + 2] == 61) {
            destination[destOffset] = (byte) ((((DECODABET[source[srcOffset]] & 255) << 18) | ((DECODABET[source[srcOffset + 1]] & 255) << 12)) >>> 16);
            return 1;
        }
        if (source[srcOffset + 3] == 61) {
            int outBuff = ((DECODABET[source[srcOffset]] & 255) << 18) | ((DECODABET[source[srcOffset + 1]] & 255) << 12) | ((DECODABET[source[srcOffset + 2]] & 255) << 6);
            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        }
        try {
            int outBuff2 = ((DECODABET[source[srcOffset]] & 255) << 18) | ((DECODABET[source[srcOffset + 1]] & 255) << 12) | ((DECODABET[source[srcOffset + 2]] & 255) << 6) | (DECODABET[source[srcOffset + 3]] & 255);
            destination[destOffset] = (byte) (outBuff2 >> 16);
            destination[destOffset + 1] = (byte) (outBuff2 >> 8);
            destination[destOffset + 2] = (byte) outBuff2;
            return 3;
        } catch (Exception e) {
            System.out.println("" + ((int) source[srcOffset]) + ": " + ((int) DECODABET[source[srcOffset]]));
            System.out.println("" + ((int) source[srcOffset + 1]) + ": " + ((int) DECODABET[source[srcOffset + 1]]));
            System.out.println("" + ((int) source[srcOffset + 2]) + ": " + ((int) DECODABET[source[srcOffset + 2]]));
            System.out.println("" + ((int) source[srcOffset + 3]) + ": " + ((int) DECODABET[source[srcOffset + 3]]));
            return -1;
        }
    }

    private static byte[] decode(byte[] source, int off, int len, int options) {
        byte[] DECODABET = getDecodabet(options);
        int len34 = (len * 3) / 4;
        byte[] outBuff = new byte[len34];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int b4Posn = 0;
        for (int i = off; i < off + len; i++) {
            byte sbiCrop = (byte) (source[i] & 127);
            byte sbiDecode = DECODABET[sbiCrop];
            if (sbiDecode >= -5) {
                if (sbiDecode >= -1) {
                    int i2 = b4Posn;
                    b4Posn++;
                    b4[i2] = sbiCrop;
                    if (b4Posn > 3) {
                        outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
                        b4Posn = 0;
                        if (sbiCrop == 61) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                System.err.println("Bad Base64 input character at " + i + ": " + ((int) source[i]) + "(decimal)");
                return null;
            }
        }
        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }

    public static byte[] decode(String s) {
        return decode(s, 0);
    }

    private static byte[] decode(String s, int options) {
        byte[] bytes;
        try {
            bytes = s.getBytes(PREFERRED_ENCODING);
        } catch (UnsupportedEncodingException e) {
            bytes = s.getBytes();
        }
        byte[] bytes2 = decode(bytes, 0, bytes.length, options);
        if (bytes2 != null && bytes2.length >= 4) {
            int head = (bytes2[0] & 255) | ((bytes2[1] << 8) & 65280);
            if (35615 == head) {
                ByteArrayInputStream bais = null;
                GZIPInputStream gzis = null;
                ByteArrayOutputStream baos = null;
                byte[] buffer = new byte[2048];
                try {
                    baos = new ByteArrayOutputStream();
                    bais = new ByteArrayInputStream(bytes2);
                    gzis = new GZIPInputStream(bais);
                    while (true) {
                        int length = gzis.read(buffer);
                        if (length < 0) {
                            break;
                        }
                        baos.write(buffer, 0, length);
                    }
                    bytes2 = baos.toByteArray();
                    try {
                        baos.close();
                    } catch (Exception e2) {
                    }
                    try {
                        gzis.close();
                    } catch (Exception e3) {
                    }
                    try {
                        bais.close();
                    } catch (Exception e4) {
                    }
                } catch (IOException e5) {
                    try {
                        baos.close();
                    } catch (Exception e6) {
                    }
                    try {
                        gzis.close();
                    } catch (Exception e7) {
                    }
                    try {
                        bais.close();
                    } catch (Exception e8) {
                    }
                } catch (Throwable th) {
                    try {
                        baos.close();
                    } catch (Exception e9) {
                    }
                    try {
                        gzis.close();
                    } catch (Exception e10) {
                    }
                    try {
                        bais.close();
                    } catch (Exception e11) {
                    }
                    throw th;
                }
            }
        }
        return bytes2;
    }

    private static byte[] decodeFromFile(String filename) {
        byte[] decodedData = null;
        InputStream bis = null;
        try {
            try {
                File file = new File(filename);
                int length = 0;
                if (file.length() > 2147483647L) {
                    System.err.println("File is too big for this convenience method (" + file.length() + " bytes).");
                    try {
                        bis.close();
                    } catch (Exception e) {
                    }
                    return null;
                }
                byte[] buffer = new byte[(int) file.length()];
                InputStream bis2 = new InputStream(new BufferedInputStream(new FileInputStream(file)), 0);
                while (true) {
                    int numBytes = bis2.read(buffer, length, 4096);
                    if (numBytes < 0) {
                        break;
                    }
                    length += numBytes;
                }
                decodedData = new byte[length];
                System.arraycopy(buffer, 0, decodedData, 0, length);
                try {
                    bis2.close();
                } catch (Exception e2) {
                }
                return decodedData;
            } catch (IOException e3) {
                System.err.println("Error decoding from file " + filename);
                try {
                    bis.close();
                } catch (Exception e4) {
                }
            }
        } catch (Throwable th) {
            try {
                bis.close();
            } catch (Exception e5) {
            }
            throw th;
        }
    }

    private static String encodeFromFile(String filename) {
        String encodedData = null;
        InputStream bis = null;
        try {
            try {
                File file = new File(filename);
                byte[] buffer = new byte[Math.max((int) (file.length() * 1.4d), 40)];
                int length = 0;
                bis = new InputStream(new BufferedInputStream(new FileInputStream(file)), 1);
                while (true) {
                    int numBytes = bis.read(buffer, length, 4096);
                    if (numBytes < 0) {
                        break;
                    }
                    length += numBytes;
                }
                encodedData = new String(buffer, 0, length, PREFERRED_ENCODING);
                try {
                    bis.close();
                } catch (Exception e) {
                }
            } catch (IOException e2) {
                System.err.println("Error encoding from file " + filename);
                try {
                    bis.close();
                } catch (Exception e3) {
                }
            }
            return encodedData;
        } catch (Throwable th) {
            try {
                bis.close();
            } catch (Exception e4) {
            }
            throw th;
        }
    }

    private static void encodeFileToFile(String infile, String outfile) {
        String encoded = encodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            try {
                out = new BufferedOutputStream(new FileOutputStream(outfile));
                out.write(encoded.getBytes(CharsetNames.CS_US_ASCII));
                try {
                    out.close();
                } catch (Exception e) {
                }
            } catch (Throwable th) {
                try {
                    out.close();
                } catch (Exception e2) {
                }
                throw th;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                out.close();
            } catch (Exception e3) {
            }
        }
    }

    private static void decodeFileToFile(String infile, String outfile) {
        byte[] decoded = decodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            try {
                out = new BufferedOutputStream(new FileOutputStream(outfile));
                out.write(decoded);
                try {
                    out.close();
                } catch (Exception e) {
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                try {
                    out.close();
                } catch (Exception e2) {
                }
            }
        } catch (Throwable th) {
            try {
                out.close();
            } catch (Exception e3) {
            }
            throw th;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/Base64$InputStream.class */
    private static class InputStream extends FilterInputStream {
        private final boolean encode;
        private int position;
        private final byte[] buffer;
        private final int bufferLength;
        private int numSigBytes;
        private int lineLength;
        private final boolean breakLines;
        private final int options;
        private final byte[] alphabet;
        private final byte[] decodabet;

        private InputStream(java.io.InputStream in, int options) {
            super(in);
            this.breakLines = (options & 8) != 8;
            this.encode = (options & 1) == 1;
            this.bufferLength = this.encode ? 4 : 3;
            this.buffer = new byte[this.bufferLength];
            this.position = -1;
            this.lineLength = 0;
            this.options = options;
            this.alphabet = Base64.getAlphabet(options);
            this.decodabet = Base64.getDecodabet(options);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            int b;
            if (this.position < 0) {
                if (this.encode) {
                    byte[] b3 = new byte[3];
                    int numBinaryBytes = 0;
                    for (int i = 0; i < 3; i++) {
                        try {
                            int b2 = this.in.read();
                            if (b2 >= 0) {
                                b3[i] = (byte) b2;
                                numBinaryBytes++;
                            }
                        } catch (IOException e) {
                            if (i == 0) {
                                throw e;
                            }
                        }
                    }
                    if (numBinaryBytes > 0) {
                        Base64.encode3to4(b3, 0, numBinaryBytes, this.buffer, 0, this.options);
                        this.position = 0;
                        this.numSigBytes = 4;
                    } else {
                        return -1;
                    }
                } else {
                    byte[] b4 = new byte[4];
                    int i2 = 0;
                    while (i2 < 4) {
                        do {
                            b = this.in.read();
                            if (b < 0) {
                                break;
                            }
                        } while (this.decodabet[b & 127] <= -5);
                        if (b < 0) {
                            break;
                        }
                        b4[i2] = (byte) b;
                        i2++;
                    }
                    if (i2 == 4) {
                        this.numSigBytes = Base64.decode4to3(b4, 0, this.buffer, 0, this.options);
                        this.position = 0;
                    } else {
                        if (i2 == 0) {
                            return -1;
                        }
                        throw new IOException("Improperly padded Base64 input.");
                    }
                }
            }
            if (this.position >= 0) {
                if (this.position >= this.numSigBytes) {
                    return -1;
                }
                if (this.encode && this.breakLines && this.lineLength >= 76) {
                    this.lineLength = 0;
                    return 10;
                }
                this.lineLength++;
                byte[] bArr = this.buffer;
                int i3 = this.position;
                this.position = i3 + 1;
                byte b5 = bArr[i3];
                if (this.position >= this.bufferLength) {
                    this.position = -1;
                }
                return b5 & 255;
            }
            throw new IOException("Error in Base64 code reading stream.");
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] dest, int off, int len) throws IOException {
            int i = 0;
            while (i < len) {
                int b = read();
                if (b >= 0) {
                    dest[off + i] = (byte) b;
                    i++;
                } else {
                    if (i != 0) {
                        break;
                    }
                    return -1;
                }
            }
            return i;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/Base64$OutputStream.class */
    private static class OutputStream extends FilterOutputStream {
        private final boolean encode;
        private int position;
        private byte[] buffer;
        private final int bufferLength;
        private int lineLength;
        private final boolean breakLines;
        private final byte[] b4;
        private final boolean suspendEncoding;
        private final int options;
        private final byte[] alphabet;
        private final byte[] decodabet;

        private OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & 8) != 8;
            this.encode = (options & 1) == 1;
            this.bufferLength = this.encode ? 3 : 4;
            this.buffer = new byte[this.bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.alphabet = Base64.getAlphabet(options);
            this.decodabet = Base64.getDecodabet(options);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(int theByte) throws IOException {
            if (this.suspendEncoding) {
                ((FilterOutputStream) this).out.write(theByte);
                return;
            }
            if (this.encode) {
                byte[] bArr = this.buffer;
                int i = this.position;
                this.position = i + 1;
                bArr[i] = (byte) theByte;
                if (this.position >= this.bufferLength) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.bufferLength, this.options));
                    this.lineLength += 4;
                    if (this.breakLines && this.lineLength >= 76) {
                        this.out.write(10);
                        this.lineLength = 0;
                    }
                    this.position = 0;
                    return;
                }
                return;
            }
            if (this.decodabet[theByte & 127] > -5) {
                byte[] bArr2 = this.buffer;
                int i2 = this.position;
                this.position = i2 + 1;
                bArr2[i2] = (byte) theByte;
                if (this.position >= this.bufferLength) {
                    int len = Base64.decode4to3(this.buffer, 0, this.b4, 0, this.options);
                    this.out.write(this.b4, 0, len);
                    this.position = 0;
                    return;
                }
                return;
            }
            if (this.decodabet[theByte & 127] != -5) {
                throw new IOException("Invalid character in Base64 data.");
            }
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] theBytes, int off, int len) throws IOException {
            if (this.suspendEncoding) {
                ((FilterOutputStream) this).out.write(theBytes, off, len);
                return;
            }
            for (int i = 0; i < len; i++) {
                write(theBytes[off + i]);
            }
        }

        private void flushBase64() throws IOException {
            if (this.position > 0) {
                if (this.encode) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.position, this.options));
                    this.position = 0;
                    return;
                }
                throw new IOException("Base64 input not properly padded.");
            }
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            flushBase64();
            super.close();
            this.buffer = null;
            this.out = null;
        }
    }
}
