package com.aowagie.text.pdf.codec;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.Jpeg;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfString;
import com.aowagie.text.pdf.RandomAccessFileOrArray;
import com.sun.jna.platform.win32.DBT;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import org.apache.pdfbox.filter.TIFFExtension;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/TiffImage.class */
public class TiffImage {
    private static int getDpi(TIFFField fd, int resolutionUnit) {
        if (fd == null) {
            return 0;
        }
        long[] res = fd.getAsRational(0);
        float frac = res[0] / res[1];
        int dpi = 0;
        switch (resolutionUnit) {
            case 1:
            case 2:
                dpi = (int) (((double) frac) + 0.5d);
                break;
            case 3:
                dpi = (int) ((((double) frac) * 2.54d) + 0.5d);
                break;
        }
        return dpi;
    }

    public static Image getTiffImage(RandomAccessFileOrArray s, int page) {
        return getTiffImage(s, page, false);
    }

    private static Image getTiffImage(RandomAccessFileOrArray s, int page, boolean direct) {
        Image img;
        if (page < 1) {
            throw new IllegalArgumentException("The page number must be >= 1.");
        }
        try {
            TIFFDirectory dir = new TIFFDirectory(s, page - 1);
            if (dir.isTagPresent(TIFF.TAG_TILE_WIDTH)) {
                throw new IllegalArgumentException("Tiles are not supported.");
            }
            int compression = (int) dir.getFieldAsLong(259);
            switch (compression) {
                case 2:
                case 3:
                case 4:
                case DBT.DBT_DEVICEREMOVEPENDING /* 32771 */:
                    float rotation = 0.0f;
                    if (dir.isTagPresent(274)) {
                        int rot = (int) dir.getFieldAsLong(274);
                        if (rot == 3 || rot == 4) {
                            rotation = 3.1415927f;
                        } else if (rot == 5 || rot == 8) {
                            rotation = 1.5707964f;
                        } else if (rot == 6 || rot == 7) {
                            rotation = -1.5707964f;
                        }
                    }
                    long tiffT4Options = 0;
                    long tiffT6Options = 0;
                    int fillOrder = 1;
                    int h = (int) dir.getFieldAsLong(257);
                    int w = (int) dir.getFieldAsLong(256);
                    float XYRatio = 0.0f;
                    int resolutionUnit = 2;
                    if (dir.isTagPresent(TIFF.TAG_RESOLUTION_UNIT)) {
                        resolutionUnit = (int) dir.getFieldAsLong(TIFF.TAG_RESOLUTION_UNIT);
                    }
                    int dpiX = getDpi(dir.getField(282), resolutionUnit);
                    int dpiY = getDpi(dir.getField(TIFF.TAG_Y_RESOLUTION), resolutionUnit);
                    if (resolutionUnit == 1) {
                        if (dpiY != 0) {
                            XYRatio = dpiX / dpiY;
                        }
                        dpiX = 0;
                        dpiY = 0;
                    }
                    int rowsStrip = h;
                    if (dir.isTagPresent(278)) {
                        rowsStrip = (int) dir.getFieldAsLong(278);
                    }
                    if (rowsStrip <= 0 || rowsStrip > h) {
                        rowsStrip = h;
                    }
                    long[] offset = getArrayLongShort(dir, TIFF.TAG_STRIP_OFFSETS);
                    long[] size = getArrayLongShort(dir, TIFF.TAG_STRIP_BYTE_COUNTS);
                    if ((size == null || (size.length == 1 && (size[0] == 0 || size[0] + offset[0] > s.length()))) && h == rowsStrip) {
                        size = new long[]{s.length() - ((int) offset[0])};
                    }
                    TIFFField fillOrderField = dir.getField(266);
                    if (fillOrderField != null) {
                        fillOrder = fillOrderField.getAsInt(0);
                    }
                    boolean z = fillOrder == 2;
                    int params = 0;
                    if (dir.isTagPresent(TIFF.TAG_PHOTOMETRIC_INTERPRETATION)) {
                        long photo = dir.getFieldAsLong(TIFF.TAG_PHOTOMETRIC_INTERPRETATION);
                        if (photo == 1) {
                            params = 0 | 1;
                        }
                    }
                    int imagecomp = 0;
                    switch (compression) {
                        case 2:
                        case DBT.DBT_DEVICEREMOVEPENDING /* 32771 */:
                            imagecomp = 257;
                            params |= 10;
                            break;
                        case 3:
                            imagecomp = 257;
                            params |= 12;
                            TIFFField t4OptionsField = dir.getField(TIFF.TAG_GROUP3OPTIONS);
                            if (t4OptionsField != null) {
                                tiffT4Options = t4OptionsField.getAsLong(0);
                                if ((tiffT4Options & 1) != 0) {
                                    imagecomp = 258;
                                }
                                if ((tiffT4Options & 4) != 0) {
                                    params |= 2;
                                }
                            }
                            break;
                        case 4:
                            imagecomp = 256;
                            TIFFField t6OptionsField = dir.getField(TIFF.TAG_GROUP4OPTIONS);
                            if (t6OptionsField != null) {
                                tiffT6Options = t6OptionsField.getAsLong(0);
                            }
                            break;
                    }
                    if (direct && rowsStrip == h) {
                        byte[] im = new byte[(int) size[0]];
                        s.seek(offset[0]);
                        s.readFully(im);
                        img = Image.getInstance(w, h, false, imagecomp, params, im);
                        img.setInverted(true);
                    } else {
                        int rowsLeft = h;
                        CCITTG4Encoder g4 = new CCITTG4Encoder(w);
                        for (int k = 0; k < offset.length; k++) {
                            byte[] im2 = new byte[(int) size[k]];
                            s.seek(offset[k]);
                            s.readFully(im2);
                            int height = Math.min(rowsStrip, rowsLeft);
                            TIFFFaxDecoder decoder = new TIFFFaxDecoder(fillOrder, w, height);
                            byte[] outBuf = new byte[((w + 7) / 8) * height];
                            switch (compression) {
                                case 2:
                                case DBT.DBT_DEVICEREMOVEPENDING /* 32771 */:
                                    decoder.decode1D(outBuf, im2, 0, height);
                                    g4.fax4Encode(outBuf, height);
                                    continue;
                                    rowsLeft -= rowsStrip;
                                    break;
                                case 3:
                                    try {
                                        decoder.decode2D(outBuf, im2, 0, height, tiffT4Options);
                                    } catch (RuntimeException e) {
                                        tiffT4Options ^= 4;
                                        try {
                                            decoder.decode2D(outBuf, im2, 0, height, tiffT4Options);
                                        } catch (RuntimeException e2) {
                                            throw e;
                                        }
                                    }
                                    g4.fax4Encode(outBuf, height);
                                    continue;
                                    rowsLeft -= rowsStrip;
                                    break;
                                case 4:
                                    decoder.decodeT6(outBuf, im2, 0, height, tiffT6Options);
                                    g4.fax4Encode(outBuf, height);
                                    continue;
                                    rowsLeft -= rowsStrip;
                                    break;
                                default:
                                    continue;
                                    rowsLeft -= rowsStrip;
                                    break;
                            }
                        }
                        byte[] g4pic = g4.close();
                        img = Image.getInstance(w, h, false, 256, params & 1, g4pic);
                    }
                    img.setDpi(dpiX, dpiY);
                    img.setXYRatio(XYRatio);
                    if (dir.isTagPresent(TIFF.TAG_ICC_PROFILE)) {
                        try {
                            TIFFField fd = dir.getField(TIFF.TAG_ICC_PROFILE);
                            ICC_Profile icc_prof = ICC_Profile.getInstance(fd.getAsBytes());
                            if (icc_prof.getNumComponents() == 1) {
                                img.tagICC(icc_prof);
                            }
                            break;
                        } catch (RuntimeException e3) {
                        }
                    }
                    img.setOriginalType(5);
                    if (rotation != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        img.setInitialRotation(rotation);
                    }
                    return img;
                default:
                    return getTiffImageColor(dir, s);
            }
            throw new ExceptionConverter(e);
        } catch (Exception e4) {
            throw new ExceptionConverter(e4);
        }
    }

