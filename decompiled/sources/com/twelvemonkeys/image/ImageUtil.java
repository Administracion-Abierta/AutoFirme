package com.twelvemonkeys.image;

import com.aowagie.text.pdf.ColumnText;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.awt.image.PackedColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ImageUtil.class */
public final class ImageUtil {
    public static final int ROTATE_90_CCW = -90;
    public static final int ROTATE_90_CW = 90;
    public static final int ROTATE_180 = 180;
    public static final int FLIP_VERTICAL = -1;
    public static final int FLIP_HORIZONTAL = 1;
    public static final int EDGE_ZERO_FILL = 0;
    public static final int EDGE_NO_OP = 1;
    public static final int EDGE_REFLECT = 2;
    public static final int EDGE_WRAP = 3;
    public static final int DITHER_DEFAULT = 0;
    public static final int DITHER_NONE = 1;
    public static final int DITHER_DIFFUSION = 2;
    public static final int DITHER_DIFFUSION_ALTSCANS = 3;
    public static final int COLOR_SELECTION_DEFAULT = 0;
    public static final int COLOR_SELECTION_FAST = 256;
    public static final int COLOR_SELECTION_QUALITY = 512;
    public static final int TRANSPARENCY_DEFAULT = 0;
    public static final int TRANSPARENCY_OPAQUE = 65536;
    public static final int TRANSPARENCY_BITMASK = 131072;
    protected static final int TRANSPARENCY_TRANSLUCENT = 196608;
    private static final int BI_TYPE_ANY = -1;
    private static boolean VM_SUPPORTS_ACCELERATION = true;
    private static final float[] SHARPEN_MATRIX = {ColumnText.GLOBAL_SPACE_CHAR_RATIO, -0.3f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -0.3f, 2.2f, -0.3f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -0.3f, ColumnText.GLOBAL_SPACE_CHAR_RATIO};
    private static final Kernel SHARPEN_KERNEL = new Kernel(3, 3, SHARPEN_MATRIX);
    private static final Component NULL_COMPONENT = new Component() { // from class: com.twelvemonkeys.image.ImageUtil.1
    };
    private static MediaTracker sTracker = new MediaTracker(NULL_COMPONENT);
    protected static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
    protected static final Point LOCATION_UPPER_LEFT = new Point(0, 0);
    private static final GraphicsConfiguration DEFAULT_CONFIGURATION = getDefaultGraphicsConfiguration();

    private static GraphicsConfiguration getDefaultGraphicsConfiguration() {
        try {
            GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (!localGraphicsEnvironment.isHeadlessInstance()) {
                return localGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
            }
            return null;
        } catch (LinkageError e) {
            VM_SUPPORTS_ACCELERATION = false;
            return null;
        }
    }

    private ImageUtil() {
    }

    public static BufferedImage toBuffered(RenderedImage renderedImage) {
        Hashtable hashtable;
        WritableRaster writableRasterCopyData;
        if (renderedImage instanceof BufferedImage) {
            return (BufferedImage) renderedImage;
        }
        if (renderedImage == null) {
            throw new IllegalArgumentException("original == null");
        }
        String[] propertyNames = renderedImage.getPropertyNames();
        if (propertyNames != null && propertyNames.length > 0) {
            hashtable = new Hashtable(propertyNames.length);
            for (String str : propertyNames) {
                hashtable.put(str, renderedImage.getProperty(str));
            }
        } else {
            hashtable = null;
        }
        WritableRaster data = renderedImage.getData();
        if (data instanceof WritableRaster) {
            writableRasterCopyData = data;
        } else {
            writableRasterCopyData = renderedImage.copyData(data.createCompatibleWritableRaster());
        }
        ColorModel colorModel = renderedImage.getColorModel();
        return new BufferedImage(colorModel, writableRasterCopyData, colorModel.isAlphaPremultiplied(), hashtable);
    }

