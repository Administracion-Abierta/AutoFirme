package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfIndirectReference;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfOCG;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfTemplate;
import com.aowagie.text.pdf.RandomAccessFileOrArray;
import com.aowagie.text.pdf.codec.BmpImage;
import com.aowagie.text.pdf.codec.CCITTG4Encoder;
import com.aowagie.text.pdf.codec.GifImage;
import com.aowagie.text.pdf.codec.JBIG2Image;
import com.aowagie.text.pdf.codec.PngImage;
import com.aowagie.text.pdf.codec.TiffImage;
import com.sun.jna.platform.win32.LMErr;
import java.awt.Color;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Image.class */
public abstract class Image extends Rectangle {
    private static final int DEFAULT = 0;
    public static final int RIGHT = 2;
    public static final int LEFT = 0;
    public static final int MIDDLE = 1;
    public static final int TEXTWRAP = 4;
    public static final int UNDERLYING = 8;
    private static final int AX = 0;
    private static final int AY = 1;
    private static final int BX = 2;
    private static final int BY = 3;
    public static final int CX = 4;
    public static final int CY = 5;
    private static final int DX = 6;
    private static final int DY = 7;
    private static final int ORIGINAL_NONE = 0;
    static final int ORIGINAL_JPEG = 1;
    public static final int ORIGINAL_PNG = 2;
    public static final int ORIGINAL_GIF = 3;
    public static final int ORIGINAL_BMP = 4;
    public static final int ORIGINAL_TIFF = 5;
    static final int ORIGINAL_WMF = 6;
    static final int ORIGINAL_JPEG2000 = 8;
    static final int ORIGINAL_JBIG2 = 9;
    protected int type;
    protected URL url;
    protected byte[] rawData;
    protected int bpc;
    private PdfTemplate[] template;
    private int alignment;
    private String alt;
    private float absoluteX;
    private float absoluteY;
    protected float plainWidth;
    protected float plainHeight;
    protected float scaledWidth;
    protected float scaledHeight;
    private int compressionLevel;
    private Long mySerialId;
    private PdfIndirectReference directReference;
    private static long serialId = 0;
    private float rotationRadians;
    private float initialRotation;
    private float indentationLeft;
    private float indentationRight;
    private float spacingBefore;
    private float spacingAfter;
    private float widthPercentage;
    private Annotation annotation;
    private PdfOCG layer;
    private boolean interpolation;
    protected int originalType;
    protected byte[] originalData;
    private boolean deflated;
    protected int dpiX;
    protected int dpiY;
    private float XYRatio;
    protected int colorspace;
    protected boolean invert;
    private ICC_Profile profile;
    private PdfDictionary additional;
    private boolean mask;
    private Image imageMask;
    private boolean smask;
    private int[] transparency;

