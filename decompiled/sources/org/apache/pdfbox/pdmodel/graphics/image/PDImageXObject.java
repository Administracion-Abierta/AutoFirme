package org.apache.pdfbox.pdmodel.graphics.image;

import com.sun.jna.platform.win32.WinNT;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImageObserver;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.util.filetypedetector.FileType;
import org.apache.pdfbox.util.filetypedetector.FileTypeDetector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/PDImageXObject.class */
public final class PDImageXObject extends PDXObject implements PDImage {
    private static final Log LOG = LogFactory.getLog(PDImageXObject.class);
    private SoftReference<BufferedImage> cachedImage;
    private PDColorSpace colorSpace;
    private int cachedImageSubsampling;
    private final PDResources resources;

    public PDImageXObject(PDDocument document) throws IOException {
        this(new PDStream(document), null);
    }

    public PDImageXObject(PDDocument document, InputStream encodedStream, COSBase cosFilter, int width, int height, int bitsPerComponent, PDColorSpace initColorSpace) throws IOException {
        super(createRawStream(document, encodedStream), COSName.IMAGE);
        this.cachedImageSubsampling = WinNT.MAXLONG;
        getCOSObject().setItem(COSName.FILTER, cosFilter);
        this.resources = null;
        this.colorSpace = null;
        setBitsPerComponent(bitsPerComponent);
        setWidth(width);
        setHeight(height);
        setColorSpace(initColorSpace);
    }

