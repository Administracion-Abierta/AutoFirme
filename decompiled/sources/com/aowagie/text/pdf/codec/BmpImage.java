package com.aowagie.text.pdf.codec;

import com.aowagie.text.BadElementException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.ImgRaw;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfString;
import com.sun.jna.platform.win32.Winspool;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/BmpImage.class */
public class BmpImage {
    private InputStream inputStream;
    private long bitmapFileSize;
    private long compression;
    private long imageSize;
    private byte[] palette;
    private int imageType;
    private int numBands;
    private boolean isBottomUp;
    private int bitsPerPixel;
    private int redMask;
    private int greenMask;
    private int blueMask;
    private int alphaMask;
    private long xPelsPerMeter;
    private long yPelsPerMeter;
    private static final int VERSION_2_1_BIT = 0;
    private static final int VERSION_2_4_BIT = 1;
    private static final int VERSION_2_8_BIT = 2;
    private static final int VERSION_2_24_BIT = 3;
    private static final int VERSION_3_1_BIT = 4;
    private static final int VERSION_3_4_BIT = 5;
    private static final int VERSION_3_8_BIT = 6;
    private static final int VERSION_3_24_BIT = 7;
    private static final int VERSION_3_NT_16_BIT = 8;
    private static final int VERSION_3_NT_32_BIT = 9;
    private static final int VERSION_4_1_BIT = 10;
    private static final int VERSION_4_4_BIT = 11;
    private static final int VERSION_4_8_BIT = 12;
    private static final int VERSION_4_16_BIT = 13;
    private static final int VERSION_4_24_BIT = 14;
    private static final int VERSION_4_32_BIT = 15;
    private static final int LCS_CALIBRATED_RGB = 0;
    private static final int LCS_sRGB = 1;
    private static final int LCS_CMYK = 2;
    private static final int BI_RGB = 0;
    private static final int BI_RLE8 = 1;
    private static final int BI_RLE4 = 2;
    private static final int BI_BITFIELDS = 3;
    private int width;
    private int height;
    private final HashMap properties = new LinkedHashMap();
    private long bitmapOffset = 0;

    private BmpImage(InputStream is, boolean noHeader, int size) throws IOException {
        this.bitmapFileSize = size;
        process(is, noHeader);
    }