    Image(URL url) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.bpc = 1;
        this.template = new PdfTemplate[1];
        this.absoluteX = Float.NaN;
        this.absoluteY = Float.NaN;
        this.compressionLevel = -1;
        this.mySerialId = getSerialId();
        this.indentationLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentationRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.widthPercentage = 100.0f;
        this.annotation = null;
        this.originalType = 0;
        this.deflated = false;
        this.dpiX = 0;
        this.dpiY = 0;
        this.XYRatio = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.colorspace = -1;
        this.invert = false;
        this.profile = null;
        this.additional = null;
        this.mask = false;
        this.url = url;
        this.alignment = 0;
        this.rotationRadians = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    /* JADX WARN: Code duplicated, block: B:155:0x02c2  */
    private static Image getInstance(URL url) throws BadElementException, IOException {
        InputStream is = null;
        try {
            InputStream is2 = url.openStream();
            int c1 = is2.read();
            int c2 = is2.read();
            int c3 = is2.read();
            int c4 = is2.read();
            int c5 = is2.read();
            int c6 = is2.read();
            int c7 = is2.read();
            int c8 = is2.read();
            is2.close();
            is = null;
            if (c1 == 71 && c2 == 73 && c3 == 70) {
                GifImage gif = new GifImage(url);
                Image img = gif.getImage(1);
                if (0 != 0) {
                    is.close();
                }
                return img;
            }
            if (c1 == 255 && c2 == 216) {
                Jpeg jpeg = new Jpeg(url);
                if (0 != 0) {
                    is.close();
                }
                return jpeg;
            }
            if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 12) {
                Jpeg2000 jpeg2000 = new Jpeg2000(url);
                if (0 != 0) {
                    is.close();
                }
                return jpeg2000;
            }
            if (c1 == 255 && c2 == 79 && c3 == 255 && c4 == 81) {
                Jpeg2000 jpeg2001 = new Jpeg2000(url);
                if (0 != 0) {
                    is.close();
                }
                return jpeg2001;
            }
            if (c1 == PngImage.PNGID[0] && c2 == PngImage.PNGID[1] && c3 == PngImage.PNGID[2] && c4 == PngImage.PNGID[3]) {
                Image image = PngImage.getImage(url);
                if (0 != 0) {
                    is.close();
                }
                return image;
            }
            if (c1 == 215 && c2 == 205) {
                ImgWMF imgWMF = new ImgWMF(url);
                if (0 != 0) {
                    is.close();
                }
                return imgWMF;
            }
            if (c1 == 66 && c2 == 77) {
                Image image2 = BmpImage.getImage(url);
                if (0 != 0) {
                    is.close();
                }
                return image2;
            }
            if ((c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42) || (c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0)) {
                RandomAccessFileOrArray ra = null;
                try {
                    if (url.getProtocol().equals("file")) {
                        String file = url.getFile();
                        ra = new RandomAccessFileOrArray(Utilities.unEscapeURL(file));
                    } else {
                        ra = new RandomAccessFileOrArray(url);
                    }
                    Image img2 = TiffImage.getTiffImage(ra, 1);
                    img2.url = url;
                    if (ra != null) {
                        ra.close();
                    }
                    if (0 != 0) {
                        is.close();
                    }
                    return img2;
                } catch (Throwable th) {
                    if (ra != null) {
                        ra.close();
                    }
                    throw th;
                }
            }
            if (c1 != 151 || c2 != 74 || c3 != 66 || c4 != 50 || c5 != 13 || c6 != 10 || c7 != 26 || c8 != 10) {
                throw new IOException(url.toString() + " is not a recognized imageformat.");
            }
            RandomAccessFileOrArray ra2 = null;
            try {
                if (url.getProtocol().equals("file")) {
                    String file2 = url.getFile();
                    ra2 = new RandomAccessFileOrArray(Utilities.unEscapeURL(file2));
                } else {
                    ra2 = new RandomAccessFileOrArray(url);
                }
                Image img3 = JBIG2Image.getJbig2Image(ra2, 1);
                img3.url = url;
                if (ra2 != null) {
                    ra2.close();
                }
                if (0 != 0) {
                    is.close();
                }
                return img3;
            } catch (Throwable th2) {
                if (ra2 != null) {
                    ra2.close();
                }
                throw th2;
            }
            if (is != null) {
                is.close();
            }
            throw th;
        } catch (Throwable th3) {
            if (is != null) {
                is.close();
            }
            throw th3;
        }
    }

    public static Image getInstance(String filename) throws BadElementException, IOException {
        return getInstance(Utilities.toURL(filename));
    }

    /* JADX WARN: Code duplicated, block: B:156:0x02b4  */
    public static Image getInstance(byte[] imgb) throws BadElementException, IOException {
        InputStream is = null;
        try {
            InputStream is2 = new ByteArrayInputStream(imgb);
            int c1 = is2.read();
            int c2 = is2.read();
            int c3 = is2.read();
            int c4 = is2.read();
            is2.close();
            is = null;
            if (c1 == 71 && c2 == 73 && c3 == 70) {
                GifImage gif = new GifImage(imgb);
                Image image = gif.getImage(1);
                if (0 != 0) {
                    is.close();
                }
                return image;
            }
            if (c1 == 255 && c2 == 216) {
                Jpeg jpeg = new Jpeg(imgb);
                if (0 != 0) {
                    is.close();
                }
                return jpeg;
            }
            if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 12) {
                Jpeg2000 jpeg2000 = new Jpeg2000(imgb);
                if (0 != 0) {
                    is.close();
                }
                return jpeg2000;
            }
            if (c1 == 255 && c2 == 79 && c3 == 255 && c4 == 81) {
                Jpeg2000 jpeg2001 = new Jpeg2000(imgb);
                if (0 != 0) {
                    is.close();
                }
                return jpeg2001;
            }
            if (c1 == PngImage.PNGID[0] && c2 == PngImage.PNGID[1] && c3 == PngImage.PNGID[2] && c4 == PngImage.PNGID[3]) {
                Image image2 = PngImage.getImage(imgb);
                if (0 != 0) {
                    is.close();
                }
                return image2;
            }
            if (c1 == 215 && c2 == 205) {
                ImgWMF imgWMF = new ImgWMF(imgb);
                if (0 != 0) {
                    is.close();
                }
                return imgWMF;
            }
            if (c1 == 66 && c2 == 77) {
                Image image3 = BmpImage.getImage(imgb);
                if (0 != 0) {
                    is.close();
                }
                return image3;
            }
            if ((c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42) || (c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0)) {
                RandomAccessFileOrArray ra = null;
                try {
                    ra = new RandomAccessFileOrArray(imgb);
                    Image img = TiffImage.getTiffImage(ra, 1);
                    if (img.getOriginalData() == null) {
                        img.setOriginalData(imgb);
                    }
                    if (ra != null) {
                        ra.close();
                    }
                    if (0 != 0) {
                        is.close();
                    }
                    return img;
                } catch (Throwable th) {
                    if (ra != null) {
                        ra.close();
                    }
                    throw th;
                }
            }
            if (c1 == 151 && c2 == 74 && c3 == 66 && c4 == 50) {
                is = new ByteArrayInputStream(imgb);
                is.skip(4L);
                int c5 = is.read();
                int c6 = is.read();
                int c7 = is.read();
                int c8 = is.read();
                if (c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10) {
                    int file_header_flags = is.read();
                    if ((file_header_flags & 2) == 2) {
                        int i = (is.read() << 24) | (is.read() << 16) | (is.read() << 8) | is.read();
                    }
                    is.close();
                    RandomAccessFileOrArray ra2 = null;
                    try {
                        ra2 = new RandomAccessFileOrArray(imgb);
                        Image img2 = JBIG2Image.getJbig2Image(ra2, 1);
                        if (img2.getOriginalData() == null) {
                            img2.setOriginalData(imgb);
                        }
                        if (ra2 != null) {
                            ra2.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                        return img2;
                    } catch (Throwable th2) {
                        if (ra2 != null) {
                            ra2.close();
                        }
                        throw th2;
                    }
                }
            }
            throw new IOException("The byte array is not a recognized imageformat.");
            if (is != null) {
                is.close();
            }
            throw th;
        } catch (Throwable th3) {
            if (is != null) {
                is.close();
            }
            throw th3;
        }
    }

    public static Image getInstance(int width, int height, int components, int bpc, byte[] data) throws BadElementException {
        return getInstance(width, height, components, bpc, data, (int[]) null);
    }

    public static Image getInstance(int width, int height, boolean reverseBits, int typeCCITT, int parameters, byte[] data) throws BadElementException {
        return getInstance(width, height, reverseBits, typeCCITT, parameters, data, null);
    }

    private static Image getInstance(int width, int height, boolean reverseBits, int typeCCITT, int parameters, byte[] data, int[] transparency) throws BadElementException {
        if (transparency != null && transparency.length != 2) {
            throw new BadElementException("Transparency length must be equal to 2 with CCITT images");
        }
        Image img = new ImgCCITT(width, height, reverseBits, typeCCITT, parameters, data);
        img.transparency = transparency;
        return img;
    }

    private static Image getInstance(int width, int height, int components, int bpc, byte[] data, int[] transparency) throws BadElementException {
        if (transparency != null && transparency.length != components * 2) {
            throw new BadElementException("Transparency length must be equal to (componentes * 2)");
        }
        if (components == 1 && bpc == 1) {
            byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            return getInstance(width, height, false, 256, 1, g4, transparency);
        }
        Image img = new ImgRaw(width, height, components, bpc, data);
        img.transparency = transparency;
        return img;
    }

    public static Image getInstance(PdfTemplate template) throws BadElementException {
        return new ImgTemplate(template);
    }

    public static Image getInstance(java.awt.Image image, Color color, boolean forceBW) throws BadElementException, IOException {
        if (image instanceof BufferedImage) {
            BufferedImage bi = (BufferedImage) image;
            if (bi.getType() == 12) {
                forceBW = true;
            }
        }
        PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
            if ((pg.getStatus() & 128) != 0) {
                throw new IOException("java.awt.Image fetch aborted or errored");
            }
            int w = pg.getWidth();
            int h = pg.getHeight();
            int[] pixels = (int[]) pg.getPixels();
            if (forceBW) {
                int byteWidth = (w / 8) + ((w & 7) != 0 ? 1 : 0);
                byte[] pixelsByte = new byte[byteWidth * h];
                int index = 0;
                int size = h * w;
                int transColor = 1;
                if (color != null) {
                    transColor = (color.getRed() + color.getGreen()) + color.getBlue() < 384 ? 0 : 1;
                }
                int[] transparency = null;
                int cbyte = 128;
                int wMarker = 0;
                int currByte = 0;
                if (color != null) {
                    for (int j = 0; j < size; j++) {
                        if (((pixels[j] >> 24) & 255) < 250) {
                            if (transColor == 1) {
                                currByte |= cbyte;
                            }
                        } else if ((pixels[j] & LMErr.NERR_ServiceNotInstalled) != 0) {
                            currByte |= cbyte;
                        }
                        cbyte >>= 1;
                        if (cbyte == 0 || wMarker + 1 >= w) {
                            int i = index;
                            index++;
                            pixelsByte[i] = (byte) currByte;
                            cbyte = 128;
                            currByte = 0;
                        }
                        wMarker++;
                        if (wMarker >= w) {
                            wMarker = 0;
                        }
                    }
                } else {
                    for (int j2 = 0; j2 < size; j2++) {
                        if (transparency == null && ((pixels[j2] >> 24) & 255) == 0) {
                            transparency = new int[2];
                            int i2 = (pixels[j2] & LMErr.NERR_ServiceNotInstalled) != 0 ? 255 : 0;
                            transparency[1] = i2;
                            transparency[0] = i2;
                        }
                        if ((pixels[j2] & LMErr.NERR_ServiceNotInstalled) != 0) {
                            currByte |= cbyte;
                        }
                        cbyte >>= 1;
                        if (cbyte == 0 || wMarker + 1 >= w) {
                            int i3 = index;
                            index++;
                            pixelsByte[i3] = (byte) currByte;
                            cbyte = 128;
                            currByte = 0;
                        }
                        wMarker++;
                        if (wMarker >= w) {
                            wMarker = 0;
                        }
                    }
                }
                return getInstance(w, h, 1, 1, pixelsByte, transparency);
            }
            byte[] pixelsByte2 = new byte[w * h * 3];
            byte[] smask = null;
            int index2 = 0;
            int size2 = h * w;
            int red = 255;
            int green = 255;
            int blue = 255;
            if (color != null) {
                red = color.getRed();
                green = color.getGreen();
                blue = color.getBlue();
            }
            int[] transparency2 = null;
            if (color != null) {
                for (int j3 = 0; j3 < size2; j3++) {
                    if (((pixels[j3] >> 24) & 255) < 250) {
                        int i4 = index2;
                        int index3 = index2 + 1;
                        pixelsByte2[i4] = (byte) red;
                        int index4 = index3 + 1;
                        pixelsByte2[index3] = (byte) green;
                        index2 = index4 + 1;
                        pixelsByte2[index4] = (byte) blue;
                    } else {
                        int i5 = index2;
                        int index5 = index2 + 1;
                        pixelsByte2[i5] = (byte) ((pixels[j3] >> 16) & 255);
                        int index6 = index5 + 1;
                        pixelsByte2[index5] = (byte) ((pixels[j3] >> 8) & 255);
                        index2 = index6 + 1;
                        pixelsByte2[index6] = (byte) (pixels[j3] & 255);
                    }
                }
            } else {
                int transparentPixel = 0;
                smask = new byte[w * h];
                boolean shades = false;
                for (int j4 = 0; j4 < size2; j4++) {
                    byte alpha = (byte) ((pixels[j4] >> 24) & 255);
                    smask[j4] = alpha;
                    if (!shades) {
                        if (alpha != 0 && alpha != -1) {
                            shades = true;
                        } else if (transparency2 == null) {
                            if (alpha == 0) {
                                transparentPixel = pixels[j4] & 16777215;
                                int i6 = (transparentPixel >> 16) & 255;
                                int i7 = (transparentPixel >> 8) & 255;
                                int i8 = transparentPixel & 255;
                                transparency2 = new int[]{i6, i6, i7, i7, i8, i8};
                            }
                        } else if ((pixels[j4] & 16777215) != transparentPixel) {
                            shades = true;
                        }
                    }
                    int i9 = index2;
                    int index7 = index2 + 1;
                    pixelsByte2[i9] = (byte) ((pixels[j4] >> 16) & 255);
                    int index8 = index7 + 1;
                    pixelsByte2[index7] = (byte) ((pixels[j4] >> 8) & 255);
                    index2 = index8 + 1;
                    pixelsByte2[index8] = (byte) (pixels[j4] & 255);
                }
                if (shades) {
                    transparency2 = null;
                } else {
                    smask = null;
                }
            }
            Image img = getInstance(w, h, 3, 8, pixelsByte2, transparency2);
            if (smask != null) {
                Image sm = getInstance(w, h, 1, 8, smask);
                try {
                    sm.makeMask();
                    img.setImageMask(sm);
                } catch (DocumentException de) {
                    throw new ExceptionConverter(de);
                }
            }
            return img;
        } catch (InterruptedException e) {
            throw new IOException("java.awt.Image Interrupted waiting for pixels!");
        }
    }

    public static Image getInstance(java.awt.Image image, Color color) throws BadElementException, IOException {
        return getInstance(image, color, false);
    }

    public PdfIndirectReference getDirectReference() {
        return this.directReference;
    }

    public void setDirectReference(PdfIndirectReference directReference) {
        this.directReference = directReference;
    }

    protected Image(Image image) {
        super(image);
        this.bpc = 1;
        this.template = new PdfTemplate[1];
        this.absoluteX = Float.NaN;
        this.absoluteY = Float.NaN;
        this.compressionLevel = -1;
        this.mySerialId = getSerialId();
        this.indentationLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentationRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.widthPercentage = 100.0f;
        this.annotation = null;
        this.originalType = 0;
        this.deflated = false;
        this.dpiX = 0;
        this.dpiY = 0;
        this.XYRatio = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.colorspace = -1;
        this.invert = false;
        this.profile = null;
        this.additional = null;
        this.mask = false;
        this.type = image.type;
        this.url = image.url;
        this.rawData = image.rawData;
        this.bpc = image.bpc;
        this.template = image.template;
        this.alignment = image.alignment;
        this.alt = image.alt;
        this.absoluteX = image.absoluteX;
        this.absoluteY = image.absoluteY;
        this.plainWidth = image.plainWidth;
        this.plainHeight = image.plainHeight;
        this.scaledWidth = image.scaledWidth;
        this.scaledHeight = image.scaledHeight;
        this.mySerialId = image.mySerialId;
        this.directReference = image.directReference;
        this.rotationRadians = image.rotationRadians;
        this.initialRotation = image.initialRotation;
        this.indentationLeft = image.indentationLeft;
        this.indentationRight = image.indentationRight;
        this.spacingBefore = image.spacingBefore;
        this.spacingAfter = image.spacingAfter;
        this.widthPercentage = image.widthPercentage;
        this.annotation = image.annotation;
        this.layer = image.layer;
        this.interpolation = image.interpolation;
        this.originalType = image.originalType;
        this.originalData = image.originalData;
        this.deflated = image.deflated;
        this.dpiX = image.dpiX;
        this.dpiY = image.dpiY;
        this.XYRatio = image.XYRatio;
        this.colorspace = image.colorspace;
        this.invert = image.invert;
        this.profile = image.profile;
        this.additional = image.additional;
        this.mask = image.mask;
        this.imageMask = image.imageMask;
        this.smask = image.smask;
        this.transparency = image.transparency;
    }

    public static Image getInstance(Image image) {
        if (image == null) {
            return null;
        }
        try {
            return (Image) image.getClass().getDeclaredConstructor(Image.class).newInstance(image);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public int type() {
        return this.type;
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    public boolean isJpeg() {
        return this.type == 32;
    }

    public boolean isImgRaw() {
        return this.type == 34;
    }

    public boolean isImgTemplate() {
        return this.type == 35;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public byte[] getRawData() {
        return this.rawData;
    }

    public int getBpc() {
        return this.bpc;
    }

    public PdfTemplate getTemplateData() {
        return this.template[0];
    }

    public void setTemplateData(PdfTemplate template) {
        this.template[0] = template;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setAbsolutePosition(float absoluteX, float absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }

    public boolean hasAbsoluteX() {
        return !Float.isNaN(this.absoluteX);
    }

    public float getAbsoluteX() {
        return this.absoluteX;
    }

    public boolean hasAbsoluteY() {
        return !Float.isNaN(this.absoluteY);
    }

    public float getAbsoluteY() {
        return this.absoluteY;
    }

    public float getScaledWidth() {
        return this.scaledWidth;
    }

    public float getScaledHeight() {
        return this.scaledHeight;
    }

    public float getPlainWidth() {
        return this.plainWidth;
    }

    public float getPlainHeight() {
        return this.plainHeight;
    }

    public void scaleAbsolute(float newWidth, float newHeight) {
        this.plainWidth = newWidth;
        this.plainHeight = newHeight;
        float[] matrix = matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        setWidthPercentage(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void scaleAbsoluteWidth(float newWidth) {
        this.plainWidth = newWidth;
        float[] matrix = matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        setWidthPercentage(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void scaleAbsoluteHeight(float newHeight) {
        this.plainHeight = newHeight;
        float[] matrix = matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        setWidthPercentage(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void scalePercent(float percent) {
        scalePercent(percent, percent);
    }

    private void scalePercent(float percentX, float percentY) {
        this.plainWidth = (getWidth() * percentX) / 100.0f;
        this.plainHeight = (getHeight() * percentY) / 100.0f;
        float[] matrix = matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        setWidthPercentage(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void scaleToFit(float fitWidth, float fitHeight) {
        scalePercent(100.0f);
        float percentX = (fitWidth * 100.0f) / getScaledWidth();
        float percentY = (fitHeight * 100.0f) / getScaledHeight();
        scalePercent(percentX < percentY ? percentX : percentY);
        setWidthPercentage(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public float[] matrix() {
        float[] matrix = new float[8];
        float cosX = (float) Math.cos(this.rotationRadians);
        float sinX = (float) Math.sin(this.rotationRadians);
        matrix[0] = this.plainWidth * cosX;
        matrix[1] = this.plainWidth * sinX;
        matrix[2] = (-this.plainHeight) * sinX;
        matrix[3] = this.plainHeight * cosX;
        if (this.rotationRadians < 1.5707963267948966d) {
            matrix[4] = matrix[2];
            matrix[5] = 0.0f;
            matrix[6] = matrix[0];
            matrix[7] = matrix[1] + matrix[3];
        } else if (this.rotationRadians < 3.141592653589793d) {
            matrix[4] = matrix[0] + matrix[2];
            matrix[5] = matrix[3];
            matrix[6] = 0.0f;
            matrix[7] = matrix[1];
        } else if (this.rotationRadians < 4.71238898038469d) {
            matrix[4] = matrix[0];
            matrix[5] = matrix[1] + matrix[3];
            matrix[6] = matrix[2];
            matrix[7] = 0.0f;
        } else {
            matrix[4] = 0.0f;
            matrix[5] = matrix[1];
            matrix[6] = matrix[0] + matrix[2];
            matrix[7] = matrix[3];
        }
        return matrix;
    }

    private static synchronized Long getSerialId() {
        serialId++;
        return Long.valueOf(serialId);
    }

    public Long getMySerialId() {
        return this.mySerialId;
    }

    public float getImageRotation() {
        float rot = (float) (((double) (this.rotationRadians - this.initialRotation)) % 6.283185307179586d);
        if (rot < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            rot = (float) (((double) rot) + 6.283185307179586d);
        }
        return rot;
    }

    public void setRotation(float r) {
        this.rotationRadians = (float) (((double) (r + this.initialRotation)) % 6.283185307179586d);
        if (this.rotationRadians < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            this.rotationRadians = (float) (((double) this.rotationRadians) + 6.283185307179586d);
        }
        float[] matrix = matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
    }

    public void setRotationDegrees(float deg) {
        setRotation((deg / 180.0f) * 3.1415927f);
    }

    public float getInitialRotation() {
        return this.initialRotation;
    }

    public void setInitialRotation(float initialRotation) {
        float old_rot = this.rotationRadians - this.initialRotation;
        this.initialRotation = initialRotation;
        setRotation(old_rot);
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public void setIndentationLeft(float f) {
        this.indentationLeft = f;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public void setIndentationRight(float f) {
        this.indentationRight = f;
    }

    public float getSpacingBefore() {
        return this.spacingBefore;
    }

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    public float getSpacingAfter() {
        return this.spacingAfter;
    }

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public PdfOCG getLayer() {
        return this.layer;
    }

    public void setLayer(PdfOCG layer) {
        this.layer = layer;
    }

    public boolean isInterpolation() {
        return this.interpolation;
    }

    public void setInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
    }

    public int getOriginalType() {
        return this.originalType;
    }

    public void setOriginalType(int originalType) {
        this.originalType = originalType;
    }

    public byte[] getOriginalData() {
        return this.originalData;
    }

    public void setOriginalData(byte[] originalData) {
        this.originalData = originalData;
    }

    public boolean isDeflated() {
        return this.deflated;
    }

    public void setDeflated(boolean deflated) {
        this.deflated = deflated;
    }

    public int getDpiX() {
        return this.dpiX;
    }

    public int getDpiY() {
        return this.dpiY;
    }

    public void setDpi(int dpiX, int dpiY) {
        this.dpiX = dpiX;
        this.dpiY = dpiY;
    }

    public float getXYRatio() {
        return this.XYRatio;
    }

    public void setXYRatio(float XYRatio) {
        this.XYRatio = XYRatio;
    }

    public int getColorspace() {
        return this.colorspace;
    }

    public boolean isInverted() {
        return this.invert;
    }

    public void setInverted(boolean invert) {
        this.invert = invert;
    }

    public void tagICC(ICC_Profile profile) {
        this.profile = profile;
    }

    public boolean hasICCProfile() {
        return this.profile != null;
    }

    public ICC_Profile getICCProfile() {
        return this.profile;
    }

    public PdfDictionary getAdditional() {
        return this.additional;
    }

    public void setAdditional(PdfDictionary additional) {
        this.additional = additional;
    }

    private PdfObject simplifyColorspace(PdfArray obj) {
        if (obj == null) {
            return obj;
        }
        PdfName first = obj.getAsName(0);
        if (PdfName.CALGRAY.equals(first)) {
            return PdfName.DEVICEGRAY;
        }
        if (PdfName.CALRGB.equals(first)) {
            return PdfName.DEVICERGB;
        }
        return obj;
    }

    public boolean isMask() {
        return this.mask;
    }

    public void makeMask() throws DocumentException {
        if (!isMaskCandidate()) {
            throw new DocumentException("This image can not be an image mask.");
        }
        this.mask = true;
    }

    public boolean isMaskCandidate() {
        return (this.type == 34 && this.bpc > 255) || this.colorspace == 1;
    }

    public Image getImageMask() {
        return this.imageMask;
    }

    public void setImageMask(Image mask) throws DocumentException {
        if (this.mask) {
            throw new DocumentException("An image mask cannot contain another image mask.");
        }
        if (!mask.mask) {
            throw new DocumentException("The image mask is not a mask. Did you do makeMask()?");
        }
        this.imageMask = mask;
        this.smask = mask.bpc > 1 && mask.bpc <= 8;
    }

    public boolean isSmask() {
        return this.smask;
    }

    public void setSmask(boolean smask) {
        this.smask = smask;
    }

    public int[] getTransparency() {
        return this.transparency;
    }

    public void setTransparency(int[] transparency) {
        this.transparency = transparency;
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            this.compressionLevel = -1;
        } else {
            this.compressionLevel = compressionLevel;
        }
    }
}
