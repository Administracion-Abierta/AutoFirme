package com.twelvemonkeys.image;

import com.sun.jna.platform.win32.WinError;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp.class */
public class ResampleOp implements BufferedImageOp {
    public static final int FILTER_UNDEFINED = 0;
    public static final int FILTER_POINT = 1;
    public static final int FILTER_BOX = 2;
    public static final int FILTER_TRIANGLE = 3;
    public static final int FILTER_HERMITE = 4;
    public static final int FILTER_HANNING = 5;
    public static final int FILTER_HAMMING = 6;
    public static final int FILTER_BLACKMAN = 7;
    public static final int FILTER_GAUSSIAN = 8;
    public static final int FILTER_QUADRATIC = 9;
    public static final int FILTER_CUBIC = 10;
    public static final int FILTER_CATROM = 11;
    public static final int FILTER_MITCHELL = 12;
    public static final int FILTER_LANCZOS = 13;
    public static final int FILTER_BLACKMAN_BESSEL = 14;
    public static final int FILTER_BLACKMAN_SINC = 15;
    public static final RenderingHints.Key KEY_RESAMPLE_INTERPOLATION = new Key("ResampleInterpolation");
    public static final Object VALUE_INTERPOLATION_POINT = new Value(KEY_RESAMPLE_INTERPOLATION, "Point", 1);
    public static final Object VALUE_INTERPOLATION_BOX = new Value(KEY_RESAMPLE_INTERPOLATION, "Box", 2);
    public static final Object VALUE_INTERPOLATION_TRIANGLE = new Value(KEY_RESAMPLE_INTERPOLATION, "Triangle", 3);
    public static final Object VALUE_INTERPOLATION_HERMITE = new Value(KEY_RESAMPLE_INTERPOLATION, "Hermite", 4);
    public static final Object VALUE_INTERPOLATION_HANNING = new Value(KEY_RESAMPLE_INTERPOLATION, "Hanning", 5);
    public static final Object VALUE_INTERPOLATION_HAMMING = new Value(KEY_RESAMPLE_INTERPOLATION, "Hamming", 6);
    public static final Object VALUE_INTERPOLATION_BLACKMAN = new Value(KEY_RESAMPLE_INTERPOLATION, "Blackman", 7);
    public static final Object VALUE_INTERPOLATION_GAUSSIAN = new Value(KEY_RESAMPLE_INTERPOLATION, "Gaussian", 8);
    public static final Object VALUE_INTERPOLATION_QUADRATIC = new Value(KEY_RESAMPLE_INTERPOLATION, "Quadratic", 9);
    public static final Object VALUE_INTERPOLATION_CUBIC = new Value(KEY_RESAMPLE_INTERPOLATION, "Cubic", 10);
    public static final Object VALUE_INTERPOLATION_CATROM = new Value(KEY_RESAMPLE_INTERPOLATION, "Catrom", 11);
    public static final Object VALUE_INTERPOLATION_MITCHELL = new Value(KEY_RESAMPLE_INTERPOLATION, "Mitchell", 12);
    public static final Object VALUE_INTERPOLATION_LANCZOS = new Value(KEY_RESAMPLE_INTERPOLATION, "Lanczos", 13);
    public static final Object VALUE_INTERPOLATION_BLACKMAN_BESSEL = new Value(KEY_RESAMPLE_INTERPOLATION, "Blackman-Bessel", 14);
    public static final Object VALUE_INTERPOLATION_BLACKMAN_SINC = new Value(KEY_RESAMPLE_INTERPOLATION, "Blackman-Sinc", 15);
    int width;
    int height;
    int filterType;
    private static final double B = 0.3333333333333333d;
    private static final double C = 0.3333333333333333d;
    private static final double P0 = 0.8888888888888888d;
    private static final double P2 = -2.0d;
    private static final double P3 = 1.1666666666666667d;
    private static final double Q0 = 1.7777777777777777d;
    private static final double Q1 = -3.3333333333333335d;
    private static final double Q2 = 2.0d;
    private static final double Q3 = -0.3888888888888889d;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$InterpolationFilter.class */
    interface InterpolationFilter {
        double filter(double d);