    public static Image getImage(URL url) throws IOException {
        InputStream is = null;
        try {
            InputStream is2 = url.openStream();
            Image img = getImage(is2);
            img.setUrl(url);
            if (is2 != null) {
            }
            return img;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Image getImage(InputStream is) throws IOException {
        return getImage(is, false, 0);
    }

    public static Image getImage(InputStream is, boolean noHeader, int size) throws IOException {
        BmpImage bmp = new BmpImage(is, noHeader, size);
        try {
            Image img = bmp.getImage();
            img.setDpi((int) ((bmp.xPelsPerMeter * 0.0254d) + 0.5d), (int) ((bmp.yPelsPerMeter * 0.0254d) + 0.5d));
            img.setOriginalType(4);
            return img;
        } catch (BadElementException be) {
            throw new ExceptionConverter(be);
        }
    }

    public static Image getImage(byte[] data) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        Image img = getImage(is);
        img.setOriginalData(data);
        return img;
    }

    private void process(InputStream stream, boolean noHeader) throws IOException {
        if (noHeader || (stream instanceof BufferedInputStream)) {
            this.inputStream = stream;
        } else {
            this.inputStream = new BufferedInputStream(stream);
        }
        if (!noHeader) {
            if (readUnsignedByte(this.inputStream) != 66 || readUnsignedByte(this.inputStream) != 77) {
                throw new RuntimeException("Invalid magic value for BMP file.");
            }
            this.bitmapFileSize = readDWord(this.inputStream);
            readWord(this.inputStream);
            readWord(this.inputStream);
            this.bitmapOffset = readDWord(this.inputStream);
        }
        long size = readDWord(this.inputStream);
        if (size == 12) {
            this.width = readWord(this.inputStream);
            this.height = readWord(this.inputStream);
        } else {
            this.width = readLong(this.inputStream);
            this.height = readLong(this.inputStream);
        }
        int planes = readWord(this.inputStream);
        this.bitsPerPixel = readWord(this.inputStream);
        this.properties.put("color_planes", Integer.valueOf(planes));
        this.properties.put("bits_per_pixel", Integer.valueOf(this.bitsPerPixel));
        this.numBands = 3;
        if (this.bitmapOffset == 0) {
            this.bitmapOffset = size;
        }
        if (size == 12) {
            this.properties.put("bmp_version", "BMP v. 2.x");
            if (this.bitsPerPixel == 1) {
                this.imageType = 0;
            } else if (this.bitsPerPixel == 4) {
                this.imageType = 1;
            } else if (this.bitsPerPixel == 8) {
                this.imageType = 2;
            } else if (this.bitsPerPixel == 24) {
                this.imageType = 3;
            }
            int numberOfEntries = (int) (((this.bitmapOffset - 14) - size) / 3);
            int sizeOfPalette = numberOfEntries * 3;
            if (this.bitmapOffset == size) {
                switch (this.imageType) {
                    case 0:
                        sizeOfPalette = 6;
                        break;
                    case 1:
                        sizeOfPalette = 48;
                        break;
                    case 2:
                        sizeOfPalette = 768;
                        break;
                    case 3:
                        sizeOfPalette = 0;
                        break;
                }
                this.bitmapOffset = size + ((long) sizeOfPalette);
            }
            readPalette(sizeOfPalette);
        } else {
            this.compression = readDWord(this.inputStream);
            this.imageSize = readDWord(this.inputStream);
            this.xPelsPerMeter = readLong(this.inputStream);
            this.yPelsPerMeter = readLong(this.inputStream);
            long colorsUsed = readDWord(this.inputStream);
            long colorsImportant = readDWord(this.inputStream);
            switch ((int) this.compression) {
                case 0:
                    this.properties.put("compression", "BI_RGB");
                    break;
                case 1:
                    this.properties.put("compression", "BI_RLE8");
                    break;
                case 2:
                    this.properties.put("compression", "BI_RLE4");
                    break;
                case 3:
                    this.properties.put("compression", "BI_BITFIELDS");
                    break;
            }
            this.properties.put("x_pixels_per_meter", Long.valueOf(this.xPelsPerMeter));
            this.properties.put("y_pixels_per_meter", Long.valueOf(this.yPelsPerMeter));
            this.properties.put("colors_used", Long.valueOf(colorsUsed));
            this.properties.put("colors_important", Long.valueOf(colorsImportant));
            if (size == 40) {
                switch ((int) this.compression) {
                    case 0:
                    case 1:
                    case 2:
                        if (this.bitsPerPixel == 1) {
                            this.imageType = 4;
                        } else if (this.bitsPerPixel == 4) {
                            this.imageType = 5;
                        } else if (this.bitsPerPixel == 8) {
                            this.imageType = 6;
                        } else if (this.bitsPerPixel == 24) {
                            this.imageType = 7;
                        } else if (this.bitsPerPixel == 16) {
                            this.imageType = 8;
                            this.redMask = 31744;
                            this.greenMask = 992;
                            this.blueMask = 31;
                            this.properties.put("red_mask", Integer.valueOf(this.redMask));
                            this.properties.put("green_mask", Integer.valueOf(this.greenMask));
                            this.properties.put("blue_mask", Integer.valueOf(this.blueMask));
                        } else if (this.bitsPerPixel == 32) {
                            this.imageType = 9;
                            this.redMask = Winspool.PRINTER_ENUM_ICONMASK;
                            this.greenMask = 65280;
                            this.blueMask = 255;
                            this.properties.put("red_mask", Integer.valueOf(this.redMask));
                            this.properties.put("green_mask", Integer.valueOf(this.greenMask));
                            this.properties.put("blue_mask", Integer.valueOf(this.blueMask));
                        }
                        int numberOfEntries2 = (int) (((this.bitmapOffset - 14) - size) / 4);
                        int sizeOfPalette2 = numberOfEntries2 * 4;
                        if (this.bitmapOffset == size) {
                            switch (this.imageType) {
                                case 4:
                                    sizeOfPalette2 = ((int) (colorsUsed == 0 ? 2L : colorsUsed)) * 4;
                                    break;
                                case 5:
                                    sizeOfPalette2 = ((int) (colorsUsed == 0 ? 16L : colorsUsed)) * 4;
                                    break;
                                case 6:
                                    sizeOfPalette2 = ((int) (colorsUsed == 0 ? 256L : colorsUsed)) * 4;
                                    break;
                                default:
                                    sizeOfPalette2 = 0;
                                    break;
                            }
                            this.bitmapOffset = size + ((long) sizeOfPalette2);
                        }
                        readPalette(sizeOfPalette2);
                        this.properties.put("bmp_version", "BMP v. 3.x");
                        break;
                    case 3:
                        if (this.bitsPerPixel == 16) {
                            this.imageType = 8;
                        } else if (this.bitsPerPixel == 32) {
                            this.imageType = 9;
                        }
                        this.redMask = (int) readDWord(this.inputStream);
                        this.greenMask = (int) readDWord(this.inputStream);
                        this.blueMask = (int) readDWord(this.inputStream);
                        this.properties.put("red_mask", Integer.valueOf(this.redMask));
                        this.properties.put("green_mask", Integer.valueOf(this.greenMask));
                        this.properties.put("blue_mask", Integer.valueOf(this.blueMask));
                        if (colorsUsed != 0) {
                            int sizeOfPalette3 = ((int) colorsUsed) * 4;
                            readPalette(sizeOfPalette3);
                        }
                        this.properties.put("bmp_version", "BMP v. 3.x NT");
                        break;
                    default:
                        throw new RuntimeException("Invalid compression specified in BMP file.");
                }
            } else if (size == 108) {
                this.properties.put("bmp_version", "BMP v. 4.x");
                this.redMask = (int) readDWord(this.inputStream);
                this.greenMask = (int) readDWord(this.inputStream);
                this.blueMask = (int) readDWord(this.inputStream);
                this.alphaMask = (int) readDWord(this.inputStream);
                long csType = readDWord(this.inputStream);
                int redX = readLong(this.inputStream);
                int redY = readLong(this.inputStream);
                int redZ = readLong(this.inputStream);
                int greenX = readLong(this.inputStream);
                int greenY = readLong(this.inputStream);
                int greenZ = readLong(this.inputStream);
                int blueX = readLong(this.inputStream);
                int blueY = readLong(this.inputStream);
                int blueZ = readLong(this.inputStream);
                long gammaRed = readDWord(this.inputStream);
                long gammaGreen = readDWord(this.inputStream);
                long gammaBlue = readDWord(this.inputStream);
                if (this.bitsPerPixel == 1) {
                    this.imageType = 10;
                } else if (this.bitsPerPixel == 4) {
                    this.imageType = 11;
                } else if (this.bitsPerPixel == 8) {
                    this.imageType = 12;
                } else if (this.bitsPerPixel == 16) {
                    this.imageType = 13;
                    if (((int) this.compression) == 0) {
                        this.redMask = 31744;
                        this.greenMask = 992;
                        this.blueMask = 31;
                    }
                } else if (this.bitsPerPixel == 24) {
                    this.imageType = 14;
                } else if (this.bitsPerPixel == 32) {
                    this.imageType = 15;
                    if (((int) this.compression) == 0) {
                        this.redMask = Winspool.PRINTER_ENUM_ICONMASK;
                        this.greenMask = 65280;
                        this.blueMask = 255;
                    }
                }
                this.properties.put("red_mask", Integer.valueOf(this.redMask));
                this.properties.put("green_mask", Integer.valueOf(this.greenMask));
                this.properties.put("blue_mask", Integer.valueOf(this.blueMask));
                this.properties.put("alpha_mask", Integer.valueOf(this.alphaMask));
                int numberOfEntries3 = (int) (((this.bitmapOffset - 14) - size) / 4);
                int sizeOfPalette4 = numberOfEntries3 * 4;
                if (this.bitmapOffset == size) {
                    switch (this.imageType) {
                        case 10:
                            sizeOfPalette4 = ((int) (colorsUsed == 0 ? 2L : colorsUsed)) * 4;
                            break;
                        case 11:
                            sizeOfPalette4 = ((int) (colorsUsed == 0 ? 16L : colorsUsed)) * 4;
                            break;
                        case 12:
                            sizeOfPalette4 = ((int) (colorsUsed == 0 ? 256L : colorsUsed)) * 4;
                            break;
                        default:
                            sizeOfPalette4 = 0;
                            break;
                    }
                    this.bitmapOffset = size + ((long) sizeOfPalette4);
                }
                readPalette(sizeOfPalette4);
                switch ((int) csType) {
                    case 0:
                        this.properties.put("color_space", "LCS_CALIBRATED_RGB");
                        this.properties.put("redX", Integer.valueOf(redX));
                        this.properties.put("redY", Integer.valueOf(redY));
                        this.properties.put("redZ", Integer.valueOf(redZ));
                        this.properties.put("greenX", Integer.valueOf(greenX));
                        this.properties.put("greenY", Integer.valueOf(greenY));
                        this.properties.put("greenZ", Integer.valueOf(greenZ));
                        this.properties.put("blueX", Integer.valueOf(blueX));
                        this.properties.put("blueY", Integer.valueOf(blueY));
                        this.properties.put("blueZ", Integer.valueOf(blueZ));
                        this.properties.put("gamma_red", Long.valueOf(gammaRed));
                        this.properties.put("gamma_green", Long.valueOf(gammaGreen));
                        this.properties.put("gamma_blue", Long.valueOf(gammaBlue));
                        throw new RuntimeException("Not implemented yet.");
                    case 1:
                        this.properties.put("color_space", "LCS_sRGB");
                        break;
                    case 2:
                        this.properties.put("color_space", "LCS_CMYK");
                        throw new RuntimeException("Not implemented yet.");
                }
            } else {
                this.properties.put("bmp_version", "BMP v. 5.x");
                throw new RuntimeException("BMP version 5 not implemented yet.");
            }
        }
        if (this.height > 0) {
            this.isBottomUp = true;
        } else {
            this.isBottomUp = false;
            this.height = Math.abs(this.height);
        }
        if (this.bitsPerPixel != 1 && this.bitsPerPixel != 4 && this.bitsPerPixel != 8) {
            if (this.bitsPerPixel == 16) {
                this.numBands = 3;
                return;
            } else if (this.bitsPerPixel == 32) {
                this.numBands = this.alphaMask == 0 ? 3 : 4;
                return;
            } else {
                this.numBands = 3;
                return;
            }
        }
        this.numBands = 1;
        if (this.imageType == 0 || this.imageType == 1 || this.imageType == 2) {
            int sizep = this.palette.length / 3;
            if (sizep > 256) {
                sizep = 256;
            }
            byte[] r = new byte[sizep];
            byte[] g = new byte[sizep];
            byte[] b = new byte[sizep];
            for (int i = 0; i < sizep; i++) {
                int off = 3 * i;
                b[i] = this.palette[off];
                g[i] = this.palette[off + 1];
                r[i] = this.palette[off + 2];
            }
            return;
        }
        int sizep2 = this.palette.length / 4;
        if (sizep2 > 256) {
            sizep2 = 256;
        }
        byte[] r2 = new byte[sizep2];
        byte[] g2 = new byte[sizep2];
        byte[] b2 = new byte[sizep2];
        for (int i2 = 0; i2 < sizep2; i2++) {
            int off2 = 4 * i2;
            b2[i2] = this.palette[off2];
            g2[i2] = this.palette[off2 + 1];
            r2[i2] = this.palette[off2 + 2];
        }
    }

    private byte[] getPalette(int group) {
        if (this.palette == null) {
            return null;
        }
        byte[] np = new byte[(this.palette.length / group) * 3];
        int e = this.palette.length / group;
        for (int k = 0; k < e; k++) {
            int src = k * group;
            int dest = k * 3;
            int src2 = src + 1;
            np[dest + 2] = this.palette[src];
            np[dest + 1] = this.palette[src2];
            np[dest] = this.palette[src2 + 1];
        }
        return np;
    }

    private Image getImage() throws BadElementException, IOException {
        switch (this.imageType) {
            case 0:
                return read1Bit(3);
            case 1:
                return read4Bit(3);
            case 2:
                return read8Bit(3);
            case 3:
                byte[] bdata = new byte[this.width * this.height * 3];
                read24Bit(bdata);
                return new ImgRaw(this.width, this.height, 3, 8, bdata);
            case 4:
                return read1Bit(4);
            case 5:
                switch ((int) this.compression) {
                    case 0:
                        return read4Bit(4);
                    case 2:
                        return readRLE4();
                    default:
                        throw new RuntimeException("Invalid compression specified for BMP file.");
                }
            case 6:
                switch ((int) this.compression) {
                    case 0:
                        return read8Bit(4);
                    case 1:
                        return readRLE8();
                    default:
                        throw new RuntimeException("Invalid compression specified for BMP file.");
                }
            case 7:
                byte[] bdata2 = new byte[this.width * this.height * 3];
                read24Bit(bdata2);
                return new ImgRaw(this.width, this.height, 3, 8, bdata2);
            case 8:
                return read1632Bit(false);
            case 9:
                return read1632Bit(true);
            case 10:
                return read1Bit(4);
            case 11:
                switch ((int) this.compression) {
                    case 0:
                        return read4Bit(4);
                    case 2:
                        return readRLE4();
                    default:
                        throw new RuntimeException("Invalid compression specified for BMP file.");
                }
            case 12:
                switch ((int) this.compression) {
                    case 0:
                        return read8Bit(4);
                    case 1:
                        return readRLE8();
                    default:
                        throw new RuntimeException("Invalid compression specified for BMP file.");
                }
            case 13:
                return read1632Bit(false);
            case 14:
                byte[] bdata3 = new byte[this.width * this.height * 3];
                read24Bit(bdata3);
                return new ImgRaw(this.width, this.height, 3, 8, bdata3);
            case 15:
                return read1632Bit(true);
            default:
                return null;
        }
    }

    private Image indexedModel(byte[] bdata, int bpc, int paletteEntries) throws BadElementException {
        Image img = new ImgRaw(this.width, this.height, 1, bpc, bdata);
        PdfArray colorspace = new PdfArray();
        colorspace.add(PdfName.INDEXED);
        colorspace.add(PdfName.DEVICERGB);
        byte[] np = getPalette(paletteEntries);
        int len = np.length;
        colorspace.add(new PdfNumber((len / 3) - 1));
        colorspace.add(new PdfString(np));
        PdfDictionary ad = new PdfDictionary();
        ad.put(PdfName.COLORSPACE, colorspace);
        img.setAdditional(ad);
        return img;
    }

    private void readPalette(int sizeOfPalette) throws IOException {
        if (sizeOfPalette == 0) {
            return;
        }
        this.palette = new byte[sizeOfPalette];
        int i = 0;
        while (true) {
            int bytesRead = i;
            if (bytesRead < sizeOfPalette) {
                int r = this.inputStream.read(this.palette, bytesRead, sizeOfPalette - bytesRead);
                if (r < 0) {
                    throw new RuntimeException("incomplete palette");
                }
                i = bytesRead + r;
            } else {
                this.properties.put("palette", this.palette);
                return;
            }
        }
    }

    private Image read1Bit(int paletteEntries) throws BadElementException, IOException {
        byte[] bdata = new byte[((this.width + 7) / 8) * this.height];
        int padding = 0;
        int bytesPerScanline = (int) Math.ceil(((double) this.width) / 8.0d);
        int remainder = bytesPerScanline % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        int imSize = (bytesPerScanline + padding) * this.height;
        byte[] values = new byte[imSize];
        int i = 0;
        while (true) {
            int bytesRead = i;
            if (bytesRead >= imSize) {
                break;
            }
            i = bytesRead + this.inputStream.read(values, bytesRead, imSize - bytesRead);
        }
        if (this.isBottomUp) {
            for (int i2 = 0; i2 < this.height; i2++) {
                System.arraycopy(values, imSize - ((i2 + 1) * (bytesPerScanline + padding)), bdata, i2 * bytesPerScanline, bytesPerScanline);
            }
        } else {
            for (int i3 = 0; i3 < this.height; i3++) {
                System.arraycopy(values, i3 * (bytesPerScanline + padding), bdata, i3 * bytesPerScanline, bytesPerScanline);
            }
        }
        return indexedModel(bdata, 1, paletteEntries);
    }

    private Image read4Bit(int paletteEntries) throws BadElementException, IOException {
        byte[] bdata = new byte[((this.width + 1) / 2) * this.height];
        int padding = 0;
        int bytesPerScanline = (int) Math.ceil(((double) this.width) / 2.0d);
        int remainder = bytesPerScanline % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        int imSize = (bytesPerScanline + padding) * this.height;
        byte[] values = new byte[imSize];
        int i = 0;
        while (true) {
            int bytesRead = i;
            if (bytesRead >= imSize) {
                break;
            }
            i = bytesRead + this.inputStream.read(values, bytesRead, imSize - bytesRead);
        }
        if (this.isBottomUp) {
            for (int i2 = 0; i2 < this.height; i2++) {
                System.arraycopy(values, imSize - ((i2 + 1) * (bytesPerScanline + padding)), bdata, i2 * bytesPerScanline, bytesPerScanline);
            }
        } else {
            for (int i3 = 0; i3 < this.height; i3++) {
                System.arraycopy(values, i3 * (bytesPerScanline + padding), bdata, i3 * bytesPerScanline, bytesPerScanline);
            }
        }
        return indexedModel(bdata, 4, paletteEntries);
    }

    private Image read8Bit(int paletteEntries) throws BadElementException, IOException {
        byte[] bdata = new byte[this.width * this.height];
        int padding = 0;
        int bitsPerScanline = this.width * 8;
        if (bitsPerScanline % 32 != 0) {
            int padding2 = (((bitsPerScanline / 32) + 1) * 32) - bitsPerScanline;
            padding = (int) Math.ceil(((double) padding2) / 8.0d);
        }
        int imSize = (this.width + padding) * this.height;
        byte[] values = new byte[imSize];
        int i = 0;
        while (true) {
            int bytesRead = i;
            if (bytesRead >= imSize) {
                break;
            }
            i = bytesRead + this.inputStream.read(values, bytesRead, imSize - bytesRead);
        }
        if (this.isBottomUp) {
            for (int i2 = 0; i2 < this.height; i2++) {
                System.arraycopy(values, imSize - ((i2 + 1) * (this.width + padding)), bdata, i2 * this.width, this.width);
            }
        } else {
            for (int i3 = 0; i3 < this.height; i3++) {
                System.arraycopy(values, i3 * (this.width + padding), bdata, i3 * this.width, this.width);
            }
        }
        return indexedModel(bdata, 8, paletteEntries);
    }

    private void read24Bit(byte[] bdata) {
        int padding = 0;
        int bitsPerScanline = this.width * 24;
        if (bitsPerScanline % 32 != 0) {
            int padding2 = (((bitsPerScanline / 32) + 1) * 32) - bitsPerScanline;
            padding = (int) Math.ceil(((double) padding2) / 8.0d);
        }
        int imSize = (((this.width * 3) + 3) / 4) * 4 * this.height;
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            try {
                int r = this.inputStream.read(values, bytesRead, imSize - bytesRead);
                if (r < 0) {
                    break;
                } else {
                    bytesRead += r;
                }
            } catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
        }
        int l = 0;
        if (this.isBottomUp) {
            int max = ((this.width * this.height) * 3) - 1;
            int count = -padding;
            for (int i = 0; i < this.height; i++) {
                int l2 = (max - (((i + 1) * this.width) * 3)) + 1;
                count += padding;
                for (int j = 0; j < this.width; j++) {
                    int i2 = count;
                    int count2 = count + 1;
                    bdata[l2 + 2] = values[i2];
                    int count3 = count2 + 1;
                    bdata[l2 + 1] = values[count2];
                    count = count3 + 1;
                    bdata[l2] = values[count3];
                    l2 += 3;
                }
            }
            return;
        }
        int count4 = -padding;
        for (int i3 = 0; i3 < this.height; i3++) {
            count4 += padding;
            for (int j2 = 0; j2 < this.width; j2++) {
                int i4 = count4;
                int count5 = count4 + 1;
                bdata[l + 2] = values[i4];
                int count6 = count5 + 1;
                bdata[l + 1] = values[count5];
                count4 = count6 + 1;
                bdata[l] = values[count6];
                l += 3;
            }
        }
    }

