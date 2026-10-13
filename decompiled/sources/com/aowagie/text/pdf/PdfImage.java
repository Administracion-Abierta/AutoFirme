package com.aowagie.text.pdf;

import com.aowagie.text.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfImage.class */
public class PdfImage extends PdfStream {
    private static final int TRANSFERSIZE = 4096;
    private PdfName name;

    PdfImage(Image image, String name, PdfIndirectReference maskRef) throws BadPdfFormatException {
        InputStream is;
        String errorID;
        this.name = null;
        this.name = new PdfName(name);
        put(PdfName.TYPE, PdfName.XOBJECT);
        put(PdfName.SUBTYPE, PdfName.IMAGE);
        put(PdfName.WIDTH, new PdfNumber(image.getWidth()));
        put(PdfName.HEIGHT, new PdfNumber(image.getHeight()));
        if (image.getLayer() != null) {
            put(PdfName.OC, image.getLayer().getRef());
        }
        if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 255)) {
            put(PdfName.IMAGEMASK, PdfBoolean.PDFTRUE);
        }
        if (maskRef != null) {
            if (image.isSmask()) {
                put(PdfName.SMASK, maskRef);
            } else {
                put(PdfName.MASK, maskRef);
            }
        }
        if (image.isMask() && image.isInverted()) {
            put(PdfName.DECODE, new PdfLiteral("[1 0]"));
        }
        if (image.isInterpolation()) {
            put(PdfName.INTERPOLATE, PdfBoolean.PDFTRUE);
        }
        InputStream is2 = null;
        try {
            try {
                if (image.isImgRaw()) {
                    int colorspace = image.getColorspace();
                    int[] transparency = image.getTransparency();
                    if (transparency != null && !image.isMask() && maskRef == null) {
                        String s = "[";
                        for (int element : transparency) {
                            s = s + element + " ";
                        }
                        put(PdfName.MASK, new PdfLiteral(s + "]"));
                    }
                    this.bytes = image.getRawData();
                    put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                    int bpc = image.getBpc();
                    if (bpc > 255) {
                        if (!image.isMask()) {
                            put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                        }
                        put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
                        put(PdfName.FILTER, PdfName.CCITTFAXDECODE);
                        int k = bpc - 257;
                        PdfDictionary decodeparms = new PdfDictionary();
                        if (k != 0) {
                            decodeparms.put(PdfName.K, new PdfNumber(k));
                        }
                        if ((colorspace & 1) != 0) {
                            decodeparms.put(PdfName.BLACKIS1, PdfBoolean.PDFTRUE);
                        }
                        if ((colorspace & 2) != 0) {
                            decodeparms.put(PdfName.ENCODEDBYTEALIGN, PdfBoolean.PDFTRUE);
                        }
                        if ((colorspace & 4) != 0) {
                            decodeparms.put(PdfName.ENDOFLINE, PdfBoolean.PDFTRUE);
                        }
                        if ((colorspace & 8) != 0) {
                            decodeparms.put(PdfName.ENDOFBLOCK, PdfBoolean.PDFFALSE);
                        }
                        decodeparms.put(PdfName.COLUMNS, new PdfNumber(image.getWidth()));
                        decodeparms.put(PdfName.ROWS, new PdfNumber(image.getHeight()));
                        put(PdfName.DECODEPARMS, decodeparms);
                    } else {
                        switch (colorspace) {
                            case 1:
                                put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                if (image.isInverted()) {
                                    put(PdfName.DECODE, new PdfLiteral("[1 0]"));
                                }
                                break;
                            case 2:
                            case 4:
                            default:
                                put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                                if (image.isInverted()) {
                                    put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
                                }
                                break;
                            case 3:
                                put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                if (image.isInverted()) {
                                    put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0]"));
                                }
                                break;
                        }
                        PdfDictionary additional = image.getAdditional();
                        if (additional != null) {
                            putAll(additional);
                        }
                        if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8)) {
                            remove(PdfName.COLORSPACE);
                        }
                        put(PdfName.BITSPERCOMPONENT, new PdfNumber(image.getBpc()));
                        if (image.isDeflated()) {
                            put(PdfName.FILTER, PdfName.FLATEDECODE);
                        } else {
                            flateCompress(image.getCompressionLevel());
                        }
                    }
                    if (0 != 0) {
                        try {
                            is2.close();
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    }
                    return;
                }
                if (image.getRawData() == null) {
                    is = image.getUrl().openStream();
                    errorID = image.getUrl().toString();
                } else {
                    is = new ByteArrayInputStream(image.getRawData());
                    errorID = "Byte array";
                }
                switch (image.type()) {
                    case 32:
                        put(PdfName.FILTER, PdfName.DCTDECODE);
                        switch (image.getColorspace()) {
                            case 1:
                                put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                break;
                            case 3:
                                put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                break;
                            default:
                                put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                                if (image.isInverted()) {
                                    put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
                                }
                                break;
                        }
                        put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                        if (image.getRawData() != null) {
                            this.bytes = image.getRawData();
                            put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                            if (is != null) {
                                try {
                                    is.close();
                                    return;
                                } catch (Exception e2) {
                                    return;
                                }
                            }
                            return;
                        }
                        this.streamBytes = new ByteArrayOutputStream();
                        transferBytes(is, this.streamBytes, -1);
                        break;
                        break;
                    case 33:
                        put(PdfName.FILTER, PdfName.JPXDECODE);
                        if (image.getColorspace() > 0) {
                            switch (image.getColorspace()) {
                                case 1:
                                    put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                    break;
                                case 3:
                                    put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                    break;
                                default:
                                    put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                                    break;
                            }
                            put(PdfName.BITSPERCOMPONENT, new PdfNumber(image.getBpc()));
                        }
                        if (image.getRawData() != null) {
                            this.bytes = image.getRawData();
                            put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                            if (is != null) {
                                try {
                                    is.close();
                                    return;
                                } catch (Exception e3) {
                                    return;
                                }
                            }
                            return;
                        }
                        this.streamBytes = new ByteArrayOutputStream();
                        transferBytes(is, this.streamBytes, -1);
                        break;
                        break;
                    case 34:
                    case 35:
                    default:
                        throw new BadPdfFormatException(errorID + " is an unknown Image format.");
                    case 36:
                        put(PdfName.FILTER, PdfName.JBIG2DECODE);
                        put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                        put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
                        if (image.getRawData() != null) {
                            this.bytes = image.getRawData();
                            put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                            if (is != null) {
                                try {
                                    is.close();
                                    return;
                                } catch (Exception e4) {
                                    return;
                                }
                            }
                            return;
                        }
                        this.streamBytes = new ByteArrayOutputStream();
                        transferBytes(is, this.streamBytes, -1);
                        break;
                        break;
                }
                put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e5) {
                    }
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        is2.close();
                    } catch (Exception e6) {
                    }
                }
                throw th;
            }
        } catch (IOException ioe) {
            throw new BadPdfFormatException(ioe.getMessage());
        }
    }

    PdfName name() {
        return this.name;
    }

    private static void transferBytes(InputStream in, OutputStream out, int len) throws IOException {
        int size;
        byte[] buffer = new byte[4096];
        if (len < 0) {
            len = 134217727;
        }
        while (len != 0 && (size = in.read(buffer, 0, Math.min(len, 4096))) >= 0) {
            out.write(buffer, 0, size);
            len -= size;
        }
    }
}
