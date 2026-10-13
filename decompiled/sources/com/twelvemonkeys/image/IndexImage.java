package com.twelvemonkeys.image;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/IndexImage.class */
class IndexImage {
    protected static final int DITHER_MASK = 255;
    public static final int DITHER_DEFAULT = 0;
    public static final int DITHER_NONE = 1;
    public static final int DITHER_DIFFUSION = 2;
    public static final int DITHER_DIFFUSION_ALTSCANS = 3;
    protected static final int COLOR_SELECTION_MASK = 65280;
    public static final int COLOR_SELECTION_DEFAULT = 0;
    public static final int COLOR_SELECTION_FAST = 256;
    public static final int COLOR_SELECTION_QUALITY = 512;
    protected static final int TRANSPARENCY_MASK = 16711680;
    public static final int TRANSPARENCY_DEFAULT = 0;
    public static final int TRANSPARENCY_OPAQUE = 65536;
    public static final int TRANSPARENCY_BITMASK = 131072;
    protected static final int TRANSPARENCY_TRANSLUCENT = 196608;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/IndexImage$Counter.class */
    private static class Counter {
        public int val;
        public int count = 1;

        public Counter(int i) {
            this.val = i;
        }

        public boolean add(int i) {
            if (this.val != i) {
                return false;
            }
            this.count++;
            return true;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/IndexImage$Cube.class */
    private static class Cube {
        int[] min = {0, 0, 0};
        int[] max = {255, 255, 255};
        boolean done = false;
        List<Counter>[] colors;
        int count;
        static final int RED = 0;
        static final int GRN = 1;
        static final int BLU = 2;

        public Cube(List<Counter>[] listArr, int i) {
            this.colors = null;
            this.count = 0;
            this.colors = listArr;
            this.count = i;
        }

        public boolean isDone() {
            return this.done;
        }

        public Cube split() {
            int i;
            int i2;
            int i3;
            int i4 = (this.max[0] - this.min[0]) + 1;
            int i5 = (this.max[1] - this.min[1]) + 1;
            int i6 = (this.max[2] - this.min[2]) + 1;
            if (i4 >= i5) {
                i2 = 1;
                if (i4 >= i6) {
                    i = 0;
                    i3 = 2;
                } else {
                    i = 2;
                    i3 = 0;
                }
            } else if (i5 >= i6) {
                i = 1;
                i2 = 0;
                i3 = 2;
            } else {
                i = 2;
                i2 = 0;
                i3 = 1;
            }
            Cube cubeSplitChannel = splitChannel(i, i2, i3);
            if (cubeSplitChannel != null) {
                return cubeSplitChannel;
            }
            Cube cubeSplitChannel2 = splitChannel(i2, i, i3);
            if (cubeSplitChannel2 != null) {
                return cubeSplitChannel2;
            }
            Cube cubeSplitChannel3 = splitChannel(i3, i, i2);
            if (cubeSplitChannel3 != null) {
                return cubeSplitChannel3;
            }
            this.done = true;
            return null;
        }

        public Cube splitChannel(int i, int i2, int i3) {
            if (this.min[i] == this.max[i]) {
                return null;
            }
            int i4 = (2 - i) * 4;
            int i5 = (2 - i2) * 4;
            int i6 = (2 - i3) * 4;
            int i7 = this.count / 2;
            int[] iArr = new int[256];
            int i8 = 0;
            int[] iArr2 = {this.min[0] >> 4, this.min[1] >> 4, this.min[2] >> 4};
            int[] iArr3 = {this.max[0] >> 4, this.max[1] >> 4, this.max[2] >> 4};
            int i9 = this.min[0];
            int i10 = this.min[1];
            int i11 = this.min[2];
            int i12 = this.max[0];
            int i13 = this.max[1];
            int i14 = this.max[2];
            int[] iArr4 = {0, 0, 0};
            for (int i15 = iArr2[i]; i15 <= iArr3[i]; i15++) {
                int i16 = i15 << i4;
                for (int i17 = iArr2[i2]; i17 <= iArr3[i2]; i17++) {
                    int i18 = i16 | (i17 << i5);
                    for (int i19 = iArr2[i3]; i19 <= iArr3[i3]; i19++) {
                        List<Counter> list = this.colors[i18 | (i19 << i6)];
                        if (list != null) {
                            for (Counter counter : list) {
                                int i20 = counter.val;
                                iArr4[0] = (i20 & 16711680) >> 16;
                                iArr4[1] = (i20 & 65280) >> 8;
                                iArr4[2] = i20 & 255;
                                if (iArr4[0] >= i9 && iArr4[0] <= i12 && iArr4[1] >= i10 && iArr4[1] <= i13 && iArr4[2] >= i11 && iArr4[2] <= i14) {
                                    int i21 = iArr4[i];
                                    iArr[i21] = iArr[i21] + counter.count;
                                    i8 += counter.count;
                                }
                            }
                        }
                    }
                }
                if (i8 >= i7) {
                    break;
                }
            }
            int i22 = 0;
            int i23 = -1;
            int i24 = this.min[i];
            int i25 = this.max[i];
            for (int i26 = this.min[i]; i26 <= this.max[i]; i26++) {
                int i27 = iArr[i26];
                if (i27 == 0) {
                    if (i22 == 0 && i26 < this.max[i]) {
                        this.min[i] = i26 + 1;
                    }
                } else if (i22 + i27 < i7) {
                    i23 = i26;
                    i22 += i27;
                } else {
                    if (i7 - i22 <= (i22 + i27) - i7) {
                        if (i23 == -1) {
                            if (i27 == this.count) {
                                this.max[i] = i26;
                                return null;
                            }
                            i24 = i26;
                            i25 = i26 + 1;
                            break;
                        }
                        i24 = i23;
                        i25 = i26;
                        break;
                    }
                    if (i26 == this.max[i]) {
                        if (i27 == this.count) {
                            return null;
                        }
                        i24 = i23;
                        i25 = i26;
                        break;
                    }
                    i22 += i27;
                    i24 = i26;
                    i25 = i26 + 1;
                    break;
                }
            }
            Cube cube = new Cube(this.colors, i22);
            this.count -= i22;
            cube.min[i] = this.min[i];
            cube.max[i] = i24;
            this.min[i] = i25;
            cube.min[i2] = this.min[i2];
            cube.max[i2] = this.max[i2];
            cube.min[i3] = this.min[i3];
            cube.max[i3] = this.max[i3];
            return cube;
        }

        public int averageColor() {
            if (this.count == 0) {
                return 0;
            }
            float f = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            int i = this.min[0];
            int i2 = this.min[1];
            int i3 = this.min[2];
            int i4 = this.max[0];
            int i5 = this.max[1];
            int i6 = this.max[2];
            int[] iArr = {i >> 4, i2 >> 4, i3 >> 4};
            int[] iArr2 = {i4 >> 4, i5 >> 4, i6 >> 4};
            for (int i7 = iArr[0]; i7 <= iArr2[0]; i7++) {
                int i8 = i7 << 8;
                for (int i9 = iArr[1]; i9 <= iArr2[1]; i9++) {
                    int i10 = i8 | (i9 << 4);
                    for (int i11 = iArr[2]; i11 <= iArr2[2]; i11++) {
                        List<Counter> list = this.colors[i10 | i11];
                        if (list != null) {
                            for (Counter counter : list) {
                                int i12 = counter.val;
                                int i13 = (i12 & 16711680) >> 16;
                                int i14 = (i12 & 65280) >> 8;
                                int i15 = i12 & 255;
                                if (i13 >= i && i13 <= i4 && i14 >= i2 && i14 <= i5 && i15 >= i3 && i15 <= i6) {
                                    float f4 = counter.count / this.count;
                                    f += i13 * f4;
                                    f2 += i14 * f4;
                                    f3 += i15 * f4;
                                }
                            }
                        }
                    }
                }
            }
            return (((int) (f + 0.5f)) << 16) | (((int) (f2 + 0.5f)) << 8) | ((int) (f3 + 0.5f));
        }
    }

    private IndexImage() {
    }

    @Deprecated
    public static IndexColorModel getIndexColorModel(Image image, int i, boolean z) {
        return getIndexColorModel(image, i, z ? 256 : 512);
    }

    public static IndexColorModel getIndexColorModel(Image image, int i, int i2) throws ImageConversionException {
        IndexColorModel inverseColorMapIndexColorModel = null;
        RenderedImage bufferedImage = null;
        if (image instanceof RenderedImage) {
            bufferedImage = (RenderedImage) image;
            IndexColorModel colorModel = bufferedImage.getColorModel();
            if ((colorModel instanceof IndexColorModel) && colorModel.getMapSize() <= i) {
                inverseColorMapIndexColorModel = colorModel;
            }
        } else {
            BufferedImageFactory bufferedImageFactory = new BufferedImageFactory(image);
            IndexColorModel colorModel2 = bufferedImageFactory.getColorModel();
            if ((colorModel2 instanceof IndexColorModel) && colorModel2.getMapSize() <= i) {
                inverseColorMapIndexColorModel = colorModel2;
            } else {
                bufferedImage = bufferedImageFactory.getBufferedImage();
            }
        }
        if (inverseColorMapIndexColorModel == null) {
            inverseColorMapIndexColorModel = createIndexColorModel(ImageUtil.toBuffered(bufferedImage), i, i2);
        } else if (!(inverseColorMapIndexColorModel instanceof InverseColorMapIndexColorModel)) {
            inverseColorMapIndexColorModel = new InverseColorMapIndexColorModel(inverseColorMapIndexColorModel);
        }
        return inverseColorMapIndexColorModel;
    }

    private static IndexColorModel createIndexColorModel(BufferedImage bufferedImage, int i, int i2) {
        InverseColorMapIndexColorModel inverseColorMapIndexColorModel;
        boolean zIsTransparent = isTransparent(i2);
        if (zIsTransparent) {
            i--;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        List[] listArr = new List[4096];
        int i3 = 1;
        if (isFast(i2)) {
            i3 = 1 + ((width * height) / 16384);
        }
        int i4 = 0;
        for (int i5 = 0; i5 < width; i5++) {
            int i6 = i5 % i3;
            while (true) {
                int i7 = i6;
                if (i7 < height) {
                    i4++;
                    int rgb = bufferedImage.getRGB(i5, i7) & 16777215;
                    int i8 = ((rgb & 15728640) >>> 12) | ((rgb & 61440) >>> 8) | ((rgb & 240) >>> 4);
                    List list = listArr[i8];
                    if (list == null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(new Counter(rgb));
                        listArr[i8] = arrayList;
                    } else {
                        Iterator it = list.iterator();
                        do {
                            if (!it.hasNext()) {
                                list.add(new Counter(rgb));
                                break;
                            }
                        } while (!((Counter) it.next()).add(rgb));
                    }
                    i6 = i7 + i3;
                }
            }
        }
        int i9 = 1;
        int i10 = 0;
        Cube[] cubeArr = new Cube[i];
        cubeArr[0] = new Cube(listArr, i4);
        while (i9 < i) {
            while (cubeArr[i10].isDone()) {
                i10++;
                if (i10 == i9) {
                    break;
                }
            }
            if (i10 == i9) {
                break;
            }
            Cube cube = cubeArr[i10];
            Cube cubeSplit = cube.split();
            if (cubeSplit != null) {
                if (cubeSplit.count > cube.count) {
                    cube = cubeSplit;
                    cubeSplit = cube;
                }
                int i11 = i10;
                int i12 = cube.count;
                for (int i13 = i10 + 1; i13 < i9 && cubeArr[i13].count >= i12; i13++) {
                    int i14 = i11;
                    i11++;
                    cubeArr[i14] = cubeArr[i13];
                }
                int i15 = i11;
                int i16 = i11 + 1;
                cubeArr[i15] = cube;
                int i17 = cubeSplit.count;
                while (i16 < i9 && cubeArr[i16].count >= i17) {
                    i16++;
                }
                System.arraycopy(cubeArr, i16, cubeArr, i16 + 1, i9 - i16);
                cubeArr[i16] = cubeSplit;
                i9++;
            }
        }
        byte[] bArr = new byte[zIsTransparent ? i9 + 1 : i9];
        byte[] bArr2 = new byte[zIsTransparent ? i9 + 1 : i9];
        byte[] bArr3 = new byte[zIsTransparent ? i9 + 1 : i9];
        for (int i18 = 0; i18 < i9; i18++) {
            int iAverageColor = cubeArr[i18].averageColor();
            bArr[i18] = (byte) ((iAverageColor >> 16) & 255);
            bArr2[i18] = (byte) ((iAverageColor >> 8) & 255);
            bArr3[i18] = (byte) (iAverageColor & 255);
        }
        if (zIsTransparent) {
            inverseColorMapIndexColorModel = new InverseColorMapIndexColorModel(8, bArr.length, bArr, bArr2, bArr3, bArr.length - 1);
        } else {
            inverseColorMapIndexColorModel = new InverseColorMapIndexColorModel(8, bArr.length, bArr, bArr2, bArr3);
        }
        return inverseColorMapIndexColorModel;
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage) {
        return getIndexedImage(bufferedImage, 256, 0);
    }

    private static boolean isFast(int i) {
        return (i & 65280) != 512;
    }

    static boolean isTransparent(int i) {
        return ((i & 131072) == 0 && (i & 196608) == 0) ? false : true;
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, Image image, Color color, int i) throws ImageConversionException {
        return getIndexedImage(bufferedImage, getIndexColorModel(image, 256, i), color, i);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, int i, Color color, int i2) {
        IndexColorModel indexColorModel;
        if (color != null) {
            indexColorModel = getIndexColorModel((Image) createSolid(bufferedImage, color), i, i2);
        } else {
            indexColorModel = getIndexColorModel((Image) bufferedImage, i, i2);
        }
        if ((i2 & 255) != 1 && indexColorModel.getMapSize() < i) {
            i2 = (i2 & (-256)) | 1;
        }
        return getIndexedImage(bufferedImage, indexColorModel, color, i2);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, IndexColorModel indexColorModel, Color color, int i) {
        BufferedImage bufferedImage2;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        boolean z = (!isTransparent(i) || bufferedImage.getColorModel().getTransparency() == 1 || indexColorModel.getTransparency() == 1) ? false : true;
        BufferedImage bufferedImageCreateSolid = bufferedImage;
        if (color != null) {
            bufferedImageCreateSolid = createSolid(bufferedImage, color);
        }
        if (indexColorModel.getMapSize() > 2) {
            bufferedImage2 = new BufferedImage(width, height, 13, indexColorModel);
        } else {
            bufferedImage2 = new BufferedImage(width, height, 12, indexColorModel);
        }
        switch (i & 255) {
            case 0:
            default:
                Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
                try {
                    graphics2DCreateGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE));
                    graphics2DCreateGraphics.drawImage(bufferedImageCreateSolid, 0, 0, (ImageObserver) null);
                    graphics2DCreateGraphics.dispose();
                } catch (Throwable th) {
                    graphics2DCreateGraphics.dispose();
                    throw th;
                }
                break;
            case 1:
                new CopyDither(indexColorModel).filter(bufferedImageCreateSolid, bufferedImage2);
                break;
            case 2:
            case 3:
                DiffusionDither diffusionDither = new DiffusionDither(indexColorModel);
                if ((i & 255) == 3) {
                    diffusionDither.setAlternateScans(true);
                }
                diffusionDither.filter(bufferedImageCreateSolid, bufferedImage2);
                break;
        }
        if (z) {
            applyAlpha(bufferedImage2, bufferedImage);
        }
        return bufferedImage2;
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, int i, int i2) {
        return getIndexedImage(bufferedImage, i, (Color) null, i2);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, IndexColorModel indexColorModel, int i) {
        return getIndexedImage(bufferedImage, indexColorModel, (Color) null, i);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, Image image, int i) {
        return getIndexedImage(bufferedImage, image, (Color) null, i);
    }

