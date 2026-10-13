package com.aowagie.text.pdf.codec;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.ImgRaw;
import com.aowagie.text.Utilities;
import com.aowagie.text.pdf.ByteBuffer;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfLiteral;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfString;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/PngImage.class */
public class PngImage {
    private static final String IHDR = "IHDR";
    private static final String PLTE = "PLTE";
    private static final String IDAT = "IDAT";
    private static final String IEND = "IEND";
    private static final String tRNS = "tRNS";
    private static final String pHYs = "pHYs";
    private static final String gAMA = "gAMA";
    private static final String cHRM = "cHRM";
    private static final String sRGB = "sRGB";
    private static final String iCCP = "iCCP";
    private static final int TRANSFERSIZE = 4096;
    private static final int PNG_FILTER_NONE = 0;
    private static final int PNG_FILTER_SUB = 1;
    private static final int PNG_FILTER_UP = 2;
    private static final int PNG_FILTER_AVERAGE = 3;
    private static final int PNG_FILTER_PAETH = 4;
    private final InputStream is;
    private DataInputStream dataStream;
    private int width;
    private int height;
    private int bitDepth;
    private int colorType;
    private int compressionMethod;
    private int filterMethod;
    private int interlaceMethod;
    private byte[] image;
    private byte[] smask;
    private byte[] trans;
    private int dpiX;
    private int dpiY;
    private float XYRatio;
    private boolean genBWMask;
    private boolean palShades;
    private int inputBands;
    private int bytesPerPixel;
    private byte[] colorTable;
    private float xW;
    private float yW;
    private float xR;
    private float yR;
    private float xG;
    private float yG;
    private float xB;
    private float yB;
    private PdfName intent;
    private ICC_Profile icc_profile;
    public static final int[] PNGID = {137, 80, 78, 71, 13, 10, 26, 10};
    private static final PdfName[] intents = {PdfName.PERCEPTUAL, PdfName.RELATIVECOLORIMETRIC, PdfName.SATURATION, PdfName.ABSOLUTECOLORIMETRIC};
    private final PdfDictionary additional = new PdfDictionary();
    private final NewByteArrayOutputStream idat = new NewByteArrayOutputStream();
    private int transRedGray = -1;
    private int transGreen = -1;
    private int transBlue = -1;
    private float gamma = 1.0f;
    private boolean hasCHRM = false;

    private PngImage(InputStream is) {
        this.is = is;
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
        PngImage png = new PngImage(is);
        return png.getImage();
    }