    public static BufferedImage toBuffered(RenderedImage renderedImage, int i) {
        if ((renderedImage instanceof BufferedImage) && ((BufferedImage) renderedImage).getType() == i) {
            return (BufferedImage) renderedImage;
        }
        if (renderedImage == null) {
            throw new IllegalArgumentException("original == null");
        }
        BufferedImage bufferedImageCreateBuffered = createBuffered(renderedImage.getWidth(), renderedImage.getHeight(), i, 3);
        Graphics2D graphics2DCreateGraphics = bufferedImageCreateBuffered.createGraphics();
        try {
            graphics2DCreateGraphics.setComposite(AlphaComposite.Src);
            graphics2DCreateGraphics.drawRenderedImage(renderedImage, IDENTITY_TRANSFORM);
            return bufferedImageCreateBuffered;
        } finally {
            graphics2DCreateGraphics.dispose();
        }
    }

    public static BufferedImage toBuffered(BufferedImage bufferedImage, int i) {
        return toBuffered((RenderedImage) bufferedImage, i);
    }

    public static BufferedImage toBuffered(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        if (image == null) {
            throw new IllegalArgumentException("original == null");
        }
        return new BufferedImageFactory(image).getBufferedImage();
    }

    public static BufferedImage createCopy(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        ColorModel colorModel = bufferedImage.getColorModel();
        BufferedImage bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), (Hashtable) null);
        drawOnto(bufferedImage2, bufferedImage);
        return bufferedImage2;
    }

    static WritableRaster createRaster(int i, int i2, Object obj, ColorModel colorModel) {
        int length;
        DataBufferInt dataBufferByte = null;
        WritableRaster writableRasterCreateCompatibleWritableRaster = null;
        if (obj instanceof int[]) {
            int[] iArr = (int[]) obj;
            dataBufferByte = new DataBufferInt(iArr, iArr.length);
            length = colorModel.getNumComponents();
        } else if (obj instanceof short[]) {
            short[] sArr = (short[]) obj;
            dataBufferByte = new DataBufferUShort(sArr, sArr.length);
            length = sArr.length / (i * i2);
        } else if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            dataBufferByte = new DataBufferByte(bArr, bArr.length);
            if (colorModel instanceof IndexColorModel) {
                length = 1;
            } else {
                length = bArr.length / (i * i2);
            }
        } else {
            length = -1;
            writableRasterCreateCompatibleWritableRaster = colorModel.createCompatibleWritableRaster(i, i2);
            writableRasterCreateCompatibleWritableRaster.setDataElements(0, 0, i, i2, obj);
        }
        if (writableRasterCreateCompatibleWritableRaster == null) {
            if ((colorModel instanceof IndexColorModel) && isIndexedPacked((IndexColorModel) colorModel)) {
                writableRasterCreateCompatibleWritableRaster = Raster.createPackedRaster(dataBufferByte, i, i2, colorModel.getPixelSize(), LOCATION_UPPER_LEFT);
            } else if (colorModel instanceof PackedColorModel) {
                writableRasterCreateCompatibleWritableRaster = Raster.createPackedRaster(dataBufferByte, i, i2, i, ((PackedColorModel) colorModel).getMasks(), LOCATION_UPPER_LEFT);
            } else {
                int[] iArr2 = new int[length];
                int i3 = 0;
                while (i3 < length) {
                    int i4 = i3;
                    i3++;
                    iArr2[i4] = length - i3;
                }
                writableRasterCreateCompatibleWritableRaster = Raster.createInterleavedRaster(dataBufferByte, i, i2, i * length, length, iArr2, LOCATION_UPPER_LEFT);
            }
        }
        return writableRasterCreateCompatibleWritableRaster;
    }

    private static boolean isIndexedPacked(IndexColorModel indexColorModel) {
        return indexColorModel.getPixelSize() == 1 || indexColorModel.getPixelSize() == 2 || indexColorModel.getPixelSize() == 4;
    }

    static WritableRaster createCompatibleWritableRaster(BufferedImage bufferedImage, ColorModel colorModel, int i, int i2) {
        if (colorModel == null || equals(bufferedImage.getColorModel(), colorModel)) {
            switch (bufferedImage.getType()) {
                case 0:
                    ComponentSampleModel sampleModel = bufferedImage.getRaster().getSampleModel();
                    if (sampleModel instanceof ComponentSampleModel) {
                        int[] bandOffsets = sampleModel.getBandOffsets();
                        return Raster.createInterleavedRaster(sampleModel.getDataType(), i, i2, i * bandOffsets.length, bandOffsets.length, bandOffsets, (Point) null);
                    }
                    break;
                case 5:
                    return Raster.createInterleavedRaster(0, i, i2, i * 3, 3, new int[]{2, 1, 0}, (Point) null);
                case 6:
                case 7:
                    return Raster.createInterleavedRaster(0, i, i2, i * 4, 4, new int[]{3, 2, 1, 0}, (Point) null);
            }
            return bufferedImage.getColorModel().createCompatibleWritableRaster(i, i2);
        }
        return colorModel.createCompatibleWritableRaster(i, i2);
    }

    public static BufferedImage toBuffered(Image image, int i) {
        return toBuffered(image, i, null);
    }

    private static BufferedImage toBuffered(Image image, int i, IndexColorModel indexColorModel) {
        BufferedImage bufferedImage;
        if ((image instanceof BufferedImage) && ((BufferedImage) image).getType() == i && (indexColorModel == null || equals(((BufferedImage) image).getColorModel(), indexColorModel))) {
            return (BufferedImage) image;
        }
        if (image == null) {
            throw new IllegalArgumentException("original == null");
        }
        if (indexColorModel == null) {
            bufferedImage = createBuffered(getWidth(image), getHeight(image), i, 3);
        } else {
            bufferedImage = new BufferedImage(getWidth(image), getHeight(image), i, indexColorModel);
        }
        drawOnto(bufferedImage, image);
        return bufferedImage;
    }

    static void drawOnto(BufferedImage bufferedImage, Image image) {
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        try {
            graphics2DCreateGraphics.setComposite(AlphaComposite.Src);
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2DCreateGraphics.drawImage(image, 0, 0, (ImageObserver) null);
        } finally {
            graphics2DCreateGraphics.dispose();
        }
    }

    public static BufferedImage createFlipped(Image image, int i) {
        AffineTransform translateInstance;
        switch (i) {
            case -1:
            case 1:
                BufferedImage buffered = toBuffered(image);
                if (i == 1) {
                    translateInstance = AffineTransform.getTranslateInstance(0.0d, buffered.getHeight());
                    translateInstance.scale(1.0d, -1.0d);
                } else {
                    translateInstance = AffineTransform.getTranslateInstance(buffered.getWidth(), 0.0d);
                    translateInstance.scale(-1.0d, 1.0d);
                }
                return new AffineTransformOp(translateInstance, 1).filter(buffered, (BufferedImage) null);
            default:
                throw new IllegalArgumentException("Illegal direction: " + i);
        }
    }

    public static BufferedImage createRotated(Image image, int i) {
        switch (i) {
            case ROTATE_90_CCW /* -90 */:
            case 90:
            case 180:
                return createRotated(image, Math.toRadians(i));
            default:
                throw new IllegalArgumentException("Illegal direction: " + i);
        }
    }

    public static BufferedImage createRotated(Image image, double d) {
        return createRotated0(toBuffered(image), d);
    }

    private static BufferedImage createRotated0(BufferedImage bufferedImage, double d) {
        if (Math.abs(Math.toDegrees(d)) % 360.0d == 0.0d) {
            return bufferedImage;
        }
        boolean z = Math.abs(Math.toDegrees(d)) % 90.0d == 0.0d;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        double dAbs = Math.abs(Math.sin(d));
        double dAbs2 = Math.abs(Math.cos(d));
        int iFloor = (int) Math.floor((((double) width) * dAbs2) + (((double) height) * dAbs));
        int iFloor2 = (int) Math.floor((((double) height) * dAbs2) + (((double) width) * dAbs));
        AffineTransform translateInstance = AffineTransform.getTranslateInstance(((double) (iFloor - width)) / 2.0d, ((double) (iFloor2 - height)) / 2.0d);
        translateInstance.rotate(d, ((double) width) / 2.0d, ((double) height) / 2.0d);
        BufferedImage bufferedImageCreateTransparent = createTransparent(iFloor, iFloor2);
        Graphics2D graphics2DCreateGraphics = bufferedImageCreateTransparent.createGraphics();
        try {
            graphics2DCreateGraphics.transform(translateInstance);
            if (!z) {
                graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2DCreateGraphics.setPaint(new TexturePaint(bufferedImage, new Rectangle2D.Float(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, bufferedImage.getWidth(), bufferedImage.getHeight())));
                graphics2DCreateGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            } else {
                graphics2DCreateGraphics.drawImage(bufferedImage, 0, 0, (ImageObserver) null);
            }
            return bufferedImageCreateTransparent;
        } finally {
            graphics2DCreateGraphics.dispose();
        }
    }

    public static BufferedImage createScaled(Image image, int i, int i2, int i3) {
        ColorModel colorModel;
        WritableRaster writableRasterCreateCompatibleWritableRaster;
        int type = -1;
        if (image instanceof RenderedImage) {
            colorModel = ((RenderedImage) image).getColorModel();
            if (image instanceof BufferedImage) {
                type = ((BufferedImage) image).getType();
            }
        } else {
            colorModel = new BufferedImageFactory(image).getColorModel();
        }
        BufferedImage bufferedImageCreateResampled = createResampled(image, i, i2, i3);
        if ((type != bufferedImageCreateResampled.getType() && type != -1) || !equals(bufferedImageCreateResampled.getColorModel(), colorModel)) {
            if (image instanceof BufferedImage) {
                writableRasterCreateCompatibleWritableRaster = createCompatibleWritableRaster((BufferedImage) image, colorModel, i, i2);
            } else {
                writableRasterCreateCompatibleWritableRaster = colorModel.createCompatibleWritableRaster(i, i2);
            }
            BufferedImage bufferedImage = new BufferedImage(colorModel, writableRasterCreateCompatibleWritableRaster, colorModel.isAlphaPremultiplied(), (Hashtable) null);
            if ((colorModel instanceof IndexColorModel) && i3 == 4) {
                new DiffusionDither((IndexColorModel) colorModel).filter(bufferedImageCreateResampled, bufferedImage);
            } else {
                drawOnto(bufferedImage, bufferedImageCreateResampled);
            }
            bufferedImageCreateResampled = bufferedImage;
        }
        return bufferedImageCreateResampled;
    }

    private static boolean equals(ColorModel colorModel, ColorModel colorModel2) {
        if (colorModel == colorModel2) {
            return true;
        }
        if (!colorModel.equals(colorModel2)) {
            return false;
        }
        if (colorModel instanceof IndexColorModel) {
            IndexColorModel indexColorModel = (IndexColorModel) colorModel;
            IndexColorModel indexColorModel2 = (IndexColorModel) colorModel2;
            int mapSize = indexColorModel.getMapSize();
            if (mapSize != indexColorModel2.getMapSize()) {
                return false;
            }
            for (int i = 0; i < mapSize; i++) {
                if (indexColorModel.getRGB(i) != indexColorModel2.getRGB(i)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static BufferedImage createResampled(Image image, int i, int i2, int i3) {
        return createResampled(image instanceof BufferedImage ? (BufferedImage) image : toBuffered(image, 6), i, i2, i3);
    }

    public static BufferedImage createResampled(RenderedImage renderedImage, int i, int i2, int i3) {
        BufferedImage buffered;
        if (renderedImage instanceof BufferedImage) {
            buffered = (BufferedImage) renderedImage;
        } else {
            buffered = toBuffered(renderedImage, renderedImage.getColorModel().hasAlpha() ? 6 : 5);
        }
        return createResampled(buffered, i, i2, i3);
    }

    public static BufferedImage createResampled(BufferedImage bufferedImage, int i, int i2, int i3) {
        return new ResampleOp(i, i2, convertAWTHints(i3)).filter(bufferedImage, null);
    }

    private static int convertAWTHints(int i) {
        switch (i) {
            case 2:
            case 8:
                return 1;
            case 4:
                return 13;
            case 16:
                return 2;
            default:
                return 9;
        }
    }

    public static IndexColorModel getIndexColorModel(Image image, int i, int i2) {
        return IndexImage.getIndexColorModel(image, i, i2);
    }

    public static BufferedImage createIndexed(Image image) {
        return IndexImage.getIndexedImage(toBuffered(image), 256, Color.black, 0);
    }

    public static BufferedImage createIndexed(Image image, int i, Color color, int i2) {
        return IndexImage.getIndexedImage(toBuffered(image), i, color, i2);
    }

    public static BufferedImage createIndexed(Image image, IndexColorModel indexColorModel, Color color, int i) {
        return IndexImage.getIndexedImage(toBuffered(image), indexColorModel, color, i);
    }

    public static BufferedImage createIndexed(Image image, Image image2, Color color, int i) {
        return IndexImage.getIndexedImage(toBuffered(image), IndexImage.getIndexColorModel(image2, 255, i), color, i);
    }

    public static BufferedImage sharpen(BufferedImage bufferedImage) {
        return convolve(bufferedImage, SHARPEN_KERNEL, 2);
    }

    public static BufferedImage sharpen(BufferedImage bufferedImage, float f) {
        if (f == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return bufferedImage;
        }
        return convolve(bufferedImage, new Kernel(3, 3, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, -f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -f, (4.0f * f) + 1.0f, -f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -f, ColumnText.GLOBAL_SPACE_CHAR_RATIO}), 2);
    }

    public static BufferedImage blur(BufferedImage bufferedImage) {
        return blur(bufferedImage, 1.5f);
    }

    public static BufferedImage blur(BufferedImage bufferedImage, float f) {
        if (f <= 1.0f) {
            return bufferedImage;
        }
        Kernel kernelMakeKernel = makeKernel(f);
        Kernel kernel = new Kernel(kernelMakeKernel.getHeight(), kernelMakeKernel.getWidth(), kernelMakeKernel.getKernelData((float[]) null));
        return convolve(convolve(addBorder(bufferedImage, kernelMakeKernel.getWidth() / 2, kernel.getHeight() / 2, 2), kernelMakeKernel, 1), kernel, 1).getSubimage(kernelMakeKernel.getWidth() / 2, kernel.getHeight() / 2, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    private static Kernel makeKernel(float f) {
        int iCeil = (int) Math.ceil(f);
        int i = (iCeil * 2) + 1;
        float[] fArr = new float[i];
        float f2 = f / 3.0f;
        float f3 = 2.0f * f2 * f2;
        float fSqrt = (float) Math.sqrt((float) (6.283185307179586d * ((double) f2)));
        float f4 = f * f;
        float f5 = 0.0f;
        int i2 = 0;
        for (int i3 = -iCeil; i3 <= iCeil; i3++) {
            float f6 = i3 * i3;
            if (f6 > f4) {
                fArr[i2] = 0.0f;
            } else {
                fArr[i2] = ((float) Math.exp((-f6) / f3)) / fSqrt;
            }
            f5 += fArr[i2];
            i2++;
        }
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i4;
            fArr[i5] = fArr[i5] / f5;
        }
        return new Kernel(i, 1, fArr);
    }

    public static BufferedImage convolve(BufferedImage bufferedImage, Kernel kernel, int i) {
        BufferedImage bufferedImageAddBorder;
        switch (i) {
            case 2:
            case 3:
                bufferedImageAddBorder = addBorder(bufferedImage, kernel.getWidth() / 2, kernel.getHeight() / 2, i);
                break;
            default:
                bufferedImageAddBorder = bufferedImage;
                break;
        }
        ConvolveOp convolveOp = new ConvolveOp(kernel, i, (RenderingHints) null);
        BufferedImage bufferedImageCreateBuffered = null;
        if (bufferedImageAddBorder.getType() == 5) {
            bufferedImageCreateBuffered = createBuffered(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType(), bufferedImage.getColorModel().getTransparency());
        }
        BufferedImage bufferedImageFilter = convolveOp.filter(bufferedImageAddBorder, bufferedImageCreateBuffered);
        if (bufferedImage != bufferedImageAddBorder) {
            bufferedImageFilter = bufferedImageFilter.getSubimage(kernel.getWidth() / 2, kernel.getHeight() / 2, bufferedImage.getWidth(), bufferedImage.getHeight());
        }
        return bufferedImageFilter;
    }

    private static BufferedImage addBorder(BufferedImage bufferedImage, int i, int i2, int i3) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ColorModel colorModel = bufferedImage.getColorModel();
        BufferedImage bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width + (2 * i), height + (2 * i2)), colorModel.isAlphaPremultiplied(), (Hashtable) null);
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        try {
            graphics2DCreateGraphics.setComposite(AlphaComposite.Src);
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2DCreateGraphics.drawImage(bufferedImage, i, i2, (ImageObserver) null);
            switch (i3) {
                case 2:
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, 0, i + width, i2, 0, 0, width, 1, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, i2, i, height + i2, 0, 0, 1, height, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, i2, (2 * i) + width, height + i2, width - 1, 0, width, height, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, i2 + height, i + width, (2 * i2) + height, 0, height - 1, width, height, (ImageObserver) null);
                    break;
                case 3:
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, (-height) + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, (-height) + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, (-height) + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, (-width) + i, height + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, i, height + i2, (ImageObserver) null);
                    graphics2DCreateGraphics.drawImage(bufferedImage, width + i, height + i2, (ImageObserver) null);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal edge operation " + i3);
            }
            graphics2DCreateGraphics.dispose();
            return bufferedImage2;
        } catch (Throwable th) {
            graphics2DCreateGraphics.dispose();
            throw th;
        }
    }

    public static Image contrast(Image image) {
        return contrast(image, 0.3f);
    }

    public static Image contrast(Image image, float f) {
        if (f == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return image;
        }
        return filter(image, new BrightnessContrastFilter(ColumnText.GLOBAL_SPACE_CHAR_RATIO, f));
    }

    public static Image brightness(Image image, float f) {
        if (f == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return image;
        }
        return filter(image, new BrightnessContrastFilter(f, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
    }

    public static Image grayscale(Image image) {
        return filter(image, new GrayFilter());
    }

    public static Image filter(Image image, ImageFilter imageFilter) {
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), imageFilter));
    }

    public static BufferedImage accelerate(Image image) {
        return accelerate(image, null, DEFAULT_CONFIGURATION);
    }

    public static BufferedImage accelerate(Image image, GraphicsConfiguration graphicsConfiguration) {
        return accelerate(image, null, graphicsConfiguration);
    }

    static BufferedImage accelerate(Image image, Color color, GraphicsConfiguration graphicsConfiguration) {
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            if (bufferedImage.getType() != 0 && equals(bufferedImage.getColorModel(), graphicsConfiguration.getColorModel(bufferedImage.getTransparency()))) {
                return bufferedImage;
            }
        }
        if (image == null) {
            throw new IllegalArgumentException("image == null");
        }
        BufferedImage bufferedImageCreateClear = createClear(getWidth(image), getHeight(image), -1, getTransparency(image), color, graphicsConfiguration);
        drawOnto(bufferedImageCreateClear, image);
        return bufferedImageCreateClear;
    }

    private static int getTransparency(Image image) {
        if (image instanceof BufferedImage) {
            return ((BufferedImage) image).getTransparency();
        }
        return 1;
    }

    public static BufferedImage createTransparent(int i, int i2) {
        return createTransparent(i, i2, -1);
    }

    public static BufferedImage createTransparent(int i, int i2, int i3) {
        BufferedImage bufferedImageCreateBuffered = createBuffered(i, i2, i3, 3);
        Graphics2D graphics2DCreateGraphics = bufferedImageCreateBuffered.createGraphics();
        try {
            graphics2DCreateGraphics.setComposite(AlphaComposite.Clear);
            graphics2DCreateGraphics.fillRect(0, 0, i, i2);
            return bufferedImageCreateBuffered;
        } finally {
            graphics2DCreateGraphics.dispose();
        }
    }

    public static BufferedImage createClear(int i, int i2, Color color) {
        return createClear(i, i2, -1, color);
    }

    public static BufferedImage createClear(int i, int i2, int i3, Color color) {
        return createClear(i, i2, i3, 1, color, DEFAULT_CONFIGURATION);
    }

    static BufferedImage createClear(int i, int i2, int i3, int i4, Color color, GraphicsConfiguration graphicsConfiguration) {
        BufferedImage bufferedImageCreateBuffered = createBuffered(i, i2, i3, color != null ? color.getTransparency() : i4, graphicsConfiguration);
        if (color != null) {
            Graphics2D graphics2DCreateGraphics = bufferedImageCreateBuffered.createGraphics();
            try {
                graphics2DCreateGraphics.setComposite(AlphaComposite.Src);
                graphics2DCreateGraphics.setColor(color);
                graphics2DCreateGraphics.fillRect(0, 0, i, i2);
            } finally {
                graphics2DCreateGraphics.dispose();
            }
        }
        return bufferedImageCreateBuffered;
    }

    private static BufferedImage createBuffered(int i, int i2, int i3, int i4) {
        return createBuffered(i, i2, i3, i4, DEFAULT_CONFIGURATION);
    }

    static BufferedImage createBuffered(int i, int i2, int i3, int i4, GraphicsConfiguration graphicsConfiguration) {
        if (VM_SUPPORTS_ACCELERATION && i3 == -1 && supportsAcceleration(GraphicsEnvironment.getLocalGraphicsEnvironment())) {
            return getConfiguration(graphicsConfiguration).createCompatibleImage(i, i2, i4);
        }
        return new BufferedImage(i, i2, getImageType(i3, i4));
    }

    private static GraphicsConfiguration getConfiguration(GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration != null ? graphicsConfiguration : DEFAULT_CONFIGURATION;
    }

    private static int getImageType(int i, int i2) {
        if (i != -1) {
            return i;
        }
        switch (i2) {
            case 1:
                return 1;
            case 2:
            case 3:
                return 2;
            default:
                throw new IllegalArgumentException("Unknown transparency type: " + i2);
        }
    }

    private static boolean supportsAcceleration(GraphicsEnvironment graphicsEnvironment) {
        try {
            return !graphicsEnvironment.isHeadlessInstance();
        } catch (LinkageError e) {
            VM_SUPPORTS_ACCELERATION = false;
            return false;
        }
    }

    public static int getWidth(Image image) {
        int width = image.getWidth(NULL_COMPONENT);
        if (width < 0) {
            if (!waitForImage(image)) {
                return -1;
            }
            width = image.getWidth(NULL_COMPONENT);
        }
        return width;
    }

    public static int getHeight(Image image) {
        int height = image.getHeight(NULL_COMPONENT);
        if (height < 0) {
            if (!waitForImage(image)) {
                return -1;
            }
            height = image.getHeight(NULL_COMPONENT);
        }
        return height;
    }

    public static boolean waitForImage(Image image) {
        return waitForImages(new Image[]{image}, -1L);
    }

    public static boolean waitForImage(Image image, long j) {
        return waitForImages(new Image[]{image}, j);
    }

    public static boolean waitForImages(Image[] imageArr) {
        return waitForImages(imageArr, -1L);
    }

    public static boolean waitForImages(Image[] imageArr, long j) {
        boolean zWaitForID = true;
        int iIdentityHashCode = imageArr.length == 1 ? System.identityHashCode(imageArr[0]) : System.identityHashCode(imageArr);
        for (Image image : imageArr) {
            sTracker.addImage(image, iIdentityHashCode);
            if (sTracker.checkID(iIdentityHashCode, false)) {
                sTracker.removeImage(image, iIdentityHashCode);
            }
        }
        try {
            if (j < 0) {
                sTracker.waitForID(iIdentityHashCode);
            } else {
                zWaitForID = sTracker.waitForID(iIdentityHashCode, j);
            }
            for (Image image2 : imageArr) {
                sTracker.removeImage(image2, iIdentityHashCode);
            }
        } catch (InterruptedException e) {
            zWaitForID = false;
            for (Image image3 : imageArr) {
                sTracker.removeImage(image3, iIdentityHashCode);
            }
        } catch (Throwable th) {
            for (Image image4 : imageArr) {
                sTracker.removeImage(image4, iIdentityHashCode);
            }
            throw th;
        }
        return zWaitForID && !sTracker.isErrorID(iIdentityHashCode);
    }

    public static boolean hasTransparentPixels(RenderedImage renderedImage, boolean z) {
        if (renderedImage == null) {
            return false;
        }
        ColorModel colorModel = renderedImage.getColorModel();
        if (!colorModel.hasAlpha()) {
            return false;
        }
        if (colorModel.getTransparency() != 2 && colorModel.getTransparency() != 3) {
            return false;
        }
        Object dataElements = null;
        for (int minTileY = renderedImage.getMinTileY(); minTileY < renderedImage.getNumYTiles(); minTileY++) {
            for (int minTileX = renderedImage.getMinTileX(); minTileX < renderedImage.getNumXTiles(); minTileX++) {
                Raster tile = renderedImage.getTile(minTileX, minTileY);
                int iMax = z ? Math.max(tile.getWidth() / 10, 1) : 1;
                int iMax2 = z ? Math.max(tile.getHeight() / 10, 1) : 1;
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < tile.getHeight()) {
                        int i3 = 0;
                        while (true) {
                            int i4 = i3;
                            if (i4 < tile.getWidth()) {
                                dataElements = tile.getDataElements(i4, i2, dataElements);
                                if (colorModel.getAlpha(dataElements) == 255) {
                                    i3 = i4 + iMax;
                                } else {
                                    return true;
                                }
                            }
                        }
                        i = i2 + iMax2;
                    }
                }
            }
        }
        return false;
    }

    public static Color createTranslucent(Color color, int i) {
        return new Color(((i & 255) << 24) | (color.getRGB() & 16777215), true);
    }

    static int blend(int i, int i2) {
        return (((i ^ i2) & (-16843010)) >> 1) + (i & i2);
    }

    public static Color blend(Color color, Color color2) {
        return new Color(blend(color.getRGB(), color2.getRGB()), true);
    }

    public static Color blend(Color color, Color color2, float f) {
        float f2 = 1.0f - f;
        return new Color(clamp((color.getRed() * f2) + (color2.getRed() * f)), clamp((color.getGreen() * f2) + (color2.getGreen() * f)), clamp((color.getBlue() * f2) + (color2.getBlue() * f)), clamp((color.getAlpha() * f2) + (color2.getAlpha() * f)));
    }

    private static int clamp(float f) {
        return (int) f;
    }
}