    private static Image getTiffImageColor(TIFFDirectory dir, RandomAccessFileOrArray s) {
        Image img;
        try {
            int compression = (int) dir.getFieldAsLong(259);
            int predictor = 1;
            TIFFLZWDecoder lzwDecoder = null;
            switch (compression) {
                case 1:
                case 5:
                case 6:
                case 7:
                case 8:
                case DBT.DBT_DEVICETYPESPECIFIC /* 32773 */:
                case TIFFExtension.COMPRESSION_DEFLATE /* 32946 */:
                    int photometric = (int) dir.getFieldAsLong(TIFF.TAG_PHOTOMETRIC_INTERPRETATION);
                    switch (photometric) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 5:
                            break;
                        case 4:
                        default:
                            if (compression != 6 && compression != 7) {
                                throw new IllegalArgumentException("The photometric " + photometric + " is not supported.");
                            }
                            break;
                    }
                    float rotation = 0.0f;
                    if (dir.isTagPresent(274)) {
                        int rot = (int) dir.getFieldAsLong(274);
                        if (rot == 3 || rot == 4) {
                            rotation = 3.1415927f;
                        } else if (rot == 5 || rot == 8) {
                            rotation = 1.5707964f;
                        } else if (rot == 6 || rot == 7) {
                            rotation = -1.5707964f;
                        }
                    }
                    if (dir.isTagPresent(TIFF.TAG_PLANAR_CONFIGURATION) && dir.getFieldAsLong(TIFF.TAG_PLANAR_CONFIGURATION) == 2) {
                        throw new IllegalArgumentException("Planar images are not supported.");
                    }
                    if (dir.isTagPresent(TIFF.TAG_EXTRA_SAMPLES)) {
                        throw new IllegalArgumentException("Extra samples are not supported.");
                    }
                    int samplePerPixel = 1;
                    if (dir.isTagPresent(277)) {
                        samplePerPixel = (int) dir.getFieldAsLong(277);
                    }
                    int bitsPerSample = 1;
                    if (dir.isTagPresent(258)) {
                        bitsPerSample = (int) dir.getFieldAsLong(258);
                    }
                    switch (bitsPerSample) {
                        case 1:
                        case 2:
                        case 4:
                        case 8:
                            int h = (int) dir.getFieldAsLong(257);
                            int w = (int) dir.getFieldAsLong(256);
                            int resolutionUnit = 2;
                            if (dir.isTagPresent(TIFF.TAG_RESOLUTION_UNIT)) {
                                resolutionUnit = (int) dir.getFieldAsLong(TIFF.TAG_RESOLUTION_UNIT);
                            }
                            int dpiX = getDpi(dir.getField(282), resolutionUnit);
                            int dpiY = getDpi(dir.getField(TIFF.TAG_Y_RESOLUTION), resolutionUnit);
                            int fillOrder = 1;
                            TIFFField fillOrderField = dir.getField(266);
                            if (fillOrderField != null) {
                                fillOrder = fillOrderField.getAsInt(0);
                            }
                            boolean reverse = fillOrder == 2;
                            int rowsStrip = h;
                            if (dir.isTagPresent(278)) {
                                rowsStrip = (int) dir.getFieldAsLong(278);
                            }
                            if (rowsStrip <= 0 || rowsStrip > h) {
                                rowsStrip = h;
                            }
                            long[] offset = getArrayLongShort(dir, TIFF.TAG_STRIP_OFFSETS);
                            long[] size = getArrayLongShort(dir, TIFF.TAG_STRIP_BYTE_COUNTS);
                            if ((size == null || (size.length == 1 && (size[0] == 0 || size[0] + offset[0] > s.length()))) && h == rowsStrip) {
                                size = new long[]{s.length() - ((int) offset[0])};
                            }
                            if (compression == 5) {
                                TIFFField predictorField = dir.getField(317);
                                if (predictorField != null) {
                                    predictor = predictorField.getAsInt(0);
                                    if (predictor != 1 && predictor != 2) {
                                        throw new RuntimeException("Illegal value for Predictor in TIFF file.");
                                    }
                                    if (predictor == 2 && bitsPerSample != 8) {
                                        throw new RuntimeException(bitsPerSample + "-bit samples are not supported for Horizontal differencing Predictor.");
                                    }
                                }
                                lzwDecoder = new TIFFLZWDecoder(w, predictor, samplePerPixel);
                            }
                            int rowsLeft = h;
                            ByteArrayOutputStream stream = null;
                            DeflaterOutputStream zip = null;
                            CCITTG4Encoder g4 = null;
                            if (bitsPerSample == 1 && samplePerPixel == 1) {
                                g4 = new CCITTG4Encoder(w);
                            } else {
                                stream = new ByteArrayOutputStream();
                                if (compression != 6 && compression != 7) {
                                    zip = new DeflaterOutputStream(stream);
                                }
                            }
                            if (compression == 6) {
                                if (!dir.isTagPresent(TIFF.TAG_JPEG_INTERCHANGE_FORMAT)) {
                                    throw new IOException("Missing tag(s) for OJPEG compression.");
                                }
                                int jpegOffset = (int) dir.getFieldAsLong(TIFF.TAG_JPEG_INTERCHANGE_FORMAT);
                                int jpegLength = s.length() - jpegOffset;
                                if (dir.isTagPresent(TIFF.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH)) {
                                    jpegLength = ((int) dir.getFieldAsLong(TIFF.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH)) + ((int) size[0]);
                                }
                                byte[] jpeg = new byte[Math.min(jpegLength, s.length() - jpegOffset)];
                                int posFilePointer = s.getFilePointer();
                                s.seek(posFilePointer + jpegOffset);
                                s.readFully(jpeg);
                                img = new Jpeg(jpeg);
                            } else if (compression == 7) {
                                if (size.length > 1) {
                                    throw new IOException("Compression JPEG is only supported with a single strip. This image has " + size.length + " strips.");
                                }
                                byte[] jpeg2 = new byte[(int) size[0]];
                                s.seek(offset[0]);
                                s.readFully(jpeg2);
                                img = new Jpeg(jpeg2);
                            } else {
                                for (int k = 0; k < offset.length; k++) {
                                    byte[] im = new byte[(int) size[k]];
                                    s.seek(offset[k]);
                                    s.readFully(im);
                                    int height = Math.min(rowsStrip, rowsLeft);
                                    byte[] outBuf = null;
                                    if (compression != 1) {
                                        outBuf = new byte[((((w * bitsPerSample) * samplePerPixel) + 7) / 8) * height];
                                    }
                                    if (reverse) {
                                        TIFFFaxDecoder.reverseBits(im);
                                    }
                                    switch (compression) {
                                        case 1:
                                            outBuf = im;
                                            break;
                                        case 5:
                                            lzwDecoder.decode(im, outBuf, height);
                                            break;
                                        case 8:
                                        case TIFFExtension.COMPRESSION_DEFLATE /* 32946 */:
                                            inflate(im, outBuf);
                                            break;
                                        case DBT.DBT_DEVICETYPESPECIFIC /* 32773 */:
                                            decodePackbits(im, outBuf);
                                            break;
                                    }
                                    if (bitsPerSample == 1 && samplePerPixel == 1) {
                                        g4.fax4Encode(outBuf, height);
                                    } else {
                                        zip.write(outBuf);
                                    }
                                    rowsLeft -= rowsStrip;
                                }
                                if (bitsPerSample == 1 && samplePerPixel == 1) {
                                    img = Image.getInstance(w, h, false, 256, photometric == 1 ? 1 : 0, g4.close());
                                } else {
                                    zip.close();
                                    img = Image.getInstance(w, h, samplePerPixel, bitsPerSample, stream.toByteArray());
                                    img.setDeflated(true);
                                }
                            }
                            img.setDpi(dpiX, dpiY);
                            if (compression != 6 && compression != 7) {
                                if (dir.isTagPresent(TIFF.TAG_ICC_PROFILE)) {
                                    try {
                                        TIFFField fd = dir.getField(TIFF.TAG_ICC_PROFILE);
                                        ICC_Profile icc_prof = ICC_Profile.getInstance(fd.getAsBytes());
                                        if (samplePerPixel == icc_prof.getNumComponents()) {
                                            img.tagICC(icc_prof);
                                        }
                                        break;
                                    } catch (RuntimeException e) {
                                    }
                                }
                                if (dir.isTagPresent(320)) {
                                    TIFFField fd2 = dir.getField(320);
                                    char[] rgb = fd2.getAsChars();
                                    byte[] palette = new byte[rgb.length];
                                    int gColor = rgb.length / 3;
                                    int bColor = gColor * 2;
                                    for (int k2 = 0; k2 < gColor; k2++) {
                                        palette[k2 * 3] = (byte) (rgb[k2] >>> '\b');
                                        palette[(k2 * 3) + 1] = (byte) (rgb[k2 + gColor] >>> '\b');
                                        palette[(k2 * 3) + 2] = (byte) (rgb[k2 + bColor] >>> '\b');
                                    }
                                    PdfArray indexed = new PdfArray();
                                    indexed.add(PdfName.INDEXED);
                                    indexed.add(PdfName.DEVICERGB);
                                    indexed.add(new PdfNumber(gColor - 1));
                                    indexed.add(new PdfString(palette));
                                    PdfDictionary additional = new PdfDictionary();
                                    additional.put(PdfName.COLORSPACE, indexed);
                                    img.setAdditional(additional);
                                }
                                img.setOriginalType(5);
                            }
                            if (photometric == 0) {
                                img.setInverted(true);
                            }
                            if (rotation != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                                img.setInitialRotation(rotation);
                            }
                            return img;
                        case 3:
                        case 5:
                        case 6:
                        case 7:
                        default:
                            throw new IllegalArgumentException("Bits per sample " + bitsPerSample + " is not supported.");
                    }
                default:
                    throw new IllegalArgumentException("The compression " + compression + " is not supported.");
            }
        } catch (Exception e2) {
            throw new ExceptionConverter(e2);
        }
    }

    private static long[] getArrayLongShort(TIFFDirectory dir, int tag) {
        long[] offset;
        TIFFField field = dir.getField(tag);
        if (field == null) {
            return null;
        }
        if (field.getType() == 4) {
            offset = field.getAsLongs();
        } else {
            char[] temp = field.getAsChars();
            offset = new long[temp.length];
            for (int k = 0; k < temp.length; k++) {
                offset[k] = temp[k];
            }
        }
        return offset;
    }

    private static void decodePackbits(byte[] data, byte[] dst) {
        int srcCount = 0;
        int dstCount = 0;
        while (dstCount < dst.length) {
            try {
                int i = srcCount;
                srcCount++;
                byte b = data[i];
                if (b >= 0 && b <= 127) {
                    for (int i2 = 0; i2 < b + 1; i2++) {
                        int i3 = dstCount;
                        dstCount++;
                        int i4 = srcCount;
                        srcCount++;
                        dst[i3] = data[i4];
                    }
                } else if (b <= -1 && b >= -127) {
                    srcCount++;
                    byte repeat = data[srcCount];
                    for (int i5 = 0; i5 < (-b) + 1; i5++) {
                        int i6 = dstCount;
                        dstCount++;
                        dst[i6] = repeat;
                    }
                } else {
                    srcCount++;
                }
            } catch (Exception e) {
                return;
            }
        }
    }

    private static void inflate(byte[] deflated, byte[] inflated) {
        Inflater inflater = new Inflater();
        inflater.setInput(deflated);
        try {
            inflater.inflate(inflated);
        } catch (DataFormatException dfe) {
            throw new ExceptionConverter(dfe);
        }
    }
}