    public static Image getImage(byte[] data) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        Image img = getImage(is);
        img.setOriginalData(data);
        return img;
    }

    private boolean checkMarker(String s) {
        if (s.length() != 4) {
            return false;
        }
        for (int k = 0; k < 4; k++) {
            char c = s.charAt(k);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                return false;
            }
        }
        return true;
    }

    private void readPng() throws IOException {
        for (int element : PNGID) {
            if (element != this.is.read()) {
                throw new IOException("File is not a valid PNG.");
            }
        }
        byte[] buffer = new byte[4096];
        while (true) {
            int len = getInt(this.is);
            String marker = getString(this.is);
            if (len >= 0 && checkMarker(marker)) {
                if (IDAT.equals(marker)) {
                    while (len != 0) {
                        int size = this.is.read(buffer, 0, Math.min(len, 4096));
                        if (size < 0) {
                            return;
                        }
                        this.idat.write(buffer, 0, size);
                        len -= size;
                    }
                } else if (tRNS.equals(marker)) {
                    switch (this.colorType) {
                        case 0:
                            if (len >= 2) {
                                len -= 2;
                                int gray = getWord(this.is);
                                if (this.bitDepth == 16) {
                                    this.transRedGray = gray;
                                } else {
                                    this.additional.put(PdfName.MASK, new PdfLiteral("[" + gray + " " + gray + "]"));
                                }
                            }
                            break;
                        case 2:
                            if (len >= 6) {
                                len -= 6;
                                int red = getWord(this.is);
                                int green = getWord(this.is);
                                int blue = getWord(this.is);
                                if (this.bitDepth == 16) {
                                    this.transRedGray = red;
                                    this.transGreen = green;
                                    this.transBlue = blue;
                                } else {
                                    this.additional.put(PdfName.MASK, new PdfLiteral("[" + red + " " + red + " " + green + " " + green + " " + blue + " " + blue + "]"));
                                }
                            }
                            break;
                        case 3:
                            if (len > 0) {
                                this.trans = new byte[len];
                                for (int k = 0; k < len; k++) {
                                    this.trans[k] = (byte) this.is.read();
                                }
                                len = 0;
                            }
                            break;
                    }
                    Utilities.skip(this.is, len);
                } else if (IHDR.equals(marker)) {
                    this.width = getInt(this.is);
                    this.height = getInt(this.is);
                    this.bitDepth = this.is.read();
                    this.colorType = this.is.read();
                    this.compressionMethod = this.is.read();
                    this.filterMethod = this.is.read();
                    this.interlaceMethod = this.is.read();
                } else if (PLTE.equals(marker)) {
                    if (this.colorType == 3) {
                        PdfArray colorspace = new PdfArray();
                        colorspace.add(PdfName.INDEXED);
                        colorspace.add(getColorspace());
                        colorspace.add(new PdfNumber((len / 3) - 1));
                        ByteBuffer colortable = new ByteBuffer();
                        while (true) {
                            int i = len;
                            len--;
                            if (i > 0) {
                                colortable.append_i(this.is.read());
                            } else {
                                byte[] byteArray = colortable.toByteArray();
                                this.colorTable = byteArray;
                                colorspace.add(new PdfString(byteArray));
                                this.additional.put(PdfName.COLORSPACE, colorspace);
                            }
                        }
                    } else {
                        Utilities.skip(this.is, len);
                    }
                } else if (pHYs.equals(marker)) {
                    int dx = getInt(this.is);
                    int dy = getInt(this.is);
                    int unit = this.is.read();
                    if (unit == 1) {
                        this.dpiX = (int) ((dx * 0.0254f) + 0.5f);
                        this.dpiY = (int) ((dy * 0.0254f) + 0.5f);
                    } else if (dy != 0) {
                        this.XYRatio = dx / dy;
                    }
                } else if (cHRM.equals(marker)) {
                    this.xW = getInt(this.is) / 100000.0f;
                    this.yW = getInt(this.is) / 100000.0f;
                    this.xR = getInt(this.is) / 100000.0f;
                    this.yR = getInt(this.is) / 100000.0f;
                    this.xG = getInt(this.is) / 100000.0f;
                    this.yG = getInt(this.is) / 100000.0f;
                    this.xB = getInt(this.is) / 100000.0f;
                    this.yB = getInt(this.is) / 100000.0f;
                    this.hasCHRM = Math.abs(this.xW) >= 1.0E-4f && Math.abs(this.yW) >= 1.0E-4f && Math.abs(this.xR) >= 1.0E-4f && Math.abs(this.yR) >= 1.0E-4f && Math.abs(this.xG) >= 1.0E-4f && Math.abs(this.yG) >= 1.0E-4f && Math.abs(this.xB) >= 1.0E-4f && Math.abs(this.yB) >= 1.0E-4f;
                } else if (sRGB.equals(marker)) {
                    int ri = this.is.read();
                    this.intent = intents[ri];
                    this.gamma = 2.2f;
                    this.xW = 0.3127f;
                    this.yW = 0.329f;
                    this.xR = 0.64f;
                    this.yR = 0.33f;
                    this.xG = 0.3f;
                    this.yG = 0.6f;
                    this.xB = 0.15f;
                    this.yB = 0.06f;
                    this.hasCHRM = true;
                } else if (gAMA.equals(marker)) {
                    int gm = getInt(this.is);
                    if (gm != 0) {
                        this.gamma = 100000.0f / gm;
                        if (!this.hasCHRM) {
                            this.xW = 0.3127f;
                            this.yW = 0.329f;
                            this.xR = 0.64f;
                            this.yR = 0.33f;
                            this.xG = 0.3f;
                            this.yG = 0.6f;
                            this.xB = 0.15f;
                            this.yB = 0.06f;
                            this.hasCHRM = true;
                        }
                    }
                } else if (iCCP.equals(marker)) {
                    do {
                        len--;
                    } while (this.is.read() != 0);
                    this.is.read();
                    int len2 = len - 1;
                    byte[] icccom = new byte[len2];
                    int p = 0;
                    while (len2 > 0) {
                        int r = this.is.read(icccom, p, len2);
                        if (r < 0) {
                            throw new IOException("Premature end of file.");
                        }
                        p += r;
                        len2 -= r;
                    }
                    byte[] iccp = PdfReader.FlateDecode(icccom, true);
                    try {
                        this.icc_profile = ICC_Profile.getInstance(iccp);
                    } catch (RuntimeException e) {
                        this.icc_profile = null;
                    }
                } else if (!IEND.equals(marker)) {
                    Utilities.skip(this.is, len);
                } else {
                    return;
                }
                Utilities.skip(this.is, 4);
            }
        }
        throw new IOException("Corrupted PNG file.");
    }

    private PdfObject getColorspace() {
        if (this.icc_profile != null) {
            if ((this.colorType & 2) == 0) {
                return PdfName.DEVICEGRAY;
            }
            return PdfName.DEVICERGB;
        }
        if (this.gamma == 1.0f && !this.hasCHRM) {
            if ((this.colorType & 2) == 0) {
                return PdfName.DEVICEGRAY;
            }
            return PdfName.DEVICERGB;
        }
        PdfArray pdfArray = new PdfArray();
        PdfDictionary dic = new PdfDictionary();
        if ((this.colorType & 2) != 0) {
            PdfObject wp = new PdfLiteral("[1 1 1]");
            pdfArray.add(PdfName.CALRGB);
            if (this.gamma != 1.0f) {
                PdfArray gm = new PdfArray();
                PdfNumber n = new PdfNumber(this.gamma);
                gm.add(n);
                gm.add(n);
                gm.add(n);
                dic.put(PdfName.GAMMA, gm);
            }
            if (this.hasCHRM) {
                float z = this.yW * ((((this.xG - this.xB) * this.yR) - ((this.xR - this.xB) * this.yG)) + ((this.xR - this.xG) * this.yB));
                float YA = (this.yR * ((((this.xG - this.xB) * this.yW) - ((this.xW - this.xB) * this.yG)) + ((this.xW - this.xG) * this.yB))) / z;
                float XA = (YA * this.xR) / this.yR;
                float ZA = YA * (((1.0f - this.xR) / this.yR) - 1.0f);
                float YB = ((-this.yG) * ((((this.xR - this.xB) * this.yW) - ((this.xW - this.xB) * this.yR)) + ((this.xW - this.xR) * this.yB))) / z;
                float XB = (YB * this.xG) / this.yG;
                float ZB = YB * (((1.0f - this.xG) / this.yG) - 1.0f);
                float YC = (this.yB * ((((this.xR - this.xG) * this.yW) - ((this.xW - this.xG) * this.yW)) + ((this.xW - this.xR) * this.yG))) / z;
                float XC = (YC * this.xB) / this.yB;
                float ZC = YC * (((1.0f - this.xB) / this.yB) - 1.0f);
                float XW = XA + XB + XC;
                float ZW = ZA + ZB + ZC;
                PdfArray wpa = new PdfArray();
                wpa.add(new PdfNumber(XW));
                wpa.add(new PdfNumber(1.0f));
                wpa.add(new PdfNumber(ZW));
                wp = wpa;
                PdfArray matrix = new PdfArray();
                matrix.add(new PdfNumber(XA));
                matrix.add(new PdfNumber(YA));
                matrix.add(new PdfNumber(ZA));
                matrix.add(new PdfNumber(XB));
                matrix.add(new PdfNumber(YB));
                matrix.add(new PdfNumber(ZB));
                matrix.add(new PdfNumber(XC));
                matrix.add(new PdfNumber(YC));
                matrix.add(new PdfNumber(ZC));
                dic.put(PdfName.MATRIX, matrix);
            }
            dic.put(PdfName.WHITEPOINT, wp);
            pdfArray.add(dic);
        } else {
            if (this.gamma == 1.0f) {
                return PdfName.DEVICEGRAY;
            }
            pdfArray.add(PdfName.CALGRAY);
            dic.put(PdfName.GAMMA, new PdfNumber(this.gamma));
            dic.put(PdfName.WHITEPOINT, new PdfLiteral("[1 1 1]"));
            pdfArray.add(dic);
        }
        return pdfArray;
    }

    private Image getImage() throws IOException {
        Image img;
        readPng();
        try {
            int pal0 = 0;
            int palIdx = 0;
            this.palShades = false;
            if (this.trans != null) {
                for (int k = 0; k < this.trans.length; k++) {
                    int n = this.trans[k] & 255;
                    if (n == 0) {
                        pal0++;
                        palIdx = k;
                    }
                    if (n != 0 && n != 255) {
                        this.palShades = true;
                        break;
                    }
                }
            }
            if ((this.colorType & 4) != 0) {
                this.palShades = true;
            }
            this.genBWMask = !this.palShades && (pal0 > 1 || this.transRedGray >= 0);
            if (!this.palShades && !this.genBWMask && pal0 == 1) {
                this.additional.put(PdfName.MASK, new PdfLiteral("[" + palIdx + " " + palIdx + "]"));
            }
            boolean needDecode = this.interlaceMethod == 1 || this.bitDepth == 16 || (this.colorType & 4) != 0 || this.palShades || this.genBWMask;
            switch (this.colorType) {
                case 0:
                    this.inputBands = 1;
                    break;
                case 2:
                    this.inputBands = 3;
                    break;
                case 3:
                    this.inputBands = 1;
                    break;
                case 4:
                    this.inputBands = 2;
                    break;
                case 6:
                    this.inputBands = 4;
                    break;
            }
            if (needDecode) {
                decodeIdat();
            }
            int components = this.inputBands;
            if ((this.colorType & 4) != 0) {
                components--;
            }
            int bpc = this.bitDepth;
            if (bpc == 16) {
                bpc = 8;
            }
            if (this.image != null) {
                if (this.colorType == 3) {
                    img = new ImgRaw(this.width, this.height, components, bpc, this.image);
                } else {
                    img = Image.getInstance(this.width, this.height, components, bpc, this.image);
                }
            } else {
                img = new ImgRaw(this.width, this.height, components, bpc, this.idat.toByteArray());
                img.setDeflated(true);
                PdfDictionary decodeparms = new PdfDictionary();
                decodeparms.put(PdfName.BITSPERCOMPONENT, new PdfNumber(this.bitDepth));
                decodeparms.put(PdfName.PREDICTOR, new PdfNumber(15));
                decodeparms.put(PdfName.COLUMNS, new PdfNumber(this.width));
                decodeparms.put(PdfName.COLORS, new PdfNumber((this.colorType == 3 || (this.colorType & 2) == 0) ? 1 : 3));
                this.additional.put(PdfName.DECODEPARMS, decodeparms);
            }
            if (this.additional.get(PdfName.COLORSPACE) == null) {
                this.additional.put(PdfName.COLORSPACE, getColorspace());
            }
            if (this.intent != null) {
                this.additional.put(PdfName.INTENT, this.intent);
            }
            if (this.additional.size() > 0) {
                img.setAdditional(this.additional);
            }
            if (this.icc_profile != null) {
                img.tagICC(this.icc_profile);
            }
            if (this.palShades) {
                Image im2 = Image.getInstance(this.width, this.height, 1, 8, this.smask);
                im2.makeMask();
                img.setImageMask(im2);
            }
            if (this.genBWMask) {
                Image im3 = Image.getInstance(this.width, this.height, 1, 1, this.smask);
                im3.makeMask();
                img.setImageMask(im3);
            }
            img.setDpi(this.dpiX, this.dpiY);
            img.setXYRatio(this.XYRatio);
            img.setOriginalType(2);
            return img;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void decodeIdat() {
        int nbitDepth = this.bitDepth;
        if (nbitDepth == 16) {
            nbitDepth = 8;
        }
        int size = -1;
        this.bytesPerPixel = this.bitDepth == 16 ? 2 : 1;
        switch (this.colorType) {
            case 0:
                size = (((nbitDepth * this.width) + 7) / 8) * this.height;
                break;
            case 2:
                size = this.width * 3 * this.height;
                this.bytesPerPixel *= 3;
                break;
            case 3:
                if (this.interlaceMethod == 1) {
                    size = (((nbitDepth * this.width) + 7) / 8) * this.height;
                }
                this.bytesPerPixel = 1;
                break;
            case 4:
                size = this.width * this.height;
                this.bytesPerPixel *= 2;
                break;
            case 6:
                size = this.width * 3 * this.height;
                this.bytesPerPixel *= 4;
                break;
        }
        if (size >= 0) {
            this.image = new byte[size];
        }
        if (this.palShades) {
            this.smask = new byte[this.width * this.height];
        } else if (this.genBWMask) {
            this.smask = new byte[((this.width + 7) / 8) * this.height];
        }
        ByteArrayInputStream bai = new ByteArrayInputStream(this.idat.getBuf(), 0, this.idat.size());
        InputStream infStream = new InflaterInputStream(bai, new Inflater());
        this.dataStream = new DataInputStream(infStream);
        if (this.interlaceMethod != 1) {
            decodePass(0, 0, 1, 1, this.width, this.height);
            return;
        }
        decodePass(0, 0, 8, 8, (this.width + 7) / 8, (this.height + 7) / 8);
        decodePass(4, 0, 8, 8, (this.width + 3) / 8, (this.height + 7) / 8);
        decodePass(0, 4, 4, 8, (this.width + 3) / 4, (this.height + 3) / 8);
        decodePass(2, 0, 4, 4, (this.width + 1) / 4, (this.height + 3) / 4);
        decodePass(0, 2, 2, 4, (this.width + 1) / 2, (this.height + 1) / 4);
        decodePass(1, 0, 2, 2, this.width / 2, (this.height + 1) / 2);
        decodePass(0, 1, 1, 2, this.width, this.height / 2);
    }

    private void decodePass(int xOffset, int yOffset, int xStep, int yStep, int passWidth, int passHeight) {
        if (passWidth == 0 || passHeight == 0) {
            return;
        }
        int bytesPerRow = (((this.inputBands * passWidth) * this.bitDepth) + 7) / 8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        int srcY = 0;
        int i = yOffset;
        while (true) {
            int dstY = i;
            if (srcY < passHeight) {
                int filter = 0;
                try {
                    filter = this.dataStream.read();
                    this.dataStream.readFully(curr, 0, bytesPerRow);
                } catch (Exception e) {
                }
                switch (filter) {
                    case 0:
                        break;
                    case 1:
                        decodeSubFilter(curr, bytesPerRow, this.bytesPerPixel);
                        break;
                    case 2:
                        decodeUpFilter(curr, prior, bytesPerRow);
                        break;
                    case 3:
                        decodeAverageFilter(curr, prior, bytesPerRow, this.bytesPerPixel);
                        break;
                    case 4:
                        decodePaethFilter(curr, prior, bytesPerRow, this.bytesPerPixel);
                        break;
                    default:
                        throw new RuntimeException("PNG filter unknown.");
                }
                processPixels(curr, xOffset, xStep, dstY, passWidth);
                byte[] tmp = prior;
                prior = curr;
                curr = tmp;
                srcY++;
                i = dstY + yStep;
            } else {
                return;
            }
        }
    }

    private void processPixels(byte[] curr, int xOffset, int step, int y, int width) {
        int[] out = getPixel(curr);
        int sizes = 0;
        switch (this.colorType) {
            case 0:
            case 3:
            case 4:
                sizes = 1;
                break;
            case 2:
            case 6:
                sizes = 3;
                break;
        }
        if (this.image != null) {
            int dstX = xOffset;
            int yStride = (((sizes * this.width) * (this.bitDepth == 16 ? 8 : this.bitDepth)) + 7) / 8;
            for (int srcX = 0; srcX < width; srcX++) {
                setPixel(this.image, out, this.inputBands * srcX, sizes, dstX, y, this.bitDepth, yStride);
                dstX += step;
            }
        }
        if (this.palShades) {
            if ((this.colorType & 4) != 0) {
                if (this.bitDepth == 16) {
                    for (int k = 0; k < width; k++) {
                        int i = (k * this.inputBands) + sizes;
                        out[i] = out[i] >>> 8;
                    }
                }
                int yStride2 = this.width;
                int dstX2 = xOffset;
                for (int srcX2 = 0; srcX2 < width; srcX2++) {
                    setPixel(this.smask, out, (this.inputBands * srcX2) + sizes, 1, dstX2, y, 8, yStride2);
                    dstX2 += step;
                }
                return;
            }
            int yStride3 = this.width;
            int[] v = new int[1];
            int dstX3 = xOffset;
            for (int srcX3 = 0; srcX3 < width; srcX3++) {
                int idx = out[srcX3];
                if (idx < this.trans.length) {
                    v[0] = this.trans[idx];
                } else {
                    v[0] = 255;
                }
                setPixel(this.smask, v, 0, 1, dstX3, y, 8, yStride3);
                dstX3 += step;
            }
            return;
        }
        if (this.genBWMask) {
            switch (this.colorType) {
                case 0:
                    int yStride4 = (this.width + 7) / 8;
                    int[] v2 = new int[1];
                    int dstX4 = xOffset;
                    for (int srcX4 = 0; srcX4 < width; srcX4++) {
                        int g = out[srcX4];
                        v2[0] = g == this.transRedGray ? 1 : 0;
                        setPixel(this.smask, v2, 0, 1, dstX4, y, 1, yStride4);
                        dstX4 += step;
                    }
                    break;
                case 2:
                    int yStride5 = (this.width + 7) / 8;
                    int[] v3 = new int[1];
                    int dstX5 = xOffset;
                    for (int srcX5 = 0; srcX5 < width; srcX5++) {
                        int markRed = this.inputBands * srcX5;
                        v3[0] = (out[markRed] == this.transRedGray && out[markRed + 1] == this.transGreen && out[markRed + 2] == this.transBlue) ? 1 : 0;
                        setPixel(this.smask, v3, 0, 1, dstX5, y, 1, yStride5);
                        dstX5 += step;
                    }
                    break;
                case 3:
                    int yStride6 = (this.width + 7) / 8;
                    int[] v4 = new int[1];
                    int dstX6 = xOffset;
                    for (int srcX6 = 0; srcX6 < width; srcX6++) {
                        int idx2 = out[srcX6];
                        v4[0] = (idx2 >= this.trans.length || this.trans[idx2] != 0) ? 0 : 1;
                        setPixel(this.smask, v4, 0, 1, dstX6, y, 1, yStride6);
                        dstX6 += step;
                    }
                    break;
            }
        }
    }

    private static void setPixel(byte[] image, int[] data, int offset, int size, int x, int y, int bitDepth, int bytesPerRow) {
        if (bitDepth == 8) {
            int pos = (bytesPerRow * y) + (size * x);
            for (int k = 0; k < size; k++) {
                image[pos + k] = (byte) data[k + offset];
            }
            return;
        }
        if (bitDepth == 16) {
            int pos2 = (bytesPerRow * y) + (size * x);
            for (int k2 = 0; k2 < size; k2++) {
                image[pos2 + k2] = (byte) (data[k2 + offset] >>> 8);
            }
            return;
        }
        int pos3 = (bytesPerRow * y) + (x / (8 / bitDepth));
        int v = data[offset] << ((8 - (bitDepth * (x % (8 / bitDepth)))) - bitDepth);
        image[pos3] = (byte) (image[pos3] | v);
    }

    private int[] getPixel(byte[] curr) {
        switch (this.bitDepth) {
            case 8:
                int[] out = new int[curr.length];
                for (int k = 0; k < out.length; k++) {
                    out[k] = curr[k] & 255;
                }
                return out;
            case 16:
                int[] out2 = new int[curr.length / 2];
                for (int k2 = 0; k2 < out2.length; k2++) {
                    out2[k2] = ((curr[k2 * 2] & 255) << 8) + (curr[(k2 * 2) + 1] & 255);
                }
                return out2;
            default:
                int[] out3 = new int[(curr.length * 8) / this.bitDepth];
                int idx = 0;
                int passes = 8 / this.bitDepth;
                int mask = (1 << this.bitDepth) - 1;
                for (byte element : curr) {
                    for (int j = passes - 1; j >= 0; j--) {
                        int i = idx;
                        idx++;
                        out3[i] = (element >>> (this.bitDepth * j)) & mask;
                    }
                }
                return out3;
        }
    }

    private static void decodeSubFilter(byte[] curr, int count, int bpp) {
        for (int i = bpp; i < count; i++) {
            int val = curr[i] & 255;
            curr[i] = (byte) (val + (curr[i - bpp] & 255));
        }
    }

    private static void decodeUpFilter(byte[] curr, byte[] prev, int count) {
        for (int i = 0; i < count; i++) {
            int raw = curr[i] & 255;
            int prior = prev[i] & 255;
            curr[i] = (byte) (raw + prior);
        }
    }

    private static void decodeAverageFilter(byte[] curr, byte[] prev, int count, int bpp) {
        for (int i = 0; i < bpp; i++) {
            int raw = curr[i] & 255;
            int priorRow = prev[i] & 255;
            curr[i] = (byte) (raw + (priorRow / 2));
        }
        for (int i2 = bpp; i2 < count; i2++) {
            int raw2 = curr[i2] & 255;
            int priorPixel = curr[i2 - bpp] & 255;
            int priorRow2 = prev[i2] & 255;
            curr[i2] = (byte) (raw2 + ((priorPixel + priorRow2) / 2));
        }
    }

    private static int paethPredictor(int a, int b, int c) {
        int p = (a + b) - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) {
            return a;
        }
        if (pb <= pc) {
            return b;
        }
        return c;
    }

    private static void decodePaethFilter(byte[] curr, byte[] prev, int count, int bpp) {
        for (int i = 0; i < bpp; i++) {
            int raw = curr[i] & 255;
            int priorRow = prev[i] & 255;
            curr[i] = (byte) (raw + priorRow);
        }
        for (int i2 = bpp; i2 < count; i2++) {
            int raw2 = curr[i2] & 255;
            int priorPixel = curr[i2 - bpp] & 255;
            int priorRow2 = prev[i2] & 255;
            int priorRowPixel = prev[i2 - bpp] & 255;
            curr[i2] = (byte) (raw2 + paethPredictor(priorPixel, priorRow2, priorRowPixel));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/PngImage$NewByteArrayOutputStream.class */
    private static class NewByteArrayOutputStream extends ByteArrayOutputStream {
        private NewByteArrayOutputStream() {
        }

        public byte[] getBuf() {
            return this.buf;
        }
    }

    private static final int getInt(InputStream is) throws IOException {
        return (is.read() << 24) + (is.read() << 16) + (is.read() << 8) + is.read();
    }

    private static final int getWord(InputStream is) throws IOException {
        return (is.read() << 8) + is.read();
    }

    private static final String getString(InputStream is) throws IOException {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            buf.append((char) is.read());
        }
        return buf.toString();
    }
}