    private static BufferedImage createSolid(BufferedImage bufferedImage, Color color) {
        BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getColorModel(), bufferedImage.copyData((WritableRaster) null), bufferedImage.isAlphaPremultiplied(), (Hashtable) null);
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        try {
            graphics2DCreateGraphics.setColor(color);
            graphics2DCreateGraphics.setComposite(AlphaComposite.DstOver);
            graphics2DCreateGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            return bufferedImage2;
        } finally {
            graphics2DCreateGraphics.dispose();
        }
    }

    private static void applyAlpha(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        for (int i = 0; i < bufferedImage2.getHeight(); i++) {
            for (int i2 = 0; i2 < bufferedImage2.getWidth(); i2++) {
                if (((bufferedImage2.getRGB(i2, i) >> 24) & 255) < 64) {
                    bufferedImage.setRGB(i2, i, 16777215);
                }
            }
        }
    }

    public static void main(String[] strArr) {
        File file;
        IndexColorModel indexColorModel;
        Image indexedImage;
        int i = 0;
        int i2 = -1;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        int i3 = 256;
        String str = null;
        String str2 = null;
        String extension = null;
        Color color = null;
        boolean z4 = false;
        String str3 = null;
        boolean z5 = false;
        while (i < strArr.length && strArr[i].charAt(0) == '-' && strArr[i].length() >= 2) {
            if (strArr[i].charAt(1) == 's' || strArr[i].equals("--speedtest")) {
                i++;
                if (strArr.length > i && strArr[i].charAt(0) != '-') {
                    try {
                        i++;
                        i2 = Integer.parseInt(strArr[i]);
                    } catch (NumberFormatException e) {
                        z5 = true;
                    }
                } else {
                    i2 = 10;
                }
            } else if (strArr[i].charAt(1) == 'w' || strArr[i].equals("--overwrite")) {
                z = true;
                i++;
            } else if (strArr[i].charAt(1) == 'c' || strArr[i].equals("--colors")) {
                i++;
                try {
                    i++;
                    i3 = Integer.parseInt(strArr[i]);
                } catch (NumberFormatException e2) {
                    z5 = true;
                }
            } else if (strArr[i].charAt(1) == 'g' || strArr[i].equals("--grayscale")) {
                i++;
                z3 = true;
            } else if (strArr[i].charAt(1) == 'm' || strArr[i].equals("--monochrome")) {
                i++;
                i3 = 2;
                z2 = true;
            } else if (strArr[i].charAt(1) == 'd' || strArr[i].equals("--dither")) {
                int i4 = i + 1;
                i = i4 + 1;
                str = strArr[i4];
            } else if (strArr[i].charAt(1) == 'p' || strArr[i].equals("--palette")) {
                int i5 = i + 1;
                i = i5 + 1;
                str3 = strArr[i5];
            } else if (strArr[i].charAt(1) == 'q' || strArr[i].equals("--quality")) {
                int i6 = i + 1;
                i = i6 + 1;
                str2 = strArr[i6];
            } else if (strArr[i].charAt(1) == 'b' || strArr[i].equals("--bgcolor")) {
                i++;
                try {
                    i++;
                    color = StringUtil.toColor(strArr[i]);
                } catch (Exception e3) {
                    z5 = true;
                }
            } else if (strArr[i].charAt(1) == 't' || strArr[i].equals("--transparency")) {
                i++;
                z4 = true;
            } else if (strArr[i].charAt(1) == 'f' || strArr[i].equals("--outputformat")) {
                int i7 = i + 1;
                i = i7 + 1;
                extension = StringUtil.toLowerCase(strArr[i7]);
            } else if (strArr[i].charAt(1) == 'h' || strArr[i].equals("--help")) {
                i++;
                z5 = true;
            } else {
                int i8 = i;
                i++;
                System.err.println("Unknown option \"" + strArr[i8] + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
        }
        if (z5 || strArr.length < i + 1) {
            System.err.println("Usage: IndexImage [--help|-h] [--speedtest|-s <integer>] [--bgcolor|-b <color>] [--colors|-c <integer> | --grayscale|g | --monochrome|-m | --palette|-p <file>] [--dither|-d (default|diffusion|none)] [--quality|-q (default|high|low)] [--transparency|-t] [--outputformat|-f (gif|jpeg|png|wbmp|...)] [--overwrite|-w] <input> [<output>]");
            System.err.print("Input format names: ");
            String[] readerFormatNames = ImageIO.getReaderFormatNames();
            for (int i9 = 0; i9 < readerFormatNames.length; i9++) {
                System.err.print(readerFormatNames[i9] + (i9 + 1 < readerFormatNames.length ? ", " : "\n"));
            }
            System.err.print("Output format names: ");
            String[] writerFormatNames = ImageIO.getWriterFormatNames();
            for (int i10 = 0; i10 < writerFormatNames.length; i10++) {
                System.err.print(writerFormatNames[i10] + (i10 + 1 < writerFormatNames.length ? ", " : "\n"));
            }
            System.exit(5);
        }
        int i11 = i;
        int i12 = i + 1;
        File file2 = new File(strArr[i11]);
        if (!file2.exists()) {
            System.err.println("File \"" + file2.getAbsolutePath() + "\" does not exist!");
            System.exit(5);
        }
        File file3 = null;
        if (str3 != null) {
            file3 = new File(str3);
            if (!file3.exists()) {
                System.err.println("File \"" + file2.getAbsolutePath() + "\" does not exist!");
                System.exit(5);
            }
        }
        if (i12 < strArr.length) {
            file = new File(strArr[i12]);
            if (extension == null) {
                extension = FileUtil.getExtension(file);
            }
        } else {
            String basename = FileUtil.getBasename(file2);
            if (extension == null) {
                extension = "png";
            }
            file = new File(basename + '.' + extension);
        }
        if (!z && file.exists()) {
            System.err.println("The file \"" + file.getAbsolutePath() + "\" allready exists!");
            System.exit(5);
        }
        BufferedImage buffered = null;
        BufferedImage bufferedImageCreateSolid = null;
        try {
            buffered = ImageIO.read(file2);
            if (buffered == null) {
                System.err.println("No reader for image: \"" + file2.getAbsolutePath() + "\"!");
                System.exit(5);
            }
            if (file3 != null) {
                bufferedImageCreateSolid = ImageIO.read(file3);
                if (bufferedImageCreateSolid == null) {
                    System.err.println("No reader for image: \"" + file3.getAbsolutePath() + "\"!");
                    System.exit(5);
                }
            }
        } catch (IOException e4) {
            e4.printStackTrace(System.err);
            System.exit(5);
        }
        int i13 = 0;
        if ("DIFFUSION".equalsIgnoreCase(str)) {
            i13 = 0 | 2;
        } else if ("DIFFUSION_ALTSCANS".equalsIgnoreCase(str)) {
            i13 = 0 | 3;
        } else if (AOSignConstants.SIGN_FORMAT_PKCS1.equalsIgnoreCase(str)) {
            i13 = 0 | 1;
        }
        if ("HIGH".equalsIgnoreCase(str2)) {
            i13 |= 512;
        } else if ("LOW".equalsIgnoreCase(str2)) {
            i13 |= 256;
        }
        if (z4) {
            i13 |= 131072;
        }
        if (color != null && bufferedImageCreateSolid == null) {
            bufferedImageCreateSolid = createSolid(buffered, color);
        }
        long jCurrentTimeMillis = 0;
        if (i2 > 0) {
            System.out.println("Measuring speed!");
            jCurrentTimeMillis = System.currentTimeMillis();
        }
        if (z2) {
            indexedImage = getIndexedImage(buffered, MonochromeColorModel.getInstance(), color, i13);
            indexColorModel = MonochromeColorModel.getInstance();
        } else if (z3) {
            buffered = ImageUtil.toBuffered(ImageUtil.grayscale(buffered));
            IndexColorModel indexColorModel2 = getIndexColorModel((Image) buffered, i3, i13);
            indexColorModel = indexColorModel2;
            indexedImage = getIndexedImage(buffered, indexColorModel2, color, i13);
            if (i2 > 0) {
                indexColorModel = getIndexColorModel(indexedImage, i3, i13);
            }
        } else if (bufferedImageCreateSolid != null) {
            BufferedImage buffered2 = ImageUtil.toBuffered(buffered, 2);
            IndexColorModel indexColorModel3 = getIndexColorModel((Image) bufferedImageCreateSolid, i3, i13);
            indexColorModel = indexColorModel3;
            indexedImage = getIndexedImage(buffered2, indexColorModel3, color, i13);
        } else {
            buffered = ImageUtil.toBuffered(buffered, 2);
            IndexColorModel indexColorModel4 = getIndexColorModel((Image) buffered, i3, i13);
            indexColorModel = indexColorModel4;
            indexedImage = getIndexedImage(buffered, indexColorModel4, color, i13);
        }
        if (i2 > 0) {
            System.out.println("Color selection + dither: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
        }
        try {
            if (!ImageIO.write(indexedImage, extension, file)) {
                System.err.println("No writer for format: \"" + extension + "\"!");
            }
        } catch (IOException e5) {
            e5.printStackTrace(System.err);
        }
        if (i2 > 0) {
            System.out.println("Measuring speed!");
            for (int i14 = 0; i14 < 10; i14++) {
                getIndexedImage(buffered, indexColorModel, color, i13);
            }
            long jCurrentTimeMillis2 = 0;
            for (int i15 = 0; i15 < i2; i15++) {
                long jCurrentTimeMillis3 = System.currentTimeMillis();
                getIndexedImage(buffered, indexColorModel, color, i13);
                jCurrentTimeMillis2 += System.currentTimeMillis() - jCurrentTimeMillis3;
                System.out.print('.');
                if ((i15 + 1) % 10 == 0) {
                    System.out.println("\nAverage (after " + (i15 + 1) + " iterations): " + (jCurrentTimeMillis2 / ((long) (i15 + 1))) + "ms");
                }
            }
            System.out.println("\nDither only:");
            System.out.println("Total time (" + i2 + " invocations): " + jCurrentTimeMillis2 + "ms");
            System.out.println("Average: " + (jCurrentTimeMillis2 / ((long) i2)) + "ms");
        }
    }
}