    private int findMask(int mask) {
        for (int k = 0; k < 32 && (mask & 1) != 1; k++) {
            mask >>>= 1;
        }
        return mask;
    }

    private int findShift(int mask) {
        int k = 0;
        while (k < 32 && (mask & 1) != 1) {
            mask >>>= 1;
            k++;
        }
        return k;
    }

    private Image read1632Bit(boolean is32) throws BadElementException, IOException {
        int word;
        int word2;
        int red_mask = findMask(this.redMask);
        int red_shift = findShift(this.redMask);
        int red_factor = red_mask + 1;
        int green_mask = findMask(this.greenMask);
        int green_shift = findShift(this.greenMask);
        int green_factor = green_mask + 1;
        int blue_mask = findMask(this.blueMask);
        int blue_shift = findShift(this.blueMask);
        int blue_factor = blue_mask + 1;
        byte[] bdata = new byte[this.width * this.height * 3];
        int padding = 0;
        if (!is32) {
            int bitsPerScanline = this.width * 16;
            if (bitsPerScanline % 32 != 0) {
                padding = (int) Math.ceil(((double) ((((bitsPerScanline / 32) + 1) * 32) - bitsPerScanline)) / 8.0d);
            }
        }
        int imSize = (int) this.imageSize;
        if (imSize == 0) {
        }
        int l = 0;
        if (this.isBottomUp) {
            for (int i = this.height - 1; i >= 0; i--) {
                int l2 = this.width * 3 * i;
                for (int j = 0; j < this.width; j++) {
                    if (is32) {
                        word2 = (int) readDWord(this.inputStream);
                    } else {
                        word2 = readWord(this.inputStream);
                    }
                    int v = word2;
                    int i2 = l2;
                    int l3 = l2 + 1;
                    bdata[i2] = (byte) ((((v >>> red_shift) & red_mask) * 256) / red_factor);
                    int l4 = l3 + 1;
                    bdata[l3] = (byte) ((((v >>> green_shift) & green_mask) * 256) / green_factor);
                    l2 = l4 + 1;
                    bdata[l4] = (byte) ((((v >>> blue_shift) & blue_mask) * 256) / blue_factor);
                }
                for (int m = 0; m < padding; m++) {
                    this.inputStream.read();
                }
            }
        } else {
            for (int i3 = 0; i3 < this.height; i3++) {
                for (int j2 = 0; j2 < this.width; j2++) {
                    if (is32) {
                        word = (int) readDWord(this.inputStream);
                    } else {
                        word = readWord(this.inputStream);
                    }
                    int v2 = word;
                    int i4 = l;
                    int l5 = l + 1;
                    bdata[i4] = (byte) ((((v2 >>> red_shift) & red_mask) * 256) / red_factor);
                    int l6 = l5 + 1;
                    bdata[l5] = (byte) ((((v2 >>> green_shift) & green_mask) * 256) / green_factor);
                    l = l6 + 1;
                    bdata[l6] = (byte) ((((v2 >>> blue_shift) & blue_mask) * 256) / blue_factor);
                }
                for (int m2 = 0; m2 < padding; m2++) {
                    this.inputStream.read();
                }
            }
        }
        return new ImgRaw(this.width, this.height, 3, 8, bdata);
    }

