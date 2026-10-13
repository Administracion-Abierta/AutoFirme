package com.twelvemonkeys.image;

import com.twelvemonkeys.lang.SystemUtil;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import magick.MagickImage;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/MagickAccelerator.class */
final class MagickAccelerator {
    private static final int RESAMPLE_OP = 0;
    private static final boolean DEBUG = Magick.DEBUG;
    private static final boolean USE_MAGICK = useMagick();
    private static Class[] nativeOp = new Class[1];

    MagickAccelerator() {
    }

    static {
        try {
            nativeOp[0] = Class.forName("com.twelvemonkeys.image.ResampleOp");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find class: " + e);
        }
    }

    private static boolean useMagick() {
        try {
            boolean zIsClassAvailable = SystemUtil.isClassAvailable("magick.MagickImage");
            if (DEBUG && !zIsClassAvailable) {
                System.err.print("ImageMagick bindings not available.");
            }
            boolean z = zIsClassAvailable && !"FALSE".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.image.accel"));
            if (DEBUG) {
                System.err.println(z ? "Will use ImageMagick bindings to accelerate image resampling operations." : "Will not use ImageMagick to accelerate image resampling operations.");
            }
            return z;
        } catch (Throwable th) {
            System.err.println("Could not enable ImageMagick bindings: " + th);
            return false;
        }
    }

    private static int getNativeOpIndex(Class cls) {
        for (int i = 0; i < nativeOp.length; i++) {
            if (cls == nativeOp[i]) {
                return i;
            }
        }
        return -1;
    }

    public static BufferedImage filter(BufferedImageOp bufferedImageOp, BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (!USE_MAGICK) {
            return null;
        }
        BufferedImage bufferedImageResampleMagick = null;
        switch (getNativeOpIndex(bufferedImageOp.getClass())) {
            case 0:
                ResampleOp resampleOp = (ResampleOp) bufferedImageOp;
                bufferedImageResampleMagick = resampleMagick(bufferedImage, resampleOp.width, resampleOp.height, resampleOp.filterType);
                if (bufferedImage2 != null) {
                    ImageUtil.drawOnto(bufferedImage2, bufferedImageResampleMagick);
                    bufferedImageResampleMagick = bufferedImage2;
                }
                break;
        }
        return bufferedImageResampleMagick;
    }

    private static BufferedImage resampleMagick(BufferedImage bufferedImage, int i, int i2, int i3) {
        MagickImage magick = null;
        MagickImage magickImageZoomImage = null;
        try {
            try {
                magick = MagickUtil.toMagick(bufferedImage);
                long jCurrentTimeMillis = 0;
                if (DEBUG) {
                    jCurrentTimeMillis = System.currentTimeMillis();
                }
                magick.setFilter(i3);
                magickImageZoomImage = magick.zoomImage(i, i2);
                if (DEBUG) {
                    System.out.println("Filtered: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                }
                BufferedImage buffered = MagickUtil.toBuffered(magickImageZoomImage);
                if (magick != null) {
                    magick.destroyImages();
                }
                if (magickImageZoomImage != null) {
                    magickImageZoomImage.destroyImages();
                }
                return buffered;
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                throw new ImageConversionException(e.getMessage(), e);
            }
        } catch (Throwable th) {
            if (magick != null) {
                magick.destroyImages();
            }
            if (magickImageZoomImage != null) {
                magickImageZoomImage.destroyImages();
            }
            throw th;
        }
    }
}