        double support();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$Key.class */
    static class Key extends RenderingHints.Key {
        static int sIndex = WinError.WSABASEERR;
        private final String name;

        /* JADX WARN: Illegal instructions before constructor call */
        public Key(String str) {
            int i = sIndex;
            sIndex = i + 1;
            super(i);
            this.name = str;
        }

        public boolean isCompatibleValue(Object obj) {
            return (obj instanceof Value) && ((Value) obj).isCompatibleKey(this);
        }

        public String toString() {
            return this.name;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$Value.class */
    static final class Value {
        private final RenderingHints.Key key;
        private final String name;
        private final int type;

        public Value(RenderingHints.Key key, String str, int i) {
            this.key = key;
            this.name = str;
            this.type = ResampleOp.validateFilterType(i);
        }

        public boolean isCompatibleKey(Key key) {
            return key == this.key;
        }

        public int getFilterType() {
            return this.type;
        }

        public String toString() {
            return this.name;
        }
    }

    public ResampleOp(int i, int i2) {
        this(i, i2, 0);
    }

    public ResampleOp(int i, int i2, RenderingHints renderingHints) {
        this(i, i2, getFilterType(renderingHints));
    }

    public ResampleOp(int i, int i2, int i3) {
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        this.width = i;
        this.height = i2;
        this.filterType = validateFilterType(i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int validateFilterType(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return i;
            default:
                throw new IllegalArgumentException("Unknown filter type: " + i);
        }
    }

    private static int getFilterType(RenderingHints renderingHints) {
        if (renderingHints == null) {
            return 0;
        }
        if (renderingHints.containsKey(KEY_RESAMPLE_INTERPOLATION)) {
            Object obj = renderingHints.get(KEY_RESAMPLE_INTERPOLATION);
            if (!KEY_RESAMPLE_INTERPOLATION.isCompatibleValue(obj)) {
                throw new IllegalArgumentException(obj + " incompatible with key " + KEY_RESAMPLE_INTERPOLATION);
            }
            if (obj != null) {
                return ((Value) obj).getFilterType();
            }
            return 0;
        }
        if (!RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(renderingHints.get(RenderingHints.KEY_INTERPOLATION))) {
            if (!renderingHints.containsKey(RenderingHints.KEY_INTERPOLATION) && (RenderingHints.VALUE_RENDER_SPEED.equals(renderingHints.get(RenderingHints.KEY_RENDERING)) || RenderingHints.VALUE_COLOR_RENDER_SPEED.equals(renderingHints.get(RenderingHints.KEY_COLOR_RENDERING)))) {
                return 1;
            }
            if (RenderingHints.VALUE_INTERPOLATION_BILINEAR.equals(renderingHints.get(RenderingHints.KEY_INTERPOLATION))) {
                return 3;
            }
            if (RenderingHints.VALUE_INTERPOLATION_BICUBIC.equals(renderingHints.get(RenderingHints.KEY_INTERPOLATION))) {
                return 9;
            }
            if (RenderingHints.VALUE_RENDER_QUALITY.equals(renderingHints.get(RenderingHints.KEY_RENDERING)) || RenderingHints.VALUE_COLOR_RENDER_QUALITY.equals(renderingHints.get(RenderingHints.KEY_COLOR_RENDERING))) {
                return 12;
            }
            return 0;
        }
        return 1;
    }

    /* JADX WARN: Code duplicated, block: B:45:0x010d  */
    public final BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        BufferedImage buffered;
        if (bufferedImage == null) {
            throw new NullPointerException("Input == null");
        }
        if (bufferedImage == bufferedImage2) {
            throw new IllegalArgumentException("Output image cannot be the same as the input image");
        }
        switch (this.filterType) {
            case 1:
                if (bufferedImage.getType() != 0) {
                    return fastResample(bufferedImage, bufferedImage2, this.width, this.height, 1);
                }
            case 3:
                if (bufferedImage.getType() != 0) {
                    return fastResample(bufferedImage, bufferedImage2, this.width, this.height, 2);
                }
            case 9:
                if (bufferedImage.getType() != 0) {
                    return fastResample(bufferedImage, bufferedImage2, this.width, this.height, 3);
                }
            default:
                InterpolationFilter interpolationFilterCreateFilter = createFilter(this.filterType);
                if (Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()) <= interpolationFilterCreateFilter.support() || Math.min(this.width, this.height) <= interpolationFilterCreateFilter.support()) {
                    return fastResample(bufferedImage, bufferedImage2, this.width, this.height, 2);
                }
                BufferedImage bufferedImageFilter = MagickAccelerator.filter(this, bufferedImage, bufferedImage2);
                if (bufferedImageFilter != null) {
                    return bufferedImageFilter;
                }
                if (this.filterType == 1 || this.filterType == 2) {
                    buffered = bufferedImage;
                } else {
                    ColorModel colorModel = bufferedImage.getColorModel();
                    if (colorModel instanceof IndexColorModel) {
                        buffered = ImageUtil.toBuffered(bufferedImage, colorModel.hasAlpha() ? 6 : 5);
                    } else {
                        buffered = bufferedImage;
                    }
                }
                BufferedImage bufferedImageCreateCompatibleDestImage = (bufferedImage2 == null || buffered.getType() == 0) ? createCompatibleDestImage(buffered, null) : ImageUtil.toBuffered(bufferedImage2, buffered.getType());
                resample(buffered, bufferedImageCreateCompatibleDestImage, interpolationFilterCreateFilter);
                if (bufferedImage2 != null && bufferedImage2 != bufferedImageCreateCompatibleDestImage) {
                    ImageUtil.drawOnto(bufferedImage2, bufferedImageCreateCompatibleDestImage);
                    bufferedImageCreateCompatibleDestImage = bufferedImage2;
                }
                return bufferedImageCreateCompatibleDestImage;
        }
    }