    private Image readRLE8() throws BadElementException, IOException {
        int imSize = (int) this.imageSize;
        if (imSize == 0) {
            imSize = (int) (this.bitmapFileSize - this.bitmapOffset);
        }
        byte[] values = new byte[imSize];
        int i = 0;
        while (true) {
            int bytesRead = i;
            if (bytesRead >= imSize) {
                break;
            }
            i = bytesRead + this.inputStream.read(values, bytesRead, imSize - bytesRead);
        }
        byte[] val = decodeRLE(true, values);
        int imSize2 = this.width * this.height;
        if (this.isBottomUp) {
            byte[] temp = new byte[val.length];
            int bytesPerScanline = this.width;
            for (int i2 = 0; i2 < this.height; i2++) {
                System.arraycopy(val, imSize2 - ((i2 + 1) * bytesPerScanline), temp, i2 * bytesPerScanline, bytesPerScanline);
            }
            val = temp;
        }
        return indexedModel(val, 8, 4);
    }

    private Image readRLE4() throws BadElementException, IOException {
        int imSize = (int) this.imageSize;
        if (imSize == 0) {
            imSize = (int) (this.bitmapFileSize - this.bitmapOffset);
        }
        byte[] values = new byte[imSize];
        int i = 0;
        while (true) {
            int bytesRead = i;
            if (bytesRead >= imSize) {
                break;
            }
            i = bytesRead + this.inputStream.read(values, bytesRead, imSize - bytesRead);
        }
        byte[] val = decodeRLE(false, values);
        if (this.isBottomUp) {
            val = new byte[this.width * this.height];
            int l = 0;
            for (int i2 = this.height - 1; i2 >= 0; i2--) {
                int index = i2 * this.width;
                int lineEnd = l + this.width;
                while (l != lineEnd) {
                    int i3 = l;
                    l++;
                    int i4 = index;
                    index++;
                    val[i3] = val[i4];
                }
            }
        }
        int stride = (this.width + 1) / 2;
        byte[] bdata = new byte[stride * this.height];
        int ptr = 0;
        int sh = 0;
        for (int h = 0; h < this.height; h++) {
            for (int w = 0; w < this.width; w++) {
                if ((w & 1) == 0) {
                    int i5 = ptr;
                    ptr++;
                    bdata[sh + (w / 2)] = (byte) (val[i5] << 4);
                } else {
                    int i6 = sh + (w / 2);
                    int i7 = ptr;
                    ptr++;
                    bdata[i6] = (byte) (bdata[i6] | ((byte) (val[i7] & 15)));
                }
            }
            sh += stride;
        }
        return indexedModel(bdata, 4, 4);
    }

