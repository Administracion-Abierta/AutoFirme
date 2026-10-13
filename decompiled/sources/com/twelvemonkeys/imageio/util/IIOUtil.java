package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TreeSet;
import javax.imageio.IIOParam;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIOServiceProvider;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/IIOUtil.class */
public final class IIOUtil {
    private IIOUtil() {
    }

    public static InputStream createStreamAdapter(ImageInputStream imageInputStream) {
        return new BufferedInputStream(new IIOInputStreamAdapter(imageInputStream));
    }

    public static InputStream createStreamAdapter(ImageInputStream imageInputStream, long j) {
        return new BufferedInputStream(new IIOInputStreamAdapter(imageInputStream, j));
    }

    public static OutputStream createStreamAdapter(ImageOutputStream imageOutputStream) {
        return new BufferedOutputStream(new IIOOutputStreamAdapter(imageOutputStream));
    }

    public static Image fakeSubsampling(Image image, IIOParam iIOParam) {
        if (image == null) {
            return null;
        }
        if (iIOParam != null) {
            int sourceXSubsampling = iIOParam.getSourceXSubsampling();
            int sourceYSubsampling = iIOParam.getSourceYSubsampling();
            if (sourceXSubsampling > 1 || sourceYSubsampling > 1) {
                return image.getScaledInstance(((ImageUtil.getWidth(image) + sourceXSubsampling) - 1) / sourceXSubsampling, ((ImageUtil.getHeight(image) + sourceYSubsampling) - 1) / sourceYSubsampling, 2);
            }
        }
        return image;
    }

    public static Rectangle getSourceRegion(IIOParam iIOParam, int i, int i2) {
        Rectangle rectangle = new Rectangle(i, i2);
        if (iIOParam != null) {
            Rectangle sourceRegion = iIOParam.getSourceRegion();
            if (sourceRegion != null) {
                rectangle = rectangle.intersection(sourceRegion);
            }
            int subsamplingXOffset = iIOParam.getSubsamplingXOffset();
            int subsamplingYOffset = iIOParam.getSubsamplingYOffset();
            rectangle.x += subsamplingXOffset;
            rectangle.y += subsamplingYOffset;
            rectangle.width -= subsamplingXOffset;
            rectangle.height -= subsamplingYOffset;
        }
        return rectangle;
    }