    private static BufferedImage fastResample(BufferedImage bufferedImage, BufferedImage bufferedImage2, int i, int i2, int i3) {
        BufferedImage bufferedImageFilter = bufferedImage;
        if (i3 > 1 && (i < bufferedImage.getWidth() || i2 < bufferedImage.getHeight())) {
            int i4 = i;
            int i5 = i2;
            while (i4 < bufferedImage.getWidth() / 2) {
                i4 *= 2;
            }
            while (i5 < bufferedImage.getHeight() / 2) {
                i5 *= 2;
            }
            bufferedImageFilter = new AffineTransformOp(AffineTransform.getScaleInstance(((double) i4) / ((double) bufferedImage.getWidth()), ((double) i5) / ((double) bufferedImage.getHeight())), 2).filter(bufferedImageFilter, (BufferedImage) null);
        }
        AffineTransformOp affineTransformOp = null;
        double width = ((double) i) / ((double) bufferedImageFilter.getWidth());
        double height = ((double) i2) / ((double) bufferedImageFilter.getHeight());
        if (i3 > 1) {
            while (true) {
                if (width >= 0.5d && height >= 0.5d) {
                    break;
                }
                if (width >= 0.5d) {
                    affineTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(1.0d, 0.5d), 2);
                    height *= Q2;
                } else if (height >= 0.5d) {
                    affineTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(0.5d, 1.0d), 2);
                    width *= Q2;
                } else {
                    width *= Q2;
                    height *= Q2;
                }
                if (affineTransformOp == null) {
                    affineTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(0.5d, 0.5d), 2);
                }
                bufferedImageFilter = affineTransformOp.filter(bufferedImageFilter, (BufferedImage) null);
            }
        }
        return new AffineTransformOp(AffineTransform.getScaleInstance(width, height), i3).filter(bufferedImageFilter, bufferedImage2);
    }

    public int getFilterType() {
        return this.filterType;
    }

    private static InterpolationFilter createFilter(int i) {
        if (i == 0) {
            i = 13;
        }
        switch (i) {
            case 1:
                return new PointFilter();
            case 2:
                return new BoxFilter();
            case 3:
                return new TriangleFilter();
            case 4:
                return new HermiteFilter();
            case 5:
                return new HanningFilter();
            case 6:
                return new HammingFilter();
            case 7:
                return new BlacmanFilter();
            case 8:
                return new GaussianFilter();
            case 9:
                return new QuadraticFilter();
            case 10:
                return new CubicFilter();
            case 11:
                return new CatromFilter();
            case 12:
                return new MitchellFilter();
            case 13:
                return new LanczosFilter();
            case 14:
                return new BlackmanBesselFilter();
            case 15:
                return new BlackmanSincFilter();
            default:
                throw new IllegalStateException("Unknown filter type: " + i);
        }
    }

    public final BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        if (bufferedImage == null) {
            throw new NullPointerException("pInput == null");
        }
        ColorModel colorModel2 = colorModel != null ? colorModel : bufferedImage.getColorModel();
        return new BufferedImage(colorModel2, ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel2, this.width, this.height), colorModel2.isAlphaPremultiplied(), (Hashtable) null);
    }

    public RenderingHints getRenderingHints() {
        Object obj;
        switch (this.filterType) {
            case 0:
                return null;
            case 1:
                obj = VALUE_INTERPOLATION_POINT;
                break;
            case 2:
                obj = VALUE_INTERPOLATION_BOX;
                break;
            case 3:
                obj = VALUE_INTERPOLATION_TRIANGLE;
                break;
            case 4:
                obj = VALUE_INTERPOLATION_HERMITE;
                break;
            case 5:
                obj = VALUE_INTERPOLATION_HANNING;
                break;
            case 6:
                obj = VALUE_INTERPOLATION_HAMMING;
                break;
            case 7:
                obj = VALUE_INTERPOLATION_BLACKMAN;
                break;
            case 8:
                obj = VALUE_INTERPOLATION_GAUSSIAN;
                break;
            case 9:
                obj = VALUE_INTERPOLATION_QUADRATIC;
                break;
            case 10:
                obj = VALUE_INTERPOLATION_CUBIC;
                break;
            case 11:
                obj = VALUE_INTERPOLATION_CATROM;
                break;
            case 12:
                obj = VALUE_INTERPOLATION_MITCHELL;
                break;
            case 13:
                obj = VALUE_INTERPOLATION_LANCZOS;
                break;
            case 14:
                obj = VALUE_INTERPOLATION_BLACKMAN_BESSEL;
                break;
            case 15:
                obj = VALUE_INTERPOLATION_BLACKMAN_SINC;
                break;
            default:
                throw new IllegalStateException("Unknown filter type: " + this.filterType);
        }
        return new RenderingHints(KEY_RESAMPLE_INTERPOLATION, obj);
    }

    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return new Rectangle(this.width, this.height);
    }

    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            if (point2D instanceof Point2D.Double) {
                point2D2 = new Point2D.Double();
            } else {
                point2D2 = new Point2D.Float();
            }
            point2D2.setLocation(point2D);
        }
        return point2D2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$HermiteFilter.class */
    static class HermiteFilter implements InterpolationFilter {
        HermiteFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < 0.0d) {
                d = -d;
            }
            if (d < 1.0d) {
                return (((ResampleOp.Q2 * d) - 3.0d) * d * d) + 1.0d;
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$PointFilter.class */
    static class PointFilter extends BoxFilter {
        public PointFilter() {
            super(0.0d);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$BoxFilter.class */
    static class BoxFilter implements InterpolationFilter {
        private final double mSupport;

        public BoxFilter() {
            this.mSupport = 0.5d;
        }

        protected BoxFilter(double d) {
            this.mSupport = d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d >= -0.5d && d < 0.5d) {
                return 1.0d;
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return this.mSupport;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$TriangleFilter.class */
    static class TriangleFilter implements InterpolationFilter {
        TriangleFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < 0.0d) {
                d = -d;
            }
            if (d < 1.0d) {
                return 1.0d - d;
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$QuadraticFilter.class */
    static class QuadraticFilter implements InterpolationFilter {
        QuadraticFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < 0.0d) {
                d = -d;
            }
            if (d < 0.5d) {
                return 0.75d - (d * d);
            }
            if (d < 1.5d) {
                double d2 = d - 1.5d;
                return 0.5d * d2 * d2;
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.5d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$CubicFilter.class */
    static class CubicFilter implements InterpolationFilter {
        CubicFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < 0.0d) {
                d = -d;
            }
            if (d < 1.0d) {
                double d2 = d * d;
                return (((0.5d * d2) * d) - d2) + 0.6666666666666666d;
            }
            if (d < ResampleOp.Q2) {
                double d3 = ResampleOp.Q2 - d;
                return 0.16666666666666666d * d3 * d3 * d3;
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return ResampleOp.Q2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double sinc(double d) {
        double d2 = d * 3.141592653589793d;
        if (d2 != 0.0d) {
            return Math.sin(d2) / d2;
        }
        return 1.0d;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$LanczosFilter.class */
    static class LanczosFilter implements InterpolationFilter {
        LanczosFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < 0.0d) {
                d = -d;
            }
            if (d < 3.0d) {
                return ResampleOp.sinc(d) * ResampleOp.sinc(d / 3.0d);
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 3.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$MitchellFilter.class */
    static class MitchellFilter implements InterpolationFilter {
        MitchellFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < -2.0d) {
                return 0.0d;
            }
            if (d < -1.0d) {
                return ResampleOp.Q0 - (d * (ResampleOp.Q1 - (d * (ResampleOp.Q2 - (d * ResampleOp.Q3)))));
            }
            if (d < 0.0d) {
                return ResampleOp.P0 + (d * d * ((-2.0d) - (d * ResampleOp.P3)));
            }
            if (d < 1.0d) {
                return ResampleOp.P0 + (d * d * ((-2.0d) + (d * ResampleOp.P3)));
            }
            if (d < ResampleOp.Q2) {
                return ResampleOp.Q0 + (d * (ResampleOp.Q1 + (d * (ResampleOp.Q2 + (d * ResampleOp.Q3)))));
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return ResampleOp.Q2;
        }
    }

    private static double j1(double d) {
        double[] dArr = {5.811993540016061E20d, -6.672106568924916E19d, 2.3164335806340024E18d, -3.588817569910106E16d, 2.9087952638347756E14d, -1.3229834803321265E12d, 3.4132341823017006E9d, -4695753.530642996d, 2701.1227108923235d};
        double[] dArr2 = {1.1623987080032122E21d, 1.185770712190321E19d, 6.092061398917522E16d, 2.0816612213076075E14d, 5.2437102621676495E11d, 1.013863514358674E9d, 1501793.5949985855d, 1606.9315734814877d, 1.0d};
        double d2 = dArr[8];
        double d3 = dArr2[8];
        for (int i = 7; i >= 0; i--) {
            d2 = (d2 * d * d) + dArr[i];
            d3 = (d3 * d * d) + dArr2[i];
        }
        return d2 / d3;
    }

    private static double p1(double d) {
        double[] dArr = {35224.66491336798d, 62758.84524716128d, 31353.963110915956d, 4985.4832060594335d, 211.15291828539623d, 1.2571716929145342d};
        double[] dArr2 = {35224.66491336798d, 62694.34695935605d, 31240.406381904104d, 4930.396490181089d, 203.07751891347593d, 1.0d};
        double d2 = dArr[5];
        double d3 = dArr2[5];
        for (int i = 4; i >= 0; i--) {
            d2 = (d2 * (8.0d / d) * (8.0d / d)) + dArr[i];
            d3 = (d3 * (8.0d / d) * (8.0d / d)) + dArr2[i];
        }
        return d2 / d3;
    }

    private static double q1(double d) {
        double[] dArr = {351.17519143035526d, 721.0391804904475d, 425.98730116544425d, 83.18989576738508d, 4.568171629551227d, 0.03532840052740124d};
        double[] dArr2 = {7491.737417180912d, 15414.177339265098d, 9152.231701516992d, 1811.1867005523513d, 103.81875854621337d, 1.0d};
        double d2 = dArr[5];
        double d3 = dArr2[5];
        for (int i = 4; i >= 0; i--) {
            d2 = (d2 * (8.0d / d) * (8.0d / d)) + dArr[i];
            d3 = (d3 * (8.0d / d) * (8.0d / d)) + dArr2[i];
        }
        return d2 / d3;
    }

    static double besselOrderOne(double d) {
        if (d == 0.0d) {
            return 0.0d;
        }
        if (d < 0.0d) {
            d = -d;
        }
        if (d < 8.0d) {
            return d * j1(d);
        }
        double dSqrt = Math.sqrt(Q2 / (3.141592653589793d * d)) * ((p1(d) * ((1.0d / Math.sqrt(Q2)) * (Math.sin(d) - Math.cos(d)))) - (((8.0d / d) * q1(d)) * (((-1.0d) / Math.sqrt(Q2)) * (Math.sin(d) + Math.cos(d)))));
        if (d < 0.0d) {
            dSqrt = -dSqrt;
        }
        return dSqrt;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double bessel(double d) {
        if (d == 0.0d) {
            return 0.7853981633974483d;
        }
        return besselOrderOne(3.141592653589793d * d) / (Q2 * d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double blackman(double d) {
        return 0.42d + (0.5d * Math.cos(3.141592653589793d * d)) + (0.08d * Math.cos(6.283185307179586d * d));
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$BlacmanFilter.class */
    static class BlacmanFilter implements InterpolationFilter {
        BlacmanFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            return ResampleOp.blackman(d);
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$CatromFilter.class */
    static class CatromFilter implements InterpolationFilter {
        CatromFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            if (d < 0.0d) {
                d = -d;
            }
            if (d < 1.0d) {
                return 0.5d * (ResampleOp.Q2 + (d * d * ((-5.0d) + (d * 3.0d))));
            }
            if (d < ResampleOp.Q2) {
                return 0.5d * (4.0d + (d * ((-8.0d) + (d * (5.0d - d)))));
            }
            return 0.0d;
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return ResampleOp.Q2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$GaussianFilter.class */
    static class GaussianFilter implements InterpolationFilter {
        GaussianFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            return Math.exp((-2.0d) * d * d) * Math.sqrt(0.6366197723675814d);
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.25d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$HanningFilter.class */
    static class HanningFilter implements InterpolationFilter {
        HanningFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            return 0.5d + (0.5d * Math.cos(3.141592653589793d * d));
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$HammingFilter.class */
    static class HammingFilter implements InterpolationFilter {
        HammingFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            return 0.54d + (0.46d * Math.cos(3.141592653589793d * d));
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 1.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$BlackmanBesselFilter.class */
    static class BlackmanBesselFilter implements InterpolationFilter {
        BlackmanBesselFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            return ResampleOp.blackman(d / support()) * ResampleOp.bessel(d);
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 3.2383d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$BlackmanSincFilter.class */
    static class BlackmanSincFilter implements InterpolationFilter {
        BlackmanSincFilter() {
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double filter(double d) {
            return ResampleOp.blackman(d / support()) * ResampleOp.sinc(d);
        }

        @Override // com.twelvemonkeys.image.ResampleOp.InterpolationFilter
        public final double support() {
            return 4.0d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$Contributor.class */
    class Contributor {
        int pixel;
        double weight;

        Contributor() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/ResampleOp$ContributorList.class */
    class ContributorList {
        int n;
        Contributor[] p;

        ContributorList() {
        }
    }

    static int round(double d) {
        int i = (int) d;
        double d2 = d - ((double) i);
        if (d2 < 0.0d) {
            d2 = -d2;
        }
        if (d2 >= 0.5d) {
            if (d < 0.0d) {
                i--;
            } else {
                i++;
            }
        }
        return i;
    }

    private ContributorList calcXContrib(double d, double d2, int i, InterpolationFilter interpolationFilter, int i2) {
        int i3;
        int i4;
        ContributorList contributorList = new ContributorList();
        if (d < 1.0d) {
            double d3 = d2 / d;
            double d4 = 1.0d / d;
            if (d3 <= 0.5d) {
                d3 = 0.500001d;
                d4 = 1.0d;
            }
            contributorList.p = new Contributor[(int) ((d3 * Q2) + 1.0d + 0.5d)];
            double d5 = ((double) i2) / d;
            int iCeil = (int) Math.ceil(d5 - d3);
            int iFloor = (int) Math.floor(d5 + d3);
            double d6 = 0.0d;
            for (int i5 = iCeil; i5 <= iFloor; i5++) {
                double dFilter = interpolationFilter.filter((d5 - ((double) i5)) / d4) / d4;
                if (i5 < 0) {
                    i4 = -i5;
                } else if (i5 >= i) {
                    i4 = ((i - i5) + i) - 1;
                } else {
                    i4 = i5;
                }
                if (i4 >= i) {
                    i4 %= i;
                } else if (i4 < 0) {
                    i4 = i - 1;
                }
                int i6 = contributorList.n;
                contributorList.n = i6 + 1;
                contributorList.p[i6] = new Contributor();
                contributorList.p[i6].pixel = i4;
                contributorList.p[i6].weight = dFilter;
                d6 += dFilter;
            }
            if (d6 != 0.0d && d6 != 1.0d) {
                double d7 = 1.0d / d6;
                for (int i7 = 0; i7 < contributorList.n; i7++) {
                    contributorList.p[i7].weight *= d7;
                }
            }
        } else {
            contributorList.p = new Contributor[(int) ((d2 * Q2) + 1.0d + 0.5d)];
            double d8 = ((double) i2) / d;
            int iCeil2 = (int) Math.ceil(d8 - d2);
            int iFloor2 = (int) Math.floor(d8 + d2);
            for (int i8 = iCeil2; i8 <= iFloor2; i8++) {
                double dFilter2 = interpolationFilter.filter(d8 - ((double) i8));
                if (i8 < 0) {
                    i3 = -i8;
                } else if (i8 >= i) {
                    i3 = ((i - i8) + i) - 1;
                } else {
                    i3 = i8;
                }
                if (i3 >= i) {
                    i3 %= i;
                } else if (i3 < 0) {
                    i3 = i - 1;
                }
                int i9 = contributorList.n;
                contributorList.n = i9 + 1;
                contributorList.p[i9] = new Contributor();
                contributorList.p[i9].pixel = i3;
                contributorList.p[i9].weight = dFilter2;
            }
        }
        return contributorList;
    }

    private BufferedImage resample(BufferedImage bufferedImage, BufferedImage bufferedImage2, InterpolationFilter interpolationFilter) {
        int i;
        int i2;
        int width = bufferedImage2.getWidth();
        int height = bufferedImage2.getHeight();
        int width2 = bufferedImage.getWidth();
        int height2 = bufferedImage.getHeight();
        WritableRaster writableRasterCreateCompatibleWritableRaster = ImageUtil.createCompatibleWritableRaster(bufferedImage, bufferedImage.getColorModel(), 1, height2);
        double d = ((double) width) / ((double) width2);
        double d2 = ((double) height) / ((double) height2);
        ContributorList[] contributorListArr = new ContributorList[height];
        for (int i3 = 0; i3 < contributorListArr.length; i3++) {
            contributorListArr[i3] = new ContributorList();
        }
        double dSupport = interpolationFilter.support();
        if (d2 < 1.0d) {
            double d3 = dSupport / d2;
            double d4 = 1.0d / d2;
            if (d3 <= 0.5d) {
                d3 = 0.500001d;
                d4 = 1.0d;
            }
            for (int i4 = 0; i4 < height; i4++) {
                contributorListArr[i4].p = new Contributor[(int) ((d3 * Q2) + 1.0d + 0.5d)];
                double d5 = ((double) i4) / d2;
                int iCeil = (int) Math.ceil(d5 - d3);
                int iFloor = (int) Math.floor(d5 + d3);
                double d6 = 0.0d;
                for (int i5 = iCeil; i5 <= iFloor; i5++) {
                    double dFilter = interpolationFilter.filter((d5 - ((double) i5)) / d4) / d4;
                    if (i5 < 0) {
                        i2 = -i5;
                    } else if (i5 >= height2) {
                        i2 = ((height2 - i5) + height2) - 1;
                    } else {
                        i2 = i5;
                    }
                    if (i2 >= height2) {
                        i2 %= height2;
                    } else if (i2 < 0) {
                        i2 = height2 - 1;
                    }
                    ContributorList contributorList = contributorListArr[i4];
                    int i6 = contributorList.n;
                    contributorList.n = i6 + 1;
                    contributorListArr[i4].p[i6] = new Contributor();
                    contributorListArr[i4].p[i6].pixel = i2;
                    contributorListArr[i4].p[i6].weight = dFilter;
                    d6 += dFilter;
                }
                if (d6 != 0.0d && d6 != 1.0d) {
                    double d7 = 1.0d / d6;
                    for (int i7 = 0; i7 < contributorListArr[i4].n; i7++) {
                        contributorListArr[i4].p[i7].weight *= d7;
                    }
                }
            }
        } else {
            for (int i8 = 0; i8 < height; i8++) {
                contributorListArr[i8].p = new Contributor[(int) ((dSupport * Q2) + 1.0d + 0.5d)];
                double d8 = ((double) i8) / d2;
                double dCeil = Math.ceil(d8 - dSupport);
                double dFloor = Math.floor(d8 + dSupport);
                for (int i9 = (int) dCeil; i9 <= dFloor; i9++) {
                    double dFilter2 = interpolationFilter.filter(d8 - ((double) i9));
                    if (i9 < 0) {
                        i = -i9;
                    } else if (i9 >= height2) {
                        i = ((height2 - i9) + height2) - 1;
                    } else {
                        i = i9;
                    }
                    if (i >= height2) {
                        i %= height2;
                    } else if (i < 0) {
                        i = height2 - 1;
                    }
                    ContributorList contributorList2 = contributorListArr[i8];
                    int i10 = contributorList2.n;
                    contributorList2.n = i10 + 1;
                    contributorListArr[i8].p[i10] = new Contributor();
                    contributorListArr[i8].p[i10].pixel = i;
                    contributorListArr[i8].p[i10].weight = dFilter2;
                }
            }
        }
        WritableRaster raster = bufferedImage.getRaster();
        WritableRaster raster2 = bufferedImage2.getRaster();
        int numBands = raster.getNumBands();
        int[] iArr = new int[numBands];
        for (int i11 = 0; i11 < numBands; i11++) {
            iArr[i11] = (1 << bufferedImage.getColorModel().getComponentSize(i11)) - 1;
        }
        for (int i12 = 0; i12 < width; i12++) {
            ContributorList contributorListCalcXContrib = calcXContrib(d, dSupport, width2, interpolationFilter, i12);
            for (int i13 = 0; i13 < height2; i13++) {
                for (int i14 = 0; i14 < numBands; i14++) {
                    double d9 = 0.0d;
                    boolean z = false;
                    double sample = raster.getSample(contributorListCalcXContrib.p[0].pixel, i13, i14);
                    int i15 = 0;
                    while (i15 < contributorListCalcXContrib.n) {
                        double sample2 = i15 == 0 ? sample : raster.getSample(contributorListCalcXContrib.p[i15].pixel, i13, i14);
                        if (sample2 != sample) {
                            z = true;
                        }
                        d9 += sample2 * contributorListCalcXContrib.p[i15].weight;
                        i15++;
                    }
                    double dRound = z ? round(d9) : sample;
                    if (dRound < 0.0d) {
                        dRound = 0.0d;
                    } else if (dRound > iArr[i14]) {
                        dRound = iArr[i14];
                    }
                    writableRasterCreateCompatibleWritableRaster.setSample(0, i13, i14, dRound);
                }
            }
            for (int i16 = 0; i16 < height; i16++) {
                for (int i17 = 0; i17 < numBands; i17++) {
                    double d10 = 0.0d;
                    boolean z2 = false;
                    double sample3 = writableRasterCreateCompatibleWritableRaster.getSample(0, contributorListArr[i16].p[0].pixel, i17);
                    int i18 = 0;
                    while (i18 < contributorListArr[i16].n) {
                        double sample4 = i18 == 0 ? sample3 : writableRasterCreateCompatibleWritableRaster.getSample(0, contributorListArr[i16].p[i18].pixel, i17);
                        if (sample4 != sample3) {
                            z2 = true;
                        }
                        d10 += sample4 * contributorListArr[i16].p[i18].weight;
                        i18++;
                    }
                    double dRound2 = z2 ? round(d10) : sample3;
                    if (dRound2 < 0.0d) {
                        dRound2 = 0.0d;
                    } else if (dRound2 > iArr[i17]) {
                        dRound2 = iArr[i17];
                    }
                    raster2.setSample(i12, i16, i17, dRound2);
                }
            }
        }
        return bufferedImage2;
    }
}