    private byte[] decodeRLE(boolean is8, byte[] values) {
        byte[] val = new byte[this.width * this.height];
        int ptr = 0;
        int x = 0;
        int q = 0;
        int y = 0;
        while (y < this.height && ptr < values.length) {
            try {
                int i = ptr;
                int ptr2 = ptr + 1;
                int count = values[i] & 255;
                if (count != 0) {
                    ptr = ptr2 + 1;
                    int bt = values[ptr2] & 255;
                    if (is8) {
                        for (int i2 = count; i2 != 0; i2--) {
                            int i3 = q;
                            q++;
                            val[i3] = (byte) bt;
                        }
                    } else {
                        for (int i4 = 0; i4 < count; i4++) {
                            int i5 = q;
                            q++;
                            val[i5] = (byte) ((i4 & 1) == 1 ? bt & 15 : (bt >>> 4) & 15);
                        }
                    }
                    x += count;
                } else {
                    ptr = ptr2 + 1;
                    int count2 = values[ptr2] & 255;
                    if (count2 != 1) {
                        switch (count2) {
                            case 0:
                                x = 0;
                                y++;
                                q = y * this.width;
                                break;
                            case 2:
                                int ptr3 = ptr + 1;
                                x += values[ptr] & 255;
                                ptr = ptr3 + 1;
                                y += values[ptr3] & 255;
                                q = (y * this.width) + x;
                                break;
                            default:
                                if (is8) {
                                    for (int i6 = count2; i6 != 0; i6--) {
                                        int i7 = q;
                                        q++;
                                        int i8 = ptr;
                                        ptr++;
                                        val[i7] = (byte) (values[i8] & 255);
                                    }
                                } else {
                                    int bt2 = 0;
                                    for (int i9 = 0; i9 < count2; i9++) {
                                        if ((i9 & 1) == 0) {
                                            int i10 = ptr;
                                            ptr++;
                                            bt2 = values[i10] & 255;
                                        }
                                        int i11 = q;
                                        q++;
                                        val[i11] = (byte) ((i9 & 1) == 1 ? bt2 & 15 : (bt2 >>> 4) & 15);
                                    }
                                }
                                x += count2;
                                if (is8) {
                                    if ((count2 & 1) == 1) {
                                        ptr++;
                                    }
                                } else if ((count2 & 3) == 1 || (count2 & 3) == 2) {
                                    ptr++;
                                }
                                break;
                        }
                    } else {
                        return val;
                    }
                }
            } catch (RuntimeException e) {
            }
        }
        return val;
    }

    private int readUnsignedByte(InputStream stream) throws IOException {
        return stream.read() & 255;
    }

    private int readUnsignedShort(InputStream stream) throws IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        return ((b2 << 8) | b1) & 65535;
    }

    private int readShort(InputStream stream) throws IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        return (b2 << 8) | b1;
    }

    private int readWord(InputStream stream) throws IOException {
        return readUnsignedShort(stream);
    }

    private long readUnsignedInt(InputStream stream) throws IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        int b3 = readUnsignedByte(stream);
        int b4 = readUnsignedByte(stream);
        long l = (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
        return l & (-1);
    }

    private int readInt(InputStream stream) throws IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        int b3 = readUnsignedByte(stream);
        int b4 = readUnsignedByte(stream);
        return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
    }

    private long readDWord(InputStream stream) throws IOException {
        return readUnsignedInt(stream);
    }

    private int readLong(InputStream stream) throws IOException {
        return readInt(stream);
    }
}