    public static BufferedImage fakeAOI(BufferedImage bufferedImage, Rectangle rectangle) {
        if (bufferedImage == null) {
            return null;
        }
        if (rectangle != null && (rectangle.x != 0 || rectangle.y != 0 || rectangle.width != bufferedImage.getWidth() || rectangle.height != bufferedImage.getHeight())) {
            return bufferedImage.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        return bufferedImage;
    }

    public static <T> void deregisterProvider(ServiceRegistry serviceRegistry, IIOServiceProvider iIOServiceProvider, Class<T> cls) {
        serviceRegistry.deregisterServiceProvider(cls.cast(iIOServiceProvider), cls);
    }

    public static <T> T lookupProviderByName(ServiceRegistry serviceRegistry, String str, Class<T> cls) {
        Iterator serviceProviders = serviceRegistry.getServiceProviders(cls, true);
        while (serviceProviders.hasNext()) {
            T t = (T) serviceProviders.next();
            if (t.getClass().getName().equals(str)) {
                return t;
            }
        }
        return null;
    }

    public static String[] getNormalizedReaderFormatNames() {
        return normalizeNames(ImageIO.getReaderFormatNames());
    }

    public static String[] getNormalizedWriterFormatNames() {
        return normalizeNames(ImageIO.getWriterFormatNames());
    }

    private static String[] normalizeNames(String[] strArr) {
        TreeSet treeSet = new TreeSet();
        for (String str : strArr) {
            treeSet.add(str.toUpperCase());
        }
        return (String[]) treeSet.toArray(new String[0]);
    }

    public static void subsampleRow(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4, int i5, int i6) {
        if (i6 == 1) {
            return;
        }
        Validate.isTrue(i6 > 1, "samplePeriod must be > 1");
        Validate.isTrue(i5 > 0 && i5 <= 8 && (i5 == 1 || i5 % 2 == 0), "bitsPerSample must be > 0 and <= 8 and a power of 2");
        Validate.isTrue(i4 > 0, "samplesPerPixel must be > 0");
        Validate.isTrue(i4 * i5 <= 8 || (i4 * i5) % 8 == 0, "samplesPerPixel * bitsPerSample must be < 8 or a multiple of 8 ");
        if ((i5 * i4) % 8 == 0) {
            int i7 = (i5 * i4) / 8;
            int i8 = 0;
            while (true) {
                int i9 = i8;
                if (i9 < i2 * i7) {
                    System.arraycopy(bArr, i + i9, bArr2, i3 + (i9 / i6), i7);
                    i8 = i9 + (i6 * i7);
                } else {
                    return;
                }
            }
        } else {
            int i10 = i5 * i4;
            int i11 = (1 << i10) - 1;
            int i12 = 0;
            while (true) {
                int i13 = i12;
                if (i13 < i2) {
                    int i14 = ((i3 + (i13 / i6)) * i10) / 8;
                    int i15 = ((i + i13) * i10) / 8;
                    int i16 = (8 - i10) - ((i13 * i10) % 8);
                    int i17 = i11 << i16;
                    int i18 = (8 - i10) - (((i13 * i10) / i6) % 8);
                    bArr2[i14] = (byte) ((bArr2[i14] & ((i11 << i18) ^ (-1))) | (((bArr[i15] & i17) >> i16) << i18));
                    i12 = i13 + i6;
                } else {
                    return;
                }
            }
        }
    }

    public static void subsampleRow(short[] sArr, int i, int i2, short[] sArr2, int i3, int i4, int i5, int i6) {
        if (i6 == 1) {
            return;
        }
        Validate.isTrue(i6 > 1, "samplePeriod must be > 1");
        Validate.isTrue(i5 > 0 && i5 <= 16 && (i5 == 1 || i5 % 2 == 0), "bitsPerSample must be > 0 and <= 16 and a power of 2");
        Validate.isTrue(i4 > 0, "samplesPerPixel must be > 0");
        Validate.isTrue(i4 * i5 <= 16 || (i4 * i5) % 16 == 0, "samplesPerPixel * bitsPerSample must be < 16 or a multiple of 16");
        int i7 = (i5 * i4) / 16;
        int i8 = 0;
        while (true) {
            int i9 = i8;
            if (i9 < i2 * i7) {
                System.arraycopy(sArr, i + i9, sArr2, i3 + (i9 / i6), i7);
                i8 = i9 + (i6 * i7);
            } else {
                return;
            }
        }
    }

    public static void subsampleRow(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4, int i5, int i6) {
        if (i6 == 1) {
            return;
        }
        Validate.isTrue(i6 > 1, "samplePeriod must be > 1");
        Validate.isTrue(i5 > 0 && i5 <= 32 && (i5 == 1 || i5 % 2 == 0), "bitsPerSample must be > 0 and <= 32 and a power of 2");
        Validate.isTrue(i4 > 0, "samplesPerPixel must be > 0");
        Validate.isTrue(i4 * i5 <= 32 || (i4 * i5) % 32 == 0, "samplesPerPixel * bitsPerSample must be < 32 or a multiple of 32");
        int i7 = (i5 * i4) / 32;
        int i8 = 0;
        while (true) {
            int i9 = i8;
            if (i9 < i2 * i7) {
                System.arraycopy(iArr, i + i9, iArr2, i3 + (i9 / i6), i7);
                i8 = i9 + (i6 * i7);
            } else {
                return;
            }
        }
    }

    public static void subsampleRow(float[] fArr, int i, int i2, float[] fArr2, int i3, int i4, int i5, int i6) {
        Validate.isTrue(i6 > 1, "samplePeriod must be > 1");
        Validate.isTrue(i5 > 0 && i5 <= 32 && (i5 == 1 || i5 % 2 == 0), "bitsPerSample must be > 0 and <= 32 and a power of 2");
        Validate.isTrue(i4 > 0, "samplesPerPixel must be > 0");
        Validate.isTrue(i4 * i5 <= 32 || (i4 * i5) % 32 == 0, "samplesPerPixel * bitsPerSample must be < 32 or a multiple of 32");
        int i7 = (i5 * i4) / 32;
        int i8 = 0;
        while (true) {
            int i9 = i8;
            if (i9 < i2 * i7) {
                System.arraycopy(fArr, i + i9, fArr2, i3 + (i9 / i6), i7);
                i8 = i9 + (i6 * i7);
            } else {
                return;
            }
        }
    }

    public static void subsampleRow(double[] dArr, int i, int i2, double[] dArr2, int i3, int i4, int i5, int i6) {
        Validate.isTrue(i6 > 1, "samplePeriod must be > 1");
        Validate.isTrue(i5 > 0 && i5 <= 64 && (i5 == 1 || i5 % 2 == 0), "bitsPerSample must be > 0 and <= 64 and a power of 2");
        Validate.isTrue(i4 > 0, "samplesPerPixel must be > 0");
        Validate.isTrue(i4 * i5 <= 64 || (i4 * i5) % 64 == 0, "samplesPerPixel * bitsPerSample must be < 64 or a multiple of 64");
        int i7 = (i5 * i4) / 64;
        int i8 = 0;
        while (true) {
            int i9 = i8;
            if (i9 < i2 * i7) {
                System.arraycopy(dArr, i + i9, dArr2, i3 + (i9 / i6), i7);
                i8 = i9 + (i6 * i7);
            } else {
                return;
            }
        }
    }
}