    public PDImageXObject(PDStream stream, PDResources resources) throws IOException {
        super(stream, COSName.IMAGE);
        this.cachedImageSubsampling = WinNT.MAXLONG;
        this.resources = resources;
        List<COSName> filters = stream.getFilters();
        if (filters != null && !filters.isEmpty() && COSName.JPX_DECODE.equals(filters.get(filters.size() - 1))) {
            COSInputStream is = null;
            try {
                COSInputStream is2 = stream.createInputStream();
                DecodeResult decodeResult = is2.getDecodeResult();
                stream.getCOSObject().addAll(decodeResult.getParameters());
                this.colorSpace = decodeResult.getJPXColorSpace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public static PDImageXObject createThumbnail(COSStream cosStream) throws IOException {
        PDStream pdStream = new PDStream(cosStream);
        return new PDImageXObject(pdStream, null);
    }

    private static COSStream createRawStream(PDDocument document, InputStream rawInput) throws IOException {
        COSStream stream = document.getDocument().createCOSStream();
        OutputStream output = null;
        try {
            OutputStream output2 = stream.createRawOutputStream();
            IOUtils.copy(rawInput, output2);
            if (output2 != null) {
            }
            return stream;
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public static PDImageXObject createFromFile(String imagePath, PDDocument doc) throws IOException {
        return createFromFileByExtension(new File(imagePath), doc);
    }

    public static PDImageXObject createFromFileByExtension(File file, PDDocument doc) throws IOException {
        String name = file.getName();
        int dot = name.lastIndexOf(46);
        if (dot == -1) {
            throw new IllegalArgumentException("Image type not supported: " + name);
        }
        String ext = name.substring(dot + 1).toLowerCase();
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            FileInputStream fis = null;
            try {
                FileInputStream fis2 = new FileInputStream(file);
                return JPEGFactory.createFromStream(doc, fis2);
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
        if ("tif".equals(ext) || "tiff".equals(ext)) {
            return CCITTFactory.createFromFile(doc, file);
        }
        if ("gif".equals(ext) || "bmp".equals(ext) || "png".equals(ext)) {
            BufferedImage bim = ImageIO.read(file);
            return LosslessFactory.createFromImage(doc, bim);
        }
        throw new IllegalArgumentException("Image type not supported: " + name);
    }

    public static PDImageXObject createFromFileByContent(File file, PDDocument doc) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            try {
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                FileType fileType = FileTypeDetector.detectFileType(bufferedInputStream);
                IOUtils.closeQuietly(fileInputStream);
                IOUtils.closeQuietly(bufferedInputStream);
                if (fileType == null) {
                    throw new IllegalArgumentException("Image type not supported: " + file.getName());
                }
                if (fileType.equals(FileType.JPEG)) {
                    FileInputStream fis = new FileInputStream(file);
                    PDImageXObject imageXObject = JPEGFactory.createFromStream(doc, fis);
                    fis.close();
                    return imageXObject;
                }
                if (fileType.equals(FileType.TIFF)) {
                    try {
                        return CCITTFactory.createFromFile(doc, file);
                    } catch (IOException ex) {
                        LOG.debug("Reading as TIFF failed, setting fileType to PNG", ex);
                        fileType = FileType.PNG;
                    }
                }
                if (fileType.equals(FileType.BMP) || fileType.equals(FileType.GIF) || fileType.equals(FileType.PNG)) {
                    BufferedImage bim = ImageIO.read(file);
                    return LosslessFactory.createFromImage(doc, bim);
                }
                throw new IllegalArgumentException("Image type " + fileType + " not supported: " + file.getName());
            } catch (IOException e) {
                throw new IOException("Could not determine file type: " + file.getName(), e);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(bufferedInputStream);
            throw th;
        }
    }

    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray, String name) throws IOException {
        PDImageXObject image;
        try {
            FileType fileType = FileTypeDetector.detectFileType(byteArray);
            if (fileType == null) {
                throw new IllegalArgumentException("Image type not supported: " + name);
            }
            if (fileType.equals(FileType.JPEG)) {
                return JPEGFactory.createFromByteArray(document, byteArray);
            }
            if (fileType.equals(FileType.PNG) && (image = PNGConverter.convertPNGImage(document, byteArray)) != null) {
                return image;
            }
            if (fileType.equals(FileType.TIFF)) {
                try {
                    return CCITTFactory.createFromByteArray(document, byteArray);
                } catch (IOException ex) {
                    LOG.debug("Reading as TIFF failed, setting fileType to PNG", ex);
                    fileType = FileType.PNG;
                }
            }
            if (fileType.equals(FileType.BMP) || fileType.equals(FileType.GIF) || fileType.equals(FileType.PNG)) {
                ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
                BufferedImage bim = ImageIO.read(bais);
                return LosslessFactory.createFromImage(document, bim);
            }
            throw new IllegalArgumentException("Image type " + fileType + " not supported: " + name);
        } catch (IOException e) {
            throw new IOException("Could not determine file type: " + name, e);
        }
    }

    public PDMetadata getMetadata() {
        COSStream cosStream = getCOSObject().getCOSStream(COSName.METADATA);
        if (cosStream != null) {
            return new PDMetadata(cosStream);
        }
        return null;
    }

    public void setMetadata(PDMetadata meta) {
        getCOSObject().setItem(COSName.METADATA, meta);
    }

    public int getStructParent() {
        return getCOSObject().getInt(COSName.STRUCT_PARENT);
    }

    public void setStructParent(int key) {
        getCOSObject().setInt(COSName.STRUCT_PARENT, key);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public BufferedImage getImage() throws IOException {
        return getImage(null, 1);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public BufferedImage getImage(Rectangle region, int subsampling) throws IOException {
        BufferedImage image;
        BufferedImage cached;
        if (region == null && subsampling == this.cachedImageSubsampling && this.cachedImage != null && (cached = this.cachedImage.get()) != null) {
            return cached;
        }
        PDImageXObject softMask = getSoftMask();
        PDImageXObject mask = getMask();
        if (softMask != null) {
            image = applyMask(SampledImageReader.getRGBImage(this, region, subsampling, getColorKeyMask()), softMask.getOpaqueImage(), softMask.getInterpolate(), true, extractMatte(softMask));
        } else if (mask != null && mask.isStencil()) {
            image = applyMask(SampledImageReader.getRGBImage(this, region, subsampling, getColorKeyMask()), mask.getOpaqueImage(), mask.getInterpolate(), false, null);
        } else {
            image = SampledImageReader.getRGBImage(this, region, subsampling, getColorKeyMask());
        }
        if (region == null && subsampling <= this.cachedImageSubsampling) {
            this.cachedImageSubsampling = subsampling;
            this.cachedImage = new SoftReference<>(image);
        }
        return image;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public BufferedImage getRawImage() throws IOException {
        return getColorSpace().toRawImage(getRawRaster());
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public WritableRaster getRawRaster() throws IOException {
        return SampledImageReader.getRawRaster(this);
    }

    private float[] extractMatte(PDImageXObject softMask) throws IOException {
        COSBase base = softMask.getCOSObject().getItem(COSName.MATTE);
        float[] matte = null;
        if (base instanceof COSArray) {
            float[] matte2 = ((COSArray) base).toFloatArray();
            if (matte2.length < getColorSpace().getNumberOfComponents()) {
                LOG.error("Image /Matte entry not long enough for colorspace, skipped");
                return null;
            }
            matte = getColorSpace().toRGB(matte2);
        }
        return matte;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public BufferedImage getStencilImage(Paint paint) throws IOException {
        if (!isStencil()) {
            throw new IllegalStateException("Image is not a stencil");
        }
        return SampledImageReader.getStencilImage(this, paint);
    }

    public BufferedImage getOpaqueImage() throws IOException {
        return SampledImageReader.getRGBImage(this, null);
    }

    private BufferedImage applyMask(BufferedImage image, BufferedImage mask, boolean interpolateMask, boolean isSoft, float[] matte) {
        int offset;
        if (mask == null) {
            return image;
        }
        int width = Math.max(image.getWidth(), mask.getWidth());
        int height = Math.max(image.getHeight(), mask.getHeight());
        if (mask.getWidth() < width || mask.getHeight() < height) {
            mask = scaleImage(mask, width, height, 10, interpolateMask);
        } else if (mask.getType() != 10) {
            mask = scaleImage(mask, width, height, 10, false);
        }
        if (image.getWidth() < width || image.getHeight() < height) {
            image = scaleImage(image, width, height, 2, getInterpolate());
        } else if (image.getType() != 2) {
            image = scaleImage(image, width, height, 2, false);
        }
        WritableRaster raster = image.getRaster();
        WritableRaster alpha = mask.getRaster();
        if (!isSoft && raster.getDataBuffer().getSize() == alpha.getDataBuffer().getSize()) {
            DataBuffer dst = raster.getDataBuffer();
            DataBuffer src = alpha.getDataBuffer();
            int i = 0;
            for (int c = dst.getSize(); c > 0; c--) {
                dst.setElem(i, (dst.getElem(i) & 16777215) | ((src.getElem(i) ^ (-1)) << 24));
                i++;
            }
        } else if (matte == null) {
            int[] samples = new int[width];
            for (int y = 0; y < height; y++) {
                alpha.getSamples(0, y, width, 1, 0, samples);
                if (!isSoft) {
                    for (int x = 0; x < width; x++) {
                        int i2 = x;
                        samples[i2] = samples[i2] ^ (-1);
                    }
                }
                raster.setSamples(0, y, width, 1, 3, samples);
            }
        } else {
            int[] alphas = new int[width];
            int[] pixels = new int[4 * width];
            int m0 = Math.round(8355840.0f * matte[0]) * 255;
            int m1 = Math.round(8355840.0f * matte[1]) * 255;
            int m2 = Math.round(8355840.0f * matte[2]) * 255;
            int m0h = (m0 / 255) + 16384;
            int m1h = (m1 / 255) + 16384;
            int m2h = (m2 / 255) + 16384;
            for (int y2 = 0; y2 < height; y2++) {
                raster.getPixels(0, y2, width, 1, pixels);
                alpha.getSamples(0, y2, width, 1, 0, alphas);
                int offset2 = 0;
                for (int x2 = 0; x2 < width; x2++) {
                    int a = alphas[x2];
                    if (a == 0) {
                        offset = offset2 + 3;
                    } else {
                        int i3 = offset2;
                        int i4 = offset2;
                        int offset3 = offset2 + 1;
                        pixels[i3] = clampColor(((((pixels[i4] * 8355840) - m0) / a) + m0h) >> 15);
                        int offset4 = offset3 + 1;
                        pixels[offset3] = clampColor(((((pixels[offset3] * 8355840) - m1) / a) + m1h) >> 15);
                        offset = offset4 + 1;
                        pixels[offset4] = clampColor(((((pixels[offset4] * 8355840) - m2) / a) + m2h) >> 15);
                    }
                    int i5 = offset;
                    offset2 = offset + 1;
                    pixels[i5] = a;
                }
                raster.setPixels(0, y2, width, 1, pixels);
            }
        }
        return image;
    }

    private static int clampColor(int color) {
        if (color < 0) {
            return 0;
        }
        if (color > 255) {
            return 255;
        }
        return color;
    }

    private static BufferedImage scaleImage(BufferedImage image, int width, int height, int type, boolean interpolate) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        boolean largeScale = width * height > 9000000 * (type == 10 ? 3 : 1);
        boolean interpolate2 = interpolate & ((imgWidth == width && imgHeight == height) ? false : true);
        BufferedImage image2 = new BufferedImage(width, height, type);
        if (interpolate2) {
            AffineTransform af = AffineTransform.getScaleInstance(((double) width) / ((double) imgWidth), ((double) height) / ((double) imgHeight));
            AffineTransformOp afo = new AffineTransformOp(af, largeScale ? 2 : 3);
            try {
                afo.filter(image, image2);
                return image2;
            } catch (ImagingOpException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        Graphics2D g = image2.createGraphics();
        if (interpolate2) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, largeScale ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, largeScale ? RenderingHints.VALUE_RENDER_DEFAULT : RenderingHints.VALUE_RENDER_QUALITY);
        }
        g.drawImage(image, 0, 0, width, height, 0, 0, imgWidth, imgHeight, (ImageObserver) null);
        g.dispose();
        return image2;
    }

    public PDImageXObject getMask() throws IOException {
        COSStream cosStream;
        COSBase mask = getCOSObject().getDictionaryObject(COSName.MASK);
        if (!(mask instanceof COSArray) && (cosStream = getCOSObject().getCOSStream(COSName.MASK)) != null) {
            return new PDImageXObject(new PDStream(cosStream), null);
        }
        return null;
    }

    public COSArray getColorKeyMask() {
        COSBase mask = getCOSObject().getDictionaryObject(COSName.MASK);
        if (mask instanceof COSArray) {
            return (COSArray) mask;
        }
        return null;
    }

    public PDImageXObject getSoftMask() throws IOException {
        COSStream cosStream = getCOSObject().getCOSStream(COSName.SMASK);
        if (cosStream != null) {
            return new PDImageXObject(new PDStream(cosStream), null);
        }
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public int getBitsPerComponent() {
        if (isStencil()) {
            return 1;
        }
        return getCOSObject().getInt(COSName.BITS_PER_COMPONENT, COSName.BPC);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setBitsPerComponent(int bpc) {
        getCOSObject().setInt(COSName.BITS_PER_COMPONENT, bpc);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public PDColorSpace getColorSpace() throws IOException {
        if (this.colorSpace == null) {
            COSBase cosBase = getCOSObject().getItem(COSName.COLORSPACE, COSName.CS);
            if (cosBase != null) {
                COSObject indirect = null;
                if ((cosBase instanceof COSObject) && this.resources != null && this.resources.getResourceCache() != null) {
                    indirect = (COSObject) cosBase;
                    this.colorSpace = this.resources.getResourceCache().getColorSpace(indirect);
                    if (this.colorSpace != null) {
                        return this.colorSpace;
                    }
                }
                this.colorSpace = PDColorSpace.create(cosBase, this.resources);
                if (indirect != null) {
                    this.resources.getResourceCache().put(indirect, this.colorSpace);
                }
            } else {
                if (isStencil()) {
                    return PDDeviceGray.INSTANCE;
                }
                throw new IOException("could not determine color space");
            }
        }
        return this.colorSpace;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public InputStream createInputStream() throws IOException {
        return getStream().createInputStream();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public InputStream createInputStream(DecodeOptions options) throws IOException {
        return getStream().createInputStream(options);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public InputStream createInputStream(List<String> stopFilters) throws IOException {
        return getStream().createInputStream(stopFilters);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public boolean isEmpty() {
        return getStream().getCOSObject().getLength() == 0;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setColorSpace(PDColorSpace cs) {
        getCOSObject().setItem(COSName.COLORSPACE, cs != null ? cs.getCOSObject() : null);
        this.colorSpace = null;
        this.cachedImage = null;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public int getHeight() {
        return getCOSObject().getInt(COSName.HEIGHT);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setHeight(int h) {
        getCOSObject().setInt(COSName.HEIGHT, h);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public int getWidth() {
        return getCOSObject().getInt(COSName.WIDTH);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setWidth(int w) {
        getCOSObject().setInt(COSName.WIDTH, w);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public boolean getInterpolate() {
        return getCOSObject().getBoolean(COSName.INTERPOLATE, false);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setInterpolate(boolean value) {
        getCOSObject().setBoolean(COSName.INTERPOLATE, value);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setDecode(COSArray decode) {
        getCOSObject().setItem(COSName.DECODE, (COSBase) decode);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public COSArray getDecode() {
        COSBase decode = getCOSObject().getDictionaryObject(COSName.DECODE);
        if (decode instanceof COSArray) {
            return (COSArray) decode;
        }
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public boolean isStencil() {
        return getCOSObject().getBoolean(COSName.IMAGE_MASK, false);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public void setStencil(boolean isStencil) {
        getCOSObject().setBoolean(COSName.IMAGE_MASK, isStencil);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.image.PDImage
    public String getSuffix() {
        List<COSName> filters = getStream().getFilters();
        if (filters == null) {
            return "png";
        }
        if (filters.contains(COSName.DCT_DECODE)) {
            return "jpg";
        }
        if (filters.contains(COSName.JPX_DECODE)) {
            return "jpx";
        }
        if (filters.contains(COSName.CCITTFAX_DECODE)) {
            return "tiff";
        }
        if (filters.contains(COSName.FLATE_DECODE) || filters.contains(COSName.LZW_DECODE) || filters.contains(COSName.RUN_LENGTH_DECODE)) {
            return "png";
        }
        if (filters.contains(COSName.JBIG2_DECODE)) {
            return "jb2";
        }
        LOG.warn("getSuffix() returns null, filters: " + filters);
        return null;
    }

    public PDPropertyList getOptionalContent() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.OC);
        if (base instanceof COSDictionary) {
            return PDPropertyList.create((COSDictionary) base);
        }
        return null;
    }

    public void setOptionalContent(PDPropertyList oc) {
        getCOSObject().setItem(COSName.OC, oc);
    }
}
